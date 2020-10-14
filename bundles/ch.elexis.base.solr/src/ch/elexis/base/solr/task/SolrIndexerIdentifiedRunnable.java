package ch.elexis.base.solr.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.Builder;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.SolrResponseBase;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.util.ContentStreamBase;
import org.apache.solr.common.util.ContentStreamBase.ByteArrayStream;
import org.apache.solr.common.util.NamedList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;

import ch.elexis.base.solr.internal.bean.IDocumentHandleBean;
import ch.elexis.base.solr.internal.bean.IEncounterSolrBean;
import ch.elexis.core.eenv.IElexisEnvironmentService;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.SingleIdentifiableTaskResult;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.IEncounterService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQueryCursor;
import ch.elexis.core.services.holder.StoreToStringServiceHolder;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.omnivore.model.IDocumentHandle;

public class SolrIndexerIdentifiedRunnable implements IIdentifiedRunnable {
	
	public static final String RUNNABLE_ID = "solrIndexer";
	public static final String DESCRIPTION = "TODO";
	
	private HttpClient httpClient;
	
	/**
	 * run parameter: the url the solr core to use is accessible from
	 */
	public static final String RCP_STRING_SERVICE_URL = "service_url";
	/**
	 * return parameter: number of encounters indexed
	 */
	public static final String RKP_INTEGER_NO_ENCOUNTERS_INDEXED = "noEncountersIndexed";
	/**
	 * return parameter: number of docHandles indexed
	 */
	public static final String RKP_INTEGER_NO_DOCHANDLE_INDEXED = "noDocHandlesIndexed";
	
	private IModelService coreModelService;
	private IModelService omnivoreModelService;
	private IEncounterService encounterService;
	
	private int noEncountersIndexed;
	private int noDocHandlesIndexed;
	private ArrayList<SingleIdentifiableTaskResult> failures;
	
	public SolrIndexerIdentifiedRunnable(IModelService coreModelService,
		IModelService omnivoreModelService, IEncounterService encounterService){
		this.coreModelService = coreModelService;
		this.omnivoreModelService = omnivoreModelService;
		this.encounterService = encounterService;
	}
	
	@Override
	public String getId(){
		return RUNNABLE_ID;
	}
	
	@Override
	public String getLocalizedDescription(){
		return DESCRIPTION;
	}
	
	@Override
	public Map<String, Serializable> getDefaultRunContext(){
		Map<String, Serializable> defaultRunContext = new HashMap<>();
		Optional<IElexisEnvironmentService> eeService =
			OsgiServiceUtil.getService(IElexisEnvironmentService.class);
		if (eeService.isPresent()) {
			defaultRunContext.put(RCP_STRING_SERVICE_URL,
				eeService.get().getBaseUrl() + "/solr/elexis/");
		}
		return defaultRunContext;
	}
	
	/**
	 * For test purposes. Allows to override the used HttpClient.
	 * 
	 * @param httpClient
	 */
	protected void setHttpClient(HttpClient httpClient){
		this.httpClient = httpClient;
	}
	
	@Override
	public Map<String, Serializable> run(Map<String, Serializable> runContext,
		IProgressMonitor progressMonitor, Logger logger) throws TaskException{
		
		noEncountersIndexed = 0;
		noDocHandlesIndexed = 0;
		failures = new ArrayList<>();
		
		String solrServiceUrl = (String) runContext.get(RCP_STRING_SERVICE_URL);
		if (solrServiceUrl == null || !solrServiceUrl.startsWith("http")) {
			throw new TaskException(TaskException.EXECUTION_REJECTED,
				"Invalid service url provided.");
		}
		Builder solrClientBuilder = new HttpSolrClient.Builder(solrServiceUrl);
		if (httpClient != null) {
			solrClientBuilder.withHttpClient(httpClient);
		}
		
		try (HttpSolrClient solr = solrClientBuilder.build()) {
			// is the server alive, else early exit
			checkResponse(solr.ping());
			
			IQuery<IPatient> patientQuery = coreModelService.getQuery(IPatient.class);
			patientQuery.and(ModelPackage.Literals.ICONTACT__PATIENT, COMPARATOR.EQUALS, true);
			try (IQueryCursor<IPatient> patientsCursor = patientQuery.executeAsCursor()) {
				int totalWork = patientsCursor.size();
				progressMonitor.beginTask("Indexing patient data ...", totalWork);
				patientsCursor
					.forEachRemaining(p -> indexPatient(solr, p, progressMonitor, logger));
			}
			
		} catch (IOException | SolrServerException e) {
			throw new TaskException(TaskException.EXECUTION_ERROR, e);
		}
		
		progressMonitor.done();
		
		Map<String, Serializable> resultMap = new HashMap<String, Serializable>();
		resultMap.put(RKP_INTEGER_NO_ENCOUNTERS_INDEXED, noEncountersIndexed);
		resultMap.put(RKP_INTEGER_NO_DOCHANDLE_INDEXED, noDocHandlesIndexed);
		if (failures.size() > 0) {
			resultMap.put(IIdentifiedRunnable.ReturnParameter.MARKER_WARN, null);
			resultMap.put(IIdentifiedRunnable.ReturnParameter.RESULT_CLASS,
				SingleIdentifiableTaskResult.class.getName());
			resultMap.put(IIdentifiedRunnable.ReturnParameter.RESULT_DATA_LIST, failures);
		}
		
		return resultMap;
	}
	
	private void indexPatient(HttpSolrClient solr, IPatient patient,
		IProgressMonitor progressMonitor, Logger logger){
		
		indexPatientEncounters(solr, patient, logger);
		indexPatientDocumentHandles(solr, patient, logger);
		
		try {
			solr.commit();
		} catch (SolrServerException | IOException e) {
			logger.warn("commit exception on patient [{}]", patient.getId(), e);
			failures.add(new SingleIdentifiableTaskResult(
				StoreToStringServiceHolder.getStoreToString(patient), "commit", e.getMessage()));
		}
		
		progressMonitor.worked(1);
	}
	
	/**
	 * Index omnivore documents
	 * 
	 * @param solr
	 * @param patient
	 * @param logger
	 * @throws IOException
	 * @throws SolrServerException
	 */
	private void indexPatientDocumentHandles(HttpSolrClient solr, IPatient patient, Logger logger){
		
		IQuery<IDocumentHandle> documentQuery =
			omnivoreModelService.getQuery(IDocumentHandle.class);
		documentQuery.and("kontakt", COMPARATOR.EQUALS, patient);
		List<IDocumentHandle> docHandles = documentQuery.execute();
		for (IDocumentHandle iDocHandle : docHandles) {
			
			String content = "";
			String metadata = "";
			
			// extract content and metadata using SolrCell
			ContentStreamUpdateRequest request = new ContentStreamUpdateRequest("/update/extract");
			
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			try (InputStream content_is = iDocHandle.getContent()) {
				if (content_is == null) {
					continue;
				}
				IOUtils.copy(content_is, os);
				final ByteArrayStream stream =
					new ContentStreamBase.ByteArrayStream(os.toByteArray(), null);
				request.addContentStream(stream);
				request.setParam("extractOnly", "true");
				request.setParam("extractFormat", "text");
				NamedList<Object> result;
				try {
					result = solr.request(request);
					
					for (int i = 0; i < result.size(); i++) {
						String name = result.getName(i);
						if (name == null) {
							content = String.valueOf(result.getVal(i));
						} else if ("null_metadata".equals(name)) {
							metadata = String.valueOf(result.getVal(i));
						}
					}
					
					// add document
					IDocumentHandleBean iDocHandleBean =
						new IDocumentHandleBean(iDocHandle, content, metadata);
					checkResponse(solr.addBean(iDocHandleBean));
					noDocHandlesIndexed++;
				} catch (SolrServerException e) {
					logger.warn("solr exception on iDocHandle [{}]", iDocHandle.getId(), e);
					failures.add(new SingleIdentifiableTaskResult(
						StoreToStringServiceHolder.getStoreToString(iDocHandle), null,
						e.getMessage()));
				}
				
			} catch (IOException e) {
				logger.warn("io exception on iDocHandle [{}]", iDocHandle.getId(), e);
				failures.add(new SingleIdentifiableTaskResult(
					StoreToStringServiceHolder.getStoreToString(iDocHandle), null, e.getMessage()));
			}
		}
		
	}
	
	private void indexPatientEncounters(HttpSolrClient solr, IPatient patient, Logger logger){
		List<IEncounter> encounters = encounterService.getAllEncountersForPatient(patient);
		for (IEncounter encounter : encounters) {
			IEncounterSolrBean iEncounterBean = new IEncounterSolrBean(encounter);
			try {
				checkResponse(solr.addBean(iEncounterBean));
				noEncountersIndexed++;
			} catch (IOException | SolrServerException e) {
				logger.warn("addBean exception on encounter [{}]", encounter.getId(), e);
				failures.add(new SingleIdentifiableTaskResult(
					StoreToStringServiceHolder.getStoreToString(encounter), null, e.getMessage()));
			}
		}
	}
	
	private void checkResponse(SolrResponseBase response) throws SolrServerException{
		if (response.getStatus() != 0) {
			throw new SolrServerException("solr response is " + response.getStatus());
		}
	}
	
}
