package at.medevit.elexis.tarmed.model.wrapper;

import ch.fd.invoice440.response.NotificationType;

public class NotificationTypeWrapper {

	private ch.fd.invoice500.response.NotificationType notification50;
	private ch.fd.invoice450.response.NotificationType notification45;
	private ch.fd.invoice440.response.NotificationType notification44;

	public NotificationTypeWrapper(NotificationType notification44) {
		this.notification44 = notification44;
	}

	public NotificationTypeWrapper(ch.fd.invoice450.response.NotificationType notification45) {
		this.notification45 = notification45;
	}

	public NotificationTypeWrapper(ch.fd.invoice500.response.NotificationType notification50) {
		this.notification50 = notification50;
	}

	public String getCode() {
		if (notification44 != null) {
			return notification44.getCode();
		} else if (notification45 != null) {
			return notification45.getCode();
		} else if (notification50 != null) {
			return notification50.getCode();
		}
		return null;
	}

	public String getText() {
		if (notification44 != null) {
			return notification44.getText();
		} else if (notification45 != null) {
			return notification45.getText();
		} else if (notification50 != null) {
			return notification50.getText();
		}
		return null;
	}
}
