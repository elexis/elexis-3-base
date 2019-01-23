/*******************************************************************************
 * Copyright (c) 2018 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.elexis.agenda.commands;

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import ch.elexis.agenda.data.Termin;
import ch.elexis.dialogs.TermineDruckenDialog;

public final class PrintAppointmentLabelHandler extends AbstractHandler {
	private static ArrayList<Termin> lTermine;

	/**
	 * Adds a list of Termine for print processing.
	 * 
	 * @param termine
	 */
	public static void setTermine(ArrayList<Termin> termine) {
		lTermine = termine;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		new TermineDruckenDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				lTermine.toArray(new Termin[0])).open();

		return null;
	}

}
