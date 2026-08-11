package ch.elexis.base.ch.arzttarife.ps25.model.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.tarmed.model.importer.EntityUtil;
import ch.elexis.core.jpa.entities.Ps25Leistung;

public class Ps25ReferenceDataImporterTest {

	@Test
	public void performImportAndNormalizeVersions() throws IOException {
		Ps25ReferenceDataImporter importer = new Ps25ReferenceDataImporter();
		Status initialStatus = (Status) importer.performImport(new NullProgressMonitor(),
				new ByteArrayInputStream(createXlsx(new String[][] {
						row("P001", "Empfaenger", "Kapitel", "Unterkapitel", "Mehrleistung alt", "Spezifikation",
								"Regel", "12", "1", "Kombi", "A", "Mehrleistung", "01.01.2026", ""),
						row("P002", "Empfaenger", "Kapitel", "Unterkapitel", "Abgelaufen", "Spezifikation",
								"Regel", "7", "1", "", "B", "Mehrleistung", "01.01.2026", "31.03.2026") })),
				null);
		assertEquals(IStatus.OK, initialStatus.getCode());

		Status updateStatus = (Status) importer.performImport(new NullProgressMonitor(),
				new ByteArrayInputStream(createXlsx(new String[][] { row("P001", "Empfaenger", "Kapitel",
						"Unterkapitel", "Mehrleistung neu", "Spezifikation", "Regel", "15", "2", "Kombi", "A",
						"Mehrleistung neu", "01.07.2026", "") })),
				null);
		assertEquals(IStatus.OK, updateStatus.getCode());

		Ps25Leistung oldVersion = EntityUtil.load("P001-20260101", Ps25Leistung.class);
		Ps25Leistung newVersion = EntityUtil.load("P001-20260701", Ps25Leistung.class);
		Ps25Leistung expiredVersion = EntityUtil.load("P002-20260101", Ps25Leistung.class);

		assertNotNull(oldVersion);
		assertEquals(LocalDate.of(2026, 6, 30), oldVersion.getValidUntil());
		assertNotNull(newVersion);
		assertEquals("15", newVersion.getTaxpunkte());
		assertNotNull(expiredVersion);
		assertEquals(LocalDate.of(2026, 3, 31), expiredVersion.getValidUntil());

		Status duplicateStatus = (Status) importer.performImport(new NullProgressMonitor(),
				new ByteArrayInputStream(createZip("ps25.xlsx", createXlsx(new String[][] { row("P001", "Empfaenger",
						"Kapitel", "Unterkapitel", "Mehrleistung neu", "Spezifikation", "Regel", "15", "2",
						"Kombi", "A", "Mehrleistung neu", "01.07.2026", "") }))),
				null);
		assertEquals(IStatus.OK, duplicateStatus.getCode());
		assertEquals(2, loadByCode("P001").size());
	}

	@Test
	public void metadata() {
		Ps25ReferenceDataImporter importer = new Ps25ReferenceDataImporter();
		assertEquals("ps25", Ps25ReferenceDataImporter.REFERENCEDATA_ID);
		assertEquals("744", Ps25Leistung.CODESYSTEM_CODE);
	}

	private List<Ps25Leistung> loadByCode(String code) {
		Map<String, Object> propertyMap = new LinkedHashMap<>();
		propertyMap.put("code", code);
		return EntityUtil.loadByNamedQuery(propertyMap, Ps25Leistung.class);
	}

	private String[] row(String... values) {
		return values;
	}

	private byte[] createXlsx(String[][] dataRows) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (ZipOutputStream zip = new ZipOutputStream(out)) {
			put(zip, "[Content_Types].xml",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
							+ "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
							+ "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
							+ "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
							+ "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
							+ "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
							+ "</Types>");
			put(zip, "_rels/.rels",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
							+ "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
							+ "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
							+ "</Relationships>");
			put(zip, "xl/workbook.xml",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
							+ "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" "
							+ "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
							+ "<sheets><sheet name=\"Leistungsbewertungen\" sheetId=\"1\" r:id=\"rId1\"/></sheets>"
							+ "</workbook>");
			put(zip, "xl/_rels/workbook.xml.rels",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
							+ "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
							+ "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
							+ "</Relationships>");
			put(zip, "xl/worksheets/sheet1.xml", createSheet(dataRows));
		}
		return out.toByteArray();
	}

	private String createSheet(String[][] dataRows) {
		StringBuilder sheet = new StringBuilder();
		sheet.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sheet.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetData>");
		sheet.append("<row r=\"4\">");
		String[] headers = { "Pos_ID", "Honorarempfaenger", "Fachgebiet/Kapitel", "Unterkapitel",
				"Fachaerztliche Mehrleistung bei", "Spezifikation", "Anwendungsregeln", "Taxpunkte", "Stufe",
				"moegliche Kombination", "Mehrleistungstyp", "Mehrleistung", "Gueltig von", "Gueltig bis" };
		for (int i = 0; i < headers.length; i++) {
			appendCell(sheet, 4, i, headers[i]);
		}
		sheet.append("</row>");
		for (int rowIndex = 0; rowIndex < dataRows.length; rowIndex++) {
			int excelRow = rowIndex + 5;
			sheet.append("<row r=\"").append(excelRow).append("\">");
			for (int col = 0; col < dataRows[rowIndex].length; col++) {
				appendCell(sheet, excelRow, col, dataRows[rowIndex][col]);
			}
			sheet.append("</row>");
		}
		sheet.append("</sheetData></worksheet>");
		return sheet.toString();
	}

	private void appendCell(StringBuilder sheet, int row, int column, String value) {
		sheet.append("<c r=\"").append(columnName(column)).append(row).append("\" t=\"inlineStr\"><is><t>")
				.append(escape(value)).append("</t></is></c>");
	}

	private String columnName(int column) {
		return String.valueOf((char) ('A' + column));
	}

	private byte[] createZip(String filename, byte[] payload) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (ZipOutputStream zip = new ZipOutputStream(out)) {
			ZipEntry entry = new ZipEntry(filename);
			zip.putNextEntry(entry);
			zip.write(payload);
			zip.closeEntry();
		}
		return out.toByteArray();
	}

	private void put(ZipOutputStream zip, String name, String content) throws IOException {
		zip.putNextEntry(new ZipEntry(name));
		zip.write(content.getBytes(StandardCharsets.UTF_8));
		zip.closeEntry();
	}

	private String escape(String value) {
		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
