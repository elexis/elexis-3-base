package ch.medshare.mediport.config;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.medshare.mediport.MediPortAbstractPrefPage;
import ch.medshare.util.SystemProperties;
import ch.medshare.util.UtilFile;

public class MPCProperties extends Properties implements ConfigKeys {
	private static final long serialVersionUID = -5479066172426446513L;
	
	private final String LOG_ENTRY_TITLE =
		SystemProperties.LINE_SEPARATOR
			+ "###########################################################" + SystemProperties.LINE_SEPARATOR + //$NON-NLS-1$
			"# PFADKONFIGURATION"
			+ SystemProperties.LINE_SEPARATOR
			+ //$NON-NLS-1$
			"###########################################################"
			+ SystemProperties.LINE_SEPARATOR + SystemProperties.LINE_SEPARATOR
			+ //$NON-NLS-1$
			"### NEUE MULTI CLIENT (SENDER) AB V01.05.00" + SystemProperties.LINE_SEPARATOR
			+ SystemProperties.LINE_SEPARATOR; //$NON-NLS-1$
	
	private final String installDir;
	
	private static MPCProperties props;
	
	private Map<Integer, Client> clientMap = new HashMap<Integer, Client>();
	
	private MPCProperties(String installDir) throws IOException{
		this.installDir = installDir;
		load();
	}
	
	public static MPCProperties reload(String installDir) throws IOException{
		if (installDir != null && installDir.length() > 0) {
			props = new MPCProperties(installDir);
		} else {
			props = null;
		}
		return props;
	}
	
	public static MPCProperties reload() throws IOException{
		SettingsPreferenceStore prefs = new SettingsPreferenceStore(CoreHub.globalCfg);
		String installDir = prefs.getString(MediPortAbstractPrefPage.MPC_INSTALL_DIR);
		return reload(installDir);
	}
	
	public static MPCProperties getCurrent() throws IOException{
		if (props == null) {
			reload();
		}
		return props;
	}
	
	private void addClientProperty(String key, String value){
		String[] parts = key.split("[.]"); //$NON-NLS-1$
		Client client = clientMap.get(Integer.parseInt(parts[1]));
		if (client == null) {
			client = new Client(installDir);
			clientMap.put(Integer.parseInt(parts[1]), client);
		}
		client.add(parts, value);
	}
	
	public String getConfigFilenamePath(){
		return this.installDir + File.separator + "config" //$NON-NLS-1$
			+ File.separator + "mpcommunicator.config"; //$NON-NLS-1$
	}
	
	public void load() throws IOException{
		load(new FileInputStream(getConfigFilenamePath()));
		for (Object keyObj : keySet()) {
			String key = (String) keyObj;
			if (key != null && key.startsWith(CLIENT + ".")) { //$NON-NLS-1$
				addClientProperty(key, getProperty(key));
			}
		}
	}
	
	public void store() throws IOException{
		boolean clientAdded = false;
		boolean skipEmptyLines = false;
		StringBuffer newContent = new StringBuffer();
		DataInputStream in = null;
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(getConfigFilenamePath());
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				if (strLine.trim().startsWith("#") || strLine.indexOf("=") < 0) { //$NON-NLS-1$ //$NON-NLS-2$
					if (!skipEmptyLines || strLine.trim().length() > 0) {
						newContent.append(strLine);
						newContent.append(SystemProperties.LINE_SEPARATOR);
						skipEmptyLines = false;
					}
				} else if (strLine.trim().startsWith(CLIENT)) {
					if (!clientAdded) {
						newContent.append(clientString());
						clientAdded = true;
						skipEmptyLines = true;
					}
				} else {
					String[] parts = strLine.split("[=]"); //$NON-NLS-1$
					String value = getProperty(parts[0].trim());
					if (value == null) {
						newContent.append(strLine);
					} else {
						newContent.append(parts[0] + "=" + value); //$NON-NLS-1$
					}
					newContent.append(SystemProperties.LINE_SEPARATOR);
					skipEmptyLines = false;
				}
			}
			
			if (!clientAdded) {
				newContent.append(LOG_ENTRY_TITLE);
				newContent.append(clientString());
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		
		UtilFile.writeTextFile(getConfigFilenamePath(), newContent.toString());
	}
	
	public Client getClient(Integer num){
		if (num == null) {
			return null;
		}
		return clientMap.get(num);
	}
	
	public int addNewClient(Client client){
		int nextNumber = getNextClientKey();
		clientMap.put(nextNumber, client);
		return nextNumber;
	}
	
	private int getNextClientKey(){
		int maxNumber = 0;
		for (Integer key : clientMap.keySet()) {
			if (key.intValue() > maxNumber) {
				maxNumber = key;
			}
		}
		return maxNumber + 1;
	}
	
	public List<Integer> getClientKeys(){
		List<Integer> keyList = new Vector<Integer>();
		for (Integer key : clientMap.keySet()) {
			keyList.add(key);
		}
		return keyList;
	}
	
	public String clientString(){
		StringBuffer buffer = new StringBuffer();
		
		for (Integer num : clientMap.keySet()) {
			Client client = getClient(num);
			buffer.append(client.toString(num));
			buffer.append(SystemProperties.LINE_SEPARATOR);
		}
		
		return buffer.toString();
	}
}
