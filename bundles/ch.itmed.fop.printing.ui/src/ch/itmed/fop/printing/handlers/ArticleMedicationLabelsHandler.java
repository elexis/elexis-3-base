package ch.itmed.fop.printing.handlers;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IArticleDefaultSignature;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.MedicationServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.util.SWTHelper;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.SettingsProvider;
import ch.itmed.fop.printing.print.PrintProvider;
import ch.itmed.fop.printing.resources.Messages;
import ch.itmed.fop.printing.resources.ResourceProvider;
import ch.itmed.fop.printing.xml.documents.ArticleLabel;
import ch.itmed.fop.printing.xml.documents.MedicationLabel;
import ch.itmed.fop.printing.xml.documents.PdfTransformer;

public class ArticleMedicationLabelsHandler extends AbstractHandler {
	private static final Logger logger = LoggerFactory.getLogger(ArticleMedicationLabelsHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			Optional<IEncounter> consultationOpt = ContextServiceHolder.get().getTyped(IEncounter.class);
			if (!consultationOpt.isPresent()) {
				SWTHelper.showError(Messages.DefaultError_Title, Messages.DefaultError_Message);
				return null;
			}
			IEncounter consultation = consultationOpt.get();
			List<IBilled> billedItems = consultation.getBilled();
			List<IPrescription> medications = consultation.getPatient()
					.getMedication(Arrays.asList(EntryType.FIXED_MEDICATION, EntryType.RESERVE_MEDICATION,
							EntryType.SYMPTOMATIC_MEDICATION, EntryType.SELF_DISPENSED));

			StructuredSelection selection = CoreUiUtil.getCommandSelection("ch.elexis.VerrechnungsDisplay", false);
			if (selection != null && !selection.isEmpty() && selection.getFirstElement() instanceof IBilled) {
				billedItems = selection.toList();
			}

			billedItems.stream().filter(iBilled -> iBilled.getBillable() instanceof IArticle).forEach(iBilled -> {
				try {
					processBilledItem(iBilled, medications);
				} catch (Exception e) {
					logger.error("Error processing billed item: {}", iBilled, e);
				}
			});

		} catch (Exception e) {
			handleException(e);
		}
		return null;
	}

	private void processBilledItem(IBilled iBilled, List<IPrescription> medications) throws Exception {
		IArticle article = (IArticle) iBilled.getBillable();
		Optional<IPrescription> prescriptionOpt = findPrescriptionByBilledId(iBilled.getId().toString(), medications);
		int amount = (int) iBilled.getAmount();
		if (prescriptionOpt.isPresent()) {
			IPrescription prescription = prescriptionOpt.get();
			String dosageInstruction = prescription.getDosageInstruction();
			String remark = prescription.getRemark();
			if (StringUtils.isNotBlank(dosageInstruction) || StringUtils.isNotBlank(remark)) {
				printMedicationLabels(prescription, amount);
			} else {
				printArticleLabels(article, amount);
			}
		} else {
			printArticleLabels(article, amount);
		}
	}

	private void printMedicationLabels(IPrescription prescription, int amount) throws Exception {
		File xslTemplate = ResourceProvider.getXslTemplateFile(PreferenceConstants.MEDICATION_LABEL_ID);
		String docName = PreferenceConstants.MEDICATION_LABEL;
		IPreferenceStore settingsStore = SettingsProvider.getStore(docName);
		String printerName = settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 0));
		logger.info("Printing document MedicationLabel on printer: " + printerName);
		for (int i = 0; i < amount; i++) {
			InputStream xmlDoc = MedicationLabel.create(prescription);
			InputStream pdf = PdfTransformer.transformXmlToPdf(xmlDoc, xslTemplate);
			PrintProvider.printPdf(pdf, printerName);
		}
	}

	private void printArticleLabels(IArticle article, int amount) throws Exception {
		InputStream xmlDoc = ArticleLabel.create(article);
		Optional<String> dosageInstructions = getDosageInstructions(article);
		String docName;
		File xslTemplate;
		IPreferenceStore settingsStore;
		if (dosageInstructions.isPresent() && hasPrinterConfigured(PreferenceConstants.ARTICLE_MEDIC_LABEL)) {
			xslTemplate = ResourceProvider.getXslTemplateFile(PreferenceConstants.ARTICLE_MEDIC_LABEL_ID);
			docName = PreferenceConstants.ARTICLE_MEDIC_LABEL;
		} else {
			xslTemplate = ResourceProvider.getXslTemplateFile(PreferenceConstants.ARTICLE_LABEL_ID);
			docName = PreferenceConstants.ARTICLE_LABEL;
		}
		settingsStore = SettingsProvider.getStore(docName);
		String printerName = settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 0));
		logger.info("Printing document " + docName + " on printer: " + printerName);
		for (int i = 0; i < amount; i++) {
			InputStream pdf = PdfTransformer.transformXmlToPdf(xmlDoc, xslTemplate);
			PrintProvider.printPdf(pdf, printerName);
		}
	}

	private boolean hasPrinterConfigured(String docName) {
		IPreferenceStore settingsStore = SettingsProvider.getStore(docName);
		String printerName = settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 0));
		return StringUtils.isNotBlank(printerName);
	}

	private void handleException(Exception e) {
		String msg = e.getMessage();
		if (msg != null && (msg.equals("No patient selected") || msg.equals("No consultation selected"))) {
			return;
		}
		SWTHelper.showError(Messages.DefaultError_Title, Messages.DefaultError_Message);
		logger.error(e.getLocalizedMessage(), e);
	}

	private static Optional<String> getDosageInstructions(IArticle article) {
		return MedicationServiceHolder.get().getDefaultSignature(article).map(IArticleDefaultSignature::getComment);
	}

	private Optional<IPrescription> findPrescriptionByBilledId(String billedId, List<IPrescription> prescriptions) {
		return prescriptions.stream().filter(
				p -> billedId.equals(p.getExtInfo(ch.elexis.core.model.prescription.Constants.FLD_EXT_VERRECHNET_ID)))
				.findFirst();
	}
}
