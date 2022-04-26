package net.medshare.connector.viollier.handlers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

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

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.data.LabResult;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.laborimport.viollier.v2.data.KontaktOrderManagement;
import ch.elexis.laborimport.viollier.v2.data.LaborwerteOrderManagement;
import net.medshare.connector.viollier.Messages;
import net.medshare.connector.viollier.data.ViollierConnectorSettings;
import net.medshare.connector.viollier.ses.PortalCookieService;

public class DiagnoseQueryOrderHandler extends AbstractHandler {
	private static Logger log = LoggerFactory.getLogger(DiagnoseQueryOrderHandler.class);

	private ViollierConnectorSettings mySettings;
	private String httpsUrl;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String cookie = "";

		mySettings = new ViollierConnectorSettings((Mandant) ElexisEventDispatcher.getSelected(Mandant.class));
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
			MessageDialog.openError(new Shell(), Messages.Handler_errorTitleGetCookie, e.getMessage());
		}
		httpsUrl += "&RCSession=" + cookie;
		try {
			httpsUrl += "&appPath=" + URLEncoder.encode("/consultit/signon.aspx", "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		Patient patient = ElexisEventDispatcher.getSelectedPatient();
		if (patient == null) {
			log.warn("No patient selected - exit execution of DiagnoseQueryOrderHandler");
			MessageDialog.openError(new Shell(), Messages.Handler_errorTitleNoPatientSelected,
					Messages.Handler_errorMessageNoPatientSelected);
			return null;
		}

		// Holen der OrderId anhand des selektierten resultats
		LabResult tempLR = ((LabResult) ElexisEventDispatcher.getSelected(LabResult.class));

		if (tempLR == null) {
			log.warn("No LabResult-ID - exit execution of RepeatOrderHandler");
			MessageDialog.openError(new Shell(), Messages.Handler_errorTitleNoLabResultSelected,
					Messages.Handler_errorMessageNoLabResultSelected);
			return null;
		}
		String labResultId = tempLR.getId();

		String orderId = LaborwerteOrderManagement.findOrderId(labResultId);
		String orderNr = KontaktOrderManagement.load(orderId).get("ORDER_NR");

		if (orderNr == null || orderNr.isEmpty()) {
			log.warn("No order for the given LabResult [" + labResultId + "] found");
			MessageDialog.openError(new Shell(), Messages.Handler_errorTitleNoOrderFound,
					Messages.Handler_errorMessageNoOrderFound);
			return null;
		}

		Boolean preferedPresentation = false;
		if (mySettings.getMachineUseGlobalSettings())
			preferedPresentation = mySettings.getMachinePreferedPresentation();
		else
			preferedPresentation = mySettings.getGlobalPreferedPresentation();

		try {
			httpsUrl += URLEncoder.encode("?sgs.cortex.instituteNr=" + orderNr.substring(0, 3), "UTF-8");
			httpsUrl += URLEncoder.encode("&sgs.cortex.requestNr=" + orderNr.substring(3), "UTF-8");
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
}
