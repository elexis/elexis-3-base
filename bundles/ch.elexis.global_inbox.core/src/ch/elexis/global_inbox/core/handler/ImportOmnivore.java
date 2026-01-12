package ch.elexis.global_inbox.core.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;
import ch.elexis.global_inbox.core.util.Constants;
import ch.elexis.global_inbox.core.util.ImportOmnivoreInboxUtil;

public class ImportOmnivore {

	private final Pattern PATIENT_MATCH_PATTERN = Pattern.compile("([0-9]+)_(.+)"); //$NON-NLS-1$
	private final Pattern PATIENT_DIR_PATTERN = Pattern.compile("([0-9]+)(?:_[^0-9].*)?"); //$NON-NLS-1$

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

	private int getPatientStrategy() {
		return ConfigServiceHolder.getGlobal(Constants.PREF_PATIENT_STRATEGY_PREFIX + deviceName, 0);
	}

	private void addFilesInDirRecursive(IVirtualFilesystemHandle dir, boolean isRoot) throws IOException {
		IVirtualFilesystemHandle[] files = dir.listHandles();
		if (files == null) {
			return;
		}

		int strategy = getPatientStrategy();

		for (IVirtualFilesystemHandle file : files) {
			if (!file.exists() || file.getName().startsWith(".")) {
				continue;
			}
			if (file.isDirectory()) {
				addFilesInDirRecursive(file, false);
			} else {
				boolean imported = false;

				switch (strategy) {
				case 0:
					imported = importByFilePrefix(file);
					break;
				case 1:
					imported = importByPatientFolder(file);
					break;
				case 2:
					imported = importByHierarchy(file);
					break;
				case 3:
				default:
					imported = importByFilePrefix(file);
					if (!imported) {
						imported = importByHierarchy(file);
					}
					break;
				}

				if (!imported) {
					log.debug("No import rule matched for file [{}]", file);
				}
			}
		}

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

	private boolean importByFilePrefix(IVirtualFilesystemHandle file) {
		Matcher matcher = PATIENT_MATCH_PATTERN.matcher(file.getName());
		if (matcher.matches()) {
			String patientNo = matcher.group(1);
			String fileName = deviceName + "_" + matcher.group(2);
			String tryImportForPatient = giutil.tryImportForPatient(file, patientNo, fileName);
			if (tryImportForPatient != null) {
				log.info("Auto imported (FILE_PREFIX) file [{}], document id is [{}]", file, tryImportForPatient);
				return true;
			}
		}
		return false;
	}

	private boolean importByPatientFolder(IVirtualFilesystemHandle file) throws IOException {
		IVirtualFilesystemHandle parent = file.getParent();
		if (parent == null) {
			return false;
		}
		String parentName = parent.getName();
		Matcher m = PATIENT_DIR_PATTERN.matcher(parentName);
		if (!m.matches()) {
			return false;
		}

		String patientNo = m.group(1);
		String documentName = deviceName + "_" + file.getName();

		String tryImportForPatient = giutil.tryImportForPatient(file, patientNo, documentName);
		if (tryImportForPatient != null) {
			log.info("Auto imported (FOLDER_WITH_NAME) file [{}], document id is [{}]", file, tryImportForPatient);
			return true;
		}
		return false;
	}

	private boolean importByHierarchy(IVirtualFilesystemHandle file) {
		try {
			IVirtualFilesystemHandle current = file.getParent();
			if (current == null) {
				return false;
			}

			List<String> segmentNames = new ArrayList<>();
			String patientNo = null;
			while (current != null) {
				String name = current.getName();

				Matcher m = PATIENT_DIR_PATTERN.matcher(name);
				if (m.matches()) {
					patientNo = m.group(1);
					break;
				} else {
					if (!name.startsWith(".")) {
						segmentNames.add(name);
					}
				}

				current = current.getParent();
			}

			if (patientNo == null) {
				return false;
			}

			String rawFileName = file.getName();
			String baseFileName = rawFileName.startsWith("_") ? rawFileName.substring(1) : rawFileName;
			StringBuilder docNameBuilder = new StringBuilder();
			docNameBuilder.append(deviceName).append('_');

			if (!segmentNames.isEmpty()) {
				for (int i = segmentNames.size() - 1; i >= 0; i--) {
					docNameBuilder.append(segmentNames.get(i)).append('_');
				}
			}

			docNameBuilder.append(baseFileName);
			String documentName = docNameBuilder.toString();

			String tryImportForPatient = giutil.tryImportForPatient(file, patientNo, documentName);
			if (tryImportForPatient != null) {
				log.info("Auto imported (HIERARCHY) file [{}], document id is [{}]", file, tryImportForPatient);
				return true;
			}
		} catch (Exception e) {
			log.warn("Failed to import file via hierarchy for [{}]", file, e);
		}

		return false;
	}
}
