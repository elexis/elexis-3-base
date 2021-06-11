package net.medshare.connector.viollier.handlers;

import static ch.elexis.core.constants.XidConstants.DOMAIN_AHV;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.constants.Elexis;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.data.Anschrift;
import ch.elexis.data.Fall;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Xid;
import net.medshare.connector.viollier.Messages;
import net.medshare.connector.viollier.data.ViollierConnectorSettings;
import net.medshare.connector.viollier.ses.PortalCookieService;

public class CreateOrderHandler extends AbstractHandler {
	private static Logger log = LoggerFactory.getLogger(CreateOrderHandler.class);
	private static String DOMAIN_VIONR = "viollier.ch/vioNumber";
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
		String vioNumber = "";
		String vorname = "";
		String name = "";
		String geburtsdatum = "";
		String gender = "";
		String address = "";
		String plz = "";
		String ort = "";
		String land = "";
		String socialSecurityNumber = ""; // AHV-Nummer
		String insuranceCardNumber = ""; // Versicherungskarte
		boolean hasVioNumber = true;
		
		Patient patient = ElexisEventDispatcher.getSelectedPatient();
		if (patient == null) {
			log.warn("No patient selected - exit CreateOrderHandler execution");
			MessageDialog.openError(new Shell(), Messages.Handler_errorTitleNoPatientSelected,
				Messages.Handler_errorMessageNoPatientSelected);
			return null;
		}
		vioNumber = getVioNr(patient);
		// wenn der Kunde keine VioNummer hat, Bestellung mit Patientendaten
		if (vioNumber.isEmpty()) {
			hasVioNumber = false;
			vorname = patient.getVorname();
			name = patient.getName();
			geburtsdatum = convertDate(patient.getGeburtsdatum());
			gender = parseGender(patient);
			Anschrift tempAdr = patient.getAnschrift();
			address = tempAdr.getStrasse();
			plz = tempAdr.getPlz();
			ort = tempAdr.getOrt();
			land = tempAdr.getLand();
			
			socialSecurityNumber = getSocialSecuritNumber(patient);
			insuranceCardNumber = getInsuranceCardNumber(patient);
		}
		
		// URL zum Direktaufruf von OrderIT
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
			log.error("No password/user defined", e);
			MessageDialog.openError(new Shell(), Messages.Handler_errorTitleGetCookie,
				e.getMessage());
		}
		httpsUrl += "&RCSession=" + cookie;
		try {
			httpsUrl += "&appPath=" + URLEncoder.encode("/orderit/createOrderFromGP?", "UTF-8");
			// falls VioNummer bekannt
			if (hasVioNumber) {
				httpsUrl += URLEncoder.encode("vioNumber=" + vioNumber, "UTF-8");
			}
			// sonst Patientendaten mitgeben
			else {
				httpsUrl += URLEncoder.encode("firstname=" + vorname, "UTF-8");
				httpsUrl += URLEncoder.encode("&surname=" + name, "UTF-8");
				httpsUrl += URLEncoder.encode("&dateOfBirth=" + geburtsdatum, "UTF-8");
				httpsUrl += URLEncoder.encode("&gender=" + gender, "UTF-8");
				httpsUrl += URLEncoder.encode("&address=" + address, "UTF-8");
				httpsUrl += URLEncoder.encode("&zip=" + plz, "UTF-8");
				httpsUrl += URLEncoder.encode("&city=" + ort, "UTF-8");
				httpsUrl += URLEncoder.encode("&country=" + land, "UTF-8");
				httpsUrl += URLEncoder.encode("&patientReference=" + patient.getPatCode(), "UTF-8");
				if (StringUtils.isNotBlank(patient.get(Patient.FLD_MOBILEPHONE))) {
					httpsUrl += URLEncoder
						.encode("&mobileNumber=" + patient.get(Patient.FLD_MOBILEPHONE), "UTF-8");
				}
				if (StringUtils.isNotBlank(socialSecurityNumber)) {
					httpsUrl +=
						URLEncoder.encode("&socialSecurityNumber=" + socialSecurityNumber, "UTF-8");
				}
				if (StringUtils.isNotBlank(insuranceCardNumber)) {
					httpsUrl +=
						URLEncoder.encode("&insuranceCardNumber=" + insuranceCardNumber, "UTF-8");
				}
				httpsUrl += URLEncoder.encode("&senderName=elexis.info", "UTF-8");
				httpsUrl += URLEncoder.encode(
					"&senderSoftware=" + getSenderSoftware(), "UTF-8");
			}
		} catch (UnsupportedEncodingException e1) {
			log.error("Enoding not supported", e1);
		}
		
		// Browser OrderIT öffnen
		try {
			IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
			IWebBrowser externalBrowser = browserSupport.getExternalBrowser();
			externalBrowser.openURL(new URI(httpsUrl).toURL());
		} catch (Exception e) {
			log.error("Error openen url [{}]: ", httpsUrl, e);
		}
		return null;
	}
	
	/**
	 * Get name with version, without qualifier etc.
	 * 
	 * @return
	 */
	private String getSenderSoftware(){
		StringBuilder sb = new StringBuilder();
		if (Elexis.APPLICATION_NAME.contains(" ")) {
			sb.append(Elexis.APPLICATION_NAME.split(" ")[0]);
		} else {
			sb.append(Elexis.APPLICATION_NAME);
		}
		sb.append(" ");
		String[] parts = Elexis.VERSION.split("\\.");
		if(parts.length > 3) {
			sb.append(parts[0]).append(".").append(parts[1]).append(".").append(parts[2]);
		} else {
			sb.append(Elexis.VERSION);
		}
		return sb.toString();
	}
	
	private String getInsuranceCardNumber(Patient patient){
		String ret = "";
		for (Fall fall : patient.getFaelle()) {
			if (fall.isOpen()) {
				if (StringUtils.isNotBlank((String) fall.getExtInfoStoredObjectByKey("VEKANr"))) {
					ret = fall.getRequiredString("VEKANr");
					break;
				}
				if (StringUtils.isNotBlank(fall.getRequiredString("Versicherten-Nummer"))) {
					ret = fall.getRequiredString("Versicherten-Nummer");
					break;
				}
			}
		}
		return ret;
	}

	private String getSocialSecuritNumber(Patient patient){
		String ret = patient.getXid(DOMAIN_AHV);
		if(StringUtils.isBlank(ret)) {
			for (Fall fall : patient.getFaelle()) {
				if (fall.isOpen() && StringUtils.isNotBlank(fall.getRequiredString("AHV-Nummer"))) {
					ret = fall.getRequiredString("AHV-Nummer");
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * retrieve a value of m, f or x for the service; as we have some uncertainty about the actual
	 * contents of the db
	 * 
	 * @param patient
	 * @return
	 */
	private String parseGender(Patient patient){
		String sexRaw = patient.getGeschlecht();
		if (sexRaw.length() > 0) {
			if (sexRaw.equalsIgnoreCase("m"))
				return "M";
			return "F";
		}
		return "X";
	}
	
	private String convertDate(String gebDat){
		if (gebDat.isEmpty())
			return "";
		String tempDay = gebDat.substring(0, 2);
		String tempMonth = gebDat.substring(3, 5);
		String tempYear = gebDat.substring(6);
		
		return tempYear + "-" + tempMonth + "-" + tempDay;
	}
	
	private static String getVioNr(Patient patient){
		Query<Xid> patientVioNrQuery = new Query<Xid>(Xid.class);
		patientVioNrQuery.add(Xid.FLD_OBJECT, Query.EQUALS, patient.getId());
		patientVioNrQuery.add(Xid.FLD_DOMAIN, Query.EQUALS, DOMAIN_VIONR);
		List<Xid> patienten = patientVioNrQuery.execute();
		if (patienten.isEmpty()) {
			return "";
		} else {
			return ((Xid) patienten.get(0)).getDomainId();
		}
	}
	
}
