package ch.elexis.tasks.integration.test.runnable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.elexis.core.importer.div.tasks.BillLabResultOnCreationIdentifiedRunnable;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.builder.ILabResultBuilder;
import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.IIdentifiedRunnable.ReturnParameter;
import ch.elexis.core.model.tasks.IIdentifiedRunnable.RunContextParameter;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.tasks.integration.test.AllTests;
import ch.elexis.tasks.integration.test.internal.TaskServiceHolder;

public class BillLabResultOnCreationIdentifiedRunnableTest {
	
	@Test
	public void billOnNonEditableEncounter() throws TaskException{
		
		IIdentifiedRunnable billLabResultRunnable = TaskServiceHolder.get()
			.instantiateRunnableById(BillLabResultOnCreationIdentifiedRunnable.RUNNABLE_ID);
		assertNotNull(billLabResultRunnable);
		
		ICoverage coverage = AllTests.getCoverage();
		coverage.setDateTo(LocalDate.now());
		CoreModelServiceHolder.get().save(coverage);
		
		ILabResult labResult = new ILabResultBuilder(CoreModelServiceHolder.get(), AllTests.getLabItem(), AllTests.getPatient()).build();
		
		labResult.setObservationTime(LocalDateTime.of(2016, Month.DECEMBER, 14, 17, 44, 25));
		labResult.setOrigin(AllTests.getLaboratory());
		labResult.setResult("2");
		labResult.setComment("no comment");
		CoreModelServiceHolder.get().save(labResult);
		
		Map<String, Serializable> runContext = new HashMap<>();
		runContext.putAll(billLabResultRunnable.getDefaultRunContext());
		runContext.put(RunContextParameter.IDENTIFIABLE_ID, labResult.getId());
		
		Map<String, Serializable> result =
			billLabResultRunnable.run(runContext, null, LoggerFactory.getLogger(getClass()));
		assertTrue(result.containsKey(ReturnParameter.MARKER_WARN));
		
		// cleanup
		coverage = AllTests.getCoverage();
		coverage.setDateTo(null);
		CoreModelServiceHolder.get().save(coverage);
	}
	
}
