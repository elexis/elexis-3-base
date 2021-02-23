package at.medevit.elexis.kapsch.referral.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.kapsch.referral.KapschReferralService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.data.Patient;

@Component(service = KapschReferralService.class)
public class KapschReferralServiceImpl implements KapschReferralService {
	
	@Override
	public Optional<String> sendPatient(Patient patient){
		
        URL url = getUrl();
		Map<String, Object> params = getPatientParameterMap(patient);
		if (params != null) {
			byte[] postDataBytes = getMapAsPostData(params);
			if (postDataBytes != null) {
				try {
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
					conn.setDoOutput(true);
					conn.getOutputStream().write(postDataBytes);
					
					BufferedReader in =
						new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String inputLine;
					
					while ((inputLine = in.readLine()) != null)
						System.out.println(inputLine);
					in.close();
				} catch (IOException e) {
					LoggerFactory.getLogger(getClass()).error("Error sending post data", e);
				}
			}
		}
		return Optional.empty();
	}
	
	private byte[] getMapAsPostData(Map<String, Object> params){
		try {
			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			return postData.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			LoggerFactory.getLogger(getClass()).error("Error getting post data", e);
			return null;
		}
	}

	private Map<String, Object> getPatientParameterMap(Patient patient){
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("PID", patient.getPatCode());
		params.put("Lastname", patient.getName());
		params.put("Firstname", patient.getVorname());
		params.put("Street", patient.getAnschrift().getStrasse());
		params.put("PostalCode", patient.getAnschrift().getPlz());
		params.put("City", patient.getAnschrift().getOrt());
		params.put("Birthday", patient.getGeburtsdatum());
		params.put("Sex", patient.getGeschlecht());
		
		return params;
	}
	
	private URL getUrl(){
		try {
			String configEndpoint = ConfigServiceHolder.getMandator(
				KapschReferralService.CONFIG_ENDPOINT, KapschReferralService.ENDPOINT_TEST);
			if (KapschReferralService.ENDPOINT_PRODUCTIV.equals(configEndpoint)) {
				return new URL("https://referral.kapsch.health/webapp");
			}
			return new URL("https://referral-test.kapsch.health/webapp");
		} catch (MalformedURLException e) {
			LoggerFactory.getLogger(getClass()).error("Error getting url", e);
			return null;
		}
	}
}
