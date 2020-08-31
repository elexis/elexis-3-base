/*******************************************************************************
 * Copyright (c) 2010, Medelexis und Niklaus Giger <niklaus.giger@member.fsf.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    N. Giger - initial implementation
 * 
 *******************************************************************************/
package org.iatrix.bestellung.rose;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.exchange.XChangeException;
import ch.elexis.data.Anwender;
import ch.elexis.data.Artikel;
import ch.elexis.data.Bestellung;

/**
 * @author niklaus
 * 
 */
public class Test_Bestellung_Rose {
	
	/**
	 * @throws java.lang.Exception
	 */
	static Artikel a;
	static Bestellung order;
	final static String MEDIKAMENT = "Medikament"; //$NON-NLS-1$
	
	private static void setPharmacode(Artikel a, String aPharmaCode){
		Map<Object, Object> ext = a.getMap(Artikel.FLD_EXTINFO);
		ext.put(Artikel.FLD_PHARMACODE, aPharmaCode);
		a.setMap(Artikel.FLD_EXTINFO, ext);
		String pharmacode = a.getPharmaCode();
		System.out.println("a: " + pharmacode);
		assertTrue(pharmacode.length() > 3);
	}
	
	private static void createTestBestellungHorizontal(){
		/*
		 * See example under: https://estudio.clustertec.ch/schemas/order/examples/order.xml <order
		 * xsi:schemaLocation=
		 * "http://estudio.clustertec.ch/schemas/order http://estudio.clustertec.ch/schemas/order/order.xsd"
		 * user="test" password="test" deliveryType="1"> <product pharmacode="1234567"
		 * eanId="7600000000000" description="ASPIRIN" quantity="1" positionType="1"/> </order>
		 */
		a = new Artikel("ASPIRIN Tabl 500 mg Ad 20 Stk", "Medikament", "0");
		a.setEAN("7680085370118");
		setPharmacode(a, "0058910");
		Anwender anwender = new Anwender("007", "topsecret");
		order = new Bestellung("Test-Bestellung", anwender);
		order.addBestellungEntry(a, null, null, 1);
		
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception{}
	
	@Test
	public void testBestellung_1(){
		System.out.println("testBestellung_1 von niklaus am 11.6.2014");
		createTestBestellungHorizontal();
		System.out.println("testBestellung_2");
		System.out.println(order.toString());
		ConfigServiceHolder.setGlobal(Constants.CFG_ROSE_CLIENT_NUMBER, "999993");
		ConfigServiceHolder.setGlobal(Constants.CFG_ROSE_USERNAME, "elexis");
		ConfigServiceHolder.setGlobal(Constants.CFG_ROSE_PASSWORD, "elexis");
		Sender sender = null;
		System.out.println("testBestellung_3");
		try {
			sender = new Sender();
			sender.store(order);
		} catch (XChangeException e) {
			System.out.println(e.toString());
			// loading the class failed; do nothing
			fail("loading Sender class failed:" + e.toString());
		}
		try {
			if (sender != null) {
				sender.finalizeExport();
				// System.out.println("elem ist: "+XChangeElement.)
			}
		} catch (XChangeException e) {
			// loading the class failed; do nothing
			fail("finalizeExport failed:" + e.toString());
		}
		System.out.println("testBestellung_4");
		assertTrue(true); // finished sending
	}
	
}
