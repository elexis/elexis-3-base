package ch.elexis.regiomed.order.config;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.services.holder.ConfigServiceHolder;
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

		String baseUrl = cfg.get(RegiomedConstants.PREF_BASE_URL, "https://www.dispomanager.ch/LiveService"); //$NON-NLS-1$
		String clientId = cfg.get(RegiomedConstants.PREF_CLIENT_ID, StringUtils.EMPTY);
		String email = cfg.get(RegiomedConstants.PREF_EMAIL, StringUtils.EMPTY);
		String password = cfg.get(RegiomedConstants.PREF_PASSWORD, StringUtils.EMPTY);
		boolean checkOrder = cfg.get("ch.elexis.regiomed.checkOrder", true); //$NON-NLS-1$
		boolean errorEmailEnabled = cfg.get(RegiomedConstants.PREF_ERROR_EMAIL_ENABLED, false);
		String errorEmailAddress = cfg.get(RegiomedConstants.PREF_ERROR_EMAIL_ADDRESS, StringUtils.EMPTY);

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
		return appendPath("/Token/Create"); //$NON-NLS-1$
	}

	public String getOrderEndpoint() {
		return appendPath("/ExternalOrders/DMO"); //$NON-NLS-1$
	}

	public String getFuzzySearchEndpoint() {
		return appendPath("/Article/FuzzySearch/"); //$NON-NLS-1$
	}

	public String getAlternativesFlexEndpoint() {
		return appendPath("/Article/AlternativesFlex/"); //$NON-NLS-1$
	}

	private String appendPath(String path) {
		if (baseUrl.endsWith("/")) { //$NON-NLS-1$
			return baseUrl.substring(0, baseUrl.length() - 1) + path;
		}
		return baseUrl + path;
	}
}
