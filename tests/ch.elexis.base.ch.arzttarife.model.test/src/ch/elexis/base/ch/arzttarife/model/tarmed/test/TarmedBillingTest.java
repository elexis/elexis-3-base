package ch.elexis.base.ch.arzttarife.model.tarmed.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.model.test.AllTestsSuite;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung;
import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.core.jpa.entities.TarmedLeistung.MandantType;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.builder.IContactBuilder;
import ch.elexis.core.model.builder.ICoverageBuilder;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IBillingService;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.test.initializer.TestDatabaseInitializer;
import ch.elexis.core.test.matchers.IBillingMatch;
import ch.elexis.core.types.Gender;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public class TarmedBillingTest {

	private final IBillingService billingService = AllTestsSuite.getBillingService();
	private final IModelService coreModelService = AllTestsSuite.getCoreModelService();

	private TarmedLeistung code_000010 = TarmedLeistung.getFromCode("00.0010", "KVG");
	private TarmedLeistung code_000015 = TarmedLeistung.getFromCode("00.0015", "KVG");
	private TarmedLeistung code_000510 = TarmedLeistung.getFromCode("00.0510", "KVG");

	private IMandator mandator;
	private IPatient patient;
	private ICoverage coverage;
	private IEncounter encounter;

	private IBilled billed;
	private Result<IBilled> status;

	@Before
	public void before() {
		assertNotNull(code_000010);

		TimeTool timeTool = new TimeTool();
		IPerson _mandator = new IContactBuilder.PersonBuilder(coreModelService, "mandator1 " + timeTool.toString(),
				"Anton" + timeTool.toString(), timeTool.toLocalDate(), Gender.MALE).mandator().buildAndSave();
		mandator = coreModelService.load(_mandator.getId(), IMandator.class).get();
		patient = new IContactBuilder.PatientBuilder(coreModelService, "Armer", "Anton" + timeTool.toString(),
				timeTool.toLocalDate().minusYears(8), Gender.MALE).buildAndSave();
		coverage = new ICoverageBuilder(coreModelService, patient, "Fallbezeichnung", "Fallgrund", "KVG")
				.buildAndSave();
		encounter = new IEncounterBuilder(coreModelService, coverage, mandator).buildAndSave();
		encounter.setDate(LocalDate.of(2025, 12, 31));
		coreModelService.save(encounter);
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(mandator);
	}

	@After
	public void after() {
		coreModelService.remove(encounter);
		coreModelService.remove(coverage);
		coreModelService.remove(patient);
		coreModelService.remove(mandator);
		status = null;
		billed = null;
	}

	@Test
	public void basicTarmedPositions() {
		IBillingSystemFactor factor = AllTestsSuite.createBillingSystemFactor(coverage.getBillingSystem().getName(),
				0.89, LocalDate.of(2000, 1, 1));

		status = billingService.bill(code_000010, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		billed = status.get();
		assertNotNull(billed);
		assertEquals("00.0010", billed.getCode());

		assertEquals(code_000010.getText(), billed.getText());
		assertEquals(0.89, billed.getFactor(), 0.0001);
		assertEquals(1861, billed.getPoints());
		assertEquals(1656, billed.getPrice().getCents());
		coreModelService.remove(factor);

		assertEquals(100, billed.getPrimaryScale());
		assertEquals(100, billed.getSecondaryScale());

		assertEquals(encounter.getId(), billed.getEncounter().getId());
		assertEquals(1, billed.getAmount(), 0.01d);

		assertEquals(billed, encounter.getBilled().get(0));

		status = billingService.bill(code_000010, encounter, 1);
		assertFalse(status.isOK());

		status = billingService.bill(code_000015, encounter, 2);
		assertFalse(status.toString(), status.isOK());
		assertEquals(1, status.get().getAmount(), 0.01d);
		assertEquals(2, encounter.getBilled().size());

		status = billingService.bill(code_000510, encounter, 4);
		assertTrue(status.toString(), status.isOK());
		assertEquals(4, status.get().getAmount(), 0.01d);

		TarmedLeistung code_000750 = TarmedLeistung.getFromCode("00.0750", LocalDate.of(2025, 12, 31), null);
		assertNotNull(code_000750);
		status = billingService.bill(code_000750, encounter, 1);
		assertFalse(status.isOK());
	}

	@Test
	public void testDoNotBillTympanometrieTwicePerSideTicket5004() {
		ITarmedLeistung code_090510 = TarmedLeistung.getFromCode("09.0510", LocalDate.of(2025, 12, 31), null);
		assertNotNull(code_090510);

		Result<IBilled> status;
		status = billingService.bill(code_090510, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());

		IBilled billed = status.get();
		assertNotNull(billed);
		assertEquals(Constants.SIDE_L, billed.getExtInfo(Constants.FLD_EXT_SIDE));

		status = billingService.bill(code_090510, encounter, 1);
		assertTrue(status.isOK());
		billed = status.get();
		assertNotNull(billed);
		assertEquals(Constants.SIDE_R, billed.getExtInfo(Constants.FLD_EXT_SIDE));
		//
		status = billingService.bill(code_090510, encounter, 1);
		assertFalse(status.isOK());
	}

	@Test
	public void testAddAutoPositions() {
		ITarmedLeistung code_390590 = TarmedLeistung.getFromCode("39.0590", LocalDate.of(2025, 12, 31), null);

		status = billingService.bill(code_390590, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());

		List<IBillingMatch> matches = new ArrayList<>();
		matches.add(new IBillingMatch("39.0590-20141001", 1));
		matches.add(new IBillingMatch("39.2000-20180101-KVG", 1));
		matches.add(new IBillingMatch("39.0020-20180101-KVG", 1));
		IBillingMatch.assertMatch(encounter, matches);
	}

	@Test
	public void testAlPercentAdditionSpecialist() {

		assertEquals(MandantType.SPECIALIST, TarmedLeistung.getMandantType(mandator));

		status = billingService.bill(code_000010, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		billed = status.get();
		// unit_mt
		assertEquals(10.42, ArzttarifeUtil.getAL(billed) / 100, 0.00001);
		// scale_factor_mt
		double primaryScale = billed.getPrimaryScaleFactor();
		double scaleFactorMt = primaryScale;
		Optional<Double> scalingFactor = getALScalingFactor(billed);
		assertFalse(scalingFactor.isPresent());
		assertEquals(1.00, scaleFactorMt, 0.00001);
		assertEquals(100.0, code_000010.getALScaling(mandator), 0.0001);
		assertEquals(10.42, ArzttarifeUtil.getALMoney(billed).doubleValue(), 0.00001);
		
		status = billingService.bill(code_000015, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(TarmedLeistung.getFromCode("00.0020", "KVG"), encounter, 2);
		assertTrue(status.getMessages().toString(), status.isOK());

		status = billingService.bill(TarmedLeistung.getFromCode("00.2520", "KVG"), encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		IBilled additionalBilled1 = status.get();
		status = billingService.bill(TarmedLeistung.getFromCode("00.2530", "KVG"), encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		IBilled additionalBilled2 = status.get();
		billed = status.get();

		// unit_mt
		assertEquals(42.14, ArzttarifeUtil.getAL(billed) / 100, 0.00001);
		// scale_factor_mt
		primaryScale = billed.getPrimaryScaleFactor();
		scaleFactorMt = primaryScale;
		scalingFactor = getALScalingFactor(billed);
		assertFalse(scalingFactor.isPresent());
		assertEquals(0.25, scaleFactorMt, 0.00001);

		// remove incompatible first
		billingService.removeBilled(additionalBilled1, encounter);
		billingService.removeBilled(additionalBilled2, encounter);

		status = billingService.bill(TarmedLeistung.getFromCode("00.2540", "KVG"), encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		additionalBilled1 = status.get();
		status = billingService.bill(TarmedLeistung.getFromCode("00.2550", "KVG"), encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		additionalBilled2 = status.get();
		billed = status.get();

		// unit_mt
		assertEquals(42.14, ArzttarifeUtil.getAL(billed) / 100, 0.00001);
		// scale_factor_mt
		primaryScale = billed.getPrimaryScaleFactor();
		scaleFactorMt = primaryScale;
		scalingFactor = getALScalingFactor(billed);
		assertFalse(scalingFactor.isPresent());
		assertEquals(0.50, scaleFactorMt, 0.00001);

	}

	@Test
	public void testAlPercentAdditionPractitioner() {

		mandator.setExtInfo(ch.elexis.core.jpa.entities.TarmedLeistung.MANDANT_TYPE_EXTINFO_KEY,
				MandantType.PRACTITIONER.name());
		CoreModelServiceHolder.get().save(mandator);
		assertEquals(MandantType.PRACTITIONER, TarmedLeistung.getMandantType(mandator));
		assertEquals(mandator.getId(), encounter.getMandator().getId());
		CoreModelServiceHolder.get().refresh(encounter, true);
		assertEquals(MandantType.PRACTITIONER, TarmedLeistung.getMandantType(encounter.getMandator()));

		status = billingService.bill(code_000010, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		billed = status.get();
		// unit_mt
		assertTrue(getALNotScaled(billed).isPresent());
		assertEquals(10.42, getALNotScaled(billed).get() / 100, 0.00001);
		// scale_factor_mt
		double primaryScale = billed.getPrimaryScaleFactor();
		double scaleFactorMt = primaryScale;
		Optional<Double> scalingFactor = getALScalingFactor(billed);
		assertTrue(scalingFactor.isPresent());
		scaleFactorMt = scalingFactor.get() * primaryScale;
		assertEquals(0.93, scaleFactorMt, 0.00001);
		assertEquals(93.0, code_000010.getALScaling(mandator), 0.0001);
		assertEquals(10.42 * 0.93, ArzttarifeUtil.getALMoney(billed).doubleValue(), 0.005);

		status = billingService.bill(code_000015, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(TarmedLeistung.getFromCode("00.0020", "KVG"), encounter, 2);
		assertTrue(status.getMessages().toString(), status.isOK());

		status = billingService.bill(TarmedLeistung.getFromCode("00.2520", "KVG"), encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		IBilled additionalBilled1 = status.get();
		status = billingService.bill(TarmedLeistung.getFromCode("00.2530", "KVG"), encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		IBilled additionalBilled2 = status.get();
		billed = status.get();

		// unit_mt
		assertTrue(getALNotScaled(billed).isPresent());
		assertEquals(42.14, getALNotScaled(billed).get() / 100, 0.00001);
		// scale_factor_mt
		primaryScale = billed.getPrimaryScaleFactor();
		scaleFactorMt = primaryScale;
		scalingFactor = getALScalingFactor(billed);
		assertTrue(scalingFactor.isPresent());
		scaleFactorMt = scalingFactor.get() * primaryScale;
		assertEquals(0.25 * 0.93, scaleFactorMt, 0.00001);

		// remove incompatible first
		billingService.removeBilled(additionalBilled1, encounter);
		billingService.removeBilled(additionalBilled2, encounter);

		status = billingService.bill(TarmedLeistung.getFromCode("00.2540", "KVG"), encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		additionalBilled1 = status.get();
		status = billingService.bill(TarmedLeistung.getFromCode("00.2550", "KVG"), encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		additionalBilled2 = status.get();
		billed = status.get();

		// unit_mt
		assertTrue(getALNotScaled(billed).isPresent());
		assertEquals(42.14, getALNotScaled(billed).get() / 100, 0.00001);
		// scale_factor_mt
		primaryScale = billed.getPrimaryScaleFactor();
		scaleFactorMt = primaryScale;
		scalingFactor = getALScalingFactor(billed);
		assertTrue(scalingFactor.isPresent());
		scaleFactorMt = scalingFactor.get() * primaryScale;
		assertEquals(0.50 * 0.93, scaleFactorMt, 0.00001);

		// remove incompatible first
		billingService.removeBilled(additionalBilled1, encounter);
		billingService.removeBilled(additionalBilled2, encounter);

		status = billingService.bill(TarmedLeistung.getFromCode("04.0630", "KVG"), encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		additionalBilled1 = status.get();
		status = billingService.bill(TarmedLeistung.getFromCode("04.0620", "KVG"), encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		additionalBilled2 = status.get();
		billed = status.get();

		// unit_mt
		assertTrue(getALNotScaled(billed).isPresent());
		assertEquals(99.76, getALNotScaled(billed).get() / 100, 0.00001);
		// scale_factor_mt
		primaryScale = billed.getPrimaryScaleFactor();
		scaleFactorMt = primaryScale;
		scalingFactor = getALScalingFactor(billed);
		assertTrue(scalingFactor.isPresent());
		scaleFactorMt = scalingFactor.get() * primaryScale;
		assertEquals(0.70 * 0.93, scaleFactorMt, 0.00001);
		// external_factor_mt (1.0) * quantity (1.0) * TP(mt) (99.76) * TPV(mt) (1.0) *
		// scale_factor_mt (0.651)
		assertEquals(ArzttarifeUtil.getALMoney(billed).doubleValue(), billed.getSecondaryScaleFactor()
				* billed.getAmount() * getALNotScaled(billed).get() / 100 * billed.getFactor() * scaleFactorMt, 0.05);

		// remove incompatible first
		billingService.removeBilled(additionalBilled1, encounter);
		billingService.removeBilled(additionalBilled2, encounter);

		status = billingService.bill(TarmedLeistung.getFromCode("04.1910", "KVG"), encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		additionalBilled1 = status.get();
		status = billingService.bill(TarmedLeistung.getFromCode("04.1930", "KVG"), encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		additionalBilled2 = status.get();
		billed = status.get();

		// unit_mt
		assertTrue(getALNotScaled(billed).isPresent());
		assertEquals(162.51, getALNotScaled(billed).get() / 100, 0.00001);
		// scale_factor_mt
		primaryScale = billed.getPrimaryScaleFactor();
		scaleFactorMt = primaryScale;
		scalingFactor = getALScalingFactor(billed);
		assertTrue(scalingFactor.isPresent());
		scaleFactorMt = scalingFactor.get() * primaryScale;
		assertEquals(0.50 * 0.93, scaleFactorMt, 0.00001);
		// external_factor_mt (1.0) * quantity (1.0) * TP(mt) (162.51) * TPV(mt) (1.0) *
		// scale_factor_mt (0.465)
		assertEquals(ArzttarifeUtil.getALMoney(billed).doubleValue(), billed.getSecondaryScaleFactor()
				* billed.getAmount() * getALNotScaled(billed).get() / 100 * billed.getFactor() * scaleFactorMt, 0.05);

		// remove incompatible first
		billingService.removeBilled(additionalBilled1, encounter);
		billingService.removeBilled(additionalBilled2, encounter);

//		status = billingService.bill(TarmedLeistung.getFromCode("29.2010", "KVG"), encounter, 1);
//		assertTrue(status.getMessages().toString(), status.isOK());
//		additionalBilled1 = status.get();
//		status = billingService.bill(TarmedLeistung.getFromCode("29.2090", "KVG"), encounter, 1);
//		assertTrue(status.getMessages().toString(), status.isOK());
//		additionalBilled2 = status.get();
//		billed = status.get();
//
//		// unit_mt
//		assertTrue(getALNotScaled(billed).isPresent());
//		assertEquals(70.80, getALNotScaled(billed).get() / 100, 0.00001);
//		// scale_factor_mt
//		primaryScale = billed.getPrimaryScaleFactor();
//		scaleFactorMt = primaryScale;
//		scalingFactor = getALScalingFactor(billed);
//		assertTrue(scalingFactor.isPresent());
//		scaleFactorMt = scalingFactor.get() * primaryScale;
//		assertEquals(0.50 * 0.93, scaleFactorMt, 0.00001);
	}

	public static Optional<Double> getALScalingFactor(IBilled billed) {
		String scalingFactor = (String) billed.getExtInfo("AL_SCALINGFACTOR");
		if (scalingFactor != null && !scalingFactor.isEmpty()) {
			try {
				return Optional.of(Double.parseDouble(scalingFactor));
			} catch (NumberFormatException ne) {
				// return empty if not parseable
			}
		}
		return Optional.empty();
	}

	public static Optional<Double> getALNotScaled(IBilled billed) {
		String notScaled = (String) billed.getExtInfo("AL_NOTSCALED");
		if (notScaled != null && !notScaled.isEmpty()) {
			try {
				return Optional.of(Double.parseDouble(notScaled));
			} catch (NumberFormatException ne) {
				// return empty if not parseable
			}
		}
		return Optional.empty();
	}
}
