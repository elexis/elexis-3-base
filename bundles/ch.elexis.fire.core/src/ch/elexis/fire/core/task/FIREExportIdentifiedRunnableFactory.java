package ch.elexis.fire.core.task;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.ComponentException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.IIdentifiedRunnableFactory;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.tasks.model.ITaskService;
import ch.elexis.fire.core.IFIREService;

@Component(immediate = true)
public class FIREExportIdentifiedRunnableFactory implements IIdentifiedRunnableFactory {

	@Reference
	private ITaskService taskService;

	@Reference
	private IFIREService fireService;

	@Activate
	public void activate() {
		try {
			FIREExportTaskDescriptor.getOrCreate(taskService);
		} catch (TaskException e) {
			throw new ComponentException(e);
		}
		taskService.bindIIdentifiedRunnableFactory(this);
	}

	@Deactivate
	public void deactivate() {
		taskService.unbindIIdentifiedRunnableFactory(this);
	}

	@Override
	public List<IIdentifiedRunnable> getProvidedRunnables() {
		List<IIdentifiedRunnable> ret = new ArrayList<>();
		ret.add(new FIREExportIdentifiedRunnable(fireService));
		return ret;
	}

}
