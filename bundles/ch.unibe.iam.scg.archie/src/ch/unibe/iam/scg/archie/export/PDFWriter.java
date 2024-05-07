package ch.unibe.iam.scg.archie.export;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import ch.unibe.iam.scg.archie.model.AbstractDataProvider;
import ch.unibe.iam.scg.archie.model.DataSet;
import ch.unibe.iam.scg.archie.utils.ProviderHelper;

public class PDFWriter {
	public static void saveFile(String fileName, AbstractDataProvider provider)
			throws DocumentException, FileNotFoundException {
		Document document = new Document(PageSize.A4.rotate());
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
		FooterEvent footerEvent = new FooterEvent();
		writer.setPageEvent(footerEvent);
		document.open();
		Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
		writeProviderInformation(document, provider, font);
		DataSet data = provider.getDataSet();
		PdfPTable table = createDataTable(data, font);
		document.add(table);
		document.close();
	}

	private static PdfPTable createDataTable(DataSet data, Font font) throws DocumentException {
		PdfPTable table = new PdfPTable(data.getHeadings().size());
		table.setHeaderRows(1);
		table.setWidthPercentage(100);
		Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
		for (String heading : data.getHeadings()) {
			PdfPCell cell = new PdfPCell(new Paragraph(heading, boldFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
		}
		for (Object[] row : data) {
			for (int i = 0; i < row.length; i++) {
				String contentText = row[i] != null ? row[i].toString() : "";
				PdfPCell cell;
				if (data.getHeadings().get(i).equals("Betrag") || data.getHeadings().get(i).equals("Betr. Leistungen")
						|| data.getHeadings().get(i).equals("Tag-Gesamt")
						|| data.getHeadings().get(i).equals("Gesamt")) {
					PdfPTable innerTable = new PdfPTable(2);
					innerTable.setWidths(new float[] { 0.5f, 1f });
					PdfPCell currencyCell = new PdfPCell(new Phrase("CHF", boldFont));
					currencyCell.setBorder(PdfPCell.NO_BORDER);
					currencyCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					innerTable.addCell(currencyCell);
					PdfPCell amountCell = new PdfPCell(new Phrase(contentText, boldFont));
					amountCell.setBorder(PdfPCell.NO_BORDER);
					amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					innerTable.addCell(amountCell);
					cell = new PdfPCell(innerTable);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				} else {
					cell = new PdfPCell(new Paragraph(contentText, font));
					if (i > 0) {
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					}
				}
				table.addCell(cell);
			}
		}
		float[] columnWidths = calculateColumnWidths(data);
		table.setWidths(columnWidths);
		return table;
	}

	private static float[] calculateColumnWidths(DataSet data) {
		float[] columnWidths = new float[data.getHeadings().size()];
		for (int i = 0; i < data.getHeadings().size(); i++) {
			String heading = data.getHeadings().get(i);
			if (heading.equals("Pat-Nr") || heading.equals("Pat-ort") || heading.equals("Rg-Empf. Ort")) {
				columnWidths[i] = 40;
			} else if (heading.contains("Betrag") || heading.contains("Betr. Leistungen")
					|| heading.contains("Tag-Gesamt") || heading.contains("Gesamt")) {
				columnWidths[i] = 80;
			} else if (heading.contains("Typ")) {
				columnWidths[i] = 30;
			} else {
				columnWidths[i] = 60;
			}
		}
		return columnWidths;
	}

	private static void writeProviderInformation(Document document, AbstractDataProvider provider, Font font)
			throws DocumentException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Paragraph providerInfo = new Paragraph(
				provider.getName() + "\n" + dateFormat.format(Calendar.getInstance().getTime()), font);
		providerInfo.setSpacingAfter(12);
		document.add(providerInfo);
		Map<String, Object> getters = ProviderHelper.getGetterMap(provider, true);
		for (String key : getters.keySet()) {
			String info = key + " = " + getters.get(key).toString();
			Paragraph paramInfo = new Paragraph(info, font);
			paramInfo.setSpacingAfter(6);
			document.add(paramInfo);
		}
		document.add(new Paragraph("\n"));
	}

	public static class FooterEvent extends PdfPageEventHelper {
		protected PdfTemplate total;
		protected BaseFont helv;
		protected Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL);

		public void onOpenDocument(PdfWriter writer, Document document) {
			total = writer.getDirectContent().createTemplate(50, 16);
			try {
				helv = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
			} catch (Exception e) {
				throw new ExceptionConverter(e);
			}
		}

		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			PdfPTable footer = new PdfPTable(2);
			try {
				footer.setWidths(new int[] { 10, 1 });
				footer.setTotalWidth(
						document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
				footer.getDefaultCell().setFixedHeight(20);
				footer.getDefaultCell().setBorder(Rectangle.TOP);
				footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
				PdfPCell textCell = new PdfPCell(new Phrase("Seite " + writer.getPageNumber() + " von ", footerFont));
				textCell.setBorder(Rectangle.TOP);
				textCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				textCell.setPaddingRight(-17);
				footer.addCell(textCell);
				PdfPCell cell = new PdfPCell(Image.getInstance(total), false);
				cell.setBorder(Rectangle.TOP);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setPaddingTop((float) -7.5);
				footer.addCell(cell);

				footer.writeSelectedRows(0, -1, document.leftMargin(), 30, writer.getDirectContent());
			} catch (DocumentException de) {
				throw new ExceptionConverter(de);
			}
		}

		@Override
		public void onCloseDocument(PdfWriter writer, Document document) {
			total.beginText();
			total.setFontAndSize(helv, 8);
			total.setTextMatrix(0, 0);
			total.showText(String.valueOf(writer.getPageNumber()));
			total.endText();
		}
	}
}
