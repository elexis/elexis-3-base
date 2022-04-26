/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SimpleTextViewer {
	Shell shell;
	Text text;

	public SimpleTextViewer(String title, String message) {
		// create the shell
		shell = new Shell(Display.getDefault(), SWT.TOOL | SWT.TITLE | SWT.CLOSE | SWT.RESIZE);
		shell.setText(title);
		shell.setLayout(new FillLayout());
		shell.setSize(480, 480);
		// create the text for the content
		text = new Text(shell, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
		text.setText(message); // $NON-NLS-1$
		shell.open();
	}
}
