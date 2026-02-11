package ch.elexis.base.solr.task;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.ComponentException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.IIdentifiedRunnableFactory;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.IAccessControlService;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.tasks.model.ITaskService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@Component(immediate = true)
public class SolrIndexerIdentifiedRunnableFactory implements IIdentifiedRunnableFactory {

	@Inject
	@Reference
	ITaskService taskService;

	@Inject
	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	IModelService coreModelService;

	@Inject
	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.omnivore.data.model)")
	IModelService omnivoreModelService;

	@Inject
	@Reference
	IConfigService configService;

	@Inject
	@Reference
	IAccessControlService accessControlService;

	@Override
	public List<IIdentifiedRunnable> getProvidedRunnables() {
		List<IIdentifiedRunnable> ret = new ArrayList<>();
		ret.add(new EncounterIndexerIdentifiedRunnable(coreModelService, configService));
		ret.add(new LetterIndexerIdentifiedRunnable(coreModelService));
		ret.add(new DocumentIndexerIdentifiedRunnable(omnivoreModelService));
		return ret;
	}

	@PostConstruct
	@Activate
	public void activate() {
		accessControlService.doPrivileged(() -> {
			try {
				// FIXME switch to assert
				SolrIndexerIdentifiedRunnableTaskDescriptor.getOrCreateForEncounter((ITaskService) taskService);
				SolrIndexerIdentifiedRunnableTaskDescriptor.getOrCreateForLetter((ITaskService) taskService);
				SolrIndexerIdentifiedRunnableTaskDescriptor.getOrCreateForDocument((ITaskService) taskService);
			} catch (TaskException e) {
				LoggerFactory.getLogger(getClass()).error("initialize", e);
				throw new ComponentException(e);
			}
			taskService.bindIIdentifiedRunnableFactory(this);
		});
	}

	@PreDestroy
	@Deactivate
	public void deactivate() {
		taskService.unbindIIdentifiedRunnableFactory(this);
	}

}
