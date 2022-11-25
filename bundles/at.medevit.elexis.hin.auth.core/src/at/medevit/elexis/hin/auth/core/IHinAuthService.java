package at.medevit.elexis.hin.auth.core;

import java.util.Map;
import java.util.Optional;

public interface IHinAuthService {

	public static String PREF_WEBAPPBASEURL = "hin/auth/wabapp/baseurl"; //$NON-NLS-1$
	public static String PREF_RESTBASEURL = "hin/auth/rest/baseurl"; //$NON-NLS-1$
	public static String PREF_OAUTHRESTBASEURL = "hin/auth/oauthrest/baseurl"; //$NON-NLS-1$

	public static String PREF_TOKEN = "hin/auth/token/"; //$NON-NLS-1$
	public static String PREF_TOKEN_EXPIRES = "hin/auth/tokenexpires/"; //$NON-NLS-1$

	public enum Parameters {
		TOKEN_GROUP, UI
	}

	/**
	 * Get the bearer token for the provided parameters. Existing tokens are
	 * returned without user interaction. If there is no existing token a
	 * {@link IHinAuthUi} implementation is needed for user interaction.
	 * 
	 * @param parameters
	 * @return
	 */
	public Optional<String> getToken(Map<String, Object> parameters);

}
