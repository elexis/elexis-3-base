package at.medevit.elexis.inbox.ui.part.model;

import java.util.ArrayList;
import java.util.List;

import at.medevit.elexis.inbox.model.InboxElement;
import ch.elexis.data.Patient;

public class PatientInboxElements {
	
	private Patient patient;
	private List<InboxElement> elements = new ArrayList<InboxElement>();
	
	public PatientInboxElements(Patient patient){
		this.patient = patient;
	}
	
	public List<InboxElement> getElements(){
		return elements;
	}
	
	public void addElement(InboxElement element){
		elements.add(element);
	}
	
	public String toString(){
		return patient.getLabel();
	}
}