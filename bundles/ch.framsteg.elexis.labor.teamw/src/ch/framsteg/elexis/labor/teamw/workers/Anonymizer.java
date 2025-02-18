package ch.framsteg.elexis.labor.teamw.workers;

import ch.elexis.core.services.IConfigService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import jakarta.inject.Inject;

public class Anonymizer {

	private static final String PASSWORD_KEY = "key.teamw.password";

	@Inject
	private IConfigService configService;

	public Anonymizer() {
		CoreUiUtil.injectServices(this);
	}

	public String anonymize(String message) {
		String anonymizedMessage = message.replace(configService.get(PASSWORD_KEY, ""), "*******");
		return anonymizedMessage;
	}
}
