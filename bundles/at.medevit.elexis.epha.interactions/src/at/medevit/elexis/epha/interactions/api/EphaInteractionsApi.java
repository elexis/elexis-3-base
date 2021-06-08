package at.medevit.elexis.epha.interactions.api;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.medevit.elexis.epha.interactions.api.model.Substance;

public class EphaInteractionsApi {
	
	private Client jaxrsClient;
	
	private Gson gson;
	
	public EphaInteractionsApi(){
		this.jaxrsClient = createJaxrsClient();
		
		this.gson = new GsonBuilder().create();
	}
	
	public synchronized Object advice(List<Substance> model){
		WebTarget target = jaxrsClient.target(getBaseUrl())
			.path("clinic/advice/" + Locale.getDefault().getLanguage() + "/");
		LoggerFactory.getLogger(getClass()).info("API target [" + target + "]");
		
		final Response response = target.request().post(Entity.json(gson.toJson(model)));
		
		if (response.getStatus() >= 300) {
			String message = "[" + response.getStatus() + "]\n" + response.readEntity(String.class);
			LoggerFactory.getLogger(getClass()).error(message);
			return message;
		} else {
			return response.readEntity(String.class);
		}
	}
	
	private String getBaseUrl(){
		return "https://api.epha.healthc/";
	}
	
	private Client createJaxrsClient(){
		try {
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, new TrustManager[] {
				new X509TrustManager() {
					public void checkClientTrusted(X509Certificate[] arg0, String arg1)
						throws CertificateException{}
					
					public void checkServerTrusted(X509Certificate[] arg0, String arg1)
						throws CertificateException{}
					
					public X509Certificate[] getAcceptedIssuers(){
						return new X509Certificate[0];
					}
					
				}
			}, new java.security.SecureRandom());
			return ClientBuilder.newBuilder().sslContext(sslcontext)
				.hostnameVerifier((s1, s2) -> true).withConfig(new ClientConfig()).build();
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).warn("Error creating jaxrs client", e);
		}
		return null;
	}
}
