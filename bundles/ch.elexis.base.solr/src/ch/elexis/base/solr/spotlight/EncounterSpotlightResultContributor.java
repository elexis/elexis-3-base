package ch.elexis.base.solr.spotlight;

import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.base.solr.internal.SolrConstants;
import ch.elexis.base.solr.internal.bean.EncounterBean;
import ch.elexis.core.eenv.IElexisEnvironmentService;
import ch.elexis.core.spotlight.ISpotlightResult;
import ch.elexis.core.spotlight.ISpotlightResultContributor;
import ch.elexis.core.spotlight.ISpotlightResultEntry.Category;

@Component(service = ISpotlightResultContributor.class)
public class EncounterSpotlightResultContributor extends AbstractSpotlightResultContributor {

	@Reference
	private IElexisEnvironmentService elexisEnvironmentService;

	public EncounterSpotlightResultContributor() {
		super(SolrConstants.CORE_ENCOUNTERS);
	}

	@Activate
	public void activate() {
		super.activate(elexisEnvironmentService);
	}

	@Deactivate
	public void deactivate() {
		super.deactivate();
	}

	@Override
	protected void handleResponse(ISpotlightResult spotlightResult, QueryResponse response) {
		List<EncounterBean> beans = response.getBeans(EncounterBean.class);
		for (EncounterBean bean : beans) {
			spotlightResult.addEntry(Category.ENCOUNTER, bean.getLabel(), bean.getId(), null);
		}
	}

}
