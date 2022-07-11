/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.inbox.core.elements;

import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.model.IInboxElementsProvider;

public class CoreElements implements IInboxElementsProvider {

	private static LabResultCreateEventHandler labResultCreateEventHandler;

	@Override
	public void activate() {
		// add inbox creation of LabResult
		if (labResultCreateEventHandler != null) {
			labResultCreateEventHandler.setActive(true);
		} else {
			LoggerFactory.getLogger(getClass()).warn("No create event handler to activate"); //$NON-NLS-1$
		}
	}

	@Override
	public void deactivate() {
		if (labResultCreateEventHandler != null) {
			labResultCreateEventHandler.setActive(false);
		} else {
			LoggerFactory.getLogger(getClass()).warn("No create event handler to deactivate"); //$NON-NLS-1$
		}
	}

	public static void setLabResultCreateEventHandler(LabResultCreateEventHandler labResultCreateEventHandler) {
		CoreElements.labResultCreateEventHandler = labResultCreateEventHandler;
	}
}
