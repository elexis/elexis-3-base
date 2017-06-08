package at.medevit.elexis.inbox.core.elements;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.core.elements.service.ServiceComponent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Kontakt;
import ch.elexis.data.LabOrder;
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
		Kontakt doctor = labResult.getPatient().getStammarzt();
		Mandant assignedMandant = loadAssignedMandant();
		
		// patient has NO stammarzt 
		if (doctor == null) {
			if (assignedMandant == null) {
				// if stammarzt and assigned contact is null use active mandant
				try {
					Thread.sleep(1500);
					assignedMandant = loadAssignedMandant();
				} catch (InterruptedException e) {
					/* ignore */
				}
				if (assignedMandant == null) {
					assignedMandant = (Mandant) ElexisEventDispatcher.getSelected(Mandant.class);
				}
			}
		} else {
			// stammarzt is defined
			logger.debug("Creating InboxElement for result [" + labResult.getId() + "] and patient "
				+ patient.getLabel() + " for mandant " + doctor.getLabel());
			ServiceComponent.getService().createInboxElement(patient, doctor, labResult);
		}
		
		// an assigned contact was found that is different than the stammarzt
		if (assignedMandant != null && !assignedMandant.equals(doctor)) {
			logger.debug("Creating InboxElement for result [" + labResult.getId() + "] and patient "
				+ patient.getLabel() + " for mandant " + assignedMandant.getLabel());
			ServiceComponent.getService().createInboxElement(patient, assignedMandant, labResult);
		}
	}
	
	private Mandant loadAssignedMandant(){
		List<LabOrder> orders =
			LabOrder.getLabOrders(labResult.getPatient(), null, null, labResult, null, null, null);
		if (orders != null && !orders.isEmpty()) {
			String mandantId = orders.get(0).get(LabOrder.FLD_MANDANT);
			if (mandantId != null && !mandantId.isEmpty()) {
				return Mandant.load(mandantId);
			}
		}
		return null;
	}
}
