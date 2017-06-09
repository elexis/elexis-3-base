package at.medevit.elexis.emediplan;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.emediplan.core.EMediplanServiceHolder;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medication;
import at.medevit.elexis.emediplan.ui.ImportEMediplanDialog;
import ch.elexis.barcode.scanner.BarcodeScannerMessage;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.medication.views.MedicationView;
import ch.elexis.data.Patient;

public class Startup implements IStartup {
	private static Logger logger = LoggerFactory.getLogger(Startup.class);
	
	ElexisEventListener elexisEventListenerImpl;
	
	@Override
	public void earlyStartup(){
		elexisEventListenerImpl =
			new ElexisEventListenerImpl(BarcodeScannerMessage.class, ElexisEvent.EVENT_UPDATE) {
				public void run(ElexisEvent ev){
					BarcodeScannerMessage b = (BarcodeScannerMessage) ev.getGenericObject();
					if (hasMediplanHeader(b.getChunk())) {
						Medication medication =
							EMediplanServiceHolder.getService().createModelFromChunk(b.getChunk());
						EMediplanServiceHolder.getService()
							.addExistingArticlesToMedication(medication);
						if (medication != null) {
							if (medication.Patient != null
								&& medication.Patient.patientId != null) {
								Patient patient = Patient.load(medication.Patient.patientId);
								if (patient.exists()) {
									ElexisEventDispatcher.fireSelectionEvent(patient);
									
									UiDesk.getDisplay().asyncExec(new Runnable() {
										public void run(){
											try {
												PlatformUI.getWorkbench().getActiveWorkbenchWindow()
													.getActivePage()
													.showView(MedicationView.PART_ID);
											} catch (PartInitException e) {
												logger.warn("cannot open view with id: "
													+ MedicationView.PART_ID, e);
											}
											logger.debug("Opening ImportEMediplanDialog");
											ImportEMediplanDialog dlg = new ImportEMediplanDialog(
												UiDesk.getTopShell(), medication);
											dlg.open();
										}
									});
								}
							}
							
						}
					}
				}
			};
		ElexisEventDispatcher.getInstance().addListeners(elexisEventListenerImpl);
		
	}
	
	private boolean hasMediplanHeader(String chunk){
		return chunk.startsWith("CHMED");
	}
}
