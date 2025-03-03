package ch.elexis.base.ch.ebanking.print;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import ch.elexis.base.ch.ebanking.model.IEsrRecord;

@XmlRootElement(name = "record")
public class ESRJournalRecord {

	@XmlElement
	private String valutaDate;
	@XmlElement
	private String invoiceNumber;
	@XmlElement
	private String patient;
	@XmlElement
	private String amount;

	public ESRJournalRecord() {
		// TODO Auto-generated constructor stub
	}

	public ESRJournalRecord(IEsrRecord record) {
		valutaDate = record.getValutaDateString();
		amount = record.getAmount().getAmountAsString();
		if (record.hasBookedDate()) {
			invoiceNumber = record.getInvoice().getNumber();
			patient = record.getPatient().getLabel();
		}
	}

}
