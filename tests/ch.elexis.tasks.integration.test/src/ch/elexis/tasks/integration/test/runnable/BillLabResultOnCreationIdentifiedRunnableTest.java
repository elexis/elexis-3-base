package ch.elexis.tasks.integration.test.runnable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.elexis.core.importer.div.tasks.BillLabResultOnCreationIdentifiedRunnable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.builder.IContactBuilder;
import ch.elexis.core.model.builder.ICoverageBuilder;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.model.builder.ILabResultBuilder;
import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.IIdentifiedRunnable.ReturnParameter;
import ch.elexis.core.model.tasks.IIdentifiedRunnable.RunContextParameter;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.EncounterServiceHolder;
import ch.elexis.core.types.Gender;
import ch.elexis.tasks.integration.test.AllTests;
import ch.elexis.tasks.integration.test.internal.TaskServiceHolder;

public class BillLabResultOnCreationIdentifiedRunnableTest {

	IIdentifiedRunnable billLabResultRunnable;

	@Before
	public void before() throws TaskException {
		ContextServiceHolder.get().setActiveUser(AllTests.getUser());
		ContextServiceHolder.get().setActiveMandator(AllTests.getMandator());
		IEncounter encounter = EncounterServiceHolder.get().getLatestEncounter(AllTests.getPatient()).get();
		List<IBilled> billed = encounter.getBilled();
		for (IBilled iBilled : billed) {
			CoreModelServiceHolder.get().remove(iBilled);
		}

		billLabResultRunnable = TaskServiceHolder.get()
				.instantiateRunnableById(BillLabResultOnCreationIdentifiedRunnable.RUNNABLE_ID);
		assertNotNull(billLabResultRunnable);
	}

	@After
	public void after() {
		ContextServiceHolder.get().setActiveMandator(null);
		ContextServiceHolder.get().setActiveUser(null);
	}

	@Test
	public void billOnNonEditableEncounter() throws TaskException {

		IEncounter encounter = EncounterServiceHolder.get().getLatestEncounter(AllTests.getPatient()).get();
		CoreModelServiceHolder.get().refresh(encounter, true);
		assertEquals(0, encounter.getBilled().size());

		// close coverage, not mitigatable
		ICoverage coverage = AllTests.getCoverage();
		coverage.setDateTo(LocalDate.now());
		CoreModelServiceHolder.get().save(coverage);

		ILabResult labResult = new ILabResultBuilder(CoreModelServiceHolder.get(), AllTests.getLabItem(),
				AllTests.getPatient()).build();

		labResult.setObservationTime(LocalDateTime.of(2016, Month.DECEMBER, 14, 17, 44, 25));
		labResult.setOrigin(AllTests.getLaboratory());
		labResult.setResult("2");
		labResult.setComment("no comment");
		CoreModelServiceHolder.get().save(labResult);

		Map<String, Serializable> runContext = new HashMap<>();
		runContext.putAll(billLabResultRunnable.getDefaultRunContext());
		runContext.put(BillLabResultOnCreationIdentifiedRunnable.Parameters.BOOLEAN_AUTO_ADD_BILLABLE_ENCOUNTER,
				Boolean.FALSE);
		runContext.put(RunContextParameter.IDENTIFIABLE_ID, labResult.getId());

		Map<String, Serializable> result = billLabResultRunnable.run(runContext, null,
				LoggerFactory.getLogger(getClass()));
		assertTrue(result.containsKey(ReturnParameter.MARKER_WARN));

		assertEquals(0, encounter.getBilled().size());

		// cleanup
		coverage = AllTests.getCoverage();
		coverage.setDateTo(null);
		CoreModelServiceHolder.get().save(coverage);
	}

	@Test
	public void billOnMitigatableNonEditableEncounter() throws TaskException {

		IEncounter encounter = EncounterServiceHolder.get().getLatestEncounter(AllTests.getPatient()).get();
		CoreModelServiceHolder.get().refresh(encounter, true);
		String closedEncounterId = encounter.getId();
		assertEquals(0, encounter.getBilled().size());

		// invoice the encounter, this should be mitigatable
		IInvoice invoice = CoreModelServiceHolder.get().create(IInvoice.class);
		invoice.setCoverage(encounter.getCoverage());
		invoice.setDate(LocalDate.now());
		invoice.setDateFrom(LocalDate.now());
		invoice.setDateTo(LocalDate.now());
		CoreModelServiceHolder.get().save(invoice);
		encounter.setInvoice(invoice);
		CoreModelServiceHolder.get().save(encounter);

		ILabResult labResult = new ILabResultBuilder(CoreModelServiceHolder.get(), AllTests.getLabItem(),
				AllTests.getPatient()).build();

		labResult.setObservationTime(LocalDateTime.of(2016, Month.DECEMBER, 14, 17, 44, 25));
		labResult.setOrigin(AllTests.getLaboratory());
		labResult.setResult("2");
		labResult.setComment("no comment");
		CoreModelServiceHolder.get().save(labResult);

		Map<String, Serializable> runContext = new HashMap<>();
		runContext.putAll(billLabResultRunnable.getDefaultRunContext());
		runContext.put(RunContextParameter.IDENTIFIABLE_ID, labResult.getId());

		Map<String, Serializable> result = billLabResultRunnable.run(runContext, null,
				LoggerFactory.getLogger(getClass()));
		assertFalse(result.containsKey(ReturnParameter.MARKER_WARN));

		encounter = EncounterServiceHolder.get().getLatestEncounter(AllTests.getPatient()).get();
		assertNotEquals(closedEncounterId, encounter.getId());
		assertEquals(1, encounter.getBilled().size());
		CoreModelServiceHolder.get().remove(encounter);
	}

	// see #22266
	// Test-Case: Es sind mehrere Konsultationen am selben Tag offen
	@Test
	public void billOnCorrectEncounterMultipleOpenKonsSameDay() throws TaskException {
		// -- Primär soll auf die Konsultation mit dem Gesetz KVG verrechnet werden
		// --- Sind bereits zwei Konsultationen mit dem Gesetz KVG geöffnet soll auf die
		// neuere der beiden verrechnet werden

		IPatient patient = new IContactBuilder.PatientBuilder(CoreModelServiceHolder.get(), "Emil", "Knaus",
				LocalDate.of(2001, 2, 12), Gender.MALE).build();

		ICoverage coverageKVG = new ICoverageBuilder(CoreModelServiceHolder.get(), patient, "testLabelKVG",
				"testReason", "KVG").buildAndSave();
		ICoverage coverageUVG = new ICoverageBuilder(CoreModelServiceHolder.get(), patient, "testLabelUVG",
				"testReason", "UVG").buildAndSave();

		IEncounter encounterUVG = new IEncounterBuilder(CoreModelServiceHolder.get(), coverageUVG,
				AllTests.getUtil().getMandator()).buildAndSave();
		IEncounter encounterKVG = new IEncounterBuilder(CoreModelServiceHolder.get(), coverageKVG,
				AllTests.getUtil().getMandator()).buildAndSave();

		ILabResult labResult = new ILabResultBuilder(CoreModelServiceHolder.get(), AllTests.getLabItem(), patient)
				.build();

		labResult.setObservationTime(LocalDateTime.of(2016, Month.DECEMBER, 14, 17, 44, 25));
		labResult.setOrigin(AllTests.getLaboratory());
		labResult.setResult("2");
		labResult.setComment("no comment");
		CoreModelServiceHolder.get().save(labResult);

		Map<String, Serializable> runContext = new HashMap<>();
		runContext.putAll(billLabResultRunnable.getDefaultRunContext());
		runContext.put(RunContextParameter.IDENTIFIABLE_ID, labResult.getId());

		// This is done by TaskService on execution via TaskService
		ContextServiceHolder.get().setActiveMandator(AllTests.getUtil().getMandator());
		//

		billLabResultRunnable.run(runContext, null, LoggerFactory.getLogger(getClass()));

		CoreModelServiceHolder.get().refresh(encounterKVG);
		assertEquals(1, encounterKVG.getBilled().size());
		assertEquals(0, encounterUVG.getBilled().size());
	}

	// see #22266
	// Test-Case: Es sind mehrere Fälle, aber keine tagesaktuelle Kons vorhanden
	@Test
	public void billOnCorrectEncounterNoOpenKonsMultipleCoverages() throws TaskException {
		// -- Analog oben gilt, primär soll auf dem KVG-Fall eine neue Konsultation
		// eröffnet & verrechnet werden
		// -- gibt es mehrere KVG Fälle soll der Aktuellste genommen werden (Fall-Datum)

		IPatient patient = new IContactBuilder.PatientBuilder(CoreModelServiceHolder.get(), "Ursine", "Knausinger",
				LocalDate.of(1979, 2, 12), Gender.FEMALE).build();

		ICoverage coverageKVG = new ICoverageBuilder(CoreModelServiceHolder.get(), patient, "testLabelKVG",
				"testReason", "KVG").buildAndSave();
		ICoverage coverageUVG = new ICoverageBuilder(CoreModelServiceHolder.get(), patient, "testLabelUVG",
				"testReason", "UVG").buildAndSave();

		ILabResult labResult = new ILabResultBuilder(CoreModelServiceHolder.get(), AllTests.getLabItem(), patient)
				.build();

		labResult.setObservationTime(LocalDateTime.of(2016, Month.DECEMBER, 14, 17, 44, 25));
		labResult.setOrigin(AllTests.getLaboratory());
		labResult.setResult("2");
		labResult.setComment("no comment");
		CoreModelServiceHolder.get().save(labResult);

		Map<String, Serializable> runContext = new HashMap<>();
		runContext.putAll(billLabResultRunnable.getDefaultRunContext());
		runContext.put(RunContextParameter.IDENTIFIABLE_ID, labResult.getId());

		// This is done by TaskService on execution via TaskService
		ContextServiceHolder.get().setActiveMandator(AllTests.getUtil().getMandator());
		//

		billLabResultRunnable.run(runContext, null, LoggerFactory.getLogger(getClass()));

		IEncounter iEncounter = EncounterServiceHolder.get().getLatestEncounter(patient).get();
		assertEquals(coverageKVG, iEncounter.getCoverage());
		assertEquals(1, iEncounter.getBilled().size());
	}

}
