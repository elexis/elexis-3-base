package ch.elexis.base.ch.ebanking.command;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;

import ch.elexis.base.ch.ebanking.esr.ESRRecord;
import ch.elexis.base.ch.ebanking.esr.ESRRecordDialog;
import ch.elexis.data.Query;

public class OpenESRWithinvoiceIdHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		String invoiceId = event.getParameter("ch.elexis.ebanking_ch.command.openESR.InvoiceId");
		String paymentDate =
			event.getParameter("ch.elexis.ebanking_ch.command.openESR.PaymentDate");
		if (StringUtils.isNotBlank(invoiceId) && StringUtils.isNotBlank(paymentDate)) {
			Query<ESRRecord> qbe = new Query<ESRRecord>(ESRRecord.class);
			qbe.add(ESRRecord.RECHNUNGS_ID, Query.EQUALS, invoiceId);
			qbe.add("Verarbeitet", Query.EQUALS, paymentDate);
			List<ESRRecord> esrRecords = qbe.execute();
			if (!esrRecords.isEmpty()) {
				for (ESRRecord esrRecord : esrRecords) {
					ESRRecordDialog erd =
						new ESRRecordDialog(Display.getDefault().getActiveShell(), esrRecord);
					erd.open();
				}
			}
		}
		return null;
	}
}
