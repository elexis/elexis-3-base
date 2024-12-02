package ch.elexis.mednet.webapi.core.handler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IConfigService;
import ch.elexis.mednet.webapi.core.IMednetAuthService;
import ch.elexis.mednet.webapi.core.constants.ApiConstants;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;

public class SingleFileDownloaderHandler {

	private static final Logger logger = LoggerFactory.getLogger(SingleFileDownloaderHandler.class);
	private IMednetAuthService authService;
	private String token;

	/**
	 * Lädt eine einzelne Datei herunter und speichert sie im konfigurierten
	 * Download-Pfad.
	 * 
	 * @param downloadUrl         Die URL zum Herunterladen der Datei.
	 * @param objectId            Die Object-ID der Datei (für den Dateinamen).
	 * @param downloadHeadersList Die Liste der Download-Header.
	 * @param patientNr
	 * @param packageId
	 * @param createDate
	 */
	public void downloadSingleFile(String downloadUrl, String patientNr, String patientName, String exportType,
			String receiver, String sender, List<Map<String, String>> downloadHeadersList, String packageId,
			String createDate) {
		try {

			String downloadPath = getDownloadStore();

			if (downloadPath == null || downloadPath.trim().isEmpty()) {
				logger.error(
						"Download path is not set. Please configure the download path in the settings.");
				return;
			}

			Path downloadDir = Paths.get(downloadPath);
			if (!Files.exists(downloadDir)) {
				try {
					Files.createDirectories(downloadDir);
					logger.info("Download directory created: {}", downloadDir.toAbsolutePath());
				} catch (IOException e) {
					logger.error("Error creating the download directory: {}", e.getMessage());
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
			String sanitizedCreateDate = createDate.replaceAll("[\\\\/:*?\"<>|]", "_");
			Path filePath = downloadDir
			        .resolve(patientNr + "_" + patientName + "_" + exportType + "_" + sender + "_" + receiver + "_"
			                + sanitizedCreateDate + ".pdf");

			HttpClient client = HttpClient.newHttpClient();
			HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(filePath));

			if (response.statusCode() == 200) {
				logger.info("File downloaded successfully: {}", filePath.toAbsolutePath());
				acknowledgeDownloadSuccess(packageId);
			} else {
				logger.error("Error downloading the file. Status code: {}", response.statusCode());
				if (Files.exists(filePath)) {
					String responseBody = new String(Files.readAllBytes(filePath));
					logger.error("Incorrect response body: {}", responseBody);
				} else {
					logger.error("The file was not created.");
				}
			}
		} catch (IOException | InterruptedException ex) {
			logger.error("An error occurred while downloading: {}", ex.getMessage());
		}
	}

	/**
	 * Hilfsmethode zum Abrufen des Preference Stores.
	 * 
	 * @return Der konfigurierte Preference Store.
	 */
	private String getDownloadStore() {
		try {
			// Überprüfen, ob der IConfigService verfügbar ist
			BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
			ServiceReference<IConfigService> serviceReference = context.getServiceReference(IConfigService.class);

			if (serviceReference != null) {
				IConfigService configService = context.getService(serviceReference);
				if (configService != null) {
					// Download-Pfad aus IConfigService abrufen
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

			// Fallback: Verwenden von IEclipsePreferences, falls IConfigService nicht
			// verfügbar ist
			String pluginId = PreferenceConstants.MEDNET_PLUGIN_STRING;
			IEclipsePreferences node = InstanceScope.INSTANCE.getNode(pluginId);
			String downloadPath = node.get(PreferenceConstants.MEDNET_DOWNLOAD_PATH, "");

			if (downloadPath == null || downloadPath.trim().isEmpty()) {
				logger.warn("No download path found in preferences.");
			} else {
				logger.info("Download path retrieved: {}", downloadPath);
			}

			return downloadPath;

		} catch (Exception e) {
			logger.error("Error when retrieving the download path from the preferences: {}", e.getMessage(), e);
			return "";
		}
	}


	private void acknowledgeDownloadSuccess(String packageId) {
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		ServiceReference<IMednetAuthService> serviceReference = context.getServiceReference(IMednetAuthService.class);
		if (serviceReference != null) {
			authService = context.getService(serviceReference);
			try {
				Map<String, Object> parameters = new HashMap<>();
				parameters.put(PreferenceConstants.TOKEN_GROUP, PreferenceConstants.TOKEN_GROUP_KEY);
				Optional<String> authToken = authService.getToken(parameters);
				if (authToken.isPresent()) {
					token = authToken.get();
					try {
						String successUrl = ApiConstants.BASE_API_URL + "/" + packageId
								+ "/download-success?objectType=Form";

						HttpRequest request = HttpRequest.newBuilder().uri(URI.create(successUrl))
								.header("Authorization", "Bearer " + token)
								.method("PATCH", HttpRequest.BodyPublishers.noBody()).build();

						HttpClient client = HttpClient.newHttpClient();
						HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
						if (response.statusCode() == 200) {
							logger.info("Download successfully confirmed for package ID: " + packageId);
						}
					} catch (Exception ex) {
						logger.error("An error occurred while confirming the download: " + ex.getMessage());
						ex.printStackTrace();
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

}
