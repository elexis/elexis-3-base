package at.medevit.elexis.agenda.ui.utils;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.line.LineStyle;

public class PdfUtils {

	public static void saveFile(FileOutputStream fout, List<Map<String, String>> appointments,
			Map<String, String> colors) throws IOException {
		appointments.sort(Comparator.comparing(a -> a.get("Von")));
		try (PDDocument document = new PDDocument()) {
			PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
			document.addPage(page);
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			float yPosDescriptionEnd = writeHeader(contentStream, page, appointments);
			contentStream.close();
			createDataTable(document, appointments, colors, yPosDescriptionEnd);
			addPageNumbers(document);
			document.save(fout);
		}
	}

	private static void createDataTable(PDDocument doc, List<Map<String, String>> appointments,
			Map<String, String> colors, float startY) throws IOException {
		PDPage page = doc.getPage(0);
		float margin = 50;
		float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
		float yPosition = startY;

		List<Map<String, String>> allDayAppointments = appointments.stream().filter(m -> m.get("Bis") == null)
				.collect(Collectors.toList());
		BaseTable allDayTable = new BaseTable(yPosition, startY, margin, tableWidth, margin, doc, page, true, true);
		addAllDayTableHeader(allDayTable);
		for (Map<String, String> appointment : allDayAppointments) {
			float rowHeight = addAllDayAppointmentRow(appointment, allDayTable, tableWidth, colors);
			yPosition -= rowHeight;
		}
		allDayTable.draw();

		yPosition -= 30;

		List<Map<String, String>> dayAppointments = appointments.stream().filter(m -> m.get("Bis") != null)
				.collect(Collectors.toList());

		BaseTable table = new BaseTable(yPosition, yPosition, margin, tableWidth, margin, doc, page, true, true);
		addTableHeader(table);
		for (Map<String, String> appointment : dayAppointments) {
			float rowHeight = addAppointmentRow(appointment, table, tableWidth, colors);
			yPosition -= rowHeight;
		}
		table.draw();

		int pageCount = doc.getNumberOfPages();
		for (int i = 0; i < pageCount; i++) {
			PDPage tablePage = doc.getPage(i);
			PDPageContentStream tableContentStream = new PDPageContentStream(doc, tablePage,
					PDPageContentStream.AppendMode.APPEND, true);

			writeHeader(tableContentStream, tablePage, appointments);
			tableContentStream.close();
		}
	}

	private static float addAllDayAppointmentRow(Map<String, String> appointment, BaseTable table, float tableWidth,
			Map<String, String> colors) {
		float rowHeight = 15f; // calculateRowHeight(appointment, tableWidth);
		Row<PDPage> dataRow = table.createRow(rowHeight);
		for (String key : new String[] { "Ganzer Tag", "Personalien", "Grund" }) {
			String text = appointment.get(key);
			if (text != null) {
				text = text.replaceAll("[\\r\\n]", " ");
			} else {
				text = StringUtils.EMPTY;
			}
			Cell<PDPage> cell = dataRow.createCell(columnWidth(key), text != null ? text : "");
			cell.setFont(PDType1Font.HELVETICA);
			cell.setFontSize(10);
			String id = appointment.get("ID");
			if (id != null && colors.containsKey(id)) {
				cell.setFillColor(getColorWithAlpha(colors.get(id)));
			} else {
				cell.setFillColor(Color.WHITE);
			}
			cell.setAlign(HorizontalAlignment.LEFT);
			cell.setBorderStyle(new LineStyle(Color.WHITE, 0));
			cell.setTopPadding(2f);
			cell.setBottomPadding(2f);
			rowHeight = cell.getHeight();
		}
		return rowHeight;
	}

	private static float addAppointmentRow(Map<String, String> appointment, BaseTable table, float tableWidth,
			Map<String, String> colors) {
		float rowHeight = 15f; // calculateRowHeight(appointment, tableWidth);
		Row<PDPage> dataRow = table.createRow(rowHeight);
		for (String key : new String[] { "Von", "Bis", "Personalien", "Grund" }) {
			String text = appointment.get(key);
			if (text != null) {
				text = text.replaceAll("[\\r\\n]", " ");
			}
			Cell<PDPage> cell = dataRow.createCell(columnWidth(key), text != null ? text : "");
			cell.setFont(PDType1Font.HELVETICA);
			cell.setFontSize(10);
			String id = appointment.get("ID");
			if (id != null && colors.containsKey(id)) {
				cell.setFillColor(getColorWithAlpha(colors.get(id)));
			} else {
				cell.setFillColor(Color.WHITE);
			}
			cell.setAlign(HorizontalAlignment.LEFT);
			cell.setBorderStyle(new LineStyle(Color.WHITE, 0));
			cell.setTopPadding(2f);
			cell.setBottomPadding(2f);
			rowHeight = cell.getHeight();
		}
		return rowHeight;
	}

	private static float columnWidth(String columnName) {
		switch (columnName) {
		case "Von":
		case "Bis":
			return 5f;
		case "Personalien":
			return 30f;
		case "Grund":
			return 60f;
		case "Ganzer Tag":
			return 10f;
		default:
			return 10f;
		}
	}

	private static void addAllDayTableHeader(BaseTable table) throws IOException {
		Row<PDPage> headerRow = table.createRow(15f);
		for (String header : new String[] { "Ganzer Tag", "Personalien", "Grund" }) {
			Cell<PDPage> cell = headerRow.createCell(columnWidth(header), header);
			cell.setFont(PDType1Font.HELVETICA_BOLD);
			cell.setFontSize(10);
			cell.setAlign(HorizontalAlignment.LEFT);
			cell.setBorderStyle(new LineStyle(Color.WHITE, 0));
			cell.setTopPadding(2f);
			cell.setBottomPadding(2f);
		}
		table.addHeaderRow(headerRow);
	}

	private static void addTableHeader(BaseTable table) throws IOException {
		Row<PDPage> headerRow = table.createRow(15f);
		for (String header : new String[] { "Von", "Bis", "Personalien", "Grund" }) {
			Cell<PDPage> cell = headerRow.createCell(columnWidth(header), header);
			cell.setFont(PDType1Font.HELVETICA_BOLD);
			cell.setFontSize(10);
			cell.setAlign(HorizontalAlignment.LEFT);
			cell.setBorderStyle(new LineStyle(Color.WHITE, 0));
			cell.setTopPadding(2f);
			cell.setBottomPadding(2f);
		}
		table.addHeaderRow(headerRow);
	}

	private static Color getColorWithAlpha(String colorStr) {
		int r = Integer.parseInt(colorStr.substring(0, 2), 16);
		int g = Integer.parseInt(colorStr.substring(2, 4), 16);
		int b = Integer.parseInt(colorStr.substring(4, 6), 16);
		double alpha = 0.6;
		r = (int) (r + (255 - r) * alpha);
		g = (int) (g + (255 - g) * alpha);
		b = (int) (b + (255 - b) * alpha);
		return new Color(r, g, b);
	}

	private static float writeHeader(PDPageContentStream contentStream, PDPage page,
			List<Map<String, String>> appointments) throws IOException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy"); // Gewünschtes Ausgabeformat
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Format des Eingabedatums
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 8);
		String area = appointments.isEmpty() ? "Unbekannt" : appointments.get(0).get("Area");
		float yPos = page.getMediaBox().getHeight() - 50;
		contentStream.newLineAtOffset(page.getMediaBox().getWidth() - 255, yPos);
		String datumStr = appointments.get(0).get("Datum");

		Date datum = null;
		try {
			datum = inputDateFormat.parse(datumStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		contentStream
				.showText("Agenda Bereich " + area + ", " + (datum != null ? dateFormat.format(datum) : "Unbekannt")
						+ " erstellt am " + dateFormat.format(Calendar.getInstance().getTime()));
		contentStream.endText();
		contentStream.setStrokingColor(Color.BLACK);
		contentStream.setLineWidth((float) 0.5);
		contentStream.moveTo(50, yPos - 3);
		contentStream.lineTo(page.getMediaBox().getWidth() - 50, yPos - 3);
		contentStream.stroke();
		return yPos - 10;
	}

	private static void addPageNumbers(PDDocument document) throws IOException {
		int totalPages = document.getNumberOfPages();
		for (int i = 0; i < totalPages; i++) {
			PDPage page = document.getPage(i);
			PDPageContentStream footerContentStream = new PDPageContentStream(document, page,
					PDPageContentStream.AppendMode.APPEND, true);
			float lineYPos = 40;
			footerContentStream.setStrokingColor(Color.BLACK);
			footerContentStream.setLineWidth((float) 0.5);
			footerContentStream.moveTo(50, lineYPos);
			footerContentStream.lineTo(page.getMediaBox().getWidth() - 50, lineYPos);
			footerContentStream.stroke();
			footerContentStream.beginText();
			footerContentStream.setFont(PDType1Font.HELVETICA, 8);
			footerContentStream.newLineAtOffset(page.getMediaBox().getWidth() - 100, 30);
			footerContentStream.showText("Seite " + (i + 1) + " von " + totalPages);
			footerContentStream.endText();
			footerContentStream.close();
		}
	}
}
