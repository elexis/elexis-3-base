package at.medevit.elexis.hin.sign.core.internal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import at.medevit.elexis.hin.auth.core.GetAuthCodeWithStateSupplier;
import at.medevit.elexis.hin.auth.core.IHinAuthService;
import at.medevit.elexis.hin.auth.core.IHinAuthUi;
import at.medevit.elexis.hin.sign.core.IHinSignService;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.status.ObjectStatus;
import ch.elexis.core.utils.OsgiServiceUtil;

@Component
public class HINSignService implements IHinSignService {

	private static Logger logger = LoggerFactory.getLogger(HINSignService.class);

	private Mode mode;

	@Reference
	private IConfigService configService;
	
	@Reference
	private IHinAuthService hinAuthService;

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
	private IHinAuthUi authUi;
	
	@Reference
	private Gson gson;

	private String currentState;

	@Override
	public void setMode(Mode mode) {
		this.mode = mode;
	}

	@Override
	public ObjectStatus<?> createPrescription(InputStream data) {
		Optional<String> adSwissAuthToken = getADSwissAuthToken();
		if (adSwissAuthToken.isPresent()) {
			Optional<String> authHandle = getEPDAuthHandle(adSwissAuthToken.get());
		}
		return null;
	}

	@Override
	public ObjectStatus<?> revokePrescription(InputStream data) {
		Optional<String> adSwissAuthToken = getADSwissAuthToken();
		if (adSwissAuthToken.isPresent()) {
			Optional<String> authHandle = getEPDAuthHandle(adSwissAuthToken.get());
		}
		return null;
	}

	protected Optional<String> getADSwissAuthToken() {
		if (hinAuthService != null) {
			return hinAuthService.getToken(Collections.singletonMap(IHinAuthService.TOKEN_GROUP,
					mode == Mode.TEST ? "ADSwiss_CI-Test" : "ADSwiss_CI"));
		} else {
			logger.error("No HIN auth service");
		}
		return Optional.empty();
	}

	protected Optional<String> getEPDAuthHandle(String bearerToken) {
		Optional<String> existingHandle = validateEPDAuthHandle(
				configService.getActiveMandator(IHinAuthService.PREF_EPDAUTHHANDLE, null));
		if(existingHandle.isEmpty()) {
			existingHandle = getEPDAuthHandle(bearerToken, authUi);
			if (existingHandle.isPresent()) {
				// expires after 12h
				Long expires = System.currentTimeMillis() + ((1000 * 60 * 60) * 12);
				configService.setActiveMandator(IHinAuthService.PREF_EPDAUTHHANDLE, existingHandle.get());
				configService.setActiveMandator(IHinAuthService.PREF_EPDAUTHHANDLE_EXPIRES, Long.toString(expires));
			}
		}
		return existingHandle;
	}
	
	private Optional<String> validateEPDAuthHandle(String existingHandle) {
		if (StringUtils.isNotBlank(existingHandle)) {
			String tokenExpires = configService.getActiveMandator(IHinAuthService.PREF_EPDAUTHHANDLE_EXPIRES, null);
			if (StringUtils.isNotBlank(tokenExpires)) {
				Long expires = Long.parseLong(tokenExpires);
				if (System.currentTimeMillis() > expires) {
					configService.setActiveMandator(IHinAuthService.PREF_EPDAUTHHANDLE, null);
					configService.setActiveMandator(IHinAuthService.PREF_EPDAUTHHANDLE_EXPIRES, null);
				} else {
					return Optional.of(existingHandle);
				}
			} else {
				return Optional.of(existingHandle);
			}
		}
		return Optional.empty();
	}

	protected Optional<String> getEPDAuthHandle(String bearerToken, IHinAuthUi authUi) {
		try {
			URL serverURL = getEPDAuthServiceAuthCodeUrl();
			logger.info("Using EPD auth code url [" + serverURL + "]");
			HttpURLConnection httpConnection = (HttpURLConnection) serverURL.openConnection();
			httpConnection.setRequestMethod("POST");
			httpConnection.setDoOutput(false);
			httpConnection.setDoInput(true);
			httpConnection.setUseCaches(false);
			httpConnection.setRequestProperty("accept", "application/json");
			httpConnection.setRequestProperty("Authorization", "Bearer " + bearerToken);

			int responseCode = httpConnection.getResponseCode();
			if (responseCode >= 200 && responseCode < 300) {
				InputStream in = httpConnection.getInputStream();
				String encoding = httpConnection.getContentEncoding();
				encoding = encoding == null ? "UTF-8" : encoding;
				String body = IOUtils.toString(in, encoding);
				@SuppressWarnings("rawtypes")
				Map map = gson.fromJson(body, Map.class);
				String epdAuthUrl = (String) map.get("epdAuthUrl");
				logger.info("Got EPD auth url [" + epdAuthUrl + "]");
				if (StringUtils.isNotBlank(epdAuthUrl)) {
					Optional<String> epdAuthCode = getEpdAuthCode(epdAuthUrl, authUi);
					if(epdAuthCode.isPresent()) {
						serverURL = getEPDAuthServiceAuthHandleUrl();
						logger.info("Using EPD auth handle url [" + serverURL + "]");
						httpConnection = (HttpURLConnection) serverURL.openConnection();
						httpConnection.setRequestMethod("POST");
						httpConnection.setDoOutput(true);
						httpConnection.setDoInput(true);
						httpConnection.setUseCaches(false);
						httpConnection.setRequestProperty("accept", "application/json");
						httpConnection.setRequestProperty("Content-Type", "application/json");
						httpConnection.setRequestProperty("Authorization", "Bearer " + bearerToken);
						
						PrintWriter out = new PrintWriter(httpConnection.getOutputStream());
						out.println(gson.toJson(Collections.singletonMap("authCode", epdAuthCode.get())));
						out.close();

						BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(httpConnection.getInputStream()));
						String line;
						StringBuffer responseMessage = new StringBuffer();
						while ((line = bufferedReader.readLine()) != null) {
							responseMessage.append(line);
						}
						bufferedReader.close();
						if(StringUtils.isNotBlank(responseMessage)) {
							@SuppressWarnings("rawtypes")
							Map respondeMap = gson.fromJson(responseMessage.toString(), Map.class);
							if(respondeMap.containsKey("authHandle")) {
								return Optional.ofNullable((String)respondeMap.get("authHandle"));
							}
						}
					} else {
						logger.warn("Failed to get EPD auth code");
					}
				}
			} else {
				logger.warn("Failed to get EPD auth handle response code [" + responseCode + "]");
			}
		} catch (Exception e) {
			logger.warn("Failed to get EPD auth handle", e);
			if (hinAuthService != null) {
				Optional<String> message = hinAuthService.handleException(e, Collections.singletonMap(
						IHinAuthService.TOKEN_GROUP, mode == Mode.TEST ? "ADSwiss_CI-Test" : "ADSwiss_CI"));
				if (message.isPresent()) {
					logger.warn("HIN Auth message", message.get());
				}
			}
		}
		return Optional.empty();
	}

	private URL getEPDAuthServiceAuthCodeUrl() throws MalformedURLException {
		StringBuilder sb = new StringBuilder();
		sb.append("https://oauth2.ci-prep.adswiss.hin.ch/authService/EPDAuth");
		sb.append("?targetUrl=");
		sb.append(URLEncoder.encode(getRedirectUri() + "/" + getCurrentState(true), StandardCharsets.UTF_8));
		sb.append("&style=redirect");
		return new URL( sb.toString());
	}

	private URL getEPDAuthServiceAuthHandleUrl() throws MalformedURLException {
		StringBuilder sb = new StringBuilder();
		sb.append("https://oauth2.ci-prep.adswiss.hin.ch/authService/EPDAuth/auth_handle");
		return new URL( sb.toString());
	}
	
	private Optional<String> getEpdAuthCode(String epdAuthUrl, IHinAuthUi iHinAuthUi) {
		iHinAuthUi.openBrowser(epdAuthUrl);
		Object value = iHinAuthUi.getWithCancelableProgress("HIN Berechtigung im Browser bestätigen.",
				new GetAuthCodeWithStateSupplier(getCurrentState(false)));
		if (value instanceof String) {
			return Optional.of((String) value);
		}
		return Optional.empty();
	}
	
	private String getCurrentState(boolean refresh) {
		if (refresh) {
			currentState = UUID.randomUUID().toString();
		}
		return currentState;
	}
	
	private String getRedirectUri() {
		return "https://tools.medelexis.ch/hin/ac";
	}
}
