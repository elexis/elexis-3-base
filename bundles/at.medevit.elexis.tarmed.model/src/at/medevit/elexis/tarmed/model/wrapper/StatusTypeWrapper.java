package at.medevit.elexis.tarmed.model.wrapper;

/**
 * Wrapper Object providing transparent access for different
 * (http://www.forum-datenaustausch.ch/invoice) versions.
 *
 * @author thomas
 *
 */
public class StatusTypeWrapper {

	private ch.fd.invoice400.response.StatusType statusType40;

	private ch.fd.invoice440.response.PayloadType payloadType44;

	private ch.fd.invoice450.response.PayloadType payloadType45;

	private ch.fd.invoice500.response.PayloadType payloadType50;

	/**
	 * "rejected", "calledIn", "pending", "resend", "modified", "annulment",
	 * "creditAdvice"
	 *
	 * @param status
	 */
	public StatusTypeWrapper(ch.fd.invoice400.response.StatusType status) {
		this.statusType40 = status;
	}

	/**
	 * "rejected"
	 *
	 * @param rejected
	 */
	public StatusTypeWrapper(ch.fd.invoice440.response.PayloadType payload) {
		this.payloadType44 = payload;
	}

	public StatusTypeWrapper(ch.fd.invoice450.response.PayloadType payload) {
		this.payloadType45 = payload;
	}

	public StatusTypeWrapper(ch.fd.invoice500.response.PayloadType payload) {
		this.payloadType50 = payload;
	}

	public RejectedTypeWrapper getRejected() {
		if (statusType40 != null) {
			ch.fd.invoice400.response.RejectedType rejected = statusType40.getRejected();
			if (rejected != null) {
				return new RejectedTypeWrapper(rejected);
			}
			ch.fd.invoice400.response.CalledInType calledIn = statusType40.getCalledIn();
			if (calledIn != null) {
				return new RejectedTypeWrapper(calledIn);
			}
			ch.fd.invoice400.response.NotificationType resend = statusType40.getResend();
			if (resend != null) {
				return new RejectedTypeWrapper(resend);
			}
		} else if (payloadType44 != null && payloadType44.getBody() != null) {
			if (payloadType44.getBody().getRejected() != null) {
				return new RejectedTypeWrapper(payloadType44.getBody().getRejected());
			}
		} else if (payloadType45 != null && payloadType45.getBody() != null) {
			if (payloadType45.getBody().getRejected() != null) {
				return new RejectedTypeWrapper(payloadType45.getBody().getRejected());
			}
		} else if (payloadType50 != null && payloadType50.getBody() != null) {
			if (payloadType50.getBody().getTiersPayant() != null
					&& payloadType50.getBody().getTiersPayant().getRejected() != null) {
				return new RejectedTypeWrapper(payloadType50.getBody().getTiersPayant().getRejected());
			}
		}
		return null;
	}

	public PendingTypeWrapper getPending() {
		if (statusType40 != null) {
			if (statusType40.getPending() != null) {
				return new PendingTypeWrapper(statusType40.getPending());
			}
		} else if (payloadType44 != null && payloadType44.getBody() != null) {
			if (payloadType44.getBody().getPending() != null) {
				return new PendingTypeWrapper(payloadType44.getBody().getPending());
			}
		} else if (payloadType45 != null && payloadType45.getBody() != null) {
			if (payloadType45.getBody().getPending() != null) {
				return new PendingTypeWrapper(payloadType45.getBody().getPending());
			}
		} else if (payloadType50 != null && payloadType50.getBody() != null) {
			if (payloadType50.getBody().getTiersPayant() != null
					&& payloadType50.getBody().getTiersPayant().getPending() != null) {
				return new PendingTypeWrapper(payloadType50.getBody().getTiersPayant().getPending());
			}
		}
		return null;
	}

	public ModifiedTypeWrapper getModified() {
		if (statusType40 != null) {
			if (statusType40.getModified() != null) {
				return new ModifiedTypeWrapper(statusType40.getModified());
			}
		}
		return null;
	}

	public AnnulmentTypeWrapper getAnnulment() {
		if (statusType40 != null) {
			if (statusType40.getAnnulment() != null) {
				return new AnnulmentTypeWrapper(statusType40.getAnnulment());
			}
		} else if (payloadType44 != null) {
			if (payloadType44.isStorno()) {
				return new AnnulmentTypeWrapper(payloadType44);
			}
		} else if (payloadType45 != null) {
			if (payloadType45.isStorno()) {
				return new AnnulmentTypeWrapper(payloadType45);
			}
		} else if (payloadType50 != null) {
			if ("storno".equalsIgnoreCase(payloadType50.getRequestSubtype())) {
				return new AnnulmentTypeWrapper(payloadType50);
			}
		}
		return null;
	}

	public CreditAdviceTypeWrapper getCreditAdvice() {
		if (statusType40 != null) {
			if (statusType40.getCreditAdvice() != null) {
				return new CreditAdviceTypeWrapper(statusType40.getCreditAdvice());
			}
		} else if (payloadType44 != null) {
			if (payloadType44.isCreditAdvice()) {
				return new CreditAdviceTypeWrapper(payloadType44);
			}
		}
		// tarmed 4.5 is missing the credit advice attribute
		return null;
	}
}
