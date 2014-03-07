/*******************************************************************************
 * Copyright (c) 2010, Oliver Egger, visionary ag
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *    
 *******************************************************************************/
package ch.docbox.elexis;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * Our sample action implements workbench action delegate. The action proxy will be created by the
 * workbench and shown in the UI. When the user tries to use the action, this delegate will be
 * created and execution will be delegated to it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public abstract class DocboxAction implements IWorkbenchWindowActionDelegate {
	protected IWorkbenchWindow window;
	
	/**
	 * The constructor.
	 */
	public DocboxAction(){}
	
	public boolean hasValidDocboxCredentials(){
		if (!UserDocboxPreferences.hasValidDocboxCredentials()) {
			PreferenceDialog preferenceDialog =
				PreferencesUtil.createPreferenceDialogOn(window.getShell(),
					UserDocboxPreferences.ID, null, null);
			if (preferenceDialog.open() == Dialog.CANCEL) {
				return false;
			}
			return UserDocboxPreferences.hasValidDocboxCredentials();
		}
		return true;
	}
	
	/**
	 * Selection in the workbench has been changed. We can change the state of the 'real' action
	 * here if we want, but this can only happen after the delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection){}
	
	/**
	 * We can use this method to dispose of any system resources we previously allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose(){}
	
	/**
	 * We will cache window object in order to be able to provide parent shell for the message
	 * dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window){
		this.window = window;
	}
}