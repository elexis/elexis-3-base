/*******************************************************************************
 * 
 * The authorship of this code and the accompanying materials is held by 
 * medshare GmbH, Switzerland. All rights reserved. 
 * http://medshare.net
 * 
 * This code and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0
 * 
 * Year of publication: 2013
 * 
 *******************************************************************************/
package net.medshare.connector.viollier;

import java.net.InetAddress;
import java.text.MessageFormat;

import net.medshare.connector.viollier.data.ViollierConnectorSettings;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Mandant;

public class ViollierConnectorPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	private ViollierConnectorSettings mySettings;
	
	private GridData gridDataForLabels;
	private GridData gridDataForInputs;
	
	private Group gGlobalSettings;
	private Text tGlobalLoginUrl;
	private Text tGlobalConsultItUrl;
	private Text tGlobalOrderItUrl;
	private Text tGlobalUserName;
	private Text tGlobalUserPassword;
	private Text tGlobalViollierClientId;
	private Button bGlobalPreferedPresentation;
	
	private Group gMandantSettings;
	private Button bMandantUseGlobalSettings;
	private Text tMandantUserName;
	private Text tMandantUserPassword;
	private Text tMandantViollierClientId;
	
	private Group gMachineSettings;
	private Button bMachineUseGlobalSettings;
	private Button bMachinePreferedPresentation;
	
	private Boolean isInitializing = true;
	
	/**
	 * Standard Constructor
	 */
	public ViollierConnectorPreferencePage(){
		super(GRID);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	@Override
	public boolean performOk(){
		
		mySettings.setGlobalLoginUrl(tGlobalLoginUrl.getText());
		mySettings.setGlobalConsultItUrl(tGlobalConsultItUrl.getText());
		mySettings.setGlobalOrderItUrl(tGlobalOrderItUrl.getText());
		mySettings.setGlobalUserName(tGlobalUserName.getText());
		mySettings.setGlobalUserPassword(tGlobalUserPassword.getText());
		mySettings.setGlobalViollierClientId(tGlobalViollierClientId.getText());
		mySettings.setGlobalPreferedPresentation(bGlobalPreferedPresentation.getSelection());
		
		mySettings.setMandantUsingGlobalSettings(bMandantUseGlobalSettings.getSelection());
		mySettings.setMandantUserName(tMandantUserName.getText());
		mySettings.setMandantUserPassword(tMandantUserPassword.getText());
		mySettings.setMandantViollierClientId(tMandantViollierClientId.getText());
		
		mySettings.setMachineUsingGlobalSettings(bMachineUseGlobalSettings.getSelection());
		mySettings.setMachinePreferedPresentation(bMachinePreferedPresentation.getSelection());
		
		mySettings.saveSettings();
		return true;
	}
	
	@Override
	public void init(IWorkbench workbench){
		if (mySettings == null)
			mySettings =
				new ViollierConnectorSettings(
					(Mandant) ElexisEventDispatcher.getSelected(Mandant.class));
	}
	
	@Override
	protected Control createContents(Composite parent){
		String undefined = Messages.Preferences_undefiniert;
		Text dummy = new Text(parent, SWT.NONE);
		dummy.setText(Messages.Preferences_LoginUrl);
		dummy.pack();
		gridDataForLabels = new GridData(dummy.getSize().x, dummy.getLineHeight());
		gridDataForInputs = new GridData(GridData.FILL_HORIZONTAL);
		if (mySettings == null) {
			mySettings =
				new ViollierConnectorSettings(
					(Mandant) ElexisEventDispatcher.getSelected(Mandant.class));
		}
		dummy.dispose();
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		
		Label lbl;
		Group grp;
		
		// =====================================================================
		// Global Settings
		// =====================================================================
		gGlobalSettings = new Group(comp, SWT.NONE);
		grp = gGlobalSettings;
		grp.setText(Messages.Preferences_GlobalSettings);
		grp.setLayout(new GridLayout(2, false));
		grp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Login URL
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_LoginUrl);
		lbl.setLayoutData(gridDataForLabels);
		tGlobalLoginUrl = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tGlobalLoginUrl.setLayoutData(gridDataForInputs);
		tGlobalLoginUrl.setText(undefined);
		
		// ConsultIT URL
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_ConsultItUrl);
		lbl.setLayoutData(gridDataForLabels);
		tGlobalConsultItUrl = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tGlobalConsultItUrl.setLayoutData(gridDataForInputs);
		tGlobalConsultItUrl.setText(undefined);
		
		// OrderIT URL
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_OrderItUrl);
		lbl.setLayoutData(gridDataForLabels);
		tGlobalOrderItUrl = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tGlobalOrderItUrl.setLayoutData(gridDataForInputs);
		tGlobalOrderItUrl.setText(undefined);
		
		// User Name Viollier Portal
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_UserName);
		lbl.setLayoutData(gridDataForLabels);
		tGlobalUserName = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tGlobalUserName.setLayoutData(gridDataForInputs);
		tGlobalUserName.setText(undefined);
		
		// Passwort Viollier Portal
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_UserPassword);
		lbl.setLayoutData(gridDataForLabels);
		tGlobalUserPassword = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tGlobalUserPassword.setLayoutData(gridDataForInputs);
		tGlobalUserPassword.setText(undefined);
		
		// Viollier Kundennummer
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_ViollierClientId);
		lbl.setLayoutData(gridDataForLabels);
		tGlobalViollierClientId = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tGlobalViollierClientId.setLayoutData(gridDataForInputs);
		tGlobalViollierClientId.setText(undefined);
		
		// Kumulativ- oder Normalanzeige
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_PreferedPresentation);
		lbl.setLayoutData(gridDataForLabels);
		bGlobalPreferedPresentation = new Button(grp, SWT.CHECK);
		bGlobalPreferedPresentation.setSelection(false);
		
		// =====================================================================
		// Mandant Settings
		// =====================================================================
		String mandantname = ""; //$NON-NLS-1$
		try {
			mandantname = mySettings.getMandant().getLabel();
		} catch (Exception e) {}
		gMandantSettings = new Group(comp, SWT.NONE);
		grp = gMandantSettings;
		grp.setText(MessageFormat.format(Messages.Preferences_MandantSettingsFor, mandantname));
		grp.setLayout(new GridLayout(2, false));
		grp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Use global Settings
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_GlobalSettings);
		lbl.setLayoutData(gridDataForLabels);
		
		bMandantUseGlobalSettings = new Button(grp, SWT.CHECK);
		bMandantUseGlobalSettings.setSelection(false);
		bMandantUseGlobalSettings.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e){
				if (isInitializing)
					return;
				Button c = (Button) e.widget;
				Boolean mandantUseGlobalSettings = c.getSelection();
				mySettings.setMandantUsingGlobalSettings(mandantUseGlobalSettings);
				showMandantSettings();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e){
				// TODO Auto-generated method stub
			}
		});
		
		// User Name Viollier Portal
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_UserName);
		lbl.setLayoutData(gridDataForLabels);
		tMandantUserName = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tMandantUserName.setLayoutData(gridDataForInputs);
		tMandantUserName.setText(undefined);
		
		// Passwort Viollier Portal
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_UserPassword);
		lbl.setLayoutData(gridDataForLabels);
		tMandantUserPassword = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tMandantUserPassword.setLayoutData(gridDataForInputs);
		tMandantUserPassword.setText(undefined);
		
		// Viollier Kundennummer
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_ViollierClientId);
		lbl.setLayoutData(gridDataForLabels);
		tMandantViollierClientId = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tMandantViollierClientId.setLayoutData(gridDataForInputs);
		tMandantViollierClientId.setText(undefined);
		
		// =====================================================================
		// Machine Settings
		// =====================================================================
		String hostname = "localhost"; //$NON-NLS-1$
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {}
		gMachineSettings = new Group(comp, SWT.NONE);
		grp = gMachineSettings;
		grp.setText(MessageFormat.format(Messages.Preferences_LocalSettingsFor, hostname));
		grp.setLayout(new GridLayout(2, false));
		grp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Use global Settings
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_GlobalSettings);
		lbl.setLayoutData(gridDataForLabels);
		
		bMachineUseGlobalSettings = new Button(grp, SWT.CHECK);
		bMachineUseGlobalSettings.setSelection(false);
		bMachineUseGlobalSettings.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e){
				if (isInitializing)
					return;
				Button c = (Button) e.widget;
				Boolean machineUseGlobalSettings = c.getSelection();
				mySettings.setMachineUsingGlobalSettings(machineUseGlobalSettings);
				showMachineSettings();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e){
				// TODO Auto-generated method stub
			}
		});
		// Kumulativ- oder Normalanzeige
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_PreferedPresentation);
		lbl.setLayoutData(gridDataForLabels);
		bMachinePreferedPresentation = new Button(grp, SWT.CHECK);
		bMachinePreferedPresentation.setSelection(false);
		bMachinePreferedPresentation.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e){
				if (isInitializing)
					return;
				Button c = (Button) e.widget;
				Boolean machinePreferedPresentation = c.getSelection();
				mySettings.setMachinePreferedPresentation(machinePreferedPresentation);
				showMachineSettings();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e){
				// TODO Auto-generated method stub
			}
		});
		
		showSettings();
		isInitializing = false;
		return comp;
	}
	
	/**
	 * Lädt die aktuell gültigen Settings in die Eingabefelder. Es werden alle Settings geladen
	 * (Globale, Mandanten- und Machine-Settings)
	 */
	private void showSettings(){
		
		tGlobalLoginUrl.setText(mySettings.getGlobalLoginUrl());
		tGlobalConsultItUrl.setText(mySettings.getGlobalConsultItUrl());
		tGlobalOrderItUrl.setText(mySettings.getGlobalOrderItUrl());
		
		tGlobalUserName.setText(mySettings.getGlobalUserName());
		tGlobalUserPassword.setText(mySettings.getGlobalUserPassword());
		tGlobalViollierClientId.setText(mySettings.getGlobalViollierClientId());
		
		bGlobalPreferedPresentation.setSelection(mySettings.getGlobalPreferedPresentation());
		
		showMandantSettings();
		showMachineSettings();
	}
	
	private void showMandantSettings(){
		Boolean mandantUseGlobalSettings = mySettings.isMandantUsingGlobalSettings();
		String mandantname = ""; //$NON-NLS-1$
		try {
			mandantname = mySettings.getMandant().getLabel();
		} catch (Exception e) {}
		gMandantSettings.setText(MessageFormat.format(Messages.Preferences_MandantSettingsFor,
			mandantname));
		bMandantUseGlobalSettings.setSelection(mandantUseGlobalSettings);
		
		tMandantUserName.setEditable(!mandantUseGlobalSettings);
		tMandantUserPassword.setEditable(!mandantUseGlobalSettings);
		tMandantViollierClientId.setEditable(!mandantUseGlobalSettings);
		
		tMandantUserName.setText(mySettings.getMandantUserName());
		tMandantUserPassword.setText(mySettings.getMandantUserPassword());
		tMandantViollierClientId.setText(mySettings.getMandantViollierClientId());
		
	}
	
	private void showMachineSettings(){
		Boolean machineUseGlobalSettings = mySettings.isMachineUsingGlobalSettings();
		String hostname = "localhost"; //$NON-NLS-1$
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {}
		gMachineSettings.setText(MessageFormat.format(Messages.Preferences_LocalSettingsFor,
			hostname));
		bMachineUseGlobalSettings.setSelection(machineUseGlobalSettings);
		
		// bMachinePreferedPresentation.setGrayed(machineUseGlobalSettings);
		bMachinePreferedPresentation.setSelection(mySettings.getMachinePreferedPresentation());
	}
	
	@Override
	protected void createFieldEditors(){
		// TODO Auto-generated method stub
		
	}
	
}
