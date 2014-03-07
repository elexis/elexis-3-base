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
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.util.Log;
import ch.rgw.tools.ExHandler;
import ch.swissmedicalsuite.HCardBrowser;

/**
 * Our sample action implements workbench action delegate. The action proxy will be created by the
 * workbench and shown in the UI. When the user tries to use the action, this delegate will be
 * created and execution will be delegated to it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class DocboxBrowserOpenAction extends DocboxAction {
	
	protected static Log log = Log.get("DocboxBrowserOpenAction"); //$NON-NLS-1$
	
	/**
	 * The constructor.
	 */
	public DocboxBrowserOpenAction(){}
	
	/**
	 * The action has been activated. The argument of the method represents the 'real' action
	 * sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action){
		if (!hasValidDocboxCredentials()) {
			return;
		}
		try {
			log.log("DocboxBrowserOpenAction runnin action", Log.DEBUGMSG);
			if (UserDocboxPreferences.useHCard() && UserDocboxPreferences.getPort() != null) {
				log.log("usinghcard and service ok, trying browser", Log.DEBUGMSG);
				HCardBrowser hCardBrowser =
					new HCardBrowser(UserDocboxPreferences.getDocboxLoginID(false),
						UserDocboxPreferences.getDocboxBrowserUrl());
				Termin termin = (Termin) ElexisEventDispatcher.getSelected(Termin.class);
				if (termin != null) {
					int retval =
						hCardBrowser.setAppointment(DocboxTermin.getDocboxTerminId(termin));
					log.log("hcardbrowser retval " + retval, Log.DEBUGMSG);
				} else {
					int retval = hCardBrowser.setHome();
					log.log("hcardbrowser retval " + retval, Log.DEBUGMSG);
				}
			} else {
				DocboxView docboxView =
					(DocboxView) window.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.showView("ch.docbox.elexis.DocboxView");
				if (docboxView != null) {
					Termin termin = (Termin) ElexisEventDispatcher.getSelected(Termin.class);
					if (termin != null) {
						docboxView.setAppointment(DocboxTermin.getDocboxTerminId(termin));
					} else {
						docboxView.setHome();
					}
				}
			}
		} catch (PartInitException e) {
			ExHandler.handle(e);
		}
	}
	
}