package at.medevit.elexis.hin.sign.core.internal;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import at.medevit.elexis.hin.auth.core.IHinAuthService;
import at.medevit.elexis.hin.sign.core.IHinSignService;
import ch.elexis.core.status.ObjectStatus;

@Component
public class HINSignService implements IHinSignService {

	private static Logger logger = LoggerFactory.getLogger(HINSignService.class);

	private Mode mode;

	@Reference
	private IHinAuthService hinAuthService;

	@Reference
	private Gson gson;

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
		try {
			URL serverURL = getEPDAuthServiceAuthCodeUrl();
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
				if (StringUtils.isNotBlank(epdAuthUrl)) {
					HttpURLConnection httpEpdAuthConnection = (HttpURLConnection) new URL(epdAuthUrl).openConnection();
					httpEpdAuthConnection.setRequestMethod("GET");
					httpEpdAuthConnection.setUseCaches(false);
					httpEpdAuthConnection.setInstanceFollowRedirects(false);

					int epdAuthResponseCode = httpEpdAuthConnection.getResponseCode();
					String location = httpEpdAuthConnection.getHeaderField("Location");
					if (StringUtils.isNotBlank(location)) {
						URL locationUrl = new URL(location);

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
		return new URL(
				"https://oauth2.ci-prep.adswiss.hin.ch/authService/EPDAuth?targetUrl=http%3A%2F%2Flocalhost%2Fsuccess&style=redirect");
	}

	private URL getEPDAuthServiceAuthHandleUrl() throws MalformedURLException {
		return new URL("https://oauth2.ci-prep.adswiss.hin.ch/authService/EPDAuth/auth_handle");
	}
}
