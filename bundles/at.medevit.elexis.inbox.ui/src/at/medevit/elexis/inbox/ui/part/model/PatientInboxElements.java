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
import java.util.HashSet;
import java.util.List;

import at.medevit.elexis.inbox.model.IInboxElement;
import ch.elexis.core.model.IPatient;

public class PatientInboxElements {

	private IPatient patient;
	private HashSet<IInboxElement> elements = new HashSet<IInboxElement>();

	public PatientInboxElements(IPatient patient) {
		this.patient = patient;
	}

	public List<IInboxElement> getElements() {
		return new ArrayList<IInboxElement>(elements);
	}

	public void addElement(IInboxElement element) {
		elements.add(element);
	}

	public void removeElement(IInboxElement element) {
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
}