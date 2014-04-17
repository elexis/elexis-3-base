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
package ch.elexis.laborimport.viollier.v2_test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Unit-Tests zum Viollier Laborimporter
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
	ch.elexis.laborimport.viollier.v2_test.importer.Test_doImport.class
})
public class AllPluginTests {
	
	/**
	 * Eigentliche Testsuite
	 * 
	 * @return Testresultate
	 * @throws ClassNotFoundException
	 */
	public static Test suite() throws ClassNotFoundException{
		return new TestSuite("Laborimport Viollier Tests"); //$NON-NLS-1$
	}
}
