package ch.framsteg.elexis.finance.analytics.controller;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Mandant;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.JdbcLink;

public class TabbedController {

	private final static String DATE_DATE_PICKER_FORMAT = "tabbed.controller.date.date.picker.format";
	private final static String DATE_DATABASE_FORMAT = "tabbed.controller.date.database.format";
	private final static String MATERIALIZED_VIEW_RM = "tabbed.controller.materialized.view.0.rm";
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

	private JdbcLink jdbcLink;
	private Connection connection;
	private Statement statement;

	public TabbedController(Properties applicationProperties, Properties sqlProperties) {
		setApplicationProperties(applicationProperties);
		setSqlProperties(sqlProperties);
	}

	public boolean isDatabaseSupported() {
		boolean valid = false;
		jdbcLink = PersistentObject.getConnection();
		connection = jdbcLink.getKeepAliveConnection();
		try {
			DatabaseMetaData metaData = connection.getMetaData();
			String product_name = metaData.getDatabaseProductName();
			if (product_name.equalsIgnoreCase("postgresql")) {
				valid = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return valid;
	}

	private void checkMaterializedViews() {
		jdbcLink = PersistentObject.getConnection();
		connection = jdbcLink.getKeepAliveConnection();
		Mandant currentMandant = ElexisEventDispatcher.getSelectedMandator();
		String mandantId = currentMandant.getId();
		try {
			String queryString = new String();
			queryString = MessageFormat.format(getSqlProperties().getProperty(MATERIALIZED_VIEW_0_DETECT),
					mandantId.toLowerCase());
			statement = connection.createStatement();
			ResultSet resultSet = null;
			resultSet = statement.executeQuery(queryString);
			if (resultSet.next()) {
				int count = resultSet.getInt(1);
				// Create Materialized View if not existing
				if (count == 0) {
					String createSql = MessageFormat.format(getSqlProperties().getProperty(MATERIALIZED_VIEW_0_CREATE),
							mandantId, mandantId);
					String grantSql = MessageFormat.format(getSqlProperties().getProperty(MATERIALIZED_VIEW_0_GRANT),
							mandantId);
					statement.executeUpdate(createSql);
					statement.executeUpdate(grantSql);
					// Refresh Materialized View if existing
				} else {
					String refreshSql = MessageFormat
							.format(getSqlProperties().getProperty(MATERIALIZED_VIEW_0_REFRESH), mandantId);
					statement.executeUpdate(refreshSql);
				}
			}
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String[]> getSalesTotalPerService(String from, String to) {
		checkMaterializedViews();
		from = from.isEmpty() ? null : from;
		to = to.isEmpty() ? null : to;
		String queryString = new String();
		Mandant currentMandant = ElexisEventDispatcher.getSelectedMandator();
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
			} else if (from == null && !to.isEmpty()) {
				dateTo = datePickerFormat.parse(to);
				dateToString = databaseFormat.format(dateTo);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_BEFORE),
						mandantId, dateToString);
			} else if (!from.isEmpty() && to == null) {
				dateFrom = datePickerFormat.parse(from);
				dateFromString = databaseFormat.format(dateFrom);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_AFTER),
						mandantId, dateFromString);
			} else if (!from.isEmpty() && !to.isEmpty()) {
				dateFrom = datePickerFormat.parse(from);
				dateTo = datePickerFormat.parse(to);
				dateFromString = databaseFormat.format(dateFrom);
				dateToString = databaseFormat.format(dateTo);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_BETWEEN),
						mandantId, dateToString, dateFromString);
			}
			JdbcLink jdbcLink = PersistentObject.getConnection();
			Connection connection = jdbcLink.getKeepAliveConnection();

			Statement statement = connection.createStatement();
			resultSet = statement.executeQuery(queryString);

			String[] headerLine = new String[] {};
			while (resultSet.next()) {
				String[] line = new String[] { resultSet.getString(1), resultSet.getString(2) };
				lines.add(line);
			}
			resultSet.close();
			statement.close();
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
		Mandant currentMandant = ElexisEventDispatcher.getSelectedMandator();
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
			} else if (from == null && !to.isEmpty()) {
				dateTo = datePickerFormat.parse(to);
				dateToString = databaseFormat.format(dateTo);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_YEAR_BEFORE),
						mandantId, dateToString);
			} else if (!from.isEmpty() && to == null) {
				dateFrom = datePickerFormat.parse(from);
				dateFromString = databaseFormat.format(dateFrom);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_YEAR_AFTER),
						mandantId, dateFromString);
			} else if (!from.isEmpty() && !to.isEmpty()) {
				dateFrom = datePickerFormat.parse(from);
				dateTo = datePickerFormat.parse(to);
				dateFromString = databaseFormat.format(dateFrom);
				dateToString = databaseFormat.format(dateTo);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_YEAR_BETWEEN),
						mandantId, dateToString, dateFromString);
			}
			JdbcLink jdbcLink = PersistentObject.getConnection();
			Connection connection = jdbcLink.getKeepAliveConnection();

			Statement statement = connection.createStatement();
			resultSet = statement.executeQuery(queryString);

			String[] headerLine = new String[] {};
			while (resultSet.next()) {
				String[] line = new String[] { resultSet.getString(2), resultSet.getString(1), resultSet.getString(3) };
				lines.add(line);
			}
			resultSet.close();
			statement.close();
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public ArrayList<String[]> getSalesTotalPerServiceYearMonth(String from, String to) {
		checkMaterializedViews();
		from = from.isEmpty() ? null : from;
		to = to.isEmpty() ? null : to;
		Mandant currentMandant = ElexisEventDispatcher.getSelectedMandator();
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
			} else if (from == null && !to.isEmpty()) {
				dateTo = datePickerFormat.parse(to);
				dateToString = databaseFormat.format(dateTo);
				queryString = MessageFormat.format(
						getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_YEAR_MONTH_BEFORE), mandantId,
						dateToString);
			} else if (!from.isEmpty() && to == null) {
				dateFrom = datePickerFormat.parse(from);
				dateFromString = databaseFormat.format(dateFrom);
				queryString = MessageFormat.format(
						getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_YEAR_MONTH_AFTER), mandantId,
						dateFromString);
			} else if (!from.isEmpty() && !to.isEmpty()) {
				dateFrom = datePickerFormat.parse(from);
				dateTo = datePickerFormat.parse(to);
				dateFromString = databaseFormat.format(dateFrom);
				dateToString = databaseFormat.format(dateTo);
				queryString = MessageFormat.format(
						getSqlProperties().getProperty(QUERY_SALES_PER_SERVICE_YEAR_MONTH_BETWEEN), mandantId,
						dateToString, dateFromString);
			}
			JdbcLink jdbcLink = PersistentObject.getConnection();
			Connection connection = jdbcLink.getKeepAliveConnection();

			Statement statement = connection.createStatement();
			resultSet = statement.executeQuery(queryString);

			while (resultSet.next()) {
				String[] line = new String[] { resultSet.getString(2), resultSet.getString(3), resultSet.getString(1),
						resultSet.getString(4) };
				lines.add(line);
			}
			resultSet.close();
			statement.close();
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public ArrayList<String[]> getSalesTotalPerYear(String from, String to) {
		checkMaterializedViews();
		from = from.isEmpty() ? null : from;
		to = to.isEmpty() ? null : to;
		Mandant currentMandant = ElexisEventDispatcher.getSelectedMandator();
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
			} else if (from == null && !to.isEmpty()) {
				dateTo = datePickerFormat.parse(to);
				dateToString = databaseFormat.format(dateTo);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_YEAR_BEFORE),
						mandantId, dateToString);
			} else if (!from.isEmpty() && to == null) {
				dateFrom = datePickerFormat.parse(from);
				dateFromString = databaseFormat.format(dateFrom);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_YEAR_AFTER),
						mandantId, dateFromString);
			} else if (!from.isEmpty() && !to.isEmpty()) {
				dateFrom = datePickerFormat.parse(from);
				dateTo = datePickerFormat.parse(to);
				dateFromString = databaseFormat.format(dateFrom);
				dateToString = databaseFormat.format(dateTo);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_YEAR_BETWEEN),
						mandantId, dateToString, dateFromString);
			}
			JdbcLink jdbcLink = PersistentObject.getConnection();
			Connection connection = jdbcLink.getKeepAliveConnection();

			Statement statement = connection.createStatement();
			resultSet = statement.executeQuery(queryString);

			while (resultSet.next()) {
				String[] line = new String[] { resultSet.getString(1), resultSet.getString(2) };
				lines.add(line);
			}
			resultSet.close();
			statement.close();
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public ArrayList<String[]> getSalesTotalPerYearMonth(String from, String to) {
		checkMaterializedViews();
		from = from.isEmpty() ? null : from;
		to = to.isEmpty() ? null : to;
		Mandant currentMandant = ElexisEventDispatcher.getSelectedMandator();
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
			} else if (from == null && !to.isEmpty()) {
				dateTo = datePickerFormat.parse(to);
				dateToString = databaseFormat.format(dateTo);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_YEAR_MONTH_BEFORE),
						mandantId, dateToString);
			} else if (!from.isEmpty() && to == null) {
				dateFrom = datePickerFormat.parse(from);
				dateFromString = databaseFormat.format(dateFrom);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_YEAR_MONTH_AFTER),
						mandantId, dateFromString);
			} else if (!from.isEmpty() && !to.isEmpty()) {
				dateFrom = datePickerFormat.parse(from);
				dateTo = datePickerFormat.parse(to);
				dateFromString = databaseFormat.format(dateFrom);
				dateToString = databaseFormat.format(dateTo);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_SALES_PER_YEAR_MONTH_BETWEEN),
						mandantId, dateToString, dateFromString);
			}
			JdbcLink jdbcLink = PersistentObject.getConnection();
			Connection connection = jdbcLink.getKeepAliveConnection();

			Statement statement = connection.createStatement();
			resultSet = statement.executeQuery(queryString);

			while (resultSet.next()) {
				String[] line = new String[] { resultSet.getString(1), resultSet.getString(2), resultSet.getString(3) };
				lines.add(line);
			}
			resultSet.close();
			statement.close();
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public ArrayList<String[]> getTarmedPerYearMonth(String from, String to) {
		checkMaterializedViews();
		from = from.isEmpty() ? null : from;
		to = to.isEmpty() ? null : to;
		Mandant currentMandant = ElexisEventDispatcher.getSelectedMandator();
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
			} else if (from == null && !to.isEmpty()) {
				dateTo = datePickerFormat.parse(to);
				dateToString = databaseFormat.format(dateTo);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_TARMED_PER_YEAR_MONTH_BEFORE),
						mandantId, dateToString, "Tarmed");
			} else if (!from.isEmpty() && to == null) {
				dateFrom = datePickerFormat.parse(from);
				dateFromString = databaseFormat.format(dateFrom);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_TARMED_PER_YEAR_MONTH_AFTER),
						mandantId, dateFromString, "Tarmed");
			} else if (!from.isEmpty() && !to.isEmpty()) {
				dateFrom = datePickerFormat.parse(from);
				dateTo = datePickerFormat.parse(to);
				dateFromString = databaseFormat.format(dateFrom);
				dateToString = databaseFormat.format(dateTo);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_TARMED_PER_YEAR_MONTH_BETWEEN),
						mandantId, dateToString, dateFromString, "Tarmed");
			}
			JdbcLink jdbcLink = PersistentObject.getConnection();
			Connection connection = jdbcLink.getKeepAliveConnection();

			Statement statement = connection.createStatement();
			resultSet = statement.executeQuery(queryString);

			while (resultSet.next()) {
				String[] line = new String[] { resultSet.getString(1), resultSet.getString(2), resultSet.getString(3) };
				lines.add(line);
			}
			resultSet.close();
			statement.close();
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public ArrayList<String[]> getMedicalPerYearMonth(String from, String to) {
		checkMaterializedViews();
		from = from.isEmpty() ? null : from;
		to = to.isEmpty() ? null : to;
		Mandant currentMandant = ElexisEventDispatcher.getSelectedMandator();
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
			} else if (from == null && !to.isEmpty()) {
				dateTo = datePickerFormat.parse(to);
				dateToString = databaseFormat.format(dateTo);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_MEDICAL_PER_YEAR_MONTH_BEFORE),
						mandantId, dateToString, "Medikamente");
			} else if (!from.isEmpty() && to == null) {
				dateFrom = datePickerFormat.parse(from);
				dateFromString = databaseFormat.format(dateFrom);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_MEDICAL_PER_YEAR_MONTH_AFTER),
						mandantId, dateFromString, "Medikamente");
			} else if (!from.isEmpty() && !to.isEmpty()) {
				dateFrom = datePickerFormat.parse(from);
				dateTo = datePickerFormat.parse(to);
				dateFromString = databaseFormat.format(dateFrom);
				dateToString = databaseFormat.format(dateTo);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_MEDICAL_PER_YEAR_MONTH_BETWEEN),
						mandantId, dateToString, dateFromString, "Medikamente");
			}
			JdbcLink jdbcLink = PersistentObject.getConnection();
			Connection connection = jdbcLink.getKeepAliveConnection();

			Statement statement = connection.createStatement();
			resultSet = statement.executeQuery(queryString);

			while (resultSet.next()) {
				String[] line = new String[] { resultSet.getString(1), resultSet.getString(2), resultSet.getString(3) };
				lines.add(line);
			}
			resultSet.close();
			statement.close();
			// connection.close();
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public ArrayList<String[]> getDailyReport(String from, String to) {
		checkMaterializedViews();
		from = from.isEmpty() ? null : from;
		to = to.isEmpty() ? null : to;
		Mandant currentMandant = ElexisEventDispatcher.getSelectedMandator();
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
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_DAILY_REPORT_ALL), mandantId);
			} else if (from == null && !to.isEmpty()) {
				dateTo = datePickerFormat.parse(to);
				dateToString = databaseFormat.format(dateTo);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_DAILY_REPORT_BEFORE), mandantId,
						dateToString);
			} else if (!from.isEmpty() && to == null) {
				dateFrom = datePickerFormat.parse(from);
				dateFromString = databaseFormat.format(dateFrom);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_DAILY_REPORT_AFTER), mandantId,
						dateFromString);
			} else if (!from.isEmpty() && !to.isEmpty()) {
				dateFrom = datePickerFormat.parse(from);
				dateTo = datePickerFormat.parse(to);
				dateFromString = databaseFormat.format(dateFrom);
				dateToString = databaseFormat.format(dateTo);
				queryString = MessageFormat.format(getSqlProperties().getProperty(QUERY_DAILY_REPORT_BETWEEN),
						mandantId, dateToString, dateFromString);
			}
			JdbcLink jdbcLink = PersistentObject.getConnection();
			Connection connection = jdbcLink.getKeepAliveConnection();

			Statement statement = connection.createStatement();
			resultSet = statement.executeQuery(queryString);

			while (resultSet.next()) {
				String[] line = new String[] { resultSet.getString(1), resultSet.getString(2), resultSet.getString(3),
						resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getString(7),
						resultSet.getString(8), resultSet.getString(9), resultSet.getString(10),
						resultSet.getString(11), Float.toString(resultSet.getFloat(12)), resultSet.getString(13),
						resultSet.getString(14), resultSet.getString(15), resultSet.getString(16),
						resultSet.getString(17), resultSet.getString(18), resultSet.getString(19) };
				lines.add(line);
			}
			resultSet.close();
			statement.close();
			// connection.close();
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return lines;
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
