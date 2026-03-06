package ch.elexis.regiomed.order.config;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.model.IMandator;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.regiomed.order.preferences.RegiomedConstants;

public class RegiomedConfig {

	private final String baseUrl;
	private final String clientId;
	private final String email;
	private final String password;
	private final boolean checkOrder;

	private final boolean errorEmailEnabled;
	private final String errorEmailAddress;

	public RegiomedConfig(String baseUrl, String clientId, String email, String password, boolean checkOrder,
			boolean errorEmailEnabled, String errorEmailAddress) {

		this.baseUrl = baseUrl;
		this.clientId = clientId;
		this.email = email;
		this.password = password;
		this.checkOrder = checkOrder;
		this.errorEmailEnabled = errorEmailEnabled;
		this.errorEmailAddress = errorEmailAddress;
	}

	public static RegiomedConfig load() {
		var cfg = ConfigServiceHolder.get();

		IMandator mandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
		String prefix = StringUtils.EMPTY;
		if (mandant != null) {
			boolean hasOwnLogin = cfg.get(mandant.getId() + RegiomedConstants.SUFFIX_OVERRIDE_GLOBAL, false);
			if (hasOwnLogin) {
				prefix = mandant.getId() + "_";
			}
		}

		String baseUrl = cfg.get(RegiomedConstants.PREF_BASE_URL, "https://www.dispomanager.ch/LiveService");
		boolean checkOrder = cfg.get("ch.elexis.regiomed.checkOrder", true);

		String clientId = cfg.get(prefix + RegiomedConstants.PREF_CLIENT_ID, StringUtils.EMPTY);
		String email = cfg.get(prefix + RegiomedConstants.PREF_EMAIL, StringUtils.EMPTY);
		String password = cfg.get(prefix + RegiomedConstants.PREF_PASSWORD, StringUtils.EMPTY);
		boolean errorEmailEnabled = cfg.get(prefix + RegiomedConstants.PREF_ERROR_EMAIL_ENABLED, false);
		String errorEmailAddress = cfg.get(prefix + RegiomedConstants.PREF_ERROR_EMAIL_ADDRESS, StringUtils.EMPTY);

		return new RegiomedConfig(baseUrl, clientId, email, password, checkOrder, errorEmailEnabled, errorEmailAddress);
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public boolean isCheckOrder() {
		return checkOrder;
	}

	public boolean isErrorEmailEnabled() {
		return errorEmailEnabled;
	}

	public String getErrorEmailAddress() {
		return errorEmailAddress;
	}

	public String getTokenEndpoint() {
		return appendPath("/Token/Create");
	}

	public String getOrderEndpoint() {
		return appendPath("/ExternalOrders/DMO");
	}

	public String getFuzzySearchEndpoint() {
		return appendPath("/Article/FuzzySearch/");
	}

	public String getAlternativesFlexEndpoint() {
		return appendPath("/Article/AlternativesFlex/");
	}

	private String appendPath(String path) {
		if (baseUrl.endsWith("/")) {
			return baseUrl.substring(0, baseUrl.length() - 1) + path;
		}
		return baseUrl + path;
	}
}