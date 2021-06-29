package at.medevit.elexis.inbox.core.elements;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.core.elements.service.ServiceComponent;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ILabOrder;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;

public class AddLabInboxElement implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(AddLabInboxElement.class);

	private static final int MAX_WAIT = 40;

	private ILabResult labResult;
	
	public AddLabInboxElement(ILabResult labResult){
		this.labResult = labResult;
	}

	@Override
	public void run(){
		// we have to wait for the fields to be set
		if (labResult.getPatient() == null) {
			int waitForFields = 0;
			while (waitForFields < MAX_WAIT) {
				try {
					waitForFields++;
					Thread.sleep(500);
					if (labResult.getPatient() != null) {
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
		
		IPatient patient = labResult.getPatient();
		IContact doctor = labResult.getPatient().getFamilyDoctor();
		IMandator assignedMandant = loadAssignedMandant(true);
		
		// patient has NO stammarzt 
		if (doctor == null) {
			if (assignedMandant == null) {
				// if stammarzt and assigned contact is null use active mandant
				assignedMandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
			}
		} else if (doctor.isMandator()) {
			// stammarzt is defined
			logger.debug("Creating InboxElement for result [" + labResult.getId() + "] and patient "
				+ patient.getLabel() + " for mandant " + doctor.getLabel());
			ServiceComponent.get().createInboxElement(patient,
				ServiceComponent.load(doctor.getId(), IMandator.class),
				labResult);
		}
		
		// an assigned contact was found that is different than the stammarzt
		if (assignedMandant != null && !assignedMandant.equals(doctor)) {
			logger.debug("Creating InboxElement for result [" + labResult.getId() + "] and patient "
				+ patient.getLabel() + " for mandant " + assignedMandant.getLabel());
			ServiceComponent.get().createInboxElement(patient, assignedMandant, labResult);
		}
	}
	
	private IMandator loadAssignedMandant(boolean retry){
		List<ILabOrder> orders = ServiceComponent.getLabOrders(labResult);
		
		if (orders != null && !orders.isEmpty()) {
			if (orders.get(0).getMandator() != null) {
				return orders.get(0).getMandator();
			}
		}
		
		// sometimes the mandant is persisted delayed from another thread - we have to try again to fetch the mandant id
		if (retry)
		{
			try {
				Thread.sleep(1500);
				return loadAssignedMandant(false);
			}
			 catch (InterruptedException e) {
				/* ignore */
			}
		}
		return null;
	}
}
