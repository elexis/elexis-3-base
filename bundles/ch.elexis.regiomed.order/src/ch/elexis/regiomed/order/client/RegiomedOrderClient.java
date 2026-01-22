package ch.elexis.regiomed.order.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.elexis.regiomed.order.config.RegiomedConfig;
import ch.elexis.regiomed.order.config.RegiomedHttpConstants;
import ch.elexis.regiomed.order.model.RegiomedAlternativesResponse;
import ch.elexis.regiomed.order.model.RegiomedOrderRequest;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse;
import ch.elexis.regiomed.order.model.RegiomedProductLookupResponse;

public class RegiomedOrderClient {

	private static final Logger log = LoggerFactory.getLogger(RegiomedOrderClient.class);

	private final Gson gson = new GsonBuilder().serializeNulls().create();

	public RegiomedOrderResponse sendOrderWithToken(RegiomedConfig config, RegiomedOrderRequest request)
			throws Exception {
		String token = fetchToken(config);
		request.clearPasswordForTokenAuth();

		String url = config.getOrderEndpoint();
		String responseBody = executeRequest(url, RegiomedHttpConstants.METHOD_POST, token, request);
		System.out.println("test " + responseBody);
		return parseResponse(responseBody, RegiomedOrderResponse.class);
	}

	public RegiomedProductLookupResponse searchProducts(RegiomedConfig config, String criteria) throws Exception {
		if (StringUtils.isBlank(criteria)) {
			return new RegiomedProductLookupResponse();
		}
		String encodedCriteria = URLEncoder.encode(criteria, StandardCharsets.UTF_8).replace("+", "%20"); //$NON-NLS-1$ //$NON-NLS-2$
		String url = config.getFuzzySearchEndpoint() + encodedCriteria;
		String token = fetchToken(config);
		String responseBody = executeRequest(url, RegiomedHttpConstants.METHOD_GET, token, null);
		RegiomedProductLookupResponse result = parseResponse(responseBody, RegiomedProductLookupResponse.class);
		return result != null ? result : new RegiomedProductLookupResponse();
	}

	public RegiomedAlternativesResponse getAlternatives(RegiomedConfig config, String type, String id) {
		if (StringUtils.isBlank(type) || StringUtils.isBlank(id)) {
			return new RegiomedAlternativesResponse();
		}
		String baseUrl = config.getAlternativesFlexEndpoint();
		String url = baseUrl + type + "/" + id; //$NON-NLS-1$ //$NON-NLS-2$
		try {
			String token = fetchToken(config);
			String responseBody = executeRequest(url, RegiomedHttpConstants.METHOD_GET, token, null);
			RegiomedAlternativesResponse result = parseResponse(responseBody, RegiomedAlternativesResponse.class);
			return result != null ? result : new RegiomedAlternativesResponse();
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().contains("HTTP 404")) { //$NON-NLS-1$
				log.info("Alternatives service not available (404) for article: " + id); //$NON-NLS-1$
			} else {
				log.warn("Alternatives Error for URL " + url, e); //$NON-NLS-1$
			}
			return new RegiomedAlternativesResponse();
		}
	}

	public boolean checkSearchAvailability(RegiomedConfig config) {
		try {
			searchProducts(config, "check_availability_ping"); //$NON-NLS-1$
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private String fetchToken(RegiomedConfig config) throws Exception {
		String url = config.getTokenEndpoint();
		String b64Password = Base64.getEncoder().encodeToString(config.getPassword().getBytes(StandardCharsets.UTF_8));
		RegiomedOrderRequest.TokenRequestBody body = new RegiomedOrderRequest.TokenRequestBody(config.getEmail(),
				b64Password);

		String responseBody = executeRequest(url, RegiomedHttpConstants.METHOD_POST, null, body);

		RegiomedOrderRequest.TokenResponse tokenResponse = gson.fromJson(responseBody,
				RegiomedOrderRequest.TokenResponse.class);
		if (tokenResponse == null || tokenResponse.getData() == null) {
			throw new RuntimeException("No data in token response."); //$NON-NLS-1$
		}

		String tokenToUse = null;
		if (StringUtils.isNotBlank(tokenResponse.getData().getToken())) {
			tokenToUse = tokenResponse.getData().getToken();
		} else if (StringUtils.isNotBlank(tokenResponse.getData().getTokenRaw())) {
			tokenToUse = tokenResponse.getData().getTokenRaw();
		}

		if (tokenToUse == null || StringUtils.isBlank(tokenToUse)) {
			throw new RuntimeException("No token included in response."); //$NON-NLS-1$
		}

		if (!tokenToUse.toLowerCase(Locale.ENGLISH).startsWith("bearer ")) { //$NON-NLS-1$
			tokenToUse = RegiomedHttpConstants.AUTH_BEARER_PREFIX + tokenToUse;
		}
		return tokenToUse;
	}

	private String executeRequest(String urlStr, String method, String token, Object requestBody) throws Exception {
		URI uri = URI.create(urlStr);
		URL url = uri.toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setUseCaches(false);
		conn.setRequestProperty(RegiomedHttpConstants.HEADER_ACCEPT, RegiomedHttpConstants.ACCEPT_JSON);
		if (StringUtils.isNotBlank(token)) {
			conn.setRequestProperty(RegiomedHttpConstants.HEADER_AUTHORIZATION, token.trim());
		}
		if (RegiomedHttpConstants.METHOD_POST.equals(method)) {
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty(RegiomedHttpConstants.HEADER_CONTENT_TYPE,
					RegiomedHttpConstants.CONTENT_TYPE_JSON_UTF8);
			if (requestBody != null) {
				try (OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
					out.write(gson.toJson(requestBody));
				}
			}
		}
		int status = conn.getResponseCode();
		InputStream in = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
		String responseBody = readStreamToString(in);
		if (status < 200 || status >= 300) {
			throw new RuntimeException("HTTP " + status + " Error. URL: " + urlStr + " Body: " + responseBody); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return responseBody;
	}

	private <T> T parseResponse(String json, Class<T> classOfT) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		try {
			String jsonToParse = json.trim();
			if (jsonToParse.startsWith("\"")) { //$NON-NLS-1$
				jsonToParse = gson.fromJson(jsonToParse, String.class);
			}
			return gson.fromJson(jsonToParse, classOfT);
		} catch (Exception e) {
			log.warn("Parsing error for class " + classOfT.getSimpleName(), e); //$NON-NLS-1$
			throw e;
		}
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