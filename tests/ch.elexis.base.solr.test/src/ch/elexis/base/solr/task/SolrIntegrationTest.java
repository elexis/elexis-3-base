package ch.elexis.base.solr.task;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IElexisEntityManager;
import ch.elexis.core.services.IEncounterService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.test.initializer.TestDatabaseInitializer;
import ch.elexis.omnivore.model.IDocumentHandle;

@Component(immediate = true)
public class SolrIntegrationTest {
	
	public static final String BASE_URL =
		"https://marcos-mbp-2019.intra.herzpraxis.at/solr/elexis/";
	
	private static HttpClient httpClient;
	
	private static IEncounterService encounterService;
	private static IModelService omnivoreModelService;
	
	@Reference
	private void setEncounterService(IEncounterService encounterService){
		SolrIntegrationTest.encounterService = encounterService;
	}
	
	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.omnivore.data.model)")
	private void setOmnivoreModelService(IModelService omnivoreModelService){
		SolrIntegrationTest.omnivoreModelService = omnivoreModelService;
	}
	
	@BeforeClass
	public static void beforeClass() throws IOException, SQLException, InterruptedException,
		KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		
		for (int i = 0; i < 10; i++) {
			if (encounterService == null) {
				System.out.println("Waiting for services");
				Thread.sleep(1000);
			} else {
				continue;
			}
		}
		if (encounterService == null) {
			fail();
		}
		
		IElexisEntityManager entityManager =
			OsgiServiceUtil.getService(IElexisEntityManager.class).get();
		TestDatabaseInitializer testDatabaseInitializer =
			new TestDatabaseInitializer(CoreModelServiceHolder.get(), entityManager);
		testDatabaseInitializer.initializeMandant();
		testDatabaseInitializer.initializePatient();
		testDatabaseInitializer.initializeLabResult();
		testDatabaseInitializer.initializePrescription();
		testDatabaseInitializer.initializeBehandlung();
		
		IDocumentHandle document = omnivoreModelService.create(IDocumentHandle.class);
		document.setPatient(testDatabaseInitializer.getPatient());
		document.setTitle("Spitalbericht");
		try (InputStream resourceAsStream = SolrIntegrationTest.class.getClassLoader()
			.getResourceAsStream("/rsc/Spitalbericht.PDF")) {
			document.setContent(resourceAsStream);
		}
		
		document.setCreated(new Date());
		omnivoreModelService.save(document);
		
		SSLContext sslcontext =
			SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
			SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
	}
	
	@Test
	public void executeTask() throws TaskException{
		Logger logger = LoggerFactory.getLogger(getClass());
		
//		SolrIndexerIdentifiedRunnable runnable = new SolrIndexerIdentifiedRunnable(
//			CoreModelServiceHolder.get(), omnivoreModelService, encounterService);
//		runnable.setHttpClient(httpClient);
//		Map<String, Serializable> defaultRunContext = runnable.getDefaultRunContext();
//		defaultRunContext.put(SolrIndexerIdentifiedRunnable.RCP_STRING_SERVICE_URL, BASE_URL);
//		
//		Map<String, Serializable> result =
//			runnable.run(defaultRunContext, new NullProgressMonitor(), logger);
//		
//		assertFalse(result.containsKey(IIdentifiedRunnable.ReturnParameter.MARKER_WARN));
//		int noEncounters =
//			(Integer) result.get(SolrIndexerIdentifiedRunnable.RKP_INTEGER_NO_ENCOUNTERS_INDEXED);
//		assertEquals(1, noEncounters);
//		int noDocHandles =
//			(Integer) result.get(SolrIndexerIdentifiedRunnable.RKP_INTEGER_NO_DOCHANDLE_INDEXED);
//		assertEquals(1, noDocHandles);
		
		// TEST Submitted encounters and the resp. data -> date is wrong 20 vs 21 sept
		// Submitted documents/omnivore ...
	}
	
}
