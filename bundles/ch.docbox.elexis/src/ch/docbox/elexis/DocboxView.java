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

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.equo.chromium.swt.Browser;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.data.Anwender;

/**
 * Creates a browser view which will sso into docbox and if a hospital
 * application is select patient info from elexis will be filled in
 */
public class DocboxView extends ViewPart {

	public static final String ID = "ch.docbox.elexis.DocboxView";
	private Browser browser;

	ElexisUiEventListenerImpl eeli_user = new ElexisUiEventListenerImpl(Anwender.class,
			ElexisEvent.EVENT_USER_CHANGED) {

		@Override
		public void runInUi(ElexisEvent ev) {
			userChanged();
		}
	};

	private String getDoboxLoginUrl() {
		return UserDocboxPreferences.getDocboxBrowserUrl();
	}

	@Override
	public void createPartControl(Composite parent) {
		ElexisEventDispatcher.getInstance().addListeners(eeli_user);
		browser = new Browser(parent, SWT.NATIVE);
		setHome();
	}

	@Override
	public void dispose() {
		ElexisEventDispatcher.getInstance().removeListeners(eeli_user);
		super.dispose();
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
		String signature = UserDocboxPreferences.getSSOSignature(ts);
		try {
			return "?ts=" + ts + "&loginId=" + URLEncoder.encode(username, "UTF-8") + "&sig="
					+ URLEncoder.encode(signature, "UTF-8") + "&page=" + URLEncoder.encode(page, "UTF-8");
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
