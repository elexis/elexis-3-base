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
import ch.elexis.core.model.IStockEntry;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.OrderServiceHolder;
import ch.itmed.fop.printing.print.LabelPrintService;
import ch.itmed.fop.printing.print.OrderEntryLabelParameter;

/**
 * Called by the order management (elexis-3-core, OrderEntryLabelPrinter) via
 * {@link OrderLabelPrintConstants#COMMAND_ID}, for delivered order entries
 * ({@link OrderLabelPrintConstants#PARAM_ENTRIES}) and for articles taken from
 * stock into patient stock entries
 * ({@link OrderLabelPrintConstants#PARAM_STOCK_ENTRIES}). Returns a
 * <code>Map&lt;String, Integer&gt;</code> with the number of printed and
 * skipped labels.
 */
public class OrderEntryLabelPrintHandler extends AbstractHandler {

	private static final Logger logger = LoggerFactory.getLogger(OrderEntryLabelPrintHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// local counters, the handler instance is shared by all executions
		int[] counts = new int[2]; // [printed, skippedNoPrinter]
		OrderEntryLabelParameter.parse(event.getParameter(OrderLabelPrintConstants.PARAM_ENTRIES))
				.forEach((id, amount) -> printOrderEntry(id, amount, counts));
		OrderEntryLabelParameter.parse(event.getParameter(OrderLabelPrintConstants.PARAM_STOCK_ENTRIES))
				.forEach((id, amount) -> printStockEntry(id, amount, counts));
		Map<String, Integer> result = new HashMap<>();
		result.put(OrderLabelPrintConstants.RESULT_PRINTED, counts[0]);
		result.put(OrderLabelPrintConstants.RESULT_SKIPPED_NO_PRINTER, counts[1]);
		return result;
	}

	private void printOrderEntry(String id, int amount, int[] counts) {
		Optional<IOrderEntry> orderEntry = CoreModelServiceHolder.get().load(id, IOrderEntry.class);
		Optional<IPatient> patient = orderEntry.flatMap(entry -> getPatient(entry.getStock()));
		if (orderEntry.isEmpty() || patient.isEmpty() || orderEntry.get().getArticle() == null) {
			logger.warn("Skipping order entry [{}] without patient stock or article", id); //$NON-NLS-1$
			return;
		}
		print(orderEntry.get().getArticle(), patient.get(), amount,
				MediorderLabelCode.encodeDelivery(orderEntry.get(), amount), id, counts);
	}

	private void printStockEntry(String id, int amount, int[] counts) {
		Optional<IStockEntry> stockEntry = CoreModelServiceHolder.get().load(id, IStockEntry.class);
		Optional<IPatient> patient = stockEntry.flatMap(entry -> getPatient(entry.getStock()));
		if (stockEntry.isEmpty() || patient.isEmpty() || stockEntry.get().getArticle() == null) {
			logger.warn("Skipping stock entry [{}] without patient stock or article", id); //$NON-NLS-1$
			return;
		}
		print(stockEntry.get().getArticle(), patient.get(), amount,
				MediorderLabelCode.encodeTakenFromStock(stockEntry.get(), amount, OrderServiceHolder.get()), id,
				counts);
	}

	private void print(IArticle article, IPatient patient, int amount, List<String> mediorderBarcodes, String id,
			int[] counts) {
		try {
			List<IPrescription> medications = patient.getMedication(LabelPrintService.MEDICATION_ENTRY_TYPES);
			Optional<IPrescription> prescription = LabelPrintService.findUniquePrescription(article, medications);
			boolean requirePrinter = true;
			int printed;
			if (prescription.isPresent() && LabelPrintService.hasDoseOrRemark(prescription.get())) {
				printed = LabelPrintService.printMedicationLabels(prescription.get(), patient, amount, requirePrinter,
						mediorderBarcodes);
			} else {
				printed = LabelPrintService.printArticleLabels(article, patient, amount, requirePrinter,
						mediorderBarcodes);
			}
			counts[0] += printed;
			counts[1] += amount - printed;
		} catch (Exception e) {
			logger.error("Error printing labels for [{}]", id, e); //$NON-NLS-1$
		}
	}

	private Optional<IPatient> getPatient(IStock stock) {
		IPerson owner = stock != null ? stock.getOwner() : null;
		if (owner == null || !owner.isPatient()) {
			return Optional.empty();
		}
		return Optional.ofNullable(owner.asIPatient());
	}
}
