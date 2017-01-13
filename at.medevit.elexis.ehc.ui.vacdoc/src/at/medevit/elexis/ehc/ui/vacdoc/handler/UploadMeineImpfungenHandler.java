package at.medevit.elexis.ehc.ui.vacdoc.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.ehealth_connector.cda.ch.vacd.CdaChVacd;
import org.ehealth_connector.common.Identificator;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.ui.vacdoc.service.MeineImpfungenServiceComponent;
import at.medevit.elexis.ehc.ui.vacdoc.service.VacdocServiceComponent;
import at.medevit.elexis.ehc.vacdoc.service.MeineImpfungenService;
import at.medevit.elexis.impfplan.model.po.Vaccination;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Patient;

public class UploadMeineImpfungenHandler extends AbstractHandler implements IHandler {
	
	private Vaccination selectedVacination;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		try {
			Patient patient = ElexisEventDispatcher.getSelectedPatient();
			ISelection selection =
				HandlerUtil.getActiveSite(event).getSelectionProvider().getSelection();
			if (selection instanceof StructuredSelection && !selection.isEmpty()) {
				selectedVacination =
					(Vaccination) ((StructuredSelection) selection).getFirstElement();
			}
			if (patient != null && selectedVacination != null) {
				ProgressMonitorDialog progress =
					new ProgressMonitorDialog(HandlerUtil.getActiveShell(event));
				try {
					progress.run(false, true, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
							throws InvocationTargetException, InterruptedException{
							monitor.beginTask("Impfung export nach meineimpfungen ...",
								IProgressMonitor.UNKNOWN);
							
							List<org.ehealth_connector.common.Patient> patients =
								MeineImpfungenServiceComponent.getService().getPatients(patient);
							if (patients != null && !patients.isEmpty()) {
								if (patients.size() == 1) {
									CdaChVacd document =
										VacdocServiceComponent.getService().getVacdocDocument(
											patient, ElexisEventDispatcher.getSelectedMandator());
									setMeineImpfungenPatientId(document.getPatient(),
										patients.get(0));
									VacdocServiceComponent.getService().addVaccinations(document,
										Collections.singletonList(selectedVacination));
									boolean success = MeineImpfungenServiceComponent.getService()
										.uploadDocument(document);
									if (!success) {
										MessageDialog.openError(HandlerUtil.getActiveShell(event),
											"meineimpfungen",
											"Beim upload ist ein Fehler aufgetreten.");
									}
								} else {
									MessageDialog.openError(HandlerUtil.getActiveShell(event),
										"meineimpfungen",
										"Mehrere Patienten für [" + patient.getLabel(false)
											+ "] auf meineimpfungen gefunden.");
								}
							} else {
								MessageDialog.openInformation(HandlerUtil.getActiveShell(event),
									"meineimpfungen", "Kein Patient [" + patient.getLabel(false)
										+ "] auf meineimpfungen gefunden.");
							}
							monitor.done();
						}
						
						private void setMeineImpfungenPatientId(
							org.ehealth_connector.common.Patient docPatient,
							org.ehealth_connector.common.Patient miPatient){
							List<Identificator> ids = miPatient.getIds();
							if (ids != null && !ids.isEmpty()) {
								for (Identificator identificator : ids) {
									if (MeineImpfungenService.PDQ_REQUEST_PATID_OID
										.equals(identificator.getRoot())) {
										docPatient.addId(identificator);
										return;
									}
								}
							}
						}
					});
				} catch (InvocationTargetException | InterruptedException e) {
					LoggerFactory.getLogger(OpenMeineImpfungenHandler.class)
						.warn("Exception on patient lookup", e);
					MessageDialog.openError(HandlerUtil.getActiveShell(event), "meineimpfungen",
						"Es ist ein Fehler aufgetreten.");
				}
			} else {
				MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "meineimpfungen",
					"Kein Patient, oder keine Impfung ausgewählt");
			}
		} catch (IllegalStateException ise) {
			LoggerFactory.getLogger(OpenMeineImpfungenHandler.class).error("Service not available",
				ise);
			MessageDialog.openError(HandlerUtil.getActiveShell(event), "meineimpfungen",
				"meineimpfungen nicht verfügbar");
		}
		return null;
	}
	
	@Override
	public boolean isEnabled(){
		try {
			return MeineImpfungenServiceComponent.getService().isVaild();
		} catch (IllegalStateException ise) {
			// do nothing, false is returned
		}
		return false;
	}
}
