package ch.elexis.fire.core.task;

import ch.elexis.core.eenv.IElexisEnvironmentService;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.tasks.model.ITaskDescriptor;
import ch.elexis.core.tasks.model.ITaskService;
import ch.elexis.core.tasks.model.TaskTriggerType;

public class FIREExportTaskDescriptor {

	/**
	 * Assert that the task descriptor is present in the database. If it is not yet
	 * present, generate it with reasonable default values. It is not activated by
	 * default.
	 */
	public static ITaskDescriptor getOrCreate(ITaskService taskService) throws TaskException {
		ITaskDescriptor taskDescriptor = taskService
				.findTaskDescriptorByIdOrReferenceId(Constants.FIRE_EXPORT_TASK_DESCRIPTOR_REFERENCE_ID)
				.orElse(null);
		if (taskDescriptor == null) {
			taskDescriptor = taskService.createTaskDescriptor(new FIREExportIdentifiedRunnable(null));
			taskDescriptor.setReferenceId(Constants.FIRE_EXPORT_TASK_DESCRIPTOR_REFERENCE_ID);
			taskDescriptor.setTriggerType(TaskTriggerType.MANUAL);
			taskDescriptor.setRunner(IElexisEnvironmentService.ES_STATION_ID_DEFAULT);
			taskDescriptor.setSystem(true);
			taskDescriptor.setActive(true);

			taskService.saveTaskDescriptor(taskDescriptor);
		}
		return taskDescriptor;
	}

}
