/*******************************************************************************
 * Copyright (c) 2009, SGAM Informatics and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.icpc.fire.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.Hub;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Sticker;
import ch.elexis.icpc.fire.model.ConsultationBuilder;
import ch.elexis.icpc.fire.model.Report;
import ch.elexis.icpc.fire.model.ReportBuilder;
import ch.elexis.icpc.fire.model.TMedi;
import ch.elexis.icpc.fire.model.XmlUtil;
import ch.elexis.icpc.fire.ui.Preferences;
import ch.rgw.tools.TimeTool;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 *
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ExportFireHandler extends AbstractHandler {

	private static Logger logger = LoggerFactory.getLogger(ExportFireHandler.class);

	public static final String FIRESTICKERNAME = "Fire (ICPC)"; //$NON-NLS-1$

	private Sticker fireSticker;

	public ExportFireHandler() {
		String id = new Query<Sticker>(Sticker.class).findSingle(Sticker.FLD_NAME, Query.EQUALS, FIRESTICKERNAME);
		if (id == null) {
			fireSticker = new Sticker(FIRESTICKERNAME, "0066CC", "C0C0C0"); //$NON-NLS-1$ //$NON-NLS-2$
			fireSticker.setClassForSticker(Konsultation.class);
		} else {
			fireSticker = Sticker.load(id);
		}
	}

	/**
	 * The time this export slot run starts from
	 *
	 * @return
	 */
	public static TimeTool getTtFrom() {
		TimeTool ttFrom = new TimeTool("20180101"); //$NON-NLS-1$
		String lastupdate = ConfigServiceHolder.getGlobal(Preferences.CFGPARAM, null);
		if (lastupdate != null) {
			ttFrom = new TimeTool(lastupdate);
		}
		ttFrom.addHours(Report.EXPORT_DELAY * -1);
		return ttFrom;
	}

	/**
	 * The time this export slot ends with
	 *
	 * @return
	 */
	public static TimeTool getTtTo() {
		TimeTool ttTo = new TimeTool();
		ttTo.addHours(Report.EXPORT_DELAY * -1);
		return ttTo;
	}

	/**
	 * the command has been executed, so extract extract the needed information from
	 * the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Query<Konsultation> qbe = new Query<Konsultation>(Konsultation.class);
		qbe.add(Konsultation.DATE, Query.GREATER, getTtFrom().toString(TimeTool.DATE_COMPACT));
		qbe.add(Konsultation.DATE, Query.LESS_OR_EQUAL, getTtTo().toString(TimeTool.DATE_COMPACT));
		qbe.orderBy(false, Konsultation.DATE);
		List<Konsultation> konsen = qbe.execute();
		if (konsen.size() > 0) {
			FileDialog fd = new FileDialog(Hub.getActiveShell(), SWT.SAVE);
			fd.setFileName("elexis-fire" + new TimeTool().toString(TimeTool.DATE_COMPACT) + ".xml"); //$NON-NLS-1$ //$NON-NLS-2$
			fd.setFilterExtensions(new String[] { "xml" }); //$NON-NLS-1$
			fd.setFilterNames(new String[] { "XML-Dateien" });
			String expath = fd.open();
			if (expath != null) {
				ProgressMonitorDialog progress = new ProgressMonitorDialog(HandlerUtil.getActiveShell(event));
				try {
					progress.run(true, true, createReportExportRunnable(konsen, expath));
				} catch (InvocationTargetException | InterruptedException e) {
					logger.warn("Exception during FIRE export", e); //$NON-NLS-1$
				}
			}
		}
		return null;
	}

	public IRunnableWithProgress createReportExportRunnable(List<Konsultation> konsen, String expath) {
		return new ReportExportRunnable(konsen, expath);
	}

	private class ReportExportRunnable implements IRunnableWithProgress {

		private List<Konsultation> consultations;
		private String exportPath;

		public ReportExportRunnable(List<Konsultation> konsen, String expath) {
			this.consultations = konsen;
			this.exportPath = expath;
		}

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			try {
				monitor.beginTask("FIRE Export ", consultations.size() + 1);
				int counter = 0;
				ReportBuilder reportBuilder = new ReportBuilder();
				if (reportBuilder.isValidConfig()) {
					Map<String, Set<TMedi>> unreferencedStopMedisPerPatient = ConsultationBuilder
							.initializeUnreferencedStopMedisPerPatient(reportBuilder.getFireConfig());

					for (Konsultation konsultation : consultations) {
						if (monitor.isCanceled()) {
							return;
						}
						monitor.setTaskName("FIRE exporting (" + ++counter + "/" + consultations.size() + ")"); //$NON-NLS-2$ //$NON-NLS-3$
						// validate
						Fall fall = konsultation.getFall();
						if (fall == null) {
							continue;
						}
						Patient pat = fall.getPatient();
						if (pat == null) {
							continue;
						}
						Mandant mandant = konsultation.getMandant();
						if (mandant == null) {
							continue;
						}

						// konsultation.removeSticker(fireSticker);
						if (!konsultation.getStickers().contains(fireSticker)) {
							try {
								// add to report
								BigInteger patId = reportBuilder.addPatient(pat);
								BigInteger docId = reportBuilder.addMandant(mandant);
								reportBuilder.addKonsultation(patId, docId, konsultation,
										unreferencedStopMedisPerPatient);

								konsultation.addSticker(fireSticker);
							} catch (IllegalStateException e) {
								logger.warn("Could not add consultation.", e); //$NON-NLS-1$
							}
						}
						monitor.worked(1);
					}

					reportBuilder.finish(unreferencedStopMedisPerPatient);
					monitor.worked(1);

					Optional<Report> report = reportBuilder.build();
					if (report.isPresent()) {
						try (FileOutputStream fout = new FileOutputStream(new File(exportPath))) {
							XmlUtil.marshallFireReport(report.get(), fout);
							// update last export date
							ConfigServiceHolder.setGlobal(Preferences.CFGPARAM,
									new TimeTool().toString(TimeTool.DATE_COMPACT));
						} catch (IOException e) {
							openError("Error", "Error writing report, see logs for details.");
							logger.error("Error writing report.", e); //$NON-NLS-1$
						}
					}
				} else {
					openError("ICPC/Fire", "Bitte konfigurieren Sie das Fire Plugin (Datei-Einstellungen)");
				}
			} catch (DatatypeConfigurationException | NumberFormatException e) {
				openError("Error", "Error creating report, see logs for details.");
				logger.error("Could not create XML output", e); //$NON-NLS-1$
			}
		}

		private void openError(String title, String description) {
			Display display = Display.getDefault();
			if (display != null) {
				display.syncExec(new Runnable() {
					@Override
					public void run() {
						MessageDialog.openError(display.getActiveShell(), title, description);
					}
				});
			}
		}
	}
}
