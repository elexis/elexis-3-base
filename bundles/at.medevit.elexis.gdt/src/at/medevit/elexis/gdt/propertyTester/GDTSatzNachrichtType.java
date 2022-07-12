/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.propertyTester;

import org.eclipse.core.expressions.PropertyTester;

import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.data.GDTProtokoll;

public class GDTSatzNachrichtType extends PropertyTester {

	public GDTSatzNachrichtType() {
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		GDTProtokoll gdtEntry = (GDTProtokoll) receiver;
		String messageType = gdtEntry.getMessageType();
		int messageTypeInt = Integer.parseInt(messageType);

		if (property.equalsIgnoreCase("isGDTSatznachricht6310") //$NON-NLS-1$
				&& messageTypeInt == GDTConstants.SATZART_DATEN_EINER_UNTERSUCHUNG_UEBERMITTELN)
			return true;

		return false;
	}

}
