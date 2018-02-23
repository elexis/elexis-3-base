package ch.elexis.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Hashtable;
import java.util.Optional;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.data.util.MultiplikatorList;
import ch.elexis.data.TarmedLeistung.MandantType;
import ch.elexis.data.importer.TarmedReferenceDataImporter;
import ch.rgw.tools.Money;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public class TarmedOptifierTest {
	private static TarmedOptifier optifier;
	private static Patient patGrissemann, patStermann, patOneYear, patBelow75;
	private static Konsultation konsGriss, konsSter, konsOneYear, konsBelow75;
	private static TarmedLeistung tlBaseFirst5Min, tlBaseXRay, tlBaseRadiologyHospital,
			tlUltrasound, tlAgeTo1Month, tlAgeTo7Years, tlAgeFrom7Years,
			tlGroupLimit1, tlGroupLimit2, tlAlZero;
			
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		optifier = new TarmedOptifier();
		
		importTarmedReferenceData();
		
		// init some basic services
		tlBaseFirst5Min =
			(TarmedLeistung) TarmedLeistung.getFromCode("00.0010", new TimeTool(), null);
		tlBaseXRay = (TarmedLeistung) TarmedLeistung.getFromCode("39.0020", new TimeTool(), null);
		tlBaseRadiologyHospital =
			(TarmedLeistung) TarmedLeistung.getFromCode("39.0015", new TimeTool(), null);
		tlUltrasound = (TarmedLeistung) TarmedLeistung.getFromCode("39.3005", new TimeTool(), null);
		
		tlAgeTo1Month =
			(TarmedLeistung) TarmedLeistung.getFromCode("00.0870", new TimeTool(), null);
		tlAgeTo7Years =
			(TarmedLeistung) TarmedLeistung.getFromCode("00.0900", new TimeTool(), null);
		tlAgeFrom7Years =
			(TarmedLeistung) TarmedLeistung.getFromCode("00.0890", new TimeTool(), null);
		
		tlGroupLimit1 =
			(TarmedLeistung) TarmedLeistung.getFromCode("02.0310", new TimeTool(), null);
		tlGroupLimit2 =
			(TarmedLeistung) TarmedLeistung.getFromCode("02.0340", new TimeTool(), null);
		
		tlAlZero = (TarmedLeistung) TarmedLeistung.getFromCode("00.0716", new TimeTool(), null);
		
		//Patient Grissemann with case and consultation
		patGrissemann = new Patient("Grissemann", "Christoph", "17.05.1966", Patient.MALE);
		Fall fallGriss = patGrissemann.neuerFall("Testfall Grissemann", Fall.getDefaultCaseReason(),
			Fall.getDefaultCaseLaw());
		fallGriss.setInfoElement("Kostenträger", patGrissemann.getId());
		konsGriss = new Konsultation(fallGriss);
		resetKons(konsGriss);
		
		//Patient Stermann with case and consultation
		patStermann = new Patient("Stermann", "Dirk", "07.12.1965", Patient.MALE);
		Fall fallSter = patStermann.neuerFall("Testfall Stermann", Fall.getDefaultCaseReason(),
			Fall.getDefaultCaseLaw());
		fallSter.setInfoElement("Kostenträger", patStermann.getId());
		konsSter = new Konsultation(fallSter);
		resetKons(konsSter);
		
		//Patient OneYear with case and consultation
		String dob = LocalDate.now().minusYears(1).minusDays(1)
			.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		patOneYear = new Patient("One", "Year", dob, Patient.MALE);
		Fall fallOneYear = patOneYear.neuerFall("Testfall One", Fall.getDefaultCaseReason(),
			Fall.getDefaultCaseLaw());
		fallOneYear.setInfoElement("Kostenträger", patOneYear.getId());
		konsOneYear = new Konsultation(fallOneYear);
		resetKons(konsOneYear);
		
		//Patient below75 with case and consultation
		dob = LocalDate.now().minusYears(74).minusDays(350)
			.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		patBelow75 = new Patient("One", "Year", dob, Patient.MALE);
		Fall fallBelow75 = patBelow75.neuerFall("Testfall below 75", Fall.getDefaultCaseReason(),
			Fall.getDefaultCaseLaw());
		fallBelow75.setInfoElement("Kostenträger", patBelow75.getId());
		konsBelow75 = new Konsultation(fallBelow75);
		resetKons(konsBelow75);
		
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
	
	private TarmedLeistung additionalService;
	private TarmedLeistung mainService;
	
	@Test
	public void testAddCompatibleAndIncompatible(){
		clearKons(konsGriss);
		Result<IVerrechenbar> resultGriss =
			optifier.add(TarmedLeistung.getFromCode("39.3005", new TimeTool(), null), konsGriss);
		assertTrue(resultGriss.isOK());
		resultGriss =
			optifier.add(TarmedLeistung.getFromCode("39.0020", new TimeTool(), null), konsGriss);
		assertFalse(resultGriss.isOK());
		resultGriss =
			optifier.add(TarmedLeistung.getFromCode("01.0110", new TimeTool(), null), konsGriss);
		assertTrue(resultGriss.isOK());
		resultGriss =
			optifier.add(TarmedLeistung.getFromCode("39.3830", new TimeTool(), null), konsGriss);
		assertTrue(resultGriss.isOK());
		resetKons(konsGriss);
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
		Result<IVerrechenbar> resCompatible =
			optifier.isCompatible(tlBaseXRay, tlUltrasound, konsSter);
		assertFalse(resCompatible.isOK());
		String resText = "";
		if (!resCompatible.getMessages().isEmpty()) {
			resText = resCompatible.getMessages().get(0).getText();
		}
		assertEquals("39.3005 nicht kombinierbar mit Kapitel 39.01", resText);
		resCompatible = optifier.isCompatible(tlUltrasound, tlBaseXRay, konsSter);
		assertTrue(resCompatible.isOK());
		
		resCompatible = optifier.isCompatible(tlBaseXRay, tlBaseRadiologyHospital, konsSter);
		assertFalse(resCompatible.isOK());
		if (!resCompatible.getMessages().isEmpty()) {
			resText = resCompatible.getMessages().get(0).getText();
		}
		assertEquals("39.0015 nicht kombinierbar mit Leistung 39.0020", resText);
		
		resCompatible = optifier.isCompatible(tlBaseRadiologyHospital, tlUltrasound, konsSter);
		assertFalse(resCompatible.isOK());
		
		resCompatible = optifier.isCompatible(tlBaseXRay, tlBaseFirst5Min, konsSter);
		assertTrue(resCompatible.isOK());
		
		resCompatible = optifier.isCompatible(tlBaseFirst5Min, tlBaseRadiologyHospital, konsSter);
		assertTrue(resCompatible.isOK());
		
		clearKons(konsSter);
		resCompatible = optifier.isCompatible(
			(TarmedLeistung) TarmedLeistung.getFromCode("00.0010", new TimeTool(), null),
			(TarmedLeistung) TarmedLeistung.getFromCode("00.1345", new TimeTool(), null), konsSter);
		assertFalse(resCompatible.isOK());
		resText = "";
		if (!resCompatible.getMessages().isEmpty()) {
			resText = resCompatible.getMessages().get(0).getText();
		}
		assertEquals("00.1345 nicht kombinierbar mit 00.0010, wegen Block Kumulation", resText);
		
		resCompatible = optifier.isCompatible(
			(TarmedLeistung) TarmedLeistung.getFromCode("01.0265", new TimeTool(), null),
			(TarmedLeistung) TarmedLeistung.getFromCode("00.1345", new TimeTool(), null), konsSter);
		assertTrue(resCompatible.isOK());
		
		resCompatible = optifier.isCompatible(
			(TarmedLeistung) TarmedLeistung.getFromCode("00.0510", new TimeTool(), null),
			(TarmedLeistung) TarmedLeistung.getFromCode("03.0020", new TimeTool(), null), konsSter);
		assertFalse(resCompatible.isOK());
		resText = "";
		if (!resCompatible.getMessages().isEmpty()) {
			resText = resCompatible.getMessages().get(0).getText();
		}
		assertEquals("03.0020 nicht kombinierbar mit 00.0510, wegen Block Kumulation", resText);
		
		resCompatible = optifier.isCompatible(
			(TarmedLeistung) TarmedLeistung.getFromCode("00.2510", new TimeTool(), null),
			(TarmedLeistung) TarmedLeistung.getFromCode("03.0020", new TimeTool(), null), konsSter);
		assertTrue(resCompatible.isOK());
		
		resetKons(konsSter);
	}
	
	@Test
	public void testSetBezug(){
		clearKons(konsSter);
		
		additionalService =
			(TarmedLeistung) TarmedLeistung.getFromCode("39.5010", new TimeTool(), null);
		mainService = (TarmedLeistung) TarmedLeistung.getFromCode("39.5060", new TimeTool(), null);
		// additional without main, not allowed
		Result<IVerrechenbar> resultSter = optifier.add(additionalService, konsSter);
		assertFalse(resultSter.isOK());
		// additional after main, allowed
		resultSter = optifier.add(mainService, konsSter);
		assertTrue(resultSter.isOK());
		assertTrue(getVerrechent(konsSter, mainService).isPresent());
		
		resultSter = optifier.add(additionalService, konsSter);
		assertTrue(resultSter.isOK());
		assertTrue(getVerrechent(konsSter, additionalService).isPresent());
		
		// another additional, not allowed
		resultSter = optifier.add(additionalService, konsSter);
		assertFalse(resultSter.isOK());
		assertTrue(getVerrechent(konsSter, additionalService).isPresent());
		
		// remove, and add again
		Optional<Verrechnet> verrechnet = getVerrechent(konsSter, additionalService);
		assertTrue(verrechnet.isPresent());
		Result<Verrechnet> result = optifier.remove(verrechnet.get(), konsSter);
		assertTrue(result.isOK());
		resultSter = optifier.add(additionalService, konsSter);
		assertTrue(resultSter.isOK());
		// add another main and additional
		resultSter = optifier.add(mainService, konsSter);
		assertTrue(resultSter.isOK());
		assertTrue(getVerrechent(konsSter, mainService).isPresent());
		
		resultSter = optifier.add(additionalService, konsSter);
		assertTrue(resultSter.isOK());
		assertTrue(getVerrechent(konsSter, additionalService).isPresent());
		
		// remove main service, should also remove additional service
		verrechnet = getVerrechent(konsSter, mainService);
		result = optifier.remove(verrechnet.get(), konsSter);
		assertTrue(result.isOK());
		assertFalse(getVerrechent(konsSter, mainService).isPresent());
		assertFalse(getVerrechent(konsSter, additionalService).isPresent());
		
		resetKons(konsSter);
	}
	
	@Test
	public void testOneYear(){
		Result<IVerrechenbar> result = optifier.add(tlAgeTo1Month, konsOneYear);
		assertFalse(result.isOK());
		
		result = optifier.add(tlAgeTo7Years, konsOneYear);
		assertTrue(result.isOK());
		
		result = optifier.add(tlAgeFrom7Years, konsOneYear);
		assertFalse(result.isOK());
	}
	
	@Test
	public void testBelow75(){
		TarmedLeistung tl =
			(TarmedLeistung) TarmedLeistung.getFromCode("00.0020", new TimeTool(), null);
		// add age restriction to 75 years with 0 tolerance, for the test, like in tarmed 1.09
		Hashtable<String, String> ext = tl.loadExtension();
		String origAgeLimits = ext.get(TarmedLeistung.EXT_FLD_SERVICE_AGE);
		ext.put(TarmedLeistung.EXT_FLD_SERVICE_AGE, origAgeLimits + (origAgeLimits.isEmpty()
				? "-1|0|75|0|26[2006-04-01|2199-12-31]" : ", -1|0|75|0|26[2006-04-01|2199-12-31]"));
		tl.setExtension(ext);
		
		Result<IVerrechenbar> result = optifier.add(tl, konsBelow75);
		assertTrue(result.isOK());
		resetKons(konsBelow75);
		
		ext.put(TarmedLeistung.EXT_FLD_SERVICE_AGE, origAgeLimits);
		tl.setExtension(ext);
	}
	
	@Test
	public void testGroupLimitation(){
		// limit on group 31 is 48 times per week
		resetKons(konsGriss);
		for (int i = 0; i < 24; i++) {
			Result<IVerrechenbar> result = konsGriss.addLeistung(tlGroupLimit1);
			assertTrue(result.isOK());
		}
		resetKons(konsSter);
		for (int i = 0; i < 24; i++) {
			Result<IVerrechenbar> result = konsSter.addLeistung(tlGroupLimit2);
			assertTrue(result.isOK());
		}
		
		Result<IVerrechenbar> result = konsGriss.addLeistung(tlGroupLimit2);
		assertTrue(result.isOK());
		
		result = konsSter.addLeistung(tlGroupLimit1);
		assertTrue(result.isOK());
		
		for (int i = 0; i < 23; i++) {
			result = konsGriss.addLeistung(tlGroupLimit1);
			assertTrue(result.isOK());
		}
		for (int i = 0; i < 23; i++) {
			result = konsSter.addLeistung(tlGroupLimit2);
			assertTrue(result.isOK());
		}
		
		result = konsGriss.addLeistung(tlGroupLimit2);
		assertFalse(result.isOK());
		
		result = konsSter.addLeistung(tlGroupLimit1);
		assertFalse(result.isOK());
		
		resetKons(konsGriss);
		resetKons(konsSter);
	}
	
	private static void resetKons(Konsultation kons){
		clearKons(kons);
		kons.addDiagnose(TICode.getFromCode("T1"));
		kons.addLeistung(tlBaseFirst5Min);
	}
	
	@Test
	public void testDignitaet(){
		Konsultation kons = konsGriss;
		setUpDignitaet(kons);
		
		// default mandant type is specialist
		clearKons(kons);
		Result<IVerrechenbar> result = kons.addLeistung(tlBaseFirst5Min);
		assertTrue(result.isOK());
		Verrechnet verrechnet = kons.getVerrechnet(tlBaseFirst5Min);
		assertNotNull(verrechnet);
		int amountAL = TarmedLeistung.getAL(verrechnet);
		assertEquals(1042, amountAL);
		Money amount = verrechnet.getNettoPreis();
		assertEquals(15.45, amount.getAmount(), 0.01);
		
		// set the mandant type to practitioner
		clearKons(kons);
		TarmedLeistung.setMandantType(kons.getMandant(), MandantType.PRACTITIONER);
		result = kons.addLeistung(tlBaseFirst5Min);
		assertTrue(result.isOK());
		verrechnet = kons.getVerrechnet(tlBaseFirst5Min);
		assertNotNull(verrechnet);
		amountAL = TarmedLeistung.getAL(verrechnet);
		assertEquals(969, amountAL);
		amount = verrechnet.getNettoPreis();
		assertEquals(14.84, amount.getAmount(), 0.01); // 10.42 * 0.83 * 0.93 + 8.19 * 0.83
		String alScalingFactor = verrechnet.getDetail("AL_SCALINGFACTOR");
		assertEquals("0.93", alScalingFactor);
		String alNotScaled = verrechnet.getDetail("AL_NOTSCALED");
		assertEquals("1042", alNotScaled);
		
		result = kons.addLeistung(tlAlZero);
		assertTrue(result.isOK());
		verrechnet = kons.getVerrechnet(tlAlZero);
		assertNotNull(verrechnet);
		amountAL = TarmedLeistung.getAL(verrechnet);
		assertEquals(0, amountAL);
		amount = verrechnet.getNettoPreis();
		assertEquals(4.08, amount.getAmount(), 0.01); // 0.0 * 0.83 * 0.93 + 4.92 * 0.83
		alScalingFactor = verrechnet.getDetail("AL_SCALINGFACTOR");
		assertEquals("0.93", alScalingFactor);
		
		tearDownDignitaet(kons);
		
		// set the mandant type to specialist
		clearKons(kons);
		TarmedLeistung.setMandantType(kons.getMandant(), MandantType.SPECIALIST);
		result = kons.addLeistung(tlBaseFirst5Min);
		assertTrue(result.isOK());
		verrechnet = kons.getVerrechnet(tlBaseFirst5Min);
		assertNotNull(verrechnet);
		amountAL = TarmedLeistung.getAL(verrechnet);
		assertEquals(957, amountAL);
		amount = verrechnet.getNettoPreis();
		assertEquals(17.76, amount.getAmount(), 0.01);
	}
	
	/**
	 * Test combination of session limit with coverage limit.
	 */
	@Test
	public void test9533(){
		clearKons(konsGriss);
		
		Result<IVerrechenbar> result = optifier.add(
			(TarmedLeistung) TarmedLeistung.getFromCode("02.0010", new TimeTool(), null),
			konsGriss);
		assertTrue(result.isOK());
		
		result = optifier.add(
			(TarmedLeistung) TarmedLeistung.getFromCode("02.0010", new TimeTool(), null),
			konsGriss);
		assertTrue(result.isOK());
		
		result = optifier.add(
			(TarmedLeistung) TarmedLeistung.getFromCode("02.0010", new TimeTool(), null),
			konsGriss);
		assertTrue(result.isOK());
		
		resetKons(konsGriss);
	}
	
	/**
	 * Test exclusion with side.
	 */
	@Test
	public void testSideExclusion(){
		clearKons(konsGriss);
		
		Result<IVerrechenbar> result = optifier.add(
			(TarmedLeistung) TarmedLeistung.getFromCode("09.0930", new TimeTool(), null),
			konsGriss);
		assertTrue(result.isOK());
		
		result = optifier.add(
			(TarmedLeistung) TarmedLeistung.getFromCode("09.0950", new TimeTool(), null),
			konsGriss);
		assertFalse(result.isOK());
		assertEquals(TarmedOptifier.EXKLUSIONSIDE, result.getCode());
		
		optifier.putContext(TarmedLeistung.SIDE, TarmedLeistung.SIDE_L);
		result = optifier.add(
			(TarmedLeistung) TarmedLeistung.getFromCode("09.0950", new TimeTool(), null),
			konsGriss);
		assertFalse(result.isOK());
		assertEquals(TarmedOptifier.EXKLUSIONSIDE, result.getCode());
		
		optifier.putContext(TarmedLeistung.SIDE, TarmedLeistung.SIDE_R);
		result = optifier.add(
			(TarmedLeistung) TarmedLeistung.getFromCode("09.0950", new TimeTool(), null),
			konsGriss);
		assertTrue(result.isOK());
		
		resetKons(konsGriss);
	}
	
	/**
	 * Test cleanup after kumulation warning.
	 */
	@Test
	public void testCleanUpAfterKumulation(){
		clearKons(konsGriss);
		
		Result<IVerrechenbar> result;
		for (int i = 0; i < 6; i++) {
			result = optifier.add(
				(TarmedLeistung) TarmedLeistung.getFromCode("00.0050", new TimeTool(), null),
				konsGriss);
			assertTrue(result.isOK());
		}
		result = optifier.add(
			(TarmedLeistung) TarmedLeistung.getFromCode("00.0050", new TimeTool(), null),
			konsGriss);
		assertFalse(result.isOK());
		assertEquals(6, konsGriss.getLeistungen().get(0).getZahl());
		
		resetKons(konsGriss);
	}
	
	private void setUpDignitaet(Konsultation kons){
		Hashtable<String, String> extension = tlBaseFirst5Min.loadExtension();
		// set reduce factor
		extension.put(TarmedLeistung.EXT_FLD_F_AL_R, "0.93");
		// the AL value
		extension.put(TarmedLeistung.EXT_FLD_TP_AL, "10.42");
		tlBaseFirst5Min.setExtension(extension);
		extension = tlAlZero.loadExtension();
		// set reduce factor
		extension.put(TarmedLeistung.EXT_FLD_F_AL_R, "0.93");
		// no AL value
		tlAlZero.setExtension(extension);
		
		// add additional multiplier
		LocalDate yesterday = LocalDate.now().minus(1, ChronoUnit.DAYS);
		MultiplikatorList multis =
			new MultiplikatorList("VK_PREISE", kons.getFall().getAbrechnungsSystem());
		multis.insertMultiplikator(new TimeTool(yesterday), "0.83");
		
	}
	
	private void tearDownDignitaet(Konsultation kons){
		Hashtable<String, String> extension = tlBaseFirst5Min.loadExtension();
		// clear reduce factor
		extension = tlBaseFirst5Min.loadExtension();
		extension.remove(TarmedLeistung.EXT_FLD_F_AL_R);
		// reset AL value
		extension.put(TarmedLeistung.EXT_FLD_TP_AL, "9.57");
		tlBaseFirst5Min.setExtension(extension);
		extension = tlAlZero.loadExtension();
		// clear reduce factor
		extension.remove(TarmedLeistung.EXT_FLD_F_AL_R);
		// no AL value
		tlAlZero.setExtension(extension);
		
		// remove additional multiplier
		LocalDate yesterday = LocalDate.now().minus(1, ChronoUnit.DAYS);
		MultiplikatorList multis =
			new MultiplikatorList("VK_PREISE", kons.getFall().getAbrechnungsSystem());
		multis.removeMultiplikator(new TimeTool(yesterday), "0.83");

	}
	
	private static void clearKons(Konsultation kons){
		for (Verrechnet verrechnet : kons.getLeistungen()) {
			kons.removeLeistung(verrechnet);
		}
	}
	
	private Optional<Verrechnet> getVerrechent(Konsultation kons, TarmedLeistung leistung){
		for (Verrechnet verrechnet : kons.getLeistungen()) {
			if (verrechnet.getCode().equals(leistung.getCode())) {
				return Optional.of(verrechnet);
			}
		}
		return Optional.empty();
	}
}
