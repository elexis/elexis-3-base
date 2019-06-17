package ch.elexis.connect.sysmex.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.connect.sysmex.Messages;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.importer.div.rs232.Connection;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.util.SWTHelper;

public class Preferences extends PreferencePage implements IWorkbenchPreferencePage {
	
	public static final String SYSMEX_BASE = "connectors/sysmex/"; //$NON-NLS-1$
	public static final String PORT = SYSMEX_BASE + "port"; //$NON-NLS-1$
	public static final String TIMEOUT = SYSMEX_BASE + "timeout"; //$NON-NLS-1$
	public static final String PARAMS = SYSMEX_BASE + "params"; //$NON-NLS-1$
	public static final String LOG = SYSMEX_BASE + "log"; //$NON-NLS-1$
	public static final String BACKGROUND = SYSMEX_BASE + "background"; //$NON-NLS-1$
	
	public static final String MODEL = SYSMEX_BASE + "model"; //$NON-NLS-1$
	public static final String RDW_TYP = SYSMEX_BASE + "rdwTyp"; //$NON-NLS-1$
	
	public static final String MODEL_KX21 = "KX-21"; //$NON-NLS-1$
	public static final String MODEL_KX21N = "KX-21N"; //$NON-NLS-1$
	public static final String MODEL_POCH = "pocH-100i"; //$NON-NLS-1$
	public static final String MODEL_UC1000 = "UC-1000"; //$NON-NLS-1$
	
	public static final String RDW_SD = "SD"; //$NON-NLS-1$
	public static final String RDW_CV = "CV"; //$NON-NLS-1$
	
	Label lblRdw;
	Combo ports;
	Text speed, data, stop, timeout, logFile;
	Button parity, log, background;
	Combo models, rdw_types;
	
	public Preferences(){
		super(Messages.SysmexAction_ButtonName);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}
	
	@Override
	protected Control createContents(final Composite parent){
		Composite retComp = new Composite(parent, SWT.NONE);
		retComp.setLayout(new GridLayout(1, false));
		retComp.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		Composite mainComp = new Composite(retComp, SWT.NONE);
		mainComp.setLayout(new GridLayout(4, false));
		mainComp.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		
		new Label(mainComp, SWT.NONE).setText(Messages.Preferences_Modell);
		models = new Combo(mainComp, SWT.SINGLE);
		models.setItems(new String[] {
			MODEL_KX21, MODEL_KX21N, MODEL_POCH, MODEL_UC1000
		});
		models.setText(CoreHub.localCfg.get(MODEL, MODEL_KX21));
		
		lblRdw = new Label(mainComp, SWT.NONE);
		lblRdw.setText(Messages.Preferences_RDW);
		rdw_types = new Combo(mainComp, SWT.SINGLE);
		rdw_types.setItems(new String[] {
			RDW_SD, RDW_CV
		});
		rdw_types.setText(CoreHub.localCfg.get(RDW_TYP, RDW_SD));
		
		new Label(mainComp, SWT.NONE).setText(Messages.Preferences_Backgroundprocess);
		background = new Button(mainComp, SWT.CHECK);
		background.setSelection(CoreHub.localCfg.get(BACKGROUND, "n").equalsIgnoreCase("y")); //$NON-NLS-1$ //$NON-NLS-2$
		GridDataFactory.swtDefaults().span(3, 1).applyTo(background);
		
		new Label(mainComp, SWT.NONE).setText(Messages.Preferences_Log);
		log = new Button(mainComp, SWT.CHECK);
		log.setSelection(CoreHub.localCfg.get(LOG, "n").equalsIgnoreCase("y")); //$NON-NLS-1$ //$NON-NLS-2$
		GridDataFactory.swtDefaults().span(3, 1).applyTo(log);
		
		Group connectGroup = new Group(retComp, SWT.NONE);
		connectGroup.setText(Messages.Preferences_Verbindung);
		connectGroup.setLayout(new GridLayout(2, false));
		connectGroup.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		Label lblPorts = new Label(connectGroup, SWT.NONE);
		lblPorts.setText(Messages.Preferences_Port);
		lblPorts.setLayoutData(new GridData(SWT.NONE));
		ports = new Combo(connectGroup, SWT.SINGLE);
		ports.setItems(Connection.getComPorts());
		ports.setText(CoreHub.localCfg.get(PORT, Messages.SysmexAction_DefaultPort));
		
		String[] param = CoreHub.localCfg.get(PARAMS, "9600,8,n,1").split(","); //$NON-NLS-1$ //$NON-NLS-2$
		
		Label lblSpeed = new Label(connectGroup, SWT.NONE);
		lblSpeed.setText(Messages.Preferences_Baud);
		lblSpeed.setLayoutData(new GridData(SWT.NONE));
		speed = new Text(connectGroup, SWT.BORDER);
		speed.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		speed.setText(param[0]);
		
		Label lblData = new Label(connectGroup, SWT.NONE);
		lblData.setText(Messages.Preferences_Databits);
		lblData.setLayoutData(new GridData(SWT.NONE));
		data = new Text(connectGroup, SWT.BORDER);
		data.setText(param[1]);
		
		Label lblParity = new Label(connectGroup, SWT.NONE);
		lblParity.setText(Messages.Preferences_Parity);
		lblParity.setLayoutData(new GridData(SWT.NONE));
		parity = new Button(connectGroup, SWT.CHECK);
		parity.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		parity.setSelection(!param[2].equalsIgnoreCase("n"));
		
		Label lblStop = new Label(connectGroup, SWT.NONE);
		lblStop.setText(Messages.Preferences_Stopbits);
		lblStop.setLayoutData(new GridData(SWT.NONE));
		stop = new Text(connectGroup, SWT.BORDER);
		stop.setText(param[3]);
		
		Label lblTimeout = new Label(connectGroup, SWT.NONE);
		lblTimeout.setText(Messages.Preferences_Timeout);
		lblTimeout.setLayoutData(new GridData(SWT.NONE));
		String timeoutStr =
			CoreHub.localCfg.get(TIMEOUT, Messages.SysmexAction_DefaultTimeout);
		timeout = new Text(connectGroup, SWT.BORDER);
		timeout.setText(timeoutStr);
		
		// Events
		models.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				modelChanged();
			}
		});
		
		modelChanged();
		return retComp;
	}
	
	private void modelChanged(){
		boolean visible = models.getText().equals(MODEL_KX21);
		lblRdw.setVisible(visible);
		rdw_types.setVisible(visible);
	}
	
	public void init(final IWorkbench workbench){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean performOk(){
		StringBuilder sb = new StringBuilder();
		sb.append(speed.getText()).append(",").append(data.getText()).append( //$NON-NLS-1$
			",").append(parity.getSelection() ? "y" : "n").append(",") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			.append(stop.getText());
		CoreHub.localCfg.set(PARAMS, sb.toString());
		CoreHub.localCfg.set(PORT, ports.getText());
		CoreHub.localCfg.set(TIMEOUT, timeout.getText());
		CoreHub.localCfg.set(LOG, log.getSelection() ? "y" : "n"); //$NON-NLS-1$ //$NON-NLS-2$
		CoreHub.localCfg.set(BACKGROUND, background.getSelection() ? "y" : "n"); //$NON-NLS-1$ //$NON-NLS-2$
		CoreHub.localCfg.set(MODEL, models.getText());
		CoreHub.localCfg.set(RDW_TYP, rdw_types.getText());
		CoreHub.localCfg.flush();
		return super.performOk();
	}
}