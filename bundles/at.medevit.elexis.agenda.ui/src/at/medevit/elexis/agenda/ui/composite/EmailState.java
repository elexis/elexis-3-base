package at.medevit.elexis.agenda.ui.composite;

public class EmailState {
	private static EmailState instance;
	private boolean emailSent;

	private EmailState() {
		emailSent = false;
	}

	public static EmailState getInstance() {
		if (instance == null) {
			instance = new EmailState();
		}
		return instance;
	}

	public void setEmailSent(boolean emailSent) {
		this.emailSent = emailSent;
	}

	public boolean isEmailSent() {
		return emailSent;
	}
}
