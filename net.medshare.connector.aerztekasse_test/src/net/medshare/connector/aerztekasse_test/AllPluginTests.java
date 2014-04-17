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
package net.medshare.connector.aerztekasse_test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	net.medshare.connector.aerztekasse_test.invoice.Test_doHttpPost.class
})
public class AllPluginTests {
	public static Test suite() throws ClassNotFoundException{
		return new TestSuite("Ärztekasse Tests");
	}
}
