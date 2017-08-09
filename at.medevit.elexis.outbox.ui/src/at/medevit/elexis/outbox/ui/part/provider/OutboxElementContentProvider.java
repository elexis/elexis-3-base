package at.medevit.elexis.outbox.ui.part.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import at.medevit.elexis.outbox.model.IOutboxElementService.State;
import at.medevit.elexis.outbox.model.OutboxElement;
import at.medevit.elexis.outbox.ui.part.model.PatientOutboxElements;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;

public class OutboxElementContentProvider implements ITreeContentProvider {
	
	HashMap<Patient, PatientOutboxElements> map = new HashMap<Patient, PatientOutboxElements>();
	private ArrayList<PatientOutboxElements> items;
	
	public Object[] getElements(Object inputElement){
		if (items != null) {
			return items.toArray();
		}
		return Collections.emptyList().toArray();
	}
	
	public Object[] getChildren(Object parentElement){
		if (parentElement instanceof PatientOutboxElements) {
			return ((PatientOutboxElements) parentElement).getElements().toArray();
		} else {
			return null;
		}
	}
	
	public boolean hasChildren(Object element){
		return (element instanceof PatientOutboxElements);
	}
	
	public Object[] getParent(Object element){
		return null;
	}
	
	public void dispose(){
		// nothing to do
	}
	
	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
		if (newInput instanceof List<?>) {
			List<OutboxElement> input = (List<OutboxElement>) newInput;
			// refresh map and list
			map.clear();
			for (OutboxElement outboxElement : input) {
				Patient patient = outboxElement.getPatient();
				PatientOutboxElements patientOutbox = map.get(patient);
				if (patientOutbox == null) {
					patientOutbox = new PatientOutboxElements(patient);
					map.put(patient, patientOutbox);
				}
				patientOutbox.addElement(outboxElement);
			}
			items = new ArrayList<PatientOutboxElements>(map.values());
		}
	}
	
	public void refreshElement(OutboxElement outboxElement){
		Patient patient = outboxElement.getPatient();
		PatientOutboxElements patientOutboxElements = map.get(patient);
		// remove seen and add unseen
		if (patientOutboxElements != null) {
			if (outboxElement.getState() == State.SEEN) {
				patientOutboxElements.removeElement(outboxElement);
			} else {
				Mandant activeMandant = ElexisEventDispatcher.getSelectedMandator();
				if (outboxElement.getMandant().equals(activeMandant)) {
					patientOutboxElements.addElement(outboxElement);
				} else {
					patientOutboxElements.removeElement(outboxElement);
				}
			}
		} else if (outboxElement.getState() == State.NEW) {
			patientOutboxElements = new PatientOutboxElements(patient);
			patientOutboxElements.addElement(outboxElement);
		}
	}
	
	public void refreshElement(PatientOutboxElements patientOutboxElements){
		if (patientOutboxElements.getElements().isEmpty()) {
			items.remove(patientOutboxElements);
		} else {
			Mandant activeMandant = ElexisEventDispatcher.getSelectedMandator();
			Mandant outboxMandant = patientOutboxElements.getElements().get(0).getMandant();
			if (!outboxMandant.equals(activeMandant)) {
				items.remove(patientOutboxElements);
			}
		}
	}
}