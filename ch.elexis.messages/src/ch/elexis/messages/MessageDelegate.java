/*******************************************************************************
 * Copyright (c) 2007-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 * $Id: MessageDelegate.java 4937 2009-01-13 17:47:02Z rgw_ch $
 *******************************************************************************/

package ch.elexis.messages;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import ch.elexis.core.ui.Hub;

public class MessageDelegate implements IWorkbenchWindowActionDelegate {
	
	public void dispose(){
		// TODO Auto-generated method stub
	}
	
	public void init(final IWorkbenchWindow window){
		// TODO Auto-generated method stub
	}
	
	public void run(final IAction action){
		new MsgDetailDialog(Hub.getActiveShell(), null).open();
		
	}
	
	public void selectionChanged(final IAction action, final ISelection selection){
		// TODO Auto-generated method stub
	}
	
}
