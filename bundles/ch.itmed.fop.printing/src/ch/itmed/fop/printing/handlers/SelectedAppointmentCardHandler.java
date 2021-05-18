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

import javax.inject.Inject;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.ui.util.CoreUiUtil;
import ch.elexis.data.PersistentObject;

@SuppressWarnings("restriction")
public final class SelectedAppointmentCardHandler extends AbstractHandler {
	
	@Inject
	private ECommandService commandService;
	
	@Inject
	private EHandlerService handlerService;
	
	public SelectedAppointmentCardHandler(){
		CoreUiUtil.injectServices(this);
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// transfer selection to elexis event dispatcher
		IStructuredSelection currentSelection = HandlerUtil.getCurrentStructuredSelection(event);
		if (!currentSelection.isEmpty()) {
			ElexisEventDispatcher.fireSelectionEvent(getAsPersistentObject(currentSelection));
		}
		// call default command
		ParameterizedCommand cmd =
			commandService.createCommand("ch.itmed.fop.printing.command.AppointmentCardPrint",
				null);
		if (cmd != null) {
			handlerService.executeHandler(cmd);
		}
		return null;
	}
	
	private PersistentObject getAsPersistentObject(IStructuredSelection currentSelection){
		PersistentObject ret = null;
		if (currentSelection.getFirstElement() instanceof PersistentObject) {
			ret = (PersistentObject) currentSelection.getFirstElement();
		} else if (currentSelection.getFirstElement() instanceof Identifiable) {
			ret =
				NoPoUtil.loadAsPersistentObject((Identifiable) currentSelection.getFirstElement());
		}
		return ret;
	}
}