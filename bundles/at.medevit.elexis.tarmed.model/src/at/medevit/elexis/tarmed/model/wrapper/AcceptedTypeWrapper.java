package at.medevit.elexis.tarmed.model.wrapper;

import ch.fd.invoice500.response.AcceptedTPType;

public class AcceptedTypeWrapper {

	private ch.fd.invoice500.response.AcceptedTPType accepted50;
	private ch.fd.invoice450.response.AcceptedType accepted45;
	private ch.fd.invoice440.response.AcceptedType accepted44;

	public AcceptedTypeWrapper(AcceptedTPType accepted) {
		this.accepted50 = accepted;
	}

	public AcceptedTypeWrapper(ch.fd.invoice450.response.AcceptedType accepted) {
		this.accepted45 = accepted;
	}

	public AcceptedTypeWrapper(ch.fd.invoice440.response.AcceptedType accepted) {
		this.accepted44 = accepted;
	}

	public String getExplanation() {
		if (accepted50 != null) {
			return accepted50.getExplanation();
		} else if (accepted45 != null) {
			return accepted45.getExplanation();
		} else if (accepted44 != null) {
			return accepted44.getExplanation();
		}
		return null;
	}

}
