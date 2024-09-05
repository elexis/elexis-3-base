package ch.elexis.global_inbox.core.handler;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IConfigService.ILocalLock;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.global_inbox.core.util.Constants;
import ch.elexis.global_inbox.core.util.GlobalInboxUtil;

public class ImportOmnivore {



	private static final String LOCAL_LOCK_INBOXIMPORT = "GlobalInboxImport"; //$NON-NLS-1$
	private final Pattern PATIENT_MATCH_PATTERN = Pattern.compile("([0-9]+)_(.+)"); //$NON-NLS-1$

	private Logger log;
	private GlobalInboxUtil giutil;
	private String deviceName;

	public ImportOmnivore(String deviceName) {
		log = LoggerFactory.getLogger(getClass());
		giutil = new GlobalInboxUtil();
		this.deviceName = deviceName;
	}

	protected IStatus run(IProgressMonitor monitor) {
		ILocalLock lock = ConfigServiceHolder.get().getLocalLock(LOCAL_LOCK_INBOXIMPORT);
		if (lock.tryLock()) {
			String filepath = GlobalInboxUtil.getDirectory(Constants.PREF_DIR_DEFAULT, deviceName);
			File dir = null;
			if (filepath == null) {
				filepath = Constants.PREF_DIR_DEFAULT;
				ConfigServiceHolder.get().setLocal(Constants.PREF_DIR, Constants.PREF_DIR_DEFAULT);
			}
			try {
				URI uri = new URI(filepath);
				Path path = Paths.get(uri);
				dir = path.toFile();
			} catch (Exception e) {
				return Status.CANCEL_STATUS;
			}
			addFilesInDirRecursive(dir);
		} else {
			long lockMillis = lock.getLockCurrentMillis();
			if (lockMillis == -1 || (System.currentTimeMillis() - lockMillis) > (120000L * 2L)) {
				log.warn("Removing pending lock " + lock.getLockMessage() + "@" + lockMillis);
				lock.unlock();
			}
		}
		ConfigServiceHolder.get().getManagedLock(LOCAL_LOCK_INBOXIMPORT)
				.ifPresent(localDocumentLock -> localDocumentLock.unlock());
		return Status.OK_STATUS;
	}

	private void addFilesInDirRecursive(File dir) {
		List<String> allFilesInDirRecursive = new ArrayList<>();
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (file.isHidden() || file.getName().startsWith(".")) {
				continue;
			}
			if (file.isDirectory()) {
				addFilesInDirRecursive(file);
			} else {
				allFilesInDirRecursive.add(file.getAbsolutePath());
			}
			if (file.isDirectory()) {
				addFilesInDirRecursive(file);
			} else {
				Matcher matcher = PATIENT_MATCH_PATTERN.matcher(file.getName());
				if (matcher.matches()) {
					String patientNo = matcher.group(1);
					String fileName = matcher.group(2);
					String tryImportForPatient = giutil.tryImportForPatient(file, patientNo, fileName);
					if (tryImportForPatient != null) {
						log.info("Auto imported file [{}], document id is [{}]", file, tryImportForPatient);
						continue;
					}
				}
				allFilesInDirRecursive.add(file.getAbsolutePath());
			}
		}
		allFilesInDirRecursive.sort(Comparator.comparingInt(String::length));
		List<File> extensionFiles = new ArrayList<>();
		for (String string : allFilesInDirRecursive) {
			File file = new File(string);
			if (extensionFiles.contains(file)) {
				continue;
			}
			File[] _extensionFiles = dir.listFiles(
					(_dir, _name) -> _name.startsWith(file.getName()) && !Objects.equals(_name, file.getName()));
			extensionFiles.addAll(Arrays.asList(_extensionFiles));
		}
	}
}