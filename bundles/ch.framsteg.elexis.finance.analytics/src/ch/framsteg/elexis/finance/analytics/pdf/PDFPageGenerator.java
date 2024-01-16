package ch.framsteg.elexis.finance.analytics.pdf;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import ch.framsteg.elexis.finance.analytics.beans.Day;
import ch.framsteg.elexis.finance.analytics.beans.Delivery;
import ch.framsteg.elexis.finance.analytics.beans.Patient;
import ch.framsteg.elexis.finance.analytics.beans.Treatment;

public class PDFPageGenerator {

	private final static String DEFAULT_FONT_SIZE = "reporting.pdf.daily.report.text.font.default.size";
	private final static String INTERMEDIATE_FONT_SIZE = "reporting.pdf.daily.report.text.font.intermediate.size";
	private final static String TITLE_FONT_SIZE = "reporting.pdf.daily.report.text.font.title.size";
	private final static String LEADING = "reporting.pdf.daily.report.text.leading";
	private final static String TEXT_LENGTH = "reporting.pdf.daily.report.text.length";
	private final static String LINES_PER_PAGE = "reporting.pdf.daily.report.lines.per.page";
	private final static String NUMEBR_OF_BLOCKED_LINES_PATIENT = "reporting.pdf.daily.report.blocked.lines.patient";
	private final static String NUMEBR_OF_BLOCKED_LINES_TREATMENT = "reporting.pdf.daily.report.blocked.lines.treatment";
	private final static String NUMBER_OF_BLOCKED_LINES_SUMMARY = "reporting.pdf.daily.report.blocked.lines.summary";
	private final static String SHORT_INDENT = "reporting.pdf.daily.report.short.indent";
	private final static String LONG_INDENT = "reporting.pdf.daily.report.long.indent";
	private final static String DEFAULT_OFFSET_X_LEFT_1 = "reporting.pdf.daily.report.offset.x.left.1";
	private final static String DEFAULT_OFFSET_X_LEFT_2 = "reporting.pdf.daily.report.offset.x.left.2";
	private final static String DEFAULT_OFFSET_X_RIGHT_1 = "reporting.pdf.daily.report.offset.x.right.1";
	private final static String DEFAULT_OFFSET_X_RIGHT_2 = "reporting.pdf.daily.report.offset.x.right.2";
	private final static String DEFAULT_OFFSET_X_RIGHT_3 = "reporting.pdf.daily.report.offset.x.right.3";
	private final static String DEFAULT_OFFSET_X_RIGHT_4 = "reporting.pdf.daily.report.offset.x.right.4";
	private final static String DEFAULT_OFFSET_X_UPPER_LINE = "reporting.pdf.daily.report.offset.x.upper.line";
	private final static String DEFAULT_OFFSET_Y_TOP = "reporting.pdf.daily.report.offset.y.top";
	private final static String DEFAULT_OFFSET_Y_RIGHT = "reporting.pdf.daily.report.offset.y.right";
	private final static String DEFAULT_OFFSET_Y_LEFT = "reporting.pdf.daily.report.offset.y.left";
	private final static String NO_MOVE = "reporting.pdf.daily.report.no.move";
	private final static String LINE_WIDTH = "reporting.pdf.daily.report.line.width";

	private final static String PDF_LABEL_PATIENT = "reporting.pdf.daily.report.label.patient";
	private final static String PDF_LABEL_NUMBER_OF_PATIENTS = "reporting.pdf.daily.report.label.number.of.patients";
	private final static String PDF_LABEL_NUMBER_OF_TREATMENTS = "reporting.pdf.daily.report.label.number.of.treatments";
	private final static String PDF_LABEL_TARMED = "reporting.pdf.daily.report.label.tarmed";
	private final static String PDF_LABEL_MEDICAL = "reporting.pdf.daily.report.label.medical";
	private final static String PDF_LABEL_LABOR = "reporting.pdf.daily.report.label.labor";
	private final static String PDF_LABEL_INTERNAL_SERVICE = "reporting.pdf.daily.report.label.internal.service";
	private final static String PDF_LABEL_PANDEMIC = "reporting.pdf.daily.report.label.pandemic";
	private final static String PDF_LABEL_INTERNAL_ASSETS = "reporting.pdf.daily.report.label.internal.assets";
	private final static String PDF_LABEL_MIGEL = "reporting.pdf.daily.report.label.migel";
	private final static String PDF_LABEL_SUMMARY = "reporting.pdf.daily.report.label.summary";
	private final static String PDF_LABEL_TOTAL = "reporting.pdf.daily.report.label.total";
	private final static String PDF_LABEL_SUMMARY_DAILY = "reporting.pdf.daily.report.label.summary.daily";
	private final static String PDF_LABEL_SUM = "reporting.pdf.daily.report.label.sum";
	private final static String PDF_LABEL_TREATMENT_BILL = "reporting.pdf.daily.report.label.treatment.bill";

	private final static String DELIVERY_CLASS_TARMED = "reporting.pdf.daily.report.delilvery.class.tarmed";
	private final static String DELIVERY_CLASS_MEDICAL = "reporting.pdf.daily.report.delilvery.class.medical";
	private final static String DELIVERY_CLASS_LABOR = "reporting.pdf.daily.report.delilvery.class.labor";
	private final static String DELIVERY_CLASS_INTERNAL_SERVICE = "reporting.pdf.daily.report.delilvery.class.internal.service";
	private final static String DELIVERY_CLASS_PANDEMIC = "reporting.pdf.daily.report.delilvery.class.pandemic";
	private final static String DELIVERY_CLASS_INTERNAL_ASSETS = "reporting.pdf.daily.report.delilvery.class.internal.assets";
	private final static String DELIVERY_CLASS_MIGEL = "reporting.pdf.daily.report.delilvery.class.migel";

	private String mandantInfo;
	private String reportTitle;
	private String reportingDateTime;

	private Properties applicationProperties;
	private Properties messagesProperties;

	private int pageCount;

	public PDFPageGenerator(String mandantInfo, String reportTitle, String reportingDateTime,
			Properties applicationProperties, Properties messagesProperties) {
		setMandantInfo(mandantInfo);
		setReportTitle(reportTitle);
		setReportingDateTime(reportingDateTime);
		setApplicationProperties(applicationProperties);
		setMessagesProperties(messagesProperties);

	}

	public PDDocument generateDailyReport(TreeMap<String, Day> input, String mandantInfo, String reportTitle,
			String reportingDateTime, int numberOfLines, String from, String to) {

		int numberOfTreatments = 0;
		int numberOfTreatmentsPerDay = 0;

		int numberOfPatients = 0;
		int numberOfPatientsPerDay = 0;

		float sumOfTarmed = 0;
		float sumOfTarmedPerDay = 0;

		float sumOfMedical = 0;
		float sumOfMedicalPerDay = 0;

		float sumOfLaboratory = 0;
		float sumOfLaboratoryPerDay = 0;

		float sumOfInternalServices = 0;
		float sumOfInternalServicesPerDay = 0;

		float sumOfPandemic = 0;
		float sumOfPandemicPerDay = 0;

		float sumOfInternalAssets = 0;
		float sumOfInternalAssetsPerDay = 0;

		float sumOfMigel = 0;
		float sumOfMigelPerDay = 0;

		PDDocument doc = new PDDocument();
		PDPage page = new PDPage();
		pageCount++;
		doc.addPage(page);

		float sum = 0;

		int treatmentCount = 1;
		int treatmentNumber = 1;

		PDPageContentStream contentStream;
		try {
			int countLines = 0;

			contentStream = new PDPageContentStream(doc, page);
			contentStream = addDocumentInformation(contentStream);

			contentStream.beginText();
			contentStream.newLineAtOffset(60, 700);
			for (Map.Entry<String, Day> days_entry : input.entrySet()) {
				if (countLines == Integer.valueOf(getMessagesProperties().getProperty(LINES_PER_PAGE))) {
					contentStream.endText();
					contentStream.close();
					countLines = 0;
					contentStream = returnNewPageContent(doc);
					contentStream.beginText();
					contentStream.newLineAtOffset(
							Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_LEFT_1)),
							Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_Y_TOP)));
				}

				String actualDate = generateHumanReadableDate(days_entry.getValue().getDate());

				contentStream.setFont(PDType1Font.HELVETICA_BOLD,
						Float.valueOf(getMessagesProperties().getProperty(TITLE_FONT_SIZE)));
				contentStream.showText(actualDate);
				contentStream.setFont(PDType1Font.HELVETICA,
						Float.valueOf(getMessagesProperties().getProperty(DEFAULT_FONT_SIZE)));
				contentStream.newLine();
				countLines += 1;
				for (Patient patient : days_entry.getValue().getPatients()) {
					if (countLines + patient.getTreatments().size() > Integer
							.valueOf(getMessagesProperties().getProperty(LINES_PER_PAGE))
							- Float.valueOf(getMessagesProperties().getProperty(NUMEBR_OF_BLOCKED_LINES_TREATMENT))) {
						contentStream.endText();
						contentStream.close();
						countLines = 0;
						contentStream = returnNewPageContent(doc);
						contentStream.beginText();
						contentStream.newLineAtOffset(
								Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_LEFT_1)),
								Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_Y_TOP)));
					}

					contentStream.setFont(PDType1Font.HELVETICA_BOLD,
							Float.valueOf(getMessagesProperties().getProperty(DEFAULT_FONT_SIZE)));
					writeLine(doc, contentStream,
							MessageFormat.format(getMessagesProperties().getProperty(PDF_LABEL_PATIENT),
									patient.getId(), patient.getName(), patient.getFirstname(), patient.getSex(),
									generateHumanReadableDate(patient.getBirthday())));
					contentStream.setFont(PDType1Font.HELVETICA,
							Float.valueOf(getMessagesProperties().getProperty(DEFAULT_FONT_SIZE)));
					numberOfPatients++;
					numberOfPatientsPerDay++;
					countLines += 1;
					treatmentCount = 1;
					treatmentNumber = 1;
					for (Treatment treatment : patient.getTreatments()) {
						if (countLines + treatment.getDeliveries().size() > Integer
								.valueOf(getMessagesProperties().getProperty(LINES_PER_PAGE))
								- Float.valueOf(
										getMessagesProperties().getProperty(NUMEBR_OF_BLOCKED_LINES_TREATMENT))) {
							contentStream.endText();
							contentStream.close();
							countLines = 0;
							contentStream = returnNewPageContent(doc);
							contentStream.beginText();
							contentStream.newLineAtOffset(
									Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_LEFT_1)),
									Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_Y_TOP)));
						}
						writeLine(doc, contentStream,
								MessageFormat.format(getMessagesProperties().getProperty(PDF_LABEL_TREATMENT_BILL),
										treatmentNumber, treatmentCount, treatment.getBillingNumber(),
										String.format("%.2f", Float.valueOf(treatment.getBillingAmount()))));
						countLines += 1;
						numberOfTreatments++;
						numberOfTreatmentsPerDay++;
						ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
						for (Delivery delivery : treatment.getDeliveries()) {
							deliveries.add(delivery);
						}
						Collections.reverse(deliveries);
						sum = 0;
						DecimalFormat df = new DecimalFormat("0.00");
						sum = Float.valueOf(df.format(sum));
						for (Delivery delivery : deliveries) {
							if (countLines == Integer.valueOf(getMessagesProperties().getProperty(LINES_PER_PAGE))) {
								contentStream.endText();
								contentStream.close();
								countLines = 0;
								contentStream = returnNewPageContent(doc);
								contentStream.beginText();
								contentStream.newLineAtOffset(
										Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_LEFT_1)),
										Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_Y_TOP)));
							}
							sum = sum + delivery.getClearingPrice().floatValue();
							sum = Float.valueOf(df.format(sum));
							String tempString = delivery.getDeliveryDescription().length() > Integer
									.valueOf(getMessagesProperties().getProperty(TEXT_LENGTH))
											? delivery.getDeliveryDescription().substring(0,
													Integer.valueOf(getMessagesProperties().getProperty(TEXT_LENGTH)))
											: delivery.getDeliveryDescription();
							contentStream.showText(delivery.getDeliveryCode());
							contentStream.newLineAtOffset(
									Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_RIGHT_3)),
									Float.valueOf(getMessagesProperties().getProperty(NO_MOVE)));
							contentStream.showText(tempString);
							contentStream.newLineAtOffset(
									Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_RIGHT_4)),
									Float.valueOf(getMessagesProperties().getProperty(NO_MOVE)));
							contentStream.showText(String.valueOf(delivery.getClearingFactor()));

							float wordLength = getTextWidth(PDType1Font.HELVETICA, 10,
									delivery.getClearingPrice().toString());
							float nextTextX = Float.valueOf(getMessagesProperties().getProperty(LONG_INDENT))
									- wordLength - 400;

							contentStream.newLineAtOffset(nextTextX,
									Float.valueOf(getMessagesProperties().getProperty(NO_MOVE)));

							contentStream.showText(String.valueOf(delivery.getClearingPrice()));
							contentStream.newLineAtOffset(
									-1 * (Float.valueOf(getMessagesProperties().getProperty(LONG_INDENT))) + wordLength,
									Float.valueOf(getMessagesProperties().getProperty(NO_MOVE)));
							contentStream.newLine();

							countLines += 1;

							if (delivery.getDeliveryClass()
									.equalsIgnoreCase(getMessagesProperties().getProperty(DELIVERY_CLASS_TARMED))) {
								sumOfTarmed = sumOfTarmed + delivery.getClearingPrice().floatValue();
								sumOfTarmedPerDay = sumOfTarmedPerDay + delivery.getClearingPrice().floatValue();

							}
							if (delivery.getDeliveryClass()
									.equalsIgnoreCase(getMessagesProperties().getProperty(DELIVERY_CLASS_MEDICAL))) {
								sumOfMedical = sumOfMedical + delivery.getClearingPrice().floatValue();
								sumOfMedicalPerDay = sumOfMedicalPerDay + delivery.getClearingPrice().floatValue();

							}
							if (delivery.getDeliveryClass()
									.equalsIgnoreCase(getMessagesProperties().getProperty(DELIVERY_CLASS_LABOR))) {
								sumOfLaboratory = sumOfLaboratory + delivery.getClearingPrice().floatValue();
								sumOfLaboratoryPerDay = sumOfLaboratoryPerDay
										+ delivery.getClearingPrice().floatValue();

							}
							if (delivery.getDeliveryClass().equalsIgnoreCase(
									getMessagesProperties().getProperty(DELIVERY_CLASS_INTERNAL_SERVICE))) {
								sumOfInternalServices = sumOfInternalServices
										+ delivery.getClearingPrice().floatValue();
								sumOfInternalServicesPerDay = sumOfInternalServicesPerDay
										+ delivery.getClearingPrice().floatValue();

							}
							if (delivery.getDeliveryClass()
									.equalsIgnoreCase(getMessagesProperties().getProperty(DELIVERY_CLASS_PANDEMIC))) {
								sumOfPandemic = sumOfPandemic + delivery.getClearingPrice().floatValue();
								sumOfPandemicPerDay = sumOfPandemicPerDay + delivery.getClearingPrice().floatValue();

							}
							if (delivery.getDeliveryClass().equalsIgnoreCase(
									getMessagesProperties().getProperty(DELIVERY_CLASS_INTERNAL_ASSETS))) {
								sumOfInternalAssets = sumOfInternalAssets + delivery.getClearingPrice().floatValue();
								sumOfInternalAssetsPerDay = sumOfInternalAssetsPerDay
										+ delivery.getClearingPrice().floatValue();

							}
							if (delivery.getDeliveryClass()
									.equalsIgnoreCase(getMessagesProperties().getProperty(DELIVERY_CLASS_MIGEL))) {
								sumOfMigel = sumOfMigel + delivery.getClearingPrice().floatValue();
								sumOfMigelPerDay = sumOfMigelPerDay + delivery.getClearingPrice().floatValue();

							}
						}
						contentStream.setFont(PDType1Font.HELVETICA_BOLD,
								Float.valueOf(getMessagesProperties().getProperty(DEFAULT_FONT_SIZE)));
						contentStream = insertValueFlushRight(contentStream,
								getMessagesProperties().getProperty(PDF_LABEL_SUM), String.valueOf(sum),
								PDType1Font.HELVETICA,
								Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
								Integer.valueOf(getMessagesProperties().getProperty(LONG_INDENT)));
						contentStream.setFont(PDType1Font.HELVETICA,
								Float.valueOf(getMessagesProperties().getProperty(DEFAULT_FONT_SIZE)));
						treatmentCount++;
					}
				}

				if (Integer.valueOf(getMessagesProperties().getProperty(LINES_PER_PAGE)) - countLines < Float
						.valueOf(getMessagesProperties().getProperty(NUMBER_OF_BLOCKED_LINES_SUMMARY))) {
					contentStream.endText();
					contentStream.close();
					countLines = 0;
					contentStream = returnNewPageContent(doc);
					contentStream.beginText();
					contentStream.newLineAtOffset(
							Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_LEFT_1)),
							Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_Y_TOP)));
				}
				contentStream.setFont(PDType1Font.HELVETICA_BOLD,
						Float.valueOf(getMessagesProperties().getProperty(DEFAULT_FONT_SIZE)));
				contentStream.showText(
						MessageFormat.format(getMessagesProperties().getProperty(PDF_LABEL_SUMMARY_DAILY), actualDate));
				contentStream.setFont(PDType1Font.HELVETICA,
						Float.valueOf(getMessagesProperties().getProperty(DEFAULT_FONT_SIZE)));
				contentStream.newLine();
				countLines += 1;
				contentStream = insertValueFlushRight(contentStream,
						getMessagesProperties().getProperty(PDF_LABEL_NUMBER_OF_PATIENTS),
						String.valueOf(numberOfPatientsPerDay), PDType1Font.HELVETICA,
						Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
						Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
				countLines += 1;
				contentStream = insertValueFlushRight(contentStream,
						getMessagesProperties().getProperty(PDF_LABEL_NUMBER_OF_TREATMENTS),
						String.valueOf(numberOfTreatmentsPerDay), PDType1Font.HELVETICA,
						Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
						Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
				countLines += 1;
				contentStream = insertValueFlushRight(contentStream,
						getMessagesProperties().getProperty(PDF_LABEL_TARMED),
						String.valueOf(String.format("%.2f", sumOfTarmedPerDay)), PDType1Font.HELVETICA,
						Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
						Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
				countLines += 1;
				contentStream = insertValueFlushRight(contentStream,
						getMessagesProperties().getProperty(PDF_LABEL_MEDICAL),
						String.valueOf(String.format("%.2f", sumOfMedicalPerDay)), PDType1Font.HELVETICA,
						Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
						Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
				countLines += 1;
				contentStream = insertValueFlushRight(contentStream,
						getMessagesProperties().getProperty(PDF_LABEL_LABOR),
						String.valueOf(String.format("%.2f", sumOfLaboratoryPerDay)), PDType1Font.HELVETICA,
						Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
						Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
				countLines += 1;
				contentStream = insertValueFlushRight(contentStream,
						getMessagesProperties().getProperty(PDF_LABEL_INTERNAL_SERVICE),
						String.valueOf(String.format("%.2f", sumOfInternalServicesPerDay)), PDType1Font.HELVETICA,
						Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
						Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
				countLines += 1;
				contentStream = insertValueFlushRight(contentStream,
						getMessagesProperties().getProperty(PDF_LABEL_PANDEMIC),
						String.valueOf(String.format("%.2f", sumOfPandemicPerDay)), PDType1Font.HELVETICA,
						Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
						Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
				countLines += 1;
				contentStream = insertValueFlushRight(contentStream,
						getMessagesProperties().getProperty(PDF_LABEL_INTERNAL_ASSETS),
						String.valueOf(String.format("%.2f", sumOfInternalAssetsPerDay)), PDType1Font.HELVETICA,
						Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
						Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
				countLines += 1;
				contentStream = insertValueFlushRight(contentStream,
						getMessagesProperties().getProperty(PDF_LABEL_MIGEL),
						String.valueOf(String.format("%.2f", sumOfMigelPerDay)), PDType1Font.HELVETICA,
						Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
						Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)
								+ String.valueOf(String.format("%.2f", sumOfMigelPerDay)).length()));
				countLines += 1;
				contentStream.setFont(PDType1Font.HELVETICA_BOLD,
						Float.valueOf(getMessagesProperties().getProperty(DEFAULT_FONT_SIZE)));
				contentStream = insertValueFlushRight(contentStream,
						getMessagesProperties().getProperty(PDF_LABEL_TOTAL),
						String.valueOf(String.format("%.2f",
								sumOfTarmedPerDay + sumOfMedicalPerDay + sumOfLaboratoryPerDay
										+ sumOfInternalServicesPerDay + sumOfPandemicPerDay + sumOfInternalAssetsPerDay
										+ sumOfMigelPerDay)),
						PDType1Font.HELVETICA,
						Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
						Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
				countLines = Integer.valueOf(getMessagesProperties().getProperty(LINES_PER_PAGE));

				numberOfPatientsPerDay = 0;
				numberOfTreatmentsPerDay = 0;
				sumOfTarmedPerDay = 0;
				sumOfMedicalPerDay = 0;
				sumOfLaboratoryPerDay = 0;
				sumOfInternalServicesPerDay = 0;
				sumOfPandemicPerDay = 0;
				sumOfInternalAssetsPerDay = 0;
				sumOfMigelPerDay = 0;

			}
			if (Integer.valueOf(getMessagesProperties().getProperty(LINES_PER_PAGE)) - countLines < Float
					.valueOf(getMessagesProperties().getProperty(NUMBER_OF_BLOCKED_LINES_SUMMARY))) {
				contentStream.endText();
				contentStream.close();
				countLines = 0;
				contentStream = returnNewPageContent(doc);
				contentStream.beginText();
				contentStream.newLineAtOffset(
						Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_LEFT_1)), 700);
			}
			contentStream.setFont(PDType1Font.HELVETICA_BOLD,
					Float.valueOf(getMessagesProperties().getProperty(DEFAULT_FONT_SIZE)));
			contentStream
					.showText(MessageFormat.format(getMessagesProperties().getProperty(PDF_LABEL_SUMMARY), from, to));
			contentStream.setFont(PDType1Font.HELVETICA,
					Float.valueOf(getMessagesProperties().getProperty(DEFAULT_FONT_SIZE)));
			contentStream.newLine();
			countLines += 1;
			contentStream = insertValueFlushRight(contentStream,
					getMessagesProperties().getProperty(PDF_LABEL_NUMBER_OF_PATIENTS), String.valueOf(numberOfPatients),
					PDType1Font.HELVETICA, Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
					Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
			countLines += 1;
			contentStream = insertValueFlushRight(contentStream,
					getMessagesProperties().getProperty(PDF_LABEL_NUMBER_OF_TREATMENTS),
					String.valueOf(numberOfTreatments), PDType1Font.HELVETICA,
					Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
					Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
			countLines += 1;
			contentStream = insertValueFlushRight(contentStream, getMessagesProperties().getProperty(PDF_LABEL_TARMED),
					String.valueOf(String.format("%.2f", sumOfTarmed)), PDType1Font.HELVETICA,
					Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
					Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
			countLines += 1;
			contentStream = insertValueFlushRight(contentStream, getMessagesProperties().getProperty(PDF_LABEL_MEDICAL),
					String.valueOf(String.format("%.2f", sumOfMedical)), PDType1Font.HELVETICA,
					Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
					Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
			countLines += 1;
			contentStream = insertValueFlushRight(contentStream, getMessagesProperties().getProperty(PDF_LABEL_LABOR),
					String.valueOf(String.format("%.2f", sumOfLaboratory)), PDType1Font.HELVETICA,
					Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
					Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
			countLines += 1;
			contentStream = insertValueFlushRight(contentStream,
					getMessagesProperties().getProperty(PDF_LABEL_INTERNAL_SERVICE),
					String.valueOf(String.format("%.2f", sumOfInternalServices)), PDType1Font.HELVETICA,
					Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
					Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
			countLines += 1;
			contentStream = insertValueFlushRight(contentStream,
					getMessagesProperties().getProperty(PDF_LABEL_PANDEMIC),
					String.valueOf(String.format("%.2f", sumOfPandemic)), PDType1Font.HELVETICA,
					Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
					Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
			countLines += 1;
			contentStream = insertValueFlushRight(contentStream,
					getMessagesProperties().getProperty(PDF_LABEL_INTERNAL_ASSETS),
					String.valueOf(String.format("%.2f", sumOfInternalAssets)), PDType1Font.HELVETICA,
					Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
					Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
			countLines += 1;
			contentStream = insertValueFlushRight(contentStream, getMessagesProperties().getProperty(PDF_LABEL_MIGEL),
					String.valueOf(String.format("%.2f", sumOfMigel)), PDType1Font.HELVETICA,
					Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
					Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
			countLines += 1;
			contentStream.setFont(PDType1Font.HELVETICA_BOLD,
					Float.valueOf(getMessagesProperties().getProperty(DEFAULT_FONT_SIZE)));
			contentStream = insertValueFlushRight(contentStream, getMessagesProperties().getProperty(PDF_LABEL_TOTAL),
					String.valueOf(String.format("%.2f",
							sumOfTarmed + sumOfMedical + sumOfLaboratory + sumOfInternalServices + sumOfPandemic
									+ sumOfInternalAssets + sumOfMigel)),
					PDType1Font.HELVETICA, Float.valueOf(getMessagesProperties().getProperty(INTERMEDIATE_FONT_SIZE)),
					Integer.valueOf(getMessagesProperties().getProperty(SHORT_INDENT)));
			countLines += 1;
			contentStream.newLine();
			countLines = Integer.valueOf(getMessagesProperties().getProperty(LINES_PER_PAGE));
			contentStream.endText();
			contentStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	private PDPageContentStream insertValueFlushRight(PDPageContentStream contentStream, String label, String value,
			PDType1Font font, float fontSize, float indent) throws IOException {
		contentStream.showText(label);
		float wordLength = getTextWidth(font, fontSize, value);
		float nextX = indent - wordLength;
		contentStream.newLineAtOffset(nextX, Float.valueOf(getMessagesProperties().getProperty(NO_MOVE)));
		contentStream.showText(value);
		contentStream.newLineAtOffset(-nextX, Float.valueOf(getMessagesProperties().getProperty(NO_MOVE)));
		contentStream.newLine();
		return contentStream;
	}

	private String generateHumanReadableDate(String inputDate) {
		String day = inputDate.substring(8, 10);
		String month = inputDate.substring(5, 7);
		String year = inputDate.substring(0, 4);
		return day + "." + month + "." + year;
	}

	private static float getTextWidth(PDType1Font font, float fontSize, String text) throws IOException {
		return (font.getStringWidth(text) / 1000.0f) * fontSize;
	}

	private PDPageContentStream returnNewPageContent(PDDocument doc) throws IOException {
		PDPage page = new PDPage();
		pageCount++;
		doc.addPage(page);
		PDPageContentStream contentStream = new PDPageContentStream(doc, page);
		return addDocumentInformation(contentStream);
	}

	private PDPageContentStream addDocumentInformation(PDPageContentStream contentStream) {
		try {

			contentStream.setFont(getFont(), Float.valueOf(getMessagesProperties().getProperty(DEFAULT_FONT_SIZE)));
			contentStream.setLeading(Float.valueOf(getMessagesProperties().getProperty(LEADING)));
			contentStream.beginText();
			contentStream.setFont(PDType1Font.HELVETICA,
					Float.valueOf(getMessagesProperties().getProperty(DEFAULT_FONT_SIZE)));
			contentStream.newLineAtOffset(Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_LEFT_1)),
					Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_LEFT_2)));
			contentStream.showText(getMandantInfo());
			contentStream.newLineAtOffset(Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_RIGHT_2)),
					Float.valueOf(getMessagesProperties().getProperty(NO_MOVE)));
			contentStream.showText(String.valueOf(pageCount));
			contentStream.setFont(PDType1Font.HELVETICA_BOLD,
					Float.valueOf(getMessagesProperties().getProperty(DEFAULT_FONT_SIZE)));
			contentStream.newLineAtOffset(-Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_RIGHT_2)),
					Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_Y_TOP)));
			contentStream.showText(getReportTitle());
			contentStream.setFont(PDType1Font.HELVETICA,
					Float.valueOf(getMessagesProperties().getProperty(DEFAULT_FONT_SIZE)));
			contentStream.newLineAtOffset(Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_RIGHT_1)),
					Float.valueOf(getMessagesProperties().getProperty(NO_MOVE)));
			contentStream.showText(getReportingDateTime());
			contentStream.endText();
			contentStream.setFont(PDType1Font.HELVETICA,
					Float.valueOf(getMessagesProperties().getProperty(DEFAULT_FONT_SIZE)));
			contentStream.setLineWidth(Float.valueOf(getMessagesProperties().getProperty(LINE_WIDTH)));
			contentStream.moveTo(Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_LEFT_1)),
					Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_Y_LEFT)));
			contentStream.lineTo(Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_UPPER_LINE)),
					Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_Y_LEFT)));
			contentStream.stroke();
			contentStream.moveTo(Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_LEFT_1)),
					Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_Y_RIGHT)));
			contentStream.lineTo(Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_X_UPPER_LINE)),
					Float.valueOf(getMessagesProperties().getProperty(DEFAULT_OFFSET_Y_RIGHT)));
			contentStream.stroke();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contentStream;
	}

	private void writeLine(PDDocument doc, PDPageContentStream contentStream, String textLine) throws IOException {
		contentStream.showText(textLine);
		contentStream.newLine();
	}

	private static PDFont getFont() throws IOException {
		return PDType1Font.HELVETICA;
	}

	public String getMandantInfo() {
		return mandantInfo;
	}

	public void setMandantInfo(String mandantInfo) {
		this.mandantInfo = mandantInfo;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public String getReportingDateTime() {
		return reportingDateTime;
	}

	public void setReportingDateTime(String reportingDateTime) {
		this.reportingDateTime = reportingDateTime;
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
