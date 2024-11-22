package ch.elexis.mednet.webapi.core.auth;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import ch.elexis.mednet.webapi.core.constants.ApiConstants;

public class GetAuthCodeWithStateSupplier implements Supplier<String> {

	private String state;
	private static final int MAX_RETRIES = 5;
	private static final int RETRY_DELAY_MS = 2000;

	public GetAuthCodeWithStateSupplier(String state) {
		this.state = state;
	}

	@Override
	public String get() {
		String ret = null;
		int retries = 0;

		while (ret == null && retries < MAX_RETRIES) {
			try (InputStream input = new URL(
					ApiConstants.BASE_REDERICT_URI_OBTAIN + URLEncoder.encode(state, "UTF-8")).openStream()) {
				String token = IOUtils.toString(input, "UTF-8");
				if (StringUtils.isNotBlank(token)) {
					ret = token;
//					LoggerFactory.getLogger(getClass()).info("Authorization Code received: {}", token);
				} else {
					LoggerFactory.getLogger(getClass()).warn("No authorization code for state: {}", state);
				}
			} catch (IOException e) {
				LoggerFactory.getLogger(getClass()).error(
						"Error retrieving the authorization code from URL: https://tools.medelexis.ch/mednet/ac-obtain/{} - {}",
						state, e.getMessage(), e);
			}

			// Retry-Logik
			if (ret == null) {
				retries++;
				LoggerFactory.getLogger(getClass()).info("Retry {} von {}", retries, MAX_RETRIES);
				try {
					Thread.sleep(RETRY_DELAY_MS);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}

		return ret;
	}
}
