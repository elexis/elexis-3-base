package at.medevit.elexis.tarmed.model.wrapper;

import ch.elexis.core.l10n.Messages;

/**
 * Wrapper Object providing transparent access for different
 * (http://www.forum-datenaustausch.ch/invoice) versions.
 *
 * @author thomas
 *
 */
public class AnnulmentTypeWrapper {

	private ch.fd.invoice400.response.AnnulmentType annulment40;
	private ch.fd.invoice440.response.PayloadType payload44;
	private ch.fd.invoice450.response.PayloadType payload45;
	private ch.fd.invoice500.response.PayloadType payload50;

	public AnnulmentTypeWrapper(ch.fd.invoice400.response.AnnulmentType annulment) {
		this.annulment40 = annulment;
	}

	public AnnulmentTypeWrapper(ch.fd.invoice440.response.PayloadType payload) {
		this.payload44 = payload;
	}

	public AnnulmentTypeWrapper(ch.fd.invoice450.response.PayloadType payload) {
		this.payload45 = payload;
	}

	public AnnulmentTypeWrapper(ch.fd.invoice500.response.PayloadType payload) {
		this.payload50 = payload;
	}

	public String getType() {
		if (annulment40 != null) {
			return annulment40.getType();
		} else if (payload44 != null) {
			return payload44.getType();
		} else if (payload45 != null) {
			return payload45.getType();
		} else if (payload50 != null) {
			return payload50.getRequestType();
		}
		return null;
	}

	public String getExplanation() {
		if (annulment40 != null) {
			String explanation = annulment40.getExplanation();

			if (annulment40.getAccepted() != null) {
				explanation += "  [" + Messages.Sync_Accepted + "] ";
			} else if (annulment40.getNotAccepted() != null) {
				explanation += " [" + Messages.Sync_NotAccepted + "] ";
			}

			if (annulment40.getInvoiceNotFound() != null) {
				explanation += ", " + Messages.Sync_InvoiceNotFound;
			}

			if (annulment40.getInvoicePaid() != null) {
				explanation += ", " + Messages.Sync_InvoicePaid;
			}

			if (annulment40.getInvoiceRejected() != null) {
				explanation += ", " + Messages.Sync_InvoiceRejected;
			}
			return explanation;
		} else if (payload44 != null) {
			String explanation = "Tarmed 4.4 annulment";
			if (payload44.getBody().getAccepted() != null) {
				explanation += "  [" + Messages.Sync_Accepted + "] ";
			} else {
				explanation += " [" + Messages.Sync_NotAccepted + "] ";
			}
			return explanation;
		} else if (payload45 != null) {
			String explanation = "Tarmed 4.5 annulment";
			if (payload45.getBody().getAccepted() != null) {
				explanation += "  [" + Messages.Sync_Accepted + "] ";
			} else {
				explanation += " [" + Messages.Sync_NotAccepted + "] ";
			}
			return explanation;
		} else if (payload50 != null) {
			String explanation = "Tarmed 5.0 annulment";
			if (payload50.getBody().getTiersPayant() != null
					&& payload50.getBody().getTiersPayant().getAccepted() != null) {
				explanation += "  [" + Messages.Sync_Accepted + "] ";
			} else {
				explanation += " [" + Messages.Sync_NotAccepted + "] ";
			}
			return explanation;
		}
		return null;
	}

}
