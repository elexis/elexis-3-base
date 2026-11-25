package ch.elexis.base.ch.arzttarife.model.physio.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.model.test.AllTestsSuite;
import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.core.cdi.PortableServiceLoader;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.builder.ICoverageBuilder;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.IBillingSystemService;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.test.AbstractTest;
import ch.rgw.tools.Result;

public class PhysioBillingTest extends AbstractTest {

	@BeforeClass
	public static void beforeClass() {
		// create the positions referenced by physio_limit.csv (the 27xxx positions
		// are not part of the test database)
		IModelService modelService = AllTestsSuite.getModelService();
		createPosition(modelService, "27100", "Sitzungspauschale", "100");
		createPosition(modelService, "27110", "Physiotherapeutische Behandlung", "737");
		createPosition(modelService, "27610", "Elektrotherapie", "100");

		IReferenceDataImporter limitImporter = PortableServiceLoader.getService(IReferenceDataImporter.class,
				"(" + IReferenceDataImporter.REFERENCEDATAID + "=physiolimitation)").get();
		IStatus result = limitImporter.performImport(new NullProgressMonitor(),
				PhysioBillingTest.class.getResourceAsStream("/rsc/physio_limit.csv"), 1);
		assertTrue(result.isOK());
	}

	private static void createPosition(IModelService modelService, String ziffer, String text, String tp) {
		IPhysioLeistung leistung = modelService.create(IPhysioLeistung.class);
		leistung.setZiffer(ziffer);
		leistung.setText(text);
		leistung.setTP(tp);
		leistung.setValidFrom(LocalDate.of(2027, 1, 1));
		modelService.setEntityProperty("law", "KVG", leistung);
		modelService.save(leistung);
	}

	private IBillingSystemFactor factor;

	@After
	@Override
	public void after() {
		if (factor != null) {
			coreModelService.remove(factor);
		}
		super.after();
	}

	@Override
	public void createCoverage() {
		IBillingSystemService billingSystemService = PortableServiceLoader.get(IBillingSystemService.class);
		billingSystemService.addOrModifyBillingSystem("Physiotherapie", CONST_TARMED_DRUCKER, KVG_REQUIREMENTS,
				BillingLaw.KVG);

		factor = AllTestsSuite.createBillingSystemFactor("Physiotherapie", 0.89, LocalDate.of(2027, 1, 1));

		if (patient == null) {
			createPatient();
		}
		coverage = new ICoverageBuilder(coreModelService, patient, "testPhysio", "testReason", "Physiotherapie")
				.buildAndSave();
	}

	@Override
	public void createEncounter() {
		if (coverage == null) {
			createCoverage();
		}
		if (mandator == null) {
			createMandator();
		}
		encounter = new IEncounterBuilder(coreModelService, coverage, mandator).build();
		encounter.setDate(LocalDate.of(2027, 1, 1));
		coreModelService.save(encounter);
	}

	private IPhysioLeistung loadPosition(String code) {
		ICodeElementService codeElementService = PortableServiceLoader.get(ICodeElementService.class);
		ICodeElement codeElement = codeElementService
				.loadFromString("Physiotherapie", code, codeElementService.createContext(encounter)).get();
		assertTrue(codeElement instanceof IPhysioLeistung);
		return (IPhysioLeistung) codeElement;
	}

	private long billedCount(String code) {
		return encounter.getBilled().stream().filter(b -> code.equals(b.getCode())).count();
	}

	/**
	 * "Max. 50 Min. pro Sitzung" for 27110: the services are billed per 5
	 * minutes, so max. 10 units per encounter (Sitzung).
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void limitSessionMinutes() {
		createEncounter();

		IPhysioLeistung treatment = loadPosition("27110");
		Result<IBilled> result = treatment.getOptifier().add(treatment, encounter, 10);
		assertTrue(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());

		result = treatment.getOptifier().add(treatment, encounter, 1);
		assertFalse(result.isOK());
	}

	/**
	 * "Max. n Min. pro Sitzung" with n != 50: 27610 is limited to 30 minutes,
	 * i.e. 6 units per encounter.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void limitSessionMinutesOtherPosition() {
		createEncounter();

		IPhysioLeistung elektro = loadPosition("27610");
		Result<IBilled> result = elektro.getOptifier().add(elektro, encounter, 6);
		assertTrue(result.isOK());

		result = elektro.getOptifier().add(elektro, encounter, 1);
		assertFalse(result.isOK());
	}

	/**
	 * "Gilt als eine (1) Sitzung": billing a treatment position automatically
	 * bills the session marker 27100 with amount 1.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void autoBillSessionMarker() {
		createEncounter();

		IPhysioLeistung treatment = loadPosition("27110");
		Result<IBilled> result = treatment.getOptifier().add(treatment, encounter, 1);
		assertTrue(result.isOK());

		assertEquals(1, billedCount("27100"));
		IBilled marker = encounter.getBilled().stream().filter(b -> "27100".equals(b.getCode())).findFirst().get();
		assertEquals(1, marker.getAmount(), 0.01);
	}

	/**
	 * "Gilt als eine (1) Sitzung": the session marker is only billed once, if it
	 * is already billed on the encounter no additional marker is added.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void autoBillSessionMarkerOnce() {
		createEncounter();

		IPhysioLeistung marker = loadPosition("27100");
		Result<IBilled> result = marker.getOptifier().add(marker, encounter, 1);
		assertTrue(result.isOK());

		IPhysioLeistung treatment = loadPosition("27110");
		result = treatment.getOptifier().add(treatment, encounter, 1);
		assertTrue(result.isOK());

		assertEquals(1, billedCount("27100"));
	}

	/**
	 * Billing the session marker itself does not trigger an auto bill.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void sessionMarkerAlone() {
		createEncounter();

		IPhysioLeistung marker = loadPosition("27100");
		Result<IBilled> result = marker.getOptifier().add(marker, encounter, 1);
		assertTrue(result.isOK());
		assertEquals(1, encounter.getBilled().size());
	}
}
