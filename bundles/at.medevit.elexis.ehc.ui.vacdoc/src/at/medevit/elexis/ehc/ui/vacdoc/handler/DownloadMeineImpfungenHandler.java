package at.medevit.elexis.ehc.ui.vacdoc.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.ehealth_connector.cda.ch.vacd.CdaChVacd;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.ui.vacdoc.service.MeineImpfungenServiceHolder;
import at.medevit.elexis.ehc.ui.vacdoc.wizard.ImportVaccinationsWizard;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Patient;

public class DownloadMeineImpfungenHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			Patient patient = ElexisEventDispatcher.getSelectedPatient();
			if (patient != null) {
				ProgressMonitorDialog progress = new ProgressMonitorDialog(HandlerUtil.getActiveShell(event));
				try {
					progress.run(false, true, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException, InterruptedException {
							monitor.beginTask("Impfungen importieren von meineimpfungen ...", IProgressMonitor.UNKNOWN);

							List<org.ehealth_connector.common.mdht.Patient> patients = MeineImpfungenServiceHolder
									.getService().getPatients(patient);
							if (patients != null && !patients.isEmpty()) {
								if (patients.size() == 1) {
									List<CdaChVacd> documents = MeineImpfungenServiceHolder.getService()
											.getDocuments(patients.get(0));
									Optional<CdaChVacd> latestVacDoc = getLatestVacdoc(documents);
									latestVacDoc.ifPresent(vd -> {
										ImportVaccinationsWizard wizard = new ImportVaccinationsWizard();
										try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
											CDAUtil.save(vd.getDocRoot().getClinicalDocument(), output);
											wizard.setDocument(new ByteArrayInputStream(output.toByteArray()));

											WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event),
													wizard);
											dialog.open();
										} catch (Exception e) {
											LoggerFactory.getLogger(DownloadMeineImpfungenHandler.class)
													.error("Error processing downloaded eVACDOC", e); //$NON-NLS-1$
										}
									});
								} else {
									MessageDialog.openError(HandlerUtil.getActiveShell(event), "meineimpfungen",
											"Mehrere Patienten für [" + patient.getLabel(false)
													+ "] auf meineimpfungen gefunden.");
								}
							} else {
								MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "meineimpfungen",
										"Kein Patient [" + patient.getLabel(false) + "] auf meineimpfungen gefunden.");
							}
							monitor.done();
						}
					});
				} catch (InvocationTargetException | InterruptedException e) {
					LoggerFactory.getLogger(DownloadMeineImpfungenHandler.class).warn("Exception on patient lookup", e); //$NON-NLS-1$
					MessageDialog.openError(HandlerUtil.getActiveShell(event), "meineimpfungen",
							"Es ist ein Fehler aufgetreten.");
				}
			} else {
				MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "meineimpfungen",
						"Kein Patient ausgewählt");
			}
		} catch (IllegalStateException ise) {
			LoggerFactory.getLogger(DownloadMeineImpfungenHandler.class).error("Service not available", ise); //$NON-NLS-1$
			MessageDialog.openError(HandlerUtil.getActiveShell(event), "meineimpfungen",
					"meineimpfungen nicht verfügbar");
		}
		return null;
	}

	private Optional<CdaChVacd> getLatestVacdoc(List<CdaChVacd> documents) {
		CdaChVacd ret = null;
		for (CdaChVacd cdaChVacd : documents) {
			if (ret == null) {
				ret = cdaChVacd;
			} else if (cdaChVacd.getTimestamp().after(ret.getTimestamp())) {
				ret = cdaChVacd;
			}
		}
		return Optional.ofNullable(ret);
	}
}
