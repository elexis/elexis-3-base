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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.agenda.acl.ACLContributor;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.ui.provider.TerminListSorter;
import ch.elexis.agenda.ui.provider.TerminListWidgetProvider;
import ch.elexis.agenda.ui.provider.TermineLabelProvider;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.data.interfaces.IPersistentObject;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;
import ch.elexis.core.ui.locks.LockRequestingRestrictedAction;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.CommonViewer.PoDoubleClickListener;
import ch.elexis.core.ui.util.viewers.DefaultContentProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.dialogs.TerminDialog;

public class TerminListeView extends ViewPart implements IActivationListener {
	public static final String ID = "ch.elexis.agenda.Terminliste";
	ScrolledForm form;
	CommonViewer cv = new CommonViewer();
	LockRequestingRestrictedAction<Termin> terminAendernAction;
	
	private ElexisEventListener eeli_pat = new ElexisUiEventListenerImpl(Patient.class,
		ElexisEvent.EVENT_SELECTED | ElexisEvent.EVENT_DESELECTED) {
		public void runInUi(ElexisEvent ev){
			if (ev.getType() == ElexisEvent.EVENT_SELECTED) {
				updateSelection((Patient) ev.getObject());
			} else if (ev.getType() == ElexisEvent.EVENT_DESELECTED) {
				form.setText(Messages.TerminListView_noPatientSelected);
			}
		}
	};
	
	private ElexisEventListener eeli_term =
		new ElexisUiEventListenerImpl(Termin.class, ElexisEvent.EVENT_UPDATE) {
			public void runInUi(ElexisEvent ev){
				if (cv != null) {
					cv.notify(CommonViewer.Message.update);
				}
			};
		};
	
	public TerminListeView(){
		terminAendernAction = new LockRequestingRestrictedAction<Termin>(
			ACLContributor.CHANGE_APPOINTMENTS, ch.elexis.agenda.Messages.TagesView_changeTermin) {
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setToolTipText(ch.elexis.agenda.Messages.TagesView_changeThisTermin);
			}
			
			@Override
			public Termin getTargetedObject(){
				return (Termin) ElexisEventDispatcher.getSelected(Termin.class);
			}
			
			@Override
			public void doRun(Termin element){
				AcquireLockBlockingUi.aquireAndRun((IPersistentObject) element, new ILockHandler() {
					
					@Override
					public void lockFailed(){
						// do nothing
					}
					
					@Override
					public void lockAcquired(){
						TerminDialog.setActResource(element.getBereich());
						TerminDialog dlg = new TerminDialog(element);
						dlg.open();
					}
				});
				if (cv != null) {
					cv.notify(CommonViewer.Message.update);
				}
			}
		};
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
		cv.addDoubleClickListener(new PoDoubleClickListener() {
			@Override
			public void doubleClicked(PersistentObject obj, CommonViewer cv){
				terminAendernAction.run();
			}
		});
		
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
			ElexisEventDispatcher.getInstance().addListeners(eeli_pat, eeli_term);
		} else {
			ElexisEventDispatcher.getInstance().removeListeners(eeli_pat, eeli_term);
		}
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
