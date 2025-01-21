package ch.elexis.global_inbox.core.handler;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.slf4j.Logger;

import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.LocalConfigService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.global_inbox.core.util.Constants;

public class ImportOmnivoreIdentifiedRunnable implements IIdentifiedRunnable {
	
	private IVirtualFilesystemService virtualFilesystemService;
	private String eventFilePath;
	private String destinationDir;
	private String deviceName;
	private Map<String, String> lastContextState = new ConcurrentHashMap<>();

	public ImportOmnivoreIdentifiedRunnable(IVirtualFilesystemService virtualFilesystemService) {
		this.virtualFilesystemService = virtualFilesystemService;
	}

	@Override
	public String getId() {
		return Constants.IMPORTFILETOOMNIVORE;
	}

	@Override
	public String getLocalizedDescription() {
		return "Move files from a directory to Omnivore directory and then import them";
	}

	@Override
	public synchronized Map<String, Serializable> run(Map<String, Serializable> context,
			IProgressMonitor progressMonitor, Logger logger) throws TaskException {
		eventFilePath = null;
		destinationDir = null;
		deviceName = null;
		for (Map.Entry<String, Serializable> entry : context.entrySet()) {
			String key = entry.getKey();
			Serializable value = entry.getValue();
			if ("url".equals(key)) {
				eventFilePath = (String) value;
			} else if ("destinationDir".equals(key)) {
				destinationDir = (String) value;
			} else if ("referenceId".equals(key)) {
				deviceName = (String) value;
			}
		}
		if (eventFilePath == null || destinationDir == null || deviceName == null) {
			throw new TaskException(TaskException.EXECUTION_ERROR,
					"Missing required run-context-parameter(s): [url, destinationDir, referenceId]");
		}
		String destinationDirPreference = LocalConfigService.get(Constants.PREF_LAST_SELECTED_CATEGORY, null);
		if (destinationDirPreference != null && !destinationDirPreference.isEmpty()) {
			int lastSlashIndex = destinationDir.lastIndexOf('\\');
			if (lastSlashIndex != -1) {
				destinationDir = destinationDir.substring(0, lastSlashIndex + 1) + destinationDirPreference;
			}
		}
		moveFiles(progressMonitor, logger);
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

	public boolean shouldRestartTask(Map<String, Serializable> context) {
		String newEventFilePath = (String) context.get("url");
		String newDestinationDir = (String) context.get("destinationDir");
		String referenceId = (String) context.get("referenceId");
		String lastEventFilePath = lastContextState.get(referenceId + "_url");
		String lastDestinationDir = lastContextState.get(referenceId + "_destinationDir");
		return !newEventFilePath.equals(lastEventFilePath) || !newDestinationDir.equals(lastDestinationDir);
	}

	public void moveFiles(IProgressMonitor monitor, Logger logger) throws TaskException {
		try {
			IVirtualFilesystemHandle vfseventFilePathHandle = virtualFilesystemService.of(eventFilePath);
			IVirtualFilesystemHandle vfsdestinationDirHandle = virtualFilesystemService.of(destinationDir);
			Path sourcePath = Paths.get(vfseventFilePathHandle.getURI());
			Path targetDir = Paths.get(vfsdestinationDirHandle.getURI());
			if (!Files.exists(targetDir)) {
				Files.createDirectories(targetDir);
			} else if (!Files.isDirectory(targetDir)) {
				throw new TaskException(TaskException.EXECUTION_ERROR, destinationDir + " is not a directory");
			}

			if (Files.isDirectory(sourcePath)) {
				try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourcePath)) {
					for (Path filePath : directoryStream) {
						moveSingleFile(filePath, targetDir, logger);
					}
				}
			} else {
				moveSingleFile(sourcePath, targetDir, logger);
			}
		} catch (IOException e) {
			throw new TaskException(TaskException.EXECUTION_ERROR, "Error moving files from [" + eventFilePath + "]");
		}
	}

	private void moveSingleFile(Path sourcePath, Path targetDir, Logger logger) throws IOException, TaskException {
		Path targetPath = targetDir.resolve(sourcePath.getFileName());
		if (Files.exists(targetPath)) {
			String fileName = sourcePath.getFileName().toString();
			String newFileName = appendCopyToFileName(fileName);
			targetPath = targetDir.resolve(newFileName);
		}
		Files.copy(sourcePath, targetPath);
		Files.delete(sourcePath);
		logger.info("Moved {} to {}", sourcePath.toString(), targetPath.toString());
	}

	private String appendCopyToFileName(String fileName) {
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex == -1) {
			return fileName + "_copy";
		} else {
			return fileName.substring(0, dotIndex) + "_copy" + fileName.substring(dotIndex);
		}
	}
}