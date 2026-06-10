package ch.elexis.global_inbox.core.handler;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.cdi.PortableServiceLoader;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.global_inbox.core.strategies.FallbackStrategy;
import ch.elexis.global_inbox.core.strategies.FilePrefixStrategy;
import ch.elexis.global_inbox.core.strategies.HierarchyStrategy;
import ch.elexis.global_inbox.core.strategies.IImportStrategy;
import ch.elexis.global_inbox.core.strategies.PatientFolderStrategy;
import ch.elexis.global_inbox.core.util.Constants;
import ch.elexis.global_inbox.core.util.ImportOmnivoreInboxUtil;

public class ImportOmnivore {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final String deviceName;
	private final ImportOmnivoreInboxUtil inboxUtil;

	public ImportOmnivore(String deviceName) {
		this.deviceName = deviceName;
		this.inboxUtil = new ImportOmnivoreInboxUtil();
	}

	protected IStatus run(IProgressMonitor monitor) {
		String filepath = inboxUtil.getDirectory(Constants.PREF_DIR_DEFAULT, deviceName);
		IVirtualFilesystemHandle dir = null;
		if (filepath == null) {
			filepath = Constants.PREF_DIR_DEFAULT;
			PortableServiceLoader.get(IConfigService.class).set(Constants.PREF_DIR, Constants.PREF_DIR_DEFAULT);
		}
		try {
			dir = PortableServiceLoader.get(IVirtualFilesystemService.class).of(filepath);
			addFilesInDirRecursive(dir, true);
		} catch (Exception e) {
			log.error("Failed to convert filepath to directory. Filepath: {}", filepath, e);
			return Status.CANCEL_STATUS;
		}

		return Status.OK_STATUS;
	}

	private int getPatientStrategyCode() {
		return PortableServiceLoader.get(IConfigService.class)
				.getGlobal(Constants.PREF_PATIENT_STRATEGY_PREFIX + deviceName, 0);
	}

	private IImportStrategy getStrategy(int code) {
		switch (code) {
		case 0:
			return new FilePrefixStrategy(inboxUtil, deviceName);
		case 1:
			return new PatientFolderStrategy(inboxUtil, deviceName);
		case 2:
			return new HierarchyStrategy(inboxUtil, deviceName);
		case 3:
		default:
			return new FallbackStrategy(new FilePrefixStrategy(inboxUtil, deviceName),
					new HierarchyStrategy(inboxUtil, deviceName));
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
					log.debug("No import rule matched for file [{}] using strategy [{}]", file.getAbsolutePath(),
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
				for (IVirtualFilesystemHandle handle : remaining) {
					if (!handle.exists()) {
						continue;
					}
					if (!handle.getName().startsWith(".")) {
						hasRealChildren = true;
						break;
					}
				}
			}

			if (!hasRealChildren) {
				try {
					dir.delete();
					log.info("Deleted empty import folder [{}]", dir.getAbsolutePath());
				} catch (IOException e) {
					log.warn("Could not delete folder [{}]", dir.getAbsolutePath(), e);
				}
			}
		}
	}
}