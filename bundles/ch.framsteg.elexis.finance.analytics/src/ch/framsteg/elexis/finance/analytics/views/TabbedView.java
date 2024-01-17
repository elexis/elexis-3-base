package ch.framsteg.elexis.finance.analytics.views;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.tiff.common.ui.datepicker.DatePickerCombo;

import ch.framsteg.elexis.finance.analytics.controller.TabbedController;
import ch.framsteg.elexis.finance.analytics.export.CsvExporter;
import ch.framsteg.elexis.finance.analytics.export.PDFExporter;

public class TabbedView {

	private Composite composite;

	private Properties applicationProperties;
	private Properties messagesProperties;
	private Properties sqlProperties;

	private final static String DATE_GROUP_CAPTION = "reporting.view.general.group.date.caption";
	private final static String DATE_FROM = "reporting.view.general.date.from";
	private final static String DATE_TO = "reporting.view.general.date.to";
	private final static String BUTTON_QUERY = "reporting.view.general.button.query";
	private final static String BUTTON_QUERY_TOOLTIP = "reporting.view.general.button.query.tooltip";
	private final static String BUTTON_CLEAR = "reporting.view.general.button.clear";
	private final static String BUTTON_CLEAR_TOOLTIP = "reporting.view.general.button.clear.tooltip";

	private final static String DATE_FIRST = "date.first";

	private final static String TABLE_GROUP_CAPTION_1 = "reporting.view.tab.1.table.group";
	private final static String TABLE_GROUP_CAPTION_2 = "reporting.view.tab.2.table.group";
	private final static String TABLE_GROUP_CAPTION_3 = "reporting.view.tab.3.table.group";
	private final static String TABLE_GROUP_CAPTION_4 = "reporting.view.tab.4.table.group";
	private final static String TABLE_GROUP_CAPTION_5 = "reporting.view.tab.5.table.group";
	private final static String TABLE_GROUP_CAPTION_6 = "reporting.view.tab.6.table.group";
	private final static String TABLE_GROUP_CAPTION_7 = "reporting.view.tab.7.table.group";
	private final static String TABLE_GROUP_CAPTION_8 = "reporting.view.tab.8.table.group";

	private final static String PDF_DOCUMENT_TITLE_1 = "reporting.view.tab.1.pdf.title";
	private final static String PDF_DOCUMENT_TITLE_2 = "reporting.view.tab.2.pdf.title";
	private final static String PDF_DOCUMENT_TITLE_3 = "reporting.view.tab.3.pdf.title";
	private final static String PDF_DOCUMENT_TITLE_4 = "reporting.view.tab.4.pdf.title";
	private final static String PDF_DOCUMENT_TITLE_5 = "reporting.view.tab.5.pdf.title";
	private final static String PDF_DOCUMENT_TITLE_6 = "reporting.view.tab.6.pdf.title";
	private final static String PDF_DOCUMENT_TITLE_7 = "reporting.view.tab.7.pdf.title";
	private final static String PDF_DOCUMENT_TITLE_8 = "reporting.view.tab.8.pdf.title";

	private final static String TAB1_TABLE_HEADER1 = "reporting.view.tab.1.table.header.1";
	private final static String TAB1_TABLE_HEADER2 = "reporting.view.tab.1.table.header.2";
	private final static String TAB1_COLUMN_WIDTHS = "reporting.view.tab.1.pdf.column.widths";

	private final static String TAB2_TABLE_HEADER1 = "reporting.view.tab.2.table.header.1";
	private final static String TAB2_TABLE_HEADER2 = "reporting.view.tab.2.table.header.2";
	private final static String TAB2_TABLE_HEADER3 = "reporting.view.tab.2.table.header.3";
	private final static String TAB2_COLUMN_WIDTHS = "reporting.view.tab.2.pdf.column.widths";

	private final static String TAB3_TABLE_HEADER1 = "reporting.view.tab.3.table.header.1";
	private final static String TAB3_TABLE_HEADER2 = "reporting.view.tab.3.table.header.2";
	private final static String TAB3_TABLE_HEADER3 = "reporting.view.tab.3.table.header.3";
	private final static String TAB3_TABLE_HEADER4 = "reporting.view.tab.3.table.header.4";
	private final static String TAB3_COLUMN_WIDTHS = "reporting.view.tab.3.pdf.column.widths";

	private final static String TAB4_TABLE_HEADER1 = "reporting.view.tab.4.table.header.1";
	private final static String TAB4_TABLE_HEADER2 = "reporting.view.tab.4.table.header.2";
	private final static String TAB4_COLUMN_WIDTHS = "reporting.view.tab.4.pdf.column.widths";

	private final static String TAB5_TABLE_HEADER1 = "reporting.view.tab.5.table.header.1";
	private final static String TAB5_TABLE_HEADER2 = "reporting.view.tab.5.table.header.2";
	private final static String TAB5_TABLE_HEADER3 = "reporting.view.tab.5.table.header.3";
	private final static String TAB5_COLUMN_WIDTHS = "reporting.view.tab.5.pdf.column.widths";

	private final static String TAB6_TABLE_HEADER1 = "reporting.view.tab.6.table.header.1";
	private final static String TAB6_TABLE_HEADER2 = "reporting.view.tab.6.table.header.2";
	private final static String TAB6_TABLE_HEADER3 = "reporting.view.tab.6.table.header.3";
	private final static String TAB6_COLUMN_WIDTHS = "reporting.view.tab.6.pdf.column.widths";

	private final static String TAB7_TABLE_HEADER1 = "reporting.view.tab.7.table.header.1";
	private final static String TAB7_TABLE_HEADER2 = "reporting.view.tab.7.table.header.2";
	private final static String TAB7_TABLE_HEADER3 = "reporting.view.tab.7.table.header.3";
	private final static String TAB7_COLUMN_WIDTHS = "reporting.view.tab.7.pdf.column.widths";

	private final static String TAB8_TABLE_HEADER1 = "reporting.view.tab.8.table.header.1";
	private final static String TAB8_TABLE_HEADER2 = "reporting.view.tab.8.table.header.2";
	private final static String TAB8_TABLE_HEADER3 = "reporting.view.tab.8.table.header.3";
	private final static String TAB8_TABLE_HEADER4 = "reporting.view.tab.8.table.header.4";
	private final static String TAB8_TABLE_HEADER5 = "reporting.view.tab.8.table.header.5";
	private final static String TAB8_TABLE_HEADER6 = "reporting.view.tab.8.table.header.6";
	private final static String TAB8_TABLE_HEADER7 = "reporting.view.tab.8.table.header.7";
	private final static String TAB8_TABLE_HEADER8 = "reporting.view.tab.8.table.header.8";
	private final static String TAB8_TABLE_HEADER9 = "reporting.view.tab.8.table.header.9";
	private final static String TAB8_TABLE_HEADER10 = "reporting.view.tab.8.table.header.10";
	private final static String TAB8_TABLE_HEADER11 = "reporting.view.tab.8.table.header.11";
	private final static String TAB8_TABLE_HEADER12 = "reporting.view.tab.8.table.header.12";
	private final static String TAB8_TABLE_HEADER13 = "reporting.view.tab.8.table.header.13";
	private final static String TAB8_TABLE_HEADER14 = "reporting.view.tab.8.table.header.14";
	private final static String TAB8_TABLE_HEADER15 = "reporting.view.tab.8.table.header.15";
	private final static String TAB8_TABLE_HEADER16 = "reporting.view.tab.8.table.header.16";
	private final static String TAB8_TABLE_HEADER17 = "reporting.view.tab.8.table.header.17";
	private final static String TAB8_TABLE_HEADER18 = "reporting.view.tab.8.table.header.18";
	private final static String TAB8_TABLE_HEADER19 = "reporting.view.tab.8.table.header.19";

	private final static String FILE_EXPORT_SALES_SERVICE_NAME = "reporting-file-export.sales.service";
	private final static String FILE_EXPORT_SALES_SERVICE_YEAR_NAME = "reporting-file.export.sales.service.year";
	private final static String FILE_EXPORT_SALES_SERVICE_YEAR_MONTH_NAME = "reporting-file.export.sales.service.year.month";
	private final static String FILE_EXPORT_SALES_YEAR_NAME = "reporting-file.export.sales.year";
	private final static String FILE_EXPORT_SALES_YEAR_MONTH_NAME = "reporting.file.export.sales.year.month";
	private final static String FILE_EXPORT_SALES_TARMED_YEAR_MONTH_NAME = "reporting.file.export.sales.tarmed.year.month";
	private final static String FILE_EXPORT_SALES_MEDICAL_YEAR_MONTH_NAME = "reporting.file.export.sales.medical.year.month";
	private final static String FILE_EXPORT_DAILY_REPORT_NAME = "reporting.file.export.dailiy.report.name";

	private final static String BUTTON_PDF_PRINT = "reporting.view.general.button.pdf.export";
	private final static String BUTTON_PDF_PRINT_TOOLTIP = "reporting.view.general.button.pdf.export.tooltip";
	private final static String BUTTON_CSV_EXPORT = "reporting.view.general.button.csv.export";
	private final static String BUTTON_CSV_EXPORT_TOOLTIP = "reporting.view.general.button.csv.export.tooltip";

	private final static String MSG_EMPTY_TABLE = "reporting.view.msg.empty.query";
	private final static String MSG_EMPTY_TABLE_TITLE = "reporting.view.msg.empty.query.title";

	private final static String MSG_UNSUPPORTED_DATABASE = "reporting.view.msg.unsupported.database";
	private final static String MSG_UNSUPPORTED_DATABASE_TITLE = "reporting.view.msg.unsupported.database.title";

	private String today;

	private Button btnQuery;
	private Button btnClear;
	private Button btnExportPdf;
	private Button btnExportCsv;
	private DatePickerCombo dpcDateFrom;
	private DatePickerCombo dpcDateTo;

	private Table table0;
	private Table table1;
	private Table table2;
	private Table table3;
	private Table table4;
	private Table table5;
	private Table table6;
	private Table table7;
	private Table table8;

	private ArrayList<String[]> salesTotalPerService;
	private ArrayList<String[]> salesTotalPerServiceYear;
	private ArrayList<String[]> salesTotalPerServiceYearMonth;
	private ArrayList<String[]> salesTotalPerYear;
	private ArrayList<String[]> salesTotalPerYearMonth;
	private ArrayList<String[]> tarmedPerYearMonth;
	private ArrayList<String[]> medicalPerYearMonth;
	private ArrayList<String[]> dailyReport;

	private String dateFrom1;
	private String dateTo1;
	private String dateFrom2;
	private String dateTo2;
	private String dateFrom3;
	private String dateTo3;
	private String dateFrom4;
	private String dateTo4;
	private String dateFrom5;
	private String dateTo5;
	private String dateFrom6;
	private String dateTo6;
	private String dateFrom7;
	private String dateTo7;
	private String dateFrom8;
	private String dateTo8;

	private boolean salesTotalPerServiceCol1Asc;
	private boolean salesTotalPerServiceCol2Asc;
	private boolean salesTotalPerServiceYearCol1Asc;
	private boolean salesTotalPerServiceYearCol2Asc;
	private boolean salesTotalPerServiceYearCol3Asc;
	private boolean salesTotalPerServiceYearMonthCol1Asc;
	private boolean salesTotalPerServiceYearMonthCol2Asc;
	private boolean salesTotalPerServiceYearMonthCol3Asc;
	private boolean salesTotalPerServiceYearMonthCol4Asc;
	private boolean salesTotalPerYearCol1Asc;
	private boolean salesTotalPerYearCol2Asc;
	private boolean salesTotalPerYearMonthCol1Asc;
	private boolean salesTotalPerYearMonthCol2Asc;
	private boolean salesTotalPerYearMonthCol3Asc;
	private boolean tarmedPerYearMonthCol1Asc;
	private boolean tarmedPerYearMonthCol2Asc;
	private boolean tarmedPerYearMonthCol3Asc;
	private boolean medicalPerYearMonthCol1Asc;
	private boolean medicalPerYearMonthCol2Asc;
	private boolean medicalPerYearMonthCol3Asc;

	public TabbedView(Composite composite, Properties applicationProperties, Properties messagesProperties,
			Properties sqlProperties) {
		setComposite(composite);
		setApplicationProperties(applicationProperties);
		setMessagesProperties(messagesProperties);
		setSqlProperties(sqlProperties);
		setSalesTotalPerServiceCol1Asc(false);
		setSalesTotalPerServiceCol2Asc(false);
		LocalDate dateObj = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		today = dateObj.format(formatter);

	}

	public Composite buildSalesTotalPerServiceComposite() {

		/* Hack to extract the table from the returned rootComposite */
		Composite rootComposite = buildGeneralTableComposite(getMessagesProperties().getProperty(DATE_GROUP_CAPTION),
				getMessagesProperties().getProperty(DATE_FROM), getMessagesProperties().getProperty(DATE_TO),
				getMessagesProperties().getProperty(BUTTON_QUERY),
				getMessagesProperties().getProperty(BUTTON_QUERY_TOOLTIP),
				getMessagesProperties().getProperty(BUTTON_CLEAR),
				getMessagesProperties().getProperty(BUTTON_CLEAR_TOOLTIP),
				getMessagesProperties().getProperty(TABLE_GROUP_CAPTION_1),
				getMessagesProperties().getProperty(BUTTON_PDF_PRINT),
				getMessagesProperties().getProperty(BUTTON_PDF_PRINT_TOOLTIP),
				getMessagesProperties().getProperty(BUTTON_CSV_EXPORT),
				getMessagesProperties().getProperty(BUTTON_CSV_EXPORT_TOOLTIP));

		Control[] controls = rootComposite.getChildren();
		Group dateGroup = (Group) controls[0];
		Group tableGroup = (Group) controls[1];
		DatePickerCombo dpcFrom = (DatePickerCombo) dateGroup.getChildren()[1];
		DatePickerCombo dpcTo = (DatePickerCombo) dateGroup.getChildren()[3];
		dateFrom1 = new String();
		dateTo1 = new String();
		Composite refreshButtonsComposite = (Composite) dateGroup.getChildren()[4];
		Button btnClear = (Button) refreshButtonsComposite.getChildren()[1];

		Composite tableComposite = (Composite) tableGroup.getChildren()[0];
		table0 = (Table) tableComposite.getChildren()[0];

		TableColumn tableColumn0 = new TableColumn(table0, SWT.LEFT);
		TableColumn tableColumn1 = new TableColumn(table0, SWT.LEFT);

		tableColumn0.setText(getMessagesProperties().getProperty(TAB1_TABLE_HEADER1));
		tableColumn0.setAlignment(SWT.LEFT);
		tableColumn0.addListener(SWT.Selection, salesTotalPerServiceCol1Listener);
		tableColumn1.setText(getMessagesProperties().getProperty(TAB1_TABLE_HEADER2));
		tableColumn1.setAlignment(SWT.LEFT);
		tableColumn1.addListener(SWT.Selection, salesTotalPerServiceCol2Listener);
		table0.setHeaderVisible(true);

		for (TableColumn tableColumn : table0.getColumns()) {
			tableColumn.pack();
		}

		table0.setLinesVisible(true);
		table0.pack();
		table0.deselectAll();
		table0.setLinesVisible(true);
		setTable0(table0);

		setSalesTotalPerService(new ArrayList<String[]>());

		btnQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TabbedController tabbedController = new TabbedController(getApplicationProperties(),
						getSqlProperties());

				if (tabbedController.isDatabaseSupported()) {
					setSalesTotalPerService(tabbedController.getSalesTotalPerService(dateFrom1, dateTo1));
					table0.clearAll();
					table0.removeAll();

					for (String[] line : getSalesTotalPerService()) {
						TableItem item = new TableItem(table0, SWT.HOME);
						item.setText(line);
					}

					for (TableColumn tableColumn : table0.getColumns()) {
						tableColumn.pack();
					}
					table0.redraw();
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_UNSUPPORTED_DATABASE_TITLE),
							getMessagesProperties().getProperty(MSG_UNSUPPORTED_DATABASE));
				}

			}

		});

		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dpcFrom.setDate(null);
				dateFrom1 = "";
				dpcTo.setDate(null);
				dateTo1 = "";
			}
		});

		dpcFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				dateFrom1 = dpcFrom.getText();
			}
		});

		dpcTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				dateTo1 = dpcTo.getText();
			}
		});

		btnExportPdf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// MSG_EMPTY_TABLE
				PDFExporter pdfExporter = new PDFExporter(getApplicationProperties(), getMessagesProperties());
				ArrayList<String[]> modifiedLines = new ArrayList<String[]>();
				ArrayList<String[]> lines = getSalesTotalPerService();
				if (lines.size() > 0) {
					String[] headerLine = new String[] { getMessagesProperties().getProperty(TAB1_TABLE_HEADER1),
							getMessagesProperties().getProperty(TAB1_TABLE_HEADER2) };
					modifiedLines.add(headerLine);

					for (String[] string : lines) {
						modifiedLines.add(string);
					}
					pdfExporter.exportTable(rootComposite.getShell(), modifiedLines,
							getMessagesProperties().getProperty(FILE_EXPORT_SALES_SERVICE_NAME),
							MessageFormat.format(getMessagesProperties().getProperty(PDF_DOCUMENT_TITLE_1),
									dateFrom1.isEmpty() ? getApplicationProperties().getProperty(DATE_FIRST)
											: dateFrom1,
									dateTo1.isEmpty() ? today : dateTo1),
							getIntegerArray(getMessagesProperties().getProperty(TAB1_COLUMN_WIDTHS).split(",")));

				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE_TITLE),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE));
				}
			}
		});

		btnExportCsv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CsvExporter csvExporter = new CsvExporter(getApplicationProperties(), getMessagesProperties());
				ArrayList<String[]> modifiedLines = new ArrayList<String[]>();
				ArrayList<String[]> lines = getSalesTotalPerService();
				if (lines.size() > 0) {
					String[] headerLine = new String[] { getMessagesProperties().getProperty(TAB1_TABLE_HEADER1),
							getMessagesProperties().getProperty(TAB1_TABLE_HEADER2) };
					modifiedLines.add(headerLine);
					for (String[] string : lines) {
						modifiedLines.add(string);
					}
					csvExporter.export(rootComposite.getShell(), modifiedLines,
							getMessagesProperties().getProperty(FILE_EXPORT_SALES_SERVICE_NAME));
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE_TITLE),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE));
				}
			}
		});

		return rootComposite;
	}

	Listener salesTotalPerServiceCol1Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isSalesTotalPerServiceCol1Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable0(tableSorter.sortByStringAsc(getTable0(), 0));
				setSalesTotalPerServiceCol1Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable0(tableSorter.sortByStringDesc(getTable0(), 0));
				setSalesTotalPerServiceCol1Asc(true);
			}
		}
	};

	Listener salesTotalPerServiceCol2Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isSalesTotalPerServiceCol2Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable0(tableSorter.sortByFloatAsc(getTable0(), 1));
				setSalesTotalPerServiceCol2Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable0(tableSorter.sortByFloatDesc(getTable0(), 1));
				setSalesTotalPerServiceCol2Asc(true);
			}
		}
	};

	public Composite buildSalesTotalPerServiceYearComposite() {
		/* Hack to extract the table from the returned rootComposite */
		Composite rootComposite = buildGeneralTableComposite(getMessagesProperties().getProperty(DATE_GROUP_CAPTION),
				getMessagesProperties().getProperty(DATE_FROM), getMessagesProperties().getProperty(DATE_TO),
				getMessagesProperties().getProperty(BUTTON_QUERY),
				getMessagesProperties().getProperty(BUTTON_QUERY_TOOLTIP),
				getMessagesProperties().getProperty(BUTTON_CLEAR),
				getMessagesProperties().getProperty(BUTTON_CLEAR_TOOLTIP),
				getMessagesProperties().getProperty(TABLE_GROUP_CAPTION_2),
				getMessagesProperties().getProperty(BUTTON_PDF_PRINT),
				getMessagesProperties().getProperty(BUTTON_PDF_PRINT_TOOLTIP),
				getMessagesProperties().getProperty(BUTTON_CSV_EXPORT),
				getMessagesProperties().getProperty(BUTTON_CSV_EXPORT_TOOLTIP));

		Control[] controls = rootComposite.getChildren();
		Group firstGroup = (Group) controls[0];
		Group secondGroup = (Group) controls[1];
		Composite child1 = (Composite) secondGroup.getChildren()[0];
		DatePickerCombo dpcFrom = (DatePickerCombo) firstGroup.getChildren()[1];
		DatePickerCombo dpcTo = (DatePickerCombo) firstGroup.getChildren()[3];
		dateFrom2 = new String();
		dateTo2 = new String();
		table1 = (Table) child1.getChildren()[0];

		TableColumn tableColumn0 = new TableColumn(table1, SWT.LEFT);
		TableColumn tableColumn1 = new TableColumn(table1, SWT.LEFT);
		TableColumn tableColumn2 = new TableColumn(table1, SWT.LEFT);

		tableColumn0.setText(getMessagesProperties().getProperty(TAB2_TABLE_HEADER1));
		tableColumn0.setAlignment(SWT.LEFT);
		tableColumn0.addListener(SWT.Selection, salesTotalPerServiceYearCol1Listener);
		tableColumn1.setText(getMessagesProperties().getProperty(TAB2_TABLE_HEADER2));
		tableColumn1.setAlignment(SWT.LEFT);
		tableColumn1.addListener(SWT.Selection, salesTotalPerServiceYearCol2Listener);
		tableColumn2.setText(getMessagesProperties().getProperty(TAB2_TABLE_HEADER3));
		tableColumn2.setAlignment(SWT.LEFT);
		tableColumn2.addListener(SWT.Selection, salesTotalPerServiceYearCol3Listener);
		table1.setHeaderVisible(true);

		for (TableColumn tableColumn : table1.getColumns()) {
			tableColumn.pack();
		}

		table1.setLinesVisible(true);
		table1.pack();
		table1.deselectAll();
		table1.setLinesVisible(true);

		setSalesTotalPerServiceYear(new ArrayList<String[]>());

		btnQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TabbedController tabbedController = new TabbedController(getApplicationProperties(),
						getSqlProperties());

				if (tabbedController.isDatabaseSupported()) {
					setSalesTotalPerServiceYear(tabbedController.getSalesTotalPerServiceYear(dateFrom2, dateTo2));
					table1.clearAll();
					table1.removeAll();

					for (String[] line : getSalesTotalPerServiceYear()) {
						TableItem item = new TableItem(table1, SWT.HOME);
						item.setText(line);
					}

					for (TableColumn tableColumn : table1.getColumns()) {
						tableColumn.pack();
					}
					table1.redraw();
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_UNSUPPORTED_DATABASE_TITLE),
							getMessagesProperties().getProperty(MSG_UNSUPPORTED_DATABASE));
				}
			}

		});

		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dpcFrom.setDate(null);
				dateFrom2 = "";
				dpcTo.setDate(null);
				dateTo2 = "";
			}
		});

		dpcFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				dateFrom2 = dpcFrom.getText();
			}
		});

		dpcTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				dateTo2 = dpcTo.getText();
			}
		});

		btnExportPdf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PDFExporter pdfExporter = new PDFExporter(getApplicationProperties(), getMessagesProperties());
				ArrayList<String[]> modifiedLines = new ArrayList<String[]>();
				ArrayList<String[]> lines = getSalesTotalPerServiceYear();
				if (lines.size() > 0) {
					String[] headerLine = new String[] { getMessagesProperties().getProperty(TAB2_TABLE_HEADER1),
							getMessagesProperties().getProperty(TAB2_TABLE_HEADER2),
							getMessagesProperties().getProperty(TAB2_TABLE_HEADER3) };
					modifiedLines.add(headerLine);
					for (String[] string : lines) {
						modifiedLines.add(string);
					}
					pdfExporter.exportTable(rootComposite.getShell(), modifiedLines,
							getMessagesProperties().getProperty(FILE_EXPORT_SALES_SERVICE_YEAR_NAME),
							MessageFormat.format(getMessagesProperties().getProperty(PDF_DOCUMENT_TITLE_2),
									dateFrom2.isEmpty() ? getApplicationProperties().getProperty(DATE_FIRST)
											: dateFrom2,
									dateTo2.isEmpty() ? today : dateTo2),
							getIntegerArray(getMessagesProperties().getProperty(TAB2_COLUMN_WIDTHS).split(",")));
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE_TITLE),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE));
				}
			}
		});

		btnExportCsv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CsvExporter csvExporter = new CsvExporter(getApplicationProperties(), getMessagesProperties());
				ArrayList<String[]> modifiedLines = new ArrayList<String[]>();
				ArrayList<String[]> lines = getSalesTotalPerServiceYear();
				if (lines.size() > 0) {
					String[] headerLine = new String[] { getMessagesProperties().getProperty(TAB2_TABLE_HEADER1),
							getMessagesProperties().getProperty(TAB2_TABLE_HEADER2),
							getMessagesProperties().getProperty(TAB2_TABLE_HEADER3) };
					modifiedLines.add(headerLine);
					for (String[] string : lines) {
						modifiedLines.add(string);
					}
					csvExporter.export(rootComposite.getShell(), modifiedLines,
							getMessagesProperties().getProperty(FILE_EXPORT_SALES_SERVICE_YEAR_NAME));
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE_TITLE),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE));
				}
			}
		});

		return rootComposite;
	}

	Listener salesTotalPerServiceYearCol1Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isSalesTotalPerServiceYearCol1Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable1(tableSorter.sortByIntegerAsc(getTable1(), 0));
				setSalesTotalPerServiceYearCol1Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable1(tableSorter.sortByIntegerDesc(getTable1(), 0));
				setSalesTotalPerServiceYearCol1Asc(true);
			}
		}
	};

	Listener salesTotalPerServiceYearCol2Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isSalesTotalPerServiceYearCol2Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable1(tableSorter.sortByStringAsc(getTable1(), 1));
				setSalesTotalPerServiceYearCol2Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable1(tableSorter.sortByStringDesc(getTable1(), 1));
				setSalesTotalPerServiceYearCol2Asc(true);
			}
		}
	};

	Listener salesTotalPerServiceYearCol3Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isSalesTotalPerServiceYearCol3Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable1(tableSorter.sortByFloatAsc(getTable1(), 2));
				setSalesTotalPerServiceYearCol3Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable1(tableSorter.sortByFloatDesc(getTable1(), 2));
				setSalesTotalPerServiceYearCol3Asc(true);
			}
		}
	};

	public Composite buildSalesTotalPerServiceYearMonthComposite() {
		/* Hack to extract the table from the returned rootComposite */
		Composite rootComposite = buildGeneralTableComposite(getMessagesProperties().getProperty(DATE_GROUP_CAPTION),
				getMessagesProperties().getProperty(DATE_FROM), getMessagesProperties().getProperty(DATE_TO),
				getMessagesProperties().getProperty(BUTTON_QUERY),
				getMessagesProperties().getProperty(BUTTON_QUERY_TOOLTIP),
				getMessagesProperties().getProperty(BUTTON_CLEAR),
				getMessagesProperties().getProperty(BUTTON_CLEAR_TOOLTIP),
				getMessagesProperties().getProperty(TABLE_GROUP_CAPTION_3),
				getMessagesProperties().getProperty(BUTTON_PDF_PRINT),
				getMessagesProperties().getProperty(BUTTON_PDF_PRINT_TOOLTIP),
				getMessagesProperties().getProperty(BUTTON_CSV_EXPORT),
				getMessagesProperties().getProperty(BUTTON_CSV_EXPORT_TOOLTIP));

		Control[] controls = rootComposite.getChildren();
		Group firstGroup = (Group) controls[0];
		Group secondGroup = (Group) controls[1];
		Composite child1 = (Composite) secondGroup.getChildren()[0];
		DatePickerCombo dpcFrom = (DatePickerCombo) firstGroup.getChildren()[1];
		DatePickerCombo dpcTo = (DatePickerCombo) firstGroup.getChildren()[3];
		dateFrom3 = new String();
		dateTo3 = new String();
		table2 = (Table) child1.getChildren()[0];

		TableColumn tableColumn0 = new TableColumn(table2, SWT.LEFT);
		TableColumn tableColumn1 = new TableColumn(table2, SWT.LEFT);
		TableColumn tableColumn2 = new TableColumn(table2, SWT.LEFT);
		TableColumn tableColumn3 = new TableColumn(table2, SWT.LEFT);

		tableColumn0.setText(getMessagesProperties().getProperty(TAB3_TABLE_HEADER1));
		tableColumn0.setAlignment(SWT.LEFT);
		tableColumn0.addListener(SWT.Selection, salesTotalPerServiceYearMonthCol1Listener);
		tableColumn1.setText(getMessagesProperties().getProperty(TAB3_TABLE_HEADER2));
		tableColumn1.setAlignment(SWT.LEFT);
		tableColumn1.addListener(SWT.Selection, salesTotalPerServiceYearMonthCol2Listener);
		tableColumn2.setText(getMessagesProperties().getProperty(TAB3_TABLE_HEADER3));
		tableColumn2.setAlignment(SWT.LEFT);
		tableColumn2.addListener(SWT.Selection, salesTotalPerServiceYearMonthCol3Listener);
		tableColumn3.setText(getMessagesProperties().getProperty(TAB3_TABLE_HEADER4));
		tableColumn3.setAlignment(SWT.LEFT);
		tableColumn3.addListener(SWT.Selection, salesTotalPerServiceYearMonthCol4Listener);
		table2.setHeaderVisible(true);

		for (TableColumn tableColumn : table2.getColumns()) {
			tableColumn.pack();
		}

		table2.setLinesVisible(true);
		table2.pack();
		table2.deselectAll();
		table2.setLinesVisible(true);

		setSalesTotalPerServiceYearMonth(new ArrayList<String[]>());

		btnQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TabbedController tabbedController = new TabbedController(getApplicationProperties(),
						getSqlProperties());
				if (tabbedController.isDatabaseSupported()) {
					setSalesTotalPerServiceYearMonth(
							tabbedController.getSalesTotalPerServiceYearMonth(dateFrom3, dateTo3));
					table2.clearAll();
					table2.removeAll();

					for (String[] line : getSalesTotalPerServiceYearMonth()) {
						TableItem item = new TableItem(table2, SWT.HOME);
						item.setText(line);
					}

					for (TableColumn tableColumn : table2.getColumns()) {
						tableColumn.pack();
					}
					table2.redraw();
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_UNSUPPORTED_DATABASE_TITLE),
							getMessagesProperties().getProperty(MSG_UNSUPPORTED_DATABASE));
				}
			}
		});

		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dpcFrom.setDate(null);
				dateFrom3 = "";
				dpcTo.setDate(null);
				dateTo3 = "";
			}
		});

		dpcFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				dateFrom3 = dpcFrom.getText();
			}
		});

		dpcTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				dateTo3 = dpcTo.getText();
			}
		});

		btnExportPdf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PDFExporter pdfExporter = new PDFExporter(getApplicationProperties(), getMessagesProperties());
				ArrayList<String[]> modifiedLines = new ArrayList<String[]>();
				ArrayList<String[]> lines = getSalesTotalPerServiceYearMonth();
				if (lines.size() > 0) {
					String[] headerLine = new String[] { getMessagesProperties().getProperty(TAB3_TABLE_HEADER1),
							getMessagesProperties().getProperty(TAB3_TABLE_HEADER2),
							getMessagesProperties().getProperty(TAB3_TABLE_HEADER3),
							getMessagesProperties().getProperty(TAB3_TABLE_HEADER4) };
					modifiedLines.add(headerLine);
					for (String[] string : lines) {
						modifiedLines.add(string);
					}
					pdfExporter.exportTable(rootComposite.getShell(), modifiedLines,
							getMessagesProperties().getProperty(FILE_EXPORT_SALES_SERVICE_YEAR_MONTH_NAME),
							MessageFormat.format(getMessagesProperties().getProperty(PDF_DOCUMENT_TITLE_3),
									dateFrom3.isEmpty() ? getApplicationProperties().getProperty(DATE_FIRST)
											: dateFrom3,
									dateTo3.isEmpty() ? today : dateTo3),
							getIntegerArray(getMessagesProperties().getProperty(TAB3_COLUMN_WIDTHS).split(",")));
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE_TITLE),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE));
				}
			}
		});

		btnExportCsv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CsvExporter csvExporter = new CsvExporter(getApplicationProperties(), getMessagesProperties());
				ArrayList<String[]> modifiedLines = new ArrayList<String[]>();
				ArrayList<String[]> lines = getSalesTotalPerServiceYearMonth();
				if (lines.size() > 0) {
					String[] headerLine = new String[] { getMessagesProperties().getProperty(TAB3_TABLE_HEADER1),
							getMessagesProperties().getProperty(TAB3_TABLE_HEADER2),
							getMessagesProperties().getProperty(TAB3_TABLE_HEADER3),
							getMessagesProperties().getProperty(TAB3_TABLE_HEADER4) };
					modifiedLines.add(headerLine);
					for (String[] string : lines) {
						modifiedLines.add(string);
					}
					csvExporter.export(rootComposite.getShell(), modifiedLines,
							getMessagesProperties().getProperty(FILE_EXPORT_SALES_SERVICE_YEAR_MONTH_NAME));
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE_TITLE),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE));
				}
			}
		});

		return rootComposite;
	}

	Listener salesTotalPerServiceYearMonthCol1Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isSalesTotalPerServiceYearMonthCol1Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable2(tableSorter.sortByIntegerAsc(getTable2(), 0));
				setSalesTotalPerServiceYearMonthCol1Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable2(tableSorter.sortByIntegerDesc(getTable2(), 0));
				setSalesTotalPerServiceYearMonthCol1Asc(true);
			}
		}
	};

	Listener salesTotalPerServiceYearMonthCol2Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isSalesTotalPerServiceYearMonthCol2Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable2(tableSorter.sortByIntegerAsc(getTable2(), 1));
				setSalesTotalPerServiceYearMonthCol2Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable2(tableSorter.sortByIntegerDesc(getTable2(), 1));
				setSalesTotalPerServiceYearMonthCol2Asc(true);
			}
		}
	};

	Listener salesTotalPerServiceYearMonthCol3Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isSalesTotalPerServiceYearMonthCol3Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable2(tableSorter.sortByStringAsc(getTable2(), 2));
				setSalesTotalPerServiceYearMonthCol3Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable2(tableSorter.sortByStringDesc(getTable2(), 2));
				setSalesTotalPerServiceYearMonthCol3Asc(true);
			}
		}
	};

	Listener salesTotalPerServiceYearMonthCol4Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isSalesTotalPerServiceYearMonthCol4Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable2(tableSorter.sortByFloatAsc(getTable2(), 3));
				setSalesTotalPerServiceYearMonthCol4Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable2(tableSorter.sortByFloatDesc(getTable2(), 3));
				setSalesTotalPerServiceYearMonthCol4Asc(true);
			}
		}
	};

	public Composite buildSalesTotalPerYearComposite() {

		/* Hack to extract the table from the returned rootComposite */
		Composite rootComposite = buildGeneralTableComposite(getMessagesProperties().getProperty(DATE_GROUP_CAPTION),
				getMessagesProperties().getProperty(DATE_FROM), getMessagesProperties().getProperty(DATE_TO),
				getMessagesProperties().getProperty(BUTTON_QUERY),
				getMessagesProperties().getProperty(BUTTON_QUERY_TOOLTIP),
				getMessagesProperties().getProperty(BUTTON_CLEAR),
				getMessagesProperties().getProperty(BUTTON_CLEAR_TOOLTIP),
				getMessagesProperties().getProperty(TABLE_GROUP_CAPTION_4),
				getMessagesProperties().getProperty(BUTTON_PDF_PRINT),
				getMessagesProperties().getProperty(BUTTON_PDF_PRINT_TOOLTIP),
				getMessagesProperties().getProperty(BUTTON_CSV_EXPORT),
				getMessagesProperties().getProperty(BUTTON_CSV_EXPORT_TOOLTIP));

		Control[] controls = rootComposite.getChildren();
		Group firstGroup = (Group) controls[0];
		Group secondGroup = (Group) controls[1];
		Composite child1 = (Composite) secondGroup.getChildren()[0];
		DatePickerCombo dpcFrom = (DatePickerCombo) firstGroup.getChildren()[1];
		DatePickerCombo dpcTo = (DatePickerCombo) firstGroup.getChildren()[3];
		dateFrom4 = new String();
		dateTo4 = new String();
		table3 = (Table) child1.getChildren()[0];

		TableColumn tableColumn0 = new TableColumn(table3, SWT.LEFT);
		TableColumn tableColumn1 = new TableColumn(table3, SWT.LEFT);

		tableColumn0.setText(getMessagesProperties().getProperty(TAB4_TABLE_HEADER1));
		tableColumn0.setAlignment(SWT.LEFT);
		tableColumn0.addListener(SWT.Selection, salesTotalPerYearCol1Listener);
		tableColumn1.setText(getMessagesProperties().getProperty(TAB4_TABLE_HEADER2));
		tableColumn1.setAlignment(SWT.LEFT);
		tableColumn1.addListener(SWT.Selection, salesTotalPerYearCol2Listener);
		table3.setHeaderVisible(true);

		for (TableColumn tableColumn : table3.getColumns()) {
			tableColumn.pack();
		}

		table3.setLinesVisible(true);
		table3.pack();
		table3.deselectAll();
		table3.setLinesVisible(true);

		setSalesTotalPerYear(new ArrayList<String[]>());

		btnQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TabbedController tabbedController = new TabbedController(getApplicationProperties(),
						getSqlProperties());
				if (tabbedController.isDatabaseSupported()) {
					setSalesTotalPerYear(tabbedController.getSalesTotalPerYear(dateFrom4, dateTo4));
					table3.clearAll();
					table3.removeAll();

					for (String[] line : getSalesTotalPerYear()) {
						TableItem item = new TableItem(table3, SWT.HOME);
						item.setText(line);
					}

					for (TableColumn tableColumn : table3.getColumns()) {
						tableColumn.pack();
					}
					table3.redraw();
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_UNSUPPORTED_DATABASE_TITLE),
							getMessagesProperties().getProperty(MSG_UNSUPPORTED_DATABASE));
				}
			}

		});

		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dpcFrom.setDate(null);
				dateFrom4 = "";
				dpcTo.setDate(null);
				dateTo4 = "";
			}
		});

		dpcFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				dateFrom4 = dpcFrom.getText();
			}
		});

		dpcTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				dateTo4 = dpcTo.getText();
			}
		});

		btnExportPdf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PDFExporter pdfExporter = new PDFExporter(getApplicationProperties(), getMessagesProperties());
				ArrayList<String[]> modifiedLines = new ArrayList<String[]>();
				ArrayList<String[]> lines = getSalesTotalPerYear();
				if (lines.size() > 0) {
					String[] headerLine = new String[] { getMessagesProperties().getProperty(TAB4_TABLE_HEADER1),
							getMessagesProperties().getProperty(TAB4_TABLE_HEADER2) };
					modifiedLines.add(headerLine);
					for (String[] string : lines) {
						modifiedLines.add(string);
					}
					pdfExporter.exportTable(rootComposite.getShell(), modifiedLines,
							getMessagesProperties().getProperty(FILE_EXPORT_SALES_YEAR_NAME),
							MessageFormat.format(getMessagesProperties().getProperty(PDF_DOCUMENT_TITLE_4),
									dateFrom4.isEmpty() ? getApplicationProperties().getProperty(DATE_FIRST)
											: dateFrom4,
									dateTo4.isEmpty() ? today : dateTo4),
							getIntegerArray(getMessagesProperties().getProperty(TAB4_COLUMN_WIDTHS).split(",")));
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE_TITLE),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE));
				}
			}
		});

		btnExportCsv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CsvExporter csvExporter = new CsvExporter(getApplicationProperties(), getMessagesProperties());
				ArrayList<String[]> modifiedLines = new ArrayList<String[]>();
				ArrayList<String[]> lines = getSalesTotalPerYear();
				if (lines.size() > 0) {
					String[] headerLine = new String[] { getMessagesProperties().getProperty(TAB4_TABLE_HEADER1),
							getMessagesProperties().getProperty(TAB4_TABLE_HEADER2) };
					modifiedLines.add(headerLine);
					for (String[] string : lines) {
						modifiedLines.add(string);
					}
					csvExporter.export(rootComposite.getShell(), modifiedLines,
							getMessagesProperties().getProperty(FILE_EXPORT_SALES_YEAR_NAME));
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE_TITLE),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE));
				}
			}
		});

		return rootComposite;
	}

	Listener salesTotalPerYearCol1Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isSalesTotalPerYearCol1Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable3(tableSorter.sortByIntegerAsc(getTable3(), 0));
				setSalesTotalPerYearCol1Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable3(tableSorter.sortByIntegerDesc(getTable3(), 0));
				setSalesTotalPerYearCol1Asc(true);
			}
		}
	};

	Listener salesTotalPerYearCol2Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isSalesTotalPerYearCol2Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable3(tableSorter.sortByFloatAsc(getTable3(), 1));
				setSalesTotalPerYearCol2Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable3(tableSorter.sortByFloatDesc(getTable3(), 1));
				setSalesTotalPerYearCol2Asc(true);
			}
		}
	};

	public Composite buildSalesTotalPerYearMonthComposite() {

		/* Hack to extract the table from the returned rootComposite */
		Composite rootComposite = buildGeneralTableComposite(getMessagesProperties().getProperty(DATE_GROUP_CAPTION),
				getMessagesProperties().getProperty(DATE_FROM), getMessagesProperties().getProperty(DATE_TO),
				getMessagesProperties().getProperty(BUTTON_QUERY),
				getMessagesProperties().getProperty(BUTTON_QUERY_TOOLTIP),
				getMessagesProperties().getProperty(BUTTON_CLEAR),
				getMessagesProperties().getProperty(BUTTON_CLEAR_TOOLTIP),
				getMessagesProperties().getProperty(TABLE_GROUP_CAPTION_5),
				getMessagesProperties().getProperty(BUTTON_PDF_PRINT),
				getMessagesProperties().getProperty(BUTTON_PDF_PRINT_TOOLTIP),
				getMessagesProperties().getProperty(BUTTON_CSV_EXPORT),
				getMessagesProperties().getProperty(BUTTON_CSV_EXPORT_TOOLTIP));
		Control[] controls = rootComposite.getChildren();
		Group firstGroup = (Group) controls[0];
		Group secondGroup = (Group) controls[1];
		Composite child1 = (Composite) secondGroup.getChildren()[0];
		DatePickerCombo dpcFrom = (DatePickerCombo) firstGroup.getChildren()[1];
		DatePickerCombo dpcTo = (DatePickerCombo) firstGroup.getChildren()[3];
		dateFrom5 = new String();
		dateTo5 = new String();
		table4 = (Table) child1.getChildren()[0];

		TableColumn tableColumn0 = new TableColumn(table4, SWT.LEFT);
		TableColumn tableColumn1 = new TableColumn(table4, SWT.LEFT);
		TableColumn tableColumn2 = new TableColumn(table4, SWT.LEFT);

		tableColumn0.setText(getMessagesProperties().getProperty(TAB5_TABLE_HEADER1));
		tableColumn0.setAlignment(SWT.LEFT);
		tableColumn0.addListener(SWT.Selection, salesTotalPerYearMonthCol1Listener);
		tableColumn1.setText(getMessagesProperties().getProperty(TAB5_TABLE_HEADER2));
		tableColumn1.setAlignment(SWT.LEFT);
		tableColumn1.addListener(SWT.Selection, salesTotalPerYearMonthCol2Listener);
		tableColumn2.setText(getMessagesProperties().getProperty(TAB5_TABLE_HEADER3));
		tableColumn2.setAlignment(SWT.LEFT);
		tableColumn2.addListener(SWT.Selection, salesTotalPerYearMonthCol3Listener);

		table4.setHeaderVisible(true);

		for (TableColumn tableColumn : table4.getColumns()) {
			tableColumn.pack();
		}

		table4.setLinesVisible(true);
		table4.pack();
		table4.deselectAll();
		table4.setLinesVisible(true);

		setSalesTotalPerYearMonth(new ArrayList<String[]>());

		btnQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TabbedController tabbedController = new TabbedController(getApplicationProperties(),
						getSqlProperties());
				if (tabbedController.isDatabaseSupported()) {
					setSalesTotalPerYearMonth(tabbedController.getSalesTotalPerYearMonth(dateFrom5, dateTo5));
					table4.clearAll();
					table4.removeAll();

					for (String[] line : getSalesTotalPerYearMonth()) {
						TableItem item = new TableItem(table4, SWT.HOME);
						item.setText(line);
					}

					for (TableColumn tableColumn : table4.getColumns()) {
						tableColumn.pack();
					}
					table4.redraw();
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_UNSUPPORTED_DATABASE_TITLE),
							getMessagesProperties().getProperty(MSG_UNSUPPORTED_DATABASE));
				}
			}

		});

		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dpcFrom.setDate(null);
				dateFrom5 = "";
				dpcTo.setDate(null);
				dateTo5 = "";
			}
		});

		dpcFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				dateFrom5 = dpcFrom.getText();
			}
		});

		dpcTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				dateTo5 = dpcTo.getText();
			}
		});

		btnExportPdf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PDFExporter pdfExporter = new PDFExporter(getApplicationProperties(), getMessagesProperties());
				ArrayList<String[]> modifiedLines = new ArrayList<String[]>();
				ArrayList<String[]> lines = getSalesTotalPerYearMonth();
				if (lines.size() > 0) {
					String[] headerLine = new String[] { getMessagesProperties().getProperty(TAB5_TABLE_HEADER1),
							getMessagesProperties().getProperty(TAB5_TABLE_HEADER2),
							getMessagesProperties().getProperty(TAB5_TABLE_HEADER3) };
					modifiedLines.add(headerLine);
					for (String[] string : lines) {
						modifiedLines.add(string);
					}
					pdfExporter.exportTable(rootComposite.getShell(), modifiedLines,
							getMessagesProperties().getProperty(FILE_EXPORT_SALES_YEAR_MONTH_NAME),
							MessageFormat.format(getMessagesProperties().getProperty(PDF_DOCUMENT_TITLE_5),
									dateFrom5.isEmpty() ? getApplicationProperties().getProperty(DATE_FIRST)
											: dateFrom5,
									dateTo5.isEmpty() ? today : dateTo5),
							getIntegerArray(getMessagesProperties().getProperty(TAB5_COLUMN_WIDTHS).split(",")));
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE_TITLE),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE));
				}
			}
		});

		btnExportCsv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CsvExporter csvExporter = new CsvExporter(getApplicationProperties(), getMessagesProperties());
				ArrayList<String[]> modifiedLines = new ArrayList<String[]>();
				ArrayList<String[]> lines = getSalesTotalPerYearMonth();
				if (lines.size() > 0) {
					String[] headerLine = new String[] { getMessagesProperties().getProperty(TAB5_TABLE_HEADER1),
							getMessagesProperties().getProperty(TAB5_TABLE_HEADER2),
							getMessagesProperties().getProperty(TAB5_TABLE_HEADER3) };
					modifiedLines.add(headerLine);
					for (String[] string : lines) {
						modifiedLines.add(string);
					}
					csvExporter.export(rootComposite.getShell(), modifiedLines,
							getMessagesProperties().getProperty(FILE_EXPORT_SALES_YEAR_MONTH_NAME));
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE_TITLE),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE));
				}
			}
		});

		return rootComposite;
	}

	Listener salesTotalPerYearMonthCol1Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isSalesTotalPerYearMonthCol1Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable4(tableSorter.sortByIntegerAsc(getTable4(), 0));
				setSalesTotalPerYearMonthCol1Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable4(tableSorter.sortByIntegerDesc(getTable4(), 0));
				setSalesTotalPerYearMonthCol1Asc(true);
			}
		}
	};

	Listener salesTotalPerYearMonthCol2Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isSalesTotalPerYearMonthCol2Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable4(tableSorter.sortByIntegerAsc(getTable4(), 1));
				setSalesTotalPerYearMonthCol2Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable4(tableSorter.sortByIntegerDesc(getTable4(), 1));
				setSalesTotalPerYearMonthCol2Asc(true);
			}
		}
	};

	Listener salesTotalPerYearMonthCol3Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isSalesTotalPerYearMonthCol3Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable4(tableSorter.sortByFloatAsc(getTable4(), 2));
				setSalesTotalPerYearMonthCol3Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable4(tableSorter.sortByFloatDesc(getTable4(), 2));
				setSalesTotalPerYearMonthCol3Asc(true);
			}
		}
	};

	public Composite buildTarmedPerYearMonthComposite() {

		/* Hack to extract the table from the returned rootComposite */
		Composite rootComposite = buildGeneralTableComposite(getMessagesProperties().getProperty(DATE_GROUP_CAPTION),
				getMessagesProperties().getProperty(DATE_FROM), getMessagesProperties().getProperty(DATE_TO),
				getMessagesProperties().getProperty(BUTTON_QUERY),
				getMessagesProperties().getProperty(BUTTON_QUERY_TOOLTIP),
				getMessagesProperties().getProperty(BUTTON_CLEAR),
				getMessagesProperties().getProperty(BUTTON_CLEAR_TOOLTIP),
				getMessagesProperties().getProperty(TABLE_GROUP_CAPTION_6),
				getMessagesProperties().getProperty(BUTTON_PDF_PRINT),
				getMessagesProperties().getProperty(BUTTON_PDF_PRINT_TOOLTIP),
				getMessagesProperties().getProperty(BUTTON_CSV_EXPORT),
				getMessagesProperties().getProperty(BUTTON_CSV_EXPORT_TOOLTIP));
		Control[] controls = rootComposite.getChildren();
		Group firstGroup = (Group) controls[0];
		Group secondGroup = (Group) controls[1];
		Composite child1 = (Composite) secondGroup.getChildren()[0];
		DatePickerCombo dpcFrom = (DatePickerCombo) firstGroup.getChildren()[1];
		DatePickerCombo dpcTo = (DatePickerCombo) firstGroup.getChildren()[3];
		dateFrom6 = new String();
		dateTo6 = new String();
		table5 = (Table) child1.getChildren()[0];

		TableColumn tableColumn0 = new TableColumn(table5, SWT.LEFT);
		TableColumn tableColumn1 = new TableColumn(table5, SWT.LEFT);
		TableColumn tableColumn2 = new TableColumn(table5, SWT.LEFT);

		tableColumn0.setText(getMessagesProperties().getProperty(TAB6_TABLE_HEADER1));
		tableColumn0.setAlignment(SWT.LEFT);
		tableColumn0.addListener(SWT.Selection, salesTotalTarmedPerYearMonthCol1Listener);
		tableColumn1.setText(getMessagesProperties().getProperty(TAB6_TABLE_HEADER2));
		tableColumn1.setAlignment(SWT.LEFT);
		tableColumn1.addListener(SWT.Selection, salesTotalTarmedPerYearMonthCol2Listener);
		tableColumn2.setText(getMessagesProperties().getProperty(TAB6_TABLE_HEADER3));
		tableColumn2.setAlignment(SWT.LEFT);
		tableColumn2.addListener(SWT.Selection, salesTotalTarmedPerYearMonthCol3Listener);

		table5.setHeaderVisible(true);

		for (TableColumn tableColumn : table5.getColumns()) {
			tableColumn.pack();
		}

		table5.setLinesVisible(true);
		table5.pack();
		table5.deselectAll();
		table5.setLinesVisible(true);

		setSalesTotalPerYearMonth(new ArrayList<String[]>());

		btnQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TabbedController tabbedController = new TabbedController(getApplicationProperties(),
						getSqlProperties());
				if (tabbedController.isDatabaseSupported()) {
					setTarmedPerYearMonth(tabbedController.getTarmedPerYearMonth(dateFrom6, dateTo6));
					table5.clearAll();
					table5.removeAll();

					for (String[] line : getTarmedPerYearMonth()) {
						TableItem item = new TableItem(table5, SWT.HOME);
						item.setText(line);
					}

					for (TableColumn tableColumn : table5.getColumns()) {
						tableColumn.pack();
					}
					table5.redraw();
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_UNSUPPORTED_DATABASE_TITLE),
							getMessagesProperties().getProperty(MSG_UNSUPPORTED_DATABASE));
				}
			}

		});

		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dpcFrom.setDate(null);
				dateFrom6 = "";
				dpcTo.setDate(null);
				dateTo6 = "";
			}
		});

		dpcFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				dateFrom6 = dpcFrom.getText();
			}
		});

		dpcTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				dateTo6 = dpcTo.getText();
			}
		});

		btnExportPdf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PDFExporter pdfExporter = new PDFExporter(getApplicationProperties(), getMessagesProperties());
				ArrayList<String[]> modifiedLines = new ArrayList<String[]>();
				ArrayList<String[]> lines = getTarmedPerYearMonth();
				if (lines.size() > 0) {
					String[] headerLine = new String[] { getMessagesProperties().getProperty(TAB6_TABLE_HEADER1),
							getMessagesProperties().getProperty(TAB6_TABLE_HEADER2),
							getMessagesProperties().getProperty(TAB6_TABLE_HEADER3) };
					modifiedLines.add(headerLine);
					for (String[] string : lines) {
						modifiedLines.add(string);
					}
					pdfExporter.exportTable(rootComposite.getShell(), modifiedLines,
							getMessagesProperties().getProperty(FILE_EXPORT_SALES_TARMED_YEAR_MONTH_NAME),
							MessageFormat.format(getMessagesProperties().getProperty(PDF_DOCUMENT_TITLE_6),
									dateFrom6.isEmpty() ? getApplicationProperties().getProperty(DATE_FIRST)
											: dateFrom6,
									dateTo6.isEmpty() ? today : dateTo6),
							getIntegerArray(getMessagesProperties().getProperty(TAB6_COLUMN_WIDTHS).split(",")));
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE_TITLE),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE));
				}
			}
		});

		btnExportCsv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CsvExporter csvExporter = new CsvExporter(getApplicationProperties(), getMessagesProperties());
				ArrayList<String[]> modifiedLines = new ArrayList<String[]>();
				ArrayList<String[]> lines = getTarmedPerYearMonth();
				if (lines.size() > 0) {
					String[] headerLine = new String[] { getMessagesProperties().getProperty(TAB6_TABLE_HEADER1),
							getMessagesProperties().getProperty(TAB6_TABLE_HEADER2),
							getMessagesProperties().getProperty(TAB6_TABLE_HEADER3) };
					modifiedLines.add(headerLine);
					for (String[] string : lines) {
						modifiedLines.add(string);
					}
					csvExporter.export(rootComposite.getShell(), modifiedLines,
							getMessagesProperties().getProperty(FILE_EXPORT_SALES_TARMED_YEAR_MONTH_NAME));
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE_TITLE),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE));
				}
			}
		});

		return rootComposite;
	}

	Listener salesTotalTarmedPerYearMonthCol1Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isTarmedPerYearMonthCol1Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable5(tableSorter.sortByIntegerAsc(getTable5(), 0));
				setTarmedPerYearMonthCol1Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable5(tableSorter.sortByIntegerDesc(getTable5(), 0));
				setTarmedPerYearMonthCol1Asc(true);
			}
		}
	};

	Listener salesTotalTarmedPerYearMonthCol2Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isTarmedPerYearMonthCol2Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable5(tableSorter.sortByIntegerAsc(getTable5(), 1));
				setTarmedPerYearMonthCol2Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable5(tableSorter.sortByIntegerDesc(getTable5(), 1));
				setTarmedPerYearMonthCol2Asc(true);
			}
		}
	};

	Listener salesTotalTarmedPerYearMonthCol3Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isTarmedPerYearMonthCol3Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable5(tableSorter.sortByFloatAsc(getTable5(), 2));
				setTarmedPerYearMonthCol3Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable5(tableSorter.sortByFloatDesc(getTable5(), 2));
				setTarmedPerYearMonthCol3Asc(true);
			}
		}
	};

	public Composite buildMedicalPerYearMonthComposite() {

		/* Hack to extract the table from the returned rootComposite */
		Composite rootComposite = buildGeneralTableComposite(getMessagesProperties().getProperty(DATE_GROUP_CAPTION),
				getMessagesProperties().getProperty(DATE_FROM), getMessagesProperties().getProperty(DATE_TO),
				getMessagesProperties().getProperty(BUTTON_QUERY),
				getMessagesProperties().getProperty(BUTTON_QUERY_TOOLTIP),
				getMessagesProperties().getProperty(BUTTON_CLEAR),
				getMessagesProperties().getProperty(BUTTON_CLEAR_TOOLTIP),
				getMessagesProperties().getProperty(TABLE_GROUP_CAPTION_7),
				getMessagesProperties().getProperty(BUTTON_PDF_PRINT),
				getMessagesProperties().getProperty(BUTTON_PDF_PRINT_TOOLTIP),
				getMessagesProperties().getProperty(BUTTON_CSV_EXPORT),
				getMessagesProperties().getProperty(BUTTON_CSV_EXPORT_TOOLTIP));
		Control[] controls = rootComposite.getChildren();
		Group firstGroup = (Group) controls[0];
		Group secondGroup = (Group) controls[1];
		Composite child1 = (Composite) secondGroup.getChildren()[0];
		DatePickerCombo dpcFrom = (DatePickerCombo) firstGroup.getChildren()[1];
		DatePickerCombo dpcTo = (DatePickerCombo) firstGroup.getChildren()[3];
		dateFrom7 = new String();
		dateTo7 = new String();
		table6 = (Table) child1.getChildren()[0];

		TableColumn tableColumn0 = new TableColumn(table6, SWT.LEFT);
		TableColumn tableColumn1 = new TableColumn(table6, SWT.LEFT);
		TableColumn tableColumn2 = new TableColumn(table6, SWT.LEFT);

		tableColumn0.setText(getMessagesProperties().getProperty(TAB7_TABLE_HEADER1));
		tableColumn0.setAlignment(SWT.LEFT);
		tableColumn0.addListener(SWT.Selection, salesTotalMedicalPerYearMonthCol1Listener);
		tableColumn1.setText(getMessagesProperties().getProperty(TAB7_TABLE_HEADER2));
		tableColumn1.setAlignment(SWT.LEFT);
		tableColumn1.addListener(SWT.Selection, salesTotalMedicalPerYearMonthCol1Listener);
		tableColumn2.setText(getMessagesProperties().getProperty(TAB7_TABLE_HEADER3));
		tableColumn2.setAlignment(SWT.LEFT);
		tableColumn2.addListener(SWT.Selection, salesTotalMedicalPerYearMonthCol1Listener);

		table6.setHeaderVisible(true);

		for (TableColumn tableColumn : table6.getColumns()) {
			tableColumn.pack();
		}

		table6.setLinesVisible(true);
		table6.pack();
		table6.deselectAll();
		table6.setLinesVisible(true);

		setMedicalPerYearMonth(new ArrayList<String[]>());

		btnQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TabbedController tabbedController = new TabbedController(getApplicationProperties(),
						getSqlProperties());
				if (tabbedController.isDatabaseSupported()) {
					setMedicalPerYearMonth(tabbedController.getMedicalPerYearMonth(dateFrom7, dateTo7));
					table6.clearAll();
					table6.removeAll();

					for (String[] line : getMedicalPerYearMonth()) {
						TableItem item = new TableItem(table6, SWT.HOME);
						item.setText(line);
					}

					for (TableColumn tableColumn : table6.getColumns()) {
						tableColumn.pack();
					}
					table6.redraw();
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_UNSUPPORTED_DATABASE_TITLE),
							getMessagesProperties().getProperty(MSG_UNSUPPORTED_DATABASE));
				}
			}

		});
		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dpcFrom.setDate(null);
				dateFrom7 = "";
				dpcTo.setDate(null);
				dateTo7 = "";
			}
		});

		dpcFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				dateFrom7 = dpcFrom.getText();
			}
		});

		dpcTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				dateTo7 = dpcTo.getText();
			}
		});

		btnExportPdf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PDFExporter pdfExporter = new PDFExporter(getApplicationProperties(), getMessagesProperties());
				ArrayList<String[]> modifiedLines = new ArrayList<String[]>();
				ArrayList<String[]> lines = getMedicalPerYearMonth();
				if (lines.size() > 0) {
					String[] headerLine = new String[] { getMessagesProperties().getProperty(TAB7_TABLE_HEADER1),
							getMessagesProperties().getProperty(TAB7_TABLE_HEADER2),
							getMessagesProperties().getProperty(TAB7_TABLE_HEADER3) };
					modifiedLines.add(headerLine);
					for (String[] string : lines) {
						modifiedLines.add(string);
					}
					pdfExporter.exportTable(rootComposite.getShell(), modifiedLines,
							getMessagesProperties().getProperty(FILE_EXPORT_SALES_MEDICAL_YEAR_MONTH_NAME),
							MessageFormat.format(getMessagesProperties().getProperty(PDF_DOCUMENT_TITLE_7),
									dateFrom7.isEmpty() ? getApplicationProperties().getProperty(DATE_FIRST)
											: dateFrom7,
									dateTo7.isEmpty() ? today : dateTo7),
							getIntegerArray(getMessagesProperties().getProperty(TAB7_COLUMN_WIDTHS).split(",")));
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE_TITLE),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE));
				}
			}
		});

		btnExportCsv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CsvExporter csvExporter = new CsvExporter(getApplicationProperties(), getMessagesProperties());
				ArrayList<String[]> modifiedLines = new ArrayList<String[]>();
				ArrayList<String[]> lines = getMedicalPerYearMonth();
				if (lines.size() > 0) {
					String[] headerLine = new String[] { getMessagesProperties().getProperty(TAB7_TABLE_HEADER1),
							getMessagesProperties().getProperty(TAB7_TABLE_HEADER2),
							getMessagesProperties().getProperty(TAB7_TABLE_HEADER3) };
					modifiedLines.add(headerLine);
					for (String[] string : lines) {
						modifiedLines.add(string);
					}
					csvExporter.export(rootComposite.getShell(), modifiedLines,
							getMessagesProperties().getProperty(FILE_EXPORT_SALES_MEDICAL_YEAR_MONTH_NAME));
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE_TITLE),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE));
				}
			}
		});

		return rootComposite;
	}

	Listener salesTotalMedicalPerYearMonthCol1Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isMedicalPerYearMonthCol1Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable6(tableSorter.sortByIntegerAsc(getTable6(), 0));
				setMedicalPerYearMonthCol1Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable6(tableSorter.sortByIntegerDesc(getTable6(), 0));
				setMedicalPerYearMonthCol1Asc(true);
			}
		}
	};

	Listener salesTotalMedicalPerYearMonthCol2Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isMedicalPerYearMonthCol2Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable6(tableSorter.sortByIntegerAsc(getTable6(), 1));
				setMedicalPerYearMonthCol2Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable6(tableSorter.sortByIntegerDesc(getTable6(), 1));
				setMedicalPerYearMonthCol2Asc(true);
			}
		}
	};

	Listener salesTotalMedicalPerYearMonthCol3Listener = new Listener() {
		@Override
		public void handleEvent(Event e) {
			if (isMedicalPerYearMonthCol3Asc()) {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable6(tableSorter.sortByFloatAsc(getTable6(), 2));
				setMedicalPerYearMonthCol3Asc(false);
			} else {
				TableSorter tableSorter = new TableSorter(getApplicationProperties());
				setTable6(tableSorter.sortByFloatDesc(getTable6(), 2));
				setMedicalPerYearMonthCol3Asc(true);
			}
		}
	};

	public Composite buildDailyReportComposite() {

		/* Hack to extract the table from the returned rootComposite */
		Composite rootComposite = buildGeneralTableComposite(getMessagesProperties().getProperty(DATE_GROUP_CAPTION),
				getMessagesProperties().getProperty(DATE_FROM), getMessagesProperties().getProperty(DATE_TO),
				getMessagesProperties().getProperty(BUTTON_QUERY),
				getMessagesProperties().getProperty(BUTTON_QUERY_TOOLTIP),
				getMessagesProperties().getProperty(BUTTON_CLEAR),
				getMessagesProperties().getProperty(BUTTON_CLEAR_TOOLTIP),
				getMessagesProperties().getProperty(TABLE_GROUP_CAPTION_8),
				getMessagesProperties().getProperty(BUTTON_PDF_PRINT),
				getMessagesProperties().getProperty(BUTTON_PDF_PRINT_TOOLTIP),
				getMessagesProperties().getProperty(BUTTON_CSV_EXPORT),
				getMessagesProperties().getProperty(BUTTON_CSV_EXPORT_TOOLTIP));
		Control[] controls = rootComposite.getChildren();
		Group firstGroup = (Group) controls[0];
		Group secondGroup = (Group) controls[1];
		Composite child1 = (Composite) secondGroup.getChildren()[0];
		DatePickerCombo dpcFrom = (DatePickerCombo) firstGroup.getChildren()[1];
		DatePickerCombo dpcTo = (DatePickerCombo) firstGroup.getChildren()[3];
		dateFrom8 = new String();
		dateTo8 = new String();
		table7 = (Table) child1.getChildren()[0];

		TableColumn tableColumn0 = new TableColumn(table7, SWT.LEFT);
		TableColumn tableColumn1 = new TableColumn(table7, SWT.LEFT);
		TableColumn tableColumn2 = new TableColumn(table7, SWT.LEFT);
		TableColumn tableColumn3 = new TableColumn(table7, SWT.LEFT);
		TableColumn tableColumn4 = new TableColumn(table7, SWT.LEFT);
		TableColumn tableColumn5 = new TableColumn(table7, SWT.LEFT);
		TableColumn tableColumn6 = new TableColumn(table7, SWT.LEFT);
		TableColumn tableColumn7 = new TableColumn(table7, SWT.LEFT);
		TableColumn tableColumn8 = new TableColumn(table7, SWT.LEFT);
		TableColumn tableColumn9 = new TableColumn(table7, SWT.LEFT);
		TableColumn tableColumn10 = new TableColumn(table7, SWT.LEFT);
		TableColumn tableColumn11 = new TableColumn(table7, SWT.LEFT);
		TableColumn tableColumn12 = new TableColumn(table7, SWT.LEFT);
		TableColumn tableColumn13 = new TableColumn(table7, SWT.LEFT);

		tableColumn0.setText(getMessagesProperties().getProperty(TAB8_TABLE_HEADER1));
		tableColumn0.setAlignment(SWT.LEFT);

		tableColumn1.setText(getMessagesProperties().getProperty(TAB8_TABLE_HEADER2));
		tableColumn1.setAlignment(SWT.LEFT);
		tableColumn2.setText(getMessagesProperties().getProperty(TAB8_TABLE_HEADER3));
		tableColumn2.setAlignment(SWT.LEFT);
		tableColumn3.setText(getMessagesProperties().getProperty(TAB8_TABLE_HEADER4));
		tableColumn3.setAlignment(SWT.LEFT);
		tableColumn4.setText(getMessagesProperties().getProperty(TAB8_TABLE_HEADER5));
		tableColumn4.setAlignment(SWT.LEFT);
		tableColumn5.setText(getMessagesProperties().getProperty(TAB8_TABLE_HEADER6));
		tableColumn5.setAlignment(SWT.LEFT);
		tableColumn6.setText(getMessagesProperties().getProperty(TAB8_TABLE_HEADER7));
		tableColumn6.setAlignment(SWT.LEFT);
		tableColumn7.setText(getMessagesProperties().getProperty(TAB8_TABLE_HEADER8));
		tableColumn7.setAlignment(SWT.LEFT);
		tableColumn8.setText(getMessagesProperties().getProperty(TAB8_TABLE_HEADER9));
		tableColumn8.setAlignment(SWT.LEFT);
		tableColumn9.setText(getMessagesProperties().getProperty(TAB8_TABLE_HEADER10));
		tableColumn9.setAlignment(SWT.LEFT);
		tableColumn10.setText(getMessagesProperties().getProperty(TAB8_TABLE_HEADER11));
		tableColumn10.setAlignment(SWT.LEFT);
		tableColumn11.setText(getMessagesProperties().getProperty(TAB8_TABLE_HEADER12));
		tableColumn11.setAlignment(SWT.LEFT);
		tableColumn12.setText(getMessagesProperties().getProperty(TAB8_TABLE_HEADER13));
		tableColumn12.setAlignment(SWT.LEFT);
		tableColumn13.setText(getMessagesProperties().getProperty(TAB8_TABLE_HEADER14));
		tableColumn13.setAlignment(SWT.LEFT);

		table7.setHeaderVisible(true);

		for (TableColumn tableColumn : table7.getColumns()) {
			tableColumn.pack();
		}

		table7.setLinesVisible(true);
		table7.pack();
		table7.deselectAll();
		table7.setLinesVisible(true);

		setDailyReport(new ArrayList<String[]>());

		btnQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				TabbedController tabbedController = new TabbedController(getApplicationProperties(),
						getSqlProperties());
				if (tabbedController.isDatabaseSupported()) {
					setDailyReport(tabbedController.getDailyReport(dateFrom8, dateTo8));
					table7.clearAll();
					table7.removeAll();

					for (String[] line : getDailyReport()) {
						TableItem item = new TableItem(table7, SWT.HOME);
						item.setText(line);
					}

					for (TableColumn tableColumn : table7.getColumns()) {
						tableColumn.pack();
					}
					table7.redraw();
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_UNSUPPORTED_DATABASE_TITLE),
							getMessagesProperties().getProperty(MSG_UNSUPPORTED_DATABASE));
				}
			}
		});

		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dpcFrom.setDate(null);
				dateFrom8 = "";
				dpcTo.setDate(null);
				dateTo8 = "";
			}
		});

		dpcFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				dateFrom8 = dpcFrom.getText();
			}
		});

		dpcTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				dateTo8 = dpcTo.getText();
			}
		});

		btnExportPdf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PDFExporter pdfExporter = new PDFExporter(getApplicationProperties(), getMessagesProperties());
				ArrayList<String[]> modifiedLines = new ArrayList<String[]>();
				ArrayList<String[]> lines = getDailyReport();
				if (lines.size() > 0) {
					String[] headerLine = new String[] { getMessagesProperties().getProperty(TAB8_TABLE_HEADER1),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER2),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER3),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER4),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER5),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER6),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER7),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER8),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER9),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER10),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER11),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER12),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER13) };
					modifiedLines.add(headerLine);
					for (String[] string : lines) {
						modifiedLines.add(string);
					}
					pdfExporter.exportReport(rootComposite.getShell(),lines,
							MessageFormat.format(getMessagesProperties().getProperty(PDF_DOCUMENT_TITLE_8),
									dateFrom8.isEmpty() ? getApplicationProperties().getProperty(DATE_FIRST)
											: dateFrom8,
									dateTo8.isEmpty() ? today : dateTo8),
							dateFrom8.isEmpty() ? getApplicationProperties().getProperty(DATE_FIRST) : dateFrom8,
							dateTo8.isEmpty() ? today : dateTo8,getMessagesProperties().getProperty(FILE_EXPORT_DAILY_REPORT_NAME));
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE_TITLE),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE));
				}
			}
		});

		btnExportCsv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CsvExporter csvExporter = new CsvExporter(getApplicationProperties(), getMessagesProperties());
				ArrayList<String[]> modifiedLines = new ArrayList<String[]>();
				ArrayList<String[]> lines = getDailyReport();
				if (lines.size() > 0) {
					String[] headerLine = new String[] { getMessagesProperties().getProperty(TAB8_TABLE_HEADER1),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER2),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER3),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER4),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER5),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER6),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER7),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER8),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER9),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER10),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER11),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER12),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER13),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER14),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER15),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER16),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER17),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER18),
							getMessagesProperties().getProperty(TAB8_TABLE_HEADER19) };
					modifiedLines.add(headerLine);
					for (String[] string : lines) {
						modifiedLines.add(string);
					}
					csvExporter.export(rootComposite.getShell(), modifiedLines,
							getMessagesProperties().getProperty(FILE_EXPORT_DAILY_REPORT_NAME));
				} else {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE_TITLE),
							getMessagesProperties().getProperty(MSG_EMPTY_TABLE));
				}
			}
		});

		return rootComposite;
	}

	/* General Methods */
	private Composite buildGeneralTableComposite(String dateGroupCaption, String dateFrom, String dateTo,
			String buttonQuery, String buttonQueryTooltip, String buttonClear, String buttonClearTooltip,
			String tableGroupCaption, String buttonPDFprint, String buttonPDFprintTooltip, String buttonCSVexport,
			String buttonCSVexportTooltip) {
		GridLayout rootLayout = new GridLayout();
		rootLayout.numColumns = 1;

		GridLayout dateGroupLayout = new GridLayout();
		dateGroupLayout.numColumns = 1;

		GridLayout refreshButtonsLayout = new GridLayout();
		refreshButtonsLayout.numColumns = 2;

		GridLayout tableCompositeLayout = new GridLayout();
		tableCompositeLayout.numColumns = 1;

		GridLayout exportButtonsLayout = new GridLayout();
		exportButtonsLayout.numColumns = 2;

		GridData rootGridData = new GridData(SWT.FILL, SWT.NONE, true, false);

		GridData buttonGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		buttonGridData.heightHint = 50;

		GridData tableCompositeGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		GridData tableGroupGridData = new GridData(SWT.FILL, SWT.FILL, true, true);

		GridData exportButtonsCompositeGridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		exportButtonsCompositeGridData.heightHint = 50;

		GridData datePickerGridData = new GridData(SWT.FILL, SWT.TOP, true, true);
		datePickerGridData.heightHint = 19;

		Composite rootComposite = new Composite(composite, SWT.NONE);
		rootComposite.setLayout(rootLayout);
		rootComposite.setLayoutData(rootGridData);

		Group dateGroup = new Group(rootComposite, SWT.FILL);
		dateGroup.setText(dateGroupCaption);
		dateGroup.setLayout(dateGroupLayout);
		dateGroup.setLayoutData(rootGridData);

		Label lblDateFrom = new Label(dateGroup, SWT.FILL);
		lblDateFrom.setText(dateFrom);

		dpcDateFrom = new DatePickerCombo(dateGroup, SWT.BORDER);
		dpcDateFrom.setLayoutData(datePickerGridData);

		Label lblDateTo = new Label(dateGroup, SWT.FILL);
		lblDateTo.setText(dateTo);

		dpcDateTo = new DatePickerCombo(dateGroup, SWT.BORDER);
		dpcDateTo.setLayoutData(datePickerGridData);
		dpcDateTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
			}
		});

		Composite refreshButtonsComposite = new Composite(dateGroup, SWT.NONE);
		refreshButtonsComposite.setLayout(refreshButtonsLayout);
		refreshButtonsComposite.setLayoutData(buttonGridData);

		btnQuery = new Button(refreshButtonsComposite, SWT.PUSH);
		btnQuery.setText(buttonQuery);
		btnQuery.setToolTipText(buttonQueryTooltip);
		btnQuery.setLayoutData(buttonGridData);

		btnClear = new Button(refreshButtonsComposite, SWT.PUSH);
		btnClear.setText(buttonClear);
		btnClear.setToolTipText(buttonClearTooltip);
		btnClear.setLayoutData(buttonGridData);

		GridLayout exportGroupLayout = new GridLayout();
		exportGroupLayout.numColumns = 1;

		Group tableGroup = new Group(rootComposite, SWT.NONE);
		tableGroup.setText(tableGroupCaption);
		tableGroup.setLayout(exportGroupLayout);
		tableGroup.setLayoutData(tableGroupGridData);

		GridData tableGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		tableGridData.heightHint = tableGroup.getSize().y;

		Composite tableComposite = new Composite(tableGroup, SWT.NONE);
		tableComposite.setLayout(tableCompositeLayout);
		tableComposite.setLayoutData(tableCompositeGridData);

		Table table = new Table(tableComposite,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setLayoutData(tableGridData);
		table.setLinesVisible(true);
		table.setVisible(true);

		Composite exportButtonsComposite = new Composite(tableGroup, SWT.NONE);
		exportButtonsComposite.setLayout(exportButtonsLayout);
		exportButtonsComposite.setLayoutData(exportButtonsCompositeGridData);

		btnExportPdf = new Button(exportButtonsComposite, SWT.PUSH);
		btnExportPdf.setText(buttonPDFprint);
		btnExportPdf.setToolTipText(buttonPDFprintTooltip);
		btnExportPdf.setLayoutData(buttonGridData);

		btnExportCsv = new Button(exportButtonsComposite, SWT.PUSH);
		btnExportCsv.setText(buttonCSVexport);
		btnExportCsv.setToolTipText(buttonCSVexportTooltip);
		btnExportCsv.setLayoutData(buttonGridData);

		return rootComposite;
	}

	private int[] getIntegerArray(String[] columnWidthsStr) {
		int size = columnWidthsStr.length;
		int[] columnWidthsInt = new int[size];
		for (int i = 0; i < size; i++) {
			columnWidthsInt[i] = Integer.parseInt(columnWidthsStr[i]);
		}
		return columnWidthsInt;
	}

	public Composite getComposite() {
		return composite;
	}

	public void setComposite(Composite composite) {
		this.composite = composite;
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

	public Properties getSqlProperties() {
		return sqlProperties;
	}

	public void setSqlProperties(Properties sqlProperties) {
		this.sqlProperties = sqlProperties;
	}

	public ArrayList<String[]> getSalesTotalPerService() {
		return salesTotalPerService;
	}

	public void setSalesTotalPerService(ArrayList<String[]> salesTotalPerService) {
		this.salesTotalPerService = salesTotalPerService;
	}

	public ArrayList<String[]> getSalesTotalPerYear() {
		return salesTotalPerYear;
	}

	public void setSalesTotalPerYear(ArrayList<String[]> salesTotalPerYear) {
		this.salesTotalPerYear = salesTotalPerYear;
	}

	public ArrayList<String[]> getSalesTotalPerYearMonth() {
		return salesTotalPerYearMonth;
	}

	public void setSalesTotalPerYearMonth(ArrayList<String[]> salesTotalPerYearMonth) {
		this.salesTotalPerYearMonth = salesTotalPerYearMonth;
	}

	public ArrayList<String[]> getTarmedPerYearMonth() {
		return tarmedPerYearMonth;
	}

	public void setTarmedPerYearMonth(ArrayList<String[]> tarmedPerYearMonth) {
		this.tarmedPerYearMonth = tarmedPerYearMonth;
	}

	public ArrayList<String[]> getMedicalPerYearMonth() {
		return medicalPerYearMonth;
	}

	public void setMedicalPerYearMonth(ArrayList<String[]> medicalPerYearMonth) {
		this.medicalPerYearMonth = medicalPerYearMonth;
	}

	public ArrayList<String[]> getDailyReport() {
		return dailyReport;
	}

	public void setDailyReport(ArrayList<String[]> dailyReport) {
		this.dailyReport = dailyReport;
	}

	public ArrayList<String[]> getSalesTotalPerServiceYear() {
		return salesTotalPerServiceYear;
	}

	public void setSalesTotalPerServiceYear(ArrayList<String[]> salesTotalPerServiceYear) {
		this.salesTotalPerServiceYear = salesTotalPerServiceYear;
	}

	public ArrayList<String[]> getSalesTotalPerServiceYearMonth() {
		return salesTotalPerServiceYearMonth;
	}

	public void setSalesTotalPerServiceYearMonth(ArrayList<String[]> salesTotalPerServiceYearMonth) {
		this.salesTotalPerServiceYearMonth = salesTotalPerServiceYearMonth;
	}

	public boolean isSalesTotalPerServiceCol1Asc() {
		return salesTotalPerServiceCol1Asc;
	}

	public void setSalesTotalPerServiceCol1Asc(boolean salesTotalPerServiceCol1Asc) {
		this.salesTotalPerServiceCol1Asc = salesTotalPerServiceCol1Asc;
	}

	public boolean isSalesTotalPerServiceCol2Asc() {
		return salesTotalPerServiceCol2Asc;
	}

	public void setSalesTotalPerServiceCol2Asc(boolean salesTotalPerServiceCol2Asc) {
		this.salesTotalPerServiceCol2Asc = salesTotalPerServiceCol2Asc;
	}

	public boolean isSalesTotalPerServiceYearCol1Asc() {
		return salesTotalPerServiceYearCol1Asc;
	}

	public void setSalesTotalPerServiceYearCol1Asc(boolean salesTotalPerServiceYearCol1Asc) {
		this.salesTotalPerServiceYearCol1Asc = salesTotalPerServiceYearCol1Asc;
	}

	public boolean isSalesTotalPerServiceYearCol2Asc() {
		return salesTotalPerServiceYearCol2Asc;
	}

	public void setSalesTotalPerServiceYearCol2Asc(boolean salesTotalPerServiceYearCol2Asc) {
		this.salesTotalPerServiceYearCol2Asc = salesTotalPerServiceYearCol2Asc;
	}

	public boolean isSalesTotalPerServiceYearCol3Asc() {
		return salesTotalPerServiceYearCol3Asc;
	}

	public void setSalesTotalPerServiceYearCol3Asc(boolean salesTotalPerServiceYearCol3Asc) {
		this.salesTotalPerServiceYearCol3Asc = salesTotalPerServiceYearCol3Asc;
	}

	public boolean isSalesTotalPerServiceYearMonthCol1Asc() {
		return salesTotalPerServiceYearMonthCol1Asc;
	}

	public void setSalesTotalPerServiceYearMonthCol1Asc(boolean salesTotalPerServiceYearMonthCol1Asc) {
		this.salesTotalPerServiceYearMonthCol1Asc = salesTotalPerServiceYearMonthCol1Asc;
	}

	public boolean isSalesTotalPerServiceYearMonthCol2Asc() {
		return salesTotalPerServiceYearMonthCol2Asc;
	}

	public void setSalesTotalPerServiceYearMonthCol2Asc(boolean salesTotalPerServiceYearMonthCol2Asc) {
		this.salesTotalPerServiceYearMonthCol2Asc = salesTotalPerServiceYearMonthCol2Asc;
	}

	public boolean isSalesTotalPerServiceYearMonthCol3Asc() {
		return salesTotalPerServiceYearMonthCol3Asc;
	}

	public void setSalesTotalPerServiceYearMonthCol3Asc(boolean salesTotalPerServiceYearMonthCol3Asc) {
		this.salesTotalPerServiceYearMonthCol3Asc = salesTotalPerServiceYearMonthCol3Asc;
	}

	public boolean isSalesTotalPerServiceYearMonthCol4Asc() {
		return salesTotalPerServiceYearMonthCol4Asc;
	}

	public void setSalesTotalPerServiceYearMonthCol4Asc(boolean salesTotalPerServiceYearMonthCol4Asc) {
		this.salesTotalPerServiceYearMonthCol4Asc = salesTotalPerServiceYearMonthCol4Asc;
	}

	public boolean isSalesTotalPerYearCol2Asc() {
		return salesTotalPerYearCol2Asc;
	}

	public void setSalesTotalPerYearCol2Asc(boolean salesTotalPerYearCol2Asc) {
		this.salesTotalPerYearCol2Asc = salesTotalPerYearCol2Asc;
	}

	public boolean isSalesTotalPerYearMonthCol1Asc() {
		return salesTotalPerYearMonthCol1Asc;
	}

	public void setSalesTotalPerYearMonthCol1Asc(boolean salesTotalPerYearMonthCol1Asc) {
		this.salesTotalPerYearMonthCol1Asc = salesTotalPerYearMonthCol1Asc;
	}

	public boolean isSalesTotalPerYearMonthCol2Asc() {
		return salesTotalPerYearMonthCol2Asc;
	}

	public void setSalesTotalPerYearMonthCol2Asc(boolean salesTotalPerYearMonthCol2Asc) {
		this.salesTotalPerYearMonthCol2Asc = salesTotalPerYearMonthCol2Asc;
	}

	public boolean isSalesTotalPerYearMonthCol3Asc() {
		return salesTotalPerYearMonthCol3Asc;
	}

	public void setSalesTotalPerYearMonthCol3Asc(boolean salesTotalPerYearMonthCol3Asc) {
		this.salesTotalPerYearMonthCol3Asc = salesTotalPerYearMonthCol3Asc;
	}

	public boolean isTarmedPerYearMonthCol1Asc() {
		return tarmedPerYearMonthCol1Asc;
	}

	public void setTarmedPerYearMonthCol1Asc(boolean tarmedPerYearMonthCol1Asc) {
		this.tarmedPerYearMonthCol1Asc = tarmedPerYearMonthCol1Asc;
	}

	public boolean isTarmedPerYearMonthCol2Asc() {
		return tarmedPerYearMonthCol2Asc;
	}

	public void setTarmedPerYearMonthCol2Asc(boolean tarmedPerYearMonthCol2Asc) {
		this.tarmedPerYearMonthCol2Asc = tarmedPerYearMonthCol2Asc;
	}

	public boolean isTarmedPerYearMonthCol3Asc() {
		return tarmedPerYearMonthCol3Asc;
	}

	public void setTarmedPerYearMonthCol3Asc(boolean tarmedPerYearMonthCol3Asc) {
		this.tarmedPerYearMonthCol3Asc = tarmedPerYearMonthCol3Asc;
	}

	public boolean isMedicalPerYearMonthCol1Asc() {
		return medicalPerYearMonthCol1Asc;
	}

	public void setMedicalPerYearMonthCol1Asc(boolean medicalPerYearMonthCol1Asc) {
		this.medicalPerYearMonthCol1Asc = medicalPerYearMonthCol1Asc;
	}

	public boolean isMedicalPerYearMonthCol2Asc() {
		return medicalPerYearMonthCol2Asc;
	}

	public void setMedicalPerYearMonthCol2Asc(boolean medicalPerYearMonthCol2Asc) {
		this.medicalPerYearMonthCol2Asc = medicalPerYearMonthCol2Asc;
	}

	public boolean isMedicalPerYearMonthCol3Asc() {
		return medicalPerYearMonthCol3Asc;
	}

	public void setMedicalPerYearMonthCol3Asc(boolean medicalPerYearMonthCol3Asc) {
		this.medicalPerYearMonthCol3Asc = medicalPerYearMonthCol3Asc;
	}

	public Table getTable0() {
		return table0;
	}

	public void setTable0(Table table0) {
		this.table0 = table0;
	}

	public Table getTable1() {
		return table1;
	}

	public void setTable1(Table table1) {
		this.table1 = table1;
	}

	public Table getTable2() {
		return table2;
	}

	public void setTable2(Table table2) {
		this.table2 = table2;
	}

	public Table getTable3() {
		return table3;
	}

	public void setTable3(Table table3) {
		this.table3 = table3;
	}

	public Table getTable4() {
		return table4;
	}

	public void setTable4(Table table4) {
		this.table4 = table4;
	}

	public Table getTable5() {
		return table5;
	}

	public void setTable5(Table table5) {
		this.table5 = table5;
	}

	public Table getTable6() {
		return table6;
	}

	public void setTable6(Table table6) {
		this.table6 = table6;
	}

	public Table getTable7() {
		return table7;
	}

	public void setTable7(Table table7) {
		this.table7 = table7;
	}

	public Table getTable8() {
		return table8;
	}

	public void setTable8(Table table8) {
		this.table8 = table8;
	}

	public boolean isSalesTotalPerYearCol1Asc() {
		return salesTotalPerYearCol1Asc;
	}

	public void setSalesTotalPerYearCol1Asc(boolean salesTotalPerYearCol1Asc) {
		this.salesTotalPerYearCol1Asc = salesTotalPerYearCol1Asc;
	}
}
