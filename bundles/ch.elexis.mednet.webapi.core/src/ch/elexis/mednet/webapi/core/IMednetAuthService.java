package ch.elexis.mednet.webapi.core;

import java.util.Map;
import java.util.Optional;

public interface IMednetAuthService {

	/**
	 * Get the bearer token for the provided parameters. Existing tokens are
	 * returned without user interaction. If there is no existing token a
	 * {@link IMednetAuthUi} implementation is needed for user interaction.
	 * 
	 * @param parameters
	 * @return
	 */
	public Optional<String> getToken(Map<String, Object> parameters);

	/**
	 * Get the bearer token for the provided parameters. Existing tokens are
	 * returned without user interaction. If there is no existing token a
	 * {@link IMednetAuthUi} implementation is needed for user interaction.
	 * 
	 * @param parameters
	 * @return
	 */
	public Optional<String> delToken(Map<String, Object> parameters);

	/**
	 * Call this method to inform the {@link IMednetAuthService} about a exception
	 * occurred accessing a web service using a token from the
	 * {@link IMednetAuthService}.
	 * 
	 * @param ex
	 * @param parameters
	 * 
	 * @return
	 */
	public Optional<String> handleException(Exception ex, Map<String, Object> parameters);

}
