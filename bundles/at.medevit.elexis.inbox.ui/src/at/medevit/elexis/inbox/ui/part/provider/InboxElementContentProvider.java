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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.model.IInboxElementService.State;
import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;

public class InboxElementContentProvider implements ITreeContentProvider {
	
	HashMap<IPatient, PatientInboxElements> map = new HashMap<IPatient, PatientInboxElements>();
	private List<PatientInboxElements> items;
	
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
			List<IInboxElement> input = (List<IInboxElement>) newInput;
			// refresh map and list
			map.clear();
			items = Collections.emptyList();
			Display.getDefault().asyncExec(() -> {
				viewer.refresh();
			});
			Job job = new Job("Loading Inbox") {
				@Override
				protected IStatus run(IProgressMonitor monitor){
					monitor.beginTask("Lade Inbox", input.size());
					for (IInboxElement inboxElement : input) {
						IPatient patient = inboxElement.getPatient();
						PatientInboxElements patientInbox = map.get(patient);
						if (patientInbox == null) {
							patientInbox = new PatientInboxElements(patient);
							map.put(patient, patientInbox);
						}
						patientInbox.addElement(inboxElement);
						monitor.worked(1);
					}
					items = new ArrayList<PatientInboxElements>(map.values());
					Display.getDefault().asyncExec(() -> {
						viewer.refresh();
					});
					return Status.OK_STATUS;
				}
				
			};
			job.schedule();
		}
	}
	
	public void refreshElement(IInboxElement inboxElement){
		IPatient patient = inboxElement.getPatient();
		PatientInboxElements patientInboxElement = map.get(patient);
		// remove seen and add unseen
		if (patientInboxElement != null) {
			if (inboxElement.getState() == State.SEEN) {
				patientInboxElement.removeElement(inboxElement);
			} else {
				IMandator activeMandant =
					ContextServiceHolder.get().getActiveMandator().orElse(null);
				if (inboxElement.getMandator().equals(activeMandant)) {
					patientInboxElement.addElement(inboxElement);
				} else {
					patientInboxElement.removeElement(inboxElement);
				}
			}
		} else if (inboxElement.getState() == State.NEW) {
			patientInboxElement = new PatientInboxElements(patient);
			patientInboxElement.addElement(inboxElement);
		}
	}
	
	public void refreshElement(PatientInboxElements patientInbox){
		if (patientInbox.getElements().isEmpty()) {
			items.remove(patientInbox);
		} else {
			IMandator activeMandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
			IMandator inboxMandant = patientInbox.getElements().get(0).getMandator();
			if (!inboxMandant.equals(activeMandant)) {
				items.remove(patientInbox);
			}
		}
	}
}