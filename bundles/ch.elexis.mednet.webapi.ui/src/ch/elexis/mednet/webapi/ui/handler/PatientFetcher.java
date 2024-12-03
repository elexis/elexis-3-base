package ch.elexis.mednet.webapi.ui.handler;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
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
import ch.elexis.mednet.webapi.core.fhir.resources.util.JsonManipulator;

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
				.getService(IModelService.class, "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)").get();
	}

	public String fetchCustomerId() {
		String response = "";
		try {
			response = Request.Get(ApiConstants.CUSTOMERS_URL).addHeader("Authorization", "Bearer " + token).execute()
					.returnContent()
					.asString();
		} catch (IOException e) {
			logger.error("Error fetching customer ID: ", e);
		}
		return response;
	}

	public String fetchProvidersId(int customerID) {
		String apiUrl = String.format(ApiConstants.PROVIDERS_URL, customerID);
		String response = "";
		try {
			response = Request.Get(apiUrl).addHeader("Authorization", "Bearer " + token).execute().returnContent()
					.asString();
		} catch (IOException e) {
			logger.error("Error fetching provider ID: ", e);
		}
		return response;
	}

	public String fetchFormsByProviderIdWithRetry(Integer customerId, Integer providerId) {
		String apiUrl = String.format(ApiConstants.FORMS_URL, customerId, providerId);
		String response = "";
		int retryCount = 0;
		boolean success = false;

		while (!success && retryCount < 5) {
			try {
				response = Request.Get(apiUrl).addHeader("Authorization", "Bearer " + token).execute().returnContent()
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
			if (response == null || response.isEmpty() || !response.trim().startsWith("[")) {
				return false;
			}
			JsonParser.parseString(response).getAsJsonArray();
			return true;
		} catch (JsonSyntaxException e) {
			return false;
		}
	}

	public String fetchSubmitFormsId(int customerID) {
		String apiUrl = String.format(ApiConstants.SUBMITTED_FORMS_URL, customerID);
		String response = "";

		try {
			var requestResponse = Request.Get(apiUrl).addHeader("Authorization", "Bearer " + token).execute()
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

			if (!response.trim().startsWith("[")) {
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
		String apiUrl = ApiConstants.PATIENTS_URL;

	    Bundle patientOverviewBundle = new Bundle();
		Patient fhirPatient = resourceFactory.getResource(sourcePatient, IPatient.class, Patient.class);
		fhirPatient.setId(UUID.nameUUIDFromBytes(sourcePatient.getId().getBytes()).toString());
		fhirPatient.getMeta().addProfile(FHIRConstants.PROFILE_PATIENT);

		PatientResource.addContactInformation(sourcePatient, fhirPatient, patientOverviewBundle, resourceFactory);
		patientOverviewBundle = BundleResource.createPatientOverviewBundle(fhirPatient, sourcePatient,
				selectedDocuments, isEpdSelected, resourceFactory, coreModelService, findingsService);
	    AdjustBundleIdentifiers.adjustBundleIdentifiers(patientOverviewBundle);

		String bundleJsonString = ModelUtil.getFhirJson(patientOverviewBundle);

		JsonManipulator manipulator = new JsonManipulator();
		try {
			bundleJsonString = manipulator.adjustDocumentReference(bundleJsonString);
		} catch (IOException e) {
			logger.error("Error when customizing the JSON: ", e);
			return null;
		}
	    JsonObject payload = new JsonObject();
	    payload.addProperty("contentType", "FHIR");
		JsonObject contentJson = JsonParser.parseString(bundleJsonString).getAsJsonObject();
		payload.add("content", contentJson);
	    JsonObject urlConfig = new JsonObject();
	    urlConfig.addProperty(FHIRConstants.FHIRKeys.CUSTOMER_ID,
	            patientJson.get(FHIRConstants.FHIRKeys.CUSTOMER_ID).getAsString());
	    urlConfig.addProperty(FHIRConstants.FHIRKeys.PROVIDER_ID,
	            patientJson.get(FHIRConstants.FHIRKeys.PROVIDER_ID).getAsString());
	    urlConfig.addProperty(FHIRConstants.FHIRKeys.FORM_ID,
	            patientJson.get(FHIRConstants.FHIRKeys.FORM_ID).getAsString());
		urlConfig.addProperty(FHIRConstants.FHIRKeys.ONLY_ONE_TAB, false);
	    payload.add("urlConfig", urlConfig);

		String payloadJson = gson.toJson(payload);

		try {
			String response = Request.Post(apiUrl).addHeader("Authorization", "Bearer " + token)
					.bodyString(payloadJson, ContentType.APPLICATION_JSON).execute().returnContent().asString();
			logger.info("Response: " + response);
	        authUi.openBrowser(response);
	        return response;
	    } catch (IOException e) {
	        logger.error("Error sending patient data: ", e);
	        return null;
	    }
	}
}