package ch.elexis.connect.afinion;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.importer.div.rs232.Connection;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.util.SWTHelper;

public class Preferences extends PreferencePage implements IWorkbenchPreferencePage {
	
	public static final String AFINION_BASE = "connectors/afinion/"; //$NON-NLS-1$
	public static final String PORT = AFINION_BASE + "port"; //$NON-NLS-1$
	public static final String TIMEOUT = AFINION_BASE + "timeout"; //$NON-NLS-1$
	public static final String PARAMS = AFINION_BASE + "params"; //$NON-NLS-1$
	public static final String LOG = AFINION_BASE + "log"; //$NON-NLS-1$
	public static final String BACKGROUND = AFINION_BASE + "background"; //$NON-NLS-1$
	public static final String APPLY_SENT_UNITS = AFINION_BASE + "applysentunits";
	
	Combo ports;
	Text speed, data, stop, timeout, logFile;
	Button parity, log, background, btnApplySentUnits;
	
	public Preferences(){
		super(Messages.AfinionAS100Action_ButtonName); //$NON-NLS-1$
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}
	
	@Override
	protected Control createContents(final Composite parent){
		String[] param = CoreHub.localCfg.get(PARAMS, "9600,8,n,1").split(","); //$NON-NLS-1$ //$NON-NLS-2$
		
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout(2, false));
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		Label lblPorts = new Label(ret, SWT.NONE);
		lblPorts.setText(Messages.Preferences_Port); //$NON-NLS-1$
		lblPorts.setLayoutData(new GridData(SWT.NONE));
		ports = new Combo(ret, SWT.SINGLE);
		ports.setItems(Connection.getComPorts());
		ports.setText(CoreHub.localCfg.get(PORT,
			Messages.AfinionAS100Action_DefaultPort)); //$NON-NLS-1$
		
		Label lblSpeed = new Label(ret, SWT.NONE);
		lblSpeed.setText(Messages.Preferences_Baud); //$NON-NLS-1$
		lblSpeed.setLayoutData(new GridData(SWT.NONE));
		speed = new Text(ret, SWT.BORDER);
		speed.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		speed.setText(param[0]);
		
		Label lblData = new Label(ret, SWT.NONE);
		lblData.setText(Messages.Preferences_Databits); //$NON-NLS-1$
		lblData.setLayoutData(new GridData(SWT.NONE));
		data = new Text(ret, SWT.BORDER);
		data.setText(param[1]);
		
		Label lblParity = new Label(ret, SWT.NONE);
		lblParity.setText(Messages.Preferences_Parity); //$NON-NLS-1$
		lblParity.setLayoutData(new GridData(SWT.NONE));
		parity = new Button(ret, SWT.CHECK);
		parity.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		parity.setSelection(!param[2].equalsIgnoreCase("n")); //$NON-NLS-1$
		
		Label lblStop = new Label(ret, SWT.NONE);
		lblStop.setText(Messages.Preferences_Stopbits); //$NON-NLS-1$
		lblStop.setLayoutData(new GridData(SWT.NONE));
		stop = new Text(ret, SWT.BORDER);
		stop.setText(param[3]);
		
		Label lblTimeout = new Label(ret, SWT.NONE);
		lblTimeout.setText(Messages.Preferences_Timeout); //$NON-NLS-1$
		lblTimeout.setLayoutData(new GridData(SWT.NONE));
		String timeoutStr =
			CoreHub.localCfg.get(TIMEOUT, Messages.AfinionAS100Action_DefaultTimeout); //$NON-NLS-1$
		timeout = new Text(ret, SWT.BORDER);
		timeout.setText(timeoutStr);
		
		new Label(ret, SWT.NONE).setText(Messages.Preferences_Backgroundprocess); //$NON-NLS-1$
		background = new Button(ret, SWT.CHECK);
		background.setSelection(CoreHub.localCfg.get(BACKGROUND, "n").equalsIgnoreCase("y")); //$NON-NLS-1$ //$NON-NLS-2$
		
		new Label(ret, SWT.NONE).setText(Messages.Preferences_Log); //$NON-NLS-1$
		log = new Button(ret, SWT.CHECK);
		log.setSelection(CoreHub.localCfg.get(LOG, "n").equalsIgnoreCase("y")); //$NON-NLS-1$ //$NON-NLS-2$
		
		new Label(ret, SWT.NONE);
		new Label(ret, SWT.NONE);
		
		Label lblAdditionalSettings = new Label(ret, SWT.SEPARATOR | SWT.HORIZONTAL);
		lblAdditionalSettings.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		lblAdditionalSettings.setText(Messages.Preferences_lblAdditionalSettings_text);
		
		btnApplySentUnits = new Button(ret, SWT.CHECK);
		btnApplySentUnits.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		btnApplySentUnits.setText(Messages.Preferences_btnCheckButton_text);
		btnApplySentUnits.setSelection(CoreHub.localCfg.get(APPLY_SENT_UNITS, "n")
			.equalsIgnoreCase("y"));
		
		return ret;
	}
	
	public void init(final IWorkbench workbench){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean performOk(){
		StringBuilder sb = new StringBuilder();
		sb.append(speed.getText()).append(",") //$NON-NLS-1$
			.append(data.getText()).append(",") //$NON-NLS-1$
			.append(parity.getSelection() ? "y" : "n").append(",") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			.append(stop.getText());
		CoreHub.localCfg.set(PARAMS, sb.toString());
		CoreHub.localCfg.set(PORT, ports.getText());
		CoreHub.localCfg.set(TIMEOUT, timeout.getText());
		CoreHub.localCfg.set(LOG, log.getSelection() ? "y" : "n"); //$NON-NLS-1$ //$NON-NLS-2$
		CoreHub.localCfg.set(BACKGROUND, background.getSelection() ? "y" : "n"); //$NON-NLS-1$ //$NON-NLS-2$
		CoreHub.localCfg.set(APPLY_SENT_UNITS, btnApplySentUnits.getSelection() ? "y" : "n");
		CoreHub.localCfg.flush();
		return super.performOk();
	}
}