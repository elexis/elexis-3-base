package at.medevit.elexis.bluemedication.core.internal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;

import com.google.gson.Gson;
import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.elexis.bluemedication.core.BlueMedicationConstants;
import at.medevit.elexis.bluemedication.core.BlueMedicationService;
import at.medevit.elexis.bluemedication.core.UploadResult;
import at.medevit.elexis.emediplan.core.EMediplanServiceHolder;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.EntryType;
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
	public Result<UploadResult> uploadDocument(IPatient patient, File document){
		initProxy();
		workaroundGet();
		try {
			ExtractionAndConsolidationApi apiInstance = new ExtractionAndConsolidationApi();
			apiInstance.getApiClient().setBasePath(getAppBasePath());
			File externalData = document;
			String patientFirstName = patient.getFirstName();
			String patientLastName = patient.getLastName();
			String patientSex = patient.getGender().name();
			LocalDate patientBirthdate = LocalDate.now();
			try {
				boolean uploadedMediplan = false;
				File internalData = null;
				if (useRemoteImport() && hasPrescriptionsWithValidIdType(patient)) {
					IMandator mandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
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
				if (e.getCode() == 400 || e.getCode() == 422) {
					// error result code should be evaluated
					try {
						Gson gson = new Gson();
						io.swagger.client.model.ErrorResult data = gson.fromJson(
							e.getResponseBody(), io.swagger.client.model.ErrorResult.class);
						return new Result<UploadResult>(SEVERITY.ERROR, 0,
							"Error result code [" + data.getCode() + "]", null, false);
					} catch (Exception je) {
						LoggerFactory.getLogger(getClass())
							.warn("Could not parse code 400 exception content ["
								+ e.getResponseBody() + "]");
					}
				}
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
					return Result
						.ERROR("Response status code was [" + response.getStatusCode() + "]");
				}
				if (response.getData() == null) {
					return Result.ERROR("Response has no data");
				}
				return Result.OK(response.getData());
			} else {
				ApiResponse<String> response =
					apiInstance.downloadIdExtractionChmedGetWithHttpInfo(uploadResult.getId(),
						true);
				if (response.getStatusCode() >= 300) {
					return Result
						.ERROR("Response status code was [" + response.getStatusCode() + "]");
				}
				if (response.getData() == null) {
					return Result.ERROR("Response has no data");
				}
				return Result.OK(response.getData());
			}
		} catch (ApiException e) {
			LoggerFactory.getLogger(getClass()).error("Error downloading Document", e);
			return Result.ERROR(e.getMessage());
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
	
	private boolean hasPrescriptionsWithValidIdType(IPatient patient) {
		List<IPrescription> allPrescriptions = getPrescriptions(patient, "all");
		List<IPrescription> nonValidIdPrescriptions = allPrescriptions.stream()
				.filter(p -> getIdType(p.getArticle()) == 1).collect(Collectors.toList());
		return nonValidIdPrescriptions.isEmpty();
	}
	
	private List<IPrescription> getPrescriptions(IPatient patient, String medicationType){
		if ("all".equals(medicationType)) {
			return patient.getMedication(Arrays.asList(EntryType.FIXED_MEDICATION,
				EntryType.RESERVE_MEDICATION, EntryType.SYMPTOMATIC_MEDICATION));
		} else if ("fix".equals(medicationType)) {
			return patient.getMedication(Arrays.asList(EntryType.FIXED_MEDICATION));
		} else if ("reserve".equals(medicationType)) {
			return patient.getMedication(Arrays.asList(EntryType.RESERVE_MEDICATION));
		} else if ("symptomatic".equals(medicationType)) {
			return patient.getMedication(Arrays.asList(EntryType.SYMPTOMATIC_MEDICATION));
		}
		return Collections.emptyList();
	}
	
	/**
	 * Get the eMediplan id type for an IArticle. Must match method of
	 * <i>at.medevit.elexis.emediplan.core.model.chmed16a.Medicament</i>.
	 * 
	 * @param article
	 * @return
	 */
	private int getIdType(IArticle article){
		if (article != null) {
			String gtin = article.getGtin();
			if (gtin != null && !gtin.isEmpty() && gtin.startsWith("76")) {
				return 2;
			}
			String pharma = null;
			if (article instanceof IArtikelstammItem) {
				pharma = ((IArtikelstammItem) article).getPHAR();
			}
			if (StringUtils.isNotBlank(pharma)) {
				return 3;
			}
		}
		return 1;
	}
}
