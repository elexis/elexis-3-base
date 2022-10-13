/*******************************************************************************
 * Copyright (c) 2019 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.fop.printing.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.PersistentObject;
import ch.itmed.fop.printing.resources.Messages;

public final class SelectedAppointmentCardHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
		// transfer selection to elexis event dispatcher
		IStructuredSelection currentSelection = HandlerUtil.getCurrentStructuredSelection(event);
		if (!currentSelection.isEmpty()) {
			ElexisEventDispatcher.fireSelectionEvent(getAsPersistentObject(currentSelection));
		}
		// call default command
		try {
			handlerService.executeCommand("ch.itmed.fop.printing.command.AppointmentCardPrint", null); //$NON-NLS-1$
		} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
			SWTHelper.showError(Messages.DefaultError_Title, Messages.DefaultError_Message);
			LoggerFactory.getLogger(getClass()).error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	private PersistentObject getAsPersistentObject(IStructuredSelection currentSelection) {
		PersistentObject ret = null;
		if (currentSelection.getFirstElement() instanceof PersistentObject) {
			ret = (PersistentObject) currentSelection.getFirstElement();
		} else if (currentSelection.getFirstElement() instanceof Identifiable) {
			ret = NoPoUtil.loadAsPersistentObject((Identifiable) currentSelection.getFirstElement());
		}
		return ret;
	}
}