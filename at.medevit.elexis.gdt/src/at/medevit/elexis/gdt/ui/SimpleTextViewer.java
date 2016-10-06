/*******************************************************************************
 * Copyright (c) 2011, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
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
		text.setText(message); //$NON-NLS-1$
		shell.open();
	}
}
