/**
 * -----------------------------------------------------------------------------
 * Programmed by: Dalibor Aksic
 * Company: Medelexis AG
 * Industry: Software Company
 * Address: Täfernstrasse 16, 5405 Dättwil, Baden, Switzerland
 * 
 * Description:
 * This file is part of the Medelexis MedNet Web API project. It serves as an 
 * API integration with MedNet to manage patient data exchange in FHIR (Fast 
 * Healthcare Interoperability Resources) format. The exchanged data ensures 
 * that forms are created correctly and meet healthcare standards.
 * 
 * The Activator class manages the plugin lifecycle and schedules periodic tasks 
 * to automatically download files from MedNet and import them into Omnivore.
 * 
 * Date: 04.12.2024
 * 
 * NOTE: This code is proprietary and confidential. Unauthorized copying or 
 * distribution of this file, via any medium, is strictly prohibited.
 * -----------------------------------------------------------------------------
 */
package ch.elexis.mednet.webapi.ui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.mednet.webapi.core.IMednetAuthService;
import ch.elexis.mednet.webapi.core.fhir.resources.util.FileDownloader;
import ch.elexis.mednet.webapi.ui.handler.ImportOmnivore;

public class Activator extends AbstractUIPlugin {

	private static final Logger logger = LoggerFactory.getLogger(Activator.class);
	private static Activator instance;
	private ScheduledExecutorService scheduler;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		instance = this;
		logger.info("Activator initialized, scheduler is not started yet."); //$NON-NLS-1$
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		instance = null;
		stopScheduler();
		super.stop(context);
		logger.info("Activator stopped."); //$NON-NLS-1$
	}

	public static Activator getInstance() {
		return instance;
	}

	public void startScheduler() {
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					try {
						BundleContext context = getBundle().getBundleContext();
						ServiceReference<IMednetAuthService> serviceReference = context
								.getServiceReference(IMednetAuthService.class);
						IMednetAuthService authService = context.getService(serviceReference);

						boolean downloadSuccess = false;

						if (authService != null) {
							FileDownloader downloader = new FileDownloader(authService);
							downloadSuccess = downloader.downloadForms();
							if (downloadSuccess) {
								logger.info("File download initiated successfully."); //$NON-NLS-1$
							} else {
								logger.error("File download failed."); //$NON-NLS-1$
							}
						} else {
							logger.error("IMednetAuthService not available."); //$NON-NLS-1$
						}

						if (downloadSuccess) {
							IStatus status = new ImportOmnivore().run(); // $NON-NLS-1$
							if (!status.isOK()) {
								throw new TaskException(TaskException.EXECUTION_ERROR,
										"Import failed with status: " + status.getMessage()); //$NON-NLS-1$
							} else {
								logger.info("Import completed successfully."); //$NON-NLS-1$
							}
						} else {
							notifySchedulerError();
						}
					} catch (Exception ex) {
						logger.error("Error during task execution: {}", ex.getMessage(), ex); //$NON-NLS-1$
						notifySchedulerError();
					}
				}
			}, 0, 5, TimeUnit.MINUTES);
			logger.info("Scheduler started with a 5-minute interval."); //$NON-NLS-1$
		} else {
			logger.info("Scheduler is already running."); //$NON-NLS-1$
		}
	}


	public void stopScheduler() {
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdownNow();
			logger.info("Scheduler shut down."); //$NON-NLS-1$
		}
	}

	private Runnable onSchedulerError;

	public void setOnSchedulerError(Runnable onSchedulerError) {
		this.onSchedulerError = onSchedulerError;
	}

	public void notifySchedulerError() {
		if (onSchedulerError != null) {
			onSchedulerError.run();
		}
	}

}
