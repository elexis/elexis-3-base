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
 *******************************************************************************/

package ch.elexis.base.messages;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import ch.elexis.core.ui.UiDesk;

public class MessageDelegate implements IWorkbenchWindowActionDelegate {

	public void dispose() {
	}

	public void init(final IWorkbenchWindow window) {
	}

	public void run(final IAction action) {
		new MsgDetailDialog(UiDesk.getTopShell(), null).open();
	}

	public void selectionChanged(final IAction action, final ISelection selection) {
	}

}
