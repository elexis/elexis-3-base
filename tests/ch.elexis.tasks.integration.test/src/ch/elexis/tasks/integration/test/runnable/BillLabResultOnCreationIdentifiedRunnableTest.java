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
import ch.elexis.core.model.builder.ILabResultBuilder;
import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.IIdentifiedRunnable.ReturnParameter;
import ch.elexis.core.model.tasks.IIdentifiedRunnable.RunContextParameter;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.EncounterServiceHolder;
import ch.elexis.tasks.integration.test.AllTests;
import ch.elexis.tasks.integration.test.internal.TaskServiceHolder;

public class BillLabResultOnCreationIdentifiedRunnableTest {

	@Before
	public void before() {
		ContextServiceHolder.get().setActiveUser(AllTests.getUser());
		IEncounter encounter = EncounterServiceHolder.get().getLatestEncounter(AllTests.getPatient()).get();
		List<IBilled> billed = encounter.getBilled();
		for (IBilled iBilled : billed) {
			assertTrue(CoreModelServiceHolder.get().remove(iBilled));
		}
	}

	@After
	public void after() {
		ContextServiceHolder.get().setActiveUser(null);
	}

	@Test
	public void billOnNonEditableEncounter() throws TaskException {

		IEncounter encounter = EncounterServiceHolder.get().getLatestEncounter(AllTests.getPatient()).get();
		assertEquals(0, encounter.getBilled().size());

		IIdentifiedRunnable billLabResultRunnable = TaskServiceHolder.get()
				.instantiateRunnableById(BillLabResultOnCreationIdentifiedRunnable.RUNNABLE_ID);
		assertNotNull(billLabResultRunnable);

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

		IIdentifiedRunnable billLabResultRunnable = TaskServiceHolder.get()
				.instantiateRunnableById(BillLabResultOnCreationIdentifiedRunnable.RUNNABLE_ID);
		assertNotNull(billLabResultRunnable);

		IEncounter encounter = EncounterServiceHolder.get().getLatestEncounter(AllTests.getPatient()).get();
		String closedEncounterId = encounter.getId();
		assertEquals(0, encounter.getBilled().size());

		// invoice the encounter, this should be mitigatable
		IInvoice invoice = CoreModelServiceHolder.get().create(IInvoice.class);
		invoice.setCoverage(encounter.getCoverage());
		invoice.setDate(LocalDate.now());
		invoice.setDateFrom(LocalDate.now());
		invoice.setDateTo(LocalDate.now());
		CoreModelServiceHolder.get().save(invoice);

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
		assertTrue(CoreModelServiceHolder.get().remove(encounter));
	}

}
