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
package org.iatrix.util;

public class Constants {

	// column indices
	public static final int DATUM = 0;
	public static final int NUMMER = 1;
	public static final int BEZEICHNUNG = 2;
	public static final int THERAPIE = 3;
	public static final int DIAGNOSEN = 4;
	public static final int STATUS = 5;

	public static final String[] COLUMN_TEXT = {
		"Datum", // DATUM
		"#", // NUMMER
		"Problem, respektive Diagnose, frühere Therapie", // BEZEICHNUNG
		"Aktuelles Prozedere und Fixmedikation", // THERAPIE
		"Code", // DIAGNOSEN
		"a/inaktiv", // Status Aktiv|Inaktiv
	};

	public static final String[] TOOLTIP_TEXT = {
		"Passen Sie die Breite der einzelnen Kolonnen so an, dass der Platz optimal ausgenutzt wird.\nDie eingestellte Breite geht durch einen Neustart nicht verloren",
		"Datum(+Dauer) des Ereignis. zB.\n2013-12-30\n1999-11\n1988\n1988—2010\n1988--90", // DATUM
		"Nummerierung der Ereignisse nach Kausalität", // NUMMER
		"Bedeutende Symptome, Probleme, Diagnosen gemäss Zunahme Verständnis", // BEZEICHNUNG
		"Freitext\n(Auf Rechtsclick Auswahl von Medikamenten)", // THERAPIE
		"Code für Rechnungsstellung oder Forschung", // DIAGOSEN
		"Problem aktiv oder inaktiv (verlangt keine weitere Behandlungen mehr)", //STATUS
	};

	public static final int[] DEFAULT_COLUMN_WIDTH = {
		80, // DATUM
		30, // NUMMER
		120, // BEZEICHNUNG
		120, // THERAPIE
		80, // DIAGNOSEN
		20 // STATUS
	};

	public static final String CFG_BASE_KEY = "org.iatrix/views/journalview/column_width";

	public static final String[] COLUMN_CFG_KEY = {
		CFG_BASE_KEY + "/" + "date", // DATUM
		CFG_BASE_KEY + "/" + "number", // NUMMER
		CFG_BASE_KEY + "/" + "description", // BEZEICHNUNG
		CFG_BASE_KEY + "/" + "therapy", // THERAPIE
		CFG_BASE_KEY + "/" + "diagnoses", // DIAGNOSEN
		/*
		 * CFG_BASE_KEY + "/" + "law", // GESETZ
		 */

			CFG_BASE_KEY + "/" + "status", // STATUS
	};
	public static final String ID = "org.iatrix.views.JournalView"; //$NON-NLS-1$
	public static final String VIEW_CONTEXT_ID = "org.iatrix.view.context"; //$NON-NLS-1$
	public static final String NEWCONS_COMMAND = "org.iatrix.commands.newcons"; //$NON-NLS-1$
	public static final String NEWPROBLEM_COMMAND = "org.iatrix.commands.newproblem"; //$NON-NLS-1$
	public static final String EXPORT_CLIPBOARD_COMMAND = "org.iatrix.commands.export_clipboard"; //$NON-NLS-1$
	public static final String EXPORT_SEND_EMAIL_COMMAND = "org.iatrix.commands.send_email"; //$NON-NLS-1$
	public static final String UNKNOWN = "(unbekannt)";
	public static final DateComparator DATE_COMPARATOR = new DateComparator();
}
