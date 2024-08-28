package ch.elexis.global_inbox.core.service;

import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.tasks.model.ITaskDescriptor;
import ch.elexis.core.tasks.model.ITaskService;
import ch.elexis.core.tasks.model.TaskTriggerType;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

@Component(property = EventConstants.EVENT_TOPIC + "=" + UIEvents.UILifeCycle.APP_STARTUP_COMPLETE)
public class TaskStartupHandler implements EventHandler {

	public static final String PREFERENCE_BRANCH = "plugins/global_inbox_server/"; //$NON-NLS-1$
	public static final String PREF_DEVICES = PREFERENCE_BRANCH + "devices"; //$NON-NLS-1$

	@Reference
	private ITaskService taskService;

	@Override
	public void handleEvent(Event event) {
		startTasksOnStartup();
	}

	private void startTasksOnStartup() {
		IPreferenceStore preferenceStore = new SettingsPreferenceStore(CoreHub.localCfg);
		String devices = preferenceStore.getString(PREF_DEVICES);

		if (StringUtils.isNotBlank(devices)) {
			String[] deviceArray = devices.split(",");
			for (String device : deviceArray) {
				try {
					Optional<ITaskDescriptor> taskDescriptorOpt = taskService
							.findTaskDescriptorByIdOrReferenceId(device);
					if (taskDescriptorOpt.isPresent()) {
						ITaskDescriptor taskDescriptor = taskDescriptorOpt.get();
						if (taskDescriptor.getTriggerType() == TaskTriggerType.FILESYSTEM_CHANGE) {
							taskService.trigger(taskDescriptor, null, TaskTriggerType.FILESYSTEM_CHANGE,
									Collections.emptyMap());
						}
					}
				} catch (TaskException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
