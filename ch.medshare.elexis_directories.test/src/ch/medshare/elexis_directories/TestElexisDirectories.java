/*******************************************************************************
 * Copyright (c) 2010, Elexis und Niklaus Giger <niklaus.giger@member.fsf.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    N. Giger - initial implementation
 * 
 * This is a generic test for importing HL7-files.
 * For each laboratory you should create a corresponding folder under rsc
 * and add (at least one) hl7 file(s).
 * 
 * The testHL7files will try to parse all hl7 files, but will not check the imported LabResult.
 * This should be enough in most cases.
 * 
 * However it might be a good idea to add a procedure (e.g. testAnalyticaHL7)  
 * if you have unusual requirements or stumbled over a bug in elexis HL7 parser.
 * 
 * Side-effects: Removes all patients & LabResults before & after running each test!
 * 
 *******************************************************************************/
package ch.medshare.elexis_directories;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import ch.elexis.core.data.util.PlatformHelper;
import ch.medshare.elexis.directories.DirectoriesContentParser;
import ch.medshare.elexis.directories.KontaktEntry;

public class TestElexisDirectories {
	
	private void compare_file_to_kontacts(File file, List<KontaktEntry> expectedKontakte)
		throws IOException{
		String content = FileUtils.readFileToString(file);
		DirectoriesContentParser parser = new DirectoriesContentParser(content);
		List<KontaktEntry> kontakte = parser.extractKontakte();
		if (expectedKontakte != null) {
			for (int j = 0; j < expectedKontakte.size(); j++) {
				KontaktEntry soll = expectedKontakte.get(j);
				KontaktEntry ist = kontakte.get(j);
				// we must compare the different fields
				Assert.assertEquals(soll.getName(), ist.getName());
				Assert.assertEquals(soll.getVorname(), ist.getVorname());
				Assert.assertEquals(soll.getZusatz(), ist.getZusatz());
				Assert.assertEquals(soll.getEmail(), ist.getEmail());
				Assert.assertEquals(soll.getAdresse(), ist.getAdresse());
				Assert.assertEquals(soll.getFax(), ist.getFax());
				Assert.assertEquals(soll.getOrt(), ist.getOrt());
				Assert.assertEquals(soll.getPlz(), ist.getPlz());
				Assert.assertEquals(soll.getTelefon(), ist.getTelefon());
			}
		}
	}
	
	private File get_ressource_path(){
		return new File(PlatformHelper.getBasePath("ch.medshare.elexis_directories.test"), "rsc");
	}

	/**
	 * Test method for {@link ch.elexis.importers.HL7#HL7(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void test_atupri_bern() throws IOException{
		File base = get_ressource_path();
		Assert.assertTrue(base.exists());
		File to_test = new File(base, "atupri_bern.html");
		Assert.assertTrue(to_test.exists());
		KontaktEntry atupri =
			new KontaktEntry("Krankenkasse Direktion", "Atupri", "Krankenkasse, Versicherung",
				"Zieglerstrasse 29", "3007", "Bern", "031 555 09 11", "", "", true);
		List<KontaktEntry> expectedKontakte = new ArrayList<KontaktEntry>();
		expectedKontakte.add(atupri);
		compare_file_to_kontacts(to_test, expectedKontakte);
	}
	
}
