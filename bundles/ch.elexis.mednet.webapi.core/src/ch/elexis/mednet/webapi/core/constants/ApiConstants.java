package ch.elexis.mednet.webapi.core.constants;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IConfigService;

public class ApiConstants {

	private static final Logger logger = LoggerFactory.getLogger(ApiConstants.class);

	public static String getBaseApiUrl() {
		Optional<IConfigService> configService = OsgiServiceUtil.getServiceWait(IConfigService.class, 5000);
		if (configService.isPresent()) {
			String mode = configService.get().get(PreferenceConstants.MEDNET_MODE, "PRODUKTIV");

			if ("PRODUKTIV".equals(mode)) {
				return "https://www.mednet.swiss/web/api/v1/external";
			} else {
				return "https://demo.mednet.swiss/web/api/v1/external";
			}
		} else {
			logger.error("Error when retrieving the base API URL from the preferences: {}");
		}
		return "https://www.mednet.swiss/web/api/v1/external";
	}

	public static String getBaseUri(boolean azure) {

		Optional<IConfigService> configService = OsgiServiceUtil.getServiceWait(IConfigService.class, 5000);
		if (configService.isPresent()) {
			String mode = configService.get().get(PreferenceConstants.MEDNET_MODE, "PRODUKTIV");
			if ("PRODUKTIV".equals(mode)) {
				return azure ? "https://www.mednet.swiss/web/v1/oauth" : "https://www.mednetpatient.swiss/idsrv/connect";
			} else {
				return azure ? "https://demo.mednet.swiss/web/v1/oauth" : "https://demo.mednetpatient.swiss/idsrv/connect";
			}
		}
		return azure ? "https://www.mednet.swiss/web/v1/oauth" : "https://www.mednetpatient.swiss/idsrv/connect";
	}

	public static final String IDSRV_SCOPE = "openid profile mednet-web is-api email role offline_access";

	public static final String BASE_REDERICT_URI = "https://tools.medelexis.ch/mednet/ac";
	public static final String BASE_REDERICT_URI_OBTAIN = "https://tools.medelexis.ch/mednet/ac-obtain/";
	public static final String CUSTOMERS_URL = "/customers?includeDetails=true";
	public static final String PROVIDERS_URL = "/providers?customerId=%d&includeDetails=true";
	public static final String FORMS_URL = "/forms?customerId=%d&providerId=%d";
	public static final String SUBMITTED_FORMS_URL = "/submitted-forms?customerId=%d";
	public static final String PATIENTS_URL = "/patients";
}
