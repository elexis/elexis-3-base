/*******************************************************************************
 * Copyright (c) 2009-2022, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 * Sponsoring: Polymed AG
 *
 *******************************************************************************/

package ch.elexis.connect.fuji.drichem3500;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import gnu.io.SerialPort;

public class Preferences extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String FUJI_BASE = "connectors/fuji-drichem-3500/";
	public static final String PORT = FUJI_BASE + "port";
	public static final String PARAMS = FUJI_BASE + "params";

	public static final String ETHERNET_PORT = FUJI_BASE + "ethernetport";
	public static final String SELECTED_CONNECTION = FUJI_BASE + "connection";

	private Combo comPorts;
	private Text speed;
	private Combo dataBits;
	private Combo parity;
	private Combo stopBits;
	private Combo connectionType;
	private Text ethernetPort;

	private Combo flowControlIn;
	private Combo flowControlOut;

	public Preferences() {
		super("Fuji DriChem");
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}

	@Override
	protected Control createContents(final Composite parent) {
		String[] param = CoreHub.localCfg.get(PARAMS, "9600,8,n,1,0,0").split(",");
		final String[] connection = new String[] { "Ethernet", "Seriell" };

		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout(3, false));
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Label lblConnection = new Label(ret, SWT.NONE);
		lblConnection.setText("Schnittstelle wählen");
		connectionType = new Combo(ret, SWT.SINGLE);
		GridData gd_connectionType = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_connectionType.widthHint = 50;
		connectionType.setLayoutData(gd_connectionType);
		connectionType.add(connection[0]);
		connectionType.add(connection[1]);
		connectionType.setText(param[1]);
		connectionType.setText(CoreHub.localCfg.get(SELECTED_CONNECTION, "Seriell"));
		connectionType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// if ethernet is selected
				if (connectionType.getText().equals(connection[0])) {
					setEthernet(true);
				} else {
					setEthernet(false);
				}
			}
		});
		new Label(ret, SWT.NONE);
		new Label(ret, SWT.NONE);
		new Label(ret, SWT.NONE);
		new Label(ret, SWT.NONE);

		Label lblEthernetPort = new Label(ret, SWT.NONE);
		lblEthernetPort.setText("Ethernet Port");

		ethernetPort = new Text(ret, SWT.BORDER);
		GridData gd_ePort = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_ePort.widthHint = 65;
		ethernetPort.setLayoutData(gd_ePort);
		ethernetPort.setText(CoreHub.localCfg.get(ETHERNET_PORT, "5000"));

		new Label(ret, SWT.NONE);
		new Label(ret, SWT.NONE);
		new Label(ret, SWT.NONE);
		new Label(ret, SWT.NONE);

		Label lblComPort = new Label(ret, SWT.NONE);
		lblComPort.setText("Com-Port");
		comPorts = new Combo(ret, SWT.SINGLE);
		GridData gd_comPorts = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_comPorts.widthHint = 50;
		comPorts.setLayoutData(gd_comPorts);
		comPorts.setItems(Connection.getComPorts());
		comPorts.setText(CoreHub.localCfg.get(PORT, "COM1"));
		new Label(ret, SWT.NONE);

		Label lblSpeed = new Label(ret, SWT.NONE);
		lblSpeed.setText("Geschwindigkeit");
		speed = new Text(ret, SWT.BORDER);
		GridData gd_speed = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_speed.widthHint = 65;
		speed.setLayoutData(gd_speed);
		speed.setText(param[0]);
		new Label(ret, SWT.NONE);

		Label lblDatenBits = new Label(ret, SWT.NONE);
		lblDatenBits.setText("Daten-Bits");
		dataBits = new Combo(ret, SWT.SINGLE);
		GridData gd_dataBits = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_dataBits.widthHint = 50;
		dataBits.setLayoutData(gd_dataBits);
		dataBits.add("5");
		dataBits.add("6");
		dataBits.add("7");
		dataBits.add("8");
		dataBits.setText(param[1]);
		new Label(ret, SWT.NONE);

		Label lblParity = new Label(ret, SWT.NONE);
		lblParity.setText("Parität");
		parity = new Combo(ret, SWT.SINGLE);
		GridData gd_parity = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_parity.widthHint = 50;
		parity.setLayoutData(gd_parity);
		parity.add("None");
		parity.add("Even");
		parity.add("Odd");
		parity.setText(param[2]);
		new Label(ret, SWT.NONE);

		Label lblStopbit = new Label(ret, SWT.NONE);
		lblStopbit.setText("Stop-Bit");
		stopBits = new Combo(ret, SWT.SINGLE);
		GridData gd_stopBits = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_stopBits.widthHint = 50;
		stopBits.setLayoutData(gd_stopBits);
		stopBits.add("1");
		stopBits.add("2");
		stopBits.setText(param[3]);
		new Label(ret, SWT.NONE);

		String[] FC = { "NONE", "RTSCTS", "XONXOFF" };

		Label lblFlowcontrol = new Label(ret, SWT.NONE);
		lblFlowcontrol.setText("Flowcontrol In");
		flowControlIn = new Combo(ret, SWT.SINGLE);
		flowControlIn.setItems(FC);
		if (param.length > 4) {
			int paramFC = Integer.parseInt(param[4]);
			if (paramFC == SerialPort.FLOWCONTROL_NONE)
				flowControlIn.select(0);
			if (paramFC == SerialPort.FLOWCONTROL_RTSCTS_IN)
				flowControlIn.select(1);
			if (paramFC == SerialPort.FLOWCONTROL_XONXOFF_IN)
				flowControlIn.select(2);
		}
		new Label(ret, SWT.NONE);

		Label label = new Label(ret, SWT.NONE);
		label.setText("Flowcontrol Out");
		flowControlOut = new Combo(ret, SWT.SINGLE);
		flowControlOut.setItems(FC);
		if (param.length > 5) {
			int paramFCO = Integer.parseInt(param[5]);
			if (paramFCO == SerialPort.FLOWCONTROL_NONE)
				flowControlOut.select(0);
			if (paramFCO == SerialPort.FLOWCONTROL_RTSCTS_OUT)
				flowControlOut.select(1);
			if (paramFCO == SerialPort.FLOWCONTROL_XONXOFF_OUT)
				flowControlOut.select(2);
		}
		new Label(ret, SWT.NONE);

		setEthernet(connectionType.getText().equals(connection[0]));

		return ret;
	}

	public void init(final IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	/**
	 * enable selected connection settings and disable settings for other connection
	 *
	 * @param b
	 */
	protected void setEthernet(boolean b) {
		if (b) {
			ethernetPort.setEnabled(true);
			comPorts.setEnabled(false);
			speed.setEnabled(false);
			dataBits.setEnabled(false);
			parity.setEnabled(false);
			stopBits.setEnabled(false);
			flowControlIn.setEnabled(false);
			flowControlOut.setEnabled(false);
		} else {
			ethernetPort.setEnabled(false);
			comPorts.setEnabled(true);
			speed.setEnabled(true);
			dataBits.setEnabled(true);
			parity.setEnabled(true);
			stopBits.setEnabled(true);
			flowControlIn.setEnabled(true);
			flowControlOut.setEnabled(true);
		}

	}

	@Override
	public boolean performOk() {
		CoreHub.localCfg.set(ETHERNET_PORT, ethernetPort.getText());
		CoreHub.localCfg.set(SELECTED_CONNECTION, connectionType.getText());

		StringBuilder sb = new StringBuilder();
		sb.append(speed.getText()).append(",");
		sb.append(dataBits.getText()).append(",");
		sb.append(parity.getText()).append(",");
		sb.append(stopBits.getText()).append(",");
		if (flowControlIn.getText().equalsIgnoreCase("NONE"))
			sb.append(SerialPort.FLOWCONTROL_NONE);
		if (flowControlIn.getText().equalsIgnoreCase("RTSCTS"))
			sb.append(SerialPort.FLOWCONTROL_RTSCTS_IN);
		if (flowControlIn.getText().equalsIgnoreCase("XONXOFF"))
			sb.append(SerialPort.FLOWCONTROL_XONXOFF_IN);
		sb.append(",");
		if (flowControlOut.getText().equalsIgnoreCase("NONE"))
			sb.append(SerialPort.FLOWCONTROL_NONE);
		if (flowControlOut.getText().equalsIgnoreCase("RTSCTS"))
			sb.append(SerialPort.FLOWCONTROL_RTSCTS_OUT);
		if (flowControlOut.getText().equalsIgnoreCase("XONXOFF"))
			sb.append(SerialPort.FLOWCONTROL_XONXOFF_OUT);
		System.out.println(sb.toString());
		CoreHub.localCfg.set(PARAMS, sb.toString());
		CoreHub.localCfg.set(PORT, comPorts.getText());
		CoreHub.localCfg.flush();
		return super.performOk();
	}

}
