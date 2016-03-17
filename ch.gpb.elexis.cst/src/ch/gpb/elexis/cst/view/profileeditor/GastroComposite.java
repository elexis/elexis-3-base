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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.gpb.elexis.cst.preferences.Messages;
import ch.gpb.elexis.cst.service.CstService;
import ch.gpb.elexis.cst.view.CstProfileEditor.GroupTokens;

public class GastroComposite extends CstComposite {

    CDateTime cdtGastroDatum;
    CDateTime cdtColoDatum;
    // gastro makro
    Button bGastroMakro1;
    Button bGastroMakro2;
    Button bGastroMakro3;
    Text txtGastroMakro;
    
    // gastro histo
    Button bGastroHisto1;
    Button bGastroHisto2;
    Button bGastroHisto3;
    Text txtGastroHisto;
    
    // colo makro
    Button bColoMakro1;
    Button bColoMakro2;
    Button bColoMakro3;
    Text txtColoMakro;

    // colo histo
    Button bColoHisto1;
    Button bColoHisto2;
    Button bColoHisto3;
    Text txtColoHisto;


    public GastroComposite(Composite parent) {
	super(parent, SWT.NONE);

	GridLayout gridLayout = new GridLayout(2, false);
	setLayout(gridLayout);

	GridData gdGastro = new GridData();
	gdGastro.heightHint = 800;
	gdGastro.minimumHeight = 800;
	setLayoutData(gdGastro);

	Label lblAuswahlBefundparameter = new Label(this, SWT.NONE);
	lblAuswahlBefundparameter.setText("Auswahl Befundparameter");

	Label lblSeparator = new Label(this, SWT.NONE);
	lblSeparator.setText("Separator");

	createLayout(this);

    }

    // dynamic Layout elements
    private void createLayout(Composite parent) {
	// 1 A
	Label lblGaDatum = new Label(parent, SWT.NONE);
	lblGaDatum.setText(Messages.CstProfileEditor_Datum);

	// 1 B
	cdtGastroDatum = new CDateTime(parent, CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_MEDIUM
		| CDT.TEXT_TRAIL);
	cdtGastroDatum.setSelection(new Date());
	cdtGastroDatum.setData("datumGastro");

	GridData gdTextGa1 = new GridData(GridData.BEGINNING);
	gdTextGa1.grabExcessHorizontalSpace = true;
	gdTextGa1.widthHint = 100;
	gdTextGa1.minimumWidth = 100;
	cdtGastroDatum.setLayoutData(gdTextGa1);

	// 2 A

	Group groupGastroMakro = new Group(parent, SWT.SHADOW_IN);
	groupGastroMakro.setText(Messages.CstProfileEditor_GastroMakroBefund);
	groupGastroMakro.setLayout(new RowLayout(SWT.VERTICAL));
	groupGastroMakro.setData(GroupTokens.GASTRO_MAKRO);

	bGastroMakro1 = new Button(groupGastroMakro, SWT.RADIO);
	bGastroMakro1.setText(Messages.CstProfileEditor_KeinBefund);
	//bA1.setData("gastro_makro");
	bGastroMakro1.setData(GroupTokens.GASTRO_MAKRO);

	bGastroMakro2 = new Button(groupGastroMakro, SWT.RADIO);
	bGastroMakro2.setText(Messages.CstProfileEditor_Normal);

	bGastroMakro3 = new Button(groupGastroMakro, SWT.RADIO);
	bGastroMakro3.setText(Messages.CstProfileEditor_Pathologisch);

	// 2 B
	txtGastroMakro = new Text(parent, SWT.MULTI);
	txtGastroMakro.setData("text1");
	GridData gdGaText1 = new GridData(GridData.BEGINNING);
	gdGaText1.grabExcessHorizontalSpace = true;
	gdGaText1.widthHint = 300;
	gdGaText1.minimumWidth = 100;
	gdGaText1.heightHint = 80;
	txtGastroMakro.setLayoutData(gdGaText1);

	Group groupGastroHisto = new Group(parent, SWT.SHADOW_IN);
	groupGastroHisto.setText(Messages.CstProfileEditor_GastroHistoBefund);
	groupGastroHisto.setLayout(new RowLayout(SWT.VERTICAL));
	groupGastroHisto.setData(GroupTokens.GASTRO_HISTO);

	bGastroHisto1 = new Button(groupGastroHisto, SWT.RADIO);
	bGastroHisto1.setText(Messages.CstProfileEditor_KeinBefund);
	//bB1.setData("gastro_histo");
	bGastroHisto1.setData(GroupTokens.GASTRO_HISTO);

	bGastroHisto2 = new Button(groupGastroHisto, SWT.RADIO);
	bGastroHisto2.setText(Messages.CstProfileEditor_Normal);

	bGastroHisto3 = new Button(groupGastroHisto, SWT.RADIO);
	bGastroHisto3.setText(Messages.CstProfileEditor_Pathologisch);

	// 2 B
	txtGastroHisto = new Text(parent, SWT.MULTI);
	txtGastroHisto.setData("text2");
	GridData gdGaText3 = new GridData(GridData.BEGINNING);
	gdGaText3.grabExcessHorizontalSpace = true;
	gdGaText3.widthHint = 300;
	gdGaText3.minimumWidth = 100;
	gdGaText3.heightHint = 80;
	txtGastroHisto.setLayoutData(gdGaText3);

	// 5 A
	Label lblColoDatum = new Label(parent, SWT.NONE);
	lblColoDatum.setText(Messages.CstProfileEditor_Datum);

	cdtColoDatum = new CDateTime(parent, CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_MEDIUM
		| CDT.TEXT_TRAIL);
	GridData gd_cdtColoDatum = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_cdtColoDatum.widthHint = 100;
	gd_cdtColoDatum.minimumWidth = 100;
	cdtColoDatum.setLayoutData(gd_cdtColoDatum);
	cdtColoDatum.setSelection(new Date());
	cdtColoDatum.setData("datumColo");

	GridData gdColo = new GridData(GridData.BEGINNING);
	gdColo.grabExcessHorizontalSpace = true;
	gdColo.widthHint = 100;
	gdColo.minimumWidth = 100;
	cdtGastroDatum.setLayoutData(gdColo);

	// 6 A
	Group groupColoMakro = new Group(parent, SWT.SHADOW_IN);
	groupColoMakro.setText(Messages.CstProfileEditor_ColoMakroBefund);
	groupColoMakro.setLayout(new RowLayout(SWT.VERTICAL));
	groupColoMakro.setData(GroupTokens.COLO_MAKRO);

	bColoMakro1 = new Button(groupColoMakro, SWT.RADIO);
	bColoMakro1.setText(Messages.CstProfileEditor_KeinBefund);
	//bC1.setData("colo_makro");
	bColoMakro1.setData(GroupTokens.COLO_MAKRO);

	bColoMakro2 = new Button(groupColoMakro, SWT.RADIO);
	bColoMakro2.setText(Messages.CstProfileEditor_Normal);

	bColoMakro3 = new Button(groupColoMakro, SWT.RADIO);
	bColoMakro3.setText(Messages.CstProfileEditor_Pathologisch);

	GridData gdGaText2 = new GridData(GridData.BEGINNING);
	gdGaText2.grabExcessHorizontalSpace = true;
	gdGaText2.widthHint = 300;
	gdGaText2.minimumWidth = 100;
	gdGaText2.heightHint = 80;
	gdGaText2.verticalAlignment = SWT.BEGINNING;

	// 6 B
	txtColoMakro = new Text(parent, SWT.MULTI);
	txtColoMakro.setLayoutData(gdGaText2);
	txtColoMakro.setData("text3");

	Group groupColoHisto = new Group(parent, SWT.SHADOW_IN);
	groupColoHisto.setText("Colo Histo-Befund:");
	groupColoHisto.setLayout(new RowLayout(SWT.VERTICAL));
	groupColoHisto.setData(GroupTokens.GASTRO_HISTO);

	bColoHisto1 = new Button(groupColoHisto, SWT.RADIO);
	bColoHisto1.setText(Messages.CstProfileEditor_KeinBefund);
	//bD1.setData("colo_histo");
	bColoHisto1.setData(GroupTokens.COLO_HISTO);

	bColoHisto2 = new Button(groupColoHisto, SWT.RADIO);
	bColoHisto2.setText(Messages.CstProfileEditor_Normal);

	bColoHisto3 = new Button(groupColoHisto, SWT.RADIO);
	bColoHisto3.setText(Messages.CstProfileEditor_Pathologisch);

	GridData gdGaText4 = new GridData(GridData.BEGINNING);
	gdGaText4.grabExcessHorizontalSpace = true;
	gdGaText4.widthHint = 300;
	gdGaText4.minimumWidth = 100;
	gdGaText4.heightHint = 80;
	gdGaText4.verticalAlignment = SWT.BEGINNING;

	// 6 B
	txtColoHisto = new Text(parent, SWT.MULTI);
	txtColoHisto.setLayoutData(gdGaText4);
	txtColoHisto.setData("text4");

    }

    public void clear() {
	Control[] controls = this.getChildren();
	for (Control control : controls) {

	    if (control instanceof Group) {
		Group g = (Group) control;
		Control[] buttons = g.getChildren();
		for (Control button : buttons) {

		    if (button instanceof Button) {
			Button b = (Button) button;
			b.setSelection(false);
		    }
		}
	    }

	}
    }

    public String getGastroDatum() {
	return CstService.getCompactFromDate(cdtGastroDatum.getSelection());
    }

    public void setGastroDatum(String gastroDatum) {
	this.cdtGastroDatum.setSelection(CstService.getDateFromCompact(gastroDatum));
    }

    public String getColoDatum() {
	return CstService.getCompactFromDate(cdtColoDatum.getSelection());
    }

    public void setColoDatum(String gastroDatum) {
	this.cdtColoDatum.setSelection(CstService.getDateFromCompact(gastroDatum));
    }


    // Gastro Makro
    public char getBefundGastroMakro() {
	if (bGastroMakro1.getSelection()) {
	    return '0';
	}
	if (bGastroMakro2.getSelection()) {
	    return '1';
	}
	if (bGastroMakro3.getSelection()) {
	    return '2';
	}
	return 0;
    }

    public void setBefundGastroMakro(char sel) {
	int selection = Character.getNumericValue(sel);

	switch (selection) {
	case 0:
	    bGastroMakro1.setSelection(true);
	    break;

	case 1:
	    bGastroMakro2.setSelection(true);
	    break;

	case 2:
	    bGastroMakro3.setSelection(true);
	    break;

	default:
	    bGastroMakro1.setSelection(true);
	    break;
	}

    }

    public String getTxtGastroMakro() {
	return txtGastroMakro.getText();
    }

    public void setTxtGastroMakro(String txtGastroMakro) {
	this.txtGastroMakro.setText(txtGastroMakro);
	;
    }

    // Gastro Histo
    public char getBefundGastroHisto() {
	if (bGastroHisto1.getSelection()) {
	    return '0';
	}
	if (bGastroHisto2.getSelection()) {
	    return '1';
	}
	if (bGastroHisto3.getSelection()) {
	    return '2';
	}
	return 0;
    }

    public void setBefundGastroHisto(char sel) {
	int selection = Character.getNumericValue(sel);
	switch (selection) {
	case 0:
	    bGastroHisto1.setSelection(true);
	    break;

	case 1:
	    bGastroHisto2.setSelection(true);
	    break;

	case 2:
	    bGastroHisto3.setSelection(true);
	    break;

	default:
	    bGastroHisto1.setSelection(true);
	    break;
	}

    }

    public String getTxtGastroHisto() {
	return txtGastroHisto.getText();
    }

    public void setTxtGastroHisto(String txtGastroHisto) {
	this.txtGastroHisto.setText(txtGastroHisto);
	;
    }



    // Colo Makro
    public char getBefundColoMakro() {
	if (bColoMakro1.getSelection()) {
	    return '0';
	}
	if (bColoMakro2.getSelection()) {
	    return '1';
	}
	if (bColoMakro3.getSelection()) {
	    return '2';
	}
	return 0;
    }

    public void setBefundColoMakro(char sel) {
	int selection = Character.getNumericValue(sel);
	switch (selection) {
	case 0:
	    bColoMakro1.setSelection(true);
	    break;

	case 1:
	    bColoMakro2.setSelection(true);
	    break;

	case 2:
	    bColoMakro3.setSelection(true);
	    break;

	default:
	    bColoMakro1.setSelection(true);
	    break;
	}

    }

    public String getTxtColoMakro() {
	return txtColoMakro.getText();
    }

    public void setTxtColoMakro(String txtColoMakro) {
	this.txtColoMakro.setText(txtColoMakro);
    }

    // Colo Histo
    public char getBefundColoHisto() {
	if (bColoHisto1.getSelection()) {
	    return '0';
	}
	if (bColoHisto2.getSelection()) {
	    return '1';
	}
	if (bColoHisto3.getSelection()) {
	    return '2';
	}
	return 0;
    }

    public void setBefundColoHisto(char sel) {
	int selection = Character.getNumericValue(sel);
	switch (selection) {
	case 0:
	    bColoHisto1.setSelection(true);
	    break;

	case 1:
	    bColoHisto2.setSelection(true);
	    break;

	case 2:
	    bColoHisto3.setSelection(true);
	    break;

	default:
	    bColoHisto1.setSelection(true);
	    break;
	}

    }

    public String getTxtColoHisto() {
	return txtColoHisto.getText();
    }

    public void setTxtColoHisto(String txtColoHisto) {
	this.txtColoHisto.setText(txtColoHisto);
	;
    }

}
