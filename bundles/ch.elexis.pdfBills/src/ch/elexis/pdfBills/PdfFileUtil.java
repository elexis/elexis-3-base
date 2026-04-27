package ch.elexis.pdfBills;

import ch.elexis.core.data.interfaces.IRnOutputter.TYPE;
import ch.elexis.core.model.InvoiceState;

public class PdfFileUtil {

	private static String getFileNameEnding(String start, TYPE type, InvoiceState newInvoiceState) {
		StringBuilder sb = new StringBuilder();
		sb.append(start);
		if (newInvoiceState == InvoiceState.DEMAND_NOTE_1_PRINTED) {
			sb.append("_m1");
		} else if (newInvoiceState == InvoiceState.DEMAND_NOTE_2_PRINTED) {
			sb.append("_m2");
		} else if (newInvoiceState == InvoiceState.DEMAND_NOTE_3_PRINTED) {
			sb.append("_m3");
		}
		if (type == TYPE.COPY) {
			sb.append("_copy");
		}
		sb.append(".pdf");
		return sb.toString();
	}

	public static String getFileName(String billNr, String start, TYPE type, InvoiceState newInvoiceState) {
		return billNr + getFileNameEnding(start, type, newInvoiceState);
	}

}
