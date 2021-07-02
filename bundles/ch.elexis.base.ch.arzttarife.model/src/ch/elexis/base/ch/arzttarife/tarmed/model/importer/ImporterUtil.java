package ch.elexis.base.ch.arzttarife.tarmed.model.importer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.rgw.tools.TimeTool;

public class ImporterUtil {
	private static DateTimeFormatter tarmedFormatter = new DateTimeFormatterBuilder()
			.appendPattern("yyyy-MM-dd hh:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter();

	public static LocalDate getLocalDate(ResultSet res, String name) throws SQLException {
		return LocalDate.parse(res.getString(name), tarmedFormatter);
	}

	public static LocalDate getLocalDate(Map<String, String> map, String name) {
		return LocalDate.parse(map.get(name), tarmedFormatter);
	}

	public static String getAsString(ResultSet res, String field) throws SQLException, IOException {
		String ret = res.getString(field);
		if (ret == null) {
			return "";
		} else {
			return ret;
		}
	}

	/**
	 * Get a List of Maps containing the rows of the ResultSet with a matching valid
	 * date information. This is needed as we can not make constraints on a date
	 * represented as string in the db.
	 * 
	 * @param input
	 * @param validFrom
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getValidValueMaps(ResultSet input, TimeTool validFrom)
			throws SQLException, IOException {
		List<Map<String, String>> ret = new ArrayList<Map<String, String>>();

		// build list of column names
		ArrayList<String> headers = new ArrayList<String>();
		ResultSetMetaData meta = input.getMetaData();
		int metaLength = meta.getColumnCount();
		for (int i = 1; i <= metaLength; i++) {
			headers.add(meta.getColumnName(i));
		}

		TimeTool from = new TimeTool();
		TimeTool to = new TimeTool();

		// find rows with matching valid date information
		while (input.next()) {
			from.set(input.getString("GUELTIG_VON"));
			to.set(input.getString("GUELTIG_BIS")); //$NON-NLS-1$
			// is this the correct result
			if (validFrom.isAfterOrEqual(from) && validFrom.isBeforeOrEqual(to)) {
				HashMap<String, String> valuesMap = new HashMap<String, String>();
				// put all the columns with values into valuesMap
				for (String columnName : headers) {
					String value = input.getString(columnName);
					valuesMap.put(columnName, value);
				}
				// add map to list of matching maps
				ret.add(valuesMap);
			}
		}
		return ret;
	}

	/**
	 * Get a List of Maps containing the rows of the ResultSet with a matching valid
	 * date information. This is needed as we can not make constraints on a date
	 * represented as string in the db.
	 * 
	 * @param input
	 * @param validFrom
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getAllValueMaps(ResultSet input) throws SQLException, IOException {
		List<Map<String, String>> ret = new ArrayList<Map<String, String>>();

		// build list of column names
		ArrayList<String> headers = new ArrayList<String>();
		ResultSetMetaData meta = input.getMetaData();
		int metaLength = meta.getColumnCount();
		for (int i = 1; i <= metaLength; i++) {
			headers.add(meta.getColumnName(i));
		}

		// find rows with matching valid date information
		while (input.next()) {
			HashMap<String, String> valuesMap = new HashMap<String, String>();
			// put all the columns with values into valuesMap
			for (String columnName : headers) {
				String value = input.getString(columnName);
				valuesMap.put(columnName, value);
			}
			// add map to list of matching maps
			ret.add(valuesMap);
		}
		return ret;
	}

	public static Map<String, String> getLatestMap(List<Map<String, String>> list) {
		TimeTool currFrom = new TimeTool("19000101");
		TimeTool from = new TimeTool();
		Map<String, String> ret = null;
		for (Map<String, String> map : list) {
			from.set(map.get("GUELTIG_VON"));
			if (from.isAfter(currFrom)) {
				currFrom.set(from);
				ret = map;
			}
		}
		return ret;
	}

	/**
	 * Put all the keys from the resultSet into the map using the specified keys.
	 * 
	 * @param map
	 * @param resultSet
	 * @param keys
	 * @throws SQLException
	 * @throws Exception
	 */
	public static void putResultSetToMap(final Map<Object, Object> map, final ResultSet resultSet, final String... keys)
			throws SQLException {
		for (String key : keys) {
			String val = resultSet.getString(key);
			if (val != null) {
				map.put(key, val);
			}
		}
	}
}
