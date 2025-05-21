package ch.itmed.fop.printing.handlers;

import java.io.InputStream;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.ui.dialogs.OrderImportDialog;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.SettingsProvider;
import ch.itmed.fop.printing.print.PrintProvider;
import ch.itmed.fop.printing.resources.ResourceProvider;
import ch.itmed.fop.printing.xml.documents.PdfTransformer;
import ch.itmed.fop.printing.xml.documents.RowaArticleMedicationLabel;

public class RowaArticleMedicationLabelHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String patientId = event.getParameter(OrderImportDialog.ROWA_ARTICL_MEDICATION_LABEL_PATIENT);
		String gtin = event.getParameter(OrderImportDialog.ROWA_ARTICL_MEDICATION_LABEL_ARTICLE);

		IPatient patient = getPatient(patientId);
		IPrescription prescription = getPatientPrescription(patient, gtin);

		try {
			InputStream xmlDoc = RowaArticleMedicationLabel.create(prescription, patient, gtin);
			InputStream pdf = PdfTransformer.transformXmlToPdf(xmlDoc,
					ResourceProvider.getXslTemplateFile(PreferenceConstants.ROWA_ARTICLE_MEDICATION_LABEL_ID));

			String docName = PreferenceConstants.ROWA_ARTICLE_MEDICATION_LABEL;
			String printer = SettingsProvider.getStore(docName)
					.getString(PreferenceConstants.getDocPreferenceConstant(docName, 0));

			PrintProvider.printPdf(pdf, printer);
		} catch (Exception e) {
			throw new ExecutionException("Error printing label " + PreferenceConstants.ROWA_ARTICLE_MEDICATION_LABEL,
					e);
		}
		return null;
	}

	private IPatient getPatient(String patientId) throws ExecutionException {
		IModelService coreModelService = OsgiServiceUtil
				.getService(IModelService.class, "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
				.orElseThrow(() -> new IllegalStateException());

		return coreModelService.load(patientId, IPatient.class)
				.orElseThrow(() -> new ExecutionException("Patient not found for ID: " + patientId));
	}

	private IPrescription getPatientPrescription(IPatient patient, String gtin) throws ExecutionException{
		List<IPrescription> medications = patient.getMedication(
				List.of(EntryType.FIXED_MEDICATION, EntryType.RESERVE_MEDICATION, EntryType.SYMPTOMATIC_MEDICATION));
		for (IPrescription pres : medications) {
			if (pres.getArticle().getGtin().equals(gtin)) {
				return pres;
			}
		}
		throw new ExecutionException("No prescription found for gtin " + gtin + " and patientId " + patient.getId());
	}
}