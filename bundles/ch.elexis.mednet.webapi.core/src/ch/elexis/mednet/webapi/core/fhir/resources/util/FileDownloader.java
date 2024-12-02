package ch.elexis.mednet.webapi.core.fhir.resources.util;


import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import ch.elexis.core.services.IConfigService;
import ch.elexis.mednet.webapi.core.IMednetAuthService;
import ch.elexis.mednet.webapi.core.constants.ApiConstants;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;


public class FileDownloader {

	private static final Logger logger = LoggerFactory.getLogger(FileDownloader.class);
	private IMednetAuthService authService;

	public FileDownloader(IMednetAuthService authService) {
		this.authService = authService;
	}

	public void downloadForms() {
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		ServiceReference<IMednetAuthService> serviceReference = context.getServiceReference(IMednetAuthService.class);

		if (serviceReference != null) {
			authService = context.getService(serviceReference);
			try {
				Map<String, Object> parameters = new HashMap<>();
				parameters.put(PreferenceConstants.TOKEN_GROUP, PreferenceConstants.TOKEN_GROUP_KEY);
				Optional<String> authToken = authService.getToken(parameters);
				if (authToken.isPresent()) {
					String token = authToken.get();
					List<Integer> customerIds = fetchCustomerIds(token);
					if (!customerIds.isEmpty()) {
						for (Integer customerId : customerIds) {

							fetchAndDownloadFormsForCustomer(token, customerId);
						}
					} else {
						logger.error("No customer IDs found.");
					}
				} else {
					logger.error("No authentication token received.");
				}
			} catch (Exception ex) {
				logger.error("An error occurred while retrieving the forms: {}", ex.getMessage());
			} finally {
				context.ungetService(serviceReference);
			}
		} else {
			logger.error("ServiceReference for IMednetAuthService is null.");
		}
	}

	private List<Integer> fetchCustomerIds(String token) {
		List<Integer> customerIds = new ArrayList<>();
		try {
			String apiUrl = ApiConstants.CUSTOMERS_URL;
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl))
					.header("Authorization", "Bearer " + token).GET().build();

			HttpClient client = HttpClient.newHttpClient();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				String responseBody = response.body();

				JsonArray customersArray = JsonParser.parseString(responseBody).getAsJsonArray();
				for (int i = 0; i < customersArray.size(); i++) {
					JsonObject customer = customersArray.get(i).getAsJsonObject();
					int customerId = customer.get("id").getAsInt();
					customerIds.add(customerId);
				}

				logger.info("Found {} customers.", customerIds.size());
			} else {
				logger.error("API request failed. Status Code: {}", response.statusCode());
			}
		} catch (Exception ex) {
			logger.error("An error occurred while fetching customer IDs: {}", ex.getMessage());
		}
		return customerIds;
	}

	private void fetchAndDownloadFormsForCustomer(String token, Integer customerId) {
		try {
			String apiUrl = String.format(ApiConstants.SUBMITTED_FORMS_URL, customerId);
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl))
					.header("Authorization", "Bearer " + token).GET().build();

			HttpClient client = HttpClient.newHttpClient();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				String responseBody = response.body();
				downloadFilesFromResponse(responseBody, token);
			}

		} catch (Exception ex) {
			logger.error("An error occurred while fetching forms for customerId {}: {}", customerId, ex.getMessage());
		}
	}

	private void downloadFilesFromResponse(String responseBody, String token) {
		try {
			Gson gson = new Gson();
			Type listType = new TypeToken<List<Map<String, Object>>>() {
			}.getType();
			List<Map<String, Object>> formList = gson.fromJson(responseBody, listType);
			System.out.println("formList " + formList);

			for (Map<String, Object> form : formList) {
				String packageId = (String) form.get("packageId");
				String externalPatientId = (String) form.get("externalPatientId");
				String patientFirstName = (String) form.get("patientFirstName");
				String patientLastName = (String) form.get("patientLastName");
				String patientName = patientLastName + " " + patientFirstName;
				String title = (String) form.get("title");


				Map<String, Object> customer = (Map<String, Object>) form.get("customer");
				Map<String, Object> provider = (Map<String, Object>) form.get("provider");
				String receiver = (String) provider.get("lastName");
				String sender = (String) customer.get("lastName");

				if (form.containsKey("files")) {
					List<Map<String, Object>> files = (List<Map<String, Object>>) form.get("files");

					for (Map<String, Object> file : files) {
						String downloadUrl = (String) file.get("downloadUrl");
						String createDate = extractDateFromUrl((String) downloadUrl);
						String objectId = (String) file.get("objectId");
						if (downloadUrl != null && objectId != null && packageId != null) {
							String sanitizedCreateDate = createDate.replaceAll("[\\\\/:*?\"<>|]", "_");
							String sanitizedPatientName = patientName.replaceAll("[\\\\/:*?\"<>|]", "_");
							String sanitizedTitle = title.replaceAll("[\\\\/:*?\"<>|]", "_");
							String sanitizedReceiver = receiver.replaceAll("[\\\\/:*?\"<>|]", "_");
							String sanitizedSender = sender.replaceAll("[\\\\/:*?\"<>|]", "_");

							String fileName = externalPatientId + "_" + sanitizedPatientName + "_" + sanitizedTitle
									+ "_" + sanitizedSender + "_" + sanitizedReceiver + "_" + sanitizedCreateDate
									+ ".pdf";

							downloadFile(downloadUrl, fileName, packageId, token,
									(List<Map<String, String>>) file.get("downloadHeaders"), externalPatientId);
						} else {
							logger.error("Missing download URL, object ID, or package ID.");
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error("An error occurred while processing the response: {}", ex.getMessage());
		}
	}


	private void downloadFile(String downloadUrl, String fileName, String packageId, String token,
            List<Map<String, String>> downloadHeadersList, String externalPatientId) {
		try {
			String downloadPath = getDownloadStore();

			File directory = new File(downloadPath);
			if (!directory.exists()) {
				boolean dirCreated = directory.mkdirs();
				if (dirCreated) {
					logger.info("Download directory created: {}", downloadPath);
				} else {
					logger.error("Could not create the download directory: {}", downloadPath);
					return;
				}
			}

			HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(downloadUrl)).GET();

			if (downloadHeadersList != null) {
				for (Map<String, String> header : downloadHeadersList) {
					String key = header.get("key");
					String value = header.get("value");
					if (key != null && value != null) {
						requestBuilder.header(key, value);
					}
				}
			}

			HttpRequest request = requestBuilder.build();
			HttpClient client = HttpClient.newHttpClient();
			Path filePath = Paths.get(downloadPath, fileName);
			HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(filePath));

			if (response.statusCode() == 200) {
				acknowledgeDownloadSuccess(packageId, token);
				logger.info("File downloaded successfully: {}", filePath.toString());
			} else {
				acknowledgeDownloadFailure(packageId, token, "Download fehlgeschlagen");
				logger.error("Error downloading the file. Status code: {}", response.statusCode());
			}
		} catch (Exception ex) {
			logger.error("An error occurred during the download: {}", ex.getMessage());
		}
	}

	private static String extractDateFromUrl(String url) {
		String pattern = "(\\d{14})"; //$NON-NLS-1$
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(url);
		if (m.find()) {
			String dateString = m.group(0);
			String year = dateString.substring(0, 4);
			String month = dateString.substring(4, 6);
			String day = dateString.substring(6, 8);
			String hour = dateString.substring(8, 10);
			String minute = dateString.substring(10, 12);
			return day + "." + month + "." + year + " " + hour + ":" + minute; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		} else {
		return "Unknown Date"; //$NON-NLS-1$
		}
	}

	private void acknowledgeDownloadSuccess(String packageId, String token) {
		try {
			String successUrl = ApiConstants.BASE_API_URL + "/" + packageId + "/download-success?objectType=Form";
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(successUrl))
					.header("Authorization", "Bearer " + token).method("PATCH", HttpRequest.BodyPublishers.noBody())
					.build();

			HttpClient client = HttpClient.newHttpClient();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				logger.info("Download successfully confirmed for package ID: " + packageId);
			}
		} catch (Exception ex) {
			logger.error("An error occurred while confirming the download: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	private void acknowledgeDownloadFailure(String packageId, String token, String errorMessage) {
		try {
			String failureUrl = ApiConstants.BASE_API_URL + "/" + packageId + "/download-failure?objectType=Form"; // Use
																													// constant

			String jsonBody = "{ \"errorMessage\": \"" + errorMessage.replace("\"", "\\\"") + "\" }";

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(failureUrl))
					.header("Authorization", "Bearer " + token).header("Content-Type", "application/json")
					.method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody)).build();

			HttpClient client = HttpClient.newHttpClient();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200 || response.statusCode() == 204) {
				logger.info("Download failure successfully acknowledged for package ID: {}", packageId);
			} else {
				logger.error("Failed to acknowledge download failure for package ID: {}. Status Code: {}", packageId,
						response.statusCode());
			}
		} catch (Exception ex) {
			logger.error("An error occurred while acknowledging the download failure: {}", ex.getMessage());
		}

	}

	private String getDownloadStore() {
		try {
			BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
			ServiceReference<IConfigService> serviceReference = context.getServiceReference(IConfigService.class);

			if (serviceReference != null) {
				IConfigService configService = context.getService(serviceReference);
				if (configService != null) {
					String downloadPath = configService.getActiveUserContact(PreferenceConstants.MEDNET_DOWNLOAD_PATH,
							"");

					if (downloadPath == null || downloadPath.trim().isEmpty()) {
						logger.warn("No download path found in preferences.");
					} else {
						logger.info("Download path retrieved: {}", downloadPath);
					}

					return downloadPath;
				}
			}

		} catch (Exception e) {
			logger.error("Error when retrieving the download path from the preferences: {}", e.getMessage(), e);
			return "";
		}
		return null;
	}

}
