/*******************************************************************************
 * Copyright (c) 2007-2011, MEDEVIT, MEDELEXIS and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    M. Descher - Initial implementation
 *
 *******************************************************************************/
package ch.elexis.agenda;

import java.util.LinkedList;
import java.util.List;

import ch.elexis.actions.Activator;
import ch.elexis.actions.IBereichSelectionEvent;

public class BereichSelectionHandler {

	static List<IBereichSelectionEvent> bereichSelectionEventListener = null;
	private static Activator agenda = Activator.getDefault();

	public static void addBereichSelectionListener(IBereichSelectionEvent listener) {
		if (bereichSelectionEventListener == null) {
			bereichSelectionEventListener = new LinkedList<IBereichSelectionEvent>();
		}
		bereichSelectionEventListener.add(listener);
	}

	public static void removeBarcodeEventListener(IBereichSelectionEvent listener) {
		bereichSelectionEventListener.remove(listener);
	}

	public static void updateListeners() {
		if (bereichSelectionEventListener == null)
			return;
		for (IBereichSelectionEvent listener : bereichSelectionEventListener) {
			String actResource = agenda.getActResource();
			listener.bereichSelectionEvent(actResource);
		}
	}

}
