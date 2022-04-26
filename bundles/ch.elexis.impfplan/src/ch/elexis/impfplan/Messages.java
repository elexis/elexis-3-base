/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 **********************************************************************/
package ch.elexis.impfplan;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.impfplan.messages";
	public static String AddVaccinationDialog_dateOnlyAbout;
	public static String AddVaccinationDialog_enterVaccinationText;
	public static String AddVaccinationDialog_enterVaccinationTextError;
	public static String AddVaccinationDialog_enterVaccinationTitle;
	public static String EditVaccinationDialog_ageFromTo;
	public static String EditVaccinationDialog_defineVaccination;
	public static String EditVaccinationDialog_distance1_2;
	public static String EditVaccinationDialog_distance2_3;
	public static String EditVaccinationDialog_distance3_4;
	public static String EditVaccinationDialog_distanceRappel;
	public static String EditVaccinationDialog_enterVaccination;
	public static String EditVaccinationDialog_nameOfVaccination;
	public static String EditVaccinationDialog_remarks;
	public static String EditVaccinationDialog_vaccinationSubstance;
	public static String ImpfplanPreferences_addCaption;
	public static String ImpfplanPreferences_nameDummy;
	public static String ImpfplanPreferences_removeVaccWarning;
	public static String ImpfplanPreferences_removeVaccination;
	public static String ImpfplanPreferences_vaccDummy;
	public static String ImpfplanPrinter_printListHeading;
	public static String ImpfplanPrinter_printPlanMessage;
	public static String ImpfplanPrinter_printPlanTitle;
	public static String ImpfplanPrinter_recommendPlaceholder;
	public static String ImpfplanPrinter_templateName;
	public static String ImpfplanPrinter_templatePlaceHolder;
	public static String ImpfplanPrinter_templateType;
	public static String ImpfplanView_dateColumn;
	public static String ImpfplanView_printActionTitle;
	public static String ImpfplanView_printActionTooltip;
	public static String ImpfplanView_removeActionTitle;
	public static String ImpfplanView_removeActionTooltip;
	public static String ImpfplanView_vaccinateActionTitle;
	public static String ImpfplanView_vaccinateActionTooltip;
	public static String ImpfplanView_vaccinationColumn;
	public static String ImpfplanView_vaccinationsDOne;
	public static String ImpfplanView_vaccinationsRecommended;

	static { // load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
