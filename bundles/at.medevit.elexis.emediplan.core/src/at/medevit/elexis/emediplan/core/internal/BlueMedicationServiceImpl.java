package at.medevit.elexis.emediplan.core.internal;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;

import at.medevit.elexis.emediplan.core.BlueMedicationConstants;
import at.medevit.elexis.emediplan.core.BlueMedicationService;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Patient;
import ch.rgw.tools.Result;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.ExtractionAndConsolidationApi;
import io.swagger.client.model.UploadResult;

@Component
public class BlueMedicationServiceImpl implements BlueMedicationService {
	
	private boolean proxyActive;
	private String oldProxyHost;
	private String oldProxyPort;
	
	private Map<LocalDateTime, UploadResult> pendingUploadResults;
	
	@Activate
	public void activate() {
		pendingUploadResults = new HashMap<>();
	}
	
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
	public Result<String> uploadDocument(Patient patient, File document){
		initProxy();
		try {
			ExtractionAndConsolidationApi apiInstance = new ExtractionAndConsolidationApi();
			apiInstance.getApiClient().setBasePath(getBasePath());
			File externalData = document;
			String patientFirstName = patient.getVorname();
			String patientLastName = patient.getName();
			String patientSex = patient.getGender().name();
			LocalDate patientBirthdate = LocalDate.now();
			try {
				ApiResponse<UploadResult> ret = apiInstance.dispatchPostWithHttpInfo((File) null,
					externalData, patientFirstName, patientLastName, patientSex, patientBirthdate);
				if (ret.getStatusCode() >= 300) {
					return Result.ERROR("Response status code was [" + ret.getStatusCode() + "]");
				}
				if (ret.getData() == null) {
					return Result.ERROR("Response has no data");
				}
				pendingUploadResults.put(LocalDateTime.now(), ret.getData());
				return Result.OK(getAppBasePath() + ret.getData().getUrl());
			} catch (ApiException e) {
				LoggerFactory.getLogger(getClass()).error("Error uploading Document", e);
				return Result.ERROR(e.getMessage());
			}
		} finally {
			deInitProxy();
		}
	}
	
	private String getBasePath(){
		if (CoreHub.globalCfg.get(BlueMedicationConstants.CFG_URL_STAGING, true)) {
			return "http://staging.blueconnect.hin.ch/bluemedication/api/v1";
		} else {
			return "http://blueconnect.hin.ch/bluemedication/api/v1";
		}
	}
	
	private String getAppBasePath(){
		if (CoreHub.globalCfg.get(BlueMedicationConstants.CFG_URL_STAGING, true)) {
			return "http://staging.blueconnect.hin.ch";
		} else {
			return "http://blueconnect.hin.ch";
		}
	}
	
	@Override
	public Result<String> downloadEMediplan(String id){
		// TODO Auto-generated method stub
		return null;
	}
	
}
