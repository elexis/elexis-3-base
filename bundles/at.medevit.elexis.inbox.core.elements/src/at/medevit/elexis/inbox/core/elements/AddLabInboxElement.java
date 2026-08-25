package at.medevit.elexis.inbox.core.elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.core.elements.service.ServiceComponent;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;

public class AddLabInboxElement implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(AddLabInboxElement.class);

	private static final int MAX_WAIT = 40;

	private ILabResult labResult;

	public AddLabInboxElement(ILabResult labResult) {
		this.labResult = labResult;
	}

	@Override
	public void run() {
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
				logger.warn(String.format("Could not get data from result [%s].", labResult.getId())); //$NON-NLS-1$
				return;
			}
		}

		IPatient patient = labResult.getPatient();
		IMandator mandator = ServiceComponent.get()
				.getInboxElementMandator("at.medevit.elexis.inbox.core.ui.uiprovider", patient).orElse(null);

		if (mandator != null) {
			logger.debug("Creating InboxElement for result [" + labResult.getId() + "] and patient " //$NON-NLS-1$ //$NON-NLS-2$
					+ patient.getLabel() + " for mandant " + mandator.getLabel()); //$NON-NLS-1$
			ServiceComponent.get().createInboxElement(patient, mandator, labResult);
		}
	}
}
