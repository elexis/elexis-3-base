/*******************************************************************************
 *
 * The authorship of this code and the accompanying materials is held by
 * medshare GmbH, Switzerland. All rights reserved.
 * http://medshare.net
 *
 * This code and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0
 *
 * Year of publication: 2013
 *
 *******************************************************************************/
package net.medshare.connector.viollier.ses;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import net.medshare.connector.viollier.Messages;
import net.medshare.connector.viollier.data.ViollierConnectorSettings;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.data.Mandant;

public class PortalCookieService {
	private static Logger log = LoggerFactory.getLogger(PortalCookieService.class);

	private String httpsUrl;
	HttpsURLConnection con = null;
	DataInputStream input;

	private String userid;
	private String password;

	private ViollierConnectorSettings mySettings;

	public String getCookie() throws ClientProtocolException, IOException, ElexisException {
		mySettings = new ViollierConnectorSettings((Mandant) ElexisEventDispatcher.getSelected(Mandant.class));
		// login URL vom Viollier SES
		httpsUrl = mySettings.getGlobalLoginUrl();

		// parameter
		if (!mySettings.getMandantUseGlobalSettings()) {
			userid = mySettings.getMandantUserName();
			password = mySettings.getMandantUserPassword();
		} else {
			userid = mySettings.getGlobalUserName();
			password = mySettings.getGlobalUserPassword();
		}
		if (userid.isEmpty() || password.isEmpty()) {
			log.error("UserId[" + userid + "]\t Password[" + password + "]");
			throw new ElexisException(PortalCookieService.class, Messages.Exception_errorMessageNoUserPasswordDefined,
					ElexisException.EE_NOT_FOUND);
		}

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(httpsUrl);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("userid", userid));
		nameValuePairs.add(new BasicNameValuePair("password", password));

		post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		String cookie = "";
		HttpResponse response = client.execute(post);
		if (response.getStatusLine().toString().equalsIgnoreCase("HTTP/1.1 302 Found")) {
			Header[] headers = response.getHeaders("Set-Cookie");
			String headerValue1 = headers[1].getValue().replaceAll("SCDID_S=", "");
			headerValue1 = headerValue1.replaceAll("; path=/; Secure; (HttpOnly)?", "");
			cookie = URLEncoder.encode(headerValue1, "UTF-8");
		} else
			throw new ElexisException(PortalCookieService.class, Messages.Handler_errorMessageGetCookie,
					ElexisException.EE_UNEXPECTED_RESPONSE);

		return cookie;
	}
}
