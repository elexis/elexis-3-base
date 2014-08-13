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
		if (patient == null) {
			return "nicht zugeordnet";
		}
		return patient.getLabel();
	}
}