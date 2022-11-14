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
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.unibe.iam.scg.archie.samples.i18n.messages"; //$NON-NLS-1$
	public static String CALCULATING;
	public static String USER_OVERVIEW_TITLE;
	public static String USER_OVERVIEW_DESCRIPTION;
	public static String USER_OVERVIEW_USER;
	public static String USER_OVERVIEW_ENTRIES;
	public static String USER_OVERVIEW_BIRTHDAY;
	public static String USER_OVERVIEW_GENDER;
	public static String USER_OVERVIEW_VALID;
	public static String USER_OVERVIEW_GROUPS;
	public static String USER_OVERVIEW_YES;
	public static String USER_OVERVIEW_NO;
	public static String USER_OVERVIEW_UNDEFINED;
	public static String CONSULTATION_STATS_TITLE;
	public static String CONSULTATION_STATS_DESCRIPTION;
	public static String CONSULTATION_STATS_AGE_GROUP;
	public static String CONSULTATION_STATS_TOTAL_COSTS;
	public static String CONSULTATION_STATS_NUMBER_OF_CONSULTATIONS;
	public static String CONSULTATION_STATS_AVERAGE_COSTS;
	public static String CONSULTATION_STATS_TOTAL_PROFITS;
	public static String CONSULTATION_STATS_AVERAGE_PROFITS;
	public static String CONSULTATION_STATS_REGEX_MESSAGE = "";
	public static String CONSULTATION_STATS_COHORT_SIZE_EXCEPTION;
	public static String CONSULTATION_TIME_STATS_TITLE;
	public static String CONSULTATION_TIME_STATS_DESCRIPTION;
	public static String CONSULTATION_TIME_STATS_HEADING_TIME_TOTAL;
	public static String CONSULTATION_TIME_STATS_HEADING_TIME_MAX;
	public static String CONSULTATION_TIME_STATS_HEADING_TIME_AVERAGE;
	public static String CONSULTATION_TIME_STATS_HEADING_INCOME;
	public static String CONSULTATION_TIME_STATS_HEADING_SPENDING;
	public static String CONSULTATION_TIME_STATS_HEADING_PROFIT;
	public static String PATIENTS_PROFITS_TITLE;
	public static String PATIENTS_PROFITS_DESCRIPTION;
	public static String PATIENTS_PROFITS_HEADING_PATIENT;
	public static String PATIENTS_PROFITS_HEADING_COSTS;
	public static String PATIENTS_PROFITS_HEADING_INCOME;
	public static String PATIENTS_PROFITS_HEADING_PROFIT;
	public static String PRESCRIPTIONS_OVERVIEW_TITLE;
	public static String PRESCRIPTIONS_OVERVIEW_DESCRIPTION;
	public static String PRESCRIPTIONS_OVERVIEW_HEADING_NAME;
	public static String PRESCRIPTIONS_OVERVIEW_HEADING_COUNT;
	public static String PRESCRIPTIONS_OVERVIEW_HEADING_AVG_TIME;
	public static String SERVICES_TITLE;
	public static String SERVICES_DESCRIPTION;
	public static String SERVICES_HEADING_CODESYSTEM;
	public static String SERVICES_HEADING_SERVICE;
	public static String SERVICES_HEADING_AMOUNT;
	public static String SERVICES_HEADING_COSTS;
	public static String SERVICES_HEADING_INCOME;
	public static String SERVICES_HEADING_PROFITS;
	public static String DIAGNOSES_TITLE;
	public static String DIAGNOSES_DESCRIPTION;
	public static String DIAGNOSES_HEADING_DIAGNOSE;
	public static String DIAGNOSES_HEADING_COUNT;
	public static String DIAGNOSES_HEADING_AGE_MIN;
	public static String DIAGNOSES_HEADING_AGE_MAX;
	public static String DIAGNOSES_HEADING_AGE_AVG;
	public static String DIAGNOSES_HEADING_AGE_MED;

	static { // load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
