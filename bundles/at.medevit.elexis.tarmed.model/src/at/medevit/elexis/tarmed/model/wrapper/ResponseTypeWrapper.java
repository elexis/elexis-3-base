package at.medevit.elexis.tarmed.model.wrapper;

import java.math.BigInteger;

import ch.fd.invoice400.response.StatusType;
import ch.fd.invoice440.response.PayloadType;
import ch.fd.invoice450.response.AcceptedType;
import ch.fd.invoice500.response.AcceptedTPType;

/**
 * Wrapper Object providing transparent access for different invoice response
 * (http://www.forum-datenaustausch.ch/invoice) versions.
 *
 * @author thomas
 *
 */
public class ResponseTypeWrapper {
	private ch.fd.invoice400.response.ResponseType responseType40;
	private ch.fd.invoice440.response.ResponseType responseType44;
	private ch.fd.invoice450.response.ResponseType responseType45;
	private ch.fd.invoice500.response.ResponseType responseType50;

	public ResponseTypeWrapper(ch.fd.invoice400.response.ResponseType responseType40) {
		this.responseType40 = responseType40;
	}

	public ResponseTypeWrapper(ch.fd.invoice440.response.ResponseType responseType44) {
		this.responseType44 = responseType44;
	}

	public ResponseTypeWrapper(ch.fd.invoice450.response.ResponseType responseType45) {
		this.responseType45 = responseType45;
	}

	public ResponseTypeWrapper(ch.fd.invoice500.response.ResponseType responseType50) {
		this.responseType50 = responseType50;
	}

	public BigInteger getResponseTimestamp() {
		if (responseType40 != null) {
			return responseType40.getInvoice().getResponseTimestamp();
		} else if (responseType44 != null) {
			return responseType44.getPayload().getResponseTimestamp();
		} else if (responseType45 != null) {
			return new BigInteger(Integer.toString(responseType45.getPayload().getResponseTimestamp()));
		} else if (responseType50 != null) {
			return new BigInteger(Long.toString(responseType50.getPayload().getResponseTimestamp()));
		}
		return null;
	}

	public String getInvoiceId() {
		if (responseType40 != null) {
			return responseType40.getInvoice().getInvoiceId();
		} else if (responseType44 != null) {
			return responseType44.getPayload().getInvoice().getRequestId();
		} else if (responseType45 != null) {
			return responseType45.getPayload().getInvoice().getRequestId();
		} else if (responseType50 != null) {
			return responseType50.getPayload().getInvoice().getRequestId();
		}
		return null;
	}

	public StatusTypeWrapper getStatus() {
		if (responseType40 != null) {
			StatusType status = responseType40.getStatus();
			return new StatusTypeWrapper(status);
		} else if (responseType44 != null) {
			PayloadType payload = responseType44.getPayload();
			if (payload != null) {
				return new StatusTypeWrapper(payload);
			}
		} else if (responseType45 != null) {
			ch.fd.invoice450.response.PayloadType payload = responseType45.getPayload();
			if (payload != null) {
				return new StatusTypeWrapper(payload);
			}
		} else if (responseType50 != null) {
			ch.fd.invoice500.response.PayloadType payload = responseType50.getPayload();
			if (payload != null) {
				return new StatusTypeWrapper(payload);
			}
		}
		return null;
	}

	public AcceptedTypeWrapper getAccepted() {
		if (responseType50 != null) {
			ch.fd.invoice500.response.PayloadType payload = responseType50.getPayload();
			if (payload != null && payload.getBody() != null && payload.getBody().getTiersPayant() != null) {
				AcceptedTPType accepted = payload.getBody().getTiersPayant().getAccepted();
				if (accepted != null) {
					return new AcceptedTypeWrapper(accepted);
				}
			}
		} else if (responseType45 != null) {
			ch.fd.invoice450.response.PayloadType payload = responseType45.getPayload();
			if (payload != null && payload.getBody() != null) {
				AcceptedType accepted = payload.getBody().getAccepted();
				if (accepted != null) {
					return new AcceptedTypeWrapper(accepted);
				}
			}
		} else if (responseType44 != null) {
			ch.fd.invoice440.response.PayloadType payload = responseType44.getPayload();
			if (payload != null && payload.getBody() != null) {
				ch.fd.invoice440.response.AcceptedType accepted = payload.getBody().getAccepted();
				if (accepted != null) {
					return new AcceptedTypeWrapper(accepted);
				}
			}
		}
		return null;
	}

	public ContactAdressTypeWrapper getContactAdressType() {
		if (responseType40 != null && responseType40.getInvoice() != null
				&& responseType40.getInvoice().getReply() != null) {
			return new ContactAdressTypeWrapper(responseType40.getInvoice().getReply());
		} else if (responseType44 != null && responseType44.getPayload() != null
				&& responseType44.getPayload().getBody() != null
				&& responseType44.getPayload().getBody().getContact() != null) {
			return new ContactAdressTypeWrapper(responseType44.getPayload().getBody().getContact());
		} else if (responseType45 != null && responseType45.getPayload() != null
				&& responseType45.getPayload().getBody() != null
				&& responseType45.getPayload().getBody().getContact() != null) {
			return new ContactAdressTypeWrapper(responseType45.getPayload().getBody().getContact());
		} else if (responseType50 != null && responseType50.getPayload() != null
				&& responseType50.getPayload().getBody() != null
				&& responseType50.getPayload().getBody().getContact() != null) {
			return new ContactAdressTypeWrapper(responseType50.getPayload().getBody().getContact());
		}
		return null;
	}
}
