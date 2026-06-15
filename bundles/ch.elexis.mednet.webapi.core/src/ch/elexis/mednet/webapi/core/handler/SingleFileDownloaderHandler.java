package ch.elexis.mednet.webapi.core.handler;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.mednet.webapi.core.IMednetAuthService;
import ch.elexis.mednet.webapi.core.constants.ApiConstants;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;
import ch.elexis.mednet.webapi.core.vfs.MedNetVfsHandler;

public class SingleFileDownloaderHandler {

	private static final Logger logger = LoggerFactory.getLogger(SingleFileDownloaderHandler.class);
	private IMednetAuthService authService;
	private String token;

	/**
	 * Downloads a single file and saves it to the configured download path (local,
	 * SMB or DAV). * @param downloadUrl The URL for downloading the file.
	 * 
	 * @param patientNr           The patient number.
	 * @param patientName         The patient's name.
	 * @param exportType          The type of export.
	 * @param receiver            The recipient.
	 * @param sender              The sender.
	 * @param downloadHeadersList The list of download headers.
	 * @param packageId           The package ID for confirmation.
	 * @param createDate          The creation date.
	 */
	public void downloadSingleFile(String downloadUrl, String patientNr, String patientName, String exportType,
			String receiver, String sender, List<Map<String, String>> downloadHeadersList, String packageId,
			String createDate) {
		try {
			if (!retrieveToken()) {
				logger.error("Download aborted: No authentication token available.");
				return;
			}
			IVirtualFilesystemHandle dirHandle = MedNetVfsHandler.getDownloadDirectory();

			if (dirHandle == null) {
				logger.error(
						"Download path is not set or invalid. Please configure the download path in the settings.");
				return;
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
			String fileName = patientNr + "_" + patientName + "_" + exportType + "_" + sender + "_" + receiver + "_"
					+ sanitizedCreateDate + ".pdf";

			HttpClient client = HttpClient.newHttpClient();

			HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

			if (response.statusCode() == 200) {
				IVirtualFilesystemHandle fileHandle = dirHandle.subFile(fileName);

				try (InputStream in = response.body(); OutputStream out = fileHandle.openOutputStream()) {
					in.transferTo(out);
				}

				logger.info("File downloaded successfully: {}", fileHandle.getAbsolutePath());
				acknowledgeDownloadSuccess(packageId);
			} else {
				logger.error("Error downloading the file. Status code: {}", response.statusCode());

				try (InputStream in = response.body()) {
					String responseBody = new String(in.readAllBytes());
					logger.error("Incorrect response body: {}", responseBody);
				}
			}
		} catch (Exception ex) {
			logger.error("An error occurred while downloading: {}", ex.getMessage(), ex);
		}
	}

	private boolean retrieveToken() {
		try {
			BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
			ServiceReference<IMednetAuthService> serviceReference = context
					.getServiceReference(IMednetAuthService.class);

			if (serviceReference != null) {
				authService = context.getService(serviceReference);
				Map<String, Object> parameters = new HashMap<>();
				parameters.put(PreferenceConstants.TOKEN_GROUP, PreferenceConstants.TOKEN_GROUP_KEY);
				Optional<String> authToken = authService.getToken(parameters);

				if (authToken.isPresent()) {
					this.token = authToken.get();
					context.ungetService(serviceReference);
					return true;
				}
				context.ungetService(serviceReference);
			}
		} catch (Exception e) {
			logger.error("Failed to retrieve auth token.", e);
		}
		return false;
	}

	private void acknowledgeDownloadSuccess(String packageId) {
		if (this.token == null) {
			logger.error("Cannot acknowledge download: Token is null.");
			return;
		}

		try {
			String successUrl = ApiConstants.getBaseApiUrl() + "/" + packageId + "/download-success?objectType=Form";

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(successUrl))
					.header("Authorization", "Bearer " + token).method("PATCH", HttpRequest.BodyPublishers.noBody())
					.build();

			HttpClient client = HttpClient.newHttpClient();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200 || response.statusCode() == 204) {
				logger.info("Download successfully confirmed for package ID: {}", packageId);
			} else {
				logger.warn("Failed to confirm download. Status code: {}", response.statusCode());
			}
		} catch (Exception ex) {
			logger.error("An error occurred while confirming the download: {}", ex.getMessage(), ex);
		}
	}
}