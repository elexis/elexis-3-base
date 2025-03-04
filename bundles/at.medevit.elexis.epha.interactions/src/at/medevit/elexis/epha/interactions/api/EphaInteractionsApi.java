package at.medevit.elexis.epha.interactions.api;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.medevit.elexis.epha.interactions.api.model.AdviceResponse;
import at.medevit.elexis.epha.interactions.api.model.Substance;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

public class EphaInteractionsApi {

	private Client jaxrsClient;

	private Gson gson;

	public EphaInteractionsApi() {
		this.jaxrsClient = createJaxrsClient();

		this.gson = new GsonBuilder().create();
	}

	public synchronized Object advice(List<Substance> model) {
		WebTarget target = jaxrsClient.target(getBaseUrl())
				.path("clinic/advice/" + Locale.getDefault().getLanguage() + "/"); //$NON-NLS-1$ //$NON-NLS-2$
		LoggerFactory.getLogger(getClass()).info("API target [" + target + "]"); //$NON-NLS-1$ //$NON-NLS-2$

		String jsonString = gson.toJson(model);
		// remove bad chars
		jsonString = jsonString.replaceAll("/", StringUtils.SPACE); //$NON-NLS-1$
		final Response response = target.request().post(Entity.json(jsonString));

		if (response.getStatus() >= 300) {
			String message = "[" + response.getStatus() + "]\n" + response.readEntity(String.class); //$NON-NLS-1$ //$NON-NLS-2$
			LoggerFactory.getLogger(getClass()).error(message);
			return message;
		} else {
			return response.readEntity(AdviceResponse.class);
		}
	}

	private String getBaseUrl() {
		return "https://api.epha.health/"; //$NON-NLS-1$
	}

	private Client createJaxrsClient() {
		try {
			SSLContext sslcontext = SSLContext.getInstance("TLS"); //$NON-NLS-1$
			sslcontext.init(null, new TrustManager[] { new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}

			} }, new java.security.SecureRandom());
			return ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier((s1, s2) -> true)
					.withConfig(new ClientConfig()).build();
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).warn("Error creating jaxrs client", e); //$NON-NLS-1$
		}
		return null;
	}
}
