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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import ch.elexis.agenda.data.Termin;
import ch.elexis.dialogs.TermineDruckenDialog;

public final class PrintAppointmentLabelHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String appointmentids = event.getParameter("ch.elexis.agenda.param.appointmentids");
		if (StringUtils.isNotBlank(appointmentids)) {
			List<Termin> lTermine = ((List<String>) Arrays.asList(appointmentids.split(",")))
				.stream().filter(id -> StringUtils.isNotBlank(id)).map(id -> Termin.load(id))
				.collect(Collectors.toList());
			if (!lTermine.isEmpty()) {
				new TermineDruckenDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					lTermine.toArray(new Termin[lTermine.size()])).open();
			}
		}
		return null;
	}

}
