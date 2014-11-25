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
package net.medshare.connector.viollier_test.cookie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.medshare.connector.viollier.data.ViollierConnectorSettings;
import net.medshare.connector.viollier.ses.PortalCookieService;

import org.junit.Test;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Mandant;

/**
 * JUnit Tests zum Viollier Connector Testet das Generieren des Cookies
 * 
 */
public class Test_getCookie {
	
	private ViollierConnectorSettings mySettings;
	
	/**
	 * Prüft, ob das vorhandene PDF auch tatsächlich in Omnivore abgelegt wird
	 */
	@Test
	public void Test01_CookieGenerieren(){
		try {
			
			if (mySettings == null)
				mySettings =
					new ViollierConnectorSettings(
						(Mandant) ElexisEventDispatcher.getSelected(Mandant.class));
			
			mySettings
				.setGlobalLoginUrl("https://portal-test.viollier.ch/login/sls/auth?cmd=loginrichclient");
			mySettings.setGlobalConsultItUrl("portal.viollier.ch/consultit");
			mySettings.setGlobalOrderItUrl("portal.viollier.ch/orderit");
			mySettings.setGlobalUserName("testde");
			mySettings.setGlobalUserPassword("testde");
			mySettings.setGlobalViollierClientId("57761");
			mySettings.setGlobalPreferedPresentation(true);
			
			mySettings.setMandantUsingGlobalSettings(true);
			mySettings.setMachineUsingGlobalSettings(true);
			
			mySettings.saveSettings();
			
			String cookie = new PortalCookieService().getCookie();
			assertEquals("Cookie holen fehlgeschlagen", 65, cookie.length());
		} catch (Exception e) {
			fail("Genereller Fehler (" + e.toString() + "): " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
}
