package at.medevit.elexis.tarmed.model.wrapper;

import ch.elexis.core.l10n.Messages;

/**
 * Wrapper Object providing transparent access for different
 * (http://www.forum-datenaustausch.ch/invoice) versions.
 *
 * @author thomas
 *
 */
public class CreditAdviceTypeWrapper {

	private ch.fd.invoice400.response.CreditAdviceType creditAdvice40;
	private ch.fd.invoice440.response.PayloadType payload44;

	public CreditAdviceTypeWrapper(ch.fd.invoice400.response.CreditAdviceType creditAdvice) {
		this.creditAdvice40 = creditAdvice;
	}

	public CreditAdviceTypeWrapper(ch.fd.invoice440.response.PayloadType payload44) {
		this.payload44 = payload44;
	}

	public String getType() {
		if (creditAdvice40 != null) {
			return creditAdvice40.getType();
		} else if (payload44 != null) {
			return payload44.getType();
		}
		return null;
	}

	public String getExplanation() {
		if (creditAdvice40 != null) {
			String explanation = creditAdvice40.getExplanation();
			if (creditAdvice40.getAccepted() != null) {
				explanation += " [" + Messages.Sync_Accepted + "] ";
			} else if (creditAdvice40.getNotAccepted() != null) {
				explanation += " [" + Messages.Sync_NotAccepted + "] ";
			}
			return explanation;
		} else if (payload44 != null) {
			String explanation = "Tarmed 4.4 credit advice";
			if (payload44.getBody().getAccepted() != null) {
				explanation += "  [" + Messages.Sync_Accepted + "] ";
			} else {
				explanation += " [" + Messages.Sync_NotAccepted + "] ";
			}
			return explanation;
		}
		return null;
	}

}
