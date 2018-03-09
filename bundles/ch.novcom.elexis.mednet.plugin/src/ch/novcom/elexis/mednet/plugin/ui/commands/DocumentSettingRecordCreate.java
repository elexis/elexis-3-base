/*******************************************************************************
 * Copyright (c) 2018 novcom AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Gutknecht - novcom AG
 *******************************************************************************/
package ch.novcom.elexis.mednet.plugin.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import ch.novcom.elexis.mednet.plugin.ui.dialog.DocumentSettingRecordEditDialog;


public class DocumentSettingRecordCreate extends AbstractHandler {
	public static final String COMMANDID = "ch.novcom.elexis.mednet.plugin.data.documentsettingrecord.create"; //$NON-NLS-1$
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		try {
			// create and open the dialog
			Shell parent = HandlerUtil.getActiveShell(event);
			DocumentSettingRecordEditDialog dialog = new DocumentSettingRecordEditDialog(parent, null);
			dialog.open();
		} catch (Exception ex) {
			throw new RuntimeException(COMMANDID, ex);
		}
		return null;
	}
}