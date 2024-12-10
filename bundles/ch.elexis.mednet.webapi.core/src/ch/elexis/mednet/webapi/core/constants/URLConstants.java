package ch.elexis.mednet.webapi.core.constants;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IConfigService;
import ch.elexis.core.utils.OsgiServiceUtil;

public class URLConstants {
	private static final Logger logger = LoggerFactory.getLogger(URLConstants.class);

	public static String getBaseApiUrl() {
		Optional<IConfigService> configService = OsgiServiceUtil.getServiceWait(IConfigService.class, 5000);
		if (configService.isPresent()) {
			String mode = configService.get().getActiveUserContact(PreferenceConstants.MEDNET_MODE, "DEMO");

			if ("PRODUKTIV".equals(mode)) {
				return "https://www.mednet.swiss";
			} else {
				return "https://demo.mednet.swiss";
			}
		} else {
			logger.error("Error when retrieving the base API URL from the preferences: {}");
		}
		return "https://demo.mednet.swiss";
	}

	public static final String URL_PATIENTS = "/DoctorUser/patients";
	public static final String URL_TASKS = "/DoctorUser/tasks";
	public static final String URL_DOCUMENTS = "/DoctorUser/documents";
	public static final String URL_THERAPY = "/DoctorUser/chronic-diseases/chronic-diseases-list";

}