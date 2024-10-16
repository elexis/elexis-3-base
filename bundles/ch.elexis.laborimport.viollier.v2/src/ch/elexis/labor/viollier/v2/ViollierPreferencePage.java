/*******************************************************************************
 *
 * The authorship of this code and the accompanying materials is held by
 * medshare GmbH, Switzerland. All rights reserved.
 * http://medshare.net
 *
 * This code and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0
 *
 * Year of publication: 2012
 *
 *******************************************************************************/
package ch.elexis.labor.viollier.v2;

import org.apache.commons.lang3.StringUtils;
import java.net.InetAddress;
import java.text.MessageFormat;

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

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.labor.viollier.v2.data.ViollierLaborImportSettings;

/**
 * Einstellungen für Viollier Plugin
 */
public class ViollierPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private ViollierLaborImportSettings mySettings;

	private GridData gridDataForLabels;
	private GridData gridDataForInputs;

	private Group gGlobalSettings;
	private Text tGlobalJMedTransferJar;
	private Text tGlobalJMedTransferParam;
	private Text tGlobalDirDownload;
	private Text tGlobalDirArchive;
	private Text tGlobalDirError;
	private Text tGlobalArchivePurgeInterval;
	private Text tGlobalDocumentCategory;

	private Group gMandantSettings;
	private Button bMandantUseGlobalSettings;
	private Text tMandantDocumentCategory;

	private Group gMachineSettings;
	private Button bMachineUseGlobalSettings;
	private Text tMachineJMedTransferJar;
	private Text tMachineJMedTransferParam;
	private Text tMachineDirDownload;
	private Text tMachineDirArchive;
	private Text tMachineDirError;
	private Text tMachineArchivePurgeInterval;

	private Boolean isInitializing = true;

	/**
	 * Standard Constructor
	 */
	public ViollierPreferencePage() {
		super(GRID);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		String temp;
		int days = 30;
		mySettings.setGlobalJMedTransferJar(tGlobalJMedTransferJar.getText());
		mySettings.setGlobalJMedTransferParam(tGlobalJMedTransferParam.getText());
		mySettings.setGlobalDirDownload(tGlobalDirDownload.getText());
		mySettings.setGlobalDirArchive(tGlobalDirArchive.getText());
		mySettings.setGlobalDirError(tGlobalDirError.getText());
		temp = tGlobalArchivePurgeInterval.getText();
		try {
			days = Integer.parseInt(temp);
		} catch (Exception e) {
		}
		mySettings.setGlobalArchivePurgeInterval(days);
		mySettings.setGlobalDocumentCategory(tGlobalDocumentCategory.getText());

		mySettings.setMandantUsingGlobalSettings(bMandantUseGlobalSettings.getSelection());
		mySettings.setMandantDocumentCategory(tMandantDocumentCategory.getText());

		mySettings.setMachineUsingGlobalSettings(bMachineUseGlobalSettings.getSelection());
		mySettings.setMachineJMedTransferJar(tMachineJMedTransferJar.getText());
		mySettings.setMachineJMedTransferParam(tMachineJMedTransferParam.getText());
		mySettings.setMachineDirDownload(tMachineDirDownload.getText());
		mySettings.setMachineDirArchive(tMachineDirArchive.getText());
		mySettings.setMachineDirError(tMachineDirError.getText());
		temp = tMachineArchivePurgeInterval.getText();
		try {
			days = Integer.parseInt(temp);
		} catch (Exception e) {
		}
		mySettings.setMachineArchivePurgeInterval(days);

		mySettings.saveSettings();

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		if (mySettings == null)
			mySettings = new ViollierLaborImportSettings((CoreHub.actMandant));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createContents(org.
	 * eclipse.swt.widgets .Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		String undefined = Messages.Preferences_undefiniert;
		Text dummy = new Text(parent, SWT.NONE);
		dummy.setText(Messages.Preferences_ArchivePurgeInterval);
		dummy.pack();
		gridDataForLabels = new GridData(dummy.getSize().x, dummy.getLineHeight());
		gridDataForInputs = new GridData(GridData.FILL_HORIZONTAL);
		if (mySettings == null)
			mySettings = new ViollierLaborImportSettings((CoreHub.actMandant));
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

		// JMedTransfer Jar
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_JMedTransferJar);
		lbl.setLayoutData(gridDataForLabels);
		tGlobalJMedTransferJar = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tGlobalJMedTransferJar.setLayoutData(gridDataForInputs);
		tGlobalJMedTransferJar.setText(undefined);
		tGlobalJMedTransferJar.setMessage("Optional");//$NON-NLS-1$

		// JMedTransfer Parameter
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_JMedTransferParam);
		lbl.setLayoutData(gridDataForLabels);
		tGlobalJMedTransferParam = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tGlobalJMedTransferParam.setLayoutData(gridDataForInputs);
		tGlobalJMedTransferParam.setText(undefined);
		tGlobalJMedTransferParam.setMessage("Optional");//$NON-NLS-1$

		// Download Verzeichnis
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_DirDownload);
		lbl.setLayoutData(gridDataForLabels);
		tGlobalDirDownload = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tGlobalDirDownload.setLayoutData(gridDataForInputs);
		tGlobalDirDownload.setText(undefined);

		// Archive Verzeichnis
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_DirArchive);
		lbl.setLayoutData(gridDataForLabels);
		tGlobalDirArchive = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tGlobalDirArchive.setLayoutData(gridDataForInputs);
		tGlobalDirArchive.setText(undefined);

		// Error Verzeichnis
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_DirError);
		lbl.setLayoutData(gridDataForLabels);
		tGlobalDirError = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tGlobalDirError.setLayoutData(gridDataForInputs);
		tGlobalDirError.setText(undefined);

		// Bereinigung Archiv Verzeichnis
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_ArchivePurgeInterval);
		lbl.setLayoutData(gridDataForLabels);
		tGlobalArchivePurgeInterval = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tGlobalArchivePurgeInterval.setLayoutData(gridDataForInputs);
		tGlobalArchivePurgeInterval.setText(undefined);

		// Document Category
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_DocumentCategory);
		lbl.setLayoutData(gridDataForLabels);
		tGlobalDocumentCategory = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tGlobalDocumentCategory.setLayoutData(gridDataForInputs);
		tGlobalDocumentCategory.setText(undefined);

		// =====================================================================
		// Mandant Settings
		// =====================================================================
		gMandantSettings = new Group(comp, SWT.NONE);
		grp = gMandantSettings;
		grp.setText(Messages.Preferences_MandantSettingsFor);
		grp.setLayout(new GridLayout(2, false));
		grp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Use global Settings
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_UseGlobalSettings);
		lbl.setLayoutData(gridDataForLabels);

		bMandantUseGlobalSettings = new Button(grp, SWT.CHECK);
		bMandantUseGlobalSettings.setSelection(false);
		bMandantUseGlobalSettings.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isInitializing)
					return;
				Button c = (Button) e.widget;
				Boolean mandantUseGlobalSettings = c.getSelection();
				mySettings.setMandantUsingGlobalSettings(mandantUseGlobalSettings);
				showMandantSettings();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		// Document Category
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_DocumentCategory);
		lbl.setLayoutData(gridDataForLabels);
		tMandantDocumentCategory = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tMandantDocumentCategory.setLayoutData(gridDataForInputs);
		tMandantDocumentCategory.setText(undefined);

		// =====================================================================
		// Machine Settings
		// =====================================================================
		gMachineSettings = new Group(comp, SWT.NONE);
		grp = gMachineSettings;
		grp.setText(Messages.Preferences_LocalSettingsFor);
		grp.setLayout(new GridLayout(2, false));
		grp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Use global Settings
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_UseGlobalSettings);
		lbl.setLayoutData(gridDataForLabels);

		bMachineUseGlobalSettings = new Button(grp, SWT.CHECK);
		bMachineUseGlobalSettings.setSelection(false);
		bMachineUseGlobalSettings.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isInitializing)
					return;
				Button c = (Button) e.widget;
				Boolean machineUseGlobalSettings = c.getSelection();
				mySettings.setMachineUsingGlobalSettings(machineUseGlobalSettings);
				showMachineSettings();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		// JMedTransfer Jar
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_JMedTransferJar);
		lbl.setLayoutData(gridDataForLabels);
		tMachineJMedTransferJar = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tMachineJMedTransferJar.setLayoutData(gridDataForInputs);
		tMachineJMedTransferJar.setText(undefined);

		// JMedTransfer Param
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_JMedTransferParam);
		lbl.setLayoutData(gridDataForLabels);
		tMachineJMedTransferParam = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tMachineJMedTransferParam.setLayoutData(gridDataForInputs);
		tMachineJMedTransferParam.setText(undefined);

		// Download Verzeichnis
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_DirDownload);
		lbl.setLayoutData(gridDataForLabels);
		tMachineDirDownload = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tMachineDirDownload.setLayoutData(gridDataForInputs);
		tMachineDirDownload.setText(undefined);

		// Archive Verzeichnis
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_DirArchive);
		lbl.setLayoutData(gridDataForLabels);
		tMachineDirArchive = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tMachineDirArchive.setLayoutData(gridDataForInputs);
		tMachineDirArchive.setText(undefined);

		// Error Verzeichnis
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_DirError);
		lbl.setLayoutData(gridDataForLabels);
		tMachineDirError = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tMachineDirError.setLayoutData(gridDataForInputs);
		tMachineDirError.setText(undefined);

		// Bereinigung Archiv Verzeichnis
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_ArchivePurgeInterval);
		lbl.setLayoutData(gridDataForLabels);
		tMachineArchivePurgeInterval = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tMachineArchivePurgeInterval.setLayoutData(gridDataForInputs);
		tMachineArchivePurgeInterval.setText(undefined);

		// ----------------------------------------------------
		// ----------------------------------------------------
		// Test Button zum Anzeigen welche Einstellung verwendet wird
		// ----------------------------------------------------
		// ----------------------------------------------------
// Button btnShowSettings = new Button(parent, SWT.NONE);
//		btnShowSettings.setText("Test Button: Anzeigen der Ausgewählten Einstellungen"); //$NON-NLS-1$
// btnShowSettings.addSelectionListener(new SelectionListener() {
//
// @Override
// public void widgetSelected(SelectionEvent e){
// SWTHelper
// .showInfo(
//						"Ausgewählte Einstellungen", //$NON-NLS-1$
//						"JMedTransfer Jar: " + mySettings.getJMedTransferJar() + StringUtils.LF //$NON-NLS-1$
//							+ "JMedTransfer Param: " + mySettings.getJMedTransferParam() + StringUtils.LF //$NON-NLS-1$
//							+ "Download Dir: " + mySettings.getDirDownload() + StringUtils.LF //$NON-NLS-1$
//							+ "Archive Dir: " + mySettings.getDirArchive() + StringUtils.LF //$NON-NLS-1$
//							+ "Error Dir: " + mySettings.getDirError() + StringUtils.LF //$NON-NLS-1$
//							+ "Purge Archive: " + Integer.toString(mySettings.getArchivePurgeInterval()) + StringUtils.LF //$NON-NLS-1$
//							+ "Dok. Kategorie: " + mySettings.getDocumentCategory() + StringUtils.LF //$NON-NLS-1$
//							+ "Save Ref Range: " + Boolean.toString(mySettings.getSaveRefRange()) + StringUtils.LF //$NON-NLS-1$
// );
// }
//
// @Override
// public void widgetDefaultSelected(SelectionEvent e){
// // TODO Auto-generated method stub
//
// }
// });
		// ----------------------------------------------------
		// ----------------------------------------------------
		// Ende Test Button
		// ----------------------------------------------------
		// ----------------------------------------------------

		showSettings();
		isInitializing = false;
		return comp;

	}

	/**
	 * Lädt die aktuell gültigen Settings in die Eingabefelder. Es werden alle
	 * Settings geladen (Globale, Mandanten- und Machine-Settings)
	 */
	private void showSettings() {
		tGlobalJMedTransferJar.setText(mySettings.getGlobalJMedTransferJar());
		tGlobalJMedTransferParam.setText(mySettings.getGlobalJMedTransferParam());
		tGlobalDirDownload.setText(mySettings.getGlobalDirDownload());
		tGlobalDirArchive.setText(mySettings.getGlobalDirArchive());
		tGlobalDirError.setText(mySettings.getGlobalDirError());
		tGlobalArchivePurgeInterval.setText(Integer.toString(mySettings.getGlobalArchivePurgeInterval()));
		tGlobalDocumentCategory.setText(mySettings.getGlobalDocumentCategory());

		showMandantSettings();
		showMachineSettings();

	}

	/**
	 * Lädt die aktuell gültigen Mandanten-Settings in die Eingabefelder
	 */
	private void showMandantSettings() {
		Boolean mandantUseGlobalSettings = mySettings.isMandantUsingGlobalSettings();
		String mandantname = StringUtils.EMPTY;
		try {
			mandantname = mySettings.getMandant().getLabel();
		} catch (Exception e) {
		}
		gMandantSettings.setText(MessageFormat.format(Messages.Preferences_MandantSettingsFor, mandantname));
		bMandantUseGlobalSettings.setSelection(mandantUseGlobalSettings);
		tMandantDocumentCategory.setEditable(!mandantUseGlobalSettings);

		tMandantDocumentCategory.setText(mySettings.getMandantDocumentCategory());
	}

	/**
	 * Lädt die aktuell gültigen Machine-Settings in die Eingabefelder
	 */
	private void showMachineSettings() {
		Boolean machineUseGlobalSettings = mySettings.isMachineUsingGlobalSettings();
		String hostname = "localhost"; //$NON-NLS-1$
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
		}
		gMachineSettings.setText(MessageFormat.format(Messages.Preferences_LocalSettingsFor, hostname));
		bMachineUseGlobalSettings.setSelection(machineUseGlobalSettings);
		tMachineJMedTransferJar.setEditable(!machineUseGlobalSettings);
		tMachineJMedTransferParam.setEditable(!machineUseGlobalSettings);
		tMachineDirDownload.setEditable(!machineUseGlobalSettings);
		tMachineDirArchive.setEditable(!machineUseGlobalSettings);
		tMachineDirError.setEditable(!machineUseGlobalSettings);
		tMachineArchivePurgeInterval.setEditable(!machineUseGlobalSettings);

		tMachineJMedTransferJar.setText(mySettings.getMachineJMedTransferJar());
		tMachineJMedTransferParam.setText(mySettings.getMachineJMedTransferParam());
		tMachineDirDownload.setText(mySettings.getMachineDirDownload());
		tMachineDirArchive.setText(mySettings.getMachineDirArchive());
		tMachineDirError.setText(mySettings.getMachineDirError());
		tMachineArchivePurgeInterval.setText(Integer.toString(mySettings.getMachineArchivePurgeInterval()));
	}
}
