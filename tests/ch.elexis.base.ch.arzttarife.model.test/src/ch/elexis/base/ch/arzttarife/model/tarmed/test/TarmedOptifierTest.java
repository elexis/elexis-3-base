package ch.elexis.base.ch.arzttarife.model.tarmed.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.model.test.AllTestsSuite;
import ch.elexis.base.ch.arzttarife.tarmed.MandantType;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedConstants;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedExtension;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedOptifier;
import ch.elexis.base.ch.arzttarife.tarmed.prefs.PreferenceConstants;
import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IDiagnosis;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.builder.IContactBuilder;
import ch.elexis.core.model.builder.ICoverageBuilder;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.holder.CodeElementServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.test.initializer.TestDatabaseInitializer;
import ch.elexis.core.types.Gender;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.rgw.tools.Money;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public class TarmedOptifierTest {

	public static final String LAW = "KVG";

	private static IModelService coreModelService;
	private static IMandator mandator;
	private static TarmedOptifier optifier;

	private static IPatient patGrissemann, patStermann, patOneYear, patBelow75;
	private static IEncounter konsGriss, konsSter, konsOneYear, konsBelow75;
	private static TarmedLeistung tlBaseFirst5Min, tlBaseXRay, tlBaseRadiologyHospital, tlUltrasound, tlAgeTo1Month,
			tlAgeTo7Years, tlAgeFrom7Years, tlGroupLimit1, tlGroupLimit2, tlAlZero;
	private static IEncounter konsPeriodStart, konsPeriodMiddle, konsPeriodEnd;

	private static ICodeElementServiceContribution tiCode;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		optifier = new TarmedOptifier();
		coreModelService = AllTestsSuite.getCoreModelService();
		LocalDate now = LocalDate.now();
		IPerson person = new IContactBuilder.PersonBuilder(coreModelService, "mandator1 " + now.toString(),
				"Anton" + now.toString(), now, Gender.MALE).mandator().buildAndSave();
		mandator = coreModelService.load(person.getId(), IMandator.class).get();

		tiCode = CodeElementServiceHolder.get().getContribution(CodeElementTyp.DIAGNOSE, "TI-Code").get();

		importTarmedReferenceData();

		// init some basic services
		tlBaseFirst5Min = TarmedLeistung.getFromCode("00.0010", LocalDate.now(), LAW);
		tlBaseXRay = TarmedLeistung.getFromCode("39.0020", LocalDate.now(), LAW);
		tlBaseRadiologyHospital = TarmedLeistung.getFromCode("39.0015", LocalDate.now(), LAW);
		tlUltrasound = TarmedLeistung.getFromCode("39.3005", LocalDate.now(), LAW);

		tlAgeTo1Month = TarmedLeistung.getFromCode("00.0870", LocalDate.now(), LAW);
		tlAgeTo7Years = TarmedLeistung.getFromCode("00.0900", LocalDate.now(), LAW);
		tlAgeFrom7Years = TarmedLeistung.getFromCode("00.0890", LocalDate.now(), LAW);

		tlGroupLimit1 = TarmedLeistung.getFromCode("02.0310", LocalDate.now(), LAW);
		tlGroupLimit2 = TarmedLeistung.getFromCode("02.0340", LocalDate.now(), LAW);

		tlAlZero = TarmedLeistung.getFromCode("00.0716", LocalDate.now(), LAW);

		// Patient Grissemann with case and consultation
		patGrissemann = new IContactBuilder.PatientBuilder(coreModelService, "Grissemann", "Christoph",
				LocalDate.of(1966, 05, 17), Gender.MALE).buildAndSave();
		ICoverage fallGriss = new ICoverageBuilder(coreModelService, patGrissemann, "Testfall Grissemann", "Krankheit",
				LAW).buildAndSave();
		// fallGriss.setInfoElement("Kostenträger", patGrissemann.getId());
		konsGriss = new IEncounterBuilder(coreModelService, fallGriss, mandator).buildAndSave();
		resetKons(konsGriss);

		// Patient Stermann with case and consultation
		patStermann = new IContactBuilder.PatientBuilder(coreModelService, "Stermann", "Dirk",
				LocalDate.of(1965, 7, 12), Gender.MALE).buildAndSave();
		ICoverage fallSter = new ICoverageBuilder(coreModelService, patStermann, "Testfall Stermann", "Krankheit", LAW)
				.buildAndSave();
		// fallSter.setInfoElement("Kostenträger", patStermann.getId());
		konsSter = new IEncounterBuilder(coreModelService, fallSter, mandator).buildAndSave();
		resetKons(konsSter);

		// Patient OneYear with case and consultation
		LocalDate dob = LocalDate.now().minusYears(1).minusDays(1);
		patOneYear = new IContactBuilder.PatientBuilder(coreModelService, "One", "Year", dob, Gender.MALE)
				.buildAndSave();
		ICoverage fallOneYear = new ICoverageBuilder(coreModelService, patOneYear, "Testfall One", "Krankheit", LAW)
				.buildAndSave();
		// fallOneYear.setInfoElement("Kostenträger", patOneYear.getId());
		konsOneYear = new IEncounterBuilder(coreModelService, fallOneYear, mandator).buildAndSave();
		resetKons(konsOneYear);

		// Patient below75 with case and consultation
		dob = LocalDate.now().minusYears(74).minusDays(350);
		patBelow75 = new IContactBuilder.PatientBuilder(coreModelService, "One", "Year", dob, Gender.MALE)
				.buildAndSave();
		ICoverage fallBelow75 = new ICoverageBuilder(coreModelService, patBelow75, "Testfall below 75", "Krankheit",
				LAW).buildAndSave();
		// fallBelow75.setCostBearer(patBelow75);
		konsBelow75 = new IEncounterBuilder(coreModelService, fallBelow75, mandator).buildAndSave();
		resetKons(konsBelow75);

		konsPeriodStart = new IEncounterBuilder(coreModelService, fallBelow75, mandator)
				.date(new TimeTool("01.01.2018").toLocalDateTime()).buildAndSave();
		resetKons(konsPeriodStart);

		konsPeriodMiddle = new IEncounterBuilder(coreModelService, fallBelow75, mandator)
				.date(new TimeTool("28.03.2018").toLocalDateTime()).buildAndSave();
		resetKons(konsPeriodMiddle);

		konsPeriodEnd = new IEncounterBuilder(coreModelService, fallBelow75, mandator)
				.date(new TimeTool("02.04.2018").toLocalDateTime()).buildAndSave();
		resetKons(konsPeriodEnd);

		// add custom exclusions
		IReferenceDataImporter customExclusionsImporter = OsgiServiceUtil.getService(IReferenceDataImporter.class,
				"(" + IReferenceDataImporter.REFERENCEDATAID + "=tarmedcustomexclusions)").get();
		customExclusionsImporter.performImport(new NullProgressMonitor(),
				IOUtils.toInputStream("00.0010,00.1345,E\n17.0090,00.0015,E", "UTF-8"), 0);

		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(mandator);
	}

	private static void importTarmedReferenceData() throws FileNotFoundException {
		// Importer not provided we import the raw db; set values that would have
		// // been set by the importer
		OsgiServiceUtil.getService(IConfigService.class).get().set(PreferenceConstants.CFG_REFERENCEINFO_AVAILABLE,
				true);
		// File tarmedFile = new File(System.getProperty("user.dir") + File.separator +
		// "rsc"
		// + File.separator + "tarmed.mdb");
		// InputStream tarmedInStream = new FileInputStream(tarmedFile);
		//
		// TarmedReferenceDataImporter importer = new TarmedReferenceDataImporter();
		// importer.suppressRestartDialog();
		// Status retStatus =
		// (Status) importer.performImport(new NullProgressMonitor(), tarmedInStream,
		// null);
		// assertEquals(IStatus.OK, retStatus.getCode());
	}

	private TarmedLeistung additionalService;
	private TarmedLeistung mainService;

	@Test
	public void mutipleBaseFirst5MinIsInvalid() {
		clearKons(konsGriss);
		Result<IBilled> resultGriss = optifier.add(tlBaseFirst5Min, konsGriss, 1.0);
		assertTrue(resultGriss.isOK());
		resultGriss = optifier.add(tlBaseFirst5Min, konsGriss, 1.0);
		assertFalse(resultGriss.isOK());
	}

	@Test
	public void testAddCompatibleAndIncompatible() {
		clearKons(konsGriss);
		Result<IBilled> resultGriss = optifier.add(TarmedLeistung.getFromCode("39.3005", LocalDate.now(), LAW),
				konsGriss, 1.0);
		assertTrue(resultGriss.isOK());
		resultGriss = optifier.add(TarmedLeistung.getFromCode("39.0020", LocalDate.now(), LAW), konsGriss, 1.0);
		assertFalse(resultGriss.isOK());
		resultGriss = optifier.add(TarmedLeistung.getFromCode("01.0110", LocalDate.now(), LAW), konsGriss, 1.0);
		assertTrue(resultGriss.isOK());
		resultGriss = optifier.add(TarmedLeistung.getFromCode("39.3830", LocalDate.now(), LAW), konsGriss, 1.0);
		assertTrue(resultGriss.isOK());
		resetKons(konsGriss);
	}

	@Test
	public void testAddMultipleIncompatible() {
		Result<IBilled> resultSter = optifier.add(tlBaseXRay, konsSter, 1.0);
		assertTrue(resultSter.toString(), resultSter.isOK());
		resultSter = optifier.add(tlUltrasound, konsSter, 1.0);
		assertFalse(resultSter.isOK());
		resultSter = optifier.add(tlBaseRadiologyHospital, konsSter, 1.0);
		assertFalse(resultSter.isOK());
	}

	@Test
	public void testIsCompatible() {
		Result<IBilled> resCompatible = optifier.isCompatible(tlBaseXRay, tlUltrasound, konsSter);
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
		resCompatible = optifier.isCompatible(tlBaseFirst5Min,
				TarmedLeistung.getFromCode("00.1345", LocalDate.now(), LAW), konsSter);
		assertFalse(resCompatible.isOK());
		resText = "";
		if (!resCompatible.getMessages().isEmpty()) {
			resText = resCompatible.getMessages().get(0).getText();
		}
		assertEquals("00.1345 nicht kombinierbar mit 00.0010, wegen Block Kumulation", resText);

		resCompatible = optifier.isCompatible(TarmedLeistung.getFromCode("01.0265", LocalDate.now(), LAW),
				TarmedLeistung.getFromCode("00.1345", LocalDate.now(), LAW), konsSter);
		assertTrue(resCompatible.isOK());

		resCompatible = optifier.isCompatible(TarmedLeistung.getFromCode("00.0510", LocalDate.now(), LAW),
				TarmedLeistung.getFromCode("03.0020", LocalDate.now(), LAW), konsSter);
		assertFalse(resCompatible.isOK());
		resText = "";
		if (!resCompatible.getMessages().isEmpty()) {
			resText = resCompatible.getMessages().get(0).getText();
		}
		assertEquals("03.0020 nicht kombinierbar mit 00.0510, wegen Block Kumulation", resText);

		resCompatible = optifier.isCompatible(TarmedLeistung.getFromCode("00.2510", LocalDate.now(), LAW),
				TarmedLeistung.getFromCode("03.0020", LocalDate.now(), LAW), konsSter);
		assertTrue(resCompatible.isOK());

		resetKons(konsSter);
	}

	@Test
	public void testSetBezug() {
		clearKons(konsSter);

		additionalService = TarmedLeistung.getFromCode("39.5010", LocalDate.now(), LAW);
		mainService = TarmedLeistung.getFromCode("39.5060", LocalDate.now(), LAW);
		// additional without main, not allowed
		Result<IBilled> resultSter = optifier.add(additionalService, konsSter, 1.0);
		assertFalse(resultSter.isOK());
		// additional after main, allowed
		resultSter = optifier.add(mainService, konsSter, 1.0);
		assertTrue(resultSter.isOK());
		assertTrue(getVerrechent(konsSter, mainService).isPresent());

		resultSter = optifier.add(additionalService, konsSter, 1.0);
		assertTrue(resultSter.isOK());
		assertTrue(getVerrechent(konsSter, additionalService).isPresent());

		// another additional, not allowed
		resultSter = optifier.add(additionalService, konsSter, 1.0);
		assertFalse(resultSter.toString(), resultSter.isOK());
		assertTrue(getVerrechent(konsSter, additionalService).isPresent());

		// remove, and add again
		Optional<IBilled> verrechnet = getVerrechent(konsSter, additionalService);
		assertTrue(verrechnet.isPresent());
		Result<IBilled> result = optifier.remove(verrechnet.get(), konsSter);
		assertTrue(result.isOK());
		resultSter = optifier.add(additionalService, konsSter, 1.0);
		assertTrue(resultSter.isOK());
		// add another main and additional
		resultSter = optifier.add(mainService, konsSter, 1.0);
		assertTrue(resultSter.isOK());
		assertTrue(getVerrechent(konsSter, mainService).isPresent());

		resultSter = optifier.add(additionalService, konsSter, 1.0);
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
	public void testOneYear() {
		Result<IBilled> result = optifier.add(tlAgeTo1Month, konsOneYear, 1.0);
		assertFalse(result.isOK());

		result = optifier.add(tlAgeTo7Years, konsOneYear, 1.0);
		assertTrue(result.isOK());

		result = optifier.add(tlAgeFrom7Years, konsOneYear, 1.0);
		assertFalse(result.isOK());
	}

	@Test
	public void testBelow75() {
		TarmedLeistung tl = TarmedLeistung.getFromCode("00.0020", LocalDate.now(), LAW);
		// add age restriction to 75 years with 0 tolerance, for the test, like in
		// tarmed 1.09
		Map<String, String> ext = tl.getExtension().getLimits();
		String origAgeLimits = ext.get(TarmedConstants.TarmedLeistung.EXT_FLD_SERVICE_AGE);
		ext.put(TarmedConstants.TarmedLeistung.EXT_FLD_SERVICE_AGE,
				origAgeLimits + (origAgeLimits.isEmpty() ? "-1|0|75|0|26[2006-04-01|2199-12-31]"
						: ", -1|0|75|0|26[2006-04-01|2199-12-31]"));

		TarmedExtension te = (TarmedExtension) tl.getExtension();
		te.setLimits(ext);

		Result<IBilled> result = optifier.add(tl, konsBelow75, 1.0);
		assertTrue(result.isOK());
		resetKons(konsBelow75);

		ext.put(TarmedConstants.TarmedLeistung.EXT_FLD_SERVICE_AGE, origAgeLimits);
		te.setLimits(ext);
	}

	@Test
	public void testGroupLimitation() {
		// limit on group 31 is 48 times per week
		resetKons(konsGriss);
		for (int i = 0; i < 24; i++) {
			Result<IBilled> result = billSingle(konsGriss, tlGroupLimit1);
			assertTrue(result.getMessages().toString(), result.isOK());
			assertEquals("02.0310", result.get().getCode());
		}
		assertEquals(2, konsGriss.getBilled().size());

		resetKons(konsSter);
		for (int i = 0; i < 24; i++) {
			Result<IBilled> result = billSingle(konsSter, tlGroupLimit2);
			assertTrue(result.isOK());
		}
		assertEquals(2, konsSter.getBilled().size());

		Result<IBilled> result = billSingle(konsGriss, tlGroupLimit2);
		assertTrue(result.isOK());

		result = billSingle(konsSter, tlGroupLimit1);
		assertTrue(result.isOK());

		for (int i = 0; i < 23; i++) {
			result = billSingle(konsGriss, tlGroupLimit1);
			assertTrue(result.isOK());
		}
		for (int i = 0; i < 23; i++) {
			result = billSingle(konsSter, tlGroupLimit2);
			assertTrue(result.isOK());
		}

		result = billSingle(konsGriss, tlGroupLimit2);
		assertFalse(result.isOK());

		result = billSingle(konsSter, tlGroupLimit1);
		assertFalse(result.isOK());

		resetKons(konsGriss);
		resetKons(konsSter);
	}

	private Result<IBilled> billSingle(IEncounter encounter, TarmedLeistung billable) {
		return AllTestsSuite.getBillingService().bill(billable, encounter, 1);
	}

	private static void resetKons(IEncounter kons) {
		clearKons(kons);
		kons.addDiagnosis((IDiagnosis) tiCode.loadFromCode("T1").get());
		CoreModelServiceHolder.get().save(kons);
		Result<IBilled> result = optifier.add(tlBaseFirst5Min, kons, 1.0);
		assertTrue(result.toString(), result.isOK());
	}

	@Test
	public void testDignitaet() {
		IEncounter kons = konsGriss;
		IBillingSystemFactor factor = AllTestsSuite.createBillingSystemFactor(
				konsGriss.getCoverage().getBillingSystem().getName(), 0.83, LocalDate.now().minus(1, ChronoUnit.DAYS));

		MandantType mandantType = ArzttarifeUtil.getMandantType(mandator);
		assertEquals(MandantType.SPECIALIST, mandantType);
		// default mandant type is specialist, factor 0.83 tarmed 1.09
		clearKons(kons);
		Result<IBilled> result = billSingle(kons, tlBaseFirst5Min);
		assertTrue(result.getMessages().toString(), result.isOK());
		IBilled billed = kons.getBilled().get(0);
		assertNotNull(billed);
		double amountAL = ArzttarifeUtil.getAL(billed);
		assertEquals(1042, amountAL, 0.01);
		Money amount = billed.getPrice();
		assertEquals(15.45, amount.getAmount(), 0.01);

		// set the mandant type to practitioner
		clearKons(kons);
		ArzttarifeUtil.setMandantType(kons.getMandator(), MandantType.PRACTITIONER);
		coreModelService.save(kons.getMandator());
		result = billSingle(kons, tlBaseFirst5Min);
		assertTrue(result.isOK());
		billed = kons.getBilled().get(0);
		assertNotNull(billed);
		amountAL = ArzttarifeUtil.getAL(billed);
		assertEquals(969, amountAL, 0.01);
		amount = billed.getPrice();
		assertEquals(14.84, amount.getAmount(), 0.01); // 10.42 * 0.83 * 0.93 + 8.19 * 0.83
		String alScalingFactor = (String) billed.getExtInfo("AL_SCALINGFACTOR");
		assertEquals("0.93", alScalingFactor);
		String alNotScaled = (String) billed.getExtInfo("AL_NOTSCALED");
		assertEquals("1042", alNotScaled);

		result = billSingle(kons, tlAlZero);
		assertTrue(result.isOK());
		billed = kons.getBilled().get(1);
		assertNotNull(billed);
		amountAL = ArzttarifeUtil.getAL(billed);
		assertEquals(0, amountAL, 0.01);
		amount = billed.getPrice();
		assertEquals(4.08, amount.getAmount(), 0.01); // 0.0 * 0.83 * 0.93 + 4.92 * 0.83
		alScalingFactor = (String) billed.getExtInfo("AL_SCALINGFACTOR");
		assertEquals("0.93", alScalingFactor);

		coreModelService.remove(factor);

		// set the mandant type to specialist, default factor 1.0 tarmed 1.09
		clearKons(kons);
		ArzttarifeUtil.setMandantType(kons.getMandator(), MandantType.SPECIALIST);
		coreModelService.save(kons.getMandator());
		result = billSingle(kons, tlBaseFirst5Min);
		assertTrue(result.isOK());
		billed = kons.getBilled().get(0);
		assertNotNull(billed);
		amountAL = ArzttarifeUtil.getAL(billed);
		assertEquals(1042, amountAL, 0.01);
		amount = billed.getPrice();
		assertEquals(18.61, amount.getAmount(), 0.01);
	}

	/**
	 * Test combination of session limit with coverage limit.
	 */
	@Test
	public void test9533() {
		clearKons(konsGriss);

		Result<IBilled> result = optifier.add(TarmedLeistung.getFromCode("02.0010", LocalDate.now(), LAW), konsGriss,
				1.0);
		assertTrue(result.isOK());

		result = optifier.add(TarmedLeistung.getFromCode("02.0010", LocalDate.now(), LAW), konsGriss, 1.0);
		assertTrue(result.isOK());

		result = optifier.add(TarmedLeistung.getFromCode("02.0010", LocalDate.now(), LAW), konsGriss, 1.0);
		assertTrue(result.isOK());

		resetKons(konsGriss);
	}

	@Test
	public void testIsCompatibleTarmedBilling() {
		clearKons(konsGriss);

		TarmedLeistung tlBaseXRay = TarmedLeistung.getFromCode("39.0020", null);
		TarmedLeistung tlUltrasound = TarmedLeistung.getFromCode("39.3005", null);
		TarmedLeistung tlBaseRadiologyHospital = TarmedLeistung.getFromCode("39.0015", null);
		TarmedLeistung tlBaseFirst5Min = TarmedLeistung.getFromCode("00.0010", null);

		Result<IBilled> resCompatible = optifier.isCompatible(tlBaseXRay, tlUltrasound, konsGriss);
		assertFalse(resCompatible.isOK());
		String resText = "";
		if (!resCompatible.getMessages().get(0).getText().isEmpty()) {
			resText = resCompatible.getMessages().get(0).getText();
		}
		assertEquals("39.3005 nicht kombinierbar mit Kapitel 39.01", resText);
		resCompatible = optifier.isCompatible(tlUltrasound, tlBaseXRay, konsGriss);
		assertTrue(resCompatible.isOK());

		resCompatible = optifier.isCompatible(tlBaseXRay, tlBaseRadiologyHospital, konsGriss);
		assertFalse(resCompatible.isOK());
		if (!resCompatible.getMessages().get(0).getText().isEmpty()) {
			resText = resCompatible.getMessages().get(0).getText();
		}
		assertEquals("39.0015 nicht kombinierbar mit Leistung 39.0020", resText);

		resCompatible = optifier.isCompatible(tlBaseRadiologyHospital, tlUltrasound, konsGriss);
		assertFalse(resCompatible.isOK());

		resCompatible = optifier.isCompatible(tlBaseXRay, tlBaseFirst5Min, konsGriss);
		assertTrue(resCompatible.isOK());

		resCompatible = optifier.isCompatible(tlBaseFirst5Min, tlBaseRadiologyHospital, konsGriss);
		assertTrue(resCompatible.isOK());

		resetKons(konsGriss);
	}

	/**
	 * Test exclusion with side.
	 */
	@Test
	public void testSideExclusion() {
		clearKons(konsGriss);

		Result<IBilled> result = optifier.add(TarmedLeistung.getFromCode("09.0930", LocalDate.now(), LAW), konsGriss,
				1.0);
		assertTrue(result.isOK());

		result = optifier.add(TarmedLeistung.getFromCode("09.0950", LocalDate.now(), LAW), konsGriss, 1.0);
		assertFalse(result.isOK());
		assertEquals(TarmedOptifier.EXKLUSIONSIDE, result.getCode());

		optifier.putContext(Constants.FLD_EXT_SIDE, Constants.SIDE_L);
		result = optifier.add(TarmedLeistung.getFromCode("09.0950", LocalDate.now(), LAW), konsGriss, 1.0);
		assertFalse(result.isOK());
		assertEquals(TarmedOptifier.EXKLUSIONSIDE, result.getCode());
		optifier.clearContext();

		optifier.putContext(Constants.FLD_EXT_SIDE, Constants.SIDE_R);
		result = optifier.add(TarmedLeistung.getFromCode("09.0950", LocalDate.now(), LAW), konsGriss, 1.0);
		assertTrue(result.isOK());
		optifier.clearContext();

		resetKons(konsGriss);
	}

	/**
	 * Test limit with side.
	 */
	@Test
	public void testSideLimit() {
		clearKons(konsGriss);

		Result<IBilled> result = optifier.add(TarmedLeistung.getFromCode("39.3408", LocalDate.now(), LAW), konsGriss,
				1.0);
		assertTrue(result.isOK());
		assertEquals(1, getLeistungAmount("39.3408", konsGriss));

		result = optifier.add(TarmedLeistung.getFromCode("39.3408", LocalDate.now(), LAW), konsGriss, 1.0);
		assertTrue(result.isOK());
		assertEquals(2, getLeistungAmount("39.3408", konsGriss));

		Set<String> sides = new HashSet<>();
		List<IBilled> leistungen = getLeistungen("39.3408", konsGriss);
		for (IBilled verrechnet : leistungen) {
			sides.add(TarmedLeistung.getSide(verrechnet));
		}
		assertEquals(2, sides.size());
		assertTrue(sides.contains(Constants.LEFT));
		assertTrue(sides.contains(Constants.RIGHT));

		result = optifier.add(TarmedLeistung.getFromCode("39.3408", LocalDate.now(), LAW), konsGriss);
		assertFalse(result.isOK());
		assertEquals(2, getLeistungAmount("39.3408", konsGriss));

		resetKons(konsGriss);
	}

	/**
	 * Test limit per patient.
	 */
	@Test
	public void testPatientLimit() {
		clearKons(konsOneYear);

		Result<IBilled> result = optifier.add(TarmedLeistung.getFromCode("03.0060", LocalDate.now(), LAW), konsOneYear,
				1.0);
		assertTrue(result.isOK());
		assertEquals(1, getLeistungAmount("03.0060", konsOneYear));

		result = optifier.add(TarmedLeistung.getFromCode("03.0060", LocalDate.now(), LAW), konsOneYear, 1.0);
		assertFalse(result.isOK());
		assertEquals(1, getLeistungAmount("03.0060", konsOneYear));

		resetKons(konsOneYear);
	}

	/**
	 * Test cleanup after kumulation warning.
	 */
	@Test
	public void testCleanUpAfterKumulation() {
		clearKons(konsGriss);

		Result<IBilled> result;
		for (int i = 0; i < 6; i++) {
			result = optifier.add(TarmedLeistung.getFromCode("00.0050", LocalDate.now(), LAW), konsGriss, 1.0);
			assertTrue(result.isOK());
		}
		result = optifier.add(TarmedLeistung.getFromCode("00.0050", LocalDate.now(), LAW), konsGriss, 1.0);
		assertFalse(result.isOK());
		assertEquals(6, konsGriss.getBilled().get(0).getAmount(), 0.01);

		clearKons(konsGriss);
		result = optifier.add(tlBaseFirst5Min, konsGriss, 1.0);
		assertTrue(result.isOK());
		result = optifier.add(TarmedLeistung.getFromCode("00.0020", LocalDate.now(), LAW), konsGriss, 1.0);
		assertTrue(result.isOK());
		result = optifier.add(TarmedLeistung.getFromCode("00.0020", LocalDate.now(), LAW), konsGriss, 1.0);
		assertTrue(result.isOK());
		result = optifier.add(TarmedLeistung.getFromCode("00.0030", LocalDate.now(), LAW), konsGriss, 1.0);
		assertTrue(result.isOK());
		result = optifier.add(tlBaseFirst5Min, konsGriss);
		assertFalse(result.isOK());
		assertEquals(1, getLeistungAmount("00.0010", konsGriss));
		result = optifier.add(TarmedLeistung.getFromCode("00.0020", LocalDate.now(), LAW), konsGriss, 1.0);
		assertFalse(result.isOK());
		assertEquals(2, getLeistungAmount("00.0020", konsGriss));

		resetKons(konsGriss);
	}

	@Test
	public void testKumulationSide() {
		clearKons(konsGriss);

		Result<IBilled> result = optifier.add(TarmedLeistung.getFromCode("20.0330", LocalDate.now(), LAW), konsGriss,
				1.0);
		assertEquals("l", result.get().getExtInfo("Seite"));
		assertTrue(result.isOK());
		result = optifier.add(TarmedLeistung.getFromCode("20.0330", LocalDate.now(), LAW), konsGriss, 1.0);
		assertFalse(result.isOK());

		clearKons(konsGriss);
	}

	/**
	 * Test of limitation per period currently Tarmed 1.08 00.0140 limit 10 per 3
	 * month. <br />
	 * With Tarmed 1.09 00.0141 should be used limit 30 per 3 month.
	 *
	 */
	@Test
	public void testLimitationPeriod() {
		clearKons(konsPeriodStart);
		clearKons(konsPeriodMiddle);
		clearKons(konsPeriodEnd);
		Result<IBilled> result = null;
		// start and middle are 1 period
		for (int i = 0; i < 15; i++) {
			result = optifier.add(TarmedLeistung.getFromCode("00.0141", LocalDate.now(), LAW), konsPeriodStart, 1.0);
			assertTrue(result.isOK());
		}
		for (int i = 0; i < 15; i++) {
			result = optifier.add(TarmedLeistung.getFromCode("00.0141", LocalDate.now(), LAW), konsPeriodMiddle, 1.0);
			assertTrue(result.isOK());
		}
		result = optifier.add(TarmedLeistung.getFromCode("00.0141", LocalDate.now(), LAW), konsPeriodMiddle, 1.0);
		assertFalse(result.isOK());
		// end is after period so middle is not included for limit
		for (int i = 0; i < 15; i++) {
			result = optifier.add(TarmedLeistung.getFromCode("00.0141", LocalDate.now(), LAW), konsPeriodEnd, 1.0);
			assertTrue(result.isOK());
		}

		clearKons(konsPeriodStart);
		clearKons(konsPeriodMiddle);
		clearKons(konsPeriodEnd);
	}

	@Test
	public void testAdditionalBlockExclusion() {
		clearKons(konsGriss);

		Result<IBilled> result = optifier.add(TarmedLeistung.getFromCode("17.0710", LocalDate.now(), LAW), konsGriss,
				1.0);
		assertTrue(result.isOK());
		result = optifier.add(TarmedLeistung.getFromCode("17.0740", LocalDate.now(), LAW), konsGriss, 1.0);
		assertTrue(result.isOK());
		// additional service, not in block LB-05, billing is allowed anyway
		result = optifier.add(TarmedLeistung.getFromCode("17.0750", LocalDate.now(), LAW), konsGriss, 1.0);
		assertTrue(result.isOK());

		clearKons(konsGriss);
	}

	@Test
	public void testCustomExclusion() {
		clearKons(konsGriss);

		Result<IBilled> result = optifier.add(TarmedLeistung.getFromCode("00.0010", LocalDate.now(), LAW), konsGriss,
				1.0);
		assertTrue(result.isOK());
		result = optifier.add(TarmedLeistung.getFromCode("00.0015", LocalDate.now(), LAW), konsGriss, 1.0);
		assertTrue(result.isOK());
		// not allowed due to custom exclusion
		result = optifier.add(TarmedLeistung.getFromCode("17.0090", LocalDate.now(), LAW), konsGriss, 1.0);
		assertFalse(result.isOK());
		assertEquals("17.0090 nicht kombinierbar mit Leistung 00.0015", result.getMessages().get(0).getText());

		// not allowed due to custom exclusion
		result = optifier.add(TarmedLeistung.getFromCode("00.1345", LocalDate.now(), LAW), konsGriss, 1.0);
		assertFalse(result.isOK());

		clearKons(konsGriss);
	}

	@Test
	public void testNoSave() {
		clearKons(konsGriss);
		Result<IBilled> resultGriss = optifier.add(tlBaseFirst5Min, konsGriss, 1.0, false);
		assertTrue(resultGriss.isOK());
		// nothing billed ...
		assertTrue(konsGriss.getBilled().isEmpty());
		coreModelService.refresh(konsGriss, true);
		assertTrue(konsGriss.getBilled().isEmpty());

		resultGriss = optifier.add(tlBaseFirst5Min, konsGriss, 1.0, true);
		assertTrue(resultGriss.isOK());
		// billed ...
		assertEquals(1, konsGriss.getBilled().size());
		coreModelService.refresh(konsGriss, true);
		assertEquals(1, konsGriss.getBilled().size());

		// parameter save can NOT be used to verify, as verification currently depends
		// on save
		resultGriss = optifier.add(tlBaseFirst5Min, konsGriss, 1.0, false);
		assertTrue(resultGriss.isOK());
		// nothing billed ...
		assertEquals(1, konsGriss.getBilled().size());
		coreModelService.refresh(konsGriss, true);
		assertEquals(1, konsGriss.getBilled().size());

	}

	@Test
	public void testBillNonIntAmount() {
		clearKons(konsGriss);
		// AL 11.20 TP TL 6.05 TP
		TarmedLeistung service = TarmedLeistung.getFromCode("02.0050", LocalDate.now(), LAW);
		Result<IBilled> resultGriss = optifier.add(service, konsGriss, 0.25, true);
		assertTrue(resultGriss.isOK());
		// 0.25 * 11.20 = 2,8
		Money alMoney = ArzttarifeUtil.getALMoney(resultGriss.get());
		assertEquals(2.8, alMoney.getAmount(), 0.0001);
		// 0.25 * 6.05 = 1,5125
		Money tlMoney = ArzttarifeUtil.getTLMoney(resultGriss.get());
		assertEquals(1.51, tlMoney.getAmount(), 0.0001);
		assertEquals(4.31, resultGriss.get().getTotal().doubleValue(), 0.0001);
	}

	private int getLeistungAmount(String code, IEncounter kons) {
		int ret = 0;
		for (IBilled leistung : kons.getBilled()) {
			if (leistung.getBillable().getCode().equals(code)) {
				ret += leistung.getAmount();
			}
		}
		return ret;
	}

	private List<IBilled> getLeistungen(String code, IEncounter kons) {
		List<IBilled> ret = new ArrayList<>();
		for (IBilled leistung : kons.getBilled()) {
			if (leistung.getBillable().getCode().equals(code)) {
				ret.add(leistung);
			}
		}
		return ret;
	}

	private static void clearKons(IEncounter kons) {
		coreModelService.remove(kons.getBilled());
		// remove does not update the reference list
		coreModelService.refresh(kons, true);
	}

	private Optional<IBilled> getVerrechent(IEncounter kons, TarmedLeistung leistung) {
		for (IBilled verrechnet : kons.getBilled()) {
			if (verrechnet.getBillable().getCode().equals(leistung.getCode())) {
				return Optional.of(verrechnet);
			}
		}
		return Optional.empty();
	}
}