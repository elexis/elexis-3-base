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
import java.util.Collections;
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
			ITaskDescriptor taskDescriptor;
			ImportOmnivoreIdentifiedRunnable test = new ImportOmnivoreIdentifiedRunnable(virtualFilesystemService);
			IIdentifiedRunnable runnable = test;

			if (existingTaskDescriptorOpt.isPresent()) {
				taskDescriptor = existingTaskDescriptorOpt.get();
				ensureNotDeletedById(taskDescriptor.getReferenceId());
				taskDescriptor.setTriggerParameter(TaskTriggerTypeParameter.FILESYSTEM_CHANGE.URL, url);
				taskDescriptor.setRunContextParameter(RunContextParameter.STRING_URL, url);
				taskDescriptor.setRunContextParameter("destinationDir", destinationDir);
				taskDescriptor.setRunContextParameter("referenceId", referenceId);
				taskDescriptor.setActive(true);
				taskDescriptor.setRunner(IElexisEnvironmentService.ES_STATION_ID_DEFAULT);
				taskService.saveTaskDescriptor(taskDescriptor);
				taskService.refresh(taskDescriptor);
			} else {
				taskDescriptor = taskService.createTaskDescriptor(runnable);
				taskDescriptor.setReferenceId(referenceId);
				taskDescriptor.setTriggerType(TaskTriggerType.FILESYSTEM_CHANGE);
				taskDescriptor.setTriggerParameter(TaskTriggerTypeParameter.FILESYSTEM_CHANGE.URL, url);
				taskDescriptor.setActive(true);
				taskDescriptor.setRunner(IElexisEnvironmentService.ES_STATION_ID_DEFAULT);
				Map<String, Serializable> runContext = new HashMap<>();
				runContext.put(RunContextParameter.STRING_URL, url);
				runContext.put("destinationDir", destinationDir);
				runContext.put("referenceId", referenceId);
				taskDescriptor.setRunContext(runContext);

				taskService.saveTaskDescriptor(taskDescriptor);
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
				taskService.setActive(taskDescriptor, false);
				taskService.saveTaskDescriptor(taskDescriptor);

			} catch (AccessControlException e) {
				LoggerFactory.getLogger(TaskManagerHandler.class).error("Berechtigungsfehler: " + e.getMessage());
			} catch (TaskException e) {
				e.printStackTrace();
			}
		}
	}

}
