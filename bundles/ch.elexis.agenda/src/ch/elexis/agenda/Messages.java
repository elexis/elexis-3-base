/*******************************************************************************
 * Copyright (c) 2006-2010, Gerry Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gerry Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.agenda;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS {

	private static final String BUNDLE_NAME = "ch.elexis.agenda.messages"; //$NON-NLS-1$

	private Messages() {
		// Do not instantiate
	}

	public static String TagesView_showToday;
	public static String TagesView_previousDay;
	public static String TagesView_selectDay;
	public static String TagesView_nextDay;
	public static String TagesView_printDay;
	public static String TagesView_14;
	public static String TagesView_lockPeriod;
	public static String TagesView_changeTermin;
	public static String TagesView_changeThisTermin;
	public static String TagesView_shortenTermin;
	public static String TagesView_enlargeTermin;
	public static String TagesView_newTermin;
	public static String TagesView_newWindow;
	public static String TagesView_createNewTermin;
	public static String TagesView_bereich;
	public static String TagesView_selectBereich;
	public static String TagesView_praxis;

	public static String Termin_appCantBeChanged;
	public static String Termin_appointment_locked;
	public static String Termin_deleted;
	public static String Termin_deleteSeries;
	public static String Termin_extra;
	public static String Termin_freeLockedNormalExtraVisit;
	public static String Termin_no;
	public static String Termin_normal;
	public static String Termin_normalAppointment;
	public static String Termin_plannedAppointment;
	public static String Termin_plannedHereFinishedMissed;
	public static String Termin_range_free;
	public static String Termin_range_locked;
	public static String Termin_thisAppIsPartOfSerie;
	public static String Termin_visit;
	public static String Termin_yes;
	public static String TerminDialog_startTime;
	public static String TerminDialog_duration;
	public static String TerminDialog_endTime;
	public static String TerminDialog_locked;
	public static String TerminDialog_emergency;
	public static String TerminDialog_serie;
	public static String TerminDialog_set;
	public static String TerminDialog_createTermin;
	public static String TerminDialog_change;
	public static String TerminDialog_changeTermin;
	public static String TerminDialog_delete;
	public static String TerminDialog_deleteTermin;
	public static String TerminDialog_find;
	public static String TerminDialog_findTermin;
	public static String TerminDialog_enterPersonalia;
	public static String TerminStatusDialog_terminState;
	public static String TerminStatusDialog_enterState;
	public static String TerminSuchenDialog_findTermin;
	public static String TerminSuchenDialog_enterfind;
	public static String TerminDialog_enterText;
	public static String TerminDialog_enterFreeText;
	public static String TerminDialog_remarks;
	public static String TerminDialog_typeandstate;
	public static String TerminDialog_reason;
	public static String TerminDialog_32;
	public static String TerminDialog_editTermins;
	public static String TerminDialog_collision;
	public static String TerminDialog_termin;
	public static String TerminDialog_noPatSelected;
	public static String TerminDialog_40;
	public static String TerminDialog_Mandator;

	public static String TerminDialog_past;
	public static String TerminDialog_print;
	public static String AgendaFarben_appstateTypes;
	public static String AgendaFarben_appTypes;
	public static String AgendaFarben_colorSettings;
	public static String AgendaDefinitionen_states;
	public static String AgendaImages_imagesForAgenda;
	public static String AgendaDefinitionen_defForAgenda;
	public static String AgendaDefinitionen_enterTypes;
	public static String AgendaDefinitionen_areaTypeLabel;
	public static String AgendaDefinitionen_AvoidPatientDoubleBooking;
	public static String AgendaDefinitionen_CurrentMappings;
	public static String AgendaImages_change;
	public static String AgendaImages_cannotCopy;
	public static String AgendaImages_6;
	public static String AgendaImages_7;
	public static String Tageseinteilung_dayPlanning;
	public static String Tageseinteilung_praxis;
	public static String Tageseinteilung_enterPeriods;
	public static String Tageseinteilung_mo;
	public static String Tageseinteilung_tu;
	public static String Tageseinteilung_we;
	public static String Tageseinteilung_th;
	public static String Tageseinteilung_fr;
	public static String Tageseinteilung_sa;
	public static String Tageseinteilung_so;
	public static String Tageseinteilung_su;
	public static String Zeitvorgaben_timePrefs;
	public static String Zeitvorgaben_praxis;
	public static String Zeitvorgaben_terminTypes;
	public static String AgendaDefinitionen_shortCutsForBer;
	public static String AgendaDefinitionen_shortCutsForBerToUser;
	public static String Tageseinteilung_no_past_Date;

	public static String AgendaAnzeige_options;
	public static String AgendaAnzeige_showReason;
	public static String AgendaAnzeige_saveColumnSize;

	public static String Synchronizer_connctNotSuccessful;
	public static String AgendaActions_unblock;
	public static String AgendaActions_state;
	public static String AgendaActions_deleteDate;
	public static String AgendaGross_newWindow;
	public static String AgendaGross_today;
	public static String BaseAgendaView_dayLimits;
	public static String BaseAgendaView_errorHappendPrinting;
	public static String BaseAgendaView_errorWhileprinting;
	public static String BaseAgendaView_exportAgenda;
	public static String BaseAgendaView_exportAppointsments;
	public static String BaseAgendaView_importAgenda;
	public static String BaseAgendaView_importFromIcal;
	public static String BaseAgendaView_printDayList;
	public static String BaseAgendaView_printFutureAppsOfSelectedPatient;
	public static String BaseAgendaView_printListOfDay;
	public static String BaseAgendaView_printPatAppointments;
	public static String BaseView_dayLimits;

	public static String BaseView_errorHappendPrinting;
	public static String BaseView_errorWhilePrinting;
	public static String BaseView_exportAgenda;
	public static String BaseView_exportAppojntmentsOfMandator;
	public static String BaseView_importAgenda;
	public static String BaseView_importFromICal;
	public static String BaseView_printAppointments;
	public static String BaseView_printAPpointmentsOfSelectedDay;
	public static String BaseView_printDayPaapintments;
	public static String BaseView_printFutureAppointmentsOfSelectedPatient;
	public static String BaseView_showToday;
	public static String BaseView_today;
	public static String BaseView_refresh;
	public static String ICalTransfer_badFileFormat;
	public static String ICalTransfer_couldNotReadFile;
	public static String ICalTransfer_couldNotWriteFile;
	public static String ICalTransfer_csvFIles;
	public static String ICalTransfer_exportAgenda;
	public static String ICalTransfer_exportAgendaMandator;
	public static String ICalTransfer_file;
	public static String ICalTransfer_fileToImport;
	public static String ICalTransfer_from;
	public static String ICalTransfer_iCalFiles;
	public static String ICalTransfer_iCalFiles2;
	public static String ICalTransfer_ICalToAgenda;
	public static String ICalTransfer_importAppointments;
	public static String ICalTransfer_iOError;
	public static String ICalTransfer_mandator;
	public static String ICalTransfer_newAppForAllMandators;
	public static String ICalTransfer_noFileSelected;
	public static String ICalTransfer_noValidICal;
	public static String ICalTransfer_pleaseSelectDateAndMandator;
	public static String ICalTransfer_pleaseSelectIcalFile;
	public static String ICalTransfer_preselectState;
	public static String ICalTransfer_preselectType;
	public static String ICalTransfer_readError;
	public static String ICalTransfer_until;
	public static String MinutesFree;
	public static String Tageseinteilung_lblDaysStartAt_text;
	public static String Tageseinteilung_btnDaysStartAt_text;
	public static String Tageseinteilung_btnCheckButton_text;
	public static String Tageseinteilung_lblHours_text;
	public static String Tageseinteilung_btnCheckButton_text_1;
	public static String Tageseinteilung_lblHours_1_text;
	public static String Tageseinteilung_lblEditValuesFor_text;
	public static String Tageseinteilung_lblChangedValuesAre_text;
	public static String Tageseinteilung_btnNewButton_text;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}