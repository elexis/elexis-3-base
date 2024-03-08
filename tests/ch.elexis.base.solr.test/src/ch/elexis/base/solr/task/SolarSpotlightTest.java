package ch.elexis.base.solr.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.base.solr.internal.SolrConstants;
import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.IEncounterService;
import ch.elexis.core.services.IModelService;

@Component(immediate = true)
public class SolarSpotlightTest {

	public static final String BASE_URL = "https://ee-master.myelexis.ch/solr/";

	private SolrIndexerIdentifiedRunnableFactory factory;
	private static IModelService omnivoreModelService;
	private DocumentIndexerIdentifiedRunnable runnable;

	@Reference
	private void setEncounterService(IEncounterService encounterService) {
	}

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.omnivore.data.model)")
	private void setOmnivoreModelService(IModelService omnivoreModelService) {
		SolarSpotlightTest.omnivoreModelService = omnivoreModelService;
	}

	@Before
	public void setUp() {
		factory = new SolrIndexerIdentifiedRunnableFactory();
		runnable = new DocumentIndexerIdentifiedRunnable(omnivoreModelService);
	}

	@Test
	public void testGetSolrCore() {
		assertEquals("Der erwartete Solr-Core sollte zurückgegeben werden", SolrConstants.CORE_DOCUMENTS,
				runnable.getSolrCore());
	}

	@Test
	public void testGetModelService() {
		assertEquals("Der übergebene ModelService sollte zurückgegeben werden", omnivoreModelService,
				runnable.getModelService());
	}

	@Test
	public void testGetProvidedRunnables() {
		List<IIdentifiedRunnable> runnables = factory.getProvidedRunnables();
		assertNotNull("Liste der Runnables sollte nicht null sein", runnables);
		assertEquals("Erwartete Anzahl der Runnables", 3, runnables.size());
	}

	@Test
	public void testInitWithValidServiceUrl() {
		TestIndexerIdentifiedRunnable runnable = new TestIndexerIdentifiedRunnable();
		Map<String, Serializable> runContext = new HashMap<>();
		runContext.put(AbstractIndexerIdentifiedRunnable.RCP_STRING_SERVICE_URL, BASE_URL);
		runContext.put(AbstractIndexerIdentifiedRunnable.RCP_STRING_MAX_RUNTIME_SECONDS, "480");

		try {
			runnable.init(runContext);
			assertTrue("Initialisierung sollte erfolgreich sein", true);
		} catch (TaskException e) {
			fail("Initialisierung sollte keine TaskException werfen");
		}
	}

	@Test(expected = TaskException.class)
	public void testInitWithInvalidServiceUrl() throws TaskException {
		TestIndexerIdentifiedRunnable runnable = new TestIndexerIdentifiedRunnable();
		Map<String, Serializable> runContext = new HashMap<>();
		runContext.put(AbstractIndexerIdentifiedRunnable.RCP_STRING_SERVICE_URL, "invalidUrl");
		runnable.init(runContext);
	}

	@Test
	public void testIdAndDescription() {
		IModelService modelService = omnivoreModelService;
		DocumentIndexerIdentifiedRunnable runnable = new DocumentIndexerIdentifiedRunnable(modelService);
		assertEquals("Die ID sollte RUNNABLE_ID entsprechen", DocumentIndexerIdentifiedRunnable.RUNNABLE_ID,
				runnable.getId());
		assertEquals("Die Beschreibung sollte DESCRIPTION entsprechen", DocumentIndexerIdentifiedRunnable.DESCRIPTION,
				runnable.getLocalizedDescription());
	}
}
