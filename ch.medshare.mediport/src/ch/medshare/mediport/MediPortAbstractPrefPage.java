package ch.medshare.mediport;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.medshare.mediport.config.MPCProperties;

public abstract class MediPortAbstractPrefPage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	public static final String MPC_INSTALL_DIR = "mpc/install_dir"; //$NON-NLS-1$
	public static final String MPC_INTERMEDIAER_EAN = "mpc/inter_ean"; // Intermediaer EAN //$NON-NLS-1$
	public static final String MPC_AUSGABE = "mpc/ausgabe"; // MediPort; Tarmed-Drucker //$NON-NLS-1$
	public static final String MPC_SERVER = "mpc/server"; // production/test //$NON-NLS-1$
	
	public static final String MEDIDATA_EAN = "7601001304307"; // EAN der MediData
	
	public static final String LBL_NEW_KEY = Messages.getString("MediPortAbstractPrefPage.lbl.Neu"); // production/test //$NON-NLS-1$
	
	public static final String LBL_DOC_NO_PRINT =
		Messages.getString("MediPortAbstractPrefPage.lbl.Nein"); //$NON-NLS-1$
	public static final String LBL_DOC_PRINT_COPY =
		Messages.getString("MediPortAbstractPrefPage.lbl.Ja"); //$NON-NLS-1$
	
	public static final String TIER_PAYANT = "Tiers_Payant"; //$NON-NLS-1$
	public static final String TIER_GARANT_MANUELL = "Tiers_Garant_Manuell"; //$NON-NLS-1$
	public static final String TIER_GARANT_DIRECT = "Tiers_Garant_Direct"; //$NON-NLS-1$
	
	public static final String LBL_DIST_TYPE_B =
		Messages.getString("MediPortAbstractPrefPage.lbl.BPost"); //$NON-NLS-1$
	public static final String LBL_DIST_TYPE_A =
		Messages.getString("MediPortAbstractPrefPage.lbl.APost"); //$NON-NLS-1$
	
	public static final String LBL_LANGUAGE_D =
		Messages.getString("MediPortAbstractPrefPage.lbl.Deutsch"); //$NON-NLS-1$
	public static final String LBL_LANGUAGE_F =
		Messages.getString("MediPortAbstractPrefPage.lbl.Franzoesisch"); //$NON-NLS-1$
	public static final String LBL_LANGUAGE_I =
		Messages.getString("MediPortAbstractPrefPage.lbl.Italienisch"); //$NON-NLS-1$
	
	public static final String LBL_SERVER_PRODUCTION = "production"; //$NON-NLS-1$
	public static final String LBL_SERVER_TEST = "test"; //$NON-NLS-1$
	
	public static final String VALUE_SERVER_URL_PRODUKTIV = "212.243.92.201"; //$NON-NLS-1$
	public static final String VALUE_SERVER_URL_TEST = "212.243.92.199"; //$NON-NLS-1$
	
	public static final String LBL_SERVER_URL_PRODUKTIV =
		VALUE_SERVER_URL_PRODUKTIV
			+ ":" + Messages.getString("MediPortAbstractPrefPage.lbl.ProduktivServer"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String LBL_SERVER_URL_TEST =
		VALUE_SERVER_URL_TEST + ":" + Messages.getString("MediPortAbstractPrefPage.lbl.TestServer"); //$NON-NLS-1$ //$NON-NLS-2$
	
	protected final SettingsPreferenceStore prefs = new SettingsPreferenceStore(CoreHub.globalCfg);
	
	MPCProperties props;
	
	public MediPortAbstractPrefPage(){
		super();
		try {
			props = MPCProperties.reload();
		} catch (IOException e) {
			MessageDialog.openError(new Shell(), Messages
				.getString("MediPortAbstractPrefPage.error.title.LoadConfiguration"), //$NON-NLS-1$
				e.getMessage());
		}
	}
	
	protected abstract boolean storeAll();
	
	protected void showReloadInfo(){
	// Show nothing
	}
	
	protected void refresh(){
	// Show nothing
	}
	
	protected String getPrefString(String name){
		return prefs.getString(name);
	}
	
	protected void putPrefString(String name, String value){
		prefs.putValue(name, value);
		prefs.setDefault(name, value);
	}
	
	protected void storePrefs(){
		prefs.flush();
	}
	
	@Override
	public boolean performOk(){
		if (storeAll()) {
			showReloadInfo();
			refresh();
			return true;
		}
		return true;
	}
	
}
