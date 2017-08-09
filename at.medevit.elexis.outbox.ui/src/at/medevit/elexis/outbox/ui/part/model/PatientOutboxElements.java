package at.medevit.elexis.outbox.ui.part.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import at.medevit.elexis.outbox.model.OutboxElement;
import ch.elexis.data.Patient;

public class PatientOutboxElements {
	
	private Patient patient;
	private HashSet<OutboxElement> elements = new HashSet<OutboxElement>();
	
	public PatientOutboxElements(Patient patient){
		this.patient = patient;
	}
	
	public List<OutboxElement> getElements(){
		return new ArrayList<OutboxElement>(elements);
	}
	
	public void addElement(OutboxElement element){
		elements.add(element);
	}
	
	public void removeElement(OutboxElement element){
		elements.remove(element);
	}
	
	public Patient getPatient(){
		return patient;
	}
	
	public String toString(){
		if (patient == null) {
			return "nicht zugeordnet";
		}
		return patient.getLabel();
	}
}