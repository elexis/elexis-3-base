/*******************************************************************************
 * Copyright (c) 2007-2009, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation
 *    P. Chaubert - adapted to Messwerte V2
 *    medshare GmbH - adapted to Messwerte V2.1 in February 2012
 *
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2_test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Test;

import ch.elexis.core.data.util.PlatformHelper;

import com.hilotec.elexis.messwerte.v2.data.MessungKonfiguration;
import com.hilotec.elexis.messwerte.v2.data.MessungTyp;
import com.hilotec.elexis.messwerte.v2.data.typen.IMesswertTyp;

public class MessungKonfigurationTest {

	private static String TEST_XML = "rsc/messwerte_test.xml";
	private static String THIS_DESCR = "MessungKonfigurationTest: ";

	private static String TC1_BOOL_TRUE = "tc1_bool_true";
	private static String TC1_BOOL_FALSE = "tc1_bool_false";
	private static String TC1_CALC = "tc1_calc_error";
	private static String TC1_DATE = "tc1_date_20120129";
	private static String TC1_ENUM = "tc1_enum_BE";
	private static String TC1_NUM = "tc1_num_324";
	private static String TC1_SCALE = "tc1_scale_17";
	private static String TC1_STR = "tc1_str_hilotec";

	private static String TC2_BOOL = "tc2_bool";
	private static String TC2_CALC = "tc2_calc";
	private static String TC2_DATE = "tc2_date";
	private static String TC2_ENUM = "tc2_enum";
	private static String TC2_NUM = "tc2_num";
	private static String TC2_SCALE = "tc2_scale";
	private static String TC2_STR = "tc2_str";

	private static String TC3_BOOL = "tc3_bool";
	private static String TC3_CALC = "tc3_calc";
	private static String TC3_DATE = "tc3_date";
	private static String TC3_ENUM = "tc3_enum";
	private static String TC3_NUM = "tc3_num";
	private static String TC3_SCALE = "tc3_scale";
	private static String TC3_STR = "tc3_str";

	@Test
	public void testReadFromXML(){

		try {
			String testXML =
				PlatformHelper.getBasePath("com.hilotec.elexis.messwerte.v2_test") + File.separator
					+ TEST_XML;
			System.out.println(testXML);
			MessungKonfiguration testConfig = MessungKonfiguration.getInstance();
			assertFalse("read eines nicht existierenden files darf nicht true zurückgeben...",
				testConfig.readFromXML("dummy.xml"));

			ArrayList<MessungTyp> dataTypes = testConfig.getTypes();
			dataTypes.clear();
			assertTrue("test xml '" + testXML + "' kann nicht initialisiert werden",
				testConfig.readFromXML(testXML));
			for (int i = 0; i < dataTypes.size(); i++) {
				MessungTyp datatype = dataTypes.get(i);
				test_datatype(datatype);
				System.out.println(THIS_DESCR + "Teste datatype " + datatype.getName());
				ArrayList<IMesswertTyp> messwertTypes = dataTypes.get(i).getMesswertTypen();
				for (int j = 0; j < messwertTypes.size(); j++) {
					IMesswertTyp messWertTyp = messwertTypes.get(j);
					String messWertTypName = messWertTyp.getName();
					System.out.println(THIS_DESCR + "Teste datatype " + dataTypes.get(i).getName()
						+ ", " + messWertTypName + " (Typ: "
						+ messwertTypes.get(j).getClass().getName() + ")");

					// teste einzelne Datentypen
					if ("".equals(messWertTypName)) {
						fail("messWertName darf nicht leer sein");
					}
					// Bool Defaultwert
					else if (TC1_BOOL_TRUE.equals(messWertTypName)) {
						assertEquals(TC1_BOOL_TRUE + ": Falscher Titel für messWertTyp "
							+ messWertTyp.getName(), "Bool default true", messWertTyp.getTitle());
						assertTrue(TC1_BOOL_TRUE + ": Defaulwert falsch",
							Boolean.parseBoolean(messWertTyp.getDefault(null)));
					} else if (TC1_BOOL_FALSE.equals(messWertTypName)) {
						assertTrue(TC1_BOOL_TRUE + ": Defaulwert falsch",
							!Boolean.parseBoolean(messWertTyp.getDefault(null)));
					}
					// Bool berechnet aus Formel
					else if (TC2_BOOL.equals(messWertTypName)) {
						assertTrue(TC2_BOOL + ": Berechneter Wert falsch",
							Boolean.parseBoolean(messWertTyp.getDefault(null)));
					}
					// Bool Defaultwert weil Formel Exception wirft
					else if (TC3_BOOL.equals(messWertTypName)) {
						assertTrue(TC3_BOOL + ": Berechneter Wert falsch",
							!Boolean.parseBoolean(messWertTyp.getDefault(null)));
					}

					// Calc Defaultwert
					else if (TC1_CALC.equals(messWertTypName)) {
						System.out.println(THIS_DESCR + "TC1_CALC Default: "
							+ messWertTyp.getDefault(null));
						assertEquals(
							TC1_CALC + ": Falscher Titel für messWertTyp " + messWertTyp.getName(),
							"Calc default error", messWertTyp.getTitle());
						assertEquals(TC1_CALC + ": Default Wert falsch", "error",
							messWertTyp.getDefault(null));
					}
					// Calc berechnet aus Formel
					else if (TC2_CALC.equals(messWertTypName)) {
						assertEquals(TC2_CALC + ": Berechneter Wert falsch", 150,
							Integer.parseInt(messWertTyp.getDefault(null)));
					}
					// Calc Defaultwert weil Formel Exception wirft
					else if (TC3_CALC.equals(messWertTypName)) {
						assertEquals(TC3_CALC + ": Default Wert falsch", "exc",
							messWertTyp.getDefault(null));
					}

					// Counterfield
					// TC3

					// Datafield
					// TC3

					// Date Defaultwert
					else if (TC1_DATE.equals(messWertTypName)) {
						System.out.println(THIS_DESCR + "TEST_DATE Default: "
							+ messWertTyp.getDefault(null));
						assertEquals(
							TC1_DATE + ": Falscher Titel für messWertTyp " + messWertTyp.getName(),
							"Date default 29.01.2012", messWertTyp.getTitle());
						assertTrue(TC1_DATE + ": Defaulwert falsch",
							"29.01.2012".equals(messWertTyp.getDefault(null)));
					}
					// Date berechnet aus Formel
					else if (TC2_DATE.equals(messWertTypName)) {
						ch.rgw.tools.TimeTool yesterday = new ch.rgw.tools.TimeTool();
						yesterday.addDays(-1);
						String ist = new SimpleDateFormat("dd.MM.yyyy").format(yesterday.getTime());
						String soll =
							new SimpleDateFormat("dd.MM.yyyy").format(new ch.rgw.tools.TimeTool(
								messWertTyp.getDefault(null)).getTime());
						assertEquals(TC2_DATE + ": Berechneter Wert falsch", soll, ist);
					}
					// Date Defaultwert weil Formel Exception wirft
					else if (TC3_DATE.equals(messWertTypName)) {
						assertTrue(TC3_DATE + ": Defaulwert falsch",
							"11.11.2011".equals(messWertTyp.getDefault(null)));
					}

					// Enum Defaultwert
					else if (TC1_ENUM.equals(messWertTypName)) {
						System.out.println(THIS_DESCR + "TEST_ENUM Default: "
							+ messWertTyp.getDefault(null).toString());
						assertEquals(
							TC1_ENUM + ": Falscher Titel für messWertTyp " + messWertTyp.getName(),
							"Enum default BE", messWertTyp.getTitle());
						assertTrue(TC1_ENUM + ": Defaulwert falsch",
							"2".equals(messWertTyp.getDefault(null)));
					}
					// Enum berechnet aus Formel
					else if (TC2_ENUM.equals(messWertTypName)) {
						assertEquals(TC2_ENUM + ": Berechneter Wert falsch", 3,
							Integer.parseInt(messWertTyp.getDefault(null)));
					}
					// Enum Defaultwert weil Formel Exception wirft
					else if (TC3_ENUM.equals(messWertTypName)) {
						assertTrue(TC3_ENUM + ": Defaulwert falsch",
							"2".equals(messWertTyp.getDefault(null)));
					}

					// Num Defaultwert
					else if (TC1_NUM.equals(messWertTypName)) {
						System.out.println(THIS_DESCR + "TEST_NUM Default: "
							+ messWertTyp.getDefault(null));
						assertEquals(
							TC1_NUM + ": Falscher Titel für messWertTyp " + messWertTyp.getName(),
							"Num default 3.24", messWertTyp.getTitle());
						assertTrue(TC1_NUM + ": Defaulwert falsch",
							"3.24".equals(messWertTyp.getDefault(null)));
					}
					// Num berechnet aus Formel
					else if (TC2_NUM.equals(messWertTypName)) {
						assertEquals(TC2_NUM + ": Berechneter Wert falsch", "2.87",
							messWertTyp.getDefault(null));
					}
					// Num Defaultwert weil Formel Exception wirft
					else if (TC3_NUM.equals(messWertTypName)) {
						assertTrue(TC3_NUM + ": Defaulwert falsch",
							"3.24".equals(messWertTyp.getDefault(null)));
					}

					// Scale Defaultwert
					else if (TC1_SCALE.equals(messWertTypName)) {
						System.out.println(THIS_DESCR + "TC1_SCALE Default: "
							+ messWertTyp.getDefault(null));
						assertEquals(
							TC1_SCALE + ": Falscher Titel für messWertTyp " + messWertTyp.getName(),
							"Scale default 17", messWertTyp.getTitle());
						assertTrue(TC1_SCALE + ": Defaulwert falsch",
							"17".equals(messWertTyp.getDefault(null)));
					}
					// Scale berechnet aus Formel
					else if (TC2_SCALE.equals(messWertTypName)) {
						assertEquals(TC2_SCALE + ": Berechneter Wert falsch", "92",
							messWertTyp.getDefault(null));
					}
					// Scale Defaultwert weil Formel Exception wirft
					else if (TC3_SCALE.equals(messWertTypName)) {
						assertTrue(TC3_SCALE + ": Defaulwert falsch",
							"17".equals(messWertTyp.getDefault(null)));
					}

					// Str Defaultwert
					else if (TC1_STR.equals(messWertTypName)) {
						System.out.println(THIS_DESCR + "TC1_STR Default: "
							+ messWertTyp.getDefault(null));
						assertEquals(
							TC1_STR + ": Falscher Titel für messWertTyp " + messWertTyp.getName(),
							"Str default Hilotec", messWertTyp.getTitle());
						assertTrue(TC1_STR + ": Defaulwert falsch",
							"Hilotec".equals(messWertTyp.getDefault(null)));
					}
					// String berechnet aus Formel
					else if (TC2_STR.equals(messWertTypName)) {
						assertEquals(TC2_SCALE + ": Berechneter Wert falsch", "medshare",
							messWertTyp.getDefault(null));
					}
					// String Defaultwert weil Formel Exception wirft
					else if (TC3_STR.equals(messWertTypName)) {
						assertTrue(TC3_STR + ": Defaulwert falsch",
							"Hilotec".equals(messWertTyp.getDefault(null)));
					}

				}
			}
		} catch (Exception e) {
			fail("Genereller Fehler: " + e.getMessage());
		}
	}

	// einzelne Tests für Datentypen
	private void test_datatype(MessungTyp datatype){
		//
		if ("tc1".equals(datatype.getName())) {
			assertEquals("Falscher Titel für datatype " + datatype.getName(), "Testcase 1",
				datatype.getTitle());
			assertEquals("Falsche Description für datatype " + datatype.getName(),
				"Default Values für alle Datentypen", datatype.getDescription());
		}
	}

	@AfterClass
	public static void teardown() throws Exception{
		PlatformUI.getWorkbench().saveAllEditors(false); // do not confirm saving
		PlatformUI.getWorkbench().saveAll(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), PlatformUI.getWorkbench().getActiveWorkbenchWindow(), null, false);
		if (PlatformUI.getWorkbench() != null) // null if run from Eclipse-IDE
		{
			// needed if run as surefire test from using mvn install
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllPerspectives(false, true);
		}
	}

}
