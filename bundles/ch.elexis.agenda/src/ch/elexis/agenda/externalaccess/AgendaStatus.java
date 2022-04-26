/*******************************************************************************
 * Copyright (c) 2007-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.agenda.externalaccess;

import ch.elexis.actions.Activator;
import ch.rgw.tools.TimeTool;

public class AgendaStatus {

	private static Activator agenda;

	static {
		agenda = Activator.getDefault();
	}

	/**
	 *
	 * @return the currently selected date
	 */
	public static TimeTool getSelectedDate() {
		return agenda.getActDate();
	}

	/**
	 *
	 * @return the currently selected Bereich
	 */
	public static String getSelectedBereich() {
		return agenda.getActResource();
	}

}
