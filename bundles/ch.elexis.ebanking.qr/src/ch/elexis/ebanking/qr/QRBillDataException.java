package ch.elexis.ebanking.qr;

import org.apache.commons.lang3.StringUtils;
import ch.elexis.core.model.IContact;

public class QRBillDataException extends Exception {

	public enum SourceType {
		CREDITOR, DEBITOR, AMOUNT, REMARK, HEADER, UNKNOWN
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private IContact contact;
	private SourceType sourceType;

	public QRBillDataException(SourceType sourceType, String string) {
		super(string);
		this.sourceType = sourceType;
	}

	public QRBillDataException(SourceType sourceType, String string, IContact contact) {
		super(string);
		this.sourceType = sourceType;
		this.contact = contact;
	}

	@Override
	public String getMessage() {
		return getSourceType() + StringUtils.SPACE + super.getMessage();
	}

	public IContact getContact() {
		return contact;
	}

	public void setContact(IContact contact) {
		this.contact = contact;
	}

	public SourceType getSourceType() {
		return sourceType;
	}
}
