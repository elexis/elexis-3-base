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
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import at.medevit.elexis.hin.auth.core.IHinAuthService;
import at.medevit.elexis.hin.auth.core.IHinAuthUi;
import ch.elexis.core.services.IConfigService;

@Component
public class HinAuthService implements IHinAuthService {

	@Reference
	private IConfigService configService;

	@Override
	public Optional<String> getToken(Map<String, Object> parameters) {
		String tokenGroup = (String) parameters.get(Parameters.TOKEN_GROUP.name());
		if (StringUtils.isNotBlank(tokenGroup)) {
			Optional<String> existingToken = validateToken(
					configService.getActiveMandator(IHinAuthService.PREF_TOKEN + tokenGroup, null), tokenGroup);
			if (existingToken.isEmpty() && (parameters.get(Parameters.UI.name()) instanceof IHinAuthUi)) {
				return getToken(tokenGroup, (IHinAuthUi) parameters.get(Parameters.UI.name()));
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

	private Optional<String> getAccessToken(String tokenGroup, String authCode, String oauthRestUrl) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("grant_type", "authorization_code");
		parameters.put("code", "authCode");
		parameters.put("redirect_uri", "");
		parameters.put("client_id", getClientId());
		parameters.put("client_secret", getClientSecret());

		String form = parameters.entrySet().stream()
				.map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
				.collect(Collectors.joining("&"));

		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(oauthRestUrl))
				.headers("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(form)).build();

		try {
			HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() >= 200 && response.statusCode() < 300) {
				Gson gson = new Gson();
				@SuppressWarnings("rawtypes")
				Map map = gson.fromJson(response.body().toString(), Map.class);
				String token = (String) map.get("access_token");
				Integer expiresInSeconds = (Integer) map.get("expires_in");
				configService.setActiveMandator(IHinAuthService.PREF_TOKEN + tokenGroup, token);
				Long expires = (Long) System.currentTimeMillis() + (expiresInSeconds * 1000);
				configService.setActiveMandator(IHinAuthService.PREF_TOKEN_EXPIRES + tokenGroup,
						Long.toString(expires));
				LoggerFactory.getLogger(getClass())
						.info("Got access token for [" + tokenGroup + "] expires [" + Long.toString(expires) + "]");
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
		iHinAuthUi.openBrowser(getQueryParamUrl(tokenGroup));
		return Optional.empty();
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
		sb.append("&state=");
		return sb.toString();
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
					configService.setActiveMandator(IHinAuthService.PREF_TOKEN + tokenGroup, null);
					configService.setActiveMandator(IHinAuthService.PREF_TOKEN_EXPIRES + tokenGroup, null);
				} else {
					return Optional.of(existingToken);
				}
			}
		}
		return Optional.empty();
	}

}
