/*******************************************************************************
 * Copyright (c) 2010, Oliver Egger, visionary ag
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package ch.docbox.elexis;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import com.equo.chromium.swt.Browser;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.IUser;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import jakarta.inject.Inject;
import jakarta.inject.Named;
/**
 * Creates a browser view which will sso into docbox and if a hospital
 * application is select patient info from elexis will be filled in
 */
public class DocboxView extends ViewPart {

	public static final String ID = "ch.docbox.elexis.DocboxView";
	private Browser browser;

	@Inject
	void activeUser(@Optional IUser user) {
		Display.getDefault().asyncExec(() -> {
			if (user != null) {
				userChanged();
			}
		});
	}

	private String getDoboxLoginUrl() {
		return UserDocboxPreferences.getDocboxBrowserUrl();
	}

	@Override
	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.NATIVE);
		setHome();
	}

	void userChanged() {
		setHome();
	}

	public void setHome() {
		if (CoreHub.getLoggedInContact() != null && UserDocboxPreferences.hasValidDocboxCredentials()) {
			// https://www.docbox.ch/cgi-bin/WebObjects/docbox.woa/wa/default?loginId=LOGIN_ID&ts=TIMESTAMP&sig=GENERATED_SIGNATURE
			// System.out.println(getDoboxLoginUrl() + getSSOLoginParams("MainWelcome"));
			// System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)));
			System.out.println(getDoboxLoginUrl() + getSSOLoginParams("MainWelcome"));
			browser.setUrl(getDoboxLoginUrl() + getSSOLoginParams("MainWelcome"));

		} else {
			browser.setUrl(UserDocboxPreferences.getDocboxBrowserHome());
		}
	}
	// https://www.test.docbox.ch/cgi-bin/WebObjects/docbox.woa/wa/default?loginId=LOGIN_ID&ts=TIMESTAMP&sig=GENERATED_SIGNATURE

	public void setHospitalReferral() {
		if (CoreHub.getLoggedInContact() != null && UserDocboxPreferences.hasValidDocboxCredentials()) {
			browser.setUrl(getDoboxLoginUrl() + getSSOLoginParams("HospitalApplicationsOverview"));
		}
	}

	public void setMyPatient() {
		if (CoreHub.getLoggedInContact() != null && UserDocboxPreferences.hasValidDocboxCredentials()) {
			browser.setUrl(getDoboxLoginUrl() + getSSOLoginParams("MyPatient"));
		}
	}

	public void setTerminvereinbarung() {
		if (CoreHub.getLoggedInContact() != null && UserDocboxPreferences.hasValidDocboxCredentials()) {
			browser.setUrl(getDoboxLoginUrl() + getSSOLoginParams("AppBookingWizzard"));
		}
	}

	public void setAppointment(String termin) {
		if (CoreHub.getLoggedInContact() != null && UserDocboxPreferences.hasValidDocboxCredentials()) {
			String url = getDoboxLoginUrl() + getSSOLoginParams("DocCalendar");
			if (termin != null) {
				try {
					url += "&id=" + URLEncoder.encode(termin, "UTF-8");
				} catch (UnsupportedEncodingException e) {
				}
			}
			browser.setUrl(url);
		}
	}

	private String getSSOLoginParams(String page) {

		long ms = System.currentTimeMillis();
		String ts = StringUtils.EMPTY + System.currentTimeMillis() / 1000;

		String username = UserDocboxPreferences.getDocboxLoginID(false);
		String password = UserDocboxPreferences.getSha1DocboxPassword();
		String basicUser = "framsteg-gmbh_elexis_7247D69F";
		String sig = new String(sig(username, password, Long.parseUnsignedLong(ts), basicUser), StandardCharsets.UTF_8);

		try {
			String result = "?ts=" + ts + "&loginId=" + URLEncoder.encode(username, "UTF-8") + "&sig="
					+ URLEncoder.encode(sig, "UTF-8") + "&page="
					+ URLEncoder.encode(page, "UTF-8");
			// return "?ts=" + ts + "&loginId=" + URLEncoder.encode(username, "UTF-8") +
			// "&sig=" + sig + "&page="
			// + URLEncoder.encode(page, "UTF-8");
			return result;
		} catch (UnsupportedEncodingException e) {
			return StringUtils.EMPTY;
		}
	}

	// Hashing process to connect to Docbox
	public byte[] sig(String loginId, String loginPassword, long ts, String basicUser) {
		try {
			// Concatenate elements
			String message = loginId + ":" + String.valueOf(ts) + ":" + toHex(sha1(loginPassword));

			// Get a Max generator instance
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(toHex(sha1(basicUser)).getBytes("UTF-8"), "HmacSHA1"));

			// Get the Mac and encode it in Base64
			return Base64.getEncoder().encode(mac.doFinal(message.getBytes("UTF-8")));
		} catch (final Exception e) {
			// Error
		}
		return null;
	}

	// Helper method to obtain SHA1 hash
	static byte[] sha1(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(text.getBytes("UTF-8"));
			return md.digest();
		} catch (final Exception e) {
			// Error
		}
		return null;
	}

	// Helper method to convert bytes to Hexadecimal form
	static String toHex(final byte[] v) {
		char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		String out = "";

		for (final byte element : v) {
			out = out + hex[(element >> 4) & 0xF] + hex[element & 0xF];
		}
		return out;
	}

	@Override
	public void setFocus() {

	}

	@Optional
	@Inject
	public void setFixLayout(MPart part, @Named(Preferences.USR_FIX_LAYOUT) boolean currentState) {
		CoreUiUtil.updateFixLayout(part, currentState);
	}
}
