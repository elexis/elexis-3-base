/*******************************************************************************
 * 
 * The authorship of this code and the accompanying materials is held by 
 * medshare GmbH, Switzerland. All rights reserved. 
 * http://medshare.net
 * 
 * This code and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0
 * 
 * Year of publication: 2012
 * 
 *******************************************************************************/
package net.medshare.connector.viollier_test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Unit-Tests zum Viollier Portal Connector
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
	net.medshare.connector.viollier_test.cookie.Test_getCookie.class
})
public class AllPluginTests {
	
	/**
	 * Eigentliche Testsuite
	 * 
	 * @return Testresultate
	 * @throws ClassNotFoundException
	 */
	public static Test suite() throws ClassNotFoundException{
		return new TestSuite("Viollier Portal Connector Tests"); //$NON-NLS-1$
	}
}
