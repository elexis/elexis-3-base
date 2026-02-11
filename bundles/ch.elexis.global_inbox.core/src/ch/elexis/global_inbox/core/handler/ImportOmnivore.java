package ch.elexis.global_inbox.core.handler;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;
import ch.elexis.global_inbox.core.strategies.FallbackStrategy;
import ch.elexis.global_inbox.core.strategies.FilePrefixStrategy;
import ch.elexis.global_inbox.core.strategies.HierarchyStrategy;
import ch.elexis.global_inbox.core.strategies.IImportStrategy;
import ch.elexis.global_inbox.core.strategies.PatientFolderStrategy;
import ch.elexis.global_inbox.core.util.Constants;
import ch.elexis.global_inbox.core.util.ImportOmnivoreInboxUtil;

public class ImportOmnivore {

	private Logger log;
	private ImportOmnivoreInboxUtil giutil;
	private String deviceName;

	public ImportOmnivore(String deviceName) {
		log = LoggerFactory.getLogger(getClass());
		giutil = new ImportOmnivoreInboxUtil();
		this.deviceName = deviceName;
	}

	protected IStatus run(IProgressMonitor monitor) {
		String filepath = ImportOmnivoreInboxUtil.getDirectory(Constants.PREF_DIR_DEFAULT, deviceName);
		IVirtualFilesystemHandle dir = null;
		if (filepath == null) {
			filepath = Constants.PREF_DIR_DEFAULT;
			ConfigServiceHolder.get().set(Constants.PREF_DIR, Constants.PREF_DIR_DEFAULT);
		}
		try {
			dir = VirtualFilesystemServiceHolder.get().of(filepath);
			addFilesInDirRecursive(dir, true);
		} catch (Exception e) {
			log.error("Failed to convert filepath to directory. Filepath: {}", filepath, e);
			return Status.CANCEL_STATUS;
		}

		return Status.OK_STATUS;
	}

	private int getPatientStrategyCode() {
		return ConfigServiceHolder.getGlobal(Constants.PREF_PATIENT_STRATEGY_PREFIX + deviceName, 0);
	}

	private IImportStrategy getStrategy(int code) {
		switch (code) {
		case 0:
			return new FilePrefixStrategy(giutil, deviceName);
		case 1:
			return new PatientFolderStrategy(giutil, deviceName);
		case 2:
			return new HierarchyStrategy(giutil, deviceName);
		case 3:
		default:
			return new FallbackStrategy(new FilePrefixStrategy(giutil, deviceName),
					new HierarchyStrategy(giutil, deviceName));
		}
	}

	private void addFilesInDirRecursive(IVirtualFilesystemHandle dir, boolean isRoot) throws IOException {
		IVirtualFilesystemHandle[] files = dir.listHandles();
		if (files == null) {
			return;
		}
		int strategyCode = getPatientStrategyCode();
		IImportStrategy strategy = getStrategy(strategyCode);

		for (IVirtualFilesystemHandle file : files) {
			if (!file.exists() || file.getName().startsWith(".")) {
				continue;
			}
			if (file.isDirectory()) {
				addFilesInDirRecursive(file, false);
			} else {
				boolean imported = strategy.importFile(file);

				if (!imported) {
					log.debug("No import rule matched for file [{}] using strategy [{}]", file,
							strategy.getClass().getSimpleName());
				}
			}
		}
		cleanupDirectory(dir, isRoot);
	}

	private void cleanupDirectory(IVirtualFilesystemHandle dir, boolean isRoot) throws IOException {
		if (!isRoot) {
			IVirtualFilesystemHandle[] remaining = dir.listHandles();
			boolean hasRealChildren = false;

			if (remaining != null) {
				for (IVirtualFilesystemHandle h : remaining) {
					if (!h.exists()) {
						continue;
					}
					if (!h.getName().startsWith(".")) {
						hasRealChildren = true;
						break;
					}
				}
			}

			if (!hasRealChildren) {
				try {
					dir.delete();
					log.info("Deleted empty import folder [{}]", dir);
				} catch (IOException e) {
					log.warn("Could not delete folder [{}]", dir, e);
				}
			}
		}
	}
}