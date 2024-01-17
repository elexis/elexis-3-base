package ch.framsteg.elexis.finance.analytics.export;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Mandant;
import ch.framsteg.elexis.finance.analytics.beans.Day;
import ch.framsteg.elexis.finance.analytics.beans.TreeBuilder;
import ch.framsteg.elexis.finance.analytics.pdf.PDFColumn;
import ch.framsteg.elexis.finance.analytics.pdf.PDFPageGenerator;
import ch.framsteg.elexis.finance.analytics.pdf.PDFTableGenerator;
import ch.framsteg.elexis.finance.analytics.pdf.PDFTable;
import ch.framsteg.elexis.finance.analytics.pdf.PDFTableBuilder;

public class PDFExporter {

	private Properties applicationProperties;
	private Properties messagesProperties;

	private final static String DATE_TIME_FORMAT = "date.format";
	private final static String DATE_TIME_FORMAT_DOT = "date.format.dot";

	private final static String EXTENSION_ALL="file.save.dialog.extension.all";
	private final static String EXTENSION_PDF="file.save.dialog.extension.pdf";
	private final static String EXTENSION_PDF_SHORT="file.save.dialog.extension.pdf.short";
	
	private final static String FILTER_ALL="file.save.dialog.filter.all";
	private final static String FILTER_PDF="file.save.dialog.filter.pdf";
	
	private final static String DASH="-";
	
	public PDFExporter(Properties applicationProperties, Properties messagesProperties) {
		setApplicationProperties(applicationProperties);
		setMessagesProperties(messagesProperties);
	}

	public void exportTable(Shell shell, ArrayList<String[]> lines, String filenamePart, String documentTitle,
			int[] columnWidths) {

		Mandant currentMandant = ElexisEventDispatcher.getSelectedMandator();
		String postAnschrift = currentMandant.getPostAnschrift();

		Display display = shell.getDisplay();

		// Page configuration
		PDRectangle PAGE_SIZE = new PDPage(PDRectangle.A4).getMediaBox();
		float MARGIN = 80;
		boolean IS_LANDSCAPE = false;

		int columnWidth = 0;

		// Font configuration
		PDFont TEXT_FONT = PDType1Font.HELVETICA;
		float FONT_SIZE = 10;

		// Table configuration
		float ROW_HEIGHT = 15;
		float CELL_MARGIN = 2;

		List<PDFColumn> columns = new ArrayList<PDFColumn>();
		String[] line = lines.get(0);
		for (int a = 0; a < line.length; a++) {
			columns.add(new PDFColumn(line[a], columnWidths[a]));
			if (a == line.length - 1) {
				columnWidth = columnWidths[a];
			}
		}

		String[][] content = new String[lines.size()][line.length];

		for (int i = 1; i < lines.size(); i++) {
			for (int j = 0; j < line.length; j++) {
				content[i][j] = lines.get(i)[j];
			}
		}

		float tableHeight = IS_LANDSCAPE ? PAGE_SIZE.getWidth() - (2 * MARGIN) : PAGE_SIZE.getHeight() - (2 * MARGIN);

		tableHeight = tableHeight - 80;

		PDFTable table = new PDFTableBuilder().setCellMargin(CELL_MARGIN).setColumns(columns).setContent(content)
				.setHeight(tableHeight).setNumberOfRows(content.length).setRowHeight(ROW_HEIGHT).setMargin(MARGIN)
				.setPageSize(PAGE_SIZE).setLandscape(IS_LANDSCAPE).setTextFont(TEXT_FONT).setFontSize(FONT_SIZE)
				.build();

		SimpleDateFormat formatter = new SimpleDateFormat(getApplicationProperties().getProperty(DATE_TIME_FORMAT_DOT));
		Date date = new Date();

		PDFTableGenerator pdfTableGenerator = new PDFTableGenerator();
		pdfTableGenerator.setReportingDateTime(formatter.format(date));
		pdfTableGenerator.setReportTitle(documentTitle);
		pdfTableGenerator.setMandantInfo(postAnschrift);

		pdfTableGenerator.setCellFont(PDType1Font.HELVETICA);
		pdfTableGenerator.setCellFontSize(10);
		pdfTableGenerator.setColumnWidth(columnWidth);

		try {
			PDDocument document = pdfTableGenerator.deliverPDF(table);

			DateTimeFormatter dateTimeFormatter = DateTimeFormatter
					.ofPattern(getApplicationProperties().getProperty(DATE_TIME_FORMAT));

			LocalDateTime now = LocalDateTime.now();
			String datePart = dateTimeFormatter.format(now);

			FileDialog dialog = new FileDialog(shell, SWT.SAVE);

			dialog.setOverwrite(true);
			dialog.setFilterNames(new String[] { getApplicationProperties().getProperty(FILTER_PDF), getApplicationProperties().getProperty(FILTER_ALL) });
			dialog.setFilterExtensions(new String[] {getApplicationProperties().getProperty(EXTENSION_PDF), getApplicationProperties().getProperty(EXTENSION_ALL) });

			dialog.setFilterPath(System.getProperty("user.home"));
			dialog.setFileName(datePart + DASH + filenamePart + getApplicationProperties().getProperty(EXTENSION_PDF_SHORT));
			dialog.open();

			document.save(dialog.getFilterPath() + System.getProperty("file.separator") + dialog.getFileName());
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exportReport(Shell shell, ArrayList<String[]> lines, String documentTitle, String from, String to,
			String filenamePart) {

		Display display = shell.getDisplay();

		TreeBuilder treeBuilder = new TreeBuilder();
		treeBuilder.buildHierarchy(lines);
		TreeMap<String, Day> days = treeBuilder.getHierarchy();

		Mandant currentMandant = ElexisEventDispatcher.getSelectedMandator();
		String postAnschrift = currentMandant.getPostAnschrift();

		SimpleDateFormat formatter = new SimpleDateFormat(getApplicationProperties().getProperty(DATE_TIME_FORMAT_DOT));
		Date date = new Date();

		PDFPageGenerator pdfPageGenerator = new PDFPageGenerator(postAnschrift, documentTitle, formatter.format(date),
				getApplicationProperties(), getMessagesProperties());
		PDDocument document = pdfPageGenerator.generateDailyReport(days, postAnschrift, documentTitle,
				formatter.format(date), lines.size(), from, to);
		try {
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter
					.ofPattern(getApplicationProperties().getProperty(DATE_TIME_FORMAT));

			LocalDateTime now = LocalDateTime.now();
			String datePart = dateTimeFormatter.format(now);

			FileDialog dialog = new FileDialog(shell, SWT.SAVE);

			dialog.setOverwrite(true);
			dialog.setFilterNames(new String[] { getApplicationProperties().getProperty(FILTER_PDF), getApplicationProperties().getProperty(FILTER_ALL) });
			dialog.setFilterExtensions(new String[] { getApplicationProperties().getProperty(EXTENSION_PDF), getApplicationProperties().getProperty(EXTENSION_ALL) });

			dialog.setFilterPath(System.getProperty("user.home"));
			dialog.setFileName(datePart + DASH + filenamePart + getApplicationProperties().getProperty(EXTENSION_PDF_SHORT));
			dialog.open();

			document.save(dialog.getFilterPath() + System.getProperty("file.separator") + dialog.getFileName());
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

}
