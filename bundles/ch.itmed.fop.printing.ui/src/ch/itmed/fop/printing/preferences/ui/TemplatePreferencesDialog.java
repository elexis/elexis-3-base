/*******************************************************************************
 * Copyright (c) 2019 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.fop.printing.preferences.ui;

import org.apache.commons.lang3.StringUtils;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.util.SWTHelper;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.resources.Messages;
import ch.itmed.fop.printing.resources.PrinterProvider;
import ch.itmed.fop.printing.resources.ResourceProvider;

class TemplatePreferencesDialog extends TitleAreaDialog {
	private String docName, textOrientation;
	private int selectionIndex;
	private IPreferenceStore settingsStore;
	private Composite templateArea, pageCustomArea;
	private Text xslTemplate, pageWidth, pageHeight, pageMarginTop, pageMarginBottom, pageMarginLeft, pageMarginRight,
			responsiblePharmacist;
	private Combo settingsStoreCombo, printerName, pageTemplateName;
	private Button buttonHorizontal, buttonVertical, xslCustomFlag, xslCustomFileDialog, pageCustomCheckBox;

	public TemplatePreferencesDialog(Shell parentShell, int selectionIndex) {
		super(parentShell);
		this.selectionIndex = selectionIndex;
		docName = PreferenceConstants.getDocumentName(selectionIndex);
		if (CoreHub.localCfg.get(PreferenceConstants.getDocPreferenceConstant(docName, 12), true)) {
			settingsStore = new ConfigServicePreferenceStore(Scope.GLOBAL);
		} else {
			settingsStore = new ConfigServicePreferenceStore(Scope.LOCAL);
		}
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label description = new Label(composite, SWT.NONE);
		description.setLayoutData(SWTHelper.getFillGridData(4, true, 1, false));

		new Label(composite, SWT.NONE).setText(Messages.TemplatePreferences_SettingsStore);
		settingsStoreCombo = new Combo(composite, SWT.READ_ONLY);
		settingsStoreCombo.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
		settingsStoreCombo.setItems(Messages.TemplatePreferences_SettingsStore_Global,
				Messages.TemplatePreferences_SettingsStore_Local);
		settingsStoreCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (settingsStoreCombo.getSelectionIndex() == 0) {
					settingsStore = new ConfigServicePreferenceStore(Scope.GLOBAL);
				} else {
					settingsStore = new ConfigServicePreferenceStore(Scope.LOCAL);
				}
				updateValues();
				System.out.println(settingsStoreCombo.getSelectionIndex());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// Auto-generated method stub
			}
		});

		new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR)
				.setLayoutData(SWTHelper.getFillGridData(4, true, 1, false));

		new Label(composite, SWT.NONE).setText("Drucker");
		printerName = new Combo(composite, SWT.READ_ONLY);
		printerName.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
		printerName.setItems(PrinterProvider.getAvailablePrinters());

		Group orientationGroup = new Group(composite, SWT.NONE);
		orientationGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
		orientationGroup.setLayoutData(SWTHelper.getFillGridData(4, true, 1, false));

		new Label(orientationGroup, SWT.NONE).setText(Messages.TemplatePreferences_TextOrientation);

		buttonHorizontal = new Button(orientationGroup, SWT.RADIO);
		buttonHorizontal.setText(Messages.TemplatePreferences_TextOrientation_Horizontal);
		buttonHorizontal.setImage(ResourceProvider.loadImage(ResourceProvider.IMAGE_ELLIPSIS_H_PATH));
		buttonHorizontal.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					textOrientation = "0"; //$NON-NLS-1$
					break;
				}
			}
		});

		new Label(orientationGroup, SWT.SEPARATOR | SWT.VERTICAL);

		buttonVertical = new Button(orientationGroup, SWT.RADIO);
		buttonVertical.setText(Messages.TemplatePreferences_TextOrientation_Vertical);
		buttonVertical.setImage(ResourceProvider.loadImage(ResourceProvider.IMAGE_ELLIPSIS_V_PATH));
		buttonVertical.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					textOrientation = "90"; //$NON-NLS-1$
					break;
				}
			}
		});

		xslCustomFlag = new Button(composite, SWT.CHECK);
		xslCustomFlag.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
		xslCustomFlag.setText(Messages.TemplatePreferences_XslSetting);
		xslCustomFlag.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WindowUtil.handleXslCustomEvent(xslCustomFlag, xslCustomFileDialog, xslTemplate);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		xslTemplate = new Text(composite, SWT.BORDER);
		xslTemplate.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));

		xslCustomFileDialog = new Button(composite, SWT.PUSH);
		xslCustomFileDialog.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		xslCustomFileDialog.setText(Messages.TemplatePreferences_XslFileChooser);
		xslCustomFileDialog.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
				dialog.setFilterNames(new String[] { Messages.TemplatePreferences_XslFileChooser_XslFilter,
						Messages.TemplatePreferences_XslFileChooser_AllFilesFilter });
				dialog.setFilterExtensions(new String[] { "*.xsl", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
				String xslPath = dialog.open();
				if (xslPath != null) {
					xslTemplate.setText(xslPath);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		pageCustomCheckBox = new Button(composite, SWT.CHECK);
		pageCustomCheckBox.setLayoutData(SWTHelper.getFillGridData(4, true, 1, false));
		pageCustomCheckBox.setText(Messages.TemplatePreferences_PaperFormat_CheckBox);
		pageCustomCheckBox.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				WindowUtil.checkBoxEvent(pageCustomCheckBox, templateArea, pageCustomArea, pageTemplateName.getText());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		templateArea = new Composite(composite, SWT.NONE);
		templateArea.setLayoutData(SWTHelper.getFillGridData(4, true, 1, false));
		templateArea.setLayout(new GridLayout(4, false));
		new Label(templateArea, SWT.NONE).setText(Messages.TemplatePreferences_PaperFormat_Label);

		pageTemplateName = new Combo(templateArea, SWT.READ_ONLY);
		pageTemplateName.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
		pageTemplateName.setItems(ResourceProvider.getPaperFormats());
		pageTemplateName.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				WindowUtil.updateCustomPaperFields(pageTemplateName.getText(), pageCustomArea);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// Auto-generated method stub
			}
		});

		pageCustomArea = new Composite(composite, SWT.NONE);
		pageCustomArea.setLayoutData(SWTHelper.getFillGridData(4, true, 1, false));
		pageCustomArea.setLayout(new GridLayout(4, false));

		new Label(pageCustomArea, SWT.NONE).setText(Messages.TemplatePreferences_Page_Width);
		pageWidth = new Text(pageCustomArea, SWT.BORDER);
		pageWidth.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		new Label(pageCustomArea, SWT.NONE).setText(Messages.TemplatePreferences_Page_Height);
		pageHeight = new Text(pageCustomArea, SWT.BORDER);
		pageHeight.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		new Label(pageCustomArea, SWT.NONE).setText(Messages.TemplatePreferences_Page_MarginTop);
		pageMarginTop = new Text(pageCustomArea, SWT.BORDER);
		pageMarginTop.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		new Label(pageCustomArea, SWT.NONE).setText(Messages.TemplatePreferences_Page_MarginBottom);
		pageMarginBottom = new Text(pageCustomArea, SWT.BORDER);
		pageMarginBottom.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		new Label(pageCustomArea, SWT.NONE).setText(Messages.TemplatePreferences_Page_MarginLeft);
		pageMarginLeft = new Text(pageCustomArea, SWT.BORDER);
		pageMarginLeft.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		new Label(pageCustomArea, SWT.NONE).setText(Messages.TemplatePreferences_Page_MarginRight);
		pageMarginRight = new Text(pageCustomArea, SWT.BORDER);
		pageMarginRight.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		if (docName.equals(PreferenceConstants.MEDICATION_LABEL)) {
			new Label(composite, SWT.NONE).setText(Messages.TemplatePreferences_ResponsiblePharmacist);
			responsiblePharmacist = new Text(composite, SWT.BORDER);
			responsiblePharmacist.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
		}

		initializeValues();

		return composite;
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.getDocumentName(selectionIndex) + StringUtils.SPACE + Messages.TemplatePreferences_Title);
		setMessage(Messages.TemplatePreferences_Description);
		getShell().setText(
				Messages.getDocumentName(selectionIndex) + StringUtils.SPACE + Messages.TemplatePreferences_Title);
	}

	@Override
	protected void okPressed() {
		if (settingsStoreCombo.getSelectionIndex() == 0) {
			boolean answer = SWTHelper.askYesNo("Globale Einstellungen",
					"Wollen Sie die globalen Einstellungen überschreiben?");
			if (!answer) {
				System.out.println("Einstellungen nicht gespeichert!!!!"); //$NON-NLS-1$
				return;
			}
		}

		settingsStore.setValue(PreferenceConstants.getDocPreferenceConstant(docName, 0), printerName.getText());
		settingsStore.setValue(PreferenceConstants.getDocPreferenceConstant(docName, 1), xslTemplate.getText());
		settingsStore.setValue(PreferenceConstants.getDocPreferenceConstant(docName, 2), xslCustomFlag.getSelection());
		settingsStore.setValue(PreferenceConstants.getDocPreferenceConstant(docName, 3), pageTemplateName.getText());
		settingsStore.setValue(PreferenceConstants.getDocPreferenceConstant(docName, 4),
				pageCustomCheckBox.getSelection());
		settingsStore.setValue(PreferenceConstants.getDocPreferenceConstant(docName, 5), pageHeight.getText());
		settingsStore.setValue(PreferenceConstants.getDocPreferenceConstant(docName, 6), pageWidth.getText());
		settingsStore.setValue(PreferenceConstants.getDocPreferenceConstant(docName, 7), textOrientation);
		settingsStore.setValue(PreferenceConstants.getDocPreferenceConstant(docName, 8), pageMarginTop.getText());
		settingsStore.setValue(PreferenceConstants.getDocPreferenceConstant(docName, 9), pageMarginBottom.getText());
		settingsStore.setValue(PreferenceConstants.getDocPreferenceConstant(docName, 10), pageMarginLeft.getText());
		settingsStore.setValue(PreferenceConstants.getDocPreferenceConstant(docName, 11), pageMarginRight.getText());
		if (docName.equals(PreferenceConstants.MEDICATION_LABEL)) {
			settingsStore.setValue(PreferenceConstants.getDocPreferenceConstant(docName, 13),
					responsiblePharmacist.getText());
		}

		super.okPressed();
	}

	private void initializeValues() {
		settingsStoreCombo.select(WindowUtil.setComboSelection(settingsStoreCombo,
				new String[] { Messages.TemplatePreferences_SettingsStore_Global,
						Messages.TemplatePreferences_SettingsStore_Local },
				CoreHub.localCfg.get(PreferenceConstants.getDocPreferenceConstant(docName, 12), true)));

		updateValues();
	}

	private void updateValues() {
		printerName.select(WindowUtil.setComboSelection(printerName, PrinterProvider.getAvailablePrinters(),
				settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 0))));
		xslCustomFlag.setSelection(settingsStore.getBoolean(PreferenceConstants.getDocPreferenceConstant(docName, 2)));
		xslTemplate.setText(settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 1)));
		pageCustomCheckBox
				.setSelection(settingsStore.getBoolean(PreferenceConstants.getDocPreferenceConstant(docName, 4)));
		pageTemplateName.select(WindowUtil.setComboSelection(pageTemplateName, ResourceProvider.getPaperFormats(),
				settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 3))));
		pageWidth.setText(settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 6)));
		pageHeight.setText(settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 5)));
		pageMarginTop.setText(settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 8)));
		pageMarginBottom.setText(settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 9)));
		pageMarginLeft.setText(settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 10)));
		pageMarginRight.setText(settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 11)));

		if (settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 7)).equals("90")) { //$NON-NLS-1$
			buttonVertical.setSelection(true);
		} else {
			buttonHorizontal.setSelection(true);
		}

		if (docName.equals(PreferenceConstants.MEDICATION_LABEL)) {
			responsiblePharmacist
					.setText(settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 13)));
		}

		WindowUtil.checkBoxEvent(pageCustomCheckBox, templateArea, pageCustomArea, pageTemplateName.getText());
		WindowUtil.handleXslCustomEvent(xslCustomFlag, xslCustomFileDialog, xslTemplate);
	}

	private static final class WindowUtil {

		public static void checkBoxEvent(Button checkBox, Composite areaTemplate, Composite areaCustom,
				String paperFormatName) {
			if (checkBox.getSelection()) {
				setControlStatus(areaCustom, true);
				setControlStatus(areaTemplate, false);
			} else {
				setControlStatus(areaCustom, false);
				setControlStatus(areaTemplate, true);
				updateCustomPaperFields(paperFormatName, areaCustom);
			}
		}

		public static void handleXslCustomEvent(Button checkBox, Button xslCustomFileDialog, Text xslTemplate) {
			if (checkBox.getSelection()) {
				xslTemplate.setEnabled(true);
				xslCustomFileDialog.setEnabled(true);
			} else {
				xslTemplate.setEnabled(false);
				xslTemplate.setText(StringUtils.EMPTY);
				xslCustomFileDialog.setEnabled(false);
			}
		}

		/**
		 * Disables Combo Widgets and Text Widgets inside a Composite.
		 *
		 * @param ctrl    The corresponding Composite container
		 * @param enabled The status of the Widgets, true = enabled, false = disabled
		 */
		public static void setControlStatus(Control ctrl, boolean enabled) {
			if (ctrl instanceof Combo) {
				ctrl.setEnabled(enabled);
			}

			if (ctrl instanceof Composite) {
				Composite comp = (Composite) ctrl;
				for (Control c : comp.getChildren())
					setControlStatus(c, enabled);
			} else if (ctrl instanceof Text) {
				ctrl.setEnabled(enabled);
			}
		}

		public static int setComboSelection(Combo combo, String[] items, String item) {
			for (int i = 0; i < items.length; i++) {
				if (items[i].equals(item))
					return i;
			}
			return 0;
		}

		public static int setComboSelection(Combo combo, String[] items, boolean item) {
			if (item) {
				return 0;
			} else {
				return 1;
			}
		}

		public static void updateCustomPaperFields(String paperFormatName, Composite customPaperFieldsArea) {
			List<String> values = ResourceProvider.getPaperFormatValues(paperFormatName);
			Iterator<String> iterator = values.iterator();

			for (Control c : customPaperFieldsArea.getChildren()) {
				if (c instanceof Text) {
					Text t = (Text) c;
					t.setText(iterator.next());
				}
			}
		}
	}
}