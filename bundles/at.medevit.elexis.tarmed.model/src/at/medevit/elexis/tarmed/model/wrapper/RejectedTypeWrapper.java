package at.medevit.elexis.tarmed.model.wrapper;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import ch.elexis.core.l10n.Messages;
import ch.fd.invoice400.response.CalledInErrorType;
import ch.fd.invoice400.response.ErrorBusinessType;
import ch.fd.invoice400.response.NotificationType;
import ch.fd.invoice400.response.RejectedErrorType;
import ch.fd.invoice440.response.ErrorType;

/**
 * Wrapper Object providing transparent access for different
 * (http://www.forum-datenaustausch.ch/invoice) versions.
 *
 * @author thomas
 *
 */
public class RejectedTypeWrapper {
	public static final String CODE_SEPARATOR = ",";

	private ch.fd.invoice400.response.RejectedType rejectedType40;
	private ch.fd.invoice400.response.CalledInType calledInType40;
	private ch.fd.invoice400.response.NotificationType notificationType40;

	private ch.fd.invoice440.response.RejectedType rejectedType44;

	private ch.fd.invoice450.response.RejectedType rejectedType45;

	private ch.fd.invoice500.response.RejectedType rejectedType50;

	public RejectedTypeWrapper(ch.fd.invoice440.response.RejectedType rejectedType) {
		this.rejectedType44 = rejectedType;
	}

	public RejectedTypeWrapper(ch.fd.invoice400.response.RejectedType rejectedType) {
		this.rejectedType40 = rejectedType;
	}

	public RejectedTypeWrapper(ch.fd.invoice400.response.CalledInType calledIn) {
		this.calledInType40 = calledIn;
	}

	public RejectedTypeWrapper(NotificationType resend) {
		this.notificationType40 = resend;
	}

	public RejectedTypeWrapper(ch.fd.invoice450.response.RejectedType rejectedType) {
		this.rejectedType45 = rejectedType;
	}

	public RejectedTypeWrapper(ch.fd.invoice500.response.RejectedType rejectedType) {
		this.rejectedType50 = rejectedType;
	}

	public String getExplanation() {
		if (rejectedType40 != null) {
			String explanation = rejectedType40.getExplanation();
			List<RejectedErrorType> errorList = rejectedType40.getError();
			for (RejectedErrorType errorType : errorList) {
				String additional = loadErrorBusinessValues(errorType.getErrorBusiness());
				if (additional != null && !additional.isEmpty()) {
					if (explanation == null) {
						explanation = additional;
					} else {
						explanation = explanation + ", " + additional;
					}
				}
			}
			return explanation;
		} else if (calledInType40 != null) {
			List<CalledInErrorType> errorList = calledInType40.getError();
			String additional = "";
			for (CalledInErrorType e : errorList) {
				additional += Messages.Sync_Major + e.getMajor() + " ";
			}
			String explanation = calledInType40.getExplanation();
			explanation += ", " + additional;
			return explanation;
		} else if (notificationType40 != null) {
			return notificationType40.getExplanation();
		} else if (rejectedType44 != null) {
			return rejectedType44.getExplanation();
		} else if (rejectedType45 != null) {
			return rejectedType45.getExplanation();
		} else if (rejectedType50 != null) {
			return rejectedType50.getExplanation();
		}
		return "Unknown";
	}

	public String getCode() {
		if (rejectedType40 != null) {
			String code = "";
			List<RejectedErrorType> errorList = rejectedType40.getError();
			for (RejectedErrorType errorType : errorList) {
				// error, major minor input
				code = errorType.getMajor() + "";

				// add error code
				BigInteger error = errorType.getError();
				if (error != null && !error.equals(0)) {
					code += CODE_SEPARATOR + error;
				}

				BigInteger minor = errorType.getMinor();
				if (minor != null && !minor.equals(0)) {
					code += CODE_SEPARATOR + minor;
				}
			}
			return code;
		} else if (calledInType40 != null) {
			return calledInType40.getType();
		} else if (notificationType40 != null) {
			return notificationType40.getType();
		} else if (rejectedType44 != null) {
			String code = "";
			List<ErrorType> errorList = rejectedType44.getError();
			for (ErrorType errorType : errorList) {
				code = errorType.getCode();
			}
			return code;
		} else if (rejectedType45 != null) {
			String code = "";
			List<ch.fd.invoice450.response.ErrorType> errorList = rejectedType45.getError();
			for (ch.fd.invoice450.response.ErrorType errorType : errorList) {
				code = errorType.getCode();
			}
			return code;
		} else if (rejectedType50 != null) {
			String code = "";
			List<ch.fd.invoice500.response.ErrorType> errorList = rejectedType50.getError();
			for (ch.fd.invoice500.response.ErrorType errorType : errorList) {
				code = errorType.getCode();
			}
			return code;
		}
		return null;
	}

	private String loadErrorBusinessValues(ErrorBusinessType ebt) {
		String additional = "";

		if (ebt != null) {
			// error value
			String errorVal = ebt.getErrorValue();
			if (errorVal != null && !errorVal.isEmpty()) {
				additional = Messages.Sync_ErrorValue + errorVal + " ";
			}

			// record id
			BigInteger recId = ebt.getRecordId();
			if (recId != null) {
				additional += Messages.Sync_RecordId + recId + " ";
			}

			// valid value
			String validVal = ebt.getValidValue();
			if (validVal != null && !validVal.isEmpty()) {
				additional += Messages.Sync_ValidValue + validVal;
			}

			// possible text
			String text = ebt.getValue();
			if (text != null && !text.isEmpty()) {
				additional += Messages.Sync_Text + text;
			}
		}
		return additional;
	}

	public List<ErrorTypeWrapper> getErrors() {
		if (rejectedType44 != null) {
			return rejectedType44.getError().stream().map(e -> new ErrorTypeWrapper(e)).toList();
		} else if (rejectedType45 != null) {
			return rejectedType45.getError().stream().map(e -> new ErrorTypeWrapper(e)).toList();
		} else if (rejectedType50 != null) {
			return rejectedType50.getError().stream().map(e -> new ErrorTypeWrapper(e)).toList();
		}
		return Collections.emptyList();
	}
}
