package ch.elexis.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.model.IVerify;
import ch.elexis.core.model.IVerifyService;
import ch.elexis.core.verify.VerifyService;
import ch.elexis.data.importer.TarmedReferenceDataImporter;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.SEVERITY;

public class TarmedOptifierTest {
	private static TarmedOptifier optifier;
	private static Patient patGrissemann, patStermann;
	private static Konsultation konsGriss, konsSter, konsVs;
	private static TarmedLeistung tlBaseFirst5Min, tlBaseXRay, tlBaseRadiologyHospital,
			tlUltrasound, tlTapingCat1, tlSkullSono, tlBaseTech;
			
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		optifier = new TarmedOptifier();
		
		importTarmedReferenceData();
		
		// init some basic services
		tlBaseFirst5Min = (TarmedLeistung) TarmedLeistung.getFromCode("00.0010");
		tlBaseXRay = (TarmedLeistung) TarmedLeistung.getFromCode("39.0020");
		tlBaseRadiologyHospital = (TarmedLeistung) TarmedLeistung.getFromCode("39.0015");
		tlUltrasound = (TarmedLeistung) TarmedLeistung.getFromCode("39.3005");
		tlTapingCat1 = (TarmedLeistung) TarmedLeistung.getFromCode("01.0110");
		tlSkullSono = (TarmedLeistung) TarmedLeistung.getFromCode("39.3200");
		tlBaseTech = (TarmedLeistung) TarmedLeistung.getFromCode("39.3800");
		
		
		//Patient Grissemann with case and consultation
		patGrissemann = new Patient("Grissemann", "Christoph", "17.05.1966", Patient.MALE);
		Fall fallGriss = patGrissemann.neuerFall("Testfall Grissemann", Fall.getDefaultCaseReason(),
			Fall.getDefaultCaseLaw());
		fallGriss.setInfoElement("Kostenträger", patGrissemann.getId());
		konsGriss = new Konsultation(fallGriss);
		konsGriss.addDiagnose(TICode.getFromCode("T1"));
		konsGriss.addLeistung(tlBaseFirst5Min);
		
		//Patient Stermann with case and consultation
		patStermann = new Patient("Stermann", "Dirk", "07.12.1965", Patient.FEMALE);
		Fall fallSter = patStermann.neuerFall("Testfall Stermann", Fall.getDefaultCaseReason(),
			Fall.getDefaultCaseLaw());
		fallSter.setInfoElement("Kostenträger", patStermann.getId());
		konsSter = new Konsultation(fallSter);
		konsSter.addDiagnose(TICode.getFromCode("T1"));
		konsSter.addLeistung(tlBaseFirst5Min);
		
		konsVs = new Konsultation(fallSter);
		konsVs.addDiagnose(TICode.getFromCode("T1"));
		konsVs.addLeistung(tlBaseFirst5Min);
	}
	
	private static void importTarmedReferenceData() throws FileNotFoundException{
		File tarmedFile = new File(System.getProperty("user.dir") + File.separator + "rsc"
			+ File.separator + "tarmed.mdb");
		InputStream tarmedInStream = new FileInputStream(tarmedFile);
		
		TarmedReferenceDataImporter importer = new TarmedReferenceDataImporter();
		importer.suppressRestartDialog();
		Status retStatus =
			(Status) importer.performImport(new NullProgressMonitor(), tarmedInStream, null);
		assertEquals(IStatus.OK, retStatus.getCode());
	}
	
	@Test
	public void testAddCompatibleAndIncompatible(){
		Result<IVerrechenbar> resultGriss = optifier.add(tlUltrasound, konsGriss);
		assertTrue(resultGriss.isOK());
		resultGriss = optifier.add(tlBaseXRay, konsGriss);
		assertFalse(resultGriss.isOK());
		resultGriss = optifier.add(tlTapingCat1, konsGriss);
		assertTrue(resultGriss.isOK());
	}
	
	@Test
	public void testAddMultipleIncompatible(){
		Result<IVerrechenbar> resultSter = optifier.add(tlBaseXRay, konsSter);
		assertTrue(resultSter.isOK());
		resultSter = optifier.add(tlUltrasound, konsSter);
		assertFalse(resultSter.isOK());
		resultSter = optifier.add(tlBaseRadiologyHospital, konsSter);
		assertFalse(resultSter.isOK());
	}
	
	@Test
	public void testIsCompatible(){
		Result<IVerrechenbar> resCompatible = optifier.isCompatible(tlBaseXRay, tlUltrasound);
		assertFalse(resCompatible.isOK());
		String resText = "";
		if (!resCompatible.getMessages().isEmpty()) {
			resText = resCompatible.getMessages().get(0).getText();
		}
		assertEquals("39.3005 nicht kombinierbar mit 39.0020", resText);
		resCompatible = optifier.isCompatible(tlUltrasound, tlBaseXRay);
		assertTrue(resCompatible.isOK());
		
		resCompatible = optifier.isCompatible(tlBaseXRay, tlBaseRadiologyHospital);
		assertFalse(resCompatible.isOK());
		if (!resCompatible.getMessages().isEmpty()) {
			resText = resCompatible.getMessages().get(0).getText();
		}
		assertEquals("39.0015 nicht kombinierbar mit 39.0020", resText);
		
		resCompatible = optifier.isCompatible(tlBaseRadiologyHospital, tlUltrasound);
		assertFalse(resCompatible.isOK());
		
		resCompatible = optifier.isCompatible(tlBaseXRay, tlBaseFirst5Min);
		assertTrue(resCompatible.isOK());
		
		resCompatible = optifier.isCompatible(tlBaseFirst5Min, tlBaseRadiologyHospital);
		assertTrue(resCompatible.isOK());
		
		resCompatible = optifier.isCompatible(tlSkullSono, tlBaseTech);
		assertTrue(resCompatible.isOK());
	}
	
	@Test
	public void testUltraAutoBaseTech()
	{
		int sizeInit = konsSter.getLeistungen().size();
		konsSter.addLeistung(tlSkullSono);
		Assert.assertEquals(sizeInit + 2, konsSter.getLeistungen().size());
		boolean found = false;
		for (Verrechnet verrechnet : konsSter.getLeistungen())
		{
			if (verrechnet.getVerrechenbar().getCode().equals(tlBaseTech.getCode()))
			{
				found = true;
				break;
			}
		}
		Assert.assertTrue(found);	
	}
	
	@Test
	@Ignore("reference code implementation for sumex needed")
	public void testValidateSumexVsExlexis(){
		int leistungenSize = konsVs.getLeistungen().size();
		
		IVerifyService verifyService = new VerifyService();
		TarmedVerifyConverter tarmedVerifyConverter = new TarmedVerifyConverter();

		//1. case-----------------------------------------------------------------------------
		// sumex validation
		System.setProperty("sumexServerUrl", "http://172.18.0.11:9000");
		IVerify sumexResult =
			verifyService.validate(VerifyContext.create(konsVs, tarmedVerifyConverter),
				tarmedVerifyConverter.convert(tlUltrasound).get());
		assertEquals(IStatus.OK, sumexResult.getStatus().getSeverity());
		assertTrue(sumexResult.getValidatorId().contains("Sumex"));
		
		// elexis validation
		System.setProperty("sumexServerUrl", "");
		IVerify elexisResult =
			verifyService.validate(VerifyContext.create(konsVs, tarmedVerifyConverter),
				tarmedVerifyConverter.convert(tlUltrasound).get());
		assertTrue(elexisResult.getValidatorId().contains("Elexis"));
		assertEquals(IStatus.OK, elexisResult.getStatus().getSeverity());
		
		// check if no new leistungen was added
		assertEquals(leistungenSize, konsVs.getLeistungen().size());
		
		// finally add new leistung
		assertEquals(SEVERITY.OK, konsVs.addLeistung(tlUltrasound).getSeverity());
		leistungenSize = konsVs.getLeistungen().size();
		//------------------------------------------------------------------------------
		
		//2. case-----------------------------------------------------------------------------
		// sumex validation @TODO fails reference code implementation needed!)
		System.setProperty("sumexServerUrl", "http://172.18.0.11:9000");
		sumexResult = verifyService.validate(VerifyContext.create(konsVs, tarmedVerifyConverter),
			tarmedVerifyConverter.convert(tlBaseXRay).get());
		assertEquals(IStatus.ERROR, sumexResult.getStatus().getSeverity());
		assertTrue(sumexResult.getValidatorId().contains("Sumex"));
		
		// elexis validation
		System.setProperty("sumexServerUrl", "");
		elexisResult =
			verifyService.validate(VerifyContext.create(konsVs, tarmedVerifyConverter),
				tarmedVerifyConverter.convert(tlBaseXRay).get());
		assertTrue(elexisResult.getValidatorId().contains("Elexis"));
		assertEquals(IStatus.WARNING, elexisResult.getStatus().getSeverity());
		
		// check if no new leistungen was added
		assertEquals(leistungenSize, konsVs.getLeistungen().size());
		
		// check if both validators has the same result
		assertEquals(elexisResult.getStatus().getMessage(), sumexResult.getStatus().getMessage());
		
		// finally add new leistung
		assertEquals(SEVERITY.WARNING, konsVs.addLeistung(tlBaseXRay).getSeverity());
		assertEquals(leistungenSize, konsVs.getLeistungen().size());
		//------------------------------------------------------------------------------
	}
}
