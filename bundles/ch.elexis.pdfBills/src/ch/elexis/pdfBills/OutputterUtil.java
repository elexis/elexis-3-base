package ch.elexis.pdfBills;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.data.activator.CoreHub;

public class OutputterUtil {

	public static final String CFG_ROOT = "pdf-outputter/"; //$NON-NLS-1$

	public static final String CFG_PRINT_GLOBALOUTPUTDIRS = CFG_ROOT + "global.output.dirs"; //$NON-NLS-1$

	public static final String CFG_PRINT_GLOBALPDFDIR = CFG_ROOT + "global.output.pdfdir"; //$NON-NLS-1$
	public static final String CFG_PRINT_GLOBALXMLDIR = CFG_ROOT + "global.output.xmldir"; //$NON-NLS-1$

	/**
	 * Test if global output directories should be used.
	 * 
	 * @return
	 */
	public static boolean useGlobalOutputDirs() {
		return hasGlobalDirectories() && CoreHub.localCfg.get(CFG_PRINT_GLOBALOUTPUTDIRS, true);
	}

	private static boolean hasGlobalDirectories() {
		return StringUtils.isNotBlank(CoreHub.globalCfg.get(CFG_PRINT_GLOBALPDFDIR, null))
				&& StringUtils.isNotBlank(CoreHub.globalCfg.get(CFG_PRINT_GLOBALXMLDIR, null));
	}

	public static String getXmlOutputDir(String configRoot) {
		if (useGlobalOutputDirs()) {
			return CoreHub.globalCfg.get(CFG_PRINT_GLOBALXMLDIR, StringUtils.EMPTY);
		} else {
			return CoreHub.localCfg.get(configRoot + RnOutputter.XMLDIR, StringUtils.EMPTY);
		}
	}

	public static String getPdfOutputDir(String configRoot) {
		if (useGlobalOutputDirs()) {
			return CoreHub.globalCfg.get(CFG_PRINT_GLOBALPDFDIR, StringUtils.EMPTY);
		} else {
			return CoreHub.localCfg.get(configRoot + RnOutputter.PDFDIR, StringUtils.EMPTY);
		}
	}
}
