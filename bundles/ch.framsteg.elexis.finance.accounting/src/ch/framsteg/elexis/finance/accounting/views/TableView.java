/*******************************************************************************
 * Copyright (c) 2020-2022,  Olivier Debenath
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olivier Debenath <olivier@debenath.ch> - initial implementation
 *    
 *******************************************************************************/
package ch.framsteg.elexis.finance.accounting.views;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Collator;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import com.tiff.common.ui.datepicker.DatePickerCombo;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.PersistentObject;
import ch.framsteg.elexis.finance.accounting.filesystem.TableExporter;
import ch.rgw.tools.JdbcLink;

public class TableView extends ViewPart implements IRefreshable {

	private final static String DELIMITER = "app.delimiter";
	private final static String NEW_LINE = "app.newline";

	private final static String HEADER_1 = "msg.table.header.1";
	private final static String HEADER_2 = "msg.table.header.2";
	private final static String HEADER_3 = "msg.table.header.3";
	private final static String HEADER_4 = "msg.table.header.4";
	private final static String HEADER_5 = "msg.table.header.5";
	private final static String HEADER_6 = "msg.table.header.6";
	private final static String HEADER_7 = "msg.table.header.7";
	private final static String HEADER_8 = "msg.table.header.8";
	private final static String HEADER_9 = "msg.table.header.9";

	private final static String JDBC_STATEMENT_0 = "app.jdbc.stmt.0";
	private final static String JDBC_STATEMENT_1 = "app.jdbc.stmt.1";
	private final static String JDBC_STATEMENT_2 = "app.jdbc.stmt.2";
	private final static String JDBC_STATEMENT_3 = "app.jdbc.stmt.3";

	private Properties applicationProperties;
	private Properties messagesProperties;

	private Composite container;
	private Table table;
	private DatePickerCombo dpcDateFrom;
	private DatePickerCombo dpcDateTo;
	private boolean billNrAsc;
	private boolean amountAsc;
	private boolean billingDateAsc;
	private boolean stateAsc;
	private boolean accountingDateAsc;
	private boolean descriptionAsc;
	private boolean patIdAsc;
	private boolean patNameAsc;
	private boolean patPrenameAsc;

	private StringBuilder filteredTableContent;
	
	private String header1;
	private String header2;
	private String header3;
	private String header4;
	private String header5;
	private String header6;
	private String header7;
	private String header8;
	private String header9;
	
	private final SettingsPreferenceStore preferenceStore = new SettingsPreferenceStore(CoreHub.globalCfg);

	public TableView() {
		billNrAsc = true;
		amountAsc = true;
		billingDateAsc = true;
		stateAsc = true;
		accountingDateAsc = true;
		descriptionAsc = true;
		patIdAsc = true;
		patNameAsc = true;
		patPrenameAsc = true;
		loadProperties();
		header1 = getMessagesProperties().getProperty(HEADER_1);
		header2 = getMessagesProperties().getProperty(HEADER_2);
		header3 = getMessagesProperties().getProperty(HEADER_3);
		header4 = getMessagesProperties().getProperty(HEADER_4);
		header5 = getMessagesProperties().getProperty(HEADER_5);
		header6 = getMessagesProperties().getProperty(HEADER_6);
		header7 = getMessagesProperties().getProperty(HEADER_7);
		header8 = getMessagesProperties().getProperty(HEADER_8);
		header9 = getMessagesProperties().getProperty(HEADER_9);
	}

	private void loadProperties() {
		try {
			String separator = FileSystems.getDefault().getSeparator();
			setApplicationProperties(new Properties());
			setMessagesProperties(new Properties());
			getApplicationProperties().load(TableView.class.getClassLoader()
					.getResourceAsStream(separator + "resources" + separator + "application.properties"));
			getMessagesProperties().load(TableView.class.getClassLoader()
					.getResourceAsStream(separator + "resources" + separator + "messages.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private StringBuilder prepareCsv(StringBuilder tableContent) {

		String delimiter = getApplicationProperties().getProperty(DELIMITER);
		String newLine = getApplicationProperties().getProperty(NEW_LINE);

		StringBuilder csvContent = new StringBuilder();
		csvContent.append(header1);
		csvContent.append(delimiter);
		csvContent.append(header2);
		csvContent.append(delimiter);
		csvContent.append(header3);
		csvContent.append(delimiter);
		csvContent.append(header4);
		csvContent.append(delimiter);
		csvContent.append(header5);
		csvContent.append(delimiter);
		csvContent.append(header6);
		csvContent.append(delimiter);
		csvContent.append(header7);
		csvContent.append(delimiter);
		csvContent.append(header8);
		csvContent.append(delimiter);
		csvContent.append(header9);
		csvContent.append(newLine);
		csvContent.append(tableContent);
		csvContent.append(newLine);
		return csvContent;
	}

	@Override
	public void createPartControl(Composite parent) {

		Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		mainComposite.setLayout(gridLayout);

		Composite headComposite = new Composite(mainComposite, SWT.NONE);
		GridLayout headLayout = new GridLayout();
		headLayout.numColumns = 2;
		headComposite.setLayout(headLayout);

		Group billingGroup = new Group(mainComposite, SWT.NONE);
		billingGroup.setText(getMessagesProperties().getProperty("msg.title"));
		billingGroup.setLayout(headLayout);

		GridData groupData = new GridData();
		groupData.grabExcessHorizontalSpace = true;
		groupData.horizontalAlignment = SWT.FILL;
		billingGroup.setLayoutData(groupData);

		Label labelFrom = new Label(billingGroup, SWT.FILL);
		labelFrom.setText(getMessagesProperties().getProperty("msg.list.from"));

		dpcDateFrom = new DatePickerCombo(billingGroup, SWT.BORDER);
		dpcDateFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
			}
		});

		Label labelTo = new Label(billingGroup, SWT.FILL);
		labelTo.setText(getMessagesProperties().getProperty("msg.list.to"));

		dpcDateTo = new DatePickerCombo(billingGroup, SWT.BORDER);
		dpcDateTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
			}
		});

		GridData tableCompositeData = new GridData();
		tableCompositeData.grabExcessHorizontalSpace = true;
		tableCompositeData.horizontalAlignment = SWT.FILL;

		GridData scrolledCompositeData = new GridData();
		scrolledCompositeData.grabExcessHorizontalSpace = true;
		scrolledCompositeData.horizontalAlignment = SWT.FILL;

		GridData containerData = new GridData();
		containerData.grabExcessHorizontalSpace = true;
		containerData.horizontalAlignment = SWT.FILL;

		Composite tableComposite = new Composite(mainComposite, SWT.NONE);
		tableComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		tableComposite.setLayoutData(tableCompositeData);

		ScrolledComposite scrolledComposite = new ScrolledComposite(tableComposite, SWT.BORDER | SWT.V_SCROLL);
		scrolledComposite
				.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 1200).create());
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		container = new Composite(scrolledComposite, SWT.NULL);
		container.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());
		container.setLayoutData(containerData);
		container.setVisible(true);

		table = new Table(container, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		table.setLinesVisible(true);

		Button btnLoad = new Button(billingGroup, SWT.PUSH);
		btnLoad.setText(getMessagesProperties().getProperty("msg.load.list"));
		btnLoad.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				table.removeAll();
				loadTable(table);

				for (TableColumn tableColumn : table.getColumns()) {
					tableColumn.pack();
				}

				table.redraw();
				table.pack();
				container.pack();

				scrolledComposite.setContent(container);
				scrolledComposite.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				sortTable(table, 3);
			}
		});

		Button btnExport = new Button(billingGroup, SWT.PUSH);
		btnExport.setText(getMessagesProperties().getProperty("msg.export.list"));
		btnExport.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!preferenceStore.getString(getApplicationProperties().getProperty("pref.output.dir")).isEmpty()) {
					TableExporter tableExporter = new TableExporter(applicationProperties, messagesProperties);
					try {
						tableExporter.export(prepareCsv(filteredTableContent));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					MessageDialog.openInformation(container.getShell(),
							getMessagesProperties().getProperty("err.dialog.title"),
							getMessagesProperties().getProperty("err.no.output.dir"));
				}
			}
		});

		TableColumn tableColumn1 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn2 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn3 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn4 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn5 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn6 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn7 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn8 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn9 = new TableColumn(table, SWT.LEFT);

		tableColumn1.setText(header1);
		tableColumn1.setAlignment(SWT.LEFT);
		tableColumn1.addListener(SWT.Selection, billingNumberSortListener);
		tableColumn2.setText(header2);
		tableColumn2.setAlignment(SWT.LEFT);
		tableColumn2.addListener(SWT.Selection, amountSortListener);
		tableColumn3.setText(header3);
		tableColumn3.setAlignment(SWT.LEFT);
		tableColumn3.addListener(SWT.Selection, billingDateSortListener);
		tableColumn4.setText(header4);
		tableColumn4.setAlignment(SWT.LEFT);
		tableColumn4.addListener(SWT.Selection, stateSortListener);
		tableColumn5.setText(header5);
		tableColumn5.setAlignment(SWT.LEFT);
		tableColumn5.addListener(SWT.Selection, accountingDateSortListener);
		tableColumn6.setText(header6);
		tableColumn6.setAlignment(SWT.LEFT);
		tableColumn6.addListener(SWT.Selection, descriptionSortListener);
		tableColumn7.setText(header7);
		tableColumn7.setAlignment(SWT.LEFT);
		tableColumn7.addListener(SWT.Selection, patIdSortListener);
		tableColumn8.setText(header8);
		tableColumn8.setAlignment(SWT.LEFT);
		tableColumn8.addListener(SWT.Selection, nameSortListener);
		tableColumn9.setText(header9);
		tableColumn9.setAlignment(SWT.LEFT);
		tableColumn9.addListener(SWT.Selection, prenameSortListener);

		table.setHeaderVisible(true);
		table.pack();

		Button btnClear = new Button(billingGroup, SWT.PUSH);
		btnClear.setText(getMessagesProperties().getProperty("msg.clear.all"));
		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dpcDateFrom.setText("");
				dpcDateTo.setText("");
				table.clearAll();
			}
		});

	}

	@SuppressWarnings("deprecation")
	private void loadTable(Table table) {

		String allTransactions = applicationProperties.getProperty(JDBC_STATEMENT_0);
		String newerTransactions = applicationProperties.getProperty(JDBC_STATEMENT_1);
		String olderTransactions = applicationProperties.getProperty(JDBC_STATEMENT_2);
		String intermediateTransactions = applicationProperties.getProperty(JDBC_STATEMENT_3);

		try {
			String dateFromStringMod = new String();
			String dateToStringMod = new String();

			if (!dpcDateFrom.getText().isEmpty()) {
				dateFromStringMod = "20" + 
						dpcDateFrom.getText().substring(6, 8) + 
						dpcDateFrom.getText().substring(3, 5) + 
						dpcDateFrom.getText().substring(0, 2);
			}
			if (!dpcDateTo.getText().isEmpty()) {
				dateToStringMod = "20" + 
						dpcDateTo.getText().substring(6, 8) + 
						dpcDateTo.getText().substring(3, 5) + 
						dpcDateTo.getText().substring(0, 2);
			}
			String parametrizedSqlString = new String();

			if (dateFromStringMod.isEmpty() && dateToStringMod.isEmpty()) {
				parametrizedSqlString = allTransactions;
			}

			if (!dateFromStringMod.isEmpty()) {
				parametrizedSqlString = MessageFormat.format(olderTransactions,
						dateFromStringMod);
			}

			if (!dateToStringMod.isEmpty()) {
				parametrizedSqlString = MessageFormat.format(newerTransactions,
						dateToStringMod);
			}

			if (!dateFromStringMod.isEmpty() && !dateToStringMod.isEmpty()) {
				parametrizedSqlString = MessageFormat.format(intermediateTransactions,
						dateFromStringMod, dateToStringMod);
			}

			JdbcLink jdbcLink = PersistentObject.getConnection();
			Connection connection = jdbcLink.getKeepAliveConnection();
			Statement statement = connection.createStatement();

			ResultSet resultSet = statement.executeQuery(parametrizedSqlString);
			ArrayList<String[]> lines = new ArrayList<String[]>();
			filteredTableContent = new StringBuilder();

			while (resultSet.next()) {
				String billingDate = new String();
				String accountingDate = new String();

				billingDate = resultSet.getString(3).substring(6, 8) + "." + 
							  resultSet.getString(3).substring(4, 6) + "." + 
							  resultSet.getString(3).substring(0, 4);
				
				accountingDate = resultSet.getString(5).substring(6, 8) + "." + 
								 resultSet.getString(5).substring(4, 6) + "." + 
								 resultSet.getString(5).substring(0, 4);
				
				String[] line = new String[] { resultSet.getString(1), 
											   resultSet.getString(2), 
											   billingDate,
											   resultSet.getString(4), 
											   accountingDate, 
											   resultSet.getString(6), 
											   resultSet.getString(7),
											   resultSet.getString(8), 
											   resultSet.getString(9)};
				lines.add(line);
				TableItem item = new TableItem(table, SWT.HOME);
				item.setText(line);
				
				filteredTableContent.append(resultSet.getString(1) + "," + 
										    resultSet.getString(2) + "," + 
										    billingDate	+ "," + 
										    resultSet.getString(4) + "," + 
										    accountingDate + "," + 
										    resultSet.getString(6) + "," + 
										    resultSet.getString(7) + "," + 
										    resultSet.getString(8) + "," + 
										    resultSet.getString(9));
				
				filteredTableContent.append("\n");
			}

			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void sortTable(Table table, int columnNumber) {
		TableItem[] items = table.getItems();
		Collator collator = Collator.getInstance(Locale.getDefault());
		TableColumn column = table.getColumn(columnNumber);
		int index = column == table.getColumn(columnNumber) ? 0 : 1;
		for (int i = 1; i < items.length; i++) {
			String value1 = items[i].getText(index);
			for (int j = 0; j < i; j++) {
				String value2 = items[j].getText(index);
				if (collator.compare(value1, value2) < 0) {
					String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
							items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
							items[i].getText(7), items[i].getText(8) };
					items[i].dispose();
					TableItem item = new TableItem(table, SWT.NONE, j);
					item.setText(values);
					items = table.getItems();
					break;
				}
			}
		}
		table.setSortColumn(column);
		table.deselectAll();
	}

	Listener billingNumberSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 0;
			if (billNrAsc) {
				billNrAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				billNrAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {
						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener amountSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 1;
			if (amountAsc) {
				amountAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				amountAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {
						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener billingDateSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			try {
				TableItem[] items = table.getItems();
				TableColumn column = (TableColumn) e.widget;
				DateFormat sourceFormat = new SimpleDateFormat(
						getApplicationProperties().getProperty("app.date.format.long"));
				int index = 2;

				if (billingDateAsc) {
					billingDateAsc = false;
					for (int i = 1; i < items.length; i++) {
						String value1 = items[i].getText(index);
						Date date1 = sourceFormat.parse(value1);
						for (int j = 0; j < i; j++) {
							String value2 = items[j].getText(index);
							Date date2 = sourceFormat.parse(value2);
							if (date1.compareTo(date2) < 0) {
								String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
										items[i].getText(3), items[i].getText(4), items[i].getText(5),
										items[i].getText(6), items[i].getText(7), items[i].getText(8) };
								items[i].dispose();
								TableItem item = new TableItem(table, SWT.NONE, j);
								item.setText(values);
								items = table.getItems();
								break;
							}
						}
					}
				} else {
					billingDateAsc = true;
					for (int i = 1; i < items.length; i++) {
						String value1 = items[i].getText(index);
						Date date1 = sourceFormat.parse(value1);
						for (int j = 0; j < i; j++) {
							String value2 = items[j].getText(index);
							Date date2 = sourceFormat.parse(value2);
							if (date1.compareTo(date2) > 0) {
								String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
										items[i].getText(3), items[i].getText(4), items[i].getText(5),
										items[i].getText(6), items[i].getText(7), items[i].getText(8) };
								items[i].dispose();
								TableItem item = new TableItem(table, SWT.NONE, j);
								item.setText(values);
								items = table.getItems();
								break;
							}
						}
					}
					table.setSortColumn(column);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	Listener stateSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 3;
			if (stateAsc) {
				stateAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				stateAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener accountingDateSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			try {
				TableItem[] items = table.getItems();
				TableColumn column = (TableColumn) e.widget;
				DateFormat sourceFormat = new SimpleDateFormat(
						getApplicationProperties().getProperty("app.date.format.long"));
				int index = 4;

				if (accountingDateAsc) {
					accountingDateAsc = false;
					for (int i = 1; i < items.length; i++) {
						String value1 = items[i].getText(index);
						Date date1 = sourceFormat.parse(value1);
						for (int j = 0; j < i; j++) {
							String value2 = items[j].getText(index);
							Date date2 = sourceFormat.parse(value2);
							if (date1.compareTo(date2) < 0) {
								String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
										items[i].getText(3), items[i].getText(4), items[i].getText(5),
										items[i].getText(6), items[i].getText(7), items[i].getText(8) };
								items[i].dispose();
								TableItem item = new TableItem(table, SWT.NONE, j);
								item.setText(values);
								items = table.getItems();
								break;
							}
						}
					}
				} else {
					accountingDateAsc = true;
					for (int i = 1; i < items.length; i++) {
						String value1 = items[i].getText(index);
						Date date1 = sourceFormat.parse(value1);
						for (int j = 0; j < i; j++) {
							String value2 = items[j].getText(index);
							Date date2 = sourceFormat.parse(value2);
							if (date1.compareTo(date2) > 0) {
								String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
										items[i].getText(3), items[i].getText(4), items[i].getText(5),
										items[i].getText(6), items[i].getText(7), items[i].getText(8) };
								items[i].dispose();
								TableItem item = new TableItem(table, SWT.NONE, j);
								item.setText(values);
								items = table.getItems();
								break;
							}
						}
					}
					table.setSortColumn(column);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	Listener descriptionSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 5;
			if (descriptionAsc) {
				descriptionAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				descriptionAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener patIdSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 6;
			if (patIdAsc) {
				patIdAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				patIdAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener nameSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 7;
			if (patNameAsc) {
				patNameAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				patNameAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener prenameSortListener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 8;
			if (patPrenameAsc) {
				patPrenameAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				patPrenameAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {
						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	@Override
	public void setFocus() {
	}

	public Properties getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	public Properties getMessagesProperties() {
		return messagesProperties;
	}

	public void setMessagesProperties(Properties messagesProperties) {
		this.messagesProperties = messagesProperties;
	}

	@Override
	public void refresh() {
	}
}
