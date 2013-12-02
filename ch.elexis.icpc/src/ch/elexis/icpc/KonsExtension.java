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
package ch.elexis.icpc;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.StyleRange;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.IDiagnose;
import ch.elexis.data.Konsultation;
import ch.elexis.core.ui.text.EnhancedTextField;
import ch.elexis.core.ui.text.IRichTextDisplay;
import ch.elexis.core.ui.util.IKonsExtension;

public class KonsExtension implements IKonsExtension {
	IRichTextDisplay mine;
	static final String EPISODE_TITLE = "Problem: ";
	
	public String connect(final IRichTextDisplay tf){
		mine = tf;
		mine.addDropReceiver(Episode.class, this);
		return Activator.PLUGIN_ID;
	}
	
	public boolean doLayout(final StyleRange n, final String provider, final String id){
		n.background = UiDesk.getColor(Desk.COL_GREEN);
		return true;
	}
	
	public boolean doXRef(final String refProvider, final String refID){
		Encounter enc = Encounter.load(refID);
		if (enc.exists()) {
			ElexisEventDispatcher.fireSelectionEvent(enc);
		}
		return true;
	}
	
	public IAction[] getActions(){
		// TODO Auto-generated method stub
		return null;
	}
	
	public void insert(final Object o, final int pos){
		if (o instanceof Episode) {
			Episode ep = (Episode) o;
			final Konsultation k =
				(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
			Encounter enc = new Encounter(k, ep);
			List<IDiagnose> diags = ep.getDiagnoses();
			for (IDiagnose dg : diags) {
				k.addDiagnose(dg);
			}
			mine.insertXRef(pos, EPISODE_TITLE + ep.getLabel(), Activator.PLUGIN_ID, enc.getId());
			k.updateEintrag(mine.getContentsAsXML(), false);
			ElexisEventDispatcher.update(k);
		}
		
	}
	
	public void removeXRef(final String refProvider, final String refID){
		Encounter encounter = Encounter.load(refID);
		encounter.delete();
		
	}
	
	public void setInitializationData(final IConfigurationElement config,
		final String propertyName, final Object data) throws CoreException{
		// TODO Auto-generated method stub
		
	}
	
}
