package ch.elexis.mednet.webapi.core.constants;


public class ApiConstants {

	// API Base URL
	public static final String BASE_API_URL = "https://demo.mednet.swiss/web/api/v1/external";
	public static final String BASE_URI = "https://demo.mednetpatient.swiss/idsrv";
	public static final String BASE_REDERICT_URI = "https://tools.medelexis.ch/mednet/ac";
	public static final String BASE_REDERICT_URI_OBTAIN = "https://tools.medelexis.ch/mednet/ac-obtain/";

	// Specific API Endpoints
	public static final String CUSTOMERS_URL = BASE_API_URL + "/customers?includeDetails=true";
	public static final String PROVIDERS_URL = BASE_API_URL + "/providers?customerId=%d&includeDetails=true";
	public static final String FORMS_URL = BASE_API_URL + "/forms?customerId=%d&providerId=%d";
	public static final String SUBMITTED_FORMS_URL = BASE_API_URL + "/submitted-forms?customerId=%d";
	public static final String PATIENTS_URL = BASE_API_URL + "/patients";


}