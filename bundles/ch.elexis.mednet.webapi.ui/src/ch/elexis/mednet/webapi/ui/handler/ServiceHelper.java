package ch.elexis.mednet.webapi.ui.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.mednet.webapi.core.IMednetAuthService;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;

public class ServiceHelper {

	private static final Logger logger = LoggerFactory.getLogger(ServiceHelper.class);

	public static Optional<String> getAuthToken(String tokenGroup) {
		BundleContext context = FrameworkUtil.getBundle(DataHandler.class).getBundleContext();
		ServiceReference<IMednetAuthService> serviceReference = context.getServiceReference(IMednetAuthService.class);

		if (serviceReference != null) {
			IMednetAuthService authService = context.getService(serviceReference);
			try {
				Map<String, Object> parameters = new HashMap<>();
				parameters.put(PreferenceConstants.TOKEN_GROUP, tokenGroup);
				return authService.getToken(parameters);
			} catch (Exception ex) {
				logger.error("Error when retrieving the authentication token", ex);
			} finally {
				context.ungetService(serviceReference);
			}
		} else {
			logger.error("ServiceReference for IMednetAuthService is null. ");
		}
		return Optional.empty();
	}
}
