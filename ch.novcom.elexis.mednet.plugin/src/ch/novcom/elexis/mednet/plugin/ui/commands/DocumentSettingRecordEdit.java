/*******************************************************************************
 * Copyright (c) 2017 novcom AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Gutknecht
 *******************************************************************************/
package ch.novcom.elexis.mednet.plugin.ui.commands;

import java.util.HashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

import ch.elexis.data.PersistentObject;
import ch.novcom.elexis.mednet.plugin.data.DocumentSettingRecord;
import ch.novcom.elexis.mednet.plugin.ui.dialog.DocumentSettingRecordEditDialog;

public class DocumentSettingRecordEdit extends AbstractHandler {
	
	public static final String COMMANDID = "ch.novcom.elexis.mednet.plugin.data.documentsettingrecord.edit";
	public static final String PARAMETERID = "ch.novcom.elexis.mednet.plugin.data.documentsettingrecord.edit.selected";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		try {
			// get the parameter
			String param = event.getParameter(PARAMETERID);
			PersistentObject documentSettingRecord =
				(PersistentObject) event.getCommand().getParameterType(PARAMETERID)
					.getValueConverter().convertToObject(param);
			// create and open the dialog with the parameter
			Shell parent = HandlerUtil.getActiveShell(event);
			DocumentSettingRecordEditDialog dialog = new DocumentSettingRecordEditDialog(parent, (DocumentSettingRecord) documentSettingRecord);
			dialog.open();
		} catch (Exception ex) {
			throw new RuntimeException(COMMANDID, ex);
		}
		return null;
	}
	
	public static void executeWithParams(PersistentObject parameter){
		try {
			// get the command
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			ICommandService cmdService = (ICommandService) window.getService(ICommandService.class);
			Command cmd = cmdService.getCommand(COMMANDID);
			// create the parameter
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put(PARAMETERID, parameter);
			// build the parameterized command
			ParameterizedCommand pc = ParameterizedCommand.generateCommand(cmd, param);
			// execute the command
			IHandlerService handlerService =
				(IHandlerService) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getService(IHandlerService.class);
			handlerService.executeCommand(pc, null);
		} catch (Exception ex) {
			throw new RuntimeException(COMMANDID, ex);
		}
	}
	
}
