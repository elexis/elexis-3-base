/*******************************************************************************
 * Copyright (c) 2008 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *******************************************************************************/
package ch.unibe.iam.scg.archie.samples.i18n;

import org.eclipse.osgi.util.NLS;

/**
 * <p>Message class. Used for i18n.</p>
 * 
 * $Id: Messages.java 700 2008-12-22 09:22:22Z hephster $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 700 $
 */
public final class Messages{

    public static String CALCULATING = ch.elexis.core.l10n.Messages.CALCULATING;
    public static String USER_OVERVIEW_TITLE = ch.elexis.core.l10n.Messages.USER_OVERVIEW_TITLE;
    public static String USER_OVERVIEW_DESCRIPTION = ch.elexis.core.l10n.Messages.USER_OVERVIEW_DESCRIPTION;
    public static String USER_OVERVIEW_USER = ch.elexis.core.l10n.Messages.Benutzer;
    public static String USER_OVERVIEW_ENTRIES = ch.elexis.core.l10n.Messages.USER_OVERVIEW_ENTRIES;
    public static String USER_OVERVIEW_BIRTHDAY = ch.elexis.core.l10n.Messages.USER_OVERVIEW_BIRTHDAY;
    public static String USER_OVERVIEW_GENDER = ch.elexis.core.l10n.Messages.USER_OVERVIEW_GENDER;
    public static String USER_OVERVIEW_VALID = ch.elexis.core.l10n.Messages.USER_OVERVIEW_VALID;
    public static String USER_OVERVIEW_GROUPS = ch.elexis.core.l10n.Messages.Core_Groups;
    public static String USER_OVERVIEW_YES = ch.elexis.core.l10n.Messages.Core_Yes;
    public static String USER_OVERVIEW_NO = ch.elexis.core.l10n.Messages.Corr_No;
    public static String USER_OVERVIEW_UNDEFINED = ch.elexis.core.l10n.Messages.USER_OVERVIEW_UNDEFINED;
    public static String CONSULTATION_STATS_TITLE = ch.elexis.core.l10n.Messages.Core_Consultations;
    public static String CONSULTATION_STATS_DESCRIPTION = ch.elexis.core.l10n.Messages.CONSULTATION_STATS_DESCRIPTION;
    public static String CONSULTATION_STATS_AGE_GROUP = ch.elexis.core.l10n.Messages.CONSULTATION_STATS_AGE_GROUP;
    public static String CONSULTATION_STATS_TOTAL_COSTS = ch.elexis.core.l10n.Messages.CONSULTATION_STATS_TOTAL_COSTS;
    public static String CONSULTATION_STATS_NUMBER_OF_CONSULTATIONS = ch.elexis.core.l10n.Messages.Core_Consultations;
    public static String CONSULTATION_STATS_AVERAGE_COSTS = ch.elexis.core.l10n.Messages.CONSULTATION_STATS_AVERAGE_COSTS;
    public static String CONSULTATION_STATS_TOTAL_PROFITS = ch.elexis.core.l10n.Messages.CONSULTATION_STATS_TOTAL_PROFITS;
    public static String CONSULTATION_STATS_AVERAGE_PROFITS = ch.elexis.core.l10n.Messages.CONSULTATION_STATS_AVERAGE_PROFITS;
	public static String CONSULTATION_STATS_REGEX_MESSAGE = "";
    public static String CONSULTATION_STATS_COHORT_SIZE_EXCEPTION = ch.elexis.core.l10n.Messages.CONSULTATION_STATS_COHORT_SIZE_EXCEPTION;
    public static String CONSULTATION_TIME_STATS_TITLE = ch.elexis.core.l10n.Messages.CONSULTATION_TIME_STATS_TITLE;
    public static String CONSULTATION_TIME_STATS_DESCRIPTION = ch.elexis.core.l10n.Messages.CONSULTATION_TIME_STATS_DESCRIPTION;
    public static String CONSULTATION_TIME_STATS_HEADING_TIME_TOTAL = ch.elexis.core.l10n.Messages.CONSULTATION_TIME_STATS_HEADING_TIME_TOTAL;
    public static String CONSULTATION_TIME_STATS_HEADING_TIME_MAX = ch.elexis.core.l10n.Messages.CONSULTATION_TIME_STATS_HEADING_TIME_MAX;
    public static String CONSULTATION_TIME_STATS_HEADING_TIME_AVERAGE = ch.elexis.core.l10n.Messages.CONSULTATION_TIME_STATS_HEADING_TIME_AVERAGE;
    public static String CONSULTATION_TIME_STATS_HEADING_INCOME = ch.elexis.core.l10n.Messages.Core_Total_Revenue;
    public static String CONSULTATION_TIME_STATS_HEADING_SPENDING = ch.elexis.core.l10n.Messages.Core_Costs;
    public static String CONSULTATION_TIME_STATS_HEADING_PROFIT = ch.elexis.core.l10n.Messages.Core_Profit;
    public static String PATIENTS_PROFITS_TITLE = ch.elexis.core.l10n.Messages.PATIENTS_PROFITS_TITLE;
    public static String PATIENTS_PROFITS_DESCRIPTION = ch.elexis.core.l10n.Messages.PATIENTS_PROFITS_DESCRIPTION;
    public static String PATIENTS_PROFITS_HEADING_PATIENT = ch.elexis.core.l10n.Messages.Core_Patients;
    public static String PATIENTS_PROFITS_HEADING_COSTS = ch.elexis.core.l10n.Messages.Core_Costs;
    public static String PATIENTS_PROFITS_HEADING_INCOME = ch.elexis.core.l10n.Messages.Core_Total_Revenue;
    public static String PATIENTS_PROFITS_HEADING_PROFIT = ch.elexis.core.l10n.Messages.Core_Profit;
    public static String PRESCRIPTIONS_OVERVIEW_TITLE = ch.elexis.core.l10n.Messages.PRESCRIPTIONS_OVERVIEW_TITLE;
    public static String PRESCRIPTIONS_OVERVIEW_DESCRIPTION = ch.elexis.core.l10n.Messages.PRESCRIPTIONS_OVERVIEW_DESCRIPTION;
    public static String PRESCRIPTIONS_OVERVIEW_HEADING_NAME = ch.elexis.core.l10n.Messages.Core_Name;
    public static String PRESCRIPTIONS_OVERVIEW_HEADING_COUNT = ch.elexis.core.l10n.Messages.PRESCRIPTIONS_OVERVIEW_HEADING_COUNT;
    public static String PRESCRIPTIONS_OVERVIEW_HEADING_AVG_TIME = ch.elexis.core.l10n.Messages.PRESCRIPTIONS_OVERVIEW_HEADING_AVG_TIME;
    public static String SERVICES_TITLE = ch.elexis.core.l10n.Messages.Core_Services;
    public static String SERVICES_DESCRIPTION = ch.elexis.core.l10n.Messages.SERVICES_DESCRIPTION;
    public static String SERVICES_HEADING_CODESYSTEM = ch.elexis.core.l10n.Messages.Core_Code_System;
    public static String SERVICES_HEADING_SERVICE = ch.elexis.core.l10n.Messages.SERVICES_HEADING_SERVICE;
    public static String SERVICES_HEADING_AMOUNT = ch.elexis.core.l10n.Messages.SERVICES_HEADING_AMOUNT;
    public static String SERVICES_HEADING_COSTS = ch.elexis.core.l10n.Messages.Core_Costs;
    public static String SERVICES_HEADING_INCOME = ch.elexis.core.l10n.Messages.Core_Total_Revenue;
    public static String SERVICES_HEADING_PROFITS = ch.elexis.core.l10n.Messages.SERVICES_HEADING_PROFITS;
    public static String DIAGNOSES_TITLE = ch.elexis.core.l10n.Messages.Core_Diagnosis;
    public static String DIAGNOSES_DESCRIPTION = ch.elexis.core.l10n.Messages.DIAGNOSES_DESCRIPTION;
    public static String DIAGNOSES_HEADING_DIAGNOSE = ch.elexis.core.l10n.Messages.DIAGNOSES_HEADING_DIAGNOSE;
    public static String DIAGNOSES_HEADING_COUNT = ch.elexis.core.l10n.Messages.DIAGNOSES_HEADING_COUNT;
    public static String DIAGNOSES_HEADING_AGE_MIN = ch.elexis.core.l10n.Messages.DIAGNOSES_HEADING_AGE_MIN;
    public static String DIAGNOSES_HEADING_AGE_MAX = ch.elexis.core.l10n.Messages.DIAGNOSES_HEADING_AGE_MAX;
    public static String DIAGNOSES_HEADING_AGE_AVG = ch.elexis.core.l10n.Messages.DIAGNOSES_HEADING_AGE_AVG;
    public static String DIAGNOSES_HEADING_AGE_MED = ch.elexis.core.l10n.Messages.DIAGNOSES_HEADING_AGE_MED;

}
