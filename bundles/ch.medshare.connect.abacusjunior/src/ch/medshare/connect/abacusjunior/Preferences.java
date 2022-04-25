package ch.medshare.connect.abacusjunior;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.importer.div.rs232.Connection;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.util.SWTHelper;

public class Preferences extends PreferencePage implements IWorkbenchPreferencePage {
	private static Logger logger = LoggerFactory.getLogger(Preferences.class);

	public static final String ABACUSJUNIOR_BASE = "connectors/abacusjunior/";
	public static final String PORT = ABACUSJUNIOR_BASE + "port";
	public static final String PARAMS = ABACUSJUNIOR_BASE + "params";
	public static final String LOG = ABACUSJUNIOR_BASE + "log";

	Combo ports;
	Text speed, data, stop;
	Button parity, log;

	public Preferences() {
		super(Messages.AbacusJuniorAction_ButtonName);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}

	@Override
	protected Control createContents(final Composite parent) {
		logger.debug("Start von createContents");
		String[] param = CoreHub.localCfg.get(PARAMS, "9600,8,n,1").split(",");

		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout(2, false));
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		new Label(ret, SWT.NONE).setText(Messages.Preferences_Port);
		ports = new Combo(ret, SWT.SINGLE);
		ports.setItems(Connection.getComPorts());
		ports.setText(CoreHub.localCfg.get(PORT, Messages.AbacusJuniorAction_DefaultPort));
		new Label(ret, SWT.NONE).setText(Messages.Preferences_Baud);
		speed = new Text(ret, SWT.BORDER);
		speed.setText(param[0]);
		new Label(ret, SWT.NONE).setText(Messages.Preferences_Databits);
		data = new Text(ret, SWT.BORDER);
		data.setText(param[1]);
		new Label(ret, SWT.NONE).setText(Messages.Preferences_Parity);
		parity = new Button(ret, SWT.CHECK);
		parity.setSelection(!param[2].equalsIgnoreCase("n"));
		new Label(ret, SWT.NONE).setText(Messages.Preferences_Stopbits);
		stop = new Text(ret, SWT.BORDER);
		stop.setText(param[3]);
		new Label(ret, SWT.NONE).setText(Messages.Preferences_Log);
		log = new Button(ret, SWT.CHECK);
		log.setSelection(CoreHub.localCfg.get(LOG, "n").equalsIgnoreCase("y"));
		return ret;
	}

	public void init(final IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean performOk() {
		StringBuilder sb = new StringBuilder();
		sb.append(speed.getText()).append(",").append(data.getText()).append(",")
				.append(parity.getSelection() ? "y" : "n").append(",").append(stop.getText());
		CoreHub.localCfg.set(PARAMS, sb.toString());
		CoreHub.localCfg.set(PORT, ports.getText());
		CoreHub.localCfg.set(LOG, log.getSelection() ? "y" : "n");
		CoreHub.localCfg.flush();
		return super.performOk();
	}
}