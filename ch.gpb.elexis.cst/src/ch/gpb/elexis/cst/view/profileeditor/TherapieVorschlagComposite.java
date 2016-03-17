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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.gpb.elexis.cst.preferences.Messages;

public class TherapieVorschlagComposite extends CstComposite {

    Text txtTherapie;
    Text txtDiagnose;

    public TherapieVorschlagComposite(Composite parent) {
	super(parent, SWT.NONE);

	GridLayout gridLayout = new GridLayout(2, false);
	setLayout(gridLayout);

	createLayout(this);

	GridData gdHint = new GridData();
	gdHint.horizontalSpan = 2;
	gdHint.verticalIndent = 40;
    }

    // dynamic Layout elements
    private void createLayout(Composite parent) {

	Label labelTherapievorschlag = new Label(parent, SWT.NONE);
	labelTherapievorschlag.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
	labelTherapievorschlag.setText(Messages.CstProfileEditor_Therapievorschlag);
	labelTherapievorschlag.setSize(200, 20);

	txtTherapie = new Text(parent, SWT.V_SCROLL | SWT.MULTI);
	txtTherapie.setData("therapie");

	GridData gdTextTherapie = new GridData(GridData.FILL_HORIZONTAL);
	gdTextTherapie.widthHint = 600;
	gdTextTherapie.minimumWidth = 600;
	gdTextTherapie.horizontalAlignment = SWT.LEFT;
	gdTextTherapie.verticalAlignment = SWT.TOP;
	gdTextTherapie.grabExcessHorizontalSpace = true;
	gdTextTherapie.heightHint = 100;
	gdTextTherapie.horizontalIndent = 10;
	gdTextTherapie.verticalIndent = 0;
	txtTherapie.setLayoutData(gdTextTherapie);

	Label labelDiagnose = new Label(parent, SWT.NONE);
	GridData gd_labelDiagnose = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
	gd_labelDiagnose.verticalIndent = 10;
	labelDiagnose.setLayoutData(gd_labelDiagnose);
	labelDiagnose.setText(Messages.CstProfileEditor_Diagnose);
	labelDiagnose.setSize(200, 20);

	txtDiagnose = new Text(parent, SWT.V_SCROLL | SWT.MULTI);
	txtDiagnose.setData("diagnose");
	GridData gdTextDiagnose = new GridData(GridData.FILL_HORIZONTAL);
	gdTextDiagnose.widthHint = 600;
	gdTextDiagnose.minimumWidth = 600;
	gdTextDiagnose.horizontalAlignment = SWT.LEFT;
	gdTextDiagnose.verticalAlignment = SWT.TOP;
	gdTextDiagnose.grabExcessHorizontalSpace = true;
	gdTextDiagnose.heightHint = 100;
	gdTextDiagnose.horizontalIndent = 10;
	gdTextDiagnose.verticalIndent = 10;
	txtDiagnose.setLayoutData(gdTextDiagnose);

    }

    public String getTextTherapie() {
	return txtTherapie.getText();
    }
    public void setTextTherapie(String textTherapie) {
	this.txtTherapie.setText(textTherapie);
    }


    public String getTextDiagnose() {
	return txtDiagnose.getText();
    }
    public void setTextDiagnose(String textDiagnose) {
	this.txtDiagnose.setText(textDiagnose);
    }

    public void clear() {
	txtDiagnose.setText("");
	txtTherapie.setText("");
    }

}