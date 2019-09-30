package at.medevit.elexis.bluemedication.core.internal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;

import at.medevit.elexis.bluemedication.core.BlueMedicationConstants;
import at.medevit.elexis.bluemedication.core.BlueMedicationService;
import at.medevit.elexis.bluemedication.core.UploadResult;
import at.medevit.elexis.emediplan.core.EMediplanServiceHolder;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.data.Artikel;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Prescription;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.SEVERITY;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.ExtractionAndConsolidationApi;

@Component
public class BlueMedicationServiceImpl implements BlueMedicationService {
	
	private boolean proxyActive;
	private String oldProxyHost;
	private String oldProxyPort;
	
	private Map<Object, UploadResult> pendingUploadResults;
	
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
	public Result<UploadResult> uploadDocument(Patient patient, File document){
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
				boolean uploadedMediplan = false;
				File internalData = null;
				if (useRemoteImport() && hasPrescriptionsWithValidIdType(patient)) {
					Mandant mandant = ElexisEventDispatcher.getSelectedMandator();
					if (mandant != null) {
						try {
							ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();
							EMediplanServiceHolder.getService().exportEMediplanPdf(mandant, patient,
								getPrescriptions(patient, "all"), pdfOutput);
							File pdfFile = File
								.createTempFile("eMediplan_" + System.currentTimeMillis(), ".pdf");
							try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
								fos.write(pdfOutput.toByteArray());
								fos.flush();
							}
							internalData = pdfFile;
							uploadedMediplan = true;
						} catch (IOException e) {
							LoggerFactory.getLogger(getClass()).error("Error creating eMediplan",
								e);
							return new Result<UploadResult>(
								SEVERITY.ERROR, 0, e.getMessage(), null, false);
						}
					}
				}
				ApiResponse<?> response =
					apiInstance.dispatchPostWithHttpInfo(internalData, externalData,
						patientFirstName, patientLastName, patientSex, patientBirthdate,
						"", "", "", "", "");
				if (response.getStatusCode() >= 300) {
					if (response.getStatusCode() == 400 || response.getStatusCode() == 422) {
						// error result code should be evaluated
						@SuppressWarnings("unchecked")
						io.swagger.client.model.ErrorResult data =
							((ApiResponse<io.swagger.client.model.ErrorResult>) response).getData();
						return new Result<UploadResult>(SEVERITY.ERROR, 0,
							"Error result code [" + data.getCode() + "]", null, false);
					}
					return new Result<UploadResult>(SEVERITY.ERROR, 0,
						"Response status code was [" + response.getStatusCode() + "]", null, false);
				}
				if (response.getData() == null) {
					return new Result<UploadResult>(SEVERITY.ERROR,
						0, "Response has no data", null, false);
				}
				// successful upload
				@SuppressWarnings("unchecked")
				io.swagger.client.model.UploadResult data =
					((ApiResponse<io.swagger.client.model.UploadResult>) response).getData();
				return new Result<UploadResult>(new UploadResult(appendPath(getBasePath(),
					data.getUrl() + "&mode=embed"), data.getId(), uploadedMediplan));
			} catch (ApiException e) {
				LoggerFactory.getLogger(getClass()).error("Error uploading Document", e);
				return new Result<UploadResult>(SEVERITY.ERROR, 0,
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
			apiInstance.downloadIdComparisonChmedGet("workaround", false);
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
	
	private Result<String> getError(String text) {
		return new Result<String>(SEVERITY.ERROR, 0, text, text, false);
	}

	private Result<String> getOk(String text) {
		return new Result<String>(SEVERITY.OK, 0, text, text, false);
	}

	@Override
	public Result<String> downloadEMediplan(UploadResult uploadResult){
		initProxy();
		try {
			ExtractionAndConsolidationApi apiInstance = new ExtractionAndConsolidationApi();
			apiInstance.getApiClient().setBasePath(getAppBasePath());
			
			if (uploadResult.isUploadedMediplan()) {
				ApiResponse<String> response =
					apiInstance.downloadIdComparisonChmedGetWithHttpInfo(uploadResult.getId(),
						true);
				if (response.getStatusCode() >= 300) {
					return getError("Response status code was [" + response.getStatusCode() + "]");
				}
				if (response.getData() == null) {
					return getError("Response has no data");
				}
				return getOk(response.getData());
			} else {
				ApiResponse<String> response =
					apiInstance.downloadIdExtractionChmedGetWithHttpInfo(uploadResult.getId(),
						true);
				if (response.getStatusCode() >= 300) {
					return getError("Response status code was [" + response.getStatusCode() + "]");
				}
				if (response.getData() == null) {
					return getError("Response has no data");
				}
				return getOk(response.getData());
			}
		} catch (ApiException e) {
			LoggerFactory.getLogger(getClass()).error("Error downloading Document", e);
			return getError(e.getMessage());
		} finally {
			deInitProxy();
		}
	}
	
	@Override
	public void addPendingUploadResult(Object object,
		UploadResult uploadResult){
		pendingUploadResults.put(object, uploadResult);
	}
	
	@Override
	public Optional<UploadResult> getPendingUploadResult(
		Object object){
		return Optional.ofNullable(pendingUploadResults.get(object));
	}
	
	@Override
	public void removePendingUploadResult(Object object){
		pendingUploadResults.remove(object);
	}
	
	private boolean useRemoteImport(){
		return CoreHub.globalCfg.get(BlueMedicationConstants.CFG_USE_IMPORT, false);
	}
	
	private boolean hasPrescriptionsWithValidIdType(Patient patient){
		List<Prescription> allPrescriptions = getPrescriptions(patient, "all");
		List<Prescription> nonValidIdPrescriptions = allPrescriptions.stream()
			.filter(p -> getIdType(p.getArtikel()) == 1).collect(Collectors.toList());
		return nonValidIdPrescriptions.isEmpty();
	}
	
	private List<Prescription> getPrescriptions(Patient patient, String medicationType){
		if ("all".equals(medicationType)) {
			List<Prescription> ret = new ArrayList<Prescription>();
			ret.addAll(patient.getMedication(EntryType.FIXED_MEDICATION));
			ret.addAll(patient.getMedication(EntryType.RESERVE_MEDICATION));
			ret.addAll(patient.getMedication(EntryType.SYMPTOMATIC_MEDICATION));
			return ret;
		} else if ("fix".equals(medicationType)) {
			return patient.getMedication(EntryType.FIXED_MEDICATION);
		} else if ("reserve".equals(medicationType)) {
			return patient.getMedication(EntryType.RESERVE_MEDICATION);
		} else if ("symptomatic".equals(medicationType)) {
			return patient.getMedication(EntryType.SYMPTOMATIC_MEDICATION);
		}
		return Collections.emptyList();
	}
	
	/**
	 * Get the eMediplan id type for an Artikel. Must match method of
	 * <i>at.medevit.elexis.emediplan.core.model.chmed16a.Medicament</i>.
	 * 
	 * @param article
	 * @return
	 */
	private int getIdType(Artikel article){
		if (article != null) {
			String gtin = article.getEAN();
			if (gtin != null && !gtin.isEmpty() && gtin.startsWith("76")) {
				return 2;
			}
			String pharma = article.getPharmaCode();
			if (pharma == null || pharma.isEmpty()) {
				pharma = article.get(Artikel.FLD_SUB_ID);
			}
			if (pharma != null && !pharma.isEmpty()
				&& !pharma.startsWith(PersistentObject.MAPPING_ERROR_MARKER)) {
				return 3;
			}
		}
		return 1;
	}
}
