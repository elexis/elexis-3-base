package ch.elexis.tasks.integration.test.runnable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.elexis.core.console.ConsoleProgressMonitor;
import ch.elexis.core.model.ILabOrder;
import ch.elexis.core.model.LabOrderState;
import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.rcp.utils.PlatformHelper;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.tasks.integration.test.AllTests;
import ch.elexis.tasks.integration.test.internal.TaskServiceHolder;

public class HL7ImporterIIdentifiedRunnableTest {

	@Test
	public void testImport() throws TaskException {

		IIdentifiedRunnable hl7Importer = TaskServiceHolder.get().instantiateRunnableById("hl7importer");
		assertNotNull(hl7Importer);

		String inputFileUri = new File(PlatformHelper.getBasePath("ch.elexis.tasks.integration.test"),
				"rsc/11871_LabCube_DCAVantage_20200224094401_976738.hl7").toURI().toString();

		Map<String, Serializable> runContext = new HashMap<String, Serializable>();
		runContext.putAll(hl7Importer.getDefaultRunContext());
		runContext.put(IIdentifiedRunnable.RunContextParameter.STRING_URL, inputFileUri);
		runContext.put("moveFile", Boolean.FALSE.toString());
		runContext.put("labName", "myLab");

		Map<String, Serializable> result = hl7Importer.run(runContext, new ConsoleProgressMonitor(),
				LoggerFactory.getLogger(getClass()));
		assertFalse((String) result.get(IIdentifiedRunnable.ReturnParameter.RESULT_DATA),
				result.containsKey(IIdentifiedRunnable.ReturnParameter.MARKER_WARN));
	}

	@Test
	public void testImportWithExistingLabOrder() throws TaskException {

		IIdentifiedRunnable hl7Importer = TaskServiceHolder.get().instantiateRunnableById("hl7importer");
		assertNotNull(hl7Importer);

		String inputFileUri = new File(PlatformHelper.getBasePath("ch.elexis.tasks.integration.test"),
				"rsc/19054_LabCube_DriChemNX700i_20200428083747_598550.hl7").toURI().toString();

		ILabOrder labOrder = CoreModelServiceHolder.get().create(ILabOrder.class);
		labOrder.setPatient(AllTests.getPatient());
		labOrder.setState(LabOrderState.ORDERED);
		labOrder.setItem(AllTests.getLabItemGPT());
		CoreModelServiceHolder.get().save(labOrder);

		Map<String, Serializable> runContext = new HashMap<String, Serializable>();
		runContext.putAll(hl7Importer.getDefaultRunContext());
		runContext.put(IIdentifiedRunnable.RunContextParameter.STRING_URL, inputFileUri);
		runContext.put("moveFile", Boolean.FALSE.toString());
		runContext.put("labName", "myLab");

		Map<String, Serializable> result = hl7Importer.run(runContext, new ConsoleProgressMonitor(),
				LoggerFactory.getLogger(getClass()));
		assertFalse((String) result.get(IIdentifiedRunnable.ReturnParameter.RESULT_DATA),
				result.containsKey(IIdentifiedRunnable.ReturnParameter.MARKER_WARN));
	}

}
