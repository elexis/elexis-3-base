/*******************************************************************************
 * Copyright (c) 2009-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Sponsoring:
 * 	 mediX Notfallpaxis, diepraxen Stauffacher AG, Zürich
 * 
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.agenda.ui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.ui.provider.TerminListSorter;
import ch.elexis.agenda.ui.provider.TerminListWidgetProvider;
import ch.elexis.agenda.ui.provider.TermineLabelProvider;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultContentProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;

public class TerminListeView extends ViewPart implements IActivationListener, ElexisEventListener {
	public static final String ID = "ch.elexis.agenda.Terminliste";
	ScrolledForm form;
	CommonViewer cv = new CommonViewer();
	
	public TerminListeView(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void createPartControl(Composite parent){
		form = UiDesk.getToolkit().createScrolledForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Composite body = form.getBody();
		body.setLayout(new GridLayout());
		
		ViewerConfigurer vc = new ViewerConfigurer(new DefaultContentProvider(cv, Termin.class) {
			@Override
			public Object[] getElements(Object inputElement){
				Patient p = ElexisEventDispatcher.getSelectedPatient();
				Query<Termin> qbe = new Query<Termin>(Termin.class);
				if (p == null) {
					qbe.add(Termin.FLD_PATIENT, Query.EQUALS, "--");
				} else {
					qbe.add(Termin.FLD_PATIENT, Query.EQUALS, p.getId());
					qbe.orderBy(false, Termin.FLD_TAG);
				}
				return qbe.execute().toArray();
			}
		}, new TermineLabelProvider(), new TerminListWidgetProvider());
		cv.create(vc, body, SWT.NONE, this);
		cv.getConfigurer().getContentProvider().startListening();
		
		GlobalEventDispatcher.addActivationListener(this, this);
	}
	
	@Override
	public void setFocus(){}
	
	public void activation(boolean mode){
		if (mode) {
			updateSelection(ElexisEventDispatcher.getSelectedPatient());
		}
	}
	
	@Override
	public void dispose(){
		GlobalEventDispatcher.removeActivationListener(this, this);
		super.dispose();
	}
	
	public void visible(boolean mode){
		if (mode) {
			// selectionEvent(GlobalEvents.getSelectedPatient());
			ElexisEventDispatcher.getInstance().addListeners(this);
		} else {
			ElexisEventDispatcher.getInstance().removeListeners(this);
		}
	}
	
	public void catchElexisEvent(final ElexisEvent ev){
		UiDesk.asyncExec(new Runnable() {
			public void run(){
				if (ev.getType() == ElexisEvent.EVENT_SELECTED) {
					updateSelection((Patient) ev.getObject());
				} else if (ev.getType() == ElexisEvent.EVENT_DESELECTED) {
					form.setText(Messages.TerminListView_noPatientSelected);
				}
			}
		});
	}
	
	public ElexisEvent getElexisEventFilter(){
		return new ElexisEvent(null, Patient.class, ElexisEvent.EVENT_SELECTED
			| ElexisEvent.EVENT_DESELECTED);
	}
	
	private void updateSelection(Patient patient){
		if (patient == null) {
			form.setText(Messages.TerminListView_noPatientSelected);
		} else {
			form.setText(patient.getLabel());
			cv.notify(CommonViewer.Message.update);
		}
	}
	
	/**
	 * Sorts the appointments in the TerminListView. Use SWT.UP for ascending and SWT.DOWN for
	 * descending.
	 * 
	 * @param sortDirection
	 */
	public void sort(int sortDirection){
		TerminListSorter sorter = new TerminListSorter();
		sorter.setDirection(sortDirection);
		cv.getViewerWidget().setSorter(sorter);
	}
}
