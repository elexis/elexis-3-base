package ch.elexis.base.solr.spotlight;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.MapSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.eenv.IElexisEnvironmentService;
import ch.elexis.core.spotlight.ISpotlightResult;
import ch.elexis.core.spotlight.ISpotlightResultContributor;

public abstract class AbstractSpotlightResultContributor implements ISpotlightResultContributor {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private HttpSolrClient client;
	private final String CORE;
	
	public AbstractSpotlightResultContributor(String core){
		this.CORE = core;
	}
	
	public void activate(IElexisEnvironmentService elexisEnvironmentService){
		client = new HttpSolrClient.Builder(elexisEnvironmentService.getSolrBaseUrl()).build();
	}
	
	public void deactivate(){
		if (client != null) {
			try {
				client.close();
			} catch (IOException e) {
				logger.warn("Error closing client", e);
			}
		}
	}
	
	@Override
	public void computeResult(List<String> stringTerms, List<LocalDate> dateTerms,
		List<Number> numericTerms, ISpotlightResult spotlightResult,
		Map<String, String> contextParameters){
		
		try {
			final Map<String, String> queryParamMap = new HashMap<String, String>();
			// TODO multiple string terms combined with AND
			queryParamMap.put("q", "text:" + stringTerms.get(0) + "*");
			queryParamMap.put("rows", "5");
			MapSolrParams queryParams = new MapSolrParams(queryParamMap);
			
			QueryResponse response = client.query(CORE, queryParams);
			handleResponse(spotlightResult, response);
			
		} catch (SolrServerException | IOException e) {
			logger.warn("Error in client.query", e);
		}
		
	}
	
	protected abstract void handleResponse(ISpotlightResult spotlightResult,
		QueryResponse response);
	
}
