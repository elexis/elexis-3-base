/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.base.ch.labortarif_2009.ui;

import java.util.LinkedList;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.base.ch.labortarif.LaborTarifConstants;
import ch.elexis.base.ch.labortarif_2009.Messages;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.preferences.inputs.MultiplikatorEditor;
import ch.elexis.core.ui.util.SWTHelper;

public class Preferences extends PreferencePage implements IWorkbenchPreferencePage {
	private static final String SPECNUM = "specnum"; //$NON-NLS-1$
	public static final String OPTIMIZE = "abrechnung/labor2009/optify"; //$NON-NLS-1$
	public static final String OPTIMIZE_ADDITION_DEADLINE = "abrechnung/labor2009/optify/addition/deadline"; //$NON-NLS-1$

	public static final String OPTIMIZE_ADDITION_INITDEADLINE = "30.06.2013"; //$NON-NLS-1$

	int langdef = 0;
	LinkedList<Button> buttons = new LinkedList<Button>();

	public Preferences() {
		String lang = ConfigServiceHolder.get().getLocal(ch.elexis.core.constants.Preferences.ABL_LANGUAGE, "d") //$NON-NLS-1$
				.toUpperCase();
		if (lang.startsWith("F")) { //$NON-NLS-1$
			langdef = 1;
		} else if (lang.startsWith("I")) { //$NON-NLS-1$
			langdef = 2;
		}

	}

	@Override
	protected Control createContents(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());
		new Label(ret, SWT.NONE).setText(Messages.Preferences_pleaseEnterMultiplier);
		MultiplikatorEditor me = new MultiplikatorEditor(ret, LaborTarifConstants.MULTIPLICATOR_NAME);
		me.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Group groupOptify = new Group(ret, SWT.NONE);
		groupOptify.setText(Messages.Preferences_automaticAdditionsGroup);
		groupOptify.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		groupOptify.setLayout(new GridLayout(2, false));
		final Button bOptify = new Button(groupOptify, SWT.CHECK);
		bOptify.setSelection(CoreHub.localCfg.get(OPTIMIZE, true));
		bOptify.setText(Messages.Preferences_automaticallyCalculatioAdditions);
		bOptify.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CoreHub.localCfg.set(OPTIMIZE, bOptify.getSelection());
			}
		});
		bOptify.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));

		return ret;
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean performOk() {
		LinkedList<String> bb = new LinkedList<String>();
		for (Button b : buttons) {
			if (b.getSelection()) {
				bb.add(((Integer) b.getData(SPECNUM)).toString());
			}
		}
		return super.performOk();
	}
}
