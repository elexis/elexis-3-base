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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.novcom.elexis.mednet.plugin.DocumentImporterPage;

public class GetResult extends AbstractHandler {
	/**
	 * Logger used to log all activities of the module
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(GetResult.class.getName());
	

	public static final String ID = "ch.novcom.elexis.mednet.plugin.ui.commands.getresult";//$NON-NLS-1$
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		
		ProgressMonitorDialog progress =
			new ProgressMonitorDialog(HandlerUtil.getActiveShell(event));
		
		try {
			progress.run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException{

					DocumentImporterPage dip = new DocumentImporterPage();
					try {
						dip.doImport(monitor);
					} catch (Exception e) {
						LOGGER.error("execute() - "+"Exception calling doImport",e);//$NON-NLS-1$
					}
			
				}
			});

		} catch (InvocationTargetException | InterruptedException e) {
			MessageDialog.openError(HandlerUtil.getActiveShell(event), "Fehler",
				"Fehler beim PDF erzeugen.\n" + e.getMessage());
		}
		
		return null;
	}
	

}
