package ch.elexis.mednet.webapi.ui.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import ch.elexis.core.findings.IFindingsService;
import ch.elexis.core.findings.util.ModelUtil;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.mednet.webapi.core.IMednetAuthUi;
import ch.elexis.mednet.webapi.core.constants.ApiConstants;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;
import ch.elexis.mednet.webapi.core.fhir.resources.BundleResource;
import ch.elexis.mednet.webapi.core.fhir.resources.PatientResource;
import ch.elexis.mednet.webapi.core.fhir.resources.util.AdjustBundleIdentifiers;
import ch.elexis.mednet.webapi.core.fhir.resources.util.FhirResourceFactory;
import jakarta.inject.Inject;

public class PatientFetcher {

	@Inject
	private IMednetAuthUi authUi;

	@Inject
	private FhirResourceFactory resourceFactory;
	
	@Inject
	private IFindingsService findingsService;

	private IModelService coreModelService;

	private static final Logger logger = LoggerFactory.getLogger(PatientFetcher.class);
	private String token;
	private static final Gson gson = new Gson();
	public static final String DEBUG_MODE = "mednet.fhir.debug"; //$NON-NLS-1$


	public void setToken(String token) {
		this.token = token;
	}

	public PatientFetcher(String token) {
		this.token = token;

		CoreUiUtil.injectServices(this);
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		ServiceReference<IMednetAuthUi> serviceReference = context.getServiceReference(IMednetAuthUi.class);
		authUi = context.getService(serviceReference);
		this.coreModelService = OsgiServiceUtil
				.getService(IModelService.class, "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)").get(); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String fetchCustomerId() {
		String response = StringUtils.EMPTY;
		try {
			response = Request.Get(ApiConstants.getBaseApiUrl() + ApiConstants.CUSTOMERS_URL)
					.addHeader("Authorization", "Bearer " + token).execute() //$NON-NLS-1$ //$NON-NLS-2$
					.returnContent()
					.asString();
		} catch (IOException e) {
			logger.error("Error fetching customer ID: ", e);
		}
		return response;
	}

	public String fetchProvidersId(int customerID) {
		String apiUrl = String.format(ApiConstants.getBaseApiUrl() + ApiConstants.PROVIDERS_URL, customerID);
		String response = StringUtils.EMPTY;
		try {
			response = Request.Get(apiUrl).addHeader("Authorization", "Bearer " + token).execute().returnContent() //$NON-NLS-1$ //$NON-NLS-2$
					.asString();
		} catch (IOException e) {
			logger.error("Error fetching provider ID: ", e);
		}
		return response;
	}

	public String fetchFormsByProviderIdWithRetry(Integer customerId, Integer providerId) {
		String apiUrl = String.format(ApiConstants.getBaseApiUrl() + ApiConstants.FORMS_URL, customerId, providerId);
		String response = StringUtils.EMPTY;
		int retryCount = 0;
		boolean success = false;

		while (!success && retryCount < 5) {
			try {
				response = Request.Get(apiUrl).addHeader("Authorization", "Bearer " + token).execute().returnContent() //$NON-NLS-1$ //$NON-NLS-2$
						.asString();
				success = true;

				if (!isValidJsonArray(response)) {
					if (isValidJsonObject(response)) {
						JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
						return gson.toJson(jsonResponse);
					} else {
						throw new JsonSyntaxException("Expected a JSON array but got: " + response);
					}
				}
			} catch (HttpResponseException e) {
				if (e.getStatusCode() == 429) {
					retryCount++;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						logger.error("Thread interrupted during wait: ", ie);
					}
				}
			} catch (JsonSyntaxException | IOException e) {
				break;
			}
		}
		return response;
	}

	private boolean isValidJsonObject(String response) {
		try {
			JsonParser.parseString(response).getAsJsonObject();
			return true;
		} catch (JsonSyntaxException e) {
			return false;
		}
	}

	private boolean isValidJsonArray(String response) {
		try {
			if (response == null || response.isEmpty() || !response.trim().startsWith("[")) { //$NON-NLS-1$
				return false;
			}
			JsonParser.parseString(response).getAsJsonArray();
			return true;
		} catch (JsonSyntaxException e) {
			return false;
		}
	}

	public String fetchSubmitFormsId(int customerID) {
		String apiUrl = String.format(ApiConstants.getBaseApiUrl() + ApiConstants.SUBMITTED_FORMS_URL, customerID);
		String response = StringUtils.EMPTY;

		try {
			var requestResponse = Request.Get(apiUrl).addHeader("Authorization", "Bearer " + token).execute() //$NON-NLS-1$ //$NON-NLS-2$
					.returnContent();

			if (requestResponse == null) {
				logger.error("Null response received from server for submitted forms.");
				return "Error: Null response from server";
			}

			response = requestResponse.asString();

			if (response == null || response.isEmpty()) {
				logger.error("Empty response received from server for submitted forms.");
				return "Error: Empty response from server";
			}

			if (!response.trim().startsWith("[")) { //$NON-NLS-1$
				logger.error("Unexpected response format: Expected a JSON array, got something else.");
				return "Error: Expected a JSON array but got something else.";
			}

		} catch (HttpResponseException e) {
			if (e.getStatusCode() == 500) {
				logger.error("Server returned HTTP 500: Internal Server Error", e);
				return "Error 500: Internal Server Error";
			}

			logger.error("HTTP Error: " + e.getStatusCode() + " - " + e.getReasonPhrase(), e);
			return "Error: " + e.getStatusCode() + " - " + e.getReasonPhrase();
		} catch (IOException e) {
			logger.error("Error fetching submitted forms for customer ID: " + customerID, e);
			return "Error: " + e.getMessage();
		}

		return response;
	}

	public String fillPatientData(IPatient sourcePatient, JsonObject patientJson, List<IDocument> selectedDocuments,
			boolean isEpdSelected) {
		String apiUrl = ApiConstants.getBaseApiUrl() + ApiConstants.PATIENTS_URL;

	    Bundle patientOverviewBundle = new Bundle();
		Patient fhirPatient = resourceFactory.getResource(sourcePatient, IPatient.class, Patient.class);
		fhirPatient.setId(UUID.nameUUIDFromBytes(sourcePatient.getId().getBytes()).toString());
		fhirPatient.getMeta().addProfile(FHIRConstants.PROFILE_PATIENT);
		removeDuplicates(fhirPatient);
		PatientResource.addTelecomInformation(sourcePatient, fhirPatient);
		PatientResource.addContactInformation(sourcePatient, fhirPatient, patientOverviewBundle, resourceFactory);
		patientOverviewBundle = BundleResource.createPatientOverviewBundle(fhirPatient, sourcePatient,
				selectedDocuments, isEpdSelected, resourceFactory, coreModelService, findingsService);
	    AdjustBundleIdentifiers.adjustBundleIdentifiers(patientOverviewBundle);

		String bundleJsonString = ModelUtil.getFhirJson(patientOverviewBundle);
	    JsonObject payload = new JsonObject();
	    payload.addProperty("contentType", "FHIR"); //$NON-NLS-1$ //$NON-NLS-2$
		JsonObject contentJson = JsonParser.parseString(bundleJsonString).getAsJsonObject();
		payload.add("content", contentJson); //$NON-NLS-1$
	    JsonObject urlConfig = new JsonObject();
	    urlConfig.addProperty(FHIRConstants.FHIRKeys.CUSTOMER_ID,
	            patientJson.get(FHIRConstants.FHIRKeys.CUSTOMER_ID).getAsString());
	    urlConfig.addProperty(FHIRConstants.FHIRKeys.PROVIDER_ID,
	            patientJson.get(FHIRConstants.FHIRKeys.PROVIDER_ID).getAsString());
	    urlConfig.addProperty(FHIRConstants.FHIRKeys.FORM_ID,
	            patientJson.get(FHIRConstants.FHIRKeys.FORM_ID).getAsString());
		urlConfig.addProperty(FHIRConstants.FHIRKeys.ONLY_ONE_TAB, false);
	    payload.add("urlConfig", urlConfig); //$NON-NLS-1$

		String payloadJson = gson.toJson(payload);

		if (System.getProperty(DEBUG_MODE) != null) {
			try {
				Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
				String prettyJson = prettyGson.toJson(JsonParser.parseString(payloadJson));
				System.out.println("FHIR JSON Payload:\n" + prettyJson); //$NON-NLS-1$
			} catch (Exception ex) {
				System.out.println("FHIR JSON Payload (raw):\n" + payloadJson); //$NON-NLS-1$
			}
		}

		try {
			String response = Request.Post(apiUrl).addHeader("Authorization", "Bearer " + token) //$NON-NLS-1$ //$NON-NLS-2$
					.bodyString(payloadJson, ContentType.APPLICATION_JSON).execute().returnContent().asString();
			logger.info("Response: " + response);
	        authUi.openBrowser(response);
	        return response;
	    } catch (IOException e) {
			logger.error("Error sending patient data: ", e);
	        return null;
	    }
	}

	private static void removeDuplicates(Patient patient) {

		List<CanonicalType> uniqueProfiles = patient.getMeta().getProfile().stream()
				.collect(Collectors.collectingAndThen(Collectors.toMap(CanonicalType::getValue, c -> c, (c1, c2) -> c1),
						map -> new ArrayList<>(map.values())));
		patient.getMeta().setProfile(uniqueProfiles);

		List<Reference> uniquePractitioners = patient.getGeneralPractitioner().stream()
				.collect(Collectors.collectingAndThen(Collectors.toMap(Reference::getReference, r -> r, (r1, r2) -> r1),
						map -> new ArrayList<>(map.values())));
		patient.getGeneralPractitioner().clear();
		patient.getGeneralPractitioner().addAll(uniquePractitioners);
	}
}