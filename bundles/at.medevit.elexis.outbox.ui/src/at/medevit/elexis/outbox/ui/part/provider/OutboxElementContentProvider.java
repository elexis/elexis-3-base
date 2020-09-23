package at.medevit.elexis.outbox.ui.part.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import at.medevit.elexis.outbox.model.IOutboxElement;
import at.medevit.elexis.outbox.model.IOutboxElementService.State;
import at.medevit.elexis.outbox.ui.part.model.PatientOutboxElements;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;

public class OutboxElementContentProvider implements ITreeContentProvider {
	
	HashMap<IPatient, PatientOutboxElements> map = new HashMap<IPatient, PatientOutboxElements>();
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
			List<IOutboxElement> input = (List<IOutboxElement>) newInput;
			// refresh map and list
			map.values().forEach(p -> p.clear());
			for (IOutboxElement outboxElement : input) {
				IPatient patient = outboxElement.getPatient();
				PatientOutboxElements patientOutbox = map.get(patient);
				if (patientOutbox == null) {
					patientOutbox = new PatientOutboxElements(patient);
					map.put(patient, patientOutbox);
				}
				patientOutbox.addElement(outboxElement);
			}
			// remove empty PatientOutboxElements
			Iterator<Entry<IPatient, PatientOutboxElements>> iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				if (iter.next().getValue().isEmpty()) {
					iter.remove();
				}
			}
			items = new ArrayList<>(map.values());
		}
	}
	
	public void refreshElement(IOutboxElement outboxElement){
		IPatient patient = outboxElement.getPatient();
		PatientOutboxElements patientOutboxElements = map.get(patient);
		// remove seen and add unseen
		if (patientOutboxElements != null) {
			IMandator activeMandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
			if (outboxElement.isDeleted()) {
				patientOutboxElements.removeElement(outboxElement);
			} else if (outboxElement.getMandator().equals(activeMandant)) {
				patientOutboxElements.addElement(outboxElement);
			} else {
				patientOutboxElements.removeElement(outboxElement);
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
			IMandator activeMandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
			IMandator outboxMandant = patientOutboxElements.getElements().get(0).getMandator();
			if (!outboxMandant.equals(activeMandant)) {
				items.remove(patientOutboxElements);
			}
		}
	}
	
	public PatientOutboxElements getPatientOutboxElements(Object selectedObj){
		if (selectedObj instanceof IOutboxElement) {
			return map.get(((IOutboxElement) selectedObj).getPatient());
		} else if (selectedObj instanceof PatientOutboxElements) {
			return (PatientOutboxElements) selectedObj;
		}
		return null;
	}
}