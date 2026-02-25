package ch.elexis.base.solr.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.Builder;
import org.apache.solr.client.solrj.response.SolrResponseBase;
import org.slf4j.LoggerFactory;

import ch.elexis.core.eenv.IElexisEnvironmentService;
import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.SingleIdentifiableTaskResult;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IContextService;

public abstract class AbstractIndexerIdentifiedRunnable implements IIdentifiedRunnable {

	/**
	 * run parameter: the url the solr service is accessible from
	 */
	public static final String RCP_STRING_SERVICE_URL = "service_url"; //$NON-NLS-1$
	/**
	 * run parameter: the maximum runtime in seconds before the runnable stops,
	 * defaults to 8*60=480
	 */
	public static final String RCP_STRING_MAX_RUNTIME_SECONDS = "maxRuntimeInSeconds"; //$NON-NLS-1$
	public static final String RCP_STRING_MAX_RUNTIME_SECONDS_DEFAULT = "480"; //$NON-NLS-1$

	private String solrServiceUrl;
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

	public void init(Map<String, Serializable> runContext) throws TaskException {

		failures = new ArrayList<>();
		resultMap = new HashMap<String, Serializable>();
		util = new SolrIndexerUtil();

		solrServiceUrl = (String) runContext.get(RCP_STRING_SERVICE_URL);
		if (solrServiceUrl == null || !solrServiceUrl.startsWith("http")) { //$NON-NLS-1$
			throw new TaskException(TaskException.EXECUTION_REJECTED, "Invalid service url provided."); //$NON-NLS-1$
		}

		startTime = System.currentTimeMillis();
		maxRunTime = Integer.valueOf((String) runContext.get(RCP_STRING_MAX_RUNTIME_SECONDS));

	}

	public Builder getSolrClientBuilder() {
		Builder solrClientBuilder = new HttpSolrClient.Builder(solrServiceUrl);
		solrClientBuilder.withConnectionTimeout(5 * 1000);
		solrClientBuilder.withSocketTimeout(10 * 1000);

		Optional<IContextService> contextService = OsgiServiceUtil.getService(IContextService.class);
		Optional<IElexisEnvironmentService> eeService = OsgiServiceUtil.getService(IElexisEnvironmentService.class);
		if (contextService.isPresent() && eeService.isPresent()) {
			if (IElexisEnvironmentService.ES_STATION_ID_DEFAULT.equals(contextService.get().getStationIdentifier())) {
				// this is Elexis-Server running in EE
				// see https://solr.apache.org/guide/8_11/basic-authentication-plugin.html
				String solrPassword = System.getenv("X_EE_SOLR_ELEXIS_SERVER_PASSWORD"); //$NON-NLS-1$
				if (StringUtils.isNotBlank(solrPassword)) {
					HttpClient httpClient = createBasicAuthHttpClient(IElexisEnvironmentService.ES_STATION_ID_DEFAULT,
							solrPassword);
					solrClientBuilder.withHttpClient(httpClient);
				} else {
					LoggerFactory.getLogger(getClass()).error(
							"Combination EE/ES found, but password is blank in env X_EE_SOLR_ELEXIS_SERVER_PASSWORD"); //$NON-NLS-1$
				}
			}
		}

		return solrClientBuilder;
	}

	public SolrIndexerUtil getUtil() {
		return util;
	}

	protected void checkResponse(SolrResponseBase response) throws SolrServerException {
		if (response.getStatus() != 0) {
			throw new SolrServerException("solr response is " + response.getStatus()); //$NON-NLS-1$
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

	private HttpClient createBasicAuthHttpClient(String username, String password) {
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();

		BasicCredentialsProvider provider = new BasicCredentialsProvider();
		provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

		clientBuilder.setDefaultCredentialsProvider(provider);
		clientBuilder.addInterceptorFirst(new PreemptiveAuthInterceptor());
		return clientBuilder.build();
	}

	private class PreemptiveAuthInterceptor implements HttpRequestInterceptor {
		@Override
		public void process(HttpRequest request, HttpContext context) throws HttpException {
			AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);
			if (authState.getAuthScheme() == null) {
				CredentialsProvider credsProvider = (CredentialsProvider) context
						.getAttribute(HttpClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
				Credentials credentials = credsProvider
						.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
				if (credentials == null) {
					throw new HttpException("No credentials provided for preemptive authentication."); //$NON-NLS-1$
				}
				authState.update(new BasicScheme(), credentials);
			}
		}

	}
}
