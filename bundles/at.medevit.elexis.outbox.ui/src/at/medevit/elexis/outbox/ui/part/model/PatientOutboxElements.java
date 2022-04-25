package at.medevit.elexis.outbox.ui.part.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import at.medevit.elexis.outbox.model.IOutboxElement;
import ch.elexis.core.model.IPatient;

public class PatientOutboxElements {

	private IPatient patient;
	private HashSet<IOutboxElement> elements = new HashSet<IOutboxElement>();

	public PatientOutboxElements(IPatient patient) {
		this.patient = patient;
	}

	public List<IOutboxElement> getElements() {
		return new ArrayList<IOutboxElement>(elements);
	}

	public void addElement(IOutboxElement element) {
		elements.add(element);
	}

	public void removeElement(IOutboxElement element) {
		elements.remove(element);
	}

	public IPatient getPatient() {
		return patient;
	}

	public String toString() {
		if (patient == null) {
			return "nicht zugeordnet";
		}
		return patient.getLabel();
	}

	public Long getHighestLastupdate() {
		return elements.stream().mapToLong(e -> e.getLastupdate()).max().orElse(0L);
	}

	public void clear() {
		elements = new HashSet<IOutboxElement>();
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}
}