/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.inbox.ui.part.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import at.medevit.elexis.inbox.model.IInboxElementService.State;
import at.medevit.elexis.inbox.model.InboxElement;
import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;
import ch.elexis.data.Patient;

public class InboxElementContentProvider implements ITreeContentProvider {
	
	HashMap<Patient, PatientInboxElements> map = new HashMap<Patient, PatientInboxElements>();
	private ArrayList<PatientInboxElements> items;
	
	public Object[] getElements(Object inputElement){
		if (items != null) {
			return items.toArray();
		}
		return Collections.emptyList().toArray();
	}
	
	public Object[] getChildren(Object parentElement){
		if (parentElement instanceof PatientInboxElements) {
			return ((PatientInboxElements) parentElement).getElements().toArray();
		} else {
			return null;
		}
	}
	
	public boolean hasChildren(Object element){
		return (element instanceof PatientInboxElements);
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
			List<InboxElement> input = (List<InboxElement>) newInput;
			// refresh map and list
			for (InboxElement inboxElement : input) {
				Patient patient = inboxElement.getPatient();
				PatientInboxElements patientInbox = map.get(patient);
				if (patientInbox == null) {
					patientInbox = new PatientInboxElements(patient);
					map.put(patient, patientInbox);
				}
				patientInbox.addElement(inboxElement);
			}
			items = new ArrayList<PatientInboxElements>(map.values());
		}
	}
	
	public void refreshElement(InboxElement inboxElement){
		Patient patient = inboxElement.getPatient();
		PatientInboxElements patientInboxElement = map.get(patient);
		// remove seen and add unseen
		if (patientInboxElement != null) {
			if (inboxElement.getState() == State.SEEN) {
				patientInboxElement.removeElement(inboxElement);
			} else {
				patientInboxElement.addElement(inboxElement);
			}
		} else if (inboxElement.getState() == State.NEW) {
			patientInboxElement = new PatientInboxElements(patient);
			patientInboxElement.addElement(inboxElement);
		}
	}
	
	public void refreshElement(PatientInboxElements patientInbox){
		if (patientInbox.getElements().isEmpty()) {
			items.remove(patientInbox);
		}
	}
}