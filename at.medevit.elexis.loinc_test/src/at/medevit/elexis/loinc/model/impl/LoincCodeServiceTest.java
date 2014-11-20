package at.medevit.elexis.loinc.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.medevit.elexis.loinc.model.LoincCode;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Labor;

public class LoincCodeServiceTest {

	private static Kontakt labor1;
	private static Kontakt labor2;

	@After
	public void teardown() throws Exception{
		PlatformUI.getWorkbench().saveAllEditors(false); // do not confirm saving
		PlatformUI.getWorkbench().saveAll(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), PlatformUI.getWorkbench().getActiveWorkbenchWindow(), null, false);
		if (PlatformUI.getWorkbench() != null) // null if run from Eclipse-IDE
		{
			// needed if run as surefire test from using mvn install
			try {

				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllPerspectives(false, true);
			} catch (Exception e) {

				System.out.println(e.getMessage());
			}


		}
	}

	@BeforeClass
	public static void before() throws IOException{
		LoincCodeService service = new LoincCodeService();
		assertNotNull(service);
		service.importFromCsv(loadTop2000(), getFieldMapping());
		List<LoincCode> codes = service.getAllCodes();
		assertTrue(codes.size() > 0);

		labor1 = new Labor("Labor 1", "Labor test one");
		labor2 = new Labor("Labor 2", "Labor test two");
	}

	@AfterClass
	public static void after(){
		LoincCodeService service = new LoincCodeService();
		assertNotNull(service);
		List<LoincCode> codes = service.getAllCodes();
		for (LoincCode loincCode : codes) {
			loincCode.delete();
		}

		labor1.delete();
		labor2.delete();
	}

	@Test
	public void testGetByCode() throws IOException{
		LoincCodeService service = new LoincCodeService();

		LoincCode creatinine = service.getByCode("14682-9");
		assertNotNull(creatinine);
		assertEquals("Creatinine [Moles/volume] in Serum or Plasma",
			creatinine.get(LoincCode.FLD_LONGNAME));
		assertEquals("Creat SerPl-sCnc", creatinine.get(LoincCode.FLD_SHORTNAME));
		assertEquals("CHEM", creatinine.get(LoincCode.FLD_CLASS));
	}

	@Test
	public void testGetAll(){
		LoincCodeService service = new LoincCodeService();

		List<LoincCode> all = service.getAllCodes();
		assertTrue((all.size() > 1500) && (all.size() < 2500));
	}

	@Test
	public void testUpdateTop2000(){
		LoincCodeService service = new LoincCodeService();

		List<LoincCode> all = service.getAllCodes();
		for (LoincCode loincCode : all) {
			loincCode.delete();
		}

		all = service.getAllCodes();
		assertEquals(0, all.size());
		LoincCode.setTop2000Version("100.0.0");
		service.updateTop2000();

		all = service.getAllCodes();
		assertEquals(0, all.size());

		LoincCode.setTop2000Version("0.0.0");
		service.updateTop2000();

		all = service.getAllCodes();
		assertTrue((all.size() > 1500) && (all.size() < 2500));
	}

	private static InputStream loadTop2000(){
		return LoincCodeServiceTest.class
			.getResourceAsStream("/rsc/TOP_2000_COMMON_LAB_RESULTS_SI_LOINC_V1-1.CSV");
	}

	private static Map<Integer, String> getFieldMapping(){
		HashMap<Integer, String> ret = new HashMap<Integer, String>();
		ret.put(0, LoincCode.FLD_CODE);
		ret.put(1, LoincCode.FLD_LONGNAME);
		ret.put(2, LoincCode.FLD_SHORTNAME);
		ret.put(3, LoincCode.FLD_CLASS);
		return ret;
	}
}
