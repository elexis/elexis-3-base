package ch.elexis.global_inbox.core.service;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.IIdentifiedRunnableFactory;
import ch.elexis.core.tasks.model.ITaskService;
import ch.elexis.global_inbox.core.handler.ImportOmnivoreIdentifiedRunnable;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@Startup
@Component(immediate = true)
public class IdentifiedRunnableFactoryImplMover implements IIdentifiedRunnableFactory {

	@Inject
	@Reference
	private ITaskService taskService;

	@PostConstruct
	@Activate
	public void activate() {
		taskService.bindIIdentifiedRunnableFactory(this);
	}

	@PreDestroy
	@Deactivate
	public void deactivate() {
		taskService.unbindIIdentifiedRunnableFactory(this);
	}

	@Override
	public List<IIdentifiedRunnable> getProvidedRunnables() {
		List<IIdentifiedRunnable> ret = new ArrayList<>();
		ret.add(new ImportOmnivoreIdentifiedRunnable());
		return ret;
	}
}
