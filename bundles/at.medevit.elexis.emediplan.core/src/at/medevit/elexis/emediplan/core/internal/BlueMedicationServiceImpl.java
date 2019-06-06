package at.medevit.elexis.emediplan.core.internal;

import java.io.File;
import java.util.Properties;

import org.osgi.service.component.annotations.Component;

import at.medevit.elexis.emediplan.core.BlueMedicationConstants;
import at.medevit.elexis.emediplan.core.BlueMedicationService;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Patient;
import ch.rgw.tools.Result;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.DefaultApi;

@Component
public class BlueMedicationServiceImpl implements BlueMedicationService {
	
	private boolean proxyActive;
	private String oldProxyHost;
	private String oldProxyPort;
	
	/**
	 * Set the HIN proxy as system property. <b>Remember to call deInitProxy</b>
	 */
	private void initProxy(){
		if (!proxyActive) {
			// get proxy settings and store old values
			Properties systemSettings = System.getProperties();
			oldProxyHost = systemSettings.getProperty("http.proxyHost"); //$NON-NLS-1$
			oldProxyPort = systemSettings.getProperty("http.proxyPort"); //$NON-NLS-1$
			
			// set new values
			systemSettings.put("http.proxyHost", CoreHub.globalCfg.get( //$NON-NLS-1$
				BlueMedicationConstants.CFG_HIN_PROXY_HOST,
				BlueMedicationConstants.DEFAULT_HIN_PROXY_HOST));
			systemSettings.put("http.proxyPort", CoreHub.globalCfg.get( //$NON-NLS-1$
				BlueMedicationConstants.CFG_HIN_PROXY_PORT,
				BlueMedicationConstants.DEFAULT_HIN_PROXY_PORT));
			System.setProperties(systemSettings);
			proxyActive = true;
		}
	}
	
	/**
	 * Reset the proxy values in the system properties.
	 */
	private void deInitProxy(){
		if (proxyActive) {
			Properties systemSettings = System.getProperties();
			if (oldProxyHost != null) {
				systemSettings.put("http.proxyHost", oldProxyHost); //$NON-NLS-1$
			}
			if (oldProxyPort != null) {
				systemSettings.put("http.proxyPort", oldProxyPort); //$NON-NLS-1$
			}
			System.setProperties(systemSettings);
			proxyActive = false;
		}
	}
	
	@Override
	public Result uploadDocument(Patient patient, File document){
		initProxy();
		try {
			DefaultApi apiInstance = new DefaultApi();
			apiInstance.getApiClient()
				.setBasePath("http://staging.blueconnect.hin.ch/bluemedication");
			File externalData = document; // byte[] | 
			String patientFirstName = patient.getVorname(); // String | 
			String patientLastName = patient.getName(); // String | 
			String patientSex = patient.getGender().name(); // String | 
			String patientBirthdate = ""; // String | 
			try {
				ApiResponse<Void> ret = apiInstance.dispatchPostWithHttpInfo(null, externalData,
					patientFirstName, patientLastName, patientSex, patientBirthdate);
				if (ret.getStatusCode() > 303) {
					return Result.ERROR("Response status code was [" + ret.getStatusCode() + "]");
				}
				return Result.OK();
			} catch (ApiException e) {
				System.err.println("Exception when calling DefaultApi#dispatchPost");
				e.printStackTrace();
				return Result.ERROR(e.getMessage());
			}
		} finally {
			deInitProxy();
		}
	}
	
	@Override
	public Result downloadEMediplan(String id){
		// TODO Auto-generated method stub
		return null;
	}
	
}
