package ch.elexis.base.solr.spotlight;

import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.base.solr.internal.SolrConstants;
import ch.elexis.base.solr.internal.bean.IDocumentBean;
import ch.elexis.core.eenv.IElexisEnvironmentService;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.spotlight.ISpotlightResult;
import ch.elexis.core.spotlight.ISpotlightResultContributor;
import ch.elexis.core.spotlight.ISpotlightResultEntry.Category;

@Component(service = ISpotlightResultContributor.class)
public class DocumentSpotlightResultContributor extends AbstractSpotlightResultContributor {

	@Reference
	private IElexisEnvironmentService elexisEnvironmentService;
	
	@Reference
	private IContextService contextService;

	public DocumentSpotlightResultContributor() {
		super(SolrConstants.CORE_DOCUMENTS);
	}

	@Activate
	public void activate() {
		super.activate(elexisEnvironmentService, contextService);
	}

	@Deactivate
	public void deactivate() {
		super.deactivate();
	}

	@Override
	protected void handleResponse(ISpotlightResult spotlightResult, QueryResponse response) {
		List<IDocumentBean> beans = response.getBeans(IDocumentBean.class);
		for (IDocumentBean bean : beans) {
			spotlightResult.addEntry(Category.DOCUMENT, bean.getLabel(), bean.getId(), bean.getContent());
		}
	}

}
