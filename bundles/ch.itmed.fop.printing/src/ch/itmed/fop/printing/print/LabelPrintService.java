package ch.itmed.fop.printing.print;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IArticleDefaultSignature;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.holder.MedicationServiceHolder;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.Setting;
import ch.itmed.fop.printing.resources.ResourceProvider;
import ch.itmed.fop.printing.xml.documents.ArticleLabel;
import ch.itmed.fop.printing.xml.documents.MedicationLabel;
import ch.itmed.fop.printing.xml.documents.PdfTransformer;

/**
 * Printing of medication and article labels, shared by the different label
 * print handlers.
 */
public final class LabelPrintService {

	private static final Logger logger = LoggerFactory.getLogger(LabelPrintService.class);

	/**
	 * Entry types of prescriptions considered when looking up a prescription for
	 * an article.
	 */
	public static final List<EntryType> MEDICATION_ENTRY_TYPES = Arrays.asList(EntryType.FIXED_MEDICATION,
			EntryType.RESERVE_MEDICATION, EntryType.SYMPTOMATIC_MEDICATION);

	private LabelPrintService() {
	}

	/**
	 * @param prescription
	 * @return <code>true</code> if the prescription has a dosage instruction or a
	 *         remark
	 */
	public static boolean hasDoseOrRemark(IPrescription prescription) {
		return prescription != null && (StringUtils.isNotBlank(prescription.getDosageInstruction())
				|| StringUtils.isNotBlank(prescription.getRemark()));
	}

	/**
	 * Find the prescription of the article, if it can be assigned unambiguously.
	 *
	 * @param article
	 * @param prescriptions
	 * @return the prescription, if exactly one fixed, reserve or symptomatic
	 *         prescription of the article exists
	 */
	public static Optional<IPrescription> findUniquePrescription(IArticle article,
			List<IPrescription> prescriptions) {
		if (article == null || prescriptions == null) {
			return Optional.empty();
		}
		List<IPrescription> matching = prescriptions.stream()
				.filter(p -> p != null && MEDICATION_ENTRY_TYPES.contains(p.getEntryType()))
				.filter(p -> article.equals(p.getArticle())).collect(Collectors.toList());
		return matching.size() == 1 ? Optional.of(matching.get(0)) : Optional.empty();
	}

	/**
	 * @param docName
	 * @return the printer configured for the document, may be empty
	 */
	public static String getPrinterName(String docName) {
		return Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 0));
	}

	/**
	 * @param docName
	 * @return <code>true</code> if a printer is configured for the document
	 */
	public static boolean hasPrinterConfigured(String docName) {
		return StringUtils.isNotBlank(getPrinterName(docName));
	}

	/**
	 * @param printerName
	 * @param requirePrinter
	 * @return <code>true</code> if printing is allowed with the printer name
	 */
	public static boolean isPrintAllowed(String printerName, boolean requirePrinter) {
		return !requirePrinter || StringUtils.isNotBlank(printerName);
	}

	/**
	 * Print medication labels of the prescription.
	 *
	 * @param prescription
	 * @param patient        the patient of the label, if <code>null</code> the
	 *                       active patient is used
	 * @param amount         number of labels
	 * @param requirePrinter if <code>true</code> nothing is printed if no printer
	 *                       is configured for the medication label
	 * @return number of labels sent to the printer
	 * @throws Exception
	 */
	public static int printMedicationLabels(IPrescription prescription, IPatient patient, int amount,
			boolean requirePrinter) throws Exception {
		return printMedicationLabels(prescription, patient, amount, requirePrinter, List.of());
	}

	/**
	 * Print medication labels of the prescription, see
	 * {@link #printMedicationLabels(IPrescription, IPatient, int, boolean)}.
	 *
	 * @param prescription
	 * @param patient
	 * @param amount
	 * @param requirePrinter
	 * @param mediorderBarcodes barcodes of the packages of a delivered mediorder
	 *                          article, one per label, may be empty
	 * @return number of labels sent to the printer
	 * @throws Exception
	 */
	public static int printMedicationLabels(IPrescription prescription, IPatient patient, int amount,
			boolean requirePrinter, List<String> mediorderBarcodes) throws Exception {
		String docName = PreferenceConstants.MEDICATION_LABEL;
		String printerName = getPrinterName(docName);
		if (!isPrintAllowed(printerName, requirePrinter)) {
			logger.info("No printer configured for document " + docName + ", skipping " + amount + " labels"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return 0;
		}
		File xslTemplate = ResourceProvider.getXslTemplateFile(PreferenceConstants.MEDICATION_LABEL_ID);
		logger.info("Printing document " + docName + " on printer: " + printerName); //$NON-NLS-1$ //$NON-NLS-2$
		for (int i = 0; i < amount; i++) {
			InputStream xmlDoc = MedicationLabel.create(prescription, patient,
					getMediorderBarcode(mediorderBarcodes, i));
			InputStream pdf = PdfTransformer.transformXmlToPdf(xmlDoc, xslTemplate);
			PrintProvider.printPdf(pdf, printerName);
		}
		return Math.max(amount, 0);
	}

	/**
	 * Print article labels of the article. If the article has a default signature
	 * and a printer is configured for the article medication label, the article
	 * medication label is used.
	 *
	 * @param article
	 * @param patient        the patient of the label, if <code>null</code> the
	 *                       active patient is used
	 * @param amount         number of labels
	 * @param requirePrinter if <code>true</code> nothing is printed if no printer
	 *                       is configured for the chosen label
	 * @return number of labels sent to the printer
	 * @throws Exception
	 */
	public static int printArticleLabels(IArticle article, IPatient patient, int amount, boolean requirePrinter)
			throws Exception {
		return printArticleLabels(article, patient, amount, requirePrinter, List.of());
	}

	/**
	 * Print article labels of the article, see
	 * {@link #printArticleLabels(IArticle, IPatient, int, boolean)}.
	 *
	 * @param article
	 * @param patient
	 * @param amount
	 * @param requirePrinter
	 * @param mediorderBarcodes barcodes of the packages of a delivered mediorder
	 *                          article, one per label, may be empty
	 * @return number of labels sent to the printer
	 * @throws Exception
	 */
	public static int printArticleLabels(IArticle article, IPatient patient, int amount, boolean requirePrinter,
			List<String> mediorderBarcodes) throws Exception {
		String docName;
		File xslTemplate;
		if (getDosageInstructions(article).isPresent() && hasPrinterConfigured(PreferenceConstants.ARTICLE_MEDIC_LABEL)) {
			xslTemplate = ResourceProvider.getXslTemplateFile(PreferenceConstants.ARTICLE_MEDIC_LABEL_ID);
			docName = PreferenceConstants.ARTICLE_MEDIC_LABEL;
		} else {
			xslTemplate = ResourceProvider.getXslTemplateFile(PreferenceConstants.ARTICLE_LABEL_ID);
			docName = PreferenceConstants.ARTICLE_LABEL;
		}
		String printerName = getPrinterName(docName);
		if (!isPrintAllowed(printerName, requirePrinter)) {
			logger.info("No printer configured for document " + docName + ", skipping " + amount + " labels"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return 0;
		}
		logger.info("Printing document " + docName + " on printer: " + printerName); //$NON-NLS-1$ //$NON-NLS-2$
		boolean hasMediorderBarcodes = mediorderBarcodes != null && !mediorderBarcodes.isEmpty();
		InputStream xmlDoc = null;
		for (int i = 0; i < amount; i++) {
			if (xmlDoc == null || hasMediorderBarcodes) {
				xmlDoc = ArticleLabel.create(article, patient, getMediorderBarcode(mediorderBarcodes, i));
			} else {
				xmlDoc.reset();
			}
			InputStream pdf = PdfTransformer.transformXmlToPdf(xmlDoc, xslTemplate);
			PrintProvider.printPdf(pdf, printerName);
		}
		return Math.max(amount, 0);
	}

	private static String getMediorderBarcode(List<String> mediorderBarcodes, int label) {
		return mediorderBarcodes != null && label < mediorderBarcodes.size() ? mediorderBarcodes.get(label) : null;
	}

	private static Optional<String> getDosageInstructions(IArticle article) {
		return MedicationServiceHolder.get().getDefaultSignature(article)
				.map(IArticleDefaultSignature::getSignatureAsDosisString);
	}
}
