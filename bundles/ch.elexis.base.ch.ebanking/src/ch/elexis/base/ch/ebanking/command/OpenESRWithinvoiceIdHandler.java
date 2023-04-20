package ch.elexis.base.ch.ebanking.command;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;

import ch.elexis.base.ch.ebanking.esr.ESRRecordDialog;
import ch.elexis.base.ch.ebanking.model.IEsrRecord;
import ch.elexis.base.ch.ebanking.model.service.holder.ModelServiceHolder;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.rgw.tools.TimeTool;

public class OpenESRWithinvoiceIdHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String invoiceId = event.getParameter("ch.elexis.ebanking_ch.command.openESR.InvoiceId"); //$NON-NLS-1$
		String paymentDate = event.getParameter("ch.elexis.ebanking_ch.command.openESR.PaymentDate"); //$NON-NLS-1$
		if (StringUtils.isNotBlank(invoiceId) && StringUtils.isNotBlank(paymentDate)) {
			TimeTool paymentDateTimeTool = new TimeTool(paymentDate);
			IQuery<IEsrRecord> esrQuery = ModelServiceHolder.get().getQuery(IEsrRecord.class);
			esrQuery.and("rechnung", COMPARATOR.EQUALS, //$NON-NLS-1$
					CoreModelServiceHolder.get().load(invoiceId, IInvoice.class).get());
			esrQuery.and("verarbeitet", COMPARATOR.EQUALS, paymentDateTimeTool.toLocalDate()); //$NON-NLS-1$

			List<IEsrRecord> esrRecords = esrQuery.execute();
			if (!esrRecords.isEmpty()) {
				for (IEsrRecord esrRecord : esrRecords) {
					ESRRecordDialog erd = new ESRRecordDialog(Display.getDefault().getActiveShell(), esrRecord);
					erd.open();
				}
			}
		}
		return null;
	}
}
