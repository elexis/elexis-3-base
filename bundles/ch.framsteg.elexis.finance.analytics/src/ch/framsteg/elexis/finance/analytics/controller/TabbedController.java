/*******************************************************************************
 * Copyright 2024 Framsteg GmbH / olivier.debenath@framsteg.ch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ch.framsteg.elexis.finance.analytics.controller;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import ch.elexis.core.constants.ElexisSystemPropertyConstants;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IMandator;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.JdbcLink.Stm;

public class TabbedController {

	private final static String DATE_DATE_PICKER_FORMAT = "tabbed.controller.date.date.picker.format";
	private final static String DATE_DATABASE_FORMAT = "tabbed.controller.date.database.format";

	private final static String MATERIALIZED_VIEW_0_CREATE = "tabbed.controller.materialized.view.0.create";
	private final static String MATERIALIZED_VIEW_0_DETECT = "tabbed.controller.materialized.view.0.count";
	private final static String MATERIALIZED_VIEW_0_REFRESH = "tabbed.controller.materialized.view.0.refresh";
	private final static String MATERIALIZED_VIEW_0_GRANT = "tabbed.controller.materialized.view.0.grant";

	private final static String QUERY_SALES_PER_SERVICE_ALL = "tabbed.controller.sales.per.service.query.0";
	private final static String QUERY_SALES_PER_SERVICE_BEFORE = "tabbed.controller.sales.per.service.query.1";
	private final static String QUERY_SALES_PER_SERVICE_AFTER = "tabbed.controller.sales.per.service.query.2";
	private final static String QUERY_SALES_PER_SERVICE_BETWEEN = "tabbed.controller.sales.per.service.query.3";
	private final static String QUERY_SALES_PER_SERVICE_YEAR_ALL = "tabbed.controller.sales.per.service.year.query.0";
	private final static String QUERY_SALES_PER_SERVICE_YEAR_BEFORE = "tabbed.controller.sales.per.service.year.query.1";
	private final static String QUERY_SALES_PER_SERVICE_YEAR_AFTER = "tabbed.controller.sales.per.service.year.query.2";
	private final static String QUERY_SALES_PER_SERVICE_YEAR_BETWEEN = "tabbed.controller.sales.per.service.year.query.3";
	private final static String QUERY_SALES_PER_SERVICE_YEAR_MONTH_ALL = "tabbed.controller.sales.per.service.year.month.query.0";
	private final static String QUERY_SALES_PER_SERVICE_YEAR_MONTH_BEFORE = "tabbed.controller.sales.per.service.year.month.query.1";
	private final static String QUERY_SALES_PER_SERVICE_YEAR_MONTH_AFTER = "tabbed.controller.sales.per.service.year.month.query.2";
	private final static String QUERY_SALES_PER_SERVICE_YEAR_MONTH_BETWEEN = "tabbed.controller.sales.per.service.year.month.query.3";
	private final static String QUERY_SALES_PER_YEAR_ALL = "tabbed.controller.sales.per.year.query.0";
	private final static String QUERY_SALES_PER_YEAR_BEFORE = "tabbed.controller.sales.per.year.query.1";
	private final static String QUERY_SALES_PER_YEAR_AFTER = "tabbed.controller.sales.per.year.query.2";
	private final static String QUERY_SALES_PER_YEAR_BETWEEN = "tabbed.controller.sales.per.year.query.3";
	private final static String QUERY_SALES_PER_YEAR_MONTH_ALL = "tabbed.controller.sales.per.year.month.query.0";
	private final static String QUERY_SALES_PER_YEAR_MONTH_BEFORE = "tabbed.controller.sales.per.year.month.query.1";
	private final static String QUERY_SALES_PER_YEAR_MONTH_AFTER = "tabbed.controller.sales.per.year.month.query.2";
	private final static String QUERY_SALES_PER_YEAR_MONTH_BETWEEN = "tabbed.controller.sales.per.year.month.query.3";

	private final static String QUERY_TARMED_PER_YEAR_MONTH_ALL = "tabbed.controller.tarmed.per.year.month.query.0";
	private final static String QUERY_TARMED_PER_YEAR_MONTH_BEFORE = "tabbed.controller.tarmed.per.year.month.query.1";
	private final static String QUERY_TARMED_PER_YEAR_MONTH_AFTER = "tabbed.controller.tarmed.per.year.month.query.2";
	private final static String QUERY_TARMED_PER_YEAR_MONTH_BETWEEN = "tabbed.controller.tarmed.per.year.month.query.3";

	private final static String QUERY_MEDICAL_PER_YEAR_MONTH_ALL = "tabbed.controller.medical.per.year.month.query.0";
	private final static String QUERY_MEDICAL_PER_YEAR_MONTH_BEFORE = "tabbed.controller.medical.per.year.month.query.1";
	private final static String QUERY_MEDICAL_PER_YEAR_MONTH_AFTER = "tabbed.controller.medical.per.year.month.query.2";
	private final static String QUERY_MEDICAL_PER_YEAR_MONTH_BETWEEN = "tabbed.controller.medical.per.year.month.query.3";

	private final static String QUERY_DAILY_REPORT_ALL = "tabbed.controller.daily.report.query.0";
	private final static String QUERY_DAILY_REPORT_BEFORE = "tabbed.controller.daily.report.query.1";
	private final static String QUERY_DAILY_REPORT_AFTER = "tabbed.controller.daily.report.query.2";
	private final static String QUERY_DAILY_REPORT_BETWEEN = "tabbed.controller.daily.report.query.3";

	private Properties applicationProperties;
	private Properties sqlProperties;

	public TabbedController(Properties applicationProperties, Properties sqlProperties) {
		setApplicationProperties(applicationProperties);
		setSqlProperties(sqlProperties);
	}

	public boolean isDatabaseSupported() {
		return PersistentObject.getDefaultConnection().getDBFlavor().equalsIgnoreCase("postgresql");
	}

	private void checkMaterializedViews() {

		IMandator currentMandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
		String mandantId = currentMandant.getId();
		try {
			String queryString = new String();
			queryString = MessageFormat.format(getSqlProperties().getProperty(MATERIALIZED_VIEW_0_DETECT),
					mandantId.toLowerCase());
			Stm statement = PersistentObject.getDefaultConnection().getStatement();
			ResultSet resultSet = null;
			resultSet = statement.query(queryString);
			if (resultSet.next()) {
				int count = resultSet.getInt(1);
				// Create Materialized View if not existing
				if (count == 0) {
					String createSql = MessageFormat.format(getSqlProperties().getProperty(MATERIALIZED_VIEW_0_CREATE),
							mandantId, mandantId);
					String dbuser = System.getProperty(ElexisSystemPropertyConstants.CONN_DB_USERNAME);
					String grantSql = MessageFormat.format(getSqlProperties().getProperty(MATERIALIZED_VIEW_0_GRANT),
							mandantId, dbuser);
					statement.exec(createSql);
					statement.exec(grantSql);
					// Refresh Materialized View if existing
				} else {
					String refreshSql = MessageFormat
							.format(getSqlProperties().getProperty(MATERIALIZED_VIEW_0_REFRESH), mandantId);
					statement.exec(refreshSql);
				}
			}
			resultSet.close();
			PersistentObject.getDefaultConnection().releaseStatement(statement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String[]> getSalesTotalPerService(String from, String to) {
		checkMaterializedViews();
		from = from.isEmpty() ? null : from;
		to = to.isEmpty() ? null : to;
		String queryString = new String();
		IMandator currentMandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
		String mandantId = currentMandant.getId();
		SimpleDateFormat datePickerFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_DATE_PICKER_FORMAT));
		SimpleDateFormat databaseFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_DATABASE_FORMAT));
		ResultSet resultSet = null;
		ArrayList<String[]> lines = new ArrayList<String[]>();
		try {
			Date dateFrom = null;
			Date dateTo = null;
			String dateFromString = null;
			String dateToString = null;
			if (from == null && to == null) {
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_ALL),
						mandantId);
			} else if (from == null && to != null) {
				if (!to.isEmpty()) {
					dateTo = datePickerFormat.parse(to);
					dateToString = databaseFormat.format(dateTo);
					queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_BEFORE),
							mandantId, dateToString);
				}
			} else if (from != null && to == null) {
				if (!from.isEmpty()) {
					dateFrom = datePickerFormat.parse(from);
					dateFromString = databaseFormat.format(dateFrom);
					queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_AFTER),
							mandantId, dateFromString);
				}
			} else if (from != null && to != null) {
				if (!from.isEmpty() && !to.isEmpty()) {
					dateFrom = datePickerFormat.parse(from);
					dateTo = datePickerFormat.parse(to);
					dateFromString = databaseFormat.format(dateFrom);
					dateToString = databaseFormat.format(dateTo);
					queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_BETWEEN),
							mandantId, dateToString, dateFromString);
				}
			}
			Stm statement = PersistentObject.getDefaultConnection().getStatement();
			resultSet = statement.query(queryString);

			while (resultSet.next()) {
				String[] line = new String[] { resultSet.getString(1), resultSet.getString(2) };
				lines.add(line);
			}
			resultSet.close();
			PersistentObject.getDefaultConnection().releaseStatement(statement);
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public ArrayList<String[]> getSalesTotalPerServiceYear(String from, String to) {
		checkMaterializedViews();
		from = from.isEmpty() || from == null ? null : from;
		to = to.isEmpty() || from == null ? null : to;
		String queryString = new String();
		IMandator currentMandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
		String mandantId = currentMandant.getId();
		SimpleDateFormat datePickerFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_DATE_PICKER_FORMAT));
		SimpleDateFormat databaseFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_DATABASE_FORMAT));
		ResultSet resultSet = null;
		ArrayList<String[]> lines = new ArrayList<String[]>();
		try {
			Date dateFrom = null;
			Date dateTo = null;
			String dateFromString = null;
			String dateToString = null;
			if (from == null && to == null) {
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_YEAR_ALL),
						mandantId);
			} else if (from == null && to != null) {
				if (!to.isEmpty()) {
					dateTo = datePickerFormat.parse(to);
					dateToString = databaseFormat.format(dateTo);
					queryString = MessageFormat.format(
							getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_YEAR_BEFORE), mandantId,
							dateToString);
				}
			} else if (from != null && to == null) {
				if (!from.isEmpty()) {
					dateFrom = datePickerFormat.parse(from);
					dateFromString = databaseFormat.format(dateFrom);
					queryString = MessageFormat.format(
							getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_YEAR_AFTER), mandantId,
							dateFromString);
				}
			} else if (from != null && to != null) {
				if (!from.isEmpty() && !to.isEmpty()) {
					dateFrom = datePickerFormat.parse(from);
					dateTo = datePickerFormat.parse(to);
					dateFromString = databaseFormat.format(dateFrom);
					dateToString = databaseFormat.format(dateTo);
					queryString = MessageFormat.format(// connection.close();
							getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_YEAR_BETWEEN), mandantId,
							dateToString, dateFromString);
				}
			}
			Stm statement = PersistentObject.getDefaultConnection().getStatement();
			resultSet = statement.query(queryString);

			while (resultSet.next()) {
				String[] line = new String[] { resultSet.getString(2), resultSet.getString(1), resultSet.getString(3) };
				lines.add(line);
			}
			resultSet.close();
			PersistentObject.getDefaultConnection().releaseStatement(statement);
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public ArrayList<String[]> getSalesTotalPerServiceYearMonth(String from, String to) {
		checkMaterializedViews();
		from = from.isEmpty() ? null : from;
		to = to.isEmpty() ? null : to;
		IMandator currentMandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
		String mandantId = currentMandant.getId();
		ArrayList<String[]> lines = new ArrayList<String[]>();
		String queryString = new String();
		SimpleDateFormat datePickerFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_DATE_PICKER_FORMAT));
		SimpleDateFormat databaseFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_DATABASE_FORMAT));
		ResultSet resultSet = null;
		try {
			Date dateFrom = null;
			Date dateTo = null;
			String dateFromString = null;
			String dateToString = null;
			if (from == null && to == null) {
				queryString = MessageFormat
						.format(getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_YEAR_MONTH_ALL), mandantId);
			} else if (from == null && to != null) {
				if (!to.isEmpty()) {
					dateTo = datePickerFormat.parse(to);
					dateToString = databaseFormat.format(dateTo);
					queryString = MessageFormat.format(
							getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_YEAR_MONTH_BEFORE), mandantId,
							dateToString);
				}
			} else if (from != null && to == null) {
				if (!from.isEmpty()) {
					dateFrom = datePickerFormat.parse(from);
					dateFromString = databaseFormat.format(dateFrom);
					queryString = MessageFormat.format(
							getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_YEAR_MONTH_AFTER), mandantId,
							dateFromString);
				}
			} else if (from != null && to != null) {
				if (!from.isEmpty() && !to.isEmpty()) {
					dateFrom = datePickerFormat.parse(from);
					dateTo = datePickerFormat.parse(to);
					dateFromString = databaseFormat.format(dateFrom);
					dateToString = databaseFormat.format(dateTo);
					queryString = MessageFormat.format(
							getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_YEAR_MONTH_BETWEEN), mandantId,
							dateToString, dateFromString);
				}
			}
			Stm statement = PersistentObject.getDefaultConnection().getStatement();
			resultSet = statement.query(queryString);
			while (resultSet.next()) {
				String[] line = new String[] { resultSet.getString(2), resultSet.getString(3), resultSet.getString(1),
						resultSet.getString(4) };
				lines.add(line);
			}
			resultSet.close();
			PersistentObject.getDefaultConnection().releaseStatement(statement);
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public ArrayList<String[]> getSalesTotalPerYear(String from, String to) {
		checkMaterializedViews();
		from = from.isEmpty() ? null : from;
		to = to.isEmpty() ? null : to;
		IMandator currentMandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
		String mandantId = currentMandant.getId();
		ArrayList<String[]> lines = new ArrayList<String[]>();
		String queryString = new String();
		SimpleDateFormat datePickerFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_DATE_PICKER_FORMAT));
		SimpleDateFormat databaseFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_DATABASE_FORMAT));
		ResultSet resultSet = null;
		try {
			Date dateFrom = null;
			Date dateTo = null;
			String dateFromString = null;
			String dateToString = null;
			if (from == null && to == null) {
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_YEAR_ALL), mandantId);
			} else if (from == null && to != null) {
				if (!to.isEmpty()) {
					dateTo = datePickerFormat.parse(to);
					dateToString = databaseFormat.format(dateTo);
					queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_YEAR_BEFORE),
							mandantId, dateToString);
				}
			} else if (from != null && to == null) {
				if (!from.isEmpty()) {
					dateFrom = datePickerFormat.parse(from);
					dateFromString = databaseFormat.format(dateFrom);
					queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_YEAR_AFTER),
							mandantId, dateFromString);
				}
			} else if (from != null && to != null) {
				if (!from.isEmpty() && !to.isEmpty()) {
					dateFrom = datePickerFormat.parse(from);
					dateTo = datePickerFormat.parse(to);
					dateFromString = databaseFormat.format(dateFrom);
					dateToString = databaseFormat.format(dateTo);
					queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_YEAR_BETWEEN),
							mandantId, dateToString, dateFromString);
				}
			}
			Stm statement = PersistentObject.getDefaultConnection().getStatement();
			resultSet = statement.query(queryString);
			while (resultSet.next()) {
				String[] line = new String[] { resultSet.getString(1), resultSet.getString(2) };
				lines.add(line);
			}
			resultSet.close();
			PersistentObject.getDefaultConnection().releaseStatement(statement);
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public ArrayList<String[]> getSalesTotalPerYearMonth(String from, String to) {
		checkMaterializedViews();
		from = from.isEmpty() ? null : from;
		to = to.isEmpty() ? null : to;
		IMandator currentMandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
		String mandantId = currentMandant.getId();
		ArrayList<String[]> lines = new ArrayList<String[]>();
		String queryString = new String();
		SimpleDateFormat datePickerFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_DATE_PICKER_FORMAT));
		SimpleDateFormat databaseFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_DATABASE_FORMAT));
		ResultSet resultSet = null;
		try {
			Date dateFrom = null;
			Date dateTo = null;
			String dateFromString = null;
			String dateToString = null;
			if (from == null && to == null) {
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_YEAR_MONTH_ALL),
						mandantId);
			} else if (from == null && to != null) {
				if (!to.isEmpty()) {
					dateTo = datePickerFormat.parse(to);
					dateToString = databaseFormat.format(dateTo);
					queryString = MessageFormat.format(
							getSqlProperties().getProperty(QUERY_SALES_PER_YEAR_MONTH_BEFORE), mandantId, dateToString);
				}
			} else if (from != null && to == null) {
				if (!from.isEmpty()) {
					dateFrom = datePickerFormat.parse(from);
					dateFromString = databaseFormat.format(dateFrom);
					queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_YEAR_MONTH_AFTER),
							mandantId, dateFromString);
				}
			} else if (from != null && to != null) {
				if (!from.isEmpty() && !to.isEmpty()) {
					dateFrom = datePickerFormat.parse(from);
					dateTo = datePickerFormat.parse(to);
					dateFromString = databaseFormat.format(dateFrom);
					dateToString = databaseFormat.format(dateTo);
					queryString = MessageFormat.format(
							getSqlProperties().getProperty(QUERY_SALES_PER_YEAR_MONTH_BETWEEN), mandantId, dateToString,
							dateFromString);
				}
			}
			Stm statement = PersistentObject.getDefaultConnection().getStatement();
			resultSet = statement.query(queryString);
			while (resultSet.next()) {
				String[] line = new String[] { resultSet.getString(1), resultSet.getString(2), resultSet.getString(3) };
				lines.add(line);
			}
			resultSet.close();
			PersistentObject.getDefaultConnection().releaseStatement(statement);
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public ArrayList<String[]> getTarmedPerYearMonth(String from, String to) {
		checkMaterializedViews();
		from = from.isEmpty() ? null : from;
		to = to.isEmpty() ? null : to;
		IMandator currentMandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
		String mandantId = currentMandant.getId();
		ArrayList<String[]> lines = new ArrayList<String[]>();
		String queryString = new String();
		SimpleDateFormat datePickerFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_DATE_PICKER_FORMAT));
		SimpleDateFormat databaseFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_DATABASE_FORMAT));
		ResultSet resultSet = null;
		try {
			Date dateFrom = null;
			Date dateTo = null;
			String dateFromString = null;
			String dateToString = null;
			if (from == null && to == null) {
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_TARMED_PER_YEAR_MONTH_ALL),
						mandantId, "Tarmed");
			} else if (from == null && to != null) {
				if (!to.isEmpty()) {
					dateTo = datePickerFormat.parse(to);
					dateToString = databaseFormat.format(dateTo);
					queryString = MessageFormat.format(
							getSqlProperties().getProperty(QUERY_TARMED_PER_YEAR_MONTH_BEFORE), mandantId, dateToString,
							"Tarmed");
				}
			} else if (from != null && to == null) {
				if (!from.isEmpty()) {
					dateFrom = datePickerFormat.parse(from);
					dateFromString = databaseFormat.format(dateFrom);
					queryString = MessageFormat.format(
							getSqlProperties().getProperty(QUERY_TARMED_PER_YEAR_MONTH_AFTER), mandantId,
							dateFromString, "Tarmed");
				}
			} else if (from != null && to != null) {
				if (!from.isEmpty() && !to.isEmpty()) {
					dateFrom = datePickerFormat.parse(from);
					dateTo = datePickerFormat.parse(to);
					dateFromString = databaseFormat.format(dateFrom);
					dateToString = databaseFormat.format(dateTo);
					queryString = MessageFormat.format(
							getSqlProperties().getProperty(QUERY_TARMED_PER_YEAR_MONTH_BETWEEN), mandantId,
							dateToString, dateFromString, "Tarmed");
				}
			}
			Stm statement = PersistentObject.getDefaultConnection().getStatement();
			resultSet = statement.query(queryString);
			while (resultSet.next()) {
				String[] line = new String[] { resultSet.getString(1), resultSet.getString(2), resultSet.getString(3) };
				lines.add(line);
			}
			resultSet.close();
			PersistentObject.getDefaultConnection().releaseStatement(statement);
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public ArrayList<String[]> getMedicalPerYearMonth(String from, String to) {
		checkMaterializedViews();
		from = from.isEmpty() ? null : from;
		to = to.isEmpty() ? null : to;
		IMandator currentMandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
		String mandantId = currentMandant.getId();
		ArrayList<String[]> lines = new ArrayList<String[]>();
		String queryString = new String();
		SimpleDateFormat datePickerFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_DATE_PICKER_FORMAT));
		SimpleDateFormat databaseFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_DATABASE_FORMAT));
		ResultSet resultSet = null;
		try {
			Date dateFrom = null;
			Date dateTo = null;
			String dateFromString = null;
			String dateToString = null;
			if (from == null && to == null) {
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_MEDICAL_PER_YEAR_MONTH_ALL),
						mandantId, "Medikamente");
			} else if (from == null && to != null) {
				if (!to.isEmpty()) {
					dateTo = datePickerFormat.parse(to);
					dateToString = databaseFormat.format(dateTo);
					queryString = MessageFormat.format(
							getSqlProperties().getProperty(QUERY_MEDICAL_PER_YEAR_MONTH_BEFORE), mandantId,
							dateToString, "Medikamente");
				}
			} else if (from != null && to == null) {
				if (!from.isEmpty()) {
					dateFrom = datePickerFormat.parse(from);
					dateFromString = databaseFormat.format(dateFrom);
					queryString = MessageFormat.format(
							getSqlProperties().getProperty(QUERY_MEDICAL_PER_YEAR_MONTH_AFTER), mandantId,
							dateFromString, "Medikamente");
				}
			} else if (from != null && to != null) {
				if (!from.isEmpty() && !to.isEmpty()) {
					dateFrom = datePickerFormat.parse(from);
					dateTo = datePickerFormat.parse(to);
					dateFromString = databaseFormat.format(dateFrom);
					dateToString = databaseFormat.format(dateTo);
					queryString = MessageFormat.format(
							getSqlProperties().getProperty(QUERY_MEDICAL_PER_YEAR_MONTH_BETWEEN), mandantId,
							dateToString, dateFromString, "Medikamente");
				}
			}
			Stm statement = PersistentObject.getDefaultConnection().getStatement();
			resultSet = statement.query(queryString);
			while (resultSet.next()) {
				String[] line = new String[] { resultSet.getString(1), resultSet.getString(2), resultSet.getString(3) };
				lines.add(line);
			}
			resultSet.close();
			PersistentObject.getDefaultConnection().releaseStatement(statement);
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public ArrayList<String[]> getDailyReport(String from, String to) {

		ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
		monitorDialog.open();
		checkMaterializedViews();
		from = from.isEmpty() ? null : from;
		to = to.isEmpty() ? null : to;
		IMandator currentMandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
		String mandantId = currentMandant.getId();
		ArrayList<String[]> lines = new ArrayList<String[]>();

		String queryString = new String();
		SimpleDateFormat datePickerFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_DATE_PICKER_FORMAT));
		SimpleDateFormat databaseFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_DATABASE_FORMAT));

		try {
			Date dateFrom = null;
			Date dateTo = null;
			String dateFromString = null;
			String dateToString = null;
			if (from == null && to == null) {
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_DAILY_REPORT_ALL), mandantId);
			} else if (from == null && to != null) {
				if (!to.isEmpty()) {
					dateTo = datePickerFormat.parse(to);
					dateToString = databaseFormat.format(dateTo);
					queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_DAILY_REPORT_BEFORE),
							mandantId, dateToString);
				}
			} else if (from != null && to == null) {
				if (!from.isEmpty()) {
					dateFrom = datePickerFormat.parse(from);
					dateFromString = databaseFormat.format(dateFrom);
					queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_DAILY_REPORT_AFTER),
							mandantId, dateFromString);
				}
			} else if (from != null && to != null) {
				if (!from.isEmpty() && !to.isEmpty()) {
					dateFrom = datePickerFormat.parse(from);
					dateTo = datePickerFormat.parse(to);
					dateFromString = databaseFormat.format(dateFrom);
					dateToString = databaseFormat.format(dateTo);
					queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_DAILY_REPORT_BETWEEN),
							mandantId, dateToString, dateFromString);
				}
			}
			final String assembledQuery = queryString;
			try {
				monitorDialog.run(true, false, new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

							Stm statement = PersistentObject.getDefaultConnection().getStatement();
							SubMonitor subMonitor1 = subMonitor.split(10);
							ResultSet resultSet = null;
							SubMonitor submsubMonitor2 = subMonitor.split(20);
							submsubMonitor2.setTaskName("Daten abfragen");
							resultSet = statement.query(assembledQuery);
							SubMonitor subMonitor3 = subMonitor.split(30);

							while (resultSet.next()) {

								// String test = trimNumericString(resultSet.getString(7));

								String[] line = new String[] { resultSet.getString(1), resultSet.getString(2),
										resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
										resultSet.getString(6), roundToTwoPlaces(resultSet.getString(7)),
										resultSet.getString(8),
										resultSet.getString(9), resultSet.getString(10),
										toInteger(resultSet.getString(11)),
										Float.toString(resultSet.getFloat(12)), resultSet.getString(13),
										resultSet.getString(14), resultSet.getString(15), resultSet.getString(16),
										resultSet.getString(17), resultSet.getString(18), resultSet.getString(19) };
								lines.add(line);
							}
							SubMonitor subMonitor4 = subMonitor.split(40);
							subMonitor4.setTaskName("Tabelle befüllen...");
							resultSet.close();
							PersistentObject.getDefaultConnection().releaseStatement(statement);

						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				});
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return lines;
	}

	private String roundToTwoPlaces(String inputString) {
		String result = new String();
		Pattern pattern = Pattern.compile("^[-+]?[0-9]+\\.[0-9]+$");
		if (pattern.matcher(inputString).matches()) {
			float f = Float.parseFloat(inputString);
			result = String.format("%.2f", f);
		} else {
			result = inputString;
		}
		return result;
	}

	private String toInteger(String inputString) {
		String result = new String();
		Pattern pattern = Pattern.compile("^[-+]?[0-9]+\\.[0-9]+$");
		if (pattern.matcher(inputString).matches()) {
			result = inputString.substring(0, inputString.lastIndexOf("."));

		} else {
			result = inputString;
		}
		return result;
	}

	public Properties getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	public Properties getSqlProperties() {
		return sqlProperties;
	}

	public void setSqlProperties(Properties sqlProperties) {
		this.sqlProperties = sqlProperties;
	}
}
