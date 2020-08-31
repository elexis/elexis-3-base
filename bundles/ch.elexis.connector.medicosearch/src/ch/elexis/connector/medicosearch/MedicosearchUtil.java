package ch.elexis.connector.medicosearch;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.connector.medicosearch.ui.MedicosearchPreferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.UiDesk;

public class MedicosearchUtil {
	private static final Logger log = LoggerFactory.getLogger(MedicosearchUtil.class);
	
	private static final String BUNDLE_NAME = "ch.elexis.connector.medicosearch"; //$NON-NLS-1$
	private static final String JAR_FILE =
		"lib/net.medshare.connector.medicosearch_2.1.6.20120925.jar";
	private static final String CONFIG_FILE = "conf/connector.properties";
	
	private static MedicosearchUtil instance = null;
	
	private static File bundleDir;
	private static File cfgFile;
	private static File jarFile;
	
	private MedicosearchUtil(){
		initLocations();
	}
	
	public static MedicosearchUtil getInstance(){
		if (instance == null) {
			instance = new MedicosearchUtil();
		}
		return instance;
	}
	
	private void initLocations(){
		Bundle bundle = Platform.getBundle(BUNDLE_NAME);
		
		// resolve needed files and dir
		cfgFile = resolveByFileURL(CONFIG_FILE, bundle);
		jarFile = resolveByFileURL(JAR_FILE, bundle);
		if (cfgFile != null) {
			bundleDir = cfgFile.getParentFile().getParentFile();
			updatePreferenceSettingsIfNeeded();
		}
		
		if (cfgFile == null || jarFile == null) {
			showWarningMessage();
		}
	}
	
	private void updatePreferenceSettingsIfNeeded(){
		String cfgSetting =
			ConfigServiceHolder.getGlobal(MedicosearchPreferences.CFG_MEDICOSEARCH_CONFIG, null);
		if (cfgSetting == null || !cfgSetting.equals(cfgFile.getAbsolutePath())) {
			ConfigServiceHolder.setGlobal(MedicosearchPreferences.CFG_MEDICOSEARCH_CONFIG,
				cfgFile.getAbsolutePath());
			CoreHub.globalCfg.flush();
		}
	}
	
	private File resolveByFileURL(String filePath, Bundle bundle){
		try {
			URL url = FileLocator.find(bundle, new Path(filePath), null);
			if (url != null) {
				File file = new File(FileLocator.toFileURL(url).getFile());
				if (file != null && file.exists()) {
					return file;
				}
			}
		} catch (IOException e) {
			log.error("Error resolving file location [" + filePath + "]", e);
		}
		log.warn("Medicosearch file [" + filePath + "] not found");
		return null;
	}
	
	private void showWarningMessage(){
		StringBuilder sb = new StringBuilder();
		if (cfgFile == null) {
			sb.append(MessageFormat.format(Messages.ConfigFile, CONFIG_FILE));
		}
		if (jarFile == null) {
			sb.append(MessageFormat.format(Messages.MedicosearchJar, JAR_FILE));
		}
		MessageDialog.openWarning(UiDesk.getTopShell(), Messages.Warn_FilesMissing,
			MessageFormat.format(Messages.Warn_FilesMissingMsg, sb.toString()));
	}
	
	public String getConfigurationFilePath(){
		if (cfgFile == null) {
			return "";
		}
		return cfgFile.getAbsolutePath();
	}
	
	public String getMedicosearchJarPath(){
		if (jarFile == null) {
			return "";
		}
		return jarFile.getAbsolutePath();
	}
	
	public File getBundleDirectory(){
		return bundleDir;
	}
}
