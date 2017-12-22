/*******************************************************************************
 * Copyright (c) 2007-2015, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *     Gerry Weirich - adapted for 2.1
 *     Niklaus Giger - small improvements, split into 20 classes
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.widgets;

import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;

/**
 * A small helper class. Areas may add code to properly update their part. The following callbacks
 * are present activation visible changed patient changed consultation
 *
 * @author Niklaus Giger
 *
 */
public interface IJournalArea {

	/**
	 *
	 * @param mode
	 *            indicates whether view should be become visible
	 */
	public void visible(boolean mode);

	/**
	 *
	 * @param mode.
	 *            True on startup, false on teardown
	 */
	public void activation(boolean mode);

	public enum KonsActions {
		ACTIVATE_KONS,
		EVENT_UPDATE,
		EVENT_RELOAD,
		EVENT_SELECTED,
		SAVE_KONS
	}
	/**
	 * Callback für JournalArea: Aktuelle Konsultation setzen.
	 *
	 * Wenn eine Konsultation gesetzt wird gehört sie zum zuletzt gesetzen Patienten.
	 *
	 * @param newKons
	 *            Neue Konsultation kann leer sein
	 * @param op
	 *           An integer, from JournalActions
	 */
	/**
	 *
	 * @param newPatient
	 * 			 may be. Caller is reponsible that it belongs to the newKons if newKons is not null
	 * @param newKons
	 *           may be null, eg. when a new patient is creted which has no kons.
	 */
	public void setKons(Patient newPatient, Konsultation newKons, KonsActions op);
}
