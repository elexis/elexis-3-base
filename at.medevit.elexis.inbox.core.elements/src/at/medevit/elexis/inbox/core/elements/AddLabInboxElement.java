package at.medevit.elexis.inbox.core.elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.core.elements.service.ServiceComponent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Kontakt;
import ch.elexis.data.LabResult;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;

public class AddLabInboxElement implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(AddLabInboxElement.class);

	private static final int MAX_WAIT = 40;

	private LabResult labResult;
	
	public AddLabInboxElement(LabResult labResult){
		this.labResult = labResult;
	}

	@Override
	public void run(){
		// we have to wait for the fields to be set
		if (labResult.get(LabResult.PATIENT_ID) == null
			|| labResult.get(LabResult.PATIENT_ID).isEmpty()) {
			int waitForFields = 0;
			while (waitForFields < MAX_WAIT) {
				try {
					waitForFields++;
					Thread.sleep(500);
					if (labResult.get(LabResult.PATIENT_ID) != null
						&& !labResult.get(LabResult.PATIENT_ID).isEmpty()) {
						break;
					}
				} catch (InterruptedException e) {
					// ignore
				}
			}
			if (waitForFields == MAX_WAIT) {
				logger
					.warn(String.format("Could not get data from result [%s].", labResult.getId()));
				return;
			}
		}
		
		Patient patient = labResult.getPatient();
		Mandant mandant = (Mandant) ElexisEventDispatcher.getSelected(Mandant.class);
		logger.debug("Creating InboxElement for result [" + labResult.getId() + "] and patient "
			+ patient.getLabel());
		ServiceComponent.getService().createInboxElement(patient, mandant, labResult);
		
		Kontakt doctor = labResult.getPatient().getStammarzt();
		if (doctor != null && doctor.exists() && !doctor.equals(mandant)) {
			ServiceComponent.getService().createInboxElement(patient, doctor, labResult);
		}
	}
}
