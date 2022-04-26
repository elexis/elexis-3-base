package ch.medshare.mediport;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.medshare.mediport.config.MPCProperties;

public abstract class MediPortAbstractPrefPage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String MPC_INSTALL_DIR = "mpc/install_dir"; //$NON-NLS-1$
	public static final String MPC_INTERMEDIAER_EAN = "mpc/inter_ean"; // Intermediaer EAN //$NON-NLS-1$
	public static final String MPC_AUSGABE = "mpc/ausgabe"; // MediPort; Tarmed-Drucker //$NON-NLS-1$
	public static final String MPC_SERVER = "mpc/server"; // production/test //$NON-NLS-1$

	public static final String MEDIDATA_EAN = "7601001304307"; // EAN der MediData

	public static final String LBL_NEW_KEY = Messages.MediPortAbstractPrefPage_lbl_Neu; // production/test

	public static final String LBL_DOC_NO_PRINT = Messages.MediPortAbstractPrefPage_lbl_Nein;
	public static final String LBL_DOC_PRINT_COPY = Messages.MediPortAbstractPrefPage_lbl_Ja;

	public static final String TIER_PAYANT = "Tiers_Payant"; //$NON-NLS-1$
	public static final String TIER_GARANT_MANUELL = "Tiers_Garant_Manuell"; //$NON-NLS-1$
	public static final String TIER_GARANT_DIRECT = "Tiers_Garant_Direct"; //$NON-NLS-1$

	public static final String LBL_DIST_TYPE_B = Messages.MediPortAbstractPrefPage_lbl_BPost;
	public static final String LBL_DIST_TYPE_A = Messages.MediPortAbstractPrefPage_lbl_APost;

	public static final String LBL_LANGUAGE_D = Messages.MediPortAbstractPrefPage_lbl_Deutsch;
	public static final String LBL_LANGUAGE_F = Messages.MediPortAbstractPrefPage_lbl_Franzoesisch;
	public static final String LBL_LANGUAGE_I = Messages.MediPortAbstractPrefPage_lbl_Italienisch;

	public static final String LBL_SERVER_PRODUCTION = "production"; //$NON-NLS-1$
	public static final String LBL_SERVER_TEST = "test"; //$NON-NLS-1$

	public static final String VALUE_SERVER_URL_PRODUKTIV = "212.243.92.201"; //$NON-NLS-1$
	public static final String VALUE_SERVER_URL_TEST = "212.243.92.199"; //$NON-NLS-1$

	public static final String LBL_SERVER_URL_PRODUKTIV = VALUE_SERVER_URL_PRODUKTIV + ":" //$NON-NLS-1$
			+ Messages.MediPortAbstractPrefPage_lbl_ProduktivServer;
	public static final String LBL_SERVER_URL_TEST = VALUE_SERVER_URL_TEST + ":" //$NON-NLS-1$
			+ Messages.MediPortAbstractPrefPage_lbl_TestServer;

	protected final ConfigServicePreferenceStore prefs = new ConfigServicePreferenceStore(Scope.GLOBAL);

	MPCProperties props;

	public MediPortAbstractPrefPage() {
		super();
		try {
			props = MPCProperties.reload();
		} catch (IOException e) {
			MessageDialog.openError(new Shell(), Messages.MediPortAbstractPrefPage_error_title_LoadConfiguration,
					e.getMessage());
		}
	}

	protected abstract boolean storeAll();

	protected void showReloadInfo() {
		// Show nothing
	}

	protected void refresh() {
		// Show nothing
	}

	protected String getPrefString(String name) {
		return prefs.getString(name);
	}

	protected void putPrefString(String name, String value) {
		prefs.putValue(name, value);
		prefs.setDefault(name, value);
	}

	protected void storePrefs() {
	}

	@Override
	public boolean performOk() {
		if (storeAll()) {
			showReloadInfo();
			refresh();
			return true;
		}
		return true;
	}

}
