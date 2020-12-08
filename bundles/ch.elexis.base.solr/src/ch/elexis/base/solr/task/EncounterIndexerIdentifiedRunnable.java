package ch.elexis.base.solr.task;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.Builder;
import org.apache.solr.common.SolrDocument;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;

import ch.elexis.base.solr.internal.SolrConstants;
import ch.elexis.base.solr.internal.bean.EncounterBean;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.SingleIdentifiableTaskResult;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQueryCursor;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.services.holder.StoreToStringServiceHolder;

public class EncounterIndexerIdentifiedRunnable extends AbstractIndexerIdentifiedRunnable {
	
	public static final String RUNNABLE_ID = "solrEncounterIndexer";
	public static final String DESCRIPTION = "Index encouters into SOLR";
	
	private IModelService coreModelService;
	private IConfigService configService;
	
	public EncounterIndexerIdentifiedRunnable(IModelService coreModelService,
		IConfigService configService){
		this.coreModelService = coreModelService;
		this.configService = configService;
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
	public Map<String, Serializable> run(Map<String, Serializable> runContext,
		IProgressMonitor progressMonitor, Logger logger) throws TaskException{
		
		super.init(runContext);
		
		Builder solrClientBuilder = super.getSolrClientBuilder();
		try (HttpSolrClient solr = solrClientBuilder.build()) {
			// is the server alive, else early exit
			checkResponse(solr.ping(SolrConstants.CORE_ENCOUNTERS));
			
			String lastIndexRunLastUpdateString =
				configService.get(SolrConstants.CONFIG_KEY_LASTINDEXRUN_ENCOUNTER, null);
			Long lastIndexRunLastUpdate =
				(lastIndexRunLastUpdateString != null) ? Long.valueOf(lastIndexRunLastUpdateString)
						: null;
			
			long newestLastUpdate =
				indexEncounters(solr, lastIndexRunLastUpdate, progressMonitor, logger, failures);
			
			configService.set(SolrConstants.CONFIG_KEY_LASTINDEXRUN_ENCOUNTER,
				Long.toString(newestLastUpdate));
			
		} catch (IOException | SolrServerException e) {
			throw new TaskException(TaskException.EXECUTION_ERROR, e);
		}
		
		return resultMap;
	}
	
	private long indexEncounters(HttpSolrClient solr, Long lastIndexRunLastUpdate,
		IProgressMonitor progressMonitor, Logger logger,
		ArrayList<SingleIdentifiableTaskResult> failures) throws SolrServerException, IOException{
		
		IQuery<IEncounter> query = coreModelService.getQuery(IEncounter.class, true);
		query.and(ModelPackage.Literals.IENCOUNTER__COVERAGE, COMPARATOR.NOT_EQUALS, null);
		if (lastIndexRunLastUpdate != null) {
			query.and(ModelPackage.Literals.IDENTIFIABLE__LASTUPDATE, COMPARATOR.GREATER_OR_EQUAL,
				lastIndexRunLastUpdate);
		}
		query.orderBy(ModelPackage.Literals.IDENTIFIABLE__LASTUPDATE, ORDER.ASC);
		query.limit(100000);
		
		int noIndexed = 0;
		int noRemovedFromIndex = 0;
		long newestLastUpdate = 0;
		try (IQueryCursor<IEncounter> encounterCursor = query.executeAsCursor()) {
			while (encounterCursor.hasNext()) {
				
				IEncounter encounter = encounterCursor.next();
				try {
					
					if (encounter.isDeleted()) {
						SolrDocument document = solr.getById(encounter.getId());
						if (document != null) {
							checkResponse(
								solr.deleteById(SolrConstants.CORE_ENCOUNTERS, encounter.getId()));
							noRemovedFromIndex++;
						}
						
					} else {
						EncounterBean iEncounterBean = EncounterBean.of(encounter);
						checkResponse(solr.addBean(SolrConstants.CORE_ENCOUNTERS, iEncounterBean));
						noIndexed++;
					}
					
					if (encounter.getLastupdate() > newestLastUpdate) {
						newestLastUpdate = encounter.getLastupdate();
					}
					
				} catch (IOException | SolrServerException | IllegalArgumentException e) {
					logger.warn("addBean exception on encounter [{}]", encounter.getId(), e);
					failures.add(new SingleIdentifiableTaskResult(
						StoreToStringServiceHolder.getStoreToString(encounter), "commit",
						e.getMessage()));
				}
				
				encounterCursor.clear();
				
				if (progressMonitor.isCanceled()) {
					break;
				}
				
				if (super.checkIsOverMaxRunTime()) {
					break;
				}
				
			}
		}
		
		checkResponse(solr.commit(SolrConstants.CORE_ENCOUNTERS));
		
		resultMap.put(IIdentifiedRunnable.ReturnParameter.RESULT_DATA, noIndexed + " indexed / "
			+ noRemovedFromIndex + " removed from index / " + newestLastUpdate + " LU");
		if (noIndexed == 0 && noRemovedFromIndex == 0 && failures.size() == 0) {
			resultMap.put(IIdentifiedRunnable.ReturnParameter.MARKER_DO_NOT_PERSIST, true);
		}
		
		return newestLastUpdate;
	}
	
}
