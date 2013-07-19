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
package ch.elexis.base.ch.ebanking.esr;

import ch.elexis.core.data.Rechnung;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;

/**
 * Eigentlich nur zur Demonstration, dass ein Selectionlistener auch unabhängig von einer View
 * existieren kann
 * 
 * @author gerry
 * 
 */
public class ESRSelectionListener implements ElexisEventListener {
	
	// ESRSelectionListener(IViewSite site) { }
	
	void activate(boolean mode){
		if (mode) {
			ElexisEventDispatcher.getInstance().addListeners(this);
		} else {
			ElexisEventDispatcher.getInstance().removeListeners(this);
		}
	}
	
	public void catchElexisEvent(ElexisEvent ev){
		ESRRecord esr = (ESRRecord) ev.getObject();
		Rechnung rn = esr.getRechnung();
		if (rn != null) {
			ElexisEventDispatcher.fireSelectionEvent(esr.getRechnung());
		}
	}
	
	private final ElexisEvent eetmpl = new ElexisEvent(null, ESRRecord.class,
		ElexisEvent.EVENT_SELECTED);
	
	public ElexisEvent getElexisEventFilter(){
		return eetmpl;
	}
	
}
