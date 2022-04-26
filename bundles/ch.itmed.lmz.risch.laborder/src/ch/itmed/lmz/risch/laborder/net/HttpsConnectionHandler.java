/*******************************************************************************
 * Copyright (c) 2018 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.lmz.risch.laborder.net;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NoRouteToHostException;
import java.net.URI;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ch.itmed.lmz.risch.laborder.json.ApiRequest;
import ch.itmed.lmz.risch.laborder.json.ApiResponse;
import ch.itmed.lmz.risch.laborder.preferences.PreferenceConstants;
import ch.itmed.lmz.risch.laborder.preferences.SettingsProvider;
import ch.itmed.lmz.risch.laborder.ui.MessageBoxUtil;

public final class HttpsConnectionHandler {

	private static Logger logger = LoggerFactory.getLogger(HttpsConnectionHandler.class);

	public static void connect(final String formId) {

		KeyStore clientStore = null;
		try {
			clientStore = KeyStore.getInstance("PKCS12");
			clientStore.load(
					new FileInputStream(
							SettingsProvider.getSettings().getString(PreferenceConstants.CLIENT_CERTIFICATE)),
					SettingsProvider.getSettings().getString(PreferenceConstants.CERTIFICATE_PASSWORD).toCharArray());
		} catch (FileNotFoundException e) {
			MessageBoxUtil.showErrorDialog("Zertifikat nicht gefunden",
					"Das angegebene Zertifikat konnte nicht geöffnet");
			logger.error("Certificate not found", e);
		} catch (IOException e) {
			if (e.getMessage().equals("keystore password was incorrect")) {
				MessageBoxUtil.showErrorDialog("Zertifikat nicht geladen",
						"Das Zertifikat-Passwort wurde nicht akzeptiert");
			}
			logger.error("Configuration error", e);
		} catch (Exception e) {
			logger.error("General error", e);
		}

		try {
			/*
			 * KeyManagers are responsible for managing the key material which is used to
			 * authenticate the local SSLSocket to its peer.
			 */
			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(clientStore,
					SettingsProvider.getSettings().getString(PreferenceConstants.CERTIFICATE_PASSWORD).toCharArray());

			KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

			// Load the Windows Trusted Root Store
			KeyStore winKeystore = KeyStore.getInstance("Windows-ROOT");
			winKeystore.load(null, null);

			/*
			 * TrustManagers are responsible for managing the trust material that is used
			 * when making trust decisions, and for deciding whether credentials presented
			 * by a peer should be accepted.
			 */
			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(winKeystore);
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagers, trustManagers, new SecureRandom());

			HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			String riboxAddress = SettingsProvider.getSettings().getString(PreferenceConstants.RIBOX_IP); // The address
																											// can be an
																											// IP
																											// or DNS
			URL url = new URL("https://" + riboxAddress + "/include/moduls/asp.php");

			ApiRequest apiRequest = null;
			try {
				apiRequest = new ApiRequest(formId);
			} catch (UnsupportedOperationException e) {
				logger.error("Error creating API request.", e);
				MessageBoxUtil.showErrorDialog("API Request fehlgeschlagen",
						"Der API Request konnte nicht erstellt werden");
				return;
			}

			Gson gson = new Gson();
			String requestJson = gson.toJson(apiRequest);
			Base64.Encoder encoder = Base64.getEncoder();
			String requestEncode = encoder.encodeToString(requestJson.getBytes());

			HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
			httpsURLConnection.setDoOutput(true);
			httpsURLConnection.setRequestMethod("POST");
			httpsURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpsURLConnection.setRequestProperty("Charset", "UTF-8");
			httpsURLConnection.setRequestProperty("Content-Length", Integer.toString(requestEncode.length()));
			httpsURLConnection.setRequestProperty("Host", riboxAddress);
			httpsURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpsURLConnection.setUseCaches(false);

			DataOutputStream dataOutputStream = new DataOutputStream(httpsURLConnection.getOutputStream());
			dataOutputStream.writeBytes("APIJson=" + requestEncode);
			dataOutputStream.flush();
			dataOutputStream.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
			String inputLine;
			String returnUrl = "";
			while ((inputLine = in.readLine()) != null) {
				Base64.Decoder decoder = Base64.getDecoder();
				byte[] bytesDecoded = decoder.decode(inputLine);
				returnUrl = new String(bytesDecoded);
			}
			in.close();

			ApiResponse apiResponse = gson.fromJson(returnUrl, ApiResponse.class);

			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse((new URI(apiResponse.getReturnUrl())));
			}

		} catch (UnrecoverableKeyException e) {
			MessageBoxUtil.showErrorDialog("Zertifikat nicht geladen",
					"Das Zertifikat-Passwort wurde nicht akzeptiert");
			logger.error("Certificate was not accepted", e);
		} catch (NoRouteToHostException e) {
			MessageBoxUtil.showErrorDialog("Keine Netzwerkverbindung",
					"Netzwerkverbindung zum Host konnte nicht hergestellt werden");
			logger.error("No connection to host", e);
		} catch (Exception e) {
			logger.error("General error", e);
		}
	}
}
