package net.medshare.connector.viollier.handlers;

import java.awt.Desktop;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import net.medshare.connector.viollier.Messages;
import net.medshare.connector.viollier.data.ViollierConnectorSettings;
import net.medshare.connector.viollier.ses.PortalCookieService;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.data.Mandant;

public class DiagnoseQueryDoctorHandler extends AbstractHandler {
	private static Logger log = LoggerFactory.getLogger(DiagnoseQueryDoctorHandler.class);
	private ViollierConnectorSettings mySettings;
	private String httpsUrl;
	
	/**
	 * Starte Labor Befundabfrage : <br>
	 * <ul>
	 * <li>HL7 Datei mit Patientendaten erstellen</li>
	 * <li>i/med Seite starten</li>
	 * </ul>
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		String cookie = "";
		
		mySettings =
			new ViollierConnectorSettings(
				(Mandant) ElexisEventDispatcher.getSelected(Mandant.class));
		httpsUrl = mySettings.getGlobalLoginUrl();
		
		// Cookie holen
		try {
			cookie = new PortalCookieService().getCookie();
			
		} catch (IOException e) {
			log.error("Error getting cookie", e);
			MessageDialog.openError(new Shell(), Messages.Handler_errorTitleGetCookie,
				Messages.Handler_errorMessageGetCookie + e.getMessage());
		} catch (ElexisException e) {
			log.error("No user/password defined", e);
			MessageDialog.openError(new Shell(),
				Messages.Exception_errorTitleNoUserPasswordDefined, e.getMessage());
		}
		httpsUrl += "&RCSession=" + cookie;
		try {
			httpsUrl += "&appPath=" + URLEncoder.encode("/consultit/signon.aspx", "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			log.error("Enoding not supported", e1);
		}
		
		// Browser ConsultIT öffnen
		try {
			Desktop.getDesktop().browse(new URI(httpsUrl));
		} catch (URISyntaxException e) {
			log.error("Could not resolve URI: " + httpsUrl, e);
		} catch (IOException e) {
			log.error("IO exception getting diagnoses via doctor", e);
		}
		return null;
	}
}
