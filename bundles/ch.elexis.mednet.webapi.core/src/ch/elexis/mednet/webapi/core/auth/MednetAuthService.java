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
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.LoggerFactory;


import ch.elexis.core.services.IConfigService;
import ch.elexis.mednet.webapi.core.IMednetAuthService;
import ch.elexis.mednet.webapi.core.IMednetAuthUi;
import ch.elexis.mednet.webapi.core.constants.ApiConstants;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@Component
public class MednetAuthService implements IMednetAuthService {

	@Reference
	private IConfigService configService;

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
	private IMednetAuthUi authUi;

	private boolean useQueryParam = true;

	private String currentState;
	private String currentCodeVerifier;


	@Override
	public Optional<String> getToken(Map<String, Object> parameters) {
		if (configService == null) {
			LoggerFactory.getLogger(getClass()).error("configService is null!");
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

	@Override
	public Optional<String> delToken(Map<String, Object> parameters) {

		String tokenGroup = "mednet";
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		ServiceReference<IMednetAuthService> serviceReference = context.getServiceReference(IMednetAuthService.class);

		if (serviceReference != null) {
			IMednetAuthService authService = context.getService(serviceReference);
			try {

				if (configService == null) {

				}

				configService.setActiveMandator(PreferenceConstants.PREF_TOKEN + tokenGroup, null);
				configService.setActiveMandator(PreferenceConstants.PREF_TOKEN_EXPIRES + tokenGroup, null);
				configService.setActiveMandator(PreferenceConstants.PREF_REFRESHTOKEN + tokenGroup, null);

			} catch (Exception ex) {

				LoggerFactory.getLogger(getClass()).error("Error when retrieving a new token", ex);
			} finally {
				context.ungetService(serviceReference);
			}
		} else {

		}
		return Optional.empty();
	}


	private Optional<String> getToken(String tokenGroup, IMednetAuthUi iHinAuthUi) {
		Optional<String> authCode = getAuthCode(tokenGroup, iHinAuthUi);
		if (authCode.isPresent()) {
			return getAccessToken(tokenGroup, authCode.get(), getOauthRestUrl());
		} else {
			LoggerFactory.getLogger(getClass()).warn("No auth code for [" + tokenGroup + "]");
		}

		return Optional.empty();
	}


	private String getOauthRestUrl() {
		return configService.get(PreferenceConstants.PREF_RESTBASEURL, ApiConstants.BASE_URI);
	}

	private Optional<String> getAccessTokenWithRefresh(String tokenGroup, String refreshToken, String oauthRestUrl) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("grant_type", "refresh_token");
		parameters.put("refresh_token", refreshToken);
		parameters.put("client_id", getClientId());
		parameters.put("client_secret", getClientSecret());

		String form = parameters.entrySet().stream()
				.map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
				.collect(Collectors.joining("&"));

		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(oauthRestUrl + "GetAccessToken"))
				.headers("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(form)).build();

		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() >= 200 && response.statusCode() < 300) {
				Gson gson = new Gson();
				try {
					Map<?, ?> map = gson.fromJson(response.body(), Map.class);
					String token = (String) map.get("access_token");

					configService.setActiveMandator(PreferenceConstants.PREF_TOKEN + tokenGroup, token);
					String refreshtoken = (String) map.get("refresh_token");

					if (StringUtils.isNotBlank(refreshtoken)) {
						configService.setActiveMandator(PreferenceConstants.PREF_REFRESHTOKEN + tokenGroup,
								refreshtoken);
					}

					Double expiresInSeconds = (Double) map.get("expires_in");
					Long expires = System.currentTimeMillis() + (expiresInSeconds.longValue() * 1000);
					configService.setActiveMandator(PreferenceConstants.PREF_TOKEN_EXPIRES + tokenGroup,
							Long.toString(expires));

					LoggerFactory.getLogger(getClass()).info("Got refreshed access token for [{}] expires [{}]",
							tokenGroup, Long.toString(expires));

					return Optional.of(token);
				} catch (JsonSyntaxException ex) {
					LoggerFactory.getLogger(getClass()).error(
							"The answer is not a valid JSON: " + response.statusCode(),
							ex);
				}
			} else {
				LoggerFactory.getLogger(getClass()).error("Getting refreshed access token failed ["
						+ response.statusCode() + " " + response.body() + "]");
			}
		} catch (IOException | InterruptedException e) {
			LoggerFactory.getLogger(getClass()).error("Error getting refreshed access token", e);
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

		LoggerFactory.getLogger(getClass()).info("Browser opened with URL: {}", authUrl);

		Object value = iMedNetAuthUi.getWithCancelableProgress("Mednet Berechtigung im Browser bestätigen.",
				new GetAuthCodeWithStateSupplier(stateValue));

		if (value instanceof String) {
			LoggerFactory.getLogger(getClass()).info("Authorization Code received: {}", value);
			return Optional.of((String) value);
		}
		LoggerFactory.getLogger(getClass()).warn("No authorization code received.");
		return Optional.empty();
	}

	private String getQueryParamUrl(String tokenGroup, String codeChallenge, String stateValue) {
		String oauthRestUrl = getOauthRestUrl();
		if (!oauthRestUrl.endsWith("/")) {
			oauthRestUrl += "/";
		}

		StringBuilder sb = new StringBuilder();
		sb.append(oauthRestUrl);
		sb.append("connect/authorize?");
		sb.append("response_type=code");
		sb.append("&client_id=").append(URLEncoder.encode(getClientId(), StandardCharsets.UTF_8));
		sb.append("&redirect_uri=").append(URLEncoder.encode(getRedirectUri(), StandardCharsets.UTF_8));
		sb.append("&scope=").append(URLEncoder.encode("openid profile mednet-web is-api email role offline_access",
				StandardCharsets.UTF_8));
		sb.append("&state=").append(URLEncoder.encode(stateValue, StandardCharsets.UTF_8));
		sb.append("&code_challenge=").append(URLEncoder.encode(codeChallenge, StandardCharsets.UTF_8));
		sb.append("&code_challenge_method=S256");
		sb.append("&login_hint=").append(URLEncoder.encode(getLoginHint(), StandardCharsets.UTF_8));

		LoggerFactory.getLogger(getClass()).info("Authorization URL: {}", sb.toString());

		return sb.toString();
	}

	private String getLoginHint() {
		IEclipsePreferences node = InstanceScope.INSTANCE
				.getNode(String.valueOf(FrameworkUtil.getBundle(getClass()).getBundleId()));
		return node.get(PreferenceConstants.MEDNET_USER_STRING, "");
	}

	private Optional<String> getAccessToken(String tokenGroup, String authCode, String oauthRestUrl) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("grant_type", "authorization_code");
		parameters.put("code", authCode);
		parameters.put("redirect_uri", getRedirectUri());
		parameters.put("client_id", getClientId());
		parameters.put("client_secret", getClientSecret());
		parameters.put("code_verifier", this.currentCodeVerifier);
		String form = parameters.entrySet().stream()
				.map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
				.collect(Collectors.joining("&"));
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(oauthRestUrl + "/connect/token"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(form)).build();
		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() >= 200 && response.statusCode() < 300) {
				Gson gson = new Gson();
				Map<?, ?> map = gson.fromJson(response.body(), Map.class);
				String token = (String) map.get("access_token");
				configService.setActiveMandator(PreferenceConstants.PREF_TOKEN + tokenGroup, token);
				String refreshToken = (String) map.get("refresh_token");
				if (StringUtils.isNotBlank(refreshToken)) {
					configService.setActiveMandator(PreferenceConstants.PREF_REFRESHTOKEN + tokenGroup, refreshToken);
				}
				Double expiresInSeconds = (Double) map.get("expires_in");
				Long expires = System.currentTimeMillis() + (expiresInSeconds.longValue() * 1000);
				configService.setActiveMandator(PreferenceConstants.PREF_TOKEN_EXPIRES + tokenGroup,
						Long.toString(expires));
				LoggerFactory.getLogger(getClass()).info("Got access token for [{}] expires [{}]", tokenGroup, expires);
				return Optional.of(token);
			} else {
				LoggerFactory.getLogger(getClass()).error("Getting access token failed [{} {}]", response.statusCode(),
						response.body());
			}

		} catch (IOException | InterruptedException e) {
			LoggerFactory.getLogger(getClass()).error("Error getting access token", e);
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
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
			return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 Algorithm not available.", e);
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
		try (InputStream properties = getClass().getResourceAsStream("/rsc/id.properties")) {
			if (properties != null) {
				Properties idProps = new Properties();
				idProps.load(properties);
				return idProps.getProperty("client_id");
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error loading id properties", e);
		}
		return StringUtils.EMPTY;
	}

	private String getClientSecret() {
		try (InputStream properties = getClass().getResourceAsStream("/rsc/id.properties")) {
			if (properties != null) {
				Properties idProps = new Properties();
				idProps.load(properties);
				return idProps.getProperty("client_secret");
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error loading id properties", e);
		}
		return StringUtils.EMPTY;
	}

	private Optional<String> validateToken(String existingToken, String tokenGroup) {
		if (StringUtils.isNotBlank(existingToken)) {
			String tokenExpires = configService.getActiveMandator(PreferenceConstants.PREF_TOKEN_EXPIRES + tokenGroup,
					null);
			if (StringUtils.isNotBlank(tokenExpires)) {
				Long expires = Long.parseLong(tokenExpires);
				if (System.currentTimeMillis() > expires) {
					String refreshToken = configService
							.getActiveMandator(PreferenceConstants.PREF_REFRESHTOKEN + tokenGroup, null);
					if (StringUtils.isNotBlank(refreshToken)) {
						Optional<String> refreshedToken = getAccessTokenWithRefresh(tokenGroup, refreshToken,
								getOauthRestUrl());
						if (refreshedToken.isPresent()) {
							return refreshedToken;
					} else {
						configService.setActiveMandator(PreferenceConstants.PREF_REFRESHTOKEN + tokenGroup, null);
						}
					}
					configService.setActiveMandator(PreferenceConstants.PREF_TOKEN + tokenGroup, null);
					configService.setActiveMandator(PreferenceConstants.PREF_TOKEN_EXPIRES + tokenGroup, null);
				} else {
					return Optional.of(existingToken);
				}
			}
		}
		return Optional.empty();
	}

	@Override
	public Optional<String> handleException(Exception ex, Map<String, Object> parameters) {
		if (ex.getMessage().contains("HTTP response code: 401")) {
			String tokenGroup = (String) parameters.get(PreferenceConstants.TOKEN_GROUP);
			LoggerFactory.getLogger(getClass()).info("Got HTTP 401 invalidating token for [{}]", tokenGroup);
			configService.setActiveMandator(PreferenceConstants.PREF_TOKEN + tokenGroup, null);
			configService.setActiveMandator(PreferenceConstants.PREF_TOKEN_EXPIRES + tokenGroup, null);
			return Optional.of("HIN oAuth token für [" + tokenGroup + "] ist nicht mehr gültig.");
		}
		return Optional.empty();
	}
}



