package ch.elexis.mednet.webapi.core.constants;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IConfigService;

public class ApiConstants {

	private static final Logger logger = LoggerFactory.getLogger(ApiConstants.class);

	public static String getBaseApiUrl() {
		try {
			BundleContext context = FrameworkUtil.getBundle(ApiConstants.class).getBundleContext();
			ServiceReference<IConfigService> serviceReference = context.getServiceReference(IConfigService.class);
			if (serviceReference != null) {
				IConfigService configService = context.getService(serviceReference);
				if (configService != null) {
					String mode = configService.getActiveUserContact(PreferenceConstants.MEDNET_MODE, "DEMO");

					if ("PRODUKTIV".equals(mode)) {
						return "https://www.mednet.swiss/web/api/v1/external";
					} else {
						return "https://demo.mednet.swiss/web/api/v1/external";
					}
				}
			}
			String pluginId = PreferenceConstants.MEDNET_PLUGIN_STRING;
			IEclipsePreferences node = InstanceScope.INSTANCE.getNode(pluginId);
			String mode = node.get(PreferenceConstants.MEDNET_MODE, "DEMO");
			if ("PRODUKTIV".equals(mode)) {
				return "https://www.mednet.swiss/web/api/v1/external";
			} else {
				return "https://demo.mednet.swiss/web/api/v1/external";
			}
		} catch (Exception e) {
			logger.error("Error when retrieving the base API URL from the preferences: {}", e.getMessage(), e);
			return "https://demo.mednet.swiss/web/api/v1/external";
		}
	}

	public static String getBaseUri() {
		try {
			BundleContext context = FrameworkUtil.getBundle(ApiConstants.class).getBundleContext();
			ServiceReference<IConfigService> serviceReference = context.getServiceReference(IConfigService.class);

			if (serviceReference != null) {
				IConfigService configService = context.getService(serviceReference);
				if (configService != null) {
					String mode = configService.getActiveUserContact(PreferenceConstants.MEDNET_MODE, "DEMO");

					if ("PRODUKTIV".equals(mode)) {
						return "https://www.mednet.swiss/idsrv";
					} else {
						return "https://demo.mednetpatient.swiss/idsrv";
					}
				}
			}

			String pluginId = PreferenceConstants.MEDNET_PLUGIN_STRING;
			IEclipsePreferences node = InstanceScope.INSTANCE.getNode(pluginId);
			String mode = node.get(PreferenceConstants.MEDNET_MODE, "DEMO");

			if ("PRODUKTIV".equals(mode)) {
				return "https://www.mednet.swiss/idsrv";
			} else {
				return "https://demo.mednetpatient.swiss/idsrv";
			}
		} catch (Exception e) {
			logger.error("Error when retrieving the base URI from the preferences: {}", e.getMessage(), e);
			return "https://demo.mednetpatient.swiss/idsrv";
		}
	}

	public static final String BASE_API_URL = getBaseApiUrl();
	public static final String BASE_URI = getBaseUri();
	public static final String BASE_REDERICT_URI = "https://tools.medelexis.ch/mednet/ac";
	public static final String BASE_REDERICT_URI_OBTAIN = "https://tools.medelexis.ch/mednet/ac-obtain/";

	public static final String CUSTOMERS_URL = BASE_API_URL + "/customers?includeDetails=true";
	public static final String PROVIDERS_URL = BASE_API_URL + "/providers?customerId=%d&includeDetails=true";
	public static final String FORMS_URL = BASE_API_URL + "/forms?customerId=%d&providerId=%d";
	public static final String SUBMITTED_FORMS_URL = BASE_API_URL + "/submitted-forms?customerId=%d";
	public static final String PATIENTS_URL = BASE_API_URL + "/patients";
}
