package ch.elexis.base.solr.spotlight;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.MapSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.eenv.AccessToken;
import ch.elexis.core.eenv.IElexisEnvironmentService;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.spotlight.ISpotlightResult;
import ch.elexis.core.spotlight.ISpotlightResultContributor;
import ch.elexis.core.spotlight.ISpotlightService;

public abstract class AbstractSpotlightResultContributor implements ISpotlightResultContributor {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private HttpSolrClient client;
	private IContextService contextService;
	private final String CORE;

	public AbstractSpotlightResultContributor(String core) {
		this.CORE = core;
	}

	public void activate(IElexisEnvironmentService elexisEnvironmentService, IContextService contextService) {
		client = new HttpSolrClient.Builder(elexisEnvironmentService.getSolrBaseUrl()).build();
		this.contextService = contextService;
	}

	public void deactivate() {
		if (client != null) {
			try {
				client.close();
			} catch (IOException e) {
				logger.warn("Error closing client", e); //$NON-NLS-1$
			}
		}
	}

	@Override
	public void computeResult(List<String> stringTerms, List<LocalDate> dateTerms, List<Number> numericTerms,
			ISpotlightResult spotlightResult, Map<String, String> contextParameters) {

		if (stringTerms.isEmpty()) {
			return;
		}

		try {
			StringBuilder qString = new StringBuilder();
			final Map<String, String> queryParamMap = new HashMap<String, String>();

			if (contextParameters != null) {
				String patientId = contextParameters.get(ISpotlightService.CONTEXT_FILTER_PATIENT_ID);
				if (patientId != null) {
					qString.append("patient_id:" + patientId + " AND "); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			qString.append("text:" + stringTerms.stream().reduce((u, t) -> u + " AND text:" + t).get()); //$NON-NLS-1$ //$NON-NLS-2$
			queryParamMap.put("q", qString.toString()); //$NON-NLS-1$
			queryParamMap.put("sort", "cr_date desc"); //$NON-NLS-1$ //$NON-NLS-2$
			queryParamMap.put("rows", "5"); //$NON-NLS-1$ //$NON-NLS-2$

			MapSolrParams queryParams = new MapSolrParams(queryParamMap);

			QueryRequest queryRequest = new QueryRequest(queryParams);
			contextService.getTyped(AccessToken.class).ifPresent(accessToken -> {
				queryRequest.addHeader("Authorization", "Bearer " + accessToken.getToken()); //$NON-NLS-1$ //$NON-NLS-2$
			});

			QueryResponse response = queryRequest.process(client, CORE);
			handleResponse(spotlightResult, response);

		} catch (SolrServerException | IOException e) {
			logger.warn("Error in client.query", e); //$NON-NLS-1$
		}

	}

	protected abstract void handleResponse(ISpotlightResult spotlightResult, QueryResponse response);

}
