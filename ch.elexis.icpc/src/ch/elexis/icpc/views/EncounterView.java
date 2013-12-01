/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/

package ch.elexis.icpc.views;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.actions.ElexisEvent;
import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.actions.ElexisEventListenerImpl;
import ch.elexis.actions.GlobalEventDispatcher;
import ch.elexis.actions.GlobalEventDispatcher.IActivationListener;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.icpc.Encounter;
import ch.elexis.util.SWTHelper;

public class EncounterView extends ViewPart implements IActivationListener {
	public static final String ID = "ch.elexis.icpc.encounterView";
	private EncounterDisplay display;
	
	private final ElexisEventListenerImpl eeli_pat = new ElexisEventListenerImpl(Patient.class,
		ElexisEvent.EVENT_SELECTED) {
		
		@Override
		public void runInUi(ElexisEvent ev){
			display.setEncounter(null);
		}
		
	};
	
	private final ElexisEventListenerImpl eeli_enc = new ElexisEventListenerImpl(Encounter.class,
		ElexisEvent.EVENT_SELECTED) {
		@Override
		public void runInUi(ElexisEvent ev){
			display.setEncounter((Encounter) ev.getObject());
		}
	};
	
	public EncounterView(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new GridLayout());
		display = new EncounterDisplay(parent);
		display.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		GlobalEventDispatcher.addActivationListener(this, getViewSite().getPart());
		
	}
	
	@Override
	public void dispose(){
		GlobalEventDispatcher.removeActivationListener(this, getViewSite().getPart());
	}
	
	@Override
	public void setFocus(){
		// TODO Auto-generated method stub
		
	}
	
	public void activation(boolean mode){
		
	}
	
	public void visible(boolean mode){
		if (mode) {
			ElexisEventDispatcher.getInstance().addListeners(eeli_enc, eeli_pat);
		} else {
			ElexisEventDispatcher.getInstance().removeListeners(eeli_enc, eeli_pat);
		}
		
	}
	
	public void clearEvent(Class template){
		// TODO Auto-generated method stub
		
	}
	
	public void selectionEvent(PersistentObject obj){
		if (obj instanceof Encounter) {
			display.setEncounter((Encounter) obj);
		} else if (obj instanceof Patient) {
			display.setEncounter(null);
		}
		
	}
	
}
