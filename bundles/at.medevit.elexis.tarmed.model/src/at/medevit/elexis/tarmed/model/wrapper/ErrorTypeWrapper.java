package at.medevit.elexis.tarmed.model.wrapper;

import org.apache.commons.lang3.StringUtils;

public class ErrorTypeWrapper {

	private ch.fd.invoice440.response.ErrorType errorType44;

	private ch.fd.invoice450.response.ErrorType errorType45;

	private ch.fd.invoice500.response.ErrorType errorType50;

	public ErrorTypeWrapper(ch.fd.invoice440.response.ErrorType e) {
		this.errorType44 = e;
	}

	public ErrorTypeWrapper(ch.fd.invoice450.response.ErrorType e) {
		this.errorType45 = e;
	}
	
	public ErrorTypeWrapper(ch.fd.invoice500.response.ErrorType e) {
		this.errorType50 = e;
	}

	public String getCode() {
        if(errorType44 != null) {
			return errorType44.getCode();
        } else if (errorType45 != null) {
        	return errorType45.getCode();
		} else if (errorType50 != null) {
			return errorType50.getCode();
        }
		return StringUtils.EMPTY;
    }

	public String getText() {
		if (errorType44 != null) {
			return errorType44.getText();
		} else if (errorType45 != null) {
			return errorType45.getText();
		} else if (errorType50 != null) {
			return errorType50.getText();
		}
		return StringUtils.EMPTY;
	}

	public String getRecordId() {
		if (errorType44 != null && errorType44.getRecordId() != null) {
			return Integer.toString(errorType44.getRecordId().intValue());
		} else if (errorType45 != null && errorType45.getRecordId() != null) {
			return Integer.toString(errorType45.getRecordId().intValue());
		} else if (errorType50 != null && errorType50.getRecordId() != null) {
			return Integer.toString(errorType50.getRecordId().intValue());
		}
		return StringUtils.EMPTY;
	}
}
