package ch.elexis.covid.cert.service.rest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.elexis.covid.cert.service.CertificatesService.Mode;
import ch.elexis.covid.cert.service.rest.model.RecoveryModel;
import ch.elexis.covid.cert.service.rest.model.RevokeModel;
import ch.elexis.covid.cert.service.rest.model.SuccessResponse;
import ch.elexis.covid.cert.service.rest.model.TestModel;
import ch.elexis.covid.cert.service.rest.model.VaccinationModel;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Response;

public class CovidCertificateApi {

	private Client jaxrsClient;
	private XSignatureClientRequestFilter xSignatureClientRequestFilter;

	private Mode mode;
	private Gson gson;

	private Properties keyProperties;

	public CovidCertificateApi(Mode mode, Properties keyProperties) {
		this.mode = mode;
		this.keyProperties = keyProperties;
		this.jaxrsClient = createJaxrsClient();

		this.gson = new GsonBuilder().create();
	}

	public synchronized Object vaccination(VaccinationModel model) {
		WebTarget target = jaxrsClient.target(getBaseUrl()).path("/api/v1/covidcertificate/vaccination");
		LoggerFactory.getLogger(getClass()).info("API target [" + target + "]");

		xSignatureClientRequestFilter.setPayload(gson.toJson(model));
		final Response response = target.request().post(Entity.json(gson.toJson(model)));

		if (response.getStatus() >= 300) {
			String message = "[" + response.getStatus() + "]\n" + response.readEntity(String.class);
			LoggerFactory.getLogger(getClass()).error(message);
			return message;
		} else {
			return response.readEntity(SuccessResponse.class);
		}
	}

	public synchronized Object test(TestModel model) {
		WebTarget target = jaxrsClient.target(getBaseUrl()).path("/api/v1/covidcertificate/test");
		LoggerFactory.getLogger(getClass()).info("API target [" + target + "]");

		xSignatureClientRequestFilter.setPayload(gson.toJson(model));
		final Response response = target.request().post(Entity.json(gson.toJson(model)));

		if (response.getStatus() >= 300) {
			String message = "[" + response.getStatus() + "]\n" + response.readEntity(String.class);
			LoggerFactory.getLogger(getClass()).error(message);
			return message;
		} else {
			return response.readEntity(SuccessResponse.class);
		}
	}

	public synchronized Object recovery(RecoveryModel model) {
		WebTarget target = jaxrsClient.target(getBaseUrl()).path("/api/v1/covidcertificate/recovery");
		LoggerFactory.getLogger(getClass()).info("API target [" + target + "]");

		xSignatureClientRequestFilter.setPayload(gson.toJson(model));
		final Response response = target.request().post(Entity.json(gson.toJson(model)));

		if (response.getStatus() >= 300) {
			String message = "[" + response.getStatus() + "]\n" + response.readEntity(String.class);
			LoggerFactory.getLogger(getClass()).error(message);
			return message;
		} else {
			return response.readEntity(SuccessResponse.class);
		}
	}

	public synchronized Object revoke(RevokeModel model) {
		WebTarget target = jaxrsClient.target(getBaseUrl()).path("/api/v1/covidcertificate/revoke");
		LoggerFactory.getLogger(getClass()).info("API target [" + target + "]");

		xSignatureClientRequestFilter.setPayload(gson.toJson(model));
		final Response response = target.request().post(Entity.json(gson.toJson(model)));

		if (response.getStatus() >= 300) {
			String message = "[" + response.getStatus() + "]\n" + response.readEntity(String.class);
			LoggerFactory.getLogger(getClass()).error(message);
			return message;
		} else {
			return null;
		}
	}

	public synchronized Object issuableVaccines() {
		WebTarget target = jaxrsClient.target(getBaseUrl()).path("/api/v1/valuesets/issuable-vaccines");
		LoggerFactory.getLogger(getClass()).info("API target [" + target + "]");

		final Response response = target.request().get();

		if (response.getStatus() >= 300) {
			String message = "[" + response.getStatus() + "]\n" + response.readEntity(String.class);
			LoggerFactory.getLogger(getClass()).error(message);
			return message;
		} else {
			return response.readEntity(String.class);
		}
	}

	public synchronized Object issuableRapidTests() {
		WebTarget target = jaxrsClient.target(getBaseUrl()).path("/api/v1/valuesets/issuable-rapid-tests");
		LoggerFactory.getLogger(getClass()).info("API target [" + target + "]");

		final Response response = target.request().get();

		if (response.getStatus() >= 300) {
			String message = "[" + response.getStatus() + "]\n" + response.readEntity(String.class);
			LoggerFactory.getLogger(getClass()).error(message);
			return message;
		} else {
			return response.readEntity(String.class);
		}
	}

	private String getBaseUrl() {
		return mode.getUrl();
	}

	private Client createJaxrsClient() {
		try {
			SSLContext sslcontext = SSLContext.getInstance("TLS");

			KeyStore clientStore = KeyStore.getInstance("PKCS12");
			clientStore.load(
					getClass().getClassLoader().getResourceAsStream(
							"/rsc/" + keyProperties.getProperty(mode == Mode.TEST ? "testcert" : "prodcert")),
					keyProperties.getProperty(mode == Mode.TEST ? "testcertpass" : "prodcertpass").toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(clientStore,
					keyProperties.getProperty(mode == Mode.TEST ? "testcertpass" : "prodcertpass").toCharArray());

			sslcontext.init(kmf.getKeyManagers(), new TrustManager[] { new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}

			} }, new java.security.SecureRandom());
			return ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier((s1, s2) -> true)
					.withConfig(getClientConfig()).build();
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).warn("Error creating jaxrs client", e);
		}
		return null;
	}

	private Configuration getClientConfig() {
		ClientConfig config = new ClientConfig();

		xSignatureClientRequestFilter = new XSignatureClientRequestFilter();

		config.register(xSignatureClientRequestFilter);

		return config;
	}

	private class XSignatureClientRequestFilter implements ClientRequestFilter {

		private String payload;

		private String signedPayload;

		private PrivateKey privateKey;

		@Override
		public void filter(ClientRequestContext request) throws IOException {
			request.getHeaders().add("X-Signature", getSignedPayload());
		}

		public String getSignedPayload() {
			if (signedPayload == null) {
				signPayload();
			}
			return signedPayload;
		}

		private void signPayload() {
			if (payload != null) {
				// load the key
				PrivateKey privateKey = getPrivateKey();
				// canonicalize
				String normalizedJson = payload.replaceAll("[\\n\\r\\t ]", StringUtils.EMPTY);
				byte[] bytes = normalizedJson.getBytes(StandardCharsets.UTF_8);
				try {
					// sign
					Signature signature = Signature.getInstance("SHA256withRSA");
					signature.initSign(privateKey);
					signature.update(bytes);

					signedPayload = Base64.getEncoder().encodeToString(signature.sign());
				} catch (Exception e) {
					LoggerFactory.getLogger(getClass()).warn("Error signing payload", e);
				} finally {
					payload = null;
				}
			}
		}

		public void setPayload(String json) {
			this.payload = json;
			this.signedPayload = null;
		}

		private PrivateKey getPrivateKey() {
			if (this.privateKey == null) {
				this.privateKey = loadPrivateKey();
			}
			return this.privateKey;
		}

		private PrivateKey loadPrivateKey() {
			PrivateKey privateKey = null;
			try {
				InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
						"/rsc/" + keyProperties.getProperty(mode == Mode.TEST ? "testkey" : "prodkey"));
				if (inputStream != null) {
					String pemString = IOUtils.toString(inputStream, "UTF-8");
					pemString = pemString.replaceAll("(\\r|\\n|\\r\\n)+", StringUtils.EMPTY);
					String keyString = StringUtils.substringBetween(pemString, "-----BEGIN PRIVATE KEY-----",
							"-----END PRIVATE KEY-----");

					byte[] decoded = Base64.getDecoder().decode(keyString);

					KeyFactory keyFactory = KeyFactory.getInstance("RSA");
					PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
					return keyFactory.generatePrivate(keySpec);
				}
			} catch (Exception e) {
				LoggerFactory.getLogger(getClass()).warn("Error loading private key", e);
			}
			return privateKey;
		}
	}
}
