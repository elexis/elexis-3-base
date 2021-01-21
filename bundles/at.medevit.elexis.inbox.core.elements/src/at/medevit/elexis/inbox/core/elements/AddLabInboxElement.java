package at.medevit.elexis.inbox.core.elements;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.core.elements.service.ServiceComponent;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.data.LabOrder;
import ch.elexis.data.LabResult;
import ch.elexis.data.Mandant;

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
		ILabResult iLabResult = NoPoUtil.loadAsIdentifiable(labResult, ILabResult.class).get();
		
		IPatient patient = iLabResult.getPatient();
		IContact doctor = iLabResult.getPatient().getFamilyDoctor();
		IMandator assignedMandant =
			NoPoUtil.loadAsIdentifiable(loadAssignedMandant(true), IMandator.class).orElse(null);
		
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
			ServiceComponent.getService().createInboxElement(patient,
				CoreModelServiceHolder.get().load(doctor.getId(), IMandator.class).get(),
				iLabResult);
		}
		
		// an assigned contact was found that is different than the stammarzt
		if (assignedMandant != null && !assignedMandant.equals(doctor)) {
			logger.debug("Creating InboxElement for result [" + labResult.getId() + "] and patient "
				+ patient.getLabel() + " for mandant " + assignedMandant.getLabel());
			ServiceComponent.getService().createInboxElement(patient, assignedMandant, iLabResult);
		}
	}
	
	private Mandant loadAssignedMandant(boolean retry){
		List<LabOrder> orders =
			LabOrder.getLabOrders(labResult.getPatient(), null, null, labResult, null, null, null);
		if (orders != null && !orders.isEmpty()) {
			String mandantId = orders.get(0).get(LabOrder.FLD_MANDANT);
			if (mandantId != null && !mandantId.isEmpty()) {
				return Mandant.load(mandantId);
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
