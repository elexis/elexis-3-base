package ch.elexis.tasks.integration.test.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.tasks.model.ITaskService;

@Component(immediate = true)
public class TaskServiceHolder {

	private static ITaskService taskService;

	@Reference
	public void setModelService(ITaskService taskService) {
		TaskServiceHolder.taskService = taskService;
	}

	public static ITaskService get() {
		if (taskService == null) {
			throw new IllegalStateException("No ITaskService available");
		}
		return taskService;
	}
}