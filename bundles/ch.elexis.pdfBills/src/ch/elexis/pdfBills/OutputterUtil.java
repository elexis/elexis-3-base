package ch.elexis.pdfBills;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.preferences.PreferencesUtil;
import ch.elexis.core.services.LocalConfigService;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class OutputterUtil {

	public static final String CFG_ROOT = "pdf-outputter/"; //$NON-NLS-1$

	public static final String CFG_PRINT_GLOBALOUTPUTDIRS = CFG_ROOT + "global.output.dirs"; //$NON-NLS-1$

	public static final String CFG_PRINT_GLOBALPDFDIR = CFG_ROOT + "global.output.pdfdir"; //$NON-NLS-1$
	public static final String CFG_PRINT_GLOBALXMLDIR = CFG_ROOT + "global.output.xmldir"; //$NON-NLS-1$

	public static final String CFG_PRINT_BESR = "print.besr"; //$NON-NLS-1$
	public static final String CFG_PRINT_RF = "print.rf"; //$NON-NLS-1$

	/**
	 * Test if global output directories should be used.
	 *
	 * @return
	 */
	public static boolean useGlobalOutputDirs() {
		return hasGlobalDirectories() && LocalConfigService.get(CFG_PRINT_GLOBALOUTPUTDIRS, true);
	}

	private static boolean hasGlobalDirectories() {
		return StringUtils
				.isNotBlank(PreferencesUtil.getOsSpecificPreference(CFG_PRINT_GLOBALPDFDIR, ConfigServiceHolder.get()))
				&& StringUtils.isNotBlank(
						PreferencesUtil.getOsSpecificPreference(CFG_PRINT_GLOBALXMLDIR, ConfigServiceHolder.get()));
	}

	public static String getXmlOutputDir(String configRoot) {
		if (useGlobalOutputDirs()) {
			return PreferencesUtil.getOsSpecificPreference(CFG_PRINT_GLOBALXMLDIR, ConfigServiceHolder.get());
		} else {
			return LocalConfigService.get(configRoot + RnOutputter.XMLDIR, StringUtils.EMPTY);
		}
	}

	public static String getPdfOutputDir(String configRoot) {
		if (useGlobalOutputDirs()) {
			return PreferencesUtil.getOsSpecificPreference(CFG_PRINT_GLOBALPDFDIR, ConfigServiceHolder.get());
		} else {
			return LocalConfigService.get(configRoot + RnOutputter.PDFDIR, StringUtils.EMPTY);
		}
	}

	/**
	 * If one of BESR or PF pdf output is configured, test if there is an output dir
	 * configured with the provided config root. If no pdf output is required return
	 * true.
	 * 
	 * @param configRoot
	 * @return
	 */
	public static boolean isPdfOutputDirValid(String configRoot) {
		if (LocalConfigService.get(configRoot + CFG_PRINT_BESR, true)
				|| LocalConfigService.get(configRoot + CFG_PRINT_RF, true)) {
			return StringUtils.isNotBlank(OutputterUtil.getPdfOutputDir(configRoot));
		}
		return true;
	}
}
