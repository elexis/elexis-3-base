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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.Log;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;

public class DocboxArztArztAction extends DocboxAction {
	
	private Patient patient;
	private Kontakt kontakt;
	
	protected static Log log = Log.get("DocboxArztArztAction"); //$NON-NLS-1$
	
	/**
	 * The constructor.
	 */
	public DocboxArztArztAction(){}
	
	/**
	 * The action has been activated. The argument of the method represents the 'real' action
	 * sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action){
		try {
			if (CoreHub.getActContact() != null) {
				patient = ElexisEventDispatcher.getSelectedPatient();
				kontakt = (Kontakt) ElexisEventDispatcher.getSelected(Kontakt.class);
				if (patient == null) {
					MessageBox box =
						new MessageBox(UiDesk.getDisplay().getActiveShell(), SWT.ICON_ERROR);
					box.setText(Messages.DocboxArztArztAction_NoPatientSelectedText);
					box.setMessage(Messages.DocboxArztArztAction_NoPatientSelectedMessage);
					box.open();
					return;
				}
				
				if (!hasValidDocboxCredentials()) {
					return;
				}
				
				DocboxArztArztDialog dlg = new DocboxArztArztDialog(patient, kontakt);
				dlg.open();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
