package ch.elexis.fire.ui.export.internal;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.TimerTask;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.tasks.model.ITask;
import ch.elexis.core.tasks.model.ITaskDescriptor;
import ch.elexis.core.tasks.model.ITaskService;
import ch.elexis.core.tasks.model.TaskState;
import ch.elexis.core.tasks.model.TaskTriggerType;
import ch.elexis.fire.core.IFIREService;
import ch.elexis.fire.core.task.FIREExportTaskDescriptor;

public class AutomaticExportTask extends TimerTask {

	public static final String SCHEDULED_STATION = "ch.elexis.fire/automaticexport/scheduledstation";

	public static final String SCHEDULESHOUR_CONFIG = "ch.elexis.fire/automaticexport/scheduledhour";

	private boolean shutdownRegistered;

	private boolean running;

	private IFIREService fireService;

	private IConfigService configService;

	private IContextService contextService;

	private ITaskService taskService;

	private ITaskDescriptor taskDescriptor;

	public AutomaticExportTask(IFIREService fireService, ITaskService taskService, IConfigService configService,
			IContextService contextService) {
		this.fireService = fireService;
		this.configService = configService;
		this.contextService = contextService;
		this.taskService = taskService;

		try {
			taskDescriptor = FIREExportTaskDescriptor.getOrCreate(taskService);
		} catch (TaskException e) {
			LoggerFactory.getLogger(getClass()).error("Could not init taskDescriptor", e);
		}
	}

	@Override
	public void run() {
		if (!shutdownRegistered) {
			shutdownRegistered = true;
			PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener() {
				@Override
				public boolean preShutdown(IWorkbench workbench, boolean forced) {
					if (contextService.getStationIdentifier().equals(configService.get(SCHEDULED_STATION, null))) {
						return MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Warnung",
								"Diese Elexis Instanz soll automatisch an FIRE übermitteln. Wollen Sie trotzdem beenden?");
					}
					return true;
				}

				@Override
				public void postShutdown(IWorkbench workbench) {
					// nothing to do here
				}
			});
		}

		if (!running && contextService.getStationIdentifier().equals(configService.get(SCHEDULED_STATION, null))) {
			// run daily after the configured scheduled hour (default 01:00)
			if (!wasScheduledToday() && isAfterScheduledHour()) {
				Job job = new Job(
						initialExportDone(fireService) ? "FIRE inkrementeller Export" : "FIRE initialer Export") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						monitor.beginTask("FIRE Export", IProgressMonitor.UNKNOWN);
						try {
							running = true;
							if (taskDescriptor.getOwner() == null) {
								taskDescriptor.setOwner(ContextServiceHolder.get().getActiveUser().get());
								taskService.saveTaskDescriptor(taskDescriptor);
							}
							ITask task = taskService.triggerSync(taskDescriptor, monitor, TaskTriggerType.MANUAL,
									Collections.emptyMap());
							if (task.getState() == TaskState.COMPLETED || task.getState() == TaskState.COMPLETED_MANUAL
									|| task.getState() == TaskState.COMPLETED_WARN) {

								return Status.OK_STATUS;
							}
						} catch (TaskException e) {
							LoggerFactory.getLogger(getClass()).error("Error performing FIRE export", e);
							Display.getDefault().syncExec(() -> {
								MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
										"Der Export ist fehlgeschlagen.");
							});
						} finally {
							running = false;
						}
						return Status.CANCEL_STATUS;
					}
				};
				job.schedule();
			}
		}
	}

	private boolean initialExportDone(IFIREService fireService) {
		return fireService.getInitialTimestamp() != -1;
	}

	private boolean wasScheduledToday() {
		if (getTimestampOfLastExport(fireService).toLocalDate().equals(LocalDate.now())) {
			return true;
		}
		return false;
	}

	private LocalDateTime getTimestampOfLastExport(IFIREService fireService) {
		if (fireService.getIncrementalTimestamp() != -1) {
			Instant instant = Instant.ofEpochMilli(fireService.getIncrementalTimestamp());
			return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		}
		if (fireService.getInitialTimestamp() != -1) {
			Instant instant = Instant.ofEpochMilli(fireService.getInitialTimestamp());
			return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		}
		return LocalDateTime.of(1970, 1, 1, 0, 0);
	}

	private boolean isAfterScheduledHour() {
		return getScheduledHour() <= LocalDateTime.now().getHour();
	}

	private Integer getScheduledHour() {
		int configuredValue = ConfigServiceHolder.get().get(SCHEDULESHOUR_CONFIG, 1);
		return configuredValue;
	}
}
