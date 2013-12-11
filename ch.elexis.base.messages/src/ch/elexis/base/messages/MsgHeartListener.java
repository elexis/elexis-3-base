/*******************************************************************************
 * Copyright (c) 2007-2008, G. Weirich and Elexis
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

import java.util.List;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Query;
import ch.elexis.messages.Message;

public class MsgHeartListener implements HeartListener {
	boolean bSkip;
	
	public void heartbeat(){
		if (!bSkip) {
			if (CoreHub.actUser != null) {
				Query<Message> qbe = new Query<Message>(Message.class);
				qbe.add("to", "=", CoreHub.actUser.getId()); //$NON-NLS-1$ //$NON-NLS-2$
				final List<Message> res = qbe.execute();
				if (res.size() > 0) {
					UiDesk.getDisplay().asyncExec(new Runnable() {
						public void run(){
							bSkip = true;
							new MsgDetailDialog(Hub.getActiveShell(), res.get(0)).open();
							bSkip = false;
						}
					});
					
				}
			}
		}
	}
}
