package ch.unibe.iam.scg.archie.export;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import ch.unibe.iam.scg.archie.model.AbstractDataProvider;
import ch.unibe.iam.scg.archie.model.DataSet;
import ch.unibe.iam.scg.archie.utils.ProviderHelper;

public class PDFWriter {
	public static void saveFile(String fileName, AbstractDataProvider provider) throws IOException {
		try (PDDocument document = new PDDocument()) {
			PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
			document.addPage(page);
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			float yPosDescriptionEnd = writeProviderInformation(contentStream, provider, page);
			contentStream.close();
			createDataTable(document, page, provider.getDataSet(), yPosDescriptionEnd);
			addPageNumbers(document);
			document.save(new FileOutputStream(fileName));
		}
	}

	private static void createDataTable(PDDocument doc, PDPage page, DataSet data, float startY) throws IOException {
		try (PDPageContentStream contentStream = new PDPageContentStream(doc, page,
				PDPageContentStream.AppendMode.APPEND, true)) {
			float margin = 50;
			float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
			BaseTable table = new BaseTable(startY, startY, margin, tableWidth, margin, doc, page, true, true);
			Color darkGray = new Color(200, 200, 200);
			Color lightGray = new Color(230, 230, 230);
			Color white = new Color(255, 255, 255);
			Row<PDPage> headerRow = table.createRow(15f);
			for (String heading : data.getHeadings()) {
				Cell<PDPage> cell = headerRow.createCell((100.0f / data.getHeadings().size()), heading);
				cell.setFont(PDType1Font.HELVETICA_BOLD);
				cell.setFillColor(darkGray);
				cell.setAlign(HorizontalAlignment.CENTER);
			}
			table.addHeaderRow(headerRow);
			boolean toggleColor = false;
			for (Object[] row : data) {
				Row<PDPage> dataRow = table.createRow(10f);
				for (Object obj : row) {
					String text = obj.toString();
					String cleanText = cleanText(text);
					Cell<PDPage> cell = dataRow.createCell((100.0f / row.length), cleanText);
					cell.setFont(PDType1Font.HELVETICA);
					cell.setFillColor(toggleColor ? lightGray : white);
					cell.setAlign(HorizontalAlignment.RIGHT);
				}
				toggleColor = !toggleColor;
			}
			table.draw();
		}
	}

	private static float writeProviderInformation(PDPageContentStream contentStream, AbstractDataProvider provider,
			PDPage page) throws IOException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 12);
		float yPos = page.getMediaBox().getHeight() - 50;
		contentStream.newLineAtOffset(50, yPos);
		contentStream.showText(provider.getName() + " " + dateFormat.format(Calendar.getInstance().getTime()));
		contentStream.endText();
		Map<String, Object> getters = ProviderHelper.getGetterMap(provider, true);
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 10);
		contentStream.newLineAtOffset(50, yPos - 15);
		int totalLines = 0;
		float maxWidth = page.getMediaBox().getWidth() - 100;
		for (String key : getters.keySet()) {
			List<String> wrappedText = wrapText(key + ": " + getters.get(key).toString(), maxWidth, contentStream, 10);
			for (String line : wrappedText) {
				String cleanLine = cleanText(line);
				contentStream.showText(cleanLine);
				contentStream.newLineAtOffset(0, -15);
			}
			totalLines += wrappedText.size();
		}
		contentStream.endText();
		return yPos - 15 - 15 * totalLines;
	}

	private static List<String> wrapText(String text, float maxWidth, PDPageContentStream contentStream, float fontSize)
			throws IOException {
		List<String> lines = new ArrayList<>();
		String[] words = text.split(" ");
		StringBuilder currentLine = new StringBuilder();
		float currentWidth = 0;
		for (String word : words) {
			float wordWidth = fontSize * PDType1Font.HELVETICA.getStringWidth(word + " ") / 1000;
			if (currentWidth + wordWidth < maxWidth) {
				currentLine.append(word).append(" ");
				currentWidth += wordWidth;
			} else {
				lines.add(currentLine.toString().trim());
				currentLine = new StringBuilder(word + " ");
				currentWidth = wordWidth;
			}
		}
		if (currentLine.length() > 0) {
			lines.add(currentLine.toString().trim());
		}
		return lines;
	}

	private static void addPageNumbers(PDDocument document) throws IOException {
		int totalPages = document.getNumberOfPages();
		for (int i = 0; i < totalPages; i++) {
			PDPage page = document.getPage(i);
			PDPageContentStream footerContentStream = new PDPageContentStream(document, page,
					PDPageContentStream.AppendMode.APPEND, true);
			footerContentStream.beginText();
			footerContentStream.setFont(PDType1Font.HELVETICA, 10);
			footerContentStream.newLineAtOffset(page.getMediaBox().getWidth() - 100, 30);
			footerContentStream.showText("Seite " + (i + 1) + " von " + totalPages);
			footerContentStream.endText();
			footerContentStream.close();
		}
	}

	private static String cleanText(String text) {
		return text.replace("\r", "").replace("\n", "");
	}
}
