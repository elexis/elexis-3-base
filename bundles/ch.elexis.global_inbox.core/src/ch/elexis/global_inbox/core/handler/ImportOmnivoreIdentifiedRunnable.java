package ch.elexis.global_inbox.core.handler;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.slf4j.Logger;

import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.global_inbox.core.util.Constants;

public class ImportOmnivoreIdentifiedRunnable implements IIdentifiedRunnable {
	

	private String eventFilePath;

	private String deviceName;

	@Override
	public String getId() {
		return Constants.IMPORTFILETOOMNIVORE;
	}

	@Override
	public String getLocalizedDescription() {
		return "Monitor a directory and import incoming files into the Omnivore";
	}

	@Override
	public synchronized Map<String, Serializable> run(Map<String, Serializable> context,
			IProgressMonitor progressMonitor, Logger logger) throws TaskException {
		eventFilePath = null;

		deviceName = null;
		for (Map.Entry<String, Serializable> entry : context.entrySet()) {
			String key = entry.getKey();
			Serializable value = entry.getValue();
			if ("url".equals(key)) {
				eventFilePath = (String) value;
			} else if ("referenceId".equals(key)) {
				deviceName = (String) value;
			}
		}
		if (eventFilePath == null || deviceName == null) {
			throw new TaskException(TaskException.EXECUTION_ERROR,
					"Missing required run-context-parameter(s): [url, destinationDir, referenceId]");
		}

		try {
			IStatus status = new ImportOmnivore(deviceName).run(progressMonitor);
			if (!status.isOK()) {
				throw new TaskException(TaskException.EXECUTION_ERROR,
						"Import failed with status: " + status.getMessage());
			}

		} catch (Exception ex) {
			throw new TaskException(TaskException.EXECUTION_ERROR,
					"An error occurred during the import process: " + ex.getMessage());
		}
		return null;
	}

	@Override
	public Map<String, Serializable> getDefaultRunContext() {
		return Collections.singletonMap("url", (Serializable) "DefaultURLValue");
	}

}