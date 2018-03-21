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

import java.util.Date;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.gpb.elexis.cst.preferences.Messages;
import ch.gpb.elexis.cst.service.CstService;

public class ProImmunComposite extends CstComposite {
    CDateTime cdtProimmunDatum;
    Text txtTested;
    Text txtProimmun4;
    Text txtProimmun2;
    Text txtProimmun3;
    Text txtProimmun1;
    private Text txtToBeTested;

    public ProImmunComposite(Composite parent) {
	super(parent, SWT.NONE);

	GridLayout gridLayout = new GridLayout(2, false);
	setLayout(gridLayout);

	createLayout(this);

    }

    // dynamic Layout elements
    private void createLayout(Composite parent) {
	Label lblPiDatum = new Label(parent, SWT.NONE);
	GridData gd_lblPiDatum = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_lblPiDatum.widthHint = 170;
	lblPiDatum.setLayoutData(gd_lblPiDatum);
	lblPiDatum.setText(Messages.CstProfileEditor_Datum);
	cdtProimmunDatum = new CDateTime(parent, CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_MEDIUM
		| CDT.TEXT_TRAIL);
	cdtProimmunDatum.setSelection(new Date());

	cdtProimmunDatum.setData("datum");
	GridData gdProimmunDatum = new GridData(GridData.BEGINNING);
	gdProimmunDatum.grabExcessHorizontalSpace = true;
	gdProimmunDatum.minimumWidth = 100;
	gdProimmunDatum.widthHint = 100;
	cdtProimmunDatum.setLayoutData(gdProimmunDatum);

	Label lblPiTested = new Label(parent, SWT.NONE);
	GridData gd_lblPiTested = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_lblPiTested.widthHint = 170;
	lblPiTested.setLayoutData(gd_lblPiTested);
	lblPiTested.setText(Messages.CstProfileEditor_AnzahlGetesterLebensmittel);

	txtTested = new Text(parent, SWT.BORDER);
	txtTested.setData("tested");
	GridData gdTested = new GridData(GridData.BEGINNING);
	gdTested.minimumWidth = 30;
	gdTested.widthHint = 30;
	gdTested.heightHint = 20;
	txtTested.setLayoutData(gdTested);

	Label lblVonAnzahlLebensmitteln = new Label(this, SWT.NONE);
	GridData gd_lblVonAnzahlLebensmitteln = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
	gd_lblVonAnzahlLebensmitteln.widthHint = 170;
	lblVonAnzahlLebensmitteln.setLayoutData(gd_lblVonAnzahlLebensmitteln);
	lblVonAnzahlLebensmitteln.setText(Messages.ProImmunComposite_von_anzahl_lebensmitteln);

	txtToBeTested = new Text(this, SWT.BORDER);
	GridData gd_txtToBeTested = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_txtToBeTested.minimumWidth = 30;
	gd_txtToBeTested.widthHint = 30;
	txtToBeTested.setLayoutData(gd_txtToBeTested);
	txtToBeTested.setData("tobetested");

	Label lblPiText4 = new Label(parent, SWT.NONE);
	GridData gd_lblPiText4 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_lblPiText4.widthHint = 170;
	lblPiText4.setLayoutData(gd_lblPiText4);
	lblPiText4.setText(Messages.CstProfileEditor_Reaktionsstaerke4);

	txtProimmun4 = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
	txtProimmun4.setData("text4");
	GridData gdPiText4 = new GridData(GridData.FILL_HORIZONTAL);
	gdPiText4.horizontalAlignment = SWT.LEFT;
	gdPiText4.widthHint = 670;
	gdPiText4.heightHint = 45;
	gdPiText4.minimumHeight = 45;
	gdPiText4.grabExcessHorizontalSpace = true;
	gdPiText4.minimumWidth = 670;
	txtProimmun4.setLayoutData(gdPiText4);

	Label lblPiText3 = new Label(parent, SWT.NONE);
	GridData gd_lblPiText3 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_lblPiText3.widthHint = 170;
	lblPiText3.setLayoutData(gd_lblPiText3);
	lblPiText3.setText(Messages.CstProfileEditor_Reaktionsstaerke3);

	txtProimmun3 = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
	txtProimmun3.setData("text3");
	GridData gdPiText3 = new GridData(GridData.FILL_HORIZONTAL);
	gdPiText3.horizontalAlignment = SWT.LEFT;
	gdPiText3.widthHint = 670;
	gdPiText3.heightHint = 45;
	gdPiText3.minimumHeight = 45;
	gdPiText3.grabExcessHorizontalSpace = true;
	gdPiText3.minimumWidth = 670;
	txtProimmun3.setLayoutData(gdPiText3);

	Label lblPiText2 = new Label(parent, SWT.NONE);
	GridData gd_lblPiText2 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_lblPiText2.widthHint = 170;
	lblPiText2.setLayoutData(gd_lblPiText2);
	lblPiText2.setText(Messages.CstProfileEditor_Reaktionsstaerke2);

	txtProimmun2 = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
	txtProimmun2.setData("text2");
	GridData gdPiText2 = new GridData(GridData.FILL_HORIZONTAL);
	gdPiText2.horizontalAlignment = SWT.LEFT;
	gdPiText2.widthHint = 670;
	gdPiText2.heightHint = 45;
	gdPiText2.minimumHeight = 45;
	gdPiText2.grabExcessHorizontalSpace = true;
	gdPiText2.minimumWidth = 670;
	txtProimmun2.setLayoutData(gdPiText2);

	Label lblPiText1 = new Label(parent, SWT.NONE);
	GridData gd_lblPiText1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_lblPiText1.widthHint = 170;
	lblPiText1.setLayoutData(gd_lblPiText1);
	lblPiText1.setText(Messages.CstProfileEditor_Reaktionsstaerke1);

	txtProimmun1 = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
	txtProimmun1.setData("text1");
	GridData gdPiText1 = new GridData(GridData.FILL_HORIZONTAL);
	gdPiText1.horizontalAlignment = SWT.LEFT;
	gdPiText1.widthHint = 670;
	gdPiText1.heightHint = 45;
	gdPiText1.minimumHeight = 45;
	gdPiText1.grabExcessHorizontalSpace = true;
	gdPiText1.minimumWidth = 670;
	txtProimmun1.setLayoutData(gdPiText1);

    }

    public String getDate() {
	return CstService.getCompactFromDate(cdtProimmunDatum.getSelection());
    }

    public void setDate(String date) {
	this.cdtProimmunDatum.setSelection(CstService.getDateFromCompact(date));
    }

    public int getTested() {
	return new Integer(txtTested.getText()).intValue();
    }

    public void setTested(int tested) {
	this.txtTested.setText(String.valueOf(tested));
    }

    public int getToBeTested() {
	return new Integer(txtToBeTested.getText()).intValue();
    }

    public void setToBeTested(int toBeTested) {
	this.txtToBeTested.setText(String.valueOf(toBeTested));
    }


    public String getReaktionsStaerke4() {
	return txtProimmun4.getText();
    }

    public void setReaktionsStaerke4(String txtProimmun4) {
	this.txtProimmun4.setText(txtProimmun4);
    }

    public String getReaktionsStaerke2() {
	return txtProimmun2.getText();
    }

    public void setReaktionsStaerke2(String txtProimmun2) {
	this.txtProimmun2.setText(txtProimmun2);
	;
    }

    public String getReaktionsStaerke3() {
	return txtProimmun3.getText();
    }

    public void setReaktionsStaerke3(String txtProimmun3) {
	this.txtProimmun3.setText(txtProimmun3);
    }

    public String getReaktionsStaerke1() {
	return txtProimmun1.getText();
    }

    public void setReaktionsStaerke1(String txtProimmun1) {
	this.txtProimmun1.setText(txtProimmun1);
	;
    }

}
