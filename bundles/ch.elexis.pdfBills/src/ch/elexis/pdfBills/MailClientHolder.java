package ch.elexis.pdfBills;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.mail.IMailClient;

@Component
public class MailClientHolder {
	private static IMailClient mailClient;

	@Reference
	public void setMailClient(IMailClient mailClient) {
		MailClientHolder.mailClient = mailClient;
	}

	public void unsetMailClient(IMailClient mailClient) {
		MailClientHolder.mailClient = null;
	}

	public static IMailClient get() {
		return mailClient;
	}
}
