package ch.elexis.global_inbox.core.handler;

import ch.elexis.core.eenv.IElexisEnvironmentService;
import ch.elexis.core.exceptions.AccessControlException;
import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.IIdentifiedRunnable.RunContextParameter;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.tasks.model.ITaskDescriptor;
import ch.elexis.core.tasks.model.ITaskService;
import ch.elexis.core.tasks.model.TaskTriggerType;
import ch.elexis.core.tasks.model.TaskTriggerTypeParameter;
import ch.elexis.core.services.IVirtualFilesystemService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.LoggerFactory;

public class TaskManagerHandler {

	private ITaskService taskService;
	private IVirtualFilesystemService virtualFilesystemService;

	public TaskManagerHandler(ITaskService taskService, IVirtualFilesystemService virtualFilesystemService) {
		this.taskService = taskService;
		this.virtualFilesystemService = virtualFilesystemService;
	}

	public ITaskDescriptor getTaskDescriptorByReferenceId(String referenceId) {
		Optional<ITaskDescriptor> taskDescriptorOpt = taskService.findTaskDescriptorByIdOrReferenceId(referenceId);
		return taskDescriptorOpt.orElse(null);
	}

	public void createAndConfigureTask(String referenceId, String url, String destinationDir) {
		try {
			Optional<ITaskDescriptor> existingTaskDescriptorOpt = taskService
					.findTaskDescriptorByIdOrReferenceId(referenceId);
			if (existingTaskDescriptorOpt.isPresent()) {
				ITaskDescriptor existingTaskDescriptor = existingTaskDescriptorOpt.get();
				ensureNotDeletedById(existingTaskDescriptor.getReferenceId());
				existingTaskDescriptor.setTriggerParameter(TaskTriggerTypeParameter.FILESYSTEM_CHANGE.URL, url);
				existingTaskDescriptor.setRunContextParameter(RunContextParameter.STRING_URL, url);
				existingTaskDescriptor.setRunContextParameter("destinationDir", destinationDir);
				existingTaskDescriptor.setActive(true);
				existingTaskDescriptor.setRunner(IElexisEnvironmentService.ES_STATION_ID_DEFAULT);
				taskService.saveTaskDescriptor(existingTaskDescriptor);
			} else {
				IIdentifiedRunnable runnable = new MoveFileIdentifiedRunnable(virtualFilesystemService);
				ITaskDescriptor newTaskDescriptor = taskService.createTaskDescriptor(runnable);
				newTaskDescriptor.setReferenceId(referenceId);
				newTaskDescriptor.setTriggerType(TaskTriggerType.FILESYSTEM_CHANGE);
				newTaskDescriptor.setTriggerParameter(TaskTriggerTypeParameter.FILESYSTEM_CHANGE.URL, url);
				newTaskDescriptor.setActive(true);
				newTaskDescriptor.setRunner(IElexisEnvironmentService.ES_STATION_ID_DEFAULT);

				Map<String, Serializable> runContext = new HashMap<>();
				runContext.put(RunContextParameter.STRING_URL, url);
				runContext.put("destinationDir", destinationDir);
				newTaskDescriptor.setRunContext(runContext);

				taskService.saveTaskDescriptor(newTaskDescriptor);
			}
		} catch (TaskException e) {
			e.printStackTrace();
		}
	}

	private void ensureNotDeletedById(String referenceId) {
		Optional<ITaskDescriptor> taskDescriptorOpt = taskService.findTaskDescriptorByIdOrReferenceId(referenceId);
		if (taskDescriptorOpt.isPresent()) {
			ITaskDescriptor taskDescriptor = taskDescriptorOpt.get();
			if (taskDescriptor.getReferenceId().equals(referenceId)) {
				try {
					if (taskDescriptor.isDeleted()) {
						taskDescriptor.setDeleted(false);
						taskService.saveTaskDescriptor(taskDescriptor);
					}
				} catch (TaskException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void deleteTaskDescriptorByReferenceId(String referenceId) {
		Optional<ITaskDescriptor> taskDescriptorOpt = taskService.findTaskDescriptorByIdOrReferenceId(referenceId);
		if (taskDescriptorOpt.isPresent()) {
			ITaskDescriptor taskDescriptor = taskDescriptorOpt.get();
			try {
				taskDescriptor.setActive(false);
				taskDescriptor.setDeleted(true);
				taskService.saveTaskDescriptor(taskDescriptor);
			} catch (AccessControlException e) {
				LoggerFactory.getLogger(TaskManagerHandler.class).error("Berechtigungsfehler: " + e.getMessage());
			} catch (TaskException e) {
				e.printStackTrace();
			}
		}
	}



}
