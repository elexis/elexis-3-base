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
package ch.gpb.elexis.cst.view.profileeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.gpb.elexis.cst.preferences.Messages;

public class TemplateComposite extends CstComposite {
    private Button btnProfilIstTemplate;
    private Text text;

    public TemplateComposite(Composite parent) {
	super(parent, SWT.NONE);

	GridLayout gridLayout = new GridLayout(1, false);
	setLayout(gridLayout);

	GridData gdGastro = new GridData();
	gdGastro.heightHint = 800;
	gdGastro.minimumHeight = 800;
	setLayoutData(gdGastro);

	Label lblAuswahlBefundparameter = new Label(this, SWT.NONE);
	lblAuswahlBefundparameter.setText(Messages.TemplateComposite_template_settings);

	createLayout(this);

	btnProfilIstTemplate = new Button(this, SWT.CHECK);
	GridData gd_btnProfilIstTemplate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_btnProfilIstTemplate.horizontalIndent = 20;
	gd_btnProfilIstTemplate.verticalIndent = 20;
	gd_btnProfilIstTemplate.widthHint = 200;
	btnProfilIstTemplate.setLayoutData(gd_btnProfilIstTemplate);
	btnProfilIstTemplate.setText(Messages.TemplateComposite_is_template);

	Label lblHeaderzeileOutput = new Label(this, SWT.NONE);
	GridData gd_lblHeaderzeileOutput = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_lblHeaderzeileOutput.verticalIndent = 20;
	lblHeaderzeileOutput.setLayoutData(gd_lblHeaderzeileOutput);
	lblHeaderzeileOutput.setText(Messages.TemplateComposite_OutputHeader);

	text = new Text(this, SWT.BORDER);

	GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_text.widthHint = 650;
	gd_text.horizontalIndent = 20;
	text.setLayoutData(gd_text);

    }

    // dynamic Layout elements
    private void createLayout(Composite parent) {

	GridData gdTextGa1 = new GridData(GridData.BEGINNING);
	gdTextGa1.grabExcessHorizontalSpace = true;
	gdTextGa1.widthHint = 100;
	gdTextGa1.minimumWidth = 100;
    }

    public void clear() {
	Control[] controls = this.getChildren();
	for (Control control : controls) {
	    if (control instanceof Button) {
		Button b = (Button) control;
		b.setSelection(false);
	    }
	    if (control instanceof Text) {
		Text b = (Text) control;
		b.setText("");
	    }

	}
    }

    public boolean isTemplate() {
	return btnProfilIstTemplate.getSelection();
    }

    public void setTemplate(boolean isTemplate) {
	btnProfilIstTemplate.setSelection(isTemplate);
    }

    public String getOutputHeader() {
	return this.text.getText();
    }

    public void setOutputHeader(String text) {
	this.text.setText(text);
    }

}
