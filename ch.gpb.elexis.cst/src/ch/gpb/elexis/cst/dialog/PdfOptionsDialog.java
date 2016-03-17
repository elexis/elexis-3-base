/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
package ch.gpb.elexis.cst.dialog;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author daniel ludin ludin@swissonline.ch
 * 27.06.2015
 * 
 */

public class PdfOptionsDialog extends TitleAreaDialog {
    private Combo combo;
    private String docWidth = "595";
    private String docHeigth = "842";
    public static int OPTION_ONE_PAGE = 1;
    public static int OPTION_SPLIT_PAGE = 2;
    private static String[] options = new String[] { "Ausgabe auf A4 Seiten aufgeteilt", "Ausgabe auf einer PDF-Seite" };
    int selectedIndex = 0;
    private Composite container_1;

    public PdfOptionsDialog(Shell parentShell) {
	super(parentShell);
    }

    @Override
    public void create() {
	super.create();
	setTitle("PDF Output Options");
	setMessage("Bitte wählen Sie die Optionen für den PDF Output", IMessageProvider.INFORMATION);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
	Composite area = (Composite) super.createDialogArea(parent);
	container_1 = new Composite(area, SWT.NONE);
	container_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	GridLayout gl_container_1 = new GridLayout(2, false);
	container_1.setLayout(gl_container_1);

	createFirstName(container_1);
	createLastName(container_1);

	Label lblSeitenlayout = new Label(container_1, SWT.NONE);
	lblSeitenlayout.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	lblSeitenlayout.setText("Seiten-Layout:");

	combo = new Combo(container_1, SWT.NONE);
	combo.setItems(options);
	combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	combo.select(0);

	return area;
    }

    private void createFirstName(Composite container) {
    }

    private void createLastName(Composite container) {
    }

    @Override
    protected boolean isResizable() {
	return true;
    }

    // save content of the Text fields because they get disposed
    // as soon as the Dialog closes
    private void saveInput() {
	selectedIndex = combo.getSelectionIndex();
    }

    @Override
    protected void okPressed() {
	saveInput();
	super.okPressed();
    }

    public String getWidth() {
	return docWidth;
    }

    public String getHeigth() {
	return docHeigth;
    }

    public int getPdfOutputOption() {

	if (selectedIndex == 0) {
	    return OPTION_SPLIT_PAGE;
	} else if (selectedIndex == 1) {
	    return OPTION_ONE_PAGE;
	} else {
	    return 0;
	}

    }

    public void setCombo(Combo combo) {
	this.combo = combo;
    }
}
