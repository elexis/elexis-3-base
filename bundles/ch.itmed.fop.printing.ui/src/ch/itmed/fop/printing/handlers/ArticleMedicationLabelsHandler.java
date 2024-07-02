package ch.itmed.fop.printing.handlers;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
	private static Logger logger = LoggerFactory.getLogger(ArticleMedicationLabelsHandler.class);

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			Optional<IEncounter> consultation = ContextServiceHolder.get().getTyped(IEncounter.class);
			if (consultation.isPresent()) {
				List<IBilled> verrechnet = consultation.get().getBilled();
				List<IPrescription> medication = consultation.get().getPatient().getMedication(Arrays.asList(
						EntryType.FIXED_MEDICATION, EntryType.RESERVE_MEDICATION, EntryType.SYMPTOMATIC_MEDICATION));

				StructuredSelection selection = CoreUiUtil.getCommandSelection("ch.elexis.VerrechnungsDisplay", false);
				if (selection != null && !selection.isEmpty() && selection.getFirstElement() instanceof IBilled) {
					verrechnet = (List<IBilled>) selection.toList();
				}

				for (IBilled iBilled : verrechnet) {
					if (iBilled.getBillable() instanceof IArticle) {
						IArticle article = (IArticle) iBilled.getBillable();
						// filter only medications which are billed on selected encounter
						Optional<IPrescription> prescription = medication.stream()
								.filter(m -> m.getArticle() != null && m.getArticle().equals(article)).findFirst();
						if (prescription.isPresent()) {
							// create medication labels for articles with medication
							for (int i = 0; i < iBilled.getAmount(); i++) {
								InputStream xmlDoc = MedicationLabel.create(prescription.get());
								InputStream pdf = PdfTransformer.transformXmlToPdf(xmlDoc,
										ResourceProvider.getXslTemplateFile(PreferenceConstants.MEDICATION_LABEL_ID));
								String docName = PreferenceConstants.MEDICATION_LABEL;
								IPreferenceStore settingsStore = SettingsProvider.getStore(docName);

								String printerName = settingsStore
										.getString(PreferenceConstants.getDocPreferenceConstant(docName, 0));
								logger.info("Printing document MedicationLabel on printer: " + printerName); //$NON-NLS-1$
								PrintProvider.printPdf(pdf, printerName);
							}
						} else {
							// create article labels without medication
							for (int i = 0; i < iBilled.getAmount(); i++) {
								InputStream xmlDoc = ArticleLabel.create(article);
								String dosageInstructions = getDosageInstructions(article);
								InputStream pdf;
								String docName;
								if (!dosageInstructions.isEmpty()) {
									pdf = PdfTransformer.transformXmlToPdf(xmlDoc, ResourceProvider
											.getXslTemplateFile(PreferenceConstants.ARTICLE_MEDIC_LABEL_ID));
									docName = PreferenceConstants.ARTICLE_MEDIC_LABEL;
								} else {
									pdf = PdfTransformer.transformXmlToPdf(xmlDoc,
											ResourceProvider.getXslTemplateFile(PreferenceConstants.ARTICLE_LABEL_ID));
									docName = PreferenceConstants.ARTICLE_LABEL;
								}
								IPreferenceStore settingsStore = SettingsProvider.getStore(docName);
								String printerName = settingsStore
										.getString(PreferenceConstants.getDocPreferenceConstant(docName, 0));
								logger.info("Printing document ArticleLabel on printer: " + printerName); //$NON-NLS-1$
								PrintProvider.printPdf(pdf, printerName);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			String msg = e.getMessage();
			if (msg != null) {
				if (msg.equals("No patient selected") || msg.equals("No consultation selected")) { //$NON-NLS-1$ //$NON-NLS-2$
					// Make sure we don't show 2 error messages.
					return null;
				}
			}
			SWTHelper.showError(Messages.DefaultError_Title, Messages.DefaultError_Message);
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
    private static String getDosageInstructions(IArticle article) {
        Optional<IArticleDefaultSignature> signatureOpt = MedicationServiceHolder.get().getDefaultSignature(article);
        if (signatureOpt.isPresent()) {
            IArticleDefaultSignature signature = signatureOpt.get();
            return signature.getComment();
        }
        return "";
    }
}
