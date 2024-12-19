package ch.itmed.fop.printing.handlers;

import java.io.InputStream;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.dialogs.OrderImportDialog;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.SettingsProvider;
import ch.itmed.fop.printing.print.PrintProvider;
import ch.itmed.fop.printing.resources.ResourceProvider;
import ch.itmed.fop.printing.xml.documents.PdfTransformer;
import ch.itmed.fop.printing.xml.documents.RowaArticleMedicationLabel;

public class RowaArticleMedicationLabelHandler extends AbstractHandler{
	private static Logger logger = LoggerFactory.getLogger(RowaArticleMedicationLabelHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		String patientId = event.getParameter(OrderImportDialog.ROWA_ARTICL_MEDICATION_LABEL_PATIENT);
		String gtin = event.getParameter(OrderImportDialog.ROWA_ARTICL_MEDICATION_LABEL_ARTICLE);
		IPrescription prescription = null;

		IPatient patient = CoreModelServiceHolder.get().load(patientId, IPatient.class)
				.orElseThrow(() -> new ExecutionException("Patient not found for ID: " + patientId));
		
		IQuery<IPrescription> query = CoreModelServiceHolder.get().getQuery(IPrescription.class);
		query.and(ModelPackage.Literals.IPRESCRIPTION__PATIENT, COMPARATOR.EQUALS, patient);
		List<IPrescription> list = query.execute();
		for (IPrescription pres : list) {
			if (pres.getArticle().getGtin().equals(gtin)) {
				prescription = pres;
			}
		}

		if (prescription == null) {
			logger.info("Error prescription is null");
		}

		try {
			InputStream xmlDoc = RowaArticleMedicationLabel.create(prescription, patient, gtin);
			InputStream pdf = PdfTransformer.transformXmlToPdf(xmlDoc,
					ResourceProvider.getXslTemplateFile(PreferenceConstants.ROWA_ARTICLE_MEDICATION_LABEL_ID));

			String docName = PreferenceConstants.ROWA_ARTICLE_MEDICATION_LABEL;
			IPreferenceStore settingsStore = SettingsProvider.getStore(docName);

			String printerName = settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 0));
			PrintProvider.printPdf(pdf, printerName);
		} catch (Exception e) {
			logger.info("Error printing MediorderMedication label", e);
		}
		return null;
	}

}
