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

	/**
	 * Callback für JournalArea: Aktuellen Patienten setzen.
	 *
	 * @param newPatient
	 *            to set may be null
	 */

	public void setPatient(Patient newPatient);

	public enum KonsActions {
		ACTIVATE_KONS,
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
	 * @param newKons
	 *            to set may be null
	 */
	public void setKons(Konsultation newKons, KonsActions op);
}
