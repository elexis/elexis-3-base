package ch.elexis.mednet.webapi.core.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
			try (InputStream input = new URL(ApiConstants.BASE_REDERICT_URI_OBTAIN + URLEncoder.encode(state, "UTF-8"))
					.openStream()) {
				String token = IOUtils.toString(input, "UTF-8");
				if (StringUtils.isNotBlank(token)) {
					ret = token;
				} else {
					LoggerFactory.getLogger(getClass()).warn("No authorization code for state: {}", state);
				}
			} catch (IOException e) {
				try {
					LoggerFactory.getLogger(getClass()).error("Error retrieving the authorization code from URL: "
							+ ApiConstants.BASE_REDERICT_URI_OBTAIN + URLEncoder.encode(state, "UTF-8"));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			}

			if (ret == null) {
				retries++;
				if (retries < MAX_RETRIES) {
					try {
						Thread.sleep(RETRY_DELAY_MS);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}

		if (ret != null) {
			LoggerFactory.getLogger(getClass()).info("Authorization token successfully retrieved after {} retries.",
					retries);
		} else {
			LoggerFactory.getLogger(getClass()).error("Failed to retrieve an authorization token after {} retries.",
					retries);
		}

		return ret;
	}
}
