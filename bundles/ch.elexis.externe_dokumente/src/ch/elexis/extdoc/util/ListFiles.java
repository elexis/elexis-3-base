package ch.elexis.extdoc.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.data.Patient;
import ch.elexis.extdoc.preferences.PreferenceConstants;
import ch.rgw.tools.StringTool;

public class ListFiles {

	/**
	 * Return all active directories of the active patient
	 *
	 * @param actPatient the actual patient, may be null
	 *
	 * @return a list of directories (maybe empty)
	 */
	public static List<File> getDirectoriesForActPatient(Patient actPatient) {
		String[] paths = PreferenceConstants.getActiveBasePaths();
		List<File> list = new ArrayList<File>();
		if (actPatient == null) {
			return list;
		}
		String name = actPatient.getName();
		String vorname = actPatient.getVorname();
		String geburtsDatum = actPatient.getGeburtsdatum();
		/*
		 * Here we load all files in the selected paths and all their sub directories
		 */
		FileFiltersConvention convention = new FileFiltersConvention(name, vorname);

		for (String path : paths) {
			if (!StringTool.isNothing(path)) {
				File mainDirectory = new File(path);
				if (mainDirectory.isDirectory()) {
					String subDir = new String(mainDirectory + File.separator + convention.getShortName())
							+ StringUtils.SPACE + MatchPatientToPath.geburtsDatumToCanonical(geburtsDatum);
					File subDirectory = new File(subDir);
					File[] files = subDirectory.listFiles();
					if (files != null) {
						list.add(subDirectory);
					}
				}
			}
		}
		return list;
	}

	/**
	 * Return all external files
	 *
	 * @param paths        the path from where to load files; may be null
	 * @param name         family name of the concerned patient
	 * @param vorname      name of the concerned patient
	 * @param geburtsDatum geburtsDatum of the concerned patient
	 *
	 * @return a list of files (maybe empty)
	 */
	public static List<File> getList(String[] paths, String name, String vorname, String geburtsDatum,
			FilenameFilter filter) {
		{
			List<File> list = new ArrayList<File>();
			/*
			 * Here we load all files in the selected paths and all their sub directories
			 */
			FileFiltersConvention convention = new FileFiltersConvention(name, vorname);

			for (String path : paths) {
				if (!StringTool.isNothing(path)) {
					File mainDirectory = new File(path);
					if (mainDirectory.isDirectory()) {
						File[] files = mainDirectory.listFiles(filter);
						if (files != null) {
							for (File file : files) {
								if (file.isFile())
									list.add(file);
							}
						}
						String subDir = new String(mainDirectory + File.separator + convention.getShortName())
								+ StringUtils.SPACE + MatchPatientToPath.geburtsDatumToCanonical(geburtsDatum);
						File subDirectory = new File(subDir);
						files = subDirectory.listFiles();
						if (files != null) {
							for (File file : files) {
								list.add(file);
							}
						}
					}
				}
			}
			return list;
		}
	}
}
