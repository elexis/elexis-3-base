package net.medshare.connector.viollier.handlers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;

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
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Xid;
import net.medshare.connector.viollier.Messages;
import net.medshare.connector.viollier.data.ViollierConnectorSettings;
import net.medshare.connector.viollier.ses.PortalCookieService;

public class DiagnoseQueryPatientHandler extends AbstractHandler {
	private static Logger log = LoggerFactory.getLogger(DiagnoseQueryPatientHandler.class);

	private static String DOMAIN_VIONR = "viollier.ch/vioNumber";
	private ViollierConnectorSettings mySettings;
	private String httpsUrl;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String cookie = "";
		String vioNumber = "";

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
			log.error("Enoding not supported", e1);
		}

		Patient patient = ElexisEventDispatcher.getSelectedPatient();
		if (patient == null) {
			log.warn("No patient selected - exit execution of DiagnoseQueryPatientHandler");
			MessageDialog.openError(new Shell(), Messages.Handler_errorTitleNoPatientSelected,
					Messages.Handler_errorMessageNoPatientSelected);
			return null;
		}
		vioNumber = getVioNr(patient);

		Boolean preferedPresentation = false;
		if (mySettings.getMachineUseGlobalSettings())
			preferedPresentation = mySettings.getMachinePreferedPresentation();
		else
			preferedPresentation = mySettings.getGlobalPreferedPresentation();

		if (!vioNumber.isEmpty()) {
			try {
				httpsUrl += URLEncoder.encode("?sgs.cortex.nanr=" + vioNumber, "UTF-8");
				httpsUrl += URLEncoder.encode("&sgs.cortex.cumulative=" + preferedPresentation.toString(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.error("Enoding not supported", e);
			}

		}

		// Browser ConsultIT öffnen
		try {
			IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
			IWebBrowser externalBrowser = browserSupport.getExternalBrowser();
			externalBrowser.openURL(new URI(httpsUrl).toURL());
		} catch (Exception e) {
			log.error("Error openen url [{}]: ", httpsUrl, e);
		}
		return null;
	}

	private static String getVioNr(Patient patient) {
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
