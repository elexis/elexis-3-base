package ch.elexis.mednet.webapi.ui.handler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import ch.elexis.mednet.webapi.ui.util.UIStyleTableHelper;

/**
 * Helper class for populating, sorting, and filtering SWT Tables.
 */
public class TableHelper {

	private static boolean ascending = true;

	private static final String UNKNOWN_DATE = "Unknown Date"; //$NON-NLS-1$
	private static final String UNKNOWN_BIRTHDATE = "Unknown Birthdate"; //$NON-NLS-1$
	private static final String INVALID_DATE = "Invalid Date"; //$NON-NLS-1$

	/**
	 * Fills a table with data from a list of maps. * @param table the table to fill
	 * 
	 * @param list          the data source
	 * @param display       the display to use for styling
	 * @param keys          the keys to extract from the maps
	 * @param defaultValues the default values if a key is missing or null
	 */
	@SuppressWarnings("unchecked")
	public static void fillTableFromList(Table table, List<Map<String, Object>> list, Display display, String[] keys,
			String[] defaultValues) {
		for (Map<String, Object> map : list) {
			String[] values = new String[keys.length];

			for (int j = 0; j < keys.length; j++) {
				Object value = map.get(keys[j]);
				if (value == null) {
					value = defaultValues[j];
				}

				if (keys[j].contains(".")) { //$NON-NLS-1$
					String[] keyParts = keys[j].split("\\."); //$NON-NLS-1$
					Map<String, Object> nestedObject = (Map<String, Object>) map.get(keyParts[0]);
					if (nestedObject != null) {
						Object nestedValue = nestedObject.get(keyParts[1]);
						values[j] = convertToString(nestedValue != null ? nestedValue : defaultValues[j]);
					} else {
						values[j] = defaultValues[j];
					}
				} else if (keys[j].equals("downloadUrl")) { //$NON-NLS-1$
					if (map.containsKey("files")) { //$NON-NLS-1$
						List<Map<String, Object>> files = (List<Map<String, Object>>) map.get("files"); //$NON-NLS-1$
						if (files != null && !files.isEmpty()) {
							String downloadUrl = (String) files.get(0).get("downloadUrl"); //$NON-NLS-1$
							values[j] = (downloadUrl != null) ? extractDateFromUrl(downloadUrl) : defaultValues[j];
						} else {
							values[j] = defaultValues[j];
						}
					} else {
						values[j] = defaultValues[j];
					}
				} else if (keys[j].equals("patientDateOfBirth")) { //$NON-NLS-1$
					String birthDate = convertToString(value);
					values[j] = formatBirthDate(birthDate);
				} else if (keys[j].equals("patientLastName")) { //$NON-NLS-1$
					Object firstNameObj = map.get("patientFirstName"); //$NON-NLS-1$
					String firstName = convertToString(firstNameObj != null ? firstNameObj : defaultValues[j]);
					String lastName = convertToString(value);
					values[j] = lastName + " " + firstName; //$NON-NLS-1$
				} else {
					values[j] = convertToString(value);
				}
			}

			TableItem item = new TableItem(table, 0);
			item.setText(values);

			if (map.containsKey("files")) { //$NON-NLS-1$
				List<Map<String, Object>> files = (List<Map<String, Object>>) map.get("files"); //$NON-NLS-1$
				if (files != null && !files.isEmpty()) {
					Map<String, Object> fileObject = files.get(0);
					String downloadUrl = (String) fileObject.get("downloadUrl"); //$NON-NLS-1$
					if (downloadUrl != null) {
						item.setData("downloadUrl", downloadUrl); //$NON-NLS-1$
					}

					if (fileObject.containsKey("downloadHeaders")) { //$NON-NLS-1$
						List<Map<String, String>> downloadHeadersList = (List<Map<String, String>>) fileObject
								.get("downloadHeaders"); //$NON-NLS-1$
						item.setData("downloadHeaders", downloadHeadersList); //$NON-NLS-1$
					}
				}
			}
			if (map.containsKey("packageId")) { //$NON-NLS-1$
				String packageId = (String) map.get("packageId"); //$NON-NLS-1$
				item.setData("packageId", packageId);
			}

			UIStyleTableHelper.styleTableRows(item, table.indexOf(item), display);
		}
		UIStyleTableHelper.addTableLines(table);
	}

	/**
	 * Safely converts an object to a string representation, handling nulls
	 * correctly. * @param value the object to convert
	 * 
	 * @return the string representation, or an empty string if null
	 */
	private static String convertToString(Object value) {
		if (value == null) {
			return ""; //$NON-NLS-1$
		}
		if (value instanceof Double) {
			return String.valueOf(((Double) value).longValue());
		}
		return value.toString();
	}

	/**
	 * Extracts the date from a download URL. * @param url the download URL
	 * 
	 * @return the formatted date string
	 */
	private static String extractDateFromUrl(String url) {
		if (url == null) {
			return UNKNOWN_DATE;
		}
		String pattern = "(\\d{14})"; //$NON-NLS-1$
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(url);

		if (m.find()) {
			String dateString = m.group(0);
			String year = dateString.substring(0, 4);
			String month = dateString.substring(4, 6);
			String day = dateString.substring(6, 8);
			String hour = dateString.substring(8, 10);
			String minute = dateString.substring(10, 12);

			return day + "." + month + "." + year + " " + hour + ":" + minute; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		} else {
			return UNKNOWN_DATE;
		}
	}

	/**
	 * Formats a birth date string into a standard format. * @param birthDate the
	 * raw birth date string
	 * 
	 * @return the formatted birth date string
	 */
	private static String formatBirthDate(String birthDate) {
		if (birthDate == null || birthDate.isEmpty() || birthDate.equals(UNKNOWN_BIRTHDATE)) {
			return UNKNOWN_BIRTHDATE;
		}
		try {
			if (birthDate.contains("T")) { //$NON-NLS-1$
				birthDate = birthDate.split("T")[0]; //$NON-NLS-1$
			}
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); //$NON-NLS-1$
			LocalDate date = LocalDate.parse(birthDate, inputFormatter);
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); //$NON-NLS-1$
			return date.format(outputFormatter);
		} catch (Exception e) {
			return INVALID_DATE;
		}
	}

	/**
	 * Sorts the table by the values in the specified column index. * @param table
	 * the table to sort
	 * 
	 * @param columnIndex the index of the column to sort by
	 */
	public static void sortTable(Table table, int columnIndex) {
		if (table.isDisposed()) {
			return;
		}

		List<String[]> tableData = new ArrayList<>();
		for (TableItem item : table.getItems()) {
			if (!item.isDisposed()) {
				String[] rowData = new String[table.getColumnCount()];
				for (int i = 0; i < table.getColumnCount(); i++) {
					rowData[i] = item.getText(i);
				}
				tableData.add(rowData);
			}
		}

		if (ascending) {
			tableData.sort(Comparator.comparing(row -> row[columnIndex].toLowerCase()));
		} else {
			tableData.sort((row1, row2) -> row2[columnIndex].toLowerCase().compareTo(row1[columnIndex].toLowerCase()));
		}
		ascending = !ascending;

		table.removeAll();
		Display display = table.getDisplay();
		int rowIndex = 0;
		for (String[] rowData : tableData) {
			TableItem newItem = new TableItem(table, SWT.NONE);
			newItem.setText(rowData);
			UIStyleTableHelper.styleTableRows(newItem, rowIndex, display);
			rowIndex++;
		}
	}

	/**
	 * Filters the table items based on the search text. * @param table the table to
	 * filter
	 * 
	 * @param searchText the text to search for
	 */
	public static void filterTable(Table table, String searchText) {
		if (table.getData("originalData") == null) { //$NON-NLS-1$
			List<String[]> originalData = new ArrayList<>();
			for (TableItem item : table.getItems()) {
				String[] rowData = new String[table.getColumnCount()];
				for (int i = 0; i < table.getColumnCount(); i++) {
					rowData[i] = item.getText(i);
				}
				originalData.add(rowData);
			}
			table.setData("originalData", originalData); //$NON-NLS-1$
		}

		table.removeAll();
		@SuppressWarnings("unchecked")
		List<String[]> originalData = (List<String[]>) table.getData("originalData"); //$NON-NLS-1$

		Display display = table.getDisplay();
		int rowIndex = 0;
		for (String[] rowData : originalData) {
			String name = rowData[1].toLowerCase();
			if (name.contains(searchText)) {
				TableItem newItem = new TableItem(table, SWT.NONE);
				newItem.setText(rowData);
				newItem.setData("rowData", rowData); //$NON-NLS-1$
				UIStyleTableHelper.styleTableRows(newItem, rowIndex, display);
				rowIndex++;
			}
		}
	}
}