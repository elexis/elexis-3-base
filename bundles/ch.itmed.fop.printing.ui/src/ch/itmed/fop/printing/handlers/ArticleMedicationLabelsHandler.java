package ch.itmed.fop.printing.handlers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.util.SWTHelper;
import ch.itmed.fop.printing.print.LabelPrintService;
import ch.itmed.fop.printing.resources.Messages;

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

			IPatient patient = consultation.getPatient();
			billedItems.stream().filter(iBilled -> iBilled.getBillable() instanceof IArticle).forEach(iBilled -> {
				try {
					processBilledItem(iBilled, patient, medications);
				} catch (Exception e) {
					logger.error("Error processing billed item: {}", iBilled, e);
				}
			});

		} catch (Exception e) {
			handleException(e);
		}
		return null;
	}

	private void processBilledItem(IBilled iBilled, IPatient patient, List<IPrescription> medications)
			throws Exception {
		IArticle article = (IArticle) iBilled.getBillable();
		Optional<IPrescription> prescriptionOpt = findPrescriptionByBilledId(iBilled.getId().toString(), medications);
		double doubleAmount = iBilled.getAmount();
		int intAmount = (int) Math.ceil(doubleAmount);
		if (prescriptionOpt.isPresent() && LabelPrintService.hasDoseOrRemark(prescriptionOpt.get())) {
			LabelPrintService.printMedicationLabels(prescriptionOpt.get(), patient, intAmount, false);
		} else {
			handleAlternativePrescriptionOrDefault(article, patient, medications, intAmount);
		}
	}

	private void handleAlternativePrescriptionOrDefault(IArticle article, IPatient patient,
			List<IPrescription> medications, int amount) throws Exception {
		Optional<IPrescription> alternativePrescription = findAlternativePrescription(article, medications);

		if (alternativePrescription.isPresent()) {
			LabelPrintService.printMedicationLabels(alternativePrescription.get(), patient, amount, false);
		} else {
			LabelPrintService.printArticleLabels(article, patient, amount, false);
		}
	}

	private Optional<IPrescription> findAlternativePrescription(IArticle article, List<IPrescription> medications) {
		return medications.stream()
				.filter(p -> p.getEntryType() == EntryType.FIXED_MEDICATION
						|| p.getEntryType() == EntryType.RESERVE_MEDICATION
						|| p.getEntryType() == EntryType.SYMPTOMATIC_MEDICATION)
				.filter(p -> p.getArticle() != null && p.getArticle().equals(article)).findFirst();
	}

	private void handleException(Exception e) {
		String msg = e.getMessage();
		if (msg != null && (msg.equals("No patient selected") || msg.equals("No consultation selected"))) {
			return;
		}
		SWTHelper.showError(Messages.DefaultError_Title, Messages.DefaultError_Message);
		logger.error(e.getLocalizedMessage(), e);
	}

	private Optional<IPrescription> findPrescriptionByBilledId(String billedId, List<IPrescription> prescriptions) {
		return prescriptions.stream().filter(
				p -> billedId.equals(p.getExtInfo(ch.elexis.core.model.prescription.Constants.FLD_EXT_VERRECHNET_ID)))
				.findFirst();
	}
}
