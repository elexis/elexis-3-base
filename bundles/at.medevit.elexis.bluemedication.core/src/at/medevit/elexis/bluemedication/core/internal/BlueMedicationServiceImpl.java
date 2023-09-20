package at.medevit.elexis.bluemedication.core.internal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;
import org.openapitools.client.api.EMediplanGenerationApi;
import org.openapitools.client.api.ExtractionAndConsolidationApi;
import org.openapitools.client.api.MediCheckApi;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.elexis.bluemedication.core.BlueMedicationConstants;
import at.medevit.elexis.bluemedication.core.BlueMedicationService;
import at.medevit.elexis.bluemedication.core.UploadResult;
import at.medevit.elexis.emediplan.core.EMediplanService;
import at.medevit.elexis.hin.auth.core.IHinAuthService;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.SEVERITY;
import okhttp3.OkHttpClient;
import okhttp3.Response;

@Component(property = EventConstants.EVENT_TOPIC + "=" + ElexisEventTopics.BASE + "emediplan/ui/create")
public class BlueMedicationServiceImpl implements BlueMedicationService, EventHandler {

	private static Logger logger = LoggerFactory.getLogger(BlueMedicationServiceImpl.class);

	private boolean proxyActive;
	private String oldProxyHost;
	private String oldProxyPort;

	private Map<Object, UploadResult> pendingUploadResults;

	private ExecutorService executor;

	@Reference
	private EMediplanService eMediplanService;

	private Optional<IHinAuthService> hinAuthService;
	
	@Activate
	public void activate() {
		pendingUploadResults = new HashMap<>();
		executor = Executors.newCachedThreadPool();
	}

	/**
	 * Set the HIN proxy as system property. <b>Remember to call deInitProxy</b>
	 */
	private void initProxyOrOauth() {
		if(hinAuthService == null) {
			hinAuthService = OsgiServiceUtil.getService(IHinAuthService.class);
		}
	}

	@Override
	public Result<UploadResult> uploadDocument(IPatient patient, File document, String resulttyp) {
		initProxyOrOauth();
		workaroundGet();
			ExtractionAndConsolidationApi apiInstance = new ExtractionAndConsolidationApi();
			configureApiClient(apiInstance.getApiClient());
			
			File externalData = document;
			String patientFirstName = patient.getFirstName();
			String patientLastName = patient.getLastName();
			String patientSex = patient.getGender().name();
			LocalDate patientBirthdate = LocalDate.now();
			try {
				boolean uploadedMediplan = false;
				File internalData = null;
				if ("chmed".equals(resulttyp) && useRemoteImport() && hasPrescriptionsWithValidIdType(patient)) {
					IMandator mandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
					if (mandant != null) {
						try {
							ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();
							eMediplanService.exportEMediplanPdf(mandant, patient, getPrescriptions(patient, "all"),
									true, pdfOutput);
							File pdfFile = File.createTempFile("eMediplan_" + System.currentTimeMillis(), ".pdf");
							try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
								fos.write(pdfOutput.toByteArray());
								fos.flush();
							}
							internalData = pdfFile;
							uploadedMediplan = true;
						} catch (IOException e) {
							logger.error("Error creating eMediplan", e);
							return new Result<UploadResult>(SEVERITY.ERROR, 0, e.getMessage(), null, false);
						}
					}
				}
				ApiResponse<?> response = apiInstance.dispatchPostWithHttpInfo(externalData, internalData,
						patientFirstName, patientLastName, patientSex,
						DateTimeFormatter.ofPattern("dd.MM.yyyy").format(patientBirthdate), StringUtils.EMPTY,
						StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
				if (response.getStatusCode() >= 300) {
					return new Result<UploadResult>(SEVERITY.ERROR, 0,
							"Response status code was [" + response.getStatusCode() + "]", null, false);
				}
				if (response.getData() == null) {
					return new Result<UploadResult>(SEVERITY.ERROR, 0, "Response has no data", null, false);
				}
				// successful upload
				@SuppressWarnings("unchecked")
				org.openapitools.client.model.UploadResult data = ((ApiResponse<org.openapitools.client.model.UploadResult>) response)
						.getData();
				return new Result<UploadResult>(
						new UploadResult(appendPath(getBrowserBasePath(), data.getUrl() + "&mode=embed"), data.getId(),
								resulttyp, uploadedMediplan));
			} catch (ApiException e) {
				if (e.getCode() == 400 || e.getCode() == 422) {
					// error result code should be evaluated
					try {
						Gson gson = new Gson();
						org.openapitools.client.model.ErrorResult[] mcArray = gson.fromJson(e.getResponseBody(),
								org.openapitools.client.model.ErrorResult[].class);
						if (mcArray != null && mcArray.length > 0) {
							return new Result<UploadResult>(SEVERITY.ERROR, 0,
									"Error result code [" + mcArray[0].getCode() + "]", null, false);
						}
					} catch (Exception je) {
						logger.warn("Could not parse code 400 exception content [" + e.getResponseBody() + "]");
					}
				}
				logger.error("Error uploading Document", e);
				return new Result<UploadResult>(SEVERITY.ERROR, 0, e.getMessage(), null, false);
			}
	}

	private void configureApiClient(ApiClient client) {
		client.setBasePath(getAppBasePath());
		if(hinAuthService.isPresent()) {
			Optional<String> authToken = hinAuthService.get()
					.getToken(Collections.singletonMap(IHinAuthService.TOKEN_GROUP, getTokenGroup()));
			if (authToken.isPresent()) {
				client.addDefaultHeader("Authorization" , "Bearer " + authToken.get());
			}
		}
	}
	
	private String getTokenGroup() {
		if (ConfigServiceHolder.getGlobal(BlueMedicationConstants.CFG_URL_STAGING, false)) {
			return "bluecare_bluemedication_staging";
		} else {
			return "bluecare_bluemedication";
		}
	}
	
	private class CheckApiClient extends ApiClient {

		private String redirectUrl;

		public CheckApiClient() {
			super(new OkHttpClient.Builder().followRedirects(false).followSslRedirects(false).build());
		}

		public String selectHeaderContentType(String[] contentTypes) {
			return "application/x-chmed16a";
		};

		@Override
		public <T> T handleResponse(Response response, Type returnType) throws ApiException {
			if (response.code() == 302) {
				redirectUrl = response.header("Location", null);
				return null;
			}
			throw new ApiException(response.message(), response.code(), response.headers().toMultimap(), null);
		}

		public String getRedirectUrl() {
			return redirectUrl;
		}
	}

	@Override
	public Result<UploadResult> uploadCheck(IPatient patient) {
		initProxyOrOauth();
		workaroundGet();
		try {
			CheckApiClient client = new CheckApiClient();
			configureApiClient(client);
			MediCheckApi apiInstance = new MediCheckApi(client);
			
			IMandator mandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
			if (mandant != null) {
				try {
					File tmpFile = File.createTempFile("bluemedication" + System.currentTimeMillis(), ".tmp");
					try (FileOutputStream fout = new FileOutputStream(tmpFile)) {
						eMediplanService.exportEMediplanChmed(mandant, patient, getPrescriptions(patient, "all"), true,
								fout);
					}
					apiInstance.checkPostWithHttpInfo(tmpFile);
					if (client.getRedirectUrl() != null) {
						return new Result<UploadResult>(
								new UploadResult(client.getRedirectUrl(), StringUtils.EMPTY, "check", true));
					} else {
						return new Result<UploadResult>(SEVERITY.ERROR, 0, "No redirect", null, false);
					}
				} catch (IOException e) {
					logger.error("Error creating eMediplan", e);
					return new Result<UploadResult>(SEVERITY.ERROR, 0, e.getMessage(), null, false);
				}
			} else {
				return new Result<UploadResult>(SEVERITY.ERROR, 0, "No active mandator", null, false);
			}
		} catch (ApiException e) {
			if (e.getCode() == 400 || e.getCode() == 422) {
				// error result code should be evaluated
				try {
					Gson gson = new Gson();
					org.openapitools.client.model.ErrorResult[] mcArray = gson.fromJson(e.getResponseBody(),
							org.openapitools.client.model.ErrorResult[].class);
					if (mcArray != null && mcArray.length > 0) {
						return new Result<UploadResult>(SEVERITY.ERROR, 0,
								"Error result code [" + mcArray[0].getCode() + "]", null, false);
					}
				} catch (Exception je) {
					logger.warn("Could not parse code 400 exception content [" + e.getResponseBody() + "]");
				}
			}
			logger.error("Error uploading Document", e);
			return new Result<UploadResult>(SEVERITY.ERROR, 0, e.getMessage(), null, false);
		}
	}

	@Override
	public Result<String> emediplanNotification(IPatient patient) {
		initProxyOrOauth();
		workaroundGet();
		try {
			EMediplanGenerationApi apiInstance = new EMediplanGenerationApi();
			configureApiClient(apiInstance.getApiClient());

			LocalDateTime patBirthDay = patient.getDateOfBirth();
			LocalDate birthDate = (patBirthDay != null
					? LocalDate.of(patBirthDay.getYear(), patBirthDay.getMonthValue(), patBirthDay.getDayOfMonth())
					: null);

			ApiResponse<?> response = apiInstance.notificationEmediplanPostWithHttpInfo(patient.getFirstName(),
					patient.getLastName(), patient.getGender().name(),
					DateTimeFormatter.ofPattern("dd.MM.yyyy").format(birthDate));
			return Result.OK(response.toString());
		} catch (ApiException e) {
			if (e.getCode() == 400 || e.getCode() == 422) {
				// error result code should be evaluated
				try {
					Gson gson = new Gson();
					org.openapitools.client.model.ErrorResult[] mcArray = gson.fromJson(e.getResponseBody(),
							org.openapitools.client.model.ErrorResult[].class);
					if (mcArray != null && mcArray.length > 0) {
						return new Result<String>(SEVERITY.ERROR, 0, "Error result code [" + mcArray[0].getCode() + "]",
								null, false);
					}
				} catch (Exception je) {
					logger.warn("Could not parse code 400 exception content [" + e.getResponseBody() + "]");
				}
			}
			logger.error("Error performing notification", e);
			return new Result<String>(SEVERITY.ERROR, 0, e.getMessage(), null, false);
		}
	}

	/**
	 * Perform a workaround get until HIN fixed POST issue
	 *
	 */
	private void workaroundGet() {
		try {
			ExtractionAndConsolidationApi apiInstance = new ExtractionAndConsolidationApi();
			apiInstance.getApiClient().setBasePath(getAppBasePath());
			configureApiClient(apiInstance.getApiClient());

			logger.warn("Performing workaround GET request");
			apiInstance.downloadIdComparisonChmedGet("workaround", false);
		} catch (Exception e) {
			// ignore
		}
	}

	private String appendPath(String pathStart, String pathEnd) {
		if (pathStart.endsWith("/") || pathEnd.startsWith("/")) {
			return pathStart + pathEnd;
		} else if (pathStart.endsWith("/") && pathEnd.startsWith("/")) {
			return pathStart + pathEnd.substring(1);
		} else {
			return pathStart + "/" + pathEnd;
		}
	}

	private String getBasePath() {
		if (hinAuthService.isPresent()) {
			if (ConfigServiceHolder.getGlobal(BlueMedicationConstants.CFG_URL_STAGING, false)) {
				return "https://oauth2.staging.bluemedication.hin.ch";
			} else {
				return "https://oauth2.bluemedication.hin.ch";
			}
		} else {
			if (ConfigServiceHolder.getGlobal(BlueMedicationConstants.CFG_URL_STAGING, false)) {
				return "http://staging.bluemedication.hin.ch";
			} else {
				return "http://bluemedication.hin.ch";
			}
		}
	}

	private String getBrowserBasePath() {
		if (ConfigServiceHolder.getGlobal(BlueMedicationConstants.CFG_URL_STAGING, false)) {
			return "http://staging.bluemedication.hin.ch";
		} else {
			return "http://bluemedication.hin.ch";
		}
	}

	private String getAppBasePath() {
		return appendPath(getBasePath(), "/api/v1");
	}

	@Override
	public Result<String> downloadEMediplan(UploadResult uploadResult) {
		initProxyOrOauth();
		try {
			ExtractionAndConsolidationApi apiInstance = new ExtractionAndConsolidationApi();
			configureApiClient(apiInstance.getApiClient());
			if (uploadResult.isUploadedMediplan()) {
				ApiResponse<String> response = apiInstance
						.downloadIdComparisonChmedGetWithHttpInfo(uploadResult.getId(), true);
				if (response.getStatusCode() >= 300) {
					return Result.ERROR("Response status code was [" + response.getStatusCode() + "]");
				}
				if (response.getData() == null) {
					return Result.ERROR("Response has no data");
				}
				return Result.OK(response.getData());
			} else {
				ApiResponse<String> response = apiInstance
						.downloadIdExtractionChmedGetWithHttpInfo(uploadResult.getId(), true);
				if (response.getStatusCode() >= 300) {
					return Result.ERROR("Response status code was [" + response.getStatusCode() + "]");
				}
				if (response.getData() == null) {
					return Result.ERROR("Response has no data");
				}
				return Result.OK(response.getData());
			}
		} catch (ApiException e) {
			logger.error("Error downloading Document", e);
			return Result.ERROR(e.getMessage());
		}
	}

	@Override
	public Result<String> downloadPdf(UploadResult uploadResult) {
		initProxyOrOauth();
		try {
			ExtractionAndConsolidationApi apiInstance = new ExtractionAndConsolidationApi();
			configureApiClient(apiInstance.getApiClient());
			ApiResponse<File> response = apiInstance.downloadIdExtractionGetWithHttpInfo(uploadResult.getId(), true);
			if (response.getStatusCode() >= 300) {
				return Result.ERROR("Response status code was [" + response.getStatusCode() + "]");
			}
			if (response.getData() == null) {
				return Result.ERROR("Response has no data");
			}
			return Result.OK(response.getData().getAbsolutePath());
		} catch (ApiException e) {
			logger.error("Error downloading Document Pdf", e);
			return Result.ERROR(e.getMessage());
		}
	}

	@Override
	public void addPendingUploadResult(Object object, UploadResult uploadResult) {
		pendingUploadResults.put(object, uploadResult);
	}

	@Override
	public Optional<UploadResult> getPendingUploadResult(Object object) {
		return Optional.ofNullable(pendingUploadResults.get(object));
	}

	@Override
	public void removePendingUploadResult(Object object) {
		pendingUploadResults.remove(object);
	}

	private boolean useRemoteImport() {
		return ConfigServiceHolder.getGlobal(BlueMedicationConstants.CFG_USE_IMPORT, false);
	}

	private boolean hasPrescriptionsWithValidIdType(IPatient patient) {
		List<IPrescription> allPrescriptions = getPrescriptions(patient, "all");
		List<IPrescription> nonValidIdPrescriptions = allPrescriptions.stream()
				.filter(p -> getIdType(p.getArticle()) == 1).collect(Collectors.toList());
		return nonValidIdPrescriptions.isEmpty();
	}

	private List<IPrescription> getPrescriptions(IPatient patient, String medicationType) {
		if ("all".equals(medicationType)) {
			return patient.getMedication(Arrays.asList(EntryType.FIXED_MEDICATION, EntryType.RESERVE_MEDICATION,
					EntryType.SYMPTOMATIC_MEDICATION));
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
	private int getIdType(IArticle article) {
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

	@Override
	public void startPollForResult(final Object object, final UploadResult uploadResult, Consumer<Object> onSuccess) {
		executor.execute(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// ignore
			}
			logger.info("Start polling for [" + uploadResult.getId() + "]");
			// configure HIN proxy for apache http client
//			HttpHost proxy = new HttpHost(
//					ConfigServiceHolder.get().getLocal(BlueMedicationConstants.CFG_HIN_PROXY_HOST,
//							BlueMedicationConstants.DEFAULT_HIN_PROXY_HOST),
//					Integer.parseInt(ConfigServiceHolder.get().getLocal(BlueMedicationConstants.CFG_HIN_PROXY_PORT,
//							BlueMedicationConstants.DEFAULT_HIN_PROXY_PORT)),
//					"http");
//			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
			Collection<BasicHeader> headers = new ArrayList<>();
			if (hinAuthService.isPresent()) {
				Optional<String> authToken = hinAuthService.get()
						.getToken(Collections.singletonMap(IHinAuthService.TOKEN_GROUP, "BlueMedication"));
				if (authToken.isPresent()) {
					headers.add(new BasicHeader("Authorization", "Bearer " + authToken.get()));
				}
			}
			HttpClient httpclient = HttpClients.custom().setDefaultHeaders(headers).build();

			HttpGet httpget = new HttpGet(getAppBasePath() + "/status/" + uploadResult.getId());
			int maxRetry = 30; // default timeout 30 sec -> 15 min.
			int statusCode = 204;
			String content = null;
			try {
				while (statusCode == 204 && maxRetry > 0) {
					HttpResponse response = httpclient.execute(httpget);
					statusCode = response.getStatusLine().getStatusCode();
					if (response.getEntity() != null) {
						content = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
					}
					maxRetry--;
				}
			} catch (IOException e) {
				logger.error("Error performing polling for [" + uploadResult.getId() + "]", e);
				return;
			}
			if (statusCode == 200) {
				if (content != null && content.contains("COMPLETED")) {
					logger.info("Finished [" + uploadResult.getId() + "] completed");
					onSuccess.accept(object);
				} else {
					logger.info("Finished [" + uploadResult.getId() + "] not completed");
					removePendingUploadResult(object);
				}
			} else {
				logger.warn("Got response code [" + statusCode + "] for [" + uploadResult.getId()
						+ "] clearing pending upload");
				removePendingUploadResult(object);
			}
		});
	}

	@Override
	public void handleEvent(Event event) {
		Object property = event.getProperty("org.eclipse.e4.data");
		if (property instanceof IDocument) {
			IPatient patient = ((IDocument) property).getPatient();
			if (patient != null) {
				emediplanNotification(patient);
			}
		}
	}
}
