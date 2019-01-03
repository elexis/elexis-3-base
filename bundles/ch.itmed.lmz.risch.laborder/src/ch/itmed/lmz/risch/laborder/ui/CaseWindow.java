package ch.itmed.lmz.risch.laborder.ui;

/*******************************************************************************
 * Copyright (c) 2018 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

import ch.elexis.data.Fall;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

public class CaseWindow {

	protected Shell shell;
	private ArrayList<Fall> caseList;
	private int caseIndex = 0;

	public int open(ArrayList<Fall> items) {
		caseList = items;
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		Point pt = display.getCursorLocation();
		shell.setLocation(pt.x, pt.y);
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return caseIndex;
	}

	private void createContents() {
		shell = new Shell();
		shell.setMinimumSize(new Point(136, 20));
		shell.setSize(450, 210);
		shell.setText("Fall ausählen");
		shell.setLayout(null);

		shell.addListener(SWT.Close, (Listener) -> {
			caseIndex = -1;
		});

		List list = new List(shell, SWT.V_SCROLL);
		list.setBounds(0, 0, 434, 137);
		caseList.forEach(fall -> list.add(fall.getLabel()));

		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setBounds(3, 141, 128, 25);
		btnNewButton.setText("Fall ausählen");
		btnNewButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					caseIndex = list.getSelectionIndex();
					shell.dispose();
					break;
				}
			}
		});
	}
}
