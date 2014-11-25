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
import ch.elexis.data.LabResult;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.laborimport.viollier.v2.data.KontaktOrderManagement;
import ch.elexis.laborimport.viollier.v2.data.LaborwerteOrderManagement;

public class RepeatOrderHandler extends AbstractHandler {
	private static Logger log = LoggerFactory.getLogger(RepeatOrderHandler.class);
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
			log.error("No password/user defined", e);
			MessageDialog.openError(new Shell(), Messages.Handler_errorTitleGetCookie,
				e.getMessage());
		}
		httpsUrl += "&RCSession=" + cookie;
		try {
			httpsUrl +=
				"&appPath="
					+ URLEncoder.encode("/orderit/createOrderFromConsultIT?app=LAB400CI&", "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			log.error("Enoding not supported", e1);
		}
		
		Patient patient = ElexisEventDispatcher.getSelectedPatient();
		if (patient == null) {
			log.warn("No patient selected - exit execution of RepeatOrderHandler");
			MessageDialog.openError(new Shell(), Messages.Handler_errorTitleNoPatientSelected,
				Messages.Handler_errorMessageNoPatientSelected);
			return null;
		}
		// Holen der OrderId anhand des selektierten resultats
		PersistentObject obj =
			(PersistentObject) ElexisEventDispatcher.getSelected(LabResult.class);
		String labResultId = null;
		if (obj != null) {
			labResultId = ((LabResult) obj).getId();
		}
		
		if (labResultId == null || labResultId.isEmpty()) {
			log.warn("No LabResult-ID - exit execution of RepeatOrderHandler");
			MessageDialog.openError(new Shell(), Messages.Handler_errorTitleNoLabResultSelected,
				Messages.Handler_errorMessageNoLabResultSelected);
			return null;
		}
		
		String orderId = LaborwerteOrderManagement.findOrderId(labResultId);
		String orderNr = KontaktOrderManagement.load(orderId).get("ORDER_NR");
		
		if (orderNr == null || orderNr.isEmpty()) {
			log.warn("No order for the given LabResult [" + labResultId + "] found");
			MessageDialog.openError(new Shell(), Messages.Handler_errorTitleNoOrderFound,
				Messages.Handler_errorMessageNoOrderFound);
			return null;
		}
		try {
			httpsUrl += URLEncoder.encode("req=" + orderNr, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			log.error("Enoding not supported", e1);
		}
		
		// Browser OrderIT öffnen
		try {
			Desktop.getDesktop().browse(new URI(httpsUrl));
		} catch (URISyntaxException e) {
			log.error("Could not resolve URI: " + httpsUrl, e);
		} catch (IOException e) {
			log.error("IO exception while trying to create order", e);
		}
		return null;
	}
}
