/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 **********************************************************************/
package ch.unibe.iam.scg.archie;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.unibe.iam.scg.archie.messages"; //$NON-NLS-1$
	public static String ACL_ACCESS;
	public static String ACL_ACCESS_DENIED;
	public static String ACTION_EXPORT_DESCRIPTION;
	public static String ACTION_EXPORT_TITLE;
	public static String ACTION_NEWSTAT_DESCRIPTION;
	public static String ACTION_NEWSTAT_ERROR_COULDNT_INIT_VIEW;
	public static String ACTION_NEWSTAT_ERROR_COULDNT_UPDATE_PROVIDER;
	public static String ACTION_NEWSTAT_TITLE;
	public static String ARCHIE_STARTED;
	public static String BUTTON_DATE_SELECT;
	public static String CANCEL;
	public static String CHART_WIZARD_BAR_CHART_BAR_CHART;
	public static String CHART_WIZARD_BAR_CHART_ERROR_ONE_COLUMN;
	public static String CHART_WIZARD_BAR_CHART_LINE_CHART;
	public static String CHART_WIZARD_BAR_CHART_PAGE_DESCRIPTION;
	public static String CHART_WIZARD_BAR_CHART_PAGE_TITLE;
	public static String CHART_WIZARD_BAR_CHART_TEXT_3D;
	public static String CHART_WIZARD_BAR_CHART_TEXT_3D_EXPLANATION;
	public static String CHART_WIZARD_BAR_CHART_TEXT_3D_TOOLTIP;
	public static String CHART_WIZARD_BAR_CHART_TEXT_CHART_TYPE;
	public static String CHART_WIZARD_BAR_CHART_TEXT_COLUMNS_CATEGORIES;
	public static String CHART_WIZARD_BAR_CHART_TEXT_COLUMN_ROW_LABEL;
	public static String CHART_WIZARD_BAR_CHART_TEXT_EXPLANATON;
	public static String CHART_WIZARD_BAR_CHART_TEXT_NAME_CHART;
	public static String CHART_WIZARD_CONTENT_SELECTION_PAGE_DESCRIPTION;
	public static String CHART_WIZARD_CONTENT_SELECTION_PAGE_TITLE;
	public static String CHART_WIZARD_DESCRIPTION;
	public static String CHART_WIZARD_PAGE_DESCRIPTION;
	public static String CHART_WIZARD_PAGE_ERROR_DATASET;
	public static String CHART_WIZARD_PAGE_TEXT_BAR_CHART;
	public static String CHART_WIZARD_PAGE_TEXT_PIE_CHART;
	public static String CHART_WIZARD_PAGE_TITLE;
	public static String CHART_WIZARD_PIE_CHART_PAGE_DESCRIPTION;
	public static String CHART_WIZARD_PIE_CHART_PAGE_ERROR_NUMERIC;
	public static String CHART_WIZARD_PIE_CHART_PAGE_TEXT_KEYS;
	public static String CHART_WIZARD_PIE_CHART_PAGE_TEXT_NAME;
	public static String CHART_WIZARD_PIE_CHART_PAGE_TEXT_VALUES;
	public static String CHART_WIZARD_PIE_CHART_PAGE_TITLE;
	public static String CHART_WIZARD_PIE_CHART_TEXT_3D;
	public static String CHART_WIZARD_PIE_CHART_TEXT_3D_TOOLTIP;
	public static String CHART_WIZARD_TITLE;
	public static String CONSULTATIONS;
	public static String DASHBOARD_CHARTS_NOT_CREATED;
	public static String DASHBOARD_WELCOME;
	public static String EMPTY_PROVIDER_DESCRIPTION;
	public static String ERROR;
	public static String ERROR_DATE_DIFFERENCE;
	public static String ERROR_DATE_FORMAT;
	public static String ERROR_END_DATE_VALID;
	public static String ERROR_FIELDS_NOT_VALID;
	public static String ERROR_FIELDS_NOT_VALID_TITLE;
	public static String ERROR_SET_END_DATE;
	public static String ERROR_SET_START_DATE;
	public static String ERROR_START_DATE_VALID;
	public static String ERROR_WRITING_FILE;
	public static String ERROR_WRITING_FILE_TITLE;
	public static String FEMALE;
	public static String FIELD_GENERAL_ERROR;
	public static String FIELD_GENERAL_ERROR_QUICKFIX;
	public static String FIELD_GENERAL_VALID;
	public static String FIELD_GENERAL_WARNING;
	public static String FIELD_NUMERIC_ERROR;
	public static String FIELD_NUMERIC_QUICKFIX;
	public static String INVOICES;
	public static String MALE;
	public static String NO_CHART_MODEL;
	public static String NO_PLUGIN_SELECTED;
	public static String OPEN;
	public static String OTHER;
	public static String PAID;
	public static String PATIENTS;
	public static String PERSPECTIVE;
	public static String PERSPECTIVE_OPEN;
	public static String RESULT_EMPTY;
	public static String STATISTICS_LIST_TITLE;
	public static String STATISTIC_PARAMETERS_TITLE;
	public static String UNKNOWN;
	public static String WORKING;
	public static String CreateChart;
	public static String RefreshChart;
	public static String PatientenStamm_DatabaseQuery;
	public static String PatientenStamm;

	static { // load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
