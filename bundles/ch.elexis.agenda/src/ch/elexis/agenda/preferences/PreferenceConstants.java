/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.agenda.preferences;

import ch.elexis.core.constants.Preferences;

public class PreferenceConstants {
	public static final String AG_BEREICHE = Preferences.AG_BEREICHE; // $NON-NLS-1$

	public static final String AG_BEREICH_PREFIX = Preferences.AG_BEREICH_PREFIX;
	public static final String AG_BEREICH_TYPE_POSTFIX = Preferences.AG_BEREICH_TYPE_POSTFIX;

	public static final String AG_TERMINTYPEN = "agenda/TerminTypen"; //$NON-NLS-1$
	public static final String AG_TERMINSTATUS = "agenda/TerminStatus"; //$NON-NLS-1$
	public static final String AG_SHOWDELETED = "agenda/zeige_geloeschte"; //$NON-NLS-1$
	// public static final String AG_USERS = "agenda/anwender";
	public static final String AG_TYPCOLOR_PREFIX = "agenda/farben/typ/"; //$NON-NLS-1$
	public static final String AG_STATCOLOR_PREFIX = "agenda/farben/status/"; //$NON-NLS-1$
	public static final String AG_TYPIMAGE_PREFIX = "agenda/bilder/typ/"; //$NON-NLS-1$
	public static final String AG_TIMEPREFERENCES = "agenda/zeitvorgaben"; //$NON-NLS-1$
	public static final String AG_DAYPREFERENCES = Preferences.AG_DAYPREFERENCES;
	public static final String AG_SHOW_REASON = "agenda/show_reason"; //$NON-NLS-1$
	public static final String AG_BEREICH = "agenda/bereich"; //$NON-NLS-1$
	public static final String AG_BIG_SAVE_COLUMNWIDTH = "agenda/big/savecolumnwidth";
	public static final String AG_BIG_COLUMNWIDTH = "agenda/big/columnwidth";

	public static final String AG_DAY_PRESENTATION_STARTS_AT = "agenda/beginnStundeTagesdarstellung";
	public static final String AG_DAY_PRESENTATION_ENDS_AT = "agenda/endStundeTagesdarstellung";

	public static final String AG_PIXEL_PER_MINUTE = "agenda/proportional/pixelperminute"; //$NON-NLS-1$
	public static final String AG_RESOURCESTOSHOW = "agenda/proportional/bereichezeigen"; //$NON-NLS-1$
	public static final String AG_DAYSTOSHOW = "agenda/wochenanzeige/tagezeigen"; //$NON-NLS-1$

	public static final String AG_SYNC_TYPE = "agenda/sync/db_type"; //$NON-NLS-1$
	public static final String AG_SYNC_HOST = "agenda/sync/db_host"; //$NON-NLS-1$
	public static final String AG_SYNC_CONNECTOR = "agenda/sync/db_connect"; //$NON-NLS-1$
	public static final String AG_SYNC_DBUSER = "agenda/sync/db_user"; //$NON-NLS-1$
	public static final String AG_SYNC_DBPWD = "agenda/sync/db_pwd"; //$NON-NLS-1$
	public static final String AG_SYNC_MAPPING = "agenda/sync/mapping"; //$NON-NLS-1$
	public static final String AG_SYNC_ENABLED = "agenda/sync/enabled"; //$NON-NLS-1$

	public static final String AG_PRINT_APPOINTMENTCARD_TEMPLATE = "agenda/print/appointmentcard_template"; //$NON-NLS-1$
	public static final String AG_PRINT_APPOINTMENTCARD_TEMPLATE_DEFAULT = Messages.PreferenceConstants_appointmentCard;
	public static final String AG_PRINT_APPOINTMENTCARD_PRINTER_NAME = "agenda/print/appointmentcard_printer_name"; //$NON-NLS-1$
	public static final String AG_PRINT_APPOINTMENTCARD_PRINTER_TRAY = "agenda/print/appointmentcard_printer_tray"; //$NON-NLS-1$
	public static final String AG_PRINT_APPOINTMENTCARD_DIRECTPRINT = "agenda/print/appointmentcard_directprint"; //$NON-NLS-1$
	public static final boolean AG_PRINT_APPOINTMENTCARD_DIRECTPRINT_DEFAULT = false;

	public static final String AG_AVOID_PATIENT_DOUBLE_BOOKING = "agenda/patient/doppelteTerminvergabeVermeiden";
	public static final boolean AG_AVOID_PATIENT_DOUBLE_BOOKING_DEFAULT = false;
	public static final String PREF_DEFAULT_MAIL_ACCOUNT_APPOINTMENT = "agenda/appointment";
	public static final String PREF_DEFAULT_MAIL_ACCOUNT_APPOINTMENT_TEMPLATE = "agenda/appointmenttemplate";
}
