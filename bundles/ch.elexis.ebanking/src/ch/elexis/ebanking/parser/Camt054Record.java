package ch.elexis.ebanking.parser;

import java.util.Date;

/**
 * Holds values from a Camt054 File
 *
 * @author med1
 *
 */
public class Camt054Record {
	// wert
	private String amount;
	// ESR-Referenznummer oder Creditor Reference nach ISO11649
	private String reference;

	// Teilnehmernummer
	private String tn;

	private Date bookingDate;
	private Date valuDate;
	private Date readDate;

	private String mode;

	public Camt054Record(String mode, String amount, String reference, String tn, Date readDate, Date bookingDate,
			Date valueDate) throws Camet054Exception {
		super();
		this.amount = amount;
		this.reference = reference;
		this.tn = tn;
		this.bookingDate = bookingDate;
		this.valuDate = valueDate;
		this.readDate = readDate;
		this.mode = mode;

		validate();
	}

	private void validate() throws Camet054Exception {
		try {
			if (Integer.parseInt(amount) < 0) {
				throw new Camet054Exception("amount is negativ: " + amount); //$NON-NLS-1$
			}
		} catch (NumberFormatException e) {
			throw new Camet054Exception("amount not valid", e); //$NON-NLS-1$
		}

		if (bookingDate != null && bookingDate.before(new Date(0))) {
			throw new Camet054Exception("booking date is not valid: " + bookingDate); //$NON-NLS-1$
		}

		if (valuDate != null && valuDate.before(new Date(0))) {
			throw new Camet054Exception("valu date is not valid: " + valuDate); //$NON-NLS-1$
		}

		if (readDate != null && readDate.before(new Date(0))) {
			throw new Camet054Exception("read date is not valid: " + readDate); //$NON-NLS-1$
		}
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getMode() {
		return mode;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getTn() {
		return tn;
	}

	public void setTn(String tn) {
		this.tn = tn;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public Date getValuDate() {
		return valuDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public void setValuDate(Date valuDate) {
		this.valuDate = valuDate;
	}

	public Date getReadDate() {
		return readDate;
	}

	@Override
	public String toString() {
		return "Camt054Record [mode=" + mode + ", reference=" + reference + ", amount=" + amount + ", tn=" + tn //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ ", bookingDate=" + bookingDate + ", valuDate=" + valuDate + ", readDate=" + readDate + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
}
