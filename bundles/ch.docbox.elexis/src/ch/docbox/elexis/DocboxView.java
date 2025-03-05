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
			browser.setUrl(getDoboxLoginUrl() + getSSOLoginParams("MainWelcome"));
		} else {
			browser.setUrl(UserDocboxPreferences.getDocboxBrowserHome());
		}
	}

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
		String ts = StringUtils.EMPTY + System.currentTimeMillis() / 1000;
		String username = UserDocboxPreferences.getDocboxLoginID(false);
		try {
			return "?ts=" + ts + "&loginId=" + URLEncoder.encode(username, "UTF-8") + "&page="
					+ URLEncoder.encode(page, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return StringUtils.EMPTY;
		}
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
