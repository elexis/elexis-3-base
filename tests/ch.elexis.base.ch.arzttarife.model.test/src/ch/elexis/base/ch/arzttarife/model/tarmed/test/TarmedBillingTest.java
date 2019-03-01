package ch.elexis.base.ch.arzttarife.model.tarmed.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.model.test.AllTestsSuite;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.builder.IContactBuilder;
import ch.elexis.core.model.builder.ICoverageBuilder;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.IBillingService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.test.matchers.IBillingMatch;
import ch.elexis.core.types.Gender;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public class TarmedBillingTest {
	
	private final IBillingService billingService = AllTestsSuite.getBillingService();
	private final IModelService coreModelService = AllTestsSuite.getCoreModelService();
	
	private TarmedLeistung code_000010 = TarmedLeistung.getFromCode("00.0010", "KVG");
	private TarmedLeistung code_000015 = TarmedLeistung.getFromCode("00.0015", "KVG");
	private TarmedLeistung code_000140 = TarmedLeistung.getFromCode("00.0140", "KVG");
	private TarmedLeistung code_000510 = TarmedLeistung.getFromCode("00.0510", "KVG");
	
	private IMandator mandator;
	private IPatient patient;
	private ICoverage coverage;
	private IEncounter encounter;
	
	private IBilled billed;
	private Result<IBilled> status;
	
	@Before
	public void before(){
		assertNotNull(code_000010);
		
		TimeTool timeTool = new TimeTool();
		IPerson _mandator =
			new IContactBuilder.PersonBuilder(coreModelService, "mandator1 " + timeTool.toString(),
				"Anton" + timeTool.toString(), timeTool.toLocalDate(), Gender.MALE).mandator()
					.buildAndSave();
		mandator = coreModelService.load(_mandator.getId(), IMandator.class).get();
		patient = new IContactBuilder.PatientBuilder(coreModelService, "Armer",
			"Anton" + timeTool.toString(), timeTool.toLocalDate(), Gender.MALE).buildAndSave();
		coverage =
			new ICoverageBuilder(coreModelService, patient, "Fallbezeichnung", "Fallgrund", "KVG")
				.buildAndSave();
		encounter = new IEncounterBuilder(coreModelService, coverage, mandator).buildAndSave();
	}
	
	@After
	public void after(){
		coreModelService.remove(encounter);
		coreModelService.remove(coverage);
		coreModelService.remove(patient);
		coreModelService.remove(mandator);
		status = null;
		billed = null;
	}
	
	@Test
	public void basicTarmedPositions(){
		status = billingService.bill(code_000010, encounter, 1);
		assertTrue(status.isOK());
		billed = status.get();
		assertNotNull(billed);
		assertEquals("00.0010-20180101-KVG", billed.getCode());
		
		assertEquals(code_000010.getText(), billed.getText());
		//		assertEquals("0.89", vr.getVk_scale());
		//		assertEquals(1776, vr.getVk_tp());
		//				assertEquals(1581, vr.getVk_preis());
		// TODO billed.getPoints is 0

		assertEquals(1.0, billed.getFactor(), 0.01);
		assertEquals(100, billed.getPrimaryScale());
		assertEquals(100, billed.getSecondaryScale());
		
		assertEquals(encounter.getId(), billed.getEncounter().getId());
		assertEquals(1, billed.getAmount(), 0.01d);
		
		assertEquals(billed, encounter.getBilled().get(0));
		
		status = billingService.bill(code_000010, encounter, 1);
		assertFalse(status.isOK());
		
		status = billingService.bill(code_000015, encounter, 2);
		assertTrue(status.toString(), status.isOK());
		assertEquals(2, encounter.getBilled().size());
		
		TarmedLeistung code_000750 = TarmedLeistung.getFromCode("00.0750", LocalDate.now(), null);
		assertNotNull(code_000750);
		status = billingService.bill(code_000750, encounter, 1);
		assertFalse(status.isOK());
	
		fail("missing points");
	}
	
	@Test
	public void testDoNotBillTympanometrieTwicePerSideTicket5004(){
		ITarmedLeistung code_090510 = TarmedLeistung.getFromCode("09.0510", LocalDate.now(), null);
		assertNotNull(code_090510);
		
		Result<IBilled> status;
		status = billingService.bill(code_090510, encounter, 1);
		assertTrue(status.isOK());
		
		IBilled billed = status.get();
		assertNotNull(billed);
		assertEquals(Constants.SIDE_L, billed.getExtInfo(Constants.FLD_EXT_SIDE));
		
		status = billingService.bill(code_090510, encounter, 1);
		assertTrue(status.isOK());
		billed = status.get();
		assertNotNull(billed);
		assertEquals(Constants.SIDE_R,
			billed.getExtInfo(Constants.FLD_EXT_SIDE));
		//
		status = billingService.bill(code_090510, encounter, 1);
		assertFalse(status.isOK());
	}
	
	@Test
	public void testAddAutoPositions(){
		ITarmedLeistung code_390590 = TarmedLeistung.getFromCode("39.0590", LocalDate.now(), null);
		
		status = billingService.bill(code_390590, encounter, 1);
		assertTrue(status.isOK());
		
		List<IBillingMatch> matches = new ArrayList<>();
		matches.add(new IBillingMatch("39.0590-20141001", 1));
		matches.add(new IBillingMatch("39.2000-20180101-KVG", 1));
		matches.add(new IBillingMatch("39.0020-20180101-KVG", 1));
		IBillingMatch.assertMatch(encounter, matches);
	}
	
	
	
}
