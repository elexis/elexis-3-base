package at.medevit.elexis.inbox.core.elements;

import at.medevit.elexis.inbox.core.elements.service.ServiceComponent;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.data.Kontakt;
import ch.elexis.data.LabResult;

public class LabResultCreateListener extends ElexisEventListenerImpl {
	public LabResultCreateListener(){
		super(LabResult.class, ElexisEvent.EVENT_CREATE);
	}
	
	@Override
	public void catchElexisEvent(ElexisEvent ev){
		LabResult result = (LabResult) ev.getObject();
		if (result != null && result.getPatient() != null) {
			ServiceComponent.getService().createInboxElement(result.getPatient(),
				CoreHub.actMandant, result);
			Kontakt doctor = result.getPatient().getStammarzt();
			if (doctor != null && doctor.exists() && !doctor.equals(CoreHub.actMandant)) {
				ServiceComponent.getService().createInboxElement(
					ElexisEventDispatcher.getSelectedPatient(), doctor, result);
			}
		}
	}
}
