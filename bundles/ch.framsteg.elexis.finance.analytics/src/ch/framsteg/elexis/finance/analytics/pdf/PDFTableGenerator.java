package ch.framsteg.elexis.finance.analytics.pdf;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;

public class PDFTableGenerator {

	private String reportingDateTime;
	private String reportTitle;
	private String mandantInfo;
	private PDType1Font cellFont;
	private float cellFontSize;
	private int columnWidth;

	// Generates document from Table object
	public void generatePDF(PDFTable table) throws IOException {
		PDDocument doc = null;
		try {
			doc = new PDDocument();
			drawTable(doc, table);
			doc.save("sample.pdf");
		} finally {
			if (doc != null) {
				doc.close();
			}
		}
	}

	public PDDocument deliverPDF(PDFTable table) throws IOException {
		//table.getColumns().forEach(a -> System.out.println(a));
		PDDocument doc = null;
		doc = new PDDocument();
		drawTable(doc, table);
		return doc;
	}

	// Configures basic setup for the table and draws it page by page
	public void drawTable(PDDocument doc, PDFTable table) throws IOException {
		// Calculate pagination
		Integer rowsPerPage = Double.valueOf(Math.floor(table.getHeight() / table.getRowHeight())).intValue() - 1; // subtract
		Integer numberOfPages = Double.valueOf(Math.ceil(table.getNumberOfRows().floatValue() / rowsPerPage))
				.intValue();

		// Generate each page, get the content and draw it
		for (int pageCount = 0; pageCount < numberOfPages; pageCount++) {
			PDPage page = generatePage(doc, table);
			PDPageContentStream contentStream = generateContentStream(doc, page, table);

			contentStream.beginText();
		
			contentStream.setFont(PDType1Font.HELVETICA, 8);
			contentStream.newLineAtOffset(80, 80);
			contentStream.showText(getMandantInfo());
			contentStream.newLineAtOffset(420, 0);
			contentStream.showText(pageCount + 1 + "/" + numberOfPages);
			contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
			contentStream.newLineAtOffset(-420, 680);
			contentStream.showText(getReportTitle());
			contentStream.setFont(PDType1Font.HELVETICA, 8);
			contentStream.newLineAtOffset(385, 0);
			contentStream.showText(getReportingDateTime());
			contentStream.endText();
			contentStream.setFont(PDType1Font.HELVETICA, 10);
			// contentStream.moveTo(100, 100);

			String[][] currentPageContent = getContentForCurrentPage(table, rowsPerPage, pageCount);
			drawCurrentPage(table, currentPageContent, contentStream);

		}
	}

	// Draws current page table grid and border lines and content
	private void drawCurrentPage(PDFTable table, String[][] currentPageContent, PDPageContentStream contentStream)
			throws IOException {

		float tableTopY = table.isLandscape() ? table.getPageSize().getWidth() - table.getMargin()
				: table.getPageSize().getHeight() - table.getMargin();

		tableTopY = tableTopY - 40;

		// Draws grid and borders
		drawTableGrid(table, currentPageContent, contentStream, tableTopY);

		// Position cursor to start drawing content
		float nextTextX = table.getMargin() + table.getCellMargin();
		// Calculate center alignment for text in cell considering font height
		float nextTextY = tableTopY - (table.getRowHeight() / 2)
				- ((table.getTextFont().getFontDescriptor().getFontBoundingBox().getHeight() / 1000
						* table.getFontSize()) / 4);

		// Write column headers
		writeContentLine(table.getColumnsNamesAsArray(), contentStream, nextTextX, nextTextY, table);
		nextTextY -= table.getRowHeight();
		nextTextX = table.getMargin() + table.getCellMargin();

		// Write content
		for (int i = 0; i < currentPageContent.length; i++) {
			writeContentLine(currentPageContent[i], contentStream, nextTextX, nextTextY, table);
			nextTextY -= table.getRowHeight();
			nextTextX = table.getMargin() + table.getCellMargin();
		}
		contentStream.close();
	}

	// Writes the content for one line
	private void writeContentLine(String[] lineContent, PDPageContentStream contentStream, float nextTextX,
			float nextTextY, PDFTable table) throws IOException {

		// Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
		Pattern pattern = Pattern.compile("^[-+]?[0-9]*\\.[0-9]+$");

		for (int i = 0; i < table.getNumberOfColumns(); i++) {
			String text = lineContent[i];
			if (text != null) {
				contentStream.beginText();
				if (text != null) {
					if (pattern.matcher(text).matches()) {
						nextTextX = (nextTextX - getTextWidth(getCellFont(), getCellFontSize(), text) + getColumnWidth()
								- 5);
						//nextTextX = (nextTextX - getTextWidth(getCellFont(), getCellFontSize(), text));
					}
				}
				contentStream.newLineAtOffset(nextTextX, nextTextY);
				contentStream.showText(text != null ? text : "");
				contentStream.endText();
				nextTextX += table.getColumns().get(i).getWidth();
			}
		}
	}

	private static float getTextWidth(PDType1Font font, float fontSize, String text) throws IOException {
		return (font.getStringWidth(text) / 1000.0f) * fontSize;
	}

	private void drawTableGrid(PDFTable table, String[][] currentPageContent, PDPageContentStream contentStream,
			float tableTopY) throws IOException {
		// Draw row lines
		float nextY = tableTopY;
		for (int i = 0; i <= currentPageContent.length + 1; i++) {
			contentStream.moveTo(table.getMargin(), nextY);
			contentStream.lineTo(table.getMargin() + table.getWidth(), nextY);
			contentStream.stroke();
			nextY -= table.getRowHeight();
		}

		// Draw column lines
		final float tableYLength = table.getRowHeight() + (table.getRowHeight() * currentPageContent.length);
		final float tableBottomY = tableTopY - tableYLength;
		float nextX = table.getMargin();
		for (int i = 0; i < table.getNumberOfColumns(); i++) {
			contentStream.moveTo(nextX, tableTopY);
			contentStream.lineTo(nextX, tableBottomY);
			contentStream.stroke();
			nextX += table.getColumns().get(i).getWidth();
		}
		contentStream.moveTo(nextX, tableTopY);
		contentStream.lineTo(nextX, tableBottomY);
		contentStream.stroke();
	}

	private String[][] getContentForCurrentPage(PDFTable table, Integer rowsPerPage, int pageCount) {
		int startRange = pageCount * rowsPerPage;
		int endRange = (pageCount * rowsPerPage) + rowsPerPage;
		if (endRange > table.getNumberOfRows()) {
			endRange = table.getNumberOfRows();
		}
		return Arrays.copyOfRange(table.getContent(), startRange, endRange);
	}

	private PDPage generatePage(PDDocument doc, PDFTable table) throws IOException {
		PDPage page = new PDPage();
		page.setMediaBox(table.getPageSize());
		page.setRotation(table.isLandscape() ? 90 : 0);
		doc.addPage(page);
		return page;
	}

	private PDPageContentStream generateContentStream(PDDocument doc, PDPage page, PDFTable table) throws IOException {
		// PDPageContentStream contentStream = new PDPageContentStream(doc, page, false,
		// false);
		PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND,
				false);
		// User transformation matrix to change the reference when drawing.
		// This is necessary for the landscape position to draw correctly
		if (table.isLandscape()) {
			contentStream.transform(new Matrix(0, 1, -1, 0, table.getPageSize().getWidth(), 0));
		}
		contentStream.setFont(table.getTextFont(), table.getFontSize());
		return contentStream;
	}

	public String getReportingDateTime() {
		return reportingDateTime;
	}

	public void setReportingDateTime(String reportingDateTime) {
		this.reportingDateTime = reportingDateTime;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public String getMandantInfo() {
		return mandantInfo;
	}

	public void setMandantInfo(String mandantInfo) {
		this.mandantInfo = mandantInfo;
	}

	public PDType1Font getCellFont() {
		return cellFont;
	}

	public void setCellFont(PDType1Font cellFont) {
		this.cellFont = cellFont;
	}

	public float getCellFontSize() {
		return cellFontSize;
	}

	public void setCellFontSize(float cellFontSize) {
		this.cellFontSize = cellFontSize;
	}

	public int getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(int columnWidth) {
		this.columnWidth = columnWidth;
	}
}
