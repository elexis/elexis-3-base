/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     T. Huster - initial API and implementation
 ******************************************************************************/
package at.medevit.elexis.swissmedic;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.program.Program;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.text.IRichTextDisplay;
import ch.elexis.core.ui.util.IKonsExtension;
import ch.elexis.data.Prescription;

public class SwissmedicSearchAction extends Action implements IKonsExtension, IHandler {
	
	public static final String ID = "at.medevit.elexis.swissmedic"; //$NON-NLS-1$
	private static SwissmedicSearchAction instance;
	
	public String connect(IRichTextDisplay tf){
		return "at.medevit.elexis.swissmedic"; //$NON-NLS-1$
	}
	
	public SwissmedicSearchAction(){
		super("Swissmedic suchen ..."); //$NON-NLS-1$
	}
	
	public boolean doLayout(StyleRange n, String provider, String id){
		return false;
	}
	
	public boolean doXRef(String refProvider, String refID){
		return false;
	}
	
	@Override
	public void run(){
		// get actual fix medication of the patient
		Prescription medication =
			(Prescription) ElexisEventDispatcher.getSelected(Prescription.class);
		
		String ean = null;
		String num = "";
		if (medication != null) {
			ean = medication.getArtikel().getEAN();
		}
		
		if (ean != null && !ean.isEmpty() && ean.length() >= 9) {
			num = ean.substring(4, 9);
		}
		
		String url = "http://www.swissmedicinfo.ch/ShowText.aspx?textType=FI&lang=DE&authNr=" + num; //$NON-NLS-1$
		Program.launch(url);
	}
	
	public IAction[] getActions(){
		return new IAction[] {
			this
		};
	}
	
	public void insert(Object o, int pos){
		// TODO Auto-generated method stub
		
	}
	
	public void removeXRef(String refProvider, String refID){
		// TODO Auto-generated method stub
		
	}
	
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
		throws CoreException{
		// TODO Auto-generated method stub
		
	}
	
	public void addHandlerListener(IHandlerListener handlerListener){
		// TODO Auto-generated method stub
		
	}
	
	public void dispose(){
		// TODO Auto-generated method stub
		
	}
	
	public Object execute(ExecutionEvent event) throws ExecutionException{
		if (instance != null)
			instance.run();
		return null;
	}
	
	public void removeHandlerListener(IHandlerListener handlerListener){
		// TODO Auto-generated method stub
	}
}
