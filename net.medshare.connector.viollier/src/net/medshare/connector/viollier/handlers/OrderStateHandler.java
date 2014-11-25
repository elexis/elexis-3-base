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
import ch.elexis.data.Patient;

public class OrderStateHandler extends AbstractHandler {
	private static Logger log = LoggerFactory.getLogger(OrderStateHandler.class);
	
	private ViollierConnectorSettings mySettings;
	private String httpsUrl;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		String vorname = "";
		String name = "";
		String geburtsdatum = "";
		String doctor = "";
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
			log.error("No username/password defined", e);
			MessageDialog.openError(new Shell(),
				Messages.Exception_errorTitleNoUserPasswordDefined, e.getMessage());
		}
		httpsUrl += "&RCSession=" + cookie;
		
		Patient patient = ElexisEventDispatcher.getSelectedPatient();
		if (patient != null) {
			try {
				httpsUrl +=
					"&appPath=" + URLEncoder.encode("/orderit/viewOrderStatusFromGP?", "UTF-8");
				vorname = patient.getVorname();
				httpsUrl += URLEncoder.encode("&firstname=" + vorname, "UTF-8");
				name = patient.getName();
				httpsUrl += URLEncoder.encode("&surname=" + name, "UTF-8");
				geburtsdatum = convertDate(patient.getGeburtsdatum());
				httpsUrl += URLEncoder.encode("&dateOfBirth=" + geburtsdatum, "UTF-8");
				
				if (mySettings.getMandantUseGlobalSettings())
					doctor = mySettings.getGlobalViollierClientId();
				else
					doctor = mySettings.getMandantViollierClientId();
				
				if (!doctor.isEmpty()) {
					httpsUrl += URLEncoder.encode("&doctor=" + doctor, "UTF-8");
				}
				
			} catch (UnsupportedEncodingException e) {
				log.error("Encoding not supported", e);
			}
		}
		
		// Browser OrderIT öffnen
		try {
			Desktop.getDesktop().browse(new URI(httpsUrl));
		} catch (URISyntaxException e) {
			log.error("Could not resolve URI: " + httpsUrl, e);
		} catch (IOException e) {
			log.error("IO Exception while trying to get order state", e);
		}
		return null;
	}
	
	private String convertDate(String gebDat){
		if (gebDat.isEmpty())
			return "";
		String tempDay = gebDat.substring(0, 2);
		String tempMonth = gebDat.substring(3, 5);
		String tempYear = gebDat.substring(6);
		
		return tempYear + "-" + tempMonth + "-" + tempDay;
	}
}
