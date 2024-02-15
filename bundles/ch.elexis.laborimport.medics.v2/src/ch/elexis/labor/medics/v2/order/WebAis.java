package ch.elexis.labor.medics.v2.order;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;

public class WebAis {

	private static String TESTURL = "https://test-order.medics.ch";
	private static String TESTAPITOKEN = "3YVRHY2AABF2GJTFBT2YY4KLHKTRJCSGXBGXGCIQRMZGCVWIWJSMIQYIHSWKIX4V";

	private static String PRODURL = "https://order.medics.ch";
	private static String PRODAPITOKEN = "JFG3ZSVLWVYZJ2N6U4ZT5P7ZDPJUQ4VC75UOMJKBTE5OGBYNKSFJK3CWAIW5KRQZ";

	private static String LOGIN = "/channel/authenticateExternal";
	private static String CREATEPATIENTANDORDER = "/channel/orderManagement/createPatientAndOrderExternal";

	private static final String CFG_MEDICS_BASE = "Medics_LaborOrder/"; //$NON-NLS-1$
	public static final String CFG_MEDICS_LABORDER_CUSTOMER = CFG_MEDICS_BASE + "customer"; //$NON-NLS-1$
	public static final String CFG_MEDICS_LABORDER_USERNAME = CFG_MEDICS_BASE + "username"; //$NON-NLS-1$
	public static final String CFG_MEDICS_LABORDER_PASSWORD = CFG_MEDICS_BASE + "password"; //$NON-NLS-1$
	public static final String CFG_MEDICS_LABORDER_REQUESTER = CFG_MEDICS_BASE + "requester"; //$NON-NLS-1$

	public static final String CFG_MEDICS_LABORDER_TESTMODE = CFG_MEDICS_BASE + "testmode"; //$NON-NLS-1$
	
	private Client jaxrsClient;

	private Gson gson;

	private Gson getGson() {
		if (gson == null) {
			gson = new GsonBuilder().create();
		}
		return gson;
	}

	private Client getClient() {
		if (jaxrsClient == null) {
			jaxrsClient = createJaxrsClient();
		}
		return jaxrsClient;
	}

	private String getBaseUrl() {
		return ConfigServiceHolder.get().get(CFG_MEDICS_LABORDER_TESTMODE, false) ? TESTURL : PRODURL;
	}

	private String getApiToken() {
		return ConfigServiceHolder.get().get(CFG_MEDICS_LABORDER_TESTMODE, false) ? TESTAPITOKEN : PRODAPITOKEN;
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

	private String login(IMandator mandator) {
		WebTarget target = getClient().target(getBaseUrl()).path(LOGIN);

		if (hasCredentials(mandator)) {
			String jsonString = getGson().toJson(getCredentials(mandator));

			final Response response = target.request(MediaType.APPLICATION_JSON).header("APIToken", getApiToken())
					.post(Entity.json(jsonString));

			if (response.getStatus() >= 300) {
				String message = target.toString() + " -> response status [" + response.getStatus() + "]\n" //$NON-NLS-1$ //$NON-NLS-2$
						+ response.readEntity(String.class);
				LoggerFactory.getLogger(getClass()).error(message);
				throw new IllegalStateException("Login fehlgeschlagen");
			} else {
				@SuppressWarnings("unchecked")
				Map<String, Object> responseMap = response.readEntity(Map.class);
				if (responseMap.get("isOK") instanceof Boolean && ((Boolean) responseMap.get("isOK"))) {
					return (String) responseMap.get("oneTimeToken");
				} else {
					return responseMap.get("errorMessage") + "\nCode: " + responseMap.get("errorCode");
				}
			}
		} else {
			throw new IllegalStateException(
					"Mandant hat keinen Konfiguration für den Zugriff.\nBitte in den Einstellungen konfigurieren.");
		}
	}

	public static boolean hasCredentials(IMandator mandator) {
		return StringUtils.isNotBlank(ConfigServiceHolder.get().get(mandator, CFG_MEDICS_LABORDER_USERNAME, null))
				&& StringUtils.isNotBlank(ConfigServiceHolder.get().get(mandator, CFG_MEDICS_LABORDER_PASSWORD, null))
				&& StringUtils.isNotBlank(ConfigServiceHolder.get().get(mandator, CFG_MEDICS_LABORDER_CUSTOMER, null));
	}

	private Map<String, String> getCredentials(IMandator mandator) {
		Map<String, String> ret = new HashMap<>();
		ret.put("username", ConfigServiceHolder.get().get(mandator, CFG_MEDICS_LABORDER_USERNAME, null));
		ret.put("password", ConfigServiceHolder.get().get(mandator, CFG_MEDICS_LABORDER_PASSWORD, null));
		ret.put("customer", ConfigServiceHolder.get().get(mandator, CFG_MEDICS_LABORDER_CUSTOMER, null));
		return ret;
	}

	private String getRequesterKey(IMandator mandator) {
		return ConfigServiceHolder.get().get(mandator, CFG_MEDICS_LABORDER_REQUESTER, null);
	}

	public String createPatientAndOrder(IPatient iPatient) {
		Optional<IMandator> mandator = ContextServiceHolder.get().getActiveMandator();
		if (mandator.isPresent()) {
			try {
				String oneTimeToken = login(mandator.get());
				if (StringUtils.isNotBlank(oneTimeToken)) {
					WebTarget target = getClient().target(getBaseUrl()).path(CREATEPATIENTANDORDER);

					Map<String, Object> postContentMap = new HashMap<>();
					postContentMap.put("patientData",
							PatientData.of(iPatient).withRequesterKey(getRequesterKey(mandator.get())));
					postContentMap.put("oneTimeToken", oneTimeToken);

					String jsonString = getGson().toJson(postContentMap);

					final Response response = target.request(MediaType.APPLICATION_JSON)
							.header("APIToken", getApiToken()).header("OneTimeToken", oneTimeToken)
							.post(Entity.json(jsonString));

					if (response.getStatus() >= 300) {
						String message = target.toString() + " -> response status [" + response.getStatus() + "]\n" //$NON-NLS-1$ //$NON-NLS-2$
								+ response.readEntity(String.class);
						LoggerFactory.getLogger(getClass()).error(message);
						throw new IllegalStateException("Login fehlgeschlagen");
					} else {
						@SuppressWarnings("unchecked")
						Map<String, Object> responseMap = response.readEntity(Map.class);
						if (responseMap.get("url") instanceof String
								&& StringUtils.isNotBlank((String) responseMap.get("url"))) {
							return (String) getBaseUrl() + "/" + responseMap.get("url");
						} else {
							return (String) responseMap.get("errorMessage");
						}
					}
				}
			} catch (Exception e) {
				return e.getMessage();
			}
		} else {
			return "Kein aktiver Mandant";
		}
		return null;
	}
}
