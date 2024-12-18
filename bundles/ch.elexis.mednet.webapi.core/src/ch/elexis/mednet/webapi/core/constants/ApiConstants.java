package ch.elexis.mednet.webapi.core.constants;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IConfigService;
import ch.elexis.core.utils.OsgiServiceUtil;

public class ApiConstants {

	private static final Logger logger = LoggerFactory.getLogger(ApiConstants.class);

	public static String getBaseApiUrl() {
		Optional<IConfigService> configService = OsgiServiceUtil.getServiceWait(IConfigService.class, 5000);
		if (configService.isPresent()) {
			String mode = configService.get().getActiveUserContact(PreferenceConstants.MEDNET_MODE, "DEMO");

			if ("PRODUKTIV".equals(mode)) {
				return "https://www.mednet.swiss/web/api/v1/external";
			} else {
				return "https://demo.mednet.swiss/web/api/v1/external";
			}
		} else {
			logger.error("Error when retrieving the base API URL from the preferences: {}");
		}
		return "https://demo.mednet.swiss/web/api/v1/external";
	}

	public static String getBaseUri() {

		Optional<IConfigService> configService = OsgiServiceUtil.getServiceWait(IConfigService.class, 5000);
		if (configService.isPresent()) {
			String mode = configService.get().getActiveUserContact(PreferenceConstants.MEDNET_MODE, "DEMO");
			if ("PRODUKTIV".equals(mode)) {
				return "https://www.mednetpatient.swiss/idsrv";
			} else {
				return "https://demo.mednetpatient.swiss/idsrv";
			}
		}
		return "https://demo.mednetpatient.swiss/idsrv";
	}

	public static final String BASE_REDERICT_URI = "https://tools.medelexis.ch/mednet/ac";
	public static final String BASE_REDERICT_URI_OBTAIN = "https://tools.medelexis.ch/mednet/ac-obtain/";
	public static final String CUSTOMERS_URL = "/customers?includeDetails=true";
	public static final String PROVIDERS_URL = "/providers?customerId=%d&includeDetails=true";
	public static final String FORMS_URL = "/forms?customerId=%d&providerId=%d";
	public static final String SUBMITTED_FORMS_URL = "/submitted-forms?customerId=%d";
	public static final String PATIENTS_URL = "/patients";
}
