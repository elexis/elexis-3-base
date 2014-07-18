package ch.medshare.mediport.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.constants.ExtensionPointConstantsData;
import ch.elexis.core.ui.constants.ExtensionPointConstantsUi;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.ui.util.Log;
import ch.medshare.mediport.config.Client;
import ch.medshare.mediport.config.ClientParam;
import ch.medshare.mediport.config.MPCProperties;
import ch.medshare.util.SystemProperties;
import ch.medshare.util.UtilFile;
import ch.rgw.tools.ExHandler;

public class MediPortHelper {
	
	private static final String module = MediPortHelper.class.getName();
	
	private static final String MPC_NUMBER_POSTFIX = "/mpc_number";//$NON-NLS-1$
	
	public final static FilenameFilter XML_FILTER = new FilenameFilter() {
		public boolean accept(File dir, String name){
			return name.toUpperCase().endsWith(".XML"); //$NON-NLS-1$
		}
	};
	
	private static MPCProperties getProperties(){
		MPCProperties props = null;
		try {
			props = MPCProperties.getCurrent();
		} catch (IOException e) {
			Log.get(module).log(e.getMessage(), Log.WARNINGS);
		}
		return props;
	}
	
	public static String getMandantPrefix(String label){
		return label + MPC_NUMBER_POSTFIX;
	}
	
	public static Client getCurrentClient(){
		MPCProperties props = getProperties();
		if (props == null) {
			return null;
		}
		SettingsPreferenceStore prefs = new SettingsPreferenceStore(CoreHub.globalCfg);
		String prefix = getMandantPrefix(CoreHub.actMandant.getLabel());
		String numStr = prefs.getString(prefix);
		if (numStr != null && numStr.length() > 0) {
			return getProperties().getClient(Integer.parseInt(numStr));
		}
		
		return null;
	}
	
	public static ClientParam getCurrentParam(String paramName){
		if (paramName == null || paramName.length() == 0) {
			return null;
		}
		Client client = getCurrentClient();
		if (client == null) {
			return null;
		}
		Integer paramNum = client.getParamKey(paramName);
		return client.getParam(paramNum);
	}
	
	public static List<IRnOutputter> getRnOutputter(){
		List<IRnOutputter> retList = new Vector<IRnOutputter>();
		for (Object outputter : Extensions.getClasses(ExtensionPointConstantsData.RECHNUNGS_MANAGER, "outputter")) { //$NON-NLS-1$ //$NON-NLS-2$
			if (outputter instanceof IRnOutputter) {
				retList.add((IRnOutputter) outputter);
			}
		}
		return retList;
	}
	
	public static int getReturnFiles(){
		Client client = getCurrentClient();
		if (client != null) {
			int errorCount = 0;
			final File errorDir = new File(client.getError_dir());
			if (errorDir.exists()) {
				errorCount = errorDir.list(XML_FILTER).length;
			}
			int receiveCount = 0;
			final File receiveDir = new File(client.getReceive_dir());
			if (receiveDir.exists()) {
				receiveCount = receiveDir.list(XML_FILTER).length;
			}
			return errorCount + receiveCount;
		}
		return 0;
	}
	
	public static String getPluginDirectory(String pluginName){
		String filePath = null;
		Bundle bundle = Platform.getBundle(pluginName); //$NON-NLS-1$
		if (bundle != null) {
			Path path = new Path("/");
			URL url = FileLocator.find(bundle, path, null);
			
			try {
				filePath = FileLocator.toFileURL(url).getPath();
				filePath = filePath.substring(1);
			} catch (IOException e) {
				ExHandler.handle(e);
			}
		}
		if (filePath != null) { //$NON-NLS-1$
			return UtilFile.getCorrectPath(filePath);
		}
		return SystemProperties.USER_DIR + UtilFile.DIRECTORY_SEPARATOR;
	}
}
