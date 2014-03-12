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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.dialogs.SelectPerspectiveDialog;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.eclipse.ui.internal.registry.PerspectiveRegistry;

import de.fhdo.elexis.Messages;

/**
 * Deletes a selected perspectives from the preference store.
 * 
 * This class pops up a dialog to select a perspective from the preference store to be deleted.
 * 
 * @author Bernhard Rimatzki, Thorsten Wagner, Pascal Proksch, Sven Lüttmann
 * @version 1.0
 * 
 */

public class DeleteHandler extends AbstractHandler implements IHandler {
	
	@Override
	@SuppressWarnings("all")
	public Object execute(ExecutionEvent event) throws ExecutionException{
		
		IWorkbenchWindow mainWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		PerspectiveRegistry perspRegistry =
			(PerspectiveRegistry) WorkbenchPlugin.getDefault().getPerspectiveRegistry();
		
		//
		// Open the dialog to select a stored perspective to be deleted
		// If 'Cancel' is pressed return
		//
		SelectPerspectiveDialog selectionDialog =
			new SelectPerspectiveDialog(mainWindow.getShell(), perspRegistry);
		
		if (selectionDialog.open() == SelectPerspectiveDialog.CANCEL)
			return null;
		
		//
		// Ask if the user really wants to delete the selected perspective
		//
		if (!MessageDialog.openQuestion(mainWindow.getShell(), Messages.DeleteHandler_ReallyDelete,
			String.format(Messages.DeleteHandler_Really_Want_To_Delete_selected_Perspective,
				selectionDialog.getSelection().getLabel())))
			return null;
		
		//
		// Get the selected perspective description
		//
		PerspectiveDescriptor pDesc = (PerspectiveDescriptor) selectionDialog.getSelection();
		
		//
		// Delete the selected perspective from the preference store
		//
		perspRegistry.deletePerspective(pDesc);
		
		//
		// If the perspective could not be deleted it is still present in the
		// preference store and thus we can check for it
		//
		PerspectiveDescriptor pd2 =
			(PerspectiveDescriptor) perspRegistry.findPerspectiveWithLabel(pDesc.getLabel());
		
		if (pd2 != null)
			MessageDialog.openInformation(mainWindow.getShell(),
				Messages.DeleteHandler_ErrorWhileDeleting,
				Messages.DeleteHandler_CannotDeleteInternalPerspective);
		
		return null;
	}
	
}
