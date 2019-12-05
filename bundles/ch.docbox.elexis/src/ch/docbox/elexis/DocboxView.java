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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.data.Anwender;

/**
 * Creates a browser view which will sso into docbox and if a hospital application is select patient
 * info from elexis will be filled in
 */
public class DocboxView extends ViewPart implements ISaveablePart2 {
	
	public static final String ID = "ch.docbox.elexis.DocboxView";
	private Browser browser;
	
	ElexisUiEventListenerImpl eeli_user = new ElexisUiEventListenerImpl(Anwender.class,
		ElexisEvent.EVENT_USER_CHANGED) {
		
		public void runInUi(ElexisEvent ev){
			userChanged();
		}
	};
	
	private String getDoboxLoginUrl(){
		return UserDocboxPreferences.getDocboxBrowserUrl();
	}
	
	@Override
	public void createPartControl(Composite parent){
		ElexisEventDispatcher.getInstance().addListeners(eeli_user);
		browser = new Browser(parent, SWT.NATIVE);
		setHome();
	}
	
	@Override
	public void dispose(){
		ElexisEventDispatcher.getInstance().removeListeners(eeli_user);
		super.dispose();
	}
	
	void userChanged(){
		setHome();
	}
	
	public void setHome(){
		if (CoreHub.getActContact() != null && UserDocboxPreferences.hasValidDocboxCredentials()) {
			browser.setUrl(getDoboxLoginUrl() + getSSOLoginParams("MainWelcome"));
		} else {
			browser.setUrl(UserDocboxPreferences.getDocboxBrowserHome());
		}
	}
	
	public void setHospitalReferral(){
		if (CoreHub.getActContact() != null && UserDocboxPreferences.hasValidDocboxCredentials()) {
			browser.setUrl(getDoboxLoginUrl() + getSSOLoginParams("HospitalApplicationsOverview"));
		}
	}
	
	public void setMyPatient(){
		if (CoreHub.getActContact() != null && UserDocboxPreferences.hasValidDocboxCredentials()) {
			browser.setUrl(getDoboxLoginUrl() + getSSOLoginParams("MyPatient"));
		}
	}
	
	public void setTerminvereinbarung(){
		if (CoreHub.getActContact() != null && UserDocboxPreferences.hasValidDocboxCredentials()) {
			browser.setUrl(getDoboxLoginUrl() + getSSOLoginParams("AppBookingWizzard"));
		}
	}
	
	public void setAppointment(String termin){
		if (CoreHub.getActContact() != null && UserDocboxPreferences.hasValidDocboxCredentials()) {
			String url = getDoboxLoginUrl() + getSSOLoginParams("DocCalendar");
			if (termin != null) {
				try {
					url += "&id=" + URLEncoder.encode(termin, "UTF-8");
				} catch (UnsupportedEncodingException e) {}
			}
			browser.setUrl(url);
		}
	}
	
	private String getSSOLoginParams(String page){
		String ts = "" + System.currentTimeMillis() / 1000;
		String username = UserDocboxPreferences.getDocboxLoginID(false);
		String signature = UserDocboxPreferences.getSSOSignature(ts);
		try {
			return "?ts=" + ts + "&loginId=" + URLEncoder.encode(username, "UTF-8") + "&sig="
				+ URLEncoder.encode(signature, "UTF-8") + "&page="
				+ URLEncoder.encode(page, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	@Override
	public void setFocus(){
		
	}
	
	public int promptToSaveOnClose(){
		return 0;
	}
	
	public void doSave(IProgressMonitor monitor){}
	
	public boolean isDirty(){
		return false;
	}
	
	public boolean isSaveAsAllowed(){
		return false;
	}
	
	public boolean isSaveOnCloseNeeded(){
		return false;
	}
	
	public void doSaveAs(){
		// TODO Auto-generated method stub
		
	}
	
}
