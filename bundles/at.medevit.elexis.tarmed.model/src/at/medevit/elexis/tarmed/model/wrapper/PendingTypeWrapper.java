package at.medevit.elexis.tarmed.model.wrapper;

import ch.fd.invoice440.response.NotificationType;

/**
 * Wrapper Object providing transparent access for different
 * (http://www.forum-datenaustausch.ch/invoice) versions.
 *
 * @author thomas
 *
 */
public class PendingTypeWrapper {

	private ch.fd.invoice400.response.NotificationType pending40;
	private ch.fd.invoice440.response.PendingType pending44;
	private ch.fd.invoice450.response.PendingType pending45;
	private ch.fd.invoice500.response.PendingType pending50;

	public PendingTypeWrapper(ch.fd.invoice400.response.NotificationType pending) {
		this.pending40 = pending;
	}

	public PendingTypeWrapper(ch.fd.invoice440.response.PendingType pending) {
		this.pending44 = pending;
	}

	public PendingTypeWrapper(ch.fd.invoice450.response.PendingType pending) {
		this.pending45 = pending;
	}

	public PendingTypeWrapper(ch.fd.invoice500.response.PendingType pending) {
		this.pending50 = pending;
	}

	public String getType() {
		if (pending40 != null) {
			return pending40.getType();
		} else if (pending44 != null) {
			return pending44.getStatusOut();
		} else if (pending45 != null) {
			return pending45.getStatusOut();
		} else if (pending50 != null) {
			return pending50.getStatusOut();
		}
		return null;
	}

	public String getExplanation() {
		if (pending40 != null) {
			return pending40.getExplanation();
		} else if (pending44 != null) {
			StringBuffer builder = new StringBuffer();
			if (pending44.getExplanation() != null) {
				builder.append(pending44.getExplanation());
			}
			for (NotificationType msg : pending44.getMessage()) {
				if (msg.getText() != null) {
					if (builder.length() > 0) {
						builder.append("\n");
					}
					builder.append(msg.getText());
					if (msg.getCode() != null) {
						builder.append(" (");
						builder.append(msg.getCode());
						builder.append(")");
					}
				}
			}
			if (builder.length() > 0) {
				return builder.toString();
			}
		} else if (pending45 != null) {
			StringBuffer builder = new StringBuffer();
			if (pending45.getExplanation() != null) {
				builder.append(pending45.getExplanation());
			}
			for (ch.fd.invoice450.response.NotificationType msg : pending45.getMessage()) {
				if (msg.getText() != null) {
					if (builder.length() > 0) {
						builder.append("\n");
					}
					builder.append(msg.getText());
					if (msg.getCode() != null) {
						builder.append(" (");
						builder.append(msg.getCode());
						builder.append(")");
					}
				}
			}
			if (builder.length() > 0) {
				return builder.toString();
			}
		} else if (pending50 != null) {
			StringBuffer builder = new StringBuffer();
			if (pending50.getExplanation() != null) {
				builder.append(pending50.getExplanation());
			}
			for (ch.fd.invoice500.response.NotificationType msg : pending50.getMessage()) {
				if (msg.getText() != null) {
					if (builder.length() > 0) {
						builder.append("\n");
					}
					builder.append(msg.getText());
					if (msg.getCode() != null) {
						builder.append(" (");
						builder.append(msg.getCode());
						builder.append(")");
					}
				}
			}
			if (builder.length() > 0) {
				return builder.toString();
			}
		}
		return null;
	}
}
