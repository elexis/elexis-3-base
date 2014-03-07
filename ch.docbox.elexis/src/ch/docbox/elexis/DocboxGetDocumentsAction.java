/*******************************************************************************
 * Copyright (c) 2008, Oliver Egger, visionary ag
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *    
 *******************************************************************************/
package ch.docbox.elexis;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import ch.elexis.core.ui.UiDesk;
import ch.rgw.tools.ExHandler;

/**
 * Performs the task go
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class DocboxGetDocumentsAction extends DocboxAction {
	
	public boolean showWaitCursor = false;
	
	public DocboxGetDocumentsAction(){}
	
	/**
	 * The action has been activated. The argument of the method represents the 'real' action
	 * sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action){
		
		if (showWaitCursor) {
			
			Runnable longJob = new Runnable() {
				boolean done = false;
				
				public void run(){
					Thread thread = new Thread(new Runnable() {
						public void run(){
							if (!hasValidDocboxCredentials()) {
								return;
							}
							Activator.docboxBackgroundJob.schedule();
							try {
								Activator.docboxBackgroundJob.join();
							} catch (InterruptedException e) {
								ExHandler.handle(e);
							}
							if (UiDesk.getDisplay().isDisposed())
								return;
							done = true;
							UiDesk.getDisplay().wake();
						}
					});
					thread.start();
					while (!done && !UiDesk.getTopShell().isDisposed()) {
						if (!UiDesk.getDisplay().readAndDispatch())
							UiDesk.getDisplay().sleep();
					}
				}
			};
			BusyIndicator.showWhile(UiDesk.getDisplay(), longJob);
		} else {
			Activator.docboxBackgroundJob.schedule();
		}
	}
}