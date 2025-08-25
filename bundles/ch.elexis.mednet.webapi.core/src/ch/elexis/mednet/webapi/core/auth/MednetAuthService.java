package ch.elexis.mednet.webapi.core.auth;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import ch.elexis.core.services.IConfigService;
import ch.elexis.mednet.webapi.core.IMednetAuthService;
import ch.elexis.mednet.webapi.core.IMednetAuthUi;
import ch.elexis.mednet.webapi.core.constants.ApiConstants;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;
import ch.elexis.mednet.webapi.core.messages.Messages;

@Component
public class MednetAuthService implements IMednetAuthService {

	@Reference
	private IConfigService configService;

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
	private IMednetAuthUi authUi;

	private String currentState;
	private String currentCodeVerifier;

	@Override
	public Optional<String> getToken(Map<String, Object> parameters) {
		if (configService == null) {
			LoggerFactory.getLogger(getClass()).error("configService is null!"); //$NON-NLS-1$
			return Optional.empty();
		}

		String tokenGroup = (String) parameters.get(PreferenceConstants.TOKEN_GROUP);
		if (StringUtils.isNotBlank(tokenGroup)) {
			Optional<String> existingToken = validateToken(
					configService.getActiveMandator(PreferenceConstants.PREF_TOKEN + tokenGroup, null), tokenGroup);
			if (existingToken.isEmpty() && authUi != null) {
				return getToken(tokenGroup, authUi);
			} else if (existingToken.isPresent()) {
				return existingToken;
			}
		}
		return Optional.empty();
	}

	private Optional<String> getToken(String tokenGroup, IMednetAuthUi iMednetAuthUi) {
		Optional<String> authCode = getAuthCode(tokenGroup, iMednetAuthUi);
		if (authCode.isPresent()) {
			return getAccessToken(tokenGroup, authCode.get(), getOauthRestUrl());
		} else {
			LoggerFactory.getLogger(getClass()).warn("No auth code for [" + tokenGroup + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return Optional.empty();
	}

	private String getOauthRestUrl() {
		return configService.get(PreferenceConstants.PREF_RESTBASEURL, ApiConstants.getBaseUri());
	}

	private Optional<String> getAccessTokenWithRefresh(String tokenGroup, String refreshToken, String oauthRestUrl) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("grant_type", "refresh_token"); //$NON-NLS-1$ //$NON-NLS-2$
		parameters.put("refresh_token", refreshToken); //$NON-NLS-1$
		parameters.put("client_id", getClientId()); //$NON-NLS-1$
		parameters.put("client_secret", getClientSecret()); //$NON-NLS-1$
		String form = parameters.entrySet().stream()
				.map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)) //$NON-NLS-1$
				.collect(Collectors.joining("&")); //$NON-NLS-1$

		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(buildTokenEndpoint(oauthRestUrl)))
				.header("Content-Type", "application/x-www-form-urlencoded") //$NON-NLS-1$ //$NON-NLS-2$
				.POST(HttpRequest.BodyPublishers.ofString(form)).build();

		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() >= 200 && response.statusCode() < 300) {
				Gson gson = new Gson();
				try {
					Map<?, ?> map = gson.fromJson(response.body(), Map.class);
					String token = (String) map.get("access_token"); //$NON-NLS-1$

					configService.setActiveMandator(PreferenceConstants.PREF_TOKEN + tokenGroup, token);
					String refreshtoken = (String) map.get("refresh_token"); //$NON-NLS-1$

					if (StringUtils.isNotBlank(refreshtoken)) {
						configService.setActiveMandator(PreferenceConstants.PREF_REFRESHTOKEN + tokenGroup,
								refreshtoken);
					}

					Double expiresInSeconds = (Double) map.get("expires_in"); //$NON-NLS-1$
					Long expires = System.currentTimeMillis() + (expiresInSeconds.longValue() * 1000);
					configService.setActiveMandator(PreferenceConstants.PREF_TOKEN_EXPIRES + tokenGroup,
							Long.toString(expires));

					LoggerFactory.getLogger(getClass()).info("Got refreshed access token for [{}] expires [{}]", //$NON-NLS-1$
							tokenGroup, Long.toString(expires));

					return Optional.of(token);
				} catch (JsonSyntaxException ex) {
					LoggerFactory.getLogger(getClass())
							.error("The answer is not a valid JSON: " + response.statusCode(), ex); //$NON-NLS-1$
				}
			} else {
				LoggerFactory.getLogger(getClass()).error("Getting refreshed access token failed [" //$NON-NLS-1$
						+ response.statusCode() + StringUtils.SPACE + response.body() + "]"); //$NON-NLS-1$
			}
		} catch (IOException | InterruptedException e) {
			LoggerFactory.getLogger(getClass()).error("Error getting refreshed access token", e); //$NON-NLS-1$
		}
		return Optional.empty();
	}

	public Optional<String> getAuthCode(String tokenGroup, IMednetAuthUi iMedNetAuthUi) {
		String codeVerifier = generateCodeVerifier();
		String codeChallenge = generateCodeChallenge(codeVerifier);
		this.currentCodeVerifier = codeVerifier;

		String stateValue = getCurrentState(true);
		String authUrl = getQueryParamUrl(tokenGroup, codeChallenge, stateValue);
		iMedNetAuthUi.openBrowser(authUrl);

		LoggerFactory.getLogger(getClass()).info("Browser opened with URL: {}", authUrl); //$NON-NLS-1$

		Object value = iMedNetAuthUi.getWithCancelableProgress(Messages.MednetAuthService_browserAuthorizationPrompt,
				new GetAuthCodeWithStateSupplier(stateValue));

		if (value instanceof String) {
			return Optional.of((String) value);
		}
		LoggerFactory.getLogger(getClass()).warn("No authorization code received."); //$NON-NLS-1$
		return Optional.empty();
	}

	private String getQueryParamUrl(String tokenGroup, String codeChallenge, String stateValue) {
		String oauthRestUrl = getOauthRestUrl();
		if (!oauthRestUrl.endsWith("/")) { //$NON-NLS-1$
			oauthRestUrl += "/"; //$NON-NLS-1$
		}

		StringBuilder sb = new StringBuilder();
		sb.append(oauthRestUrl);
		sb.append("connect/authorize?"); //$NON-NLS-1$
		sb.append("response_type=code"); //$NON-NLS-1$
		sb.append("&client_id=").append(URLEncoder.encode(getClientId(), StandardCharsets.UTF_8)); //$NON-NLS-1$
		sb.append("&redirect_uri=").append(URLEncoder.encode(getRedirectUri(), StandardCharsets.UTF_8)); //$NON-NLS-1$
		sb.append("&scope=").append(URLEncoder.encode("openid profile mednet-web is-api email role offline_access", //$NON-NLS-1$ //$NON-NLS-2$
				StandardCharsets.UTF_8));
		sb.append("&state=").append(URLEncoder.encode(stateValue, StandardCharsets.UTF_8)); //$NON-NLS-1$
		sb.append("&code_challenge=").append(URLEncoder.encode(codeChallenge, StandardCharsets.UTF_8)); //$NON-NLS-1$
		sb.append("&code_challenge_method=S256"); //$NON-NLS-1$
		sb.append("&login_hint=").append(URLEncoder.encode(getLoginHint(), StandardCharsets.UTF_8)); //$NON-NLS-1$

		LoggerFactory.getLogger(getClass()).info("Authorization URL: {}", sb.toString()); //$NON-NLS-1$

		return sb.toString();
	}

	private String getLoginHint() {
		if (configService == null) {
			throw new IllegalStateException("IConfigService ist nicht initialisiert."); //$NON-NLS-1$
		}
		String userName = configService.getActiveUserContact(PreferenceConstants.MEDNET_USER_STRING, ""); //$NON-NLS-1$
		if (userName == null || userName.trim().isEmpty()) {
			LoggerFactory.getLogger(getClass()).warn("Kein Login-Hinweis in den Einstellungen gefunden."); //$NON-NLS-1$
		} else {
			LoggerFactory.getLogger(getClass()).info("Login-Hinweis abgerufen: {}", userName); //$NON-NLS-1$
		}
		return userName;
	}

	private Optional<String> getAccessToken(String tokenGroup, String authCode, String oauthRestUrl) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("grant_type", "authorization_code"); //$NON-NLS-1$ //$NON-NLS-2$
		parameters.put("code", authCode); //$NON-NLS-1$
		parameters.put("redirect_uri", getRedirectUri()); //$NON-NLS-1$
		parameters.put("client_id", getClientId()); //$NON-NLS-1$
		parameters.put("client_secret", getClientSecret()); //$NON-NLS-1$
		parameters.put("code_verifier", this.currentCodeVerifier); //$NON-NLS-1$
		String form = parameters.entrySet().stream()
				.map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)) //$NON-NLS-1$
				.collect(Collectors.joining("&")); //$NON-NLS-1$
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(buildTokenEndpoint(oauthRestUrl)))
				.header("Content-Type", "application/x-www-form-urlencoded") //$NON-NLS-1$ //$NON-NLS-2$
				.POST(HttpRequest.BodyPublishers.ofString(form)).build();
		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() >= 200 && response.statusCode() < 300) {
				Gson gson = new Gson();
				Map<?, ?> map = gson.fromJson(response.body(), Map.class);
				String token = (String) map.get("access_token"); //$NON-NLS-1$
				configService.setActiveMandator(PreferenceConstants.PREF_TOKEN + tokenGroup, token);
				String refreshToken = (String) map.get("refresh_token"); //$NON-NLS-1$
				if (StringUtils.isNotBlank(refreshToken)) {
					configService.setActiveMandator(PreferenceConstants.PREF_REFRESHTOKEN + tokenGroup, refreshToken);
				}
				Double expiresInSeconds = (Double) map.get("expires_in"); //$NON-NLS-1$
				Long expires = System.currentTimeMillis() + (expiresInSeconds.longValue() * 1000);
				configService.setActiveMandator(PreferenceConstants.PREF_TOKEN_EXPIRES + tokenGroup,
						Long.toString(expires));
				LoggerFactory.getLogger(getClass()).info("Got access token for [{}] expires [{}]", tokenGroup, expires); //$NON-NLS-1$
				return Optional.of(token);
			} else {
				LoggerFactory.getLogger(getClass()).error("Getting access token failed [{} {}]", response.statusCode(), //$NON-NLS-1$
						response.body());
			}

		} catch (IOException | InterruptedException e) {
			LoggerFactory.getLogger(getClass()).error("Error getting access token", e); //$NON-NLS-1$
		}
		return Optional.empty();
	}

	private String generateCodeVerifier() {
		byte[] code = new byte[64];
		new Random().nextBytes(code);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(code).substring(0, 43);
	}

	private String generateCodeChallenge(String codeVerifier) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256"); //$NON-NLS-1$
			byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
			return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 Algorithm not available.", e); //$NON-NLS-1$
		}
	}

	private String getRedirectUri() {
		return ApiConstants.BASE_REDERICT_URI;
	}

	private String getCurrentState(boolean refresh) {
		if (refresh) {
			currentState = UUID.randomUUID().toString();
		}
		return currentState;
	}

	private String getClientId() {
		String mode = configService.getActiveUserContact(PreferenceConstants.MEDNET_MODE, "DEMO"); //$NON-NLS-1$
		try (InputStream properties = getClass().getResourceAsStream("/rsc/id.properties")) { //$NON-NLS-1$
			if (properties != null) {
				Properties idProps = new Properties();
				idProps.load(properties);
				if ("PRODUKTIV".equals(mode)) { //$NON-NLS-1$
					return idProps.getProperty("client_id_prod"); //$NON-NLS-1$
				} else {
					return idProps.getProperty("client_id_demo"); //$NON-NLS-1$
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error loading id properties", e); //$NON-NLS-1$
		}
		return StringUtils.EMPTY;
	}

	private String getClientSecret() {
		String mode = configService.getActiveUserContact(PreferenceConstants.MEDNET_MODE, "DEMO"); //$NON-NLS-1$
		try (InputStream properties = getClass().getResourceAsStream("/rsc/id.properties")) { //$NON-NLS-1$
			if (properties != null) {
				Properties idProps = new Properties();
				idProps.load(properties);
				if ("PRODUKTIV".equals(mode)) { //$NON-NLS-1$
					return idProps.getProperty("client_secret_prod"); //$NON-NLS-1$
				} else {
					return idProps.getProperty("client_secret_demo"); //$NON-NLS-1$
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error loading id properties", e); //$NON-NLS-1$
		}
		return StringUtils.EMPTY;
	}

	private Optional<String> validateToken(String existingToken, String tokenGroup) {
		String tokenExpires = configService.getActiveMandator(PreferenceConstants.PREF_TOKEN_EXPIRES + tokenGroup,
				null);
		String refreshToken = configService.getActiveMandator(PreferenceConstants.PREF_REFRESHTOKEN + tokenGroup, null);

		if (StringUtils.isNotBlank(existingToken) && StringUtils.isNotBlank(tokenExpires)) {
			try {
				long expires = Long.parseLong(tokenExpires);
				if (System.currentTimeMillis() < expires) {
					return Optional.of(existingToken);
				}
			} catch (NumberFormatException ignore) {
				/* gehe zum Refresh */ }
		}

		if (StringUtils.isNotBlank(refreshToken)) {
			Optional<String> refreshed = getAccessTokenWithRefresh(tokenGroup, refreshToken, getOauthRestUrl());
			if (refreshed.isPresent()) {
				return refreshed;
			}
			configService.setActiveMandator(PreferenceConstants.PREF_REFRESHTOKEN + tokenGroup, null);
		}
		configService.setActiveMandator(PreferenceConstants.PREF_TOKEN + tokenGroup, null);
		configService.setActiveMandator(PreferenceConstants.PREF_TOKEN_EXPIRES + tokenGroup, null);
		return Optional.empty();
	}

	@Override
	public Optional<String> handleException(Exception ex, Map<String, Object> parameters) {
		if (ex.getMessage().contains("HTTP response code: 401")) { //$NON-NLS-1$
			String tokenGroup = (String) parameters.get(PreferenceConstants.TOKEN_GROUP);
			LoggerFactory.getLogger(getClass()).info("Got HTTP 401 invalidating token for [{}]", tokenGroup); //$NON-NLS-1$
			configService.setActiveMandator(PreferenceConstants.PREF_TOKEN + tokenGroup, null);
			configService.setActiveMandator(PreferenceConstants.PREF_TOKEN_EXPIRES + tokenGroup, null);
			return Optional.of("HIN oAuth token für [" + tokenGroup + "] ist nicht mehr gültig."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return Optional.empty();
	}

	private String buildTokenEndpoint(String base) {
		if (!base.endsWith("/")) //$NON-NLS-1$
			base += "/"; //$NON-NLS-1$
		return base + "connect/token"; //$NON-NLS-1$
	}
}
