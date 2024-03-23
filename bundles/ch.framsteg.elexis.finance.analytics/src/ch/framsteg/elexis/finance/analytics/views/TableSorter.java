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
package ch.framsteg.elexis.finance.analytics.views;

import java.text.Collator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TableSorter {
	
	private Properties applicationProperties;
	
	private final static String DATE_FORMAT = "date.format.short";
	
	public TableSorter(Properties applicationProperties) {
		setApplicationProperties(applicationProperties);
	}

	public Table sortByStringAsc(Table table, int columnNumber) {
		TableItem[] items = table.getItems();
		Collator collator = Collator.getInstance(Locale.getDefault());
		TableColumn column = table.getColumn(columnNumber);
		int index = columnNumber;
		for (int i = 1; i < items.length; i++) {
			String value1 = items[i].getText(index);
			for (int j = 0; j < i; j++) {
				String value2 = items[j].getText(index);
				if (collator.compare(value1, value2) < 0) {
					int columnCount = table.getColumnCount();
					ArrayList<String> rowValues = new ArrayList<String>();
					for (int x = 0; x < columnCount; x++) {
						rowValues.add(items[i].getText(x));
					}
					items[i].dispose();
					TableItem item = new TableItem(table, SWT.NONE, j);
					String[] values = rowValues.toArray(new String[0]);
					item.setText(values);
					items = table.getItems();
					break;
				}
			}
		}
		table.setSortColumn(column);
		table.deselectAll();
		table.setTopIndex(0);
		table.setLinesVisible(true);
		return table;
	}

	public Table sortByStringDesc(Table table, int columnNumber) {
		TableItem[] items = table.getItems();
		Collator collator = Collator.getInstance(Locale.getDefault());
		TableColumn column = table.getColumn(columnNumber);
		int index = columnNumber;
		for (int i = 1; i < items.length; i++) {
			String value1 = items[i].getText(index);
			for (int j = 0; j < i; j++) {
				String value2 = items[j].getText(index);
				if (collator.compare(value1, value2) > 0) {
					int columnCount = table.getColumnCount();
					ArrayList<String> rowValues = new ArrayList<String>();
					for (int x = 0; x < columnCount; x++) {
						rowValues.add(items[i].getText(x));
					}
					items[i].dispose();
					TableItem item = new TableItem(table, SWT.NONE, j);
					String[] values = rowValues.toArray(new String[0]);
					item.setText(values);
					items = table.getItems();
					break;
				}
			}
		}
		table.setSortColumn(column);
		table.deselectAll();
		table.setTopIndex(0);
		table.setLinesVisible(true);
		return table;
	}

	public Table sortByFloatAsc(Table table, int columnNumber) {
		TableItem[] items = table.getItems();
		Collator.getInstance(Locale.getDefault());
		TableColumn column = table.getColumn(columnNumber);
		int index = columnNumber;
		for (int i = 1; i < items.length; i++) {
			Float value1 = Float.parseFloat(items[i].getText(index));
			for (int j = 0; j < i; j++) {
				Float value2 = Float.parseFloat(items[j].getText(index));
				if (Float.compare(value1, value2) < 0) {
					int columnCount = table.getColumnCount();
					ArrayList<String> rowValues = new ArrayList<String>();
					for (int x = 0; x < columnCount; x++) {
						rowValues.add(items[i].getText(x));
					}
					items[i].dispose();
					TableItem item = new TableItem(table, SWT.NONE, j);
					String[] values = rowValues.toArray(new String[0]);
					item.setText(values);
					items = table.getItems();
					break;
				}
			}
		}
		table.setSortColumn(column);
		table.deselectAll();
		table.setTopIndex(0);
		table.setLinesVisible(true);
		return table;
	}

	public Table sortByFloatDesc(Table table, int columnNumber) {
		TableItem[] items = table.getItems();
		TableColumn column = table.getColumn(columnNumber);
		int index = columnNumber;
		for (int i = 1; i < items.length; i++) {
			Float value1 = Float.parseFloat(items[i].getText(index));
			for (int j = 0; j < i; j++) {
				Float value2 = Float.parseFloat(items[j].getText(index));
				if (Float.compare(value1, value2) > 0) {
					int columnCount = table.getColumnCount();
					ArrayList<String> rowValues = new ArrayList<String>();
					for (int x = 0; x < columnCount; x++) {
						rowValues.add(items[i].getText(x));
					}
					items[i].dispose();
					TableItem item = new TableItem(table, SWT.NONE, j);
					String[] values = rowValues.toArray(new String[0]);
					item.setText(values);
					items = table.getItems();
					break;
				}
			}
		}
		table.setSortColumn(column);
		table.deselectAll();
		table.setTopIndex(0);
		table.setLinesVisible(true);
		return table;
	}
	
	public Table sortByIntegerAsc(Table table, int columnNumber) {

		TableItem[] items = table.getItems();
		TableColumn column = table.getColumn(columnNumber);
		int index = columnNumber;
		for (int i = 1; i < items.length; i++) {
			Integer value1 = Integer.parseInt(items[i].getText(index));
			for (int j = 0; j < i; j++) {
				Integer value2 = Integer.parseInt(items[j].getText(index));
				if (Integer.compare(value1, value2) < 0) {
					int columnCount = table.getColumnCount();
					ArrayList<String> rowValues = new ArrayList<String>();
					for (int x = 0; x < columnCount; x++) {
						rowValues.add(items[i].getText(x));
					}
					items[i].dispose();
					TableItem item = new TableItem(table, SWT.NONE, j);
					String[] values = rowValues.toArray(new String[0]);
					item.setText(values);
					items = table.getItems();
					break;
				}
			}
		}
		table.setSortColumn(column);
		table.deselectAll();
		table.setTopIndex(0);
		table.setLinesVisible(true);
		return table;
	}

	public Table sortByIntegerDesc(Table table, int columnNumber) {
		TableItem[] items = table.getItems();
		TableColumn column = table.getColumn(columnNumber);
		int index = columnNumber;
		for (int i = 1; i < items.length; i++) {
			Integer value1 = Integer.parseInt(items[i].getText(index));
			for (int j = 0; j < i; j++) {
				Integer value2 = Integer.parseInt(items[j].getText(index));
				if (Integer.compare(value1, value2) > 0) {
					int columnCount = table.getColumnCount();
					ArrayList<String> rowValues = new ArrayList<String>();
					for (int x = 0; x < columnCount; x++) {
						rowValues.add(items[i].getText(x));
					}
					items[i].dispose();
					TableItem item = new TableItem(table, SWT.NONE, j);
					String[] values = rowValues.toArray(new String[0]);
					item.setText(values);
					items = table.getItems();
					break;
				}
			}
		}
		table.setSortColumn(column);
		table.deselectAll();
		table.setTopIndex(0);
		table.setLinesVisible(true);
		return table;
	}
	
	public Table sortByDateAsc(Table table, int columnNumber) {

		TableItem[] items = table.getItems();
		TableColumn column = table.getColumn(columnNumber);
		DateFormat sourceFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_FORMAT));
		int index = columnNumber;
		for (int i = 1; i < items.length; i++) {
			String value1 = items[i].getText(index);
			Date date1 = new Date();
			try {
				date1 = sourceFormat.parse(value1);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			for (int j = 0; j < i; j++) {
				String value2 = items[j].getText(index);
				Date date2=null;
				try {
					date2 = sourceFormat.parse(value2);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (date1.compareTo(date2) < 0) {
					int columnCount = table.getColumnCount();
					ArrayList<String> rowValues = new ArrayList<String>();
					for (int x = 0; x < columnCount; x++) {
						rowValues.add(items[i].getText(x));
					}
					items[i].dispose();
					TableItem item = new TableItem(table, SWT.NONE, j);
					String[] values = rowValues.toArray(new String[0]);
					item.setText(values);
					items = table.getItems();
					break;
				}
			}
		}
		table.setSortColumn(column);
		table.deselectAll();
		table.setTopIndex(0);
		table.setLinesVisible(true);
		return table;
	}

	public Table sortByDateDesc(Table table, int columnNumber) {
		TableItem[] items = table.getItems();
		TableColumn column = table.getColumn(columnNumber);
		DateFormat sourceFormat = new SimpleDateFormat(
				getApplicationProperties().getProperty(DATE_FORMAT));
		int index = columnNumber;
		for (int i = 1; i < items.length; i++) {
			String value1 = items[i].getText(index);
			Date date1 = new Date();
			try {
				date1 = sourceFormat.parse(value1);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			for (int j = 0; j < i; j++) {
				String value2 = items[j].getText(index);
				Date date2=null;
				try {
					date2 = sourceFormat.parse(value2);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (date1.compareTo(date2) > 0) {
					int columnCount = table.getColumnCount();
					ArrayList<String> rowValues = new ArrayList<String>();
					for (int x = 0; x < columnCount; x++) {
						rowValues.add(items[i].getText(x));
					}
					items[i].dispose();
					TableItem item = new TableItem(table, SWT.NONE, j);
					String[] values = rowValues.toArray(new String[0]);
					item.setText(values);
					items = table.getItems();
					break;
				}
			}
		}
		table.setSortColumn(column);
		table.deselectAll();
		table.setTopIndex(0);
		table.setLinesVisible(true);
		return table;
	}

	public Properties getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}
}
