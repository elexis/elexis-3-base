package ch.itmed.fop.printing.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.constants.OrderLabelPrintConstants;
import ch.elexis.core.mediorder.MediorderLabelCode;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IOrderEntry;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.IStock;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.itmed.fop.printing.print.LabelPrintService;
import ch.itmed.fop.printing.print.OrderEntryLabelParameter;

/**
 * Called by the order management (elexis-3-core, OrderEntryLabelPrinter) via
 * {@link OrderLabelPrintConstants#COMMAND_ID}. Returns a
 * <code>Map&lt;String, Integer&gt;</code> with the number of printed and
 * skipped labels.
 */
public class OrderEntryLabelPrintHandler extends AbstractHandler {

	private static final Logger logger = LoggerFactory.getLogger(OrderEntryLabelPrintHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Map<String, Integer> entries = OrderEntryLabelParameter
				.parse(event.getParameter(OrderLabelPrintConstants.PARAM_ENTRIES));
		int printed = 0;
		int skippedNoPrinter = 0;
		for (Map.Entry<String, Integer> entry : entries.entrySet()) {
			int amount = entry.getValue();
			try {
				Optional<IOrderEntry> orderEntry = CoreModelServiceHolder.get().load(entry.getKey(),
						IOrderEntry.class);
				Optional<IPatient> patient = orderEntry.flatMap(this::getPatient);
				if (orderEntry.isEmpty() || patient.isEmpty() || orderEntry.get().getArticle() == null) {
					logger.warn("Skipping order entry [{}] without patient stock or article", entry.getKey()); //$NON-NLS-1$
					continue;
				}
				int entryPrinted = print(orderEntry.get(), patient.get(), amount);
				printed += entryPrinted;
				skippedNoPrinter += amount - entryPrinted;
			} catch (Exception e) {
				logger.error("Error printing labels for order entry [{}]", entry.getKey(), e); //$NON-NLS-1$
			}
		}
		Map<String, Integer> result = new HashMap<>();
		result.put(OrderLabelPrintConstants.RESULT_PRINTED, printed);
		result.put(OrderLabelPrintConstants.RESULT_SKIPPED_NO_PRINTER, skippedNoPrinter);
		return result;
	}

	private int print(IOrderEntry orderEntry, IPatient patient, int amount) throws Exception {
		IArticle article = orderEntry.getArticle();
		List<String> mediorderBarcodes = MediorderLabelCode.encodeDelivery(orderEntry, amount);
		List<IPrescription> medications = patient.getMedication(LabelPrintService.MEDICATION_ENTRY_TYPES);
		Optional<IPrescription> prescription = LabelPrintService.findUniquePrescription(article, medications);
		boolean requirePrinter = true;
		if (prescription.isPresent() && LabelPrintService.hasDoseOrRemark(prescription.get())) {
			return LabelPrintService.printMedicationLabels(prescription.get(), patient, amount, requirePrinter,
					mediorderBarcodes);
		}
		return LabelPrintService.printArticleLabels(article, patient, amount, requirePrinter, mediorderBarcodes);
	}

	private Optional<IPatient> getPatient(IOrderEntry orderEntry) {
		IStock stock = orderEntry.getStock();
		IPerson owner = stock != null ? stock.getOwner() : null;
		if (owner == null || !owner.isPatient()) {
			return Optional.empty();
		}
		return Optional.ofNullable(owner.asIPatient());
	}
}
