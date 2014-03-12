/*******************************************************************************
 * Copyright (c) 2011, fhdo and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bernhard Rimatzki, Thorsten Wagner, Pascal Proksch, Sven Lüttmann
 *  	- initial implementation
 * Niklaus Giger - show only user defined perspectives. Cleanup    
 *
 *******************************************************************************/
package de.fhdo.elexis.perspective.handler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.dialogs.SelectPerspectiveDialog;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.eclipse.ui.internal.registry.PerspectiveRegistry;
import de.fhdo.elexis.Messages;

/**
 * Export selected perspectives from the preference store to a certain .xml file.
 * 
 * This class pops up a dialog to select a perspective from the preference store to be saved. You
 * got to chose a filename and location for the .xml file where to save the perspective description.
 * 
 * @author Bernhard Rimatzki, Thorsten Wagner, Pascal Proksch, Sven Lüttmann
 * @version 1.0
 * 
 */

public class ExportHandler extends AbstractHandler implements IHandler {
	
	@SuppressWarnings("restriction")
	private class UserPerspectiveRegistry extends PerspectiveRegistry {
		
		public IPerspectiveDescriptor[] getPerspectives(){
			IPerspectiveDescriptor[] descs =
				WorkbenchPlugin.getDefault().getPerspectiveRegistry().getPerspectives();
			List<IPerspectiveDescriptor> perspectives = new ArrayList<IPerspectiveDescriptor>(10);
			for (IPerspectiveDescriptor item : descs) {
				if (item.getDescription() == null)
					perspectives.add(item);
			}
			return (IPerspectiveDescriptor[]) perspectives
				.toArray(new IPerspectiveDescriptor[perspectives.size()]);
			
		}
	}
	
	@Override
	@SuppressWarnings("all")
	public Object execute(ExecutionEvent event) throws ExecutionException{
		
		IWorkbenchWindow mainWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		PerspectiveRegistry perspRegistry = new UserPerspectiveRegistry();
		
		//
		// Open the dialog to select a stored perspective
		// If 'Cancel' is pressed return
		//
		SelectPerspectiveDialog selectionDialog =
			new SelectPerspectiveDialog(mainWindow.getShell(), perspRegistry);
		
		if (selectionDialog.open() == SelectPerspectiveDialog.CANCEL)
			return null;
		
		PerspectiveDescriptor pDesc;
		pDesc = (PerspectiveDescriptor) selectionDialog.getSelection();
		
		XMLMemento mem = null;
		
		try {
			//
			// Try to get the internal memento for the selected perspective
			// If it fails display an error message and return
			//
			mem = (XMLMemento) perspRegistry.getCustomPersp(pDesc.getId());
			
		} catch (WorkbenchException e2) {
			
			MessageDialog.openError(mainWindow.getShell(), Messages.ExportHandler_Error,
				Messages.ExportHandler_Error_Exporting);
			return null;
		} catch (IOException e2) {
			
			MessageDialog.openError(mainWindow.getShell(), Messages.ExportHandler_Error,
				Messages.ExportHandler_Error_Exporting);
			return null;
		}
		
		//
		// Ok now we do have the internal memento and can save this to a certain
		// user selected location and filename
		// Therefore we open a FileDialog and let the user select the filename
		// and the location
		//
		FileDialog diag = new FileDialog(mainWindow.getShell(), SWT.SAVE);
		String filename;
		
		//
		// Only .xml is allowed as file extension so we have to pass this
		// setting to the FileDialog
		//
		String[] filetypes = new String[1];
		filetypes[0] = "*.xml";//$NON-NLS-1$ 
		diag.setFilterExtensions(filetypes);
		
		//
		// If 'filename' is null 'Cancel' was pressed by the user otherwise the
		// variable contains the absolute path and the filename
		//
		if ((filename = diag.open()) == null)
			return null;
		
		File file = new File(filename);
		FileWriter writer = null;
		
		try {
			
			//
			// Let's now save the memento in the selected .xml file
			// If something crashes print out the error message on the screen
			//
			writer = new FileWriter(file);
			mem.save(writer);
		} catch (IOException e1) {
			
			MessageDialog.openError(mainWindow.getShell(), Messages.ExportHandler_ErrorOccured,
				e1.getMessage());
			return null;
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				MessageDialog.openError(mainWindow.getShell(), Messages.ExportHandler_ErrorOccured,
					e.getMessage());
			}
		}
		
		return null;
	}
	
}
