package org.iatrix.bestellung.rose.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.iatrix.bestellung.rose.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ch.elexis.core.l10n.Messages;
import ch.elexis.core.model.IOrderEntry;
import ch.elexis.core.model.OrderEntryState;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.exchange.XChangeException;

public class HttpOrderTransportService {
	private static Logger logger = LoggerFactory.getLogger(HttpOrderTransportService.class);

	// Testmodus-Flag
	private boolean isTestMode = false;

	public void setTestMode(boolean isTestMode) {
		this.isTestMode = isTestMode;
	}

	public String retrieveAccessToken(String clientId, String clientSecret) throws XChangeException {
		try {
			URL url = getUrl(Constants.TOKEN_URL, Constants.TOKEN_URL_TEST);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST"); //$NON-NLS-1$
			conn.setRequestProperty("Authorization", "Basic " + encodeBasicAuth(clientId, clientSecret)); //$NON-NLS-1$ //$NON-NLS-2$
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); //$NON-NLS-1$ //$NON-NLS-2$
			conn.setDoOutput(true);

			String body = "grant_type=client_credentials"; //$NON-NLS-1$

			try (OutputStream os = conn.getOutputStream()) {
				os.write(body.getBytes(StandardCharsets.UTF_8));
			}

			BufferedReader in = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			in.close();

			JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
			return json.get("access_token").getAsString(); //$NON-NLS-1$

		} catch (Exception e) {
			throw new XChangeException("Error retrieving the token: " + e.getMessage()); //$NON-NLS-1$
		}
	}

	public void sendOrderRequest(String xml, String token, List<IOrderEntry> entriesToMark) throws XChangeException {
		try {
			URL url = getUrl(Constants.ORDER_URL, Constants.ORDER_URL_TEST);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST"); //$NON-NLS-1$
			conn.setRequestProperty("Authorization", "Bearer " + token); //$NON-NLS-1$ //$NON-NLS-2$
			conn.setRequestProperty("Content-Type", "application/vnd.orders.v1+xml"); //$NON-NLS-1$ //$NON-NLS-2$
			conn.setDoOutput(true);

			try (OutputStream os = conn.getOutputStream()) {
				os.write(xml.getBytes(StandardCharsets.UTF_8));
			}

			int responseCode = conn.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					(responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream(),
					StandardCharsets.UTF_8));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			in.close();

			if (responseCode == 409) {
				showUserFriendlyDialog(Messages.HttpOrderTransportService_OrderAlreadySent_Title,
						Messages.HttpOrderTransportService_OrderAlreadySent_Message);
			    return;
			}

			
			if (responseCode != 200) {
				throw new XChangeException(
						"Error sending the order: HTTP " + responseCode + " - " + response.toString()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			for (IOrderEntry entry : entriesToMark) {
				entry.setState(OrderEntryState.ORDERED);
				CoreModelServiceHolder.get().save(entry);
			}

			String landingPage = extractLandingPageUrl(response.toString());
			if (landingPage != null && !landingPage.isEmpty() && !isTestMode) {
				if (showConfirmationDialog()) {
					openBrowser(landingPage);
				} else {
					logger.info("User has not opened the landing Page"); //$NON-NLS-1$
				}
			}

		} catch (Exception e) {
			throw new XChangeException("Error sending the order: " + e.getMessage()); //$NON-NLS-1$
		}
	}

	private URL getUrl(String liveUrl, String testUrl) throws URISyntaxException, MalformedURLException {
		return new URI(isTestMode ? testUrl : liveUrl).toURL();
	}

	private String extractLandingPageUrl(String responseXml) {
		String startTag = "<landingPageUrl>"; //$NON-NLS-1$
		String endTag = "</landingPageUrl>"; //$NON-NLS-1$

		int startIdx = responseXml.indexOf(startTag);
		if (startIdx == -1) {
			return null;
		}
		int endIdx = responseXml.indexOf(endTag, startIdx);
		if (endIdx == -1) {
			return null;
		}

		int contentStart = startIdx + startTag.length();
		return responseXml.substring(contentStart, endIdx).trim();
	}

	private void openBrowser(String url) {
		Program.launch(url);
	}

	private String encodeBasicAuth(String clientId, String clientSecret) {
		String credentials = clientId + ":" + clientSecret; //$NON-NLS-1$
		return java.util.Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
	}

	private boolean showConfirmationDialog() {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
		messageBox.setText(Messages.Attention);
		messageBox.setMessage(Messages.HttpOrderTransportService_Dialog);
		int response = messageBox.open();
		return response == SWT.YES;
	}
	
	private void showUserFriendlyDialog(String title, String message) {
		Display display = Display.getDefault();
		Shell shell = new Shell(display, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(title);
		shell.setSize(480, 240);
		shell.setLayout(new GridLayout(1, false));
		Label label = new Label(shell, SWT.WRAP);
		label.setText(message);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		new Label(shell, SWT.NONE).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Link link = new Link(shell, SWT.WRAP);
		link.setText(Messages.HttpOrderTransportService_LinkText);
		link.setToolTipText(Messages.HttpOrderTransportService_LinkTooltip);
		link.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		link.addListener(SWT.Selection, e -> {
			Program.launch(
					"https://ghp.clustertec.com/auth/realms/GHP/protocol/openid-connect/auth?client_id=marketplace-portal&redirect_uri=https%3A%2F%2Fghp.clustertec.com%2F%3Ferror%3Dinvalid_request%26error_description%3DMissing%2Bparameter%253A%2Bresponse_type%26state%3D551f02b0-3793-4ed7-8b29-9717fba87de2&state=7769aa6d-1871-4555-8b33-70a805f696f6&response_mode=fragment&response_type=code&scope=openid&nonce=f472a669-678a-4b0d-8a80-8b8f22750e91"); //$NON-NLS-1$
		});

		Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText(Messages.Core_Ok); // $NON-NLS-1$

		GridData gd = new GridData(SWT.END, SWT.BOTTOM, true, true);
		int widthHint = convertHorizontalDLUsToPixels(okButton, 61);
		gd.widthHint = Math.max(90, widthHint);
		okButton.setLayoutData(gd);

		okButton.addListener(SWT.Selection, e -> shell.close());
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private int convertHorizontalDLUsToPixels(Button button, int dlus) {
		GC gc = new GC(button);
		gc.setFont(button.getFont());
		int avgCharWidth = (int) gc.getFontMetrics().getAverageCharacterWidth();
		gc.dispose();
		return (dlus * avgCharWidth) / 4;
	}

}
