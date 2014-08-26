/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.impfplan.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class VaccinationHeaderHandler extends AbstractHandler {
	
	public static final String COMMAND_ID = "at.medevit.elexis.impfplan.ui.view.vaccinationHeader";
	public static final String HEADER_TYPE_PARAM = "at.medevit.elexis.impfplan.ui.view.vaccinationHeader.headerType";
	
	public static final String HEADER_WITH_ADMINISTERED_VACCINES = "HWAV";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		String parameter = event.getParameter(HEADER_TYPE_PARAM);
		return null;
	}
	
}
