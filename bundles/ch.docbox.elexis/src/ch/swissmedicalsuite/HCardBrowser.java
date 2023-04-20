/*******************************************************************************
 * Copyright (c) 2010, Oliver Egger, visionary ag
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

package ch.swissmedicalsuite;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;

import com.sun.jna.ptr.PointerByReference;

import ch.elexis.core.ui.util.Log;

public class HCardBrowser {

	final private String gln;
	final private String browserUrl;

	static boolean initApi = false;
	static String glnOld;

	protected static Log log = Log.get("HCardBrowser"); //$NON-NLS-1$

	public HCardBrowser(String gln, String browserUrl) {
		if (!HCardBrowser.initApi) {
			HCardBrowser.initApi = true;

			PointerByReference pByReference = new PointerByReference();
			HCardAPI.INSTANCE.initApi("hCard-OEM-Test", false, pByReference);
			System.setProperty("https.proxyHost", "localhost");
		}
		this.browserUrl = browserUrl;
		this.gln = gln;
		log.log("hcardbrowser initiated " + browserUrl + StringUtils.SPACE + gln, Log.DEBUGMSG);
	}

	public void setProxyPort() {
		if (glnOld == null || !glnOld.equals(gln)) {
			int port = HCardAPI.INSTANCE.getUserProxyPort(gln);
			log.log("getting proxy port for gln:" + gln + " port " + port, Log.DEBUGMSG);

			System.setProperty("https.proxyPort", StringUtils.EMPTY + port);
			glnOld = gln;

			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
				}

				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
				}
			} };

			SSLContext sc;
			try {
				sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			}

			// HostnameVerifier allHostsValid = new HostnameVerifier() {
			// public boolean verify(String arg0, SSLSession arg1) {
			// return true;
			// }
			// };
			//
			// HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		}
	}

	private String getPageParam(String page) {
		try {
			return "?page=" + URLEncoder.encode(page, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return StringUtils.EMPTY;
	}

	public int setTerminvereinbarung() {
		log.log("startSmsBroser, Terminvereinbarung:" + gln, Log.DEBUGMSG);
		String url = browserUrl + this.getPageParam("AppBookingWizzard");
		return HCardAPI.INSTANCE.startSmsBrowser(gln, url, 0);
	}

	public int setHome() {
		log.log("startSmsBroser, setHome :" + gln, Log.DEBUGMSG);
		String url = browserUrl + this.getPageParam("MainWelcome");
		return HCardAPI.INSTANCE.startSmsBrowser(gln, url, 0);
	}

	public int setHospitalReferral() {
		log.log("startSmsBroser, setHospitalReferral: " + gln, Log.DEBUGMSG);
		String url = browserUrl + this.getPageParam("HospitalApplicationsOverview");
		return HCardAPI.INSTANCE.startSmsBrowser(gln, url, 0);
	}

	public int setMyPatient() {
		log.log("startSmsBroser, setMyPatient: " + gln, Log.DEBUGMSG);
		String url = browserUrl + this.getPageParam("MyPatient");
		return HCardAPI.INSTANCE.startSmsBrowser(gln, url, 0);
	}

	public int setAppointment(String terminId) {
		log.log("startSmsBroser, setAppointment: " + gln, Log.DEBUGMSG);
		String url = browserUrl + getPageParam("DocCalendar");
		if (terminId != null) {
			String id = terminId;
			if (id.endsWith("2")) {
				id = id.substring(0, id.length() - 1);
			}
			try {
				url += "&id=" + URLEncoder.encode(id, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return HCardAPI.INSTANCE.startSmsBrowser(gln, url, 0);
	}

}
