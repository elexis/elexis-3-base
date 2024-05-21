package at.medevit.elexis.hin.auth.core.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import at.medevit.elexis.hin.auth.core.GetAuthCodeWithStateSupplier;
import at.medevit.elexis.hin.auth.core.IHinAuthService;
import at.medevit.elexis.hin.auth.core.IHinAuthUi;
import ch.elexis.core.services.IConfigService;

@Component
public class HinAuthService implements IHinAuthService {

	@Reference
	private IConfigService configService;

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
	private IHinAuthUi authUi;

	private boolean useQueryParam = true;

	private String currentState;

	@Override
	public Optional<String> getToken(Map<String, Object> parameters) {
		String tokenGroup = (String) parameters.get(IHinAuthService.TOKEN_GROUP);
		if (StringUtils.isNotBlank(tokenGroup)) {
			Optional<String> existingToken = validateToken(
					configService.getActiveMandator(IHinAuthService.PREF_TOKEN + tokenGroup, null), tokenGroup);
			if (existingToken.isEmpty() && authUi != null) {
				return getToken(tokenGroup, authUi);
			} else if (existingToken.isPresent()) {
				return existingToken;
			}
		}
		return Optional.empty();
	}

	private Optional<String> getToken(String tokenGroup, IHinAuthUi iHinAuthUi) {
		Optional<String> authCode = getAuthCode(tokenGroup, iHinAuthUi);
		if (authCode.isPresent()) {
			return getAccessToken(tokenGroup, authCode.get(), getOauthRestUrl());
		} else {
			LoggerFactory.getLogger(getClass()).warn("No auth code for [" + tokenGroup + "]");
		}

		return Optional.empty();
	}

	private String getOauthRestUrl() {
		return configService.get(PREF_RESTBASEURL, "https://oauth2.hin.ch/REST/v1/OAuth/");
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
			HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() >= 200 && response.statusCode() < 300) {
				Gson gson = new Gson();
				@SuppressWarnings("rawtypes")
				Map map = gson.fromJson(response.body().toString(), Map.class);
				String token = (String) map.get("access_token");
				configService.setActiveMandator(IHinAuthService.PREF_TOKEN + tokenGroup, token);
				String refreshtoken = (String) map.get("refresh_token");
				if (StringUtils.isNotBlank(refreshtoken)) {
					configService.setActiveMandator(IHinAuthService.PREF_REFRESHTOKEN + tokenGroup, refreshtoken);
				}
				Double expiresInSeconds = (Double) map.get("expires_in");
				Long expires = (Long) System.currentTimeMillis() + (expiresInSeconds.longValue() * 1000);
				configService.setActiveMandator(IHinAuthService.PREF_TOKEN_EXPIRES + tokenGroup,
						Long.toString(expires));
				LoggerFactory.getLogger(getClass()).info(
						"Got refreshed access token for [" + tokenGroup + "] expires [" + Long.toString(expires) + "]");
				return Optional.of(token);
			} else {
				LoggerFactory.getLogger(getClass()).error("Getting refreshed access token failed ["
						+ response.statusCode() + " " + response.body().toString() + "]");
			}
		} catch (IOException | InterruptedException e) {
			LoggerFactory.getLogger(getClass()).error("Error getting refreshed access token", e);
		}
		return Optional.empty();
	}

	private Optional<String> getAccessToken(String tokenGroup, String authCode, String oauthRestUrl) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("grant_type", "authorization_code");
		parameters.put("code", authCode);
		parameters.put("redirect_uri", getRedirectUri());
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
			HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() >= 200 && response.statusCode() < 300) {
				Gson gson = new Gson();
				@SuppressWarnings("rawtypes")
				Map map = gson.fromJson(response.body().toString(), Map.class);
				String token = (String) map.get("access_token");
				configService.setActiveMandator(IHinAuthService.PREF_TOKEN + tokenGroup, token);
				String refreshtoken = (String) map.get("refresh_token");
				if (StringUtils.isNotBlank(refreshtoken)) {
					configService.setActiveMandator(IHinAuthService.PREF_REFRESHTOKEN + tokenGroup, refreshtoken);
				}
				Double expiresInSeconds = (Double) map.get("expires_in");
				Long expires = (Long) System.currentTimeMillis() + (expiresInSeconds.longValue() * 1000);
				configService.setActiveMandator(IHinAuthService.PREF_TOKEN_EXPIRES + tokenGroup,
						Long.toString(expires));
				LoggerFactory.getLogger(getClass())
						.info("Got access token for [" + tokenGroup + "] expires [" + Long.toString(expires) + "]");
				return Optional.of(token);
			} else {
				LoggerFactory.getLogger(getClass()).error("Getting access token failed [" + response.statusCode() + " "
						+ response.body().toString() + "]");
			}
		} catch (IOException | InterruptedException e) {
			LoggerFactory.getLogger(getClass()).error(
					"Error getting access token", e);
		}
		return Optional.empty();
	}

	private Optional<String> getAuthCode(String tokenGroup, IHinAuthUi iHinAuthUi) {
		if (useQueryParam) {
			iHinAuthUi.openBrowser(getQueryParamUrl(tokenGroup));
			Object value =
					iHinAuthUi.getWithCancelableProgress("HIN Berechtigung im Browser bestätigen.",
							new GetAuthCodeWithStateSupplier(getCurrentState(false)));
			if (value instanceof String) {
				return Optional.of((String) value);
			}
			return Optional.empty();
		} else {
			iHinAuthUi.openBrowser(getWebappUrl(tokenGroup));
			return iHinAuthUi.openInputDialog("HIN oAuth Token",
					"Bitte geben Sie den oAuth Code von der HIN Webseite hier ein.");
		}
	}

	private String getWebappUrl(String tokenGroup) {
		StringBuilder sb = new StringBuilder();
		sb.append(configService.get(PREF_WEBAPPBASEURL, "http://apps.hin.ch/#app=HinCredMgrOAuth;"));
		sb.append("tokenGroup=");
		sb.append(URLEncoder.encode(tokenGroup, StandardCharsets.UTF_8));
		return sb.toString();
	}

	private String getQueryParamUrl(String tokenGroup) {
		StringBuilder sb = new StringBuilder();
		sb.append(configService.get(PREF_RESTBASEURL, "http://apps.hin.ch/REST/v1/OAuth/"));
		sb.append("GetAuthCode/");
		sb.append(URLEncoder.encode(tokenGroup, StandardCharsets.UTF_8));
		sb.append("?response_type=code");
		sb.append("&client_id=");
		sb.append(URLEncoder.encode(getClientId(), StandardCharsets.UTF_8));
		sb.append("&redirect_uri=");
		sb.append(URLEncoder.encode(getRedirectUri(), StandardCharsets.UTF_8));
		sb.append("&state=");
		sb.append(URLEncoder.encode(getCurrentState(true), StandardCharsets.UTF_8));
		return sb.toString();
	}

	private String getRedirectUri() {
		if (useQueryParam) {
			return "https://tools.medelexis.ch/hin/ac";
		}
		return "";
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
			String tokenExpires = configService.getActiveMandator(IHinAuthService.PREF_TOKEN_EXPIRES + tokenGroup,
					null);
			if (StringUtils.isNotBlank(tokenExpires)) {
				Long expires = Long.parseLong(tokenExpires);
				if (System.currentTimeMillis() > expires) {
					String refreshToken = configService
							.getActiveMandator(IHinAuthService.PREF_REFRESHTOKEN + tokenGroup, null);
					if (StringUtils.isNotBlank(refreshToken)) {
						Optional<String> refreshedToken = getAccessTokenWithRefresh(tokenGroup, refreshToken,
								getOauthRestUrl());
						if (refreshedToken.isPresent()) {
							return refreshedToken;
						} else {
							configService.setActiveMandator(IHinAuthService.PREF_REFRESHTOKEN + tokenGroup, null);
						}
					}
					configService.setActiveMandator(IHinAuthService.PREF_TOKEN + tokenGroup, null);
					configService.setActiveMandator(IHinAuthService.PREF_TOKEN_EXPIRES + tokenGroup, null);
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
			String tokenGroup = (String) parameters.get(IHinAuthService.TOKEN_GROUP);
			LoggerFactory.getLogger(getClass()).info("Got HTTP 401 invalidating token for [" + tokenGroup + "]");
			configService.setActiveMandator(IHinAuthService.PREF_TOKEN + tokenGroup, null);
			configService.setActiveMandator(IHinAuthService.PREF_TOKEN_EXPIRES + tokenGroup, null);
			return Optional.of("HIN oAuth token für [" + tokenGroup + "] nicht mehr gültig.");
		}
		return Optional.empty();
	}
}
