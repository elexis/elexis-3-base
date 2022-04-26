package at.medevit.elexis.cobasmira.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.cobasmira.Messages;
import at.medevit.elexis.cobasmira.connection.CobasMiraConnection;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class Preferences extends PreferencePage implements IWorkbenchPreferencePage {
	private static final Logger logger = LoggerFactory.getLogger(Preferences.class);

	public static final String COBASMIRA_BASE = "connectors/cobasmira/"; //$NON-NLS-1$
	public static final String PORT = COBASMIRA_BASE + "port"; //$NON-NLS-1$
	public static final String TIMEOUT = COBASMIRA_BASE + "timeout"; //$NON-NLS-1$
	public static final String PARAMS = COBASMIRA_BASE + "params"; //$NON-NLS-1$
	public static final String LOG = COBASMIRA_BASE + "log"; //$NON-NLS-1$
	public static final String BACKGROUND = COBASMIRA_BASE + "background"; //$NON-NLS-1$
	public static final String CONTROLUSER = COBASMIRA_BASE + "ignoreuser"; //$NON-NLS-1$ //IDENTITY to be ignored (for
																			// CobasMira Control Purposes)
	public static final String LABIDENTIFICATION = COBASMIRA_BASE + "labidentification"; //$NON-NLS-1$ //XID of own Lab
	public static final String MAPPINGSCSVFILE = COBASMIRA_BASE + "csvmappingsfile"; //$NON-NLS-1$
	public static final String ERRORMSGRECEIVER = COBASMIRA_BASE + "errMsgReceiver"; //$NON-NLS-1$
	public static final String CONTROLLOGFILE = COBASMIRA_BASE + "controlLogFile";

	Combo ports, databits, stopbits, parity;
	Combo flowctrlIn, flowctrlOut;
	int selected;
	private Text labIdentification;
	private Text controlUser;
	private Text speed;
	private Button automaticStart;
	private Text mappingLoc;
	private Text controlLogLoc;

	public Preferences() {
		super(Messages.CobasMiraAction_ButtonName);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));

		initMappingFileLocation();
	}

	@Override
	protected Control createContents(final Composite parent) {
		String[] param = CoreHub.localCfg.get(PARAMS, "1200,7,None,2,1,2").split(","); //$NON-NLS-1$ //$NON-NLS-2$

		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout(4, false));

		Label lblLabIdentification = new Label(ret, SWT.NONE);
		lblLabIdentification.setText(Messages.Preferences_LabIdentification);
		lblLabIdentification.setLayoutData(new GridData(SWT.NONE));
		String labIdentificationStr = CoreHub.localCfg.get(LABIDENTIFICATION,
				Messages.CobasMiraAction_OwnLabIdentification);
		labIdentification = new Text(ret, SWT.BORDER);
		labIdentification.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		labIdentification.setText(labIdentificationStr);

		Label lblIgnoreUser = new Label(ret, SWT.NONE);
		lblIgnoreUser.setText(Messages.Preferences_IgnoreUserOnInput);
		lblIgnoreUser.setLayoutData(new GridData(SWT.NONE));
		String controlUserStr = CoreHub.localCfg.get(CONTROLUSER, Messages.CobasMiraAction_DefaultIgnoreUser);
		controlUser = new Text(ret, SWT.BORDER);
		controlUser.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		controlUser.setText(controlUserStr);

		Label lblControlLogLoc = new Label(ret, SWT.NONE);
		lblControlLogLoc.setText(Messages.Preferences_lblcontrolLogLoc);
		lblControlLogLoc.setLayoutData(new GridData(SWT.None));
		String controlLogLocStr = CoreHub.localCfg.get(CONTROLLOGFILE, Messages.Message_notset);
		controlLogLoc = new Text(ret, SWT.BORDER);
		controlLogLoc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		controlLogLoc.setText(controlLogLocStr);

		Label lblMappingLoc = new Label(ret, SWT.NONE);
		lblMappingLoc.setText(Messages.Preferences_lblMappingDatei);
		lblMappingLoc.setLayoutData(new GridData(SWT.NONE));
		String mappingLocStr = CoreHub.localCfg.get(MAPPINGSCSVFILE, Messages.Message_notset);
		mappingLoc = new Text(ret, SWT.BORDER);
		mappingLoc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		mappingLoc.setText(mappingLocStr);

		Button btnBrowse = new Button(ret, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
				String selected = fd.open();
				mappingLoc.setText(selected);
			}
		});
		btnBrowse.setText(Messages.Preferences_btnBrowse_text);

		Label lblPorts = new Label(ret, SWT.NONE);
		lblPorts.setText(Messages.Preferences_Port);
		lblPorts.setLayoutData(new GridData(SWT.NONE));
		ports = new Combo(ret, SWT.SINGLE);
		ports.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		ports.setItems(CobasMiraConnection.getComPorts());
		ports.setText(CoreHub.localCfg.get(PORT, Messages.CobasMiraAction_DefaultPort));

		Label lblSpeed = new Label(ret, SWT.NONE);
		lblSpeed.setText(Messages.Preferences_Baud);
		lblSpeed.setLayoutData(new GridData(SWT.NONE));
		speed = new Text(ret, SWT.BORDER);
		GridData gd_speed = new GridData(GridData.FILL_HORIZONTAL);
		gd_speed.horizontalSpan = 3;
		speed.setLayoutData(gd_speed);
		speed.setText(param[0]);

		Label lblDatabits = new Label(ret, SWT.NONE);
		lblDatabits.setText(Messages.Preferences_Databits);
		lblDatabits.setLayoutData(new GridData(SWT.NONE));
		databits = new Combo(ret, SWT.SINGLE);
		databits.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		databits.add("5");
		databits.add("6");
		databits.add("7");
		databits.add("8");
		databits.setText(param[1]);

		Label lblParity = new Label(ret, SWT.NONE);
		lblParity.setText(Messages.Preferences_Parity);
		lblParity.setLayoutData(new GridData(SWT.NONE));
		parity = new Combo(ret, SWT.SINGLE);
		parity.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		parity.add("None");
		parity.add("Even");
		parity.add("Odd");
		parity.setText(param[2]);

		Label lblStopbits = new Label(ret, SWT.NONE);
		lblStopbits.setText(Messages.Preferences_Stopbits);
		lblStopbits.setLayoutData(new GridData(SWT.NONE));
		stopbits = new Combo(ret, SWT.SINGLE);
		stopbits.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		stopbits.add("1");
		stopbits.add("2");
		stopbits.setText(param[3]);

		Label lblflowctrlIn = new Label(ret, SWT.NONE);
		lblflowctrlIn.setText(Messages.Preferences_FlowCtrlIn);
		lblflowctrlIn.setLayoutData(new GridData(SWT.NONE));
		flowctrlIn = new Combo(ret, SWT.SINGLE);
		flowctrlIn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		flowctrlIn.add("NONE");
		flowctrlIn.add("RTSCTS");
		flowctrlIn.add("XONXOFF");
		if (param.length > 4) {
			selected = Integer.parseInt(param[4]);
			if (selected == 0)
				flowctrlIn.setText("NONE");
			if (selected == 1)
				flowctrlIn.setText("RTSCTS");
			if (selected == 4)
				flowctrlIn.setText("XONXOFF");
		}

		Label lblflowctrlOut = new Label(ret, SWT.NONE);
		lblflowctrlOut.setText(Messages.Preferences_FlowCtrlOut);
		lblflowctrlOut.setLayoutData(new GridData(SWT.NONE));
		flowctrlOut = new Combo(ret, SWT.SINGLE);
		flowctrlOut.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		flowctrlOut.add("NONE");
		flowctrlOut.add("RTSCTS");
		flowctrlOut.add("XONXOFF");
		if (param.length > 5) {
			selected = Integer.parseInt(param[5]);
			if (selected == 0)
				flowctrlOut.setText("NONE");
			if (selected == 2)
				flowctrlOut.setText("RTSCTS");
			if (selected == 8)
				flowctrlOut.setText("XONXOFF");
		}

		new Label(ret, SWT.NONE).setText(Messages.Preferences_Backgroundprocess);
		automaticStart = new Button(ret, SWT.CHECK);
		automaticStart.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		automaticStart.setSelection(CoreHub.localCfg.get(BACKGROUND, "n").equalsIgnoreCase("y"));

		return ret;
	}

	@Override
	public void init(IWorkbench arg0) {
	}

	private void initMappingFileLocation() {
		String cobasMappingCSV = CoreHub.localCfg.get(MAPPINGSCSVFILE, null);
		if (cobasMappingCSV == null) {
			cobasMappingCSV = getDefaultMappingCSVLocation();
		}
		setOrCreateMappingCSV(cobasMappingCSV);
	}

	private static void setOrCreateMappingCSV(String path) {
		try {
			File csv = new File(path);
			if (!csv.exists()) {
				csv.getParentFile().mkdirs();
				csv.createNewFile();

				// copy csv to destination file
				InputStream sourceStream = Preferences.class.getResourceAsStream("/rsc/cmmli.csv");
				FileOutputStream destStream = new FileOutputStream(csv);

				byte[] buffer = new byte[1024];
				int length;
				while ((length = sourceStream.read(buffer)) > 0) {
					destStream.write(buffer, 0, length);
				}
				sourceStream.close();
				destStream.close();
			}
			CoreHub.localCfg.set(MAPPINGSCSVFILE, csv.getAbsolutePath());
		} catch (IOException ioe) {
			logger.error("Unable to initialize CobasMira base mapping csv", ioe);
		}
	}

	public static String getDefaultMappingCSVLocation() {
		String csv_file = CoreHub.getWritableUserDir() + File.separator + "cobasMira" + File.separator + "cmmli.csv";
		setOrCreateMappingCSV(csv_file);
		return csv_file;
	}

	@Override
	public boolean performOk() {
		StringBuilder sb = new StringBuilder();
		sb.append(speed.getText()).append(",").append(databits.getText()).append( //$NON-NLS-1$
				",").append(parity.getText()).append(",") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				.append(stopbits.getText());
		if (flowctrlIn.getText().equals("NONE")) {
			sb.append(",0");
		} else if (flowctrlIn.getText().equals("RTSCTS")) {
			sb.append(",1");
		} else if (flowctrlIn.getText().equals("XONXOFF")) {
			sb.append(",4");
		}
		if (flowctrlOut.getText().equals("NONE")) {
			sb.append(",0");
		} else if (flowctrlOut.getText().equals("RTSCTS")) {
			sb.append(",2");
		} else if (flowctrlOut.getText().equals("XONXOFF")) {
			sb.append(",8");
		}
		CoreHub.localCfg.set(CONTROLUSER, controlUser.getText().trim());
		CoreHub.localCfg.set(PARAMS, sb.toString());
		CoreHub.localCfg.set(PORT, ports.getText());
		// Hub.localCfg.set(LOG, log.getSelection() ? "y" : "n"); //$NON-NLS-1$
		// //$NON-NLS-2$
		CoreHub.localCfg.set(BACKGROUND, automaticStart.getSelection() ? "y" : "n"); //$NON-NLS-1$ //$NON-NLS-2$
		CoreHub.localCfg.set(LABIDENTIFICATION, labIdentification.getText());
		CoreHub.localCfg.set(MAPPINGSCSVFILE, mappingLoc.getText());
		CoreHub.localCfg.set(CONTROLLOGFILE, controlLogLoc.getText());
		CoreHub.localCfg.flush();
		return super.performOk();
	}

}
