package ch.elexis.mednet.webapi.ui.handler;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;


public class ImportOmnivore {

	private final Pattern PATIENT_MATCH_PATTERN = Pattern.compile("([0-9]+)_(.+)"); //$NON-NLS-1$

	private Logger log;
	private ImportOmnivoreUtil giutil;


	public ImportOmnivore() {
		log = LoggerFactory.getLogger(getClass());
		giutil = new ImportOmnivoreUtil();

	}

	public IStatus run() {

		String filepath = getdownload();
		File dir = null;
		if (filepath == null) {
			filepath = PreferenceConstants.MEDNET_DOWNLOAD_PATH;
			ConfigServiceHolder.get().setLocal(PreferenceConstants.MEDNET_PLUGIN_STRING,
					PreferenceConstants.MEDNET_DOWNLOAD_PATH);
		}
		try {
			Path path = Paths.get(filepath);
			dir = path.toFile();

		} catch (Exception e) {
			return Status.CANCEL_STATUS;
		}
		addFilesInDirRecursive(dir);
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

	private String getdownload() {
		try {

			String pluginId = PreferenceConstants.MEDNET_PLUGIN_STRING;
			IEclipsePreferences node = InstanceScope.INSTANCE.getNode(pluginId);
			String downloadPath = node.get(PreferenceConstants.MEDNET_DOWNLOAD_PATH, "");

			return downloadPath;
		} catch (Exception e) {
			return "";
		}

	}
}