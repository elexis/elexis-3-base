package ch.elexis.base.solr.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.http.client.HttpClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.Builder;
import org.apache.solr.client.solrj.response.SolrResponseBase;

import ch.elexis.core.eenv.IElexisEnvironmentService;
import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.SingleIdentifiableTaskResult;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.utils.OsgiServiceUtil;

public abstract class AbstractIndexerIdentifiedRunnable implements IIdentifiedRunnable {

	/**
	 * run parameter: the url the solr service is accessible from
	 */
	public static final String RCP_STRING_SERVICE_URL = "service_url";
	/**
	 * run parameter: the maximum runtime in seconds before the runnable stops,
	 * defaults to 8*60=480
	 */
	public static final String RCP_STRING_MAX_RUNTIME_SECONDS = "maxRuntimeInSeconds";
	public static final String RCP_STRING_MAX_RUNTIME_SECONDS_DEFAULT = "480";

	private String solrServiceUrl;
	private HttpClient httpClient;
	private SolrIndexerUtil util;

	private long startTime;
	private int maxRunTime;

	protected ArrayList<SingleIdentifiableTaskResult> failures;
	protected Map<String, Serializable> resultMap;

	@Override
	public Map<String, Serializable> getDefaultRunContext() {
		Map<String, Serializable> defaultRunContext = new HashMap<>();
		Optional<IElexisEnvironmentService> eeService = OsgiServiceUtil.getService(IElexisEnvironmentService.class);
		if (eeService.isPresent()) {
			defaultRunContext.put(RCP_STRING_SERVICE_URL, eeService.get().getSolrBaseUrl());
		}
		defaultRunContext.put(RCP_STRING_MAX_RUNTIME_SECONDS, RCP_STRING_MAX_RUNTIME_SECONDS_DEFAULT);
		return defaultRunContext;
	}

	/**
	 * For test purposes. Allows to override the used HttpClient.
	 *
	 * @param httpClient
	 */
	protected void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public void init(Map<String, Serializable> runContext) throws TaskException {

		failures = new ArrayList<>();
		resultMap = new HashMap<String, Serializable>();
		util = new SolrIndexerUtil();

		solrServiceUrl = (String) runContext.get(RCP_STRING_SERVICE_URL);
		if (solrServiceUrl == null || !solrServiceUrl.startsWith("http")) {
			throw new TaskException(TaskException.EXECUTION_REJECTED, "Invalid service url provided.");
		}

		startTime = System.currentTimeMillis();
		maxRunTime = Integer.valueOf((String) runContext.get(RCP_STRING_MAX_RUNTIME_SECONDS));
	}

	public Builder getSolrClientBuilder() {
		Builder solrClientBuilder = new HttpSolrClient.Builder(solrServiceUrl);
		if (httpClient != null) {
			solrClientBuilder.withHttpClient(httpClient);
		}
		return solrClientBuilder;
	}

	public SolrIndexerUtil getUtil() {
		return util;
	}

	protected void checkResponse(SolrResponseBase response) throws SolrServerException {
		if (response.getStatus() != 0) {
			throw new SolrServerException("solr response is " + response.getStatus());
		}
	}

	public boolean checkIsOverMaxRunTime() {
		long currentTime = System.currentTimeMillis();
		long runTimeInSeconds = ((currentTime - startTime) / 1000);
		return runTimeInSeconds >= (maxRunTime + 10);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
