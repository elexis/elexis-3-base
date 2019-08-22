package at.medevit.elexis.emediplan.core.internal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
import ch.rgw.tools.Result.SEVERITY;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.ExtractionAndConsolidationApi;
import io.swagger.client.model.UploadResult;

@Component
public class BlueMedicationServiceImpl implements BlueMedicationService {
	
	private boolean proxyActive;
	private String oldProxyHost;
	private String oldProxyPort;
	
	private Map<Object, at.medevit.elexis.emediplan.core.UploadResult> pendingUploadResults;
	
	@Activate
	public void activate(){
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
	public Result<at.medevit.elexis.emediplan.core.UploadResult> uploadDocument(Patient patient,
		File document){
		initProxy();
		workaroundGet();
		try {
			ExtractionAndConsolidationApi apiInstance = new ExtractionAndConsolidationApi();
			apiInstance.getApiClient().setBasePath(getAppBasePath());
			File externalData = document;
			String patientFirstName = patient.getVorname();
			String patientLastName = patient.getName();
			String patientSex = patient.getGender().name();
			LocalDate patientBirthdate = LocalDate.now();
			try {
				ApiResponse<UploadResult> response =
					apiInstance.dispatchPostWithHttpInfo((File) null, externalData,
						patientFirstName, patientLastName, patientSex, patientBirthdate);
				if (response.getStatusCode() >= 300) {
					return new Result<at.medevit.elexis.emediplan.core.UploadResult>(SEVERITY.ERROR,
						0, "Response status code was [" + response.getStatusCode() + "]", null,
						false);
				}
				if (response.getData() == null) {
					return new Result<at.medevit.elexis.emediplan.core.UploadResult>(SEVERITY.ERROR,
						0, "Response has no data", null, false);
				}
				return new Result<at.medevit.elexis.emediplan.core.UploadResult>(
					new at.medevit.elexis.emediplan.core.UploadResult(
						appendPath(getBasePath(), response.getData().getUrl() + "&mode=embed"),
						response.getData().getId()));
			} catch (ApiException e) {
				LoggerFactory.getLogger(getClass()).error("Error uploading Document", e);
				return new Result<at.medevit.elexis.emediplan.core.UploadResult>(SEVERITY.ERROR, 0,
					e.getMessage(), null, false);
			}
		} finally {
			deInitProxy();
		}
	}
	
	/**
	 * Perform a workaround get until HIN fixed POST issue
	 * 
	 */
	private void workaroundGet(){
		try {
			ExtractionAndConsolidationApi apiInstance = new ExtractionAndConsolidationApi();
			apiInstance.getApiClient().setBasePath(getAppBasePath());
			
			LoggerFactory.getLogger(getClass()).warn("Performing workaround GET request");
			apiInstance.comparisonIdGet("workaround");
		} catch (Exception e) {
			// ignore
		}
	}
	
	private String appendPath(String pathStart, String pathEnd){
		if (pathStart.endsWith("/") || pathEnd.startsWith("/")) {
			return pathStart + pathEnd;
		} else if (pathStart.endsWith("/") && pathEnd.startsWith("/")) {
			return pathStart + pathEnd.substring(1);
		} else {
			return pathStart + "/" + pathEnd;
		}
	}
	
	private String getBasePath(){
		if (CoreHub.globalCfg.get(BlueMedicationConstants.CFG_URL_STAGING, false)) {
			return "http://staging.bluemedication.hin.ch";
		} else {
			return "http://bluemedication.hin.ch";
		}
	}
	
	private String getAppBasePath(){
		return appendPath(getBasePath(), "/api/v1");
	}
	
	@Override
	public Result<String> downloadEMediplan(String id){
		initProxy();
		try {
			ExtractionAndConsolidationApi apiInstance = new ExtractionAndConsolidationApi();
			apiInstance.getApiClient().setBasePath(getAppBasePath());
			
			ApiResponse<String> response =
				apiInstance.downloadIdExtractionChmedGetWithHttpInfo(id, true);
			if (response.getStatusCode() >= 300) {
				return Result.ERROR("Response status code was [" + response.getStatusCode() + "]");
			}
			if (response.getData() == null) {
				return Result.ERROR("Response has no data");
			}
			return Result.OK(response.getData());
		} catch (ApiException e) {
			LoggerFactory.getLogger(getClass()).error("Error downloading Document", e);
			return Result.ERROR(e.getMessage());
		} finally {
			deInitProxy();
		}
	}
	
	@Override
	public void addPendingUploadResult(Object object,
		at.medevit.elexis.emediplan.core.UploadResult uploadResult){
		pendingUploadResults.put(object, uploadResult);
	}
	
	@Override
	public Optional<at.medevit.elexis.emediplan.core.UploadResult> getPendingUploadResult(
		Object object){
		return Optional.ofNullable(pendingUploadResults.get(object));
	}
	
	@Override
	public void removePendingUploadResult(Object object){
		pendingUploadResults.remove(object);
	}
}
