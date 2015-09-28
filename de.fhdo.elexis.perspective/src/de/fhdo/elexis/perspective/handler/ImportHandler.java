/*******************************************************************************
 * Copyright (c) 2011, fhdo and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bernhard Rimatzki, Thorsten Wagner, Pascal Proksch, Sven Lüttmann
		- initial implementation
 *    
 *******************************************************************************/
package de.fhdo.elexis.perspective.handler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.eclipse.ui.internal.registry.PerspectiveRegistry;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

import de.fhdo.elexis.Messages;

/**
 * Imports selected perspectives from given .xml files
 * 
 * This class pops up a FileDialog to select one or more stored perspectives to be restored An error
 * correction routine is provided if perspectives with the same name are tried to restore
 * 
 * @author Bernhard Rimatzki, Thorsten Wagner, Pascal Proksch, Sven Lüttmann
 * @version 1.0
 * 
 */

public class ImportHandler extends AbstractHandler implements IHandler {
	
	@Override
	@SuppressWarnings("all")
	public Object execute(ExecutionEvent event) throws ExecutionException{
		
		IWorkbenchWindow mainWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		PerspectiveRegistry perspRegistry =
			(PerspectiveRegistry) WorkbenchPlugin.getDefault().getPerspectiveRegistry();
		String importMessage = ""; //$NON-NLS-1$
		
		//
		// Open a FileDialog to select the .xml files with stored perspectives
		// Only display .xml Files to select
		//
		FileDialog diag = new FileDialog(mainWindow.getShell(), SWT.MULTI);
		
		String[] filterNames = {
			"XML"};//$NON-NLS-1$
		String[] filterExtensions = {
			"*.xml"};//$NON-NLS-1$
		
		diag.setFilterNames(filterNames);
		diag.setFilterExtensions(filterExtensions);
		
		if (diag.open() == null)
			return null;
		
		//
		// Since it is possible to select multiple perspectives to be restored we have to iterate
		// over the selected files
		//
		for (String file : diag.getFileNames()) {
			String filename = diag.getFilterPath() + File.separator + file;
			FileReader reader;
			XMLMemento memento = null;
			
			try {
				reader = new FileReader(new File(filename));
				memento = XMLMemento.createReadRoot(reader);
				PerspectiveDescriptor newPersp = new PerspectiveDescriptor(null, null, null);
				
				//
				// Get the label and the ID of the stored perspective
				//
				String label = memento.getChild("descriptor").getString("label"); //$NON-NLS-1$ //$NON-NLS-2$
				String id = memento.getChild("descriptor").getString("id"); //$NON-NLS-1$ //$NON-NLS-2$
				
				//
				// Find the perspective by label within the preference store
				//
				PerspectiveDescriptor pd =
					(PerspectiveDescriptor) perspRegistry.findPerspectiveWithLabel(label);
				
				String[] buttonLabels =
					{
						Messages.ImportHandler_Abort, Messages.ImportHandler_Overwrite,
						Messages.ImportHandler_Rename
					};
				
				while (pd != null) {
					
					//
					// If pd != null the perspective is already present in the preference store
					// though we have to store it with a different name
					//
					String notDeleted = "";//$NON-NLS-1$
					String dialogMessage =
						String.format(Messages.ImportHandler_Name_Import_Already_Exists, label);
					MessageDialog mesDiag =
						new MessageDialog(mainWindow.getShell(),
							Messages.ImportHandler_OverWrite_Perspective, null, dialogMessage, 0,
							buttonLabels, 0);
					int ergMesDiag = mesDiag.open();
					
					if (ergMesDiag == 0) // Cancel was pressed
						return null;
					else if (ergMesDiag == 1) // Overwrite was pressed
					{
						perspRegistry.deletePerspective(pd);
						PerspectiveDescriptor pd2 =
							(PerspectiveDescriptor) perspRegistry.findPerspectiveWithLabel(label);
						
						//
						// If the perspective could not be deleted, the user have to choose another
						// name
						//
						if (pd2 != null) {
							notDeleted = Messages.ImportHandler_Cannot_Overwrite_Perspective;
							ergMesDiag = 2;
						}
						
						//
						// After the Perspective has been deleted the descriptor has to be null
						//
						pd = null;
					}
					
					if (ergMesDiag == 2) // Rename was pressed
					{
						
						String dialogMessageOverride =
							notDeleted + Messages.ImportHandler_Choose_new_name_for_Perspective;
						;
						InputDialog inputDiag =
							new InputDialog(mainWindow.getShell(),
								Messages.ImportHandler_Rename_Perspective, dialogMessageOverride,
								null, null);
						
						inputDiag.open();
						
						String[] idsplit = id.split("\\.");//$NON-NLS-1$
						System.out.println("ID: " + idsplit.length);//$NON-NLS-1$
						id = "";//$NON-NLS-1$
						label = inputDiag.getValue();
						
						for (int i = 0; i < idsplit.length - 1; i++) {
							id += idsplit[i] + ".";//$NON-NLS-1$
						}
						
						id += label;
						
						//
						// Create a new perspective with the new name
						//
						newPersp = new PerspectiveDescriptor(id, label, pd);
						
						pd = (PerspectiveDescriptor) perspRegistry.findPerspectiveWithLabel(label);
					}
				}
				
				memento.getChild("descriptor").putString("label", label); //$NON-NLS-1$ //$NON-NLS-2$
				memento.getChild("descriptor").putString("id", id);//$NON-NLS-1$ //$NON-NLS-2$
				
				newPersp.restoreState(memento);
				
				reader.close();
				
				//
				// Save the new generated perspective in the preference store
				//
				perspRegistry.saveCustomPersp(newPersp, memento);
				
				importMessage +=
					String.format(Messages.ImportHandler_Saved_As, file, newPersp.getLabel());
				
			} catch (WorkbenchException e) {
				unableToLoadPerspective(e.getStatus());
			} catch (IOException e) {
				unableToLoadPerspective(null);
			}
		}
		
		MessageDialog.openInformation(mainWindow.getShell(),
			Messages.ImportHandler_Successfully_Imported,
			Messages.ImportHandler_Imported_perspectives_successfully + importMessage);
		
		return null;
	}
	
	private void unableToLoadPerspective(IStatus status){
		String msg = Messages.ImportHandler_Unable_to_load_Perspective;
		
		if (status == null) {
			IStatus errStatus = new Status(IStatus.ERROR, WorkbenchPlugin.PI_WORKBENCH, msg);
			StatusManager.getManager().handle(errStatus, StatusManager.SHOW | StatusManager.LOG);
		} else {
			StatusAdapter adapter = new StatusAdapter(status);
			adapter.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, msg);
			StatusManager.getManager().handle(adapter, StatusManager.SHOW | StatusManager.LOG);
		}
	}
}
