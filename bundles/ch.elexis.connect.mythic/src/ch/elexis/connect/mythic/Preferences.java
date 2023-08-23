/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.connect.mythic;

import org.apache.commons.lang3.StringUtils;
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

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.serial.Connection;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.util.SWTHelper;

public class Preferences extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String MYTHIC_BASE = "connectors/mythic/";
	public static final String PORT = MYTHIC_BASE + "port";
	public static final String PARAMS = MYTHIC_BASE + "params";

	Combo ports;
	Text speed, data, stop;
	Button parity;

	public Preferences() {
		super("Mythic");
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}

	@Override
	protected Control createContents(final Composite parent) {
		String[] param = CoreHub.localCfg.get(PARAMS, "9600,8,n,1").split(",");

		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout(2, false));
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		new Label(ret, SWT.NONE).setText("Com-Port");
		ports = new Combo(ret, SWT.SINGLE);
		ports.setItems(Connection.getComPorts());
		ports.setText(CoreHub.localCfg.get(PORT, "COM1"));
		new Label(ret, SWT.NONE).setText("Geschwindigkeit");
		speed = new Text(ret, SWT.BORDER);
		speed.setText(getParamText(param, 0));
		new Label(ret, SWT.NONE).setText("Datenbits");
		data = new Text(ret, SWT.BORDER);
		data.setText(getParamText(param, 1));
		new Label(ret, SWT.NONE).setText("Parity");
		parity = new Button(ret, SWT.CHECK);
		parity.setSelection(!getParamText(param, 2).equalsIgnoreCase("n"));
		new Label(ret, SWT.NONE).setText("Stopbits");
		stop = new Text(ret, SWT.BORDER);
		stop.setText(getParamText(param, 3));
		return ret;
	}

	private String getParamText(String[] param, int idx) {
		if (idx < param.length) {
			return param[idx];
		}
		return StringUtils.EMPTY;
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
		CoreHub.localCfg.flush();
		return super.performOk();
	}
}
