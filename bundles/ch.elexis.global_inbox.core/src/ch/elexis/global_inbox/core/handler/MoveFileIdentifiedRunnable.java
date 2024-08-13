package ch.elexis.global_inbox.core.handler;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;

import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;

import ch.elexis.global_inbox.core.util.Constants;

public class MoveFileIdentifiedRunnable implements IIdentifiedRunnable {

	private IVirtualFilesystemService virtualFilesystemService;

	public MoveFileIdentifiedRunnable(IVirtualFilesystemService virtualFilesystemService) {
		this.virtualFilesystemService = virtualFilesystemService;
	}

	@Override
	public String getId() {
		return Constants.IMPORTFILETOOMNIVORE;
	}

	@Override
	public Map<String, Serializable> run(Map<String, Serializable> context, IProgressMonitor progressMonitor,
			Logger logger) throws TaskException {
		String eventFilePath = (String) context.get(RunContextParameter.STRING_URL);
		String destinationDir = (String) context.get("destinationDir");

		if (eventFilePath == null || destinationDir == null) {
			throw new TaskException(TaskException.EXECUTION_ERROR,
					"Missing required run-context-parameter(s): [url, destinationDir]");
		}

		try {
			IVirtualFilesystemHandle vfsHandle = virtualFilesystemService.of(eventFilePath);
			Path sourcePath = Paths.get(vfsHandle.getURI());
			Path targetDir = Paths.get(destinationDir);
			if (!Files.exists(targetDir)) {
				Files.createDirectories(targetDir);
			} else if (!Files.isDirectory(targetDir)) {
				throw new TaskException(TaskException.EXECUTION_ERROR, destinationDir + " is not a directory");
			}

			if (Files.isDirectory(sourcePath)) {
				try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourcePath)) {
					for (Path filePath : directoryStream) {
						Path targetPath = targetDir.resolve(filePath.getFileName());
						Files.move(filePath, targetPath);
						logger.info("Moved {} to {}", filePath.toString(), targetPath.toString());
					}
				}
			} else {
				Path targetPath = targetDir.resolve(sourcePath.getFileName());
				Files.move(sourcePath, targetPath);
				logger.info("Moved {} to {}", sourcePath.toString(), targetPath.toString());
			}
		} catch (IOException e) {
			throw new TaskException(TaskException.EXECUTION_ERROR, "Error moving files from [" + eventFilePath + "]",
					e);
		}

		return null;
	}

	@Override
	public String getLocalizedDescription() {
		return "Move files from a directory to Omnivore directory";
	}

	@Override
	public Map<String, Serializable> getDefaultRunContext() {
		return Collections.singletonMap(RunContextParameter.STRING_URL,
				RunContextParameter.VALUE_MISSING_REQUIRED);
	}
}
