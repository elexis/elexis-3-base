package ch.elexis.mednet.webapi.core.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IMandator;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;

/**
 * Immutable configuration object for MedNet Web API. Automatically resolves
 * whether to load global settings or mandator-specific overrides.
 */
public class MedNetConfig {

	private static final Logger log = LoggerFactory.getLogger(MedNetConfig.class);
	private static final String OVERRIDE_GLOBAL_SUFFIX = "_override_global";
	private static final String DEFAULT_MODE = "PRODUKTIV";

	private final String downloadPath;
	private final String loginName;
	private final boolean confirmBeforeSend;
	private final String operatingMode;

	public MedNetConfig(String downloadPath, String loginName, boolean confirmBeforeSend, String operatingMode) {
		this.downloadPath = downloadPath;
		this.loginName = loginName;
		this.confirmBeforeSend = confirmBeforeSend;
		this.operatingMode = operatingMode;
	}

	/**
	 * Loads the current MedNet configuration from the active context.
	 *
	 * @return a new instance of MedNetConfig with the resolved values
	 */
	public static MedNetConfig load() {
		IConfigService configService = ConfigServiceHolder.get();

		if (configService == null) {
			log.warn("ConfigService is unavailable. Returning empty MedNetConfig fallback.");
			return new MedNetConfig(StringUtils.EMPTY, StringUtils.EMPTY, true, DEFAULT_MODE);
		}

		IMandator activeMandator = ContextServiceHolder.get().getActiveMandator().orElse(null);
		String prefix = StringUtils.EMPTY;

		if (activeMandator != null) {
			boolean hasOverride = configService.get(activeMandator.getId() + OVERRIDE_GLOBAL_SUFFIX, false);
			if (hasOverride) {
				prefix = activeMandator.getId() + "_";
			}
		}

		String downloadPath = configService.get(prefix + PreferenceConstants.MEDNET_DOWNLOAD_PATH, StringUtils.EMPTY);
		String loginName = configService.get(prefix + PreferenceConstants.MEDNET_USER_STRING, StringUtils.EMPTY);
		// FIXED: Changed getBoolean to get
		boolean confirmBeforeSend = configService.get(prefix + PreferenceConstants.MEDNET_CONFIRM_BEFORE_SEND, true);
		String operatingMode = configService.get(prefix + PreferenceConstants.MEDNET_MODE, DEFAULT_MODE);

		return new MedNetConfig(downloadPath, loginName, confirmBeforeSend, operatingMode);
	}

	/**
	 * @return the configured download path for Omnivore imports
	 */
	public String getDownloadPath() {
		return downloadPath;
	}

	/**
	 * @return the login name used for MedNet API authentication
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * @return true if a confirmation dialog should be shown before sending
	 *         documents
	 */
	public boolean isConfirmBeforeSend() {
		return confirmBeforeSend;
	}

	/**
	 * @return the operating mode, e.g., "PRODUKTIV" or "DEMO"
	 */
	public String getOperatingMode() {
		return operatingMode;
	}
}