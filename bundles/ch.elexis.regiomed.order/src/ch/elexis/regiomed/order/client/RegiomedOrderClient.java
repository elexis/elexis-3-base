package ch.elexis.regiomed.order.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.elexis.regiomed.order.config.RegiomedConfig;
import ch.elexis.regiomed.order.config.RegiomedHttpConstants;
import ch.elexis.regiomed.order.model.RegiomedOrderRequest;
import ch.elexis.regiomed.order.model.RegiomedOrderRequest.TokenRequestBody;
import ch.elexis.regiomed.order.model.RegiomedOrderRequest.TokenResponse;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse;

public class RegiomedOrderClient {

	private static final Logger log = LoggerFactory.getLogger(RegiomedOrderClient.class);

	private final Gson gson = new GsonBuilder().serializeNulls().create();

	public RegiomedOrderResponse sendOrderWithToken(RegiomedConfig config, RegiomedOrderRequest request)
			throws Exception {
		String endpoint = config.getOrderEndpoint();
		String token = fetchToken(config);
		request.clearPasswordForTokenAuth();

		URI uri = URI.create(endpoint);
		URL serverURL = uri.toURL();
		HttpURLConnection conn = (HttpURLConnection) serverURL.openConnection();
		conn.setRequestMethod(RegiomedHttpConstants.METHOD_POST);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty(RegiomedHttpConstants.HEADER_CONTENT_TYPE,
				RegiomedHttpConstants.CONTENT_TYPE_JSON_UTF8);
		conn.setRequestProperty(RegiomedHttpConstants.HEADER_ACCEPT, RegiomedHttpConstants.ACCEPT_JSON);

		String authHeaderValue = null;
		if (StringUtils.isNotBlank(token)) {
			String cleanToken = token.trim();
			authHeaderValue = cleanToken;
			conn.setRequestProperty(RegiomedHttpConstants.HEADER_AUTHORIZATION, authHeaderValue);
		}

		try (OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
			out.write(gson.toJson(request));
		}

		int status = conn.getResponseCode();
		InputStream in = (status >= 200 && status < 300) ? conn.getInputStream()
						: conn.getErrorStream();

		String responseBody = readStreamToString(in);

		if (status < 200 || status >= 300) {
			throw new RuntimeException("HTTP " + status + " Error. See log for details. Body: " + responseBody); //$NON-NLS-1$ //$NON-NLS-2$
		}

		RegiomedOrderResponse response = null;
		if (StringUtils.isNotBlank(responseBody)) {
			try {
				String jsonToParse = responseBody.trim();
				if (jsonToParse.startsWith("\"")) { //$NON-NLS-1$
					jsonToParse = gson.fromJson(jsonToParse, String.class);
				}
				response = gson.fromJson(jsonToParse, RegiomedOrderResponse.class);
			} catch (Exception parseEx) {
				log.warn("Parsing error", parseEx); //$NON-NLS-1$
				throw parseEx;
			}
		}
		return response;
	}

	private String fetchToken(RegiomedConfig config) throws Exception {
		String tokenUrl = config.getTokenEndpoint();
		String b64Password = Base64.getEncoder().encodeToString(config.getPassword().getBytes(StandardCharsets.UTF_8));
		TokenRequestBody body = new TokenRequestBody(config.getEmail(), b64Password);

		Gson gson = new Gson();
		URI uri = URI.create(tokenUrl);
		URL url = uri.toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(RegiomedHttpConstants.METHOD_POST);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty(RegiomedHttpConstants.HEADER_CONTENT_TYPE,
				RegiomedHttpConstants.CONTENT_TYPE_JSON_UTF8);
		conn.setRequestProperty(RegiomedHttpConstants.HEADER_ACCEPT, RegiomedHttpConstants.ACCEPT_JSON);

		String json = gson.toJson(body);
		try (OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
			out.write(json);
		}

		int status = conn.getResponseCode();
		InputStream in = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();

		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		}
		String responseBody = sb.toString();
		if (status < 200 || status >= 300) {
			throw new RuntimeException("Error retrieving token (HTTP " + status + "): " + responseBody); //$NON-NLS-1$ //$NON-NLS-2$
		}

		TokenResponse tokenResponse = gson.fromJson(responseBody, TokenResponse.class);
		if (tokenResponse == null || tokenResponse.data == null) {
			throw new RuntimeException("No data in token response."); //$NON-NLS-1$
		}

		String tokenToUse = null;
		if (StringUtils.isNotBlank(tokenResponse.data.tokenRaw)) {
			tokenToUse = tokenResponse.data.tokenRaw;
		} else if (StringUtils.isNotBlank(tokenResponse.data.token)) {
			tokenToUse = tokenResponse.data.token;
		}

		if (StringUtils.isBlank(tokenToUse)) {
			throw new RuntimeException("No token included in response."); //$NON-NLS-1$
		}

		return tokenToUse;
	}

	private String readStreamToString(InputStream in) throws Exception {
		if (in == null) {
			return StringUtils.EMPTY;
		}
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		}
		return sb.toString();
	}
}