/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.ui;

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import at.medevit.medelexis.text.msword.Activator;
import at.medevit.medelexis.text.msword.Messages;
import at.medevit.medelexis.text.msword.plugin.util.DocumentConversion;
import ch.elexis.core.ui.util.SWTHelper;

public class MissingConversionDialog extends TitleAreaDialog {

	private String officeHomePath;
	private Text tOfficeHome;

	public MissingConversionDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		new Label(ret, SWT.NONE).setText(Messages.MissingConversionDialog_OfficeHome);
		tOfficeHome = new Text(ret, SWT.BORDER | SWT.SINGLE);
		tOfficeHome.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tOfficeHome.setText(
				Activator.getDefault().getPreferenceStore().getString(DocumentConversion.PREFERENCE_OFFICE_HOME));
		return ret;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText("Office Home Verzeichnis"); //$NON-NLS-1$
		setTitle(Messages.MissingConversionDialog_OfficeHomeConfig);
		setMessage(Messages.MissingConversionDialog_OfficeHomeMessage);
	}

	@Override
	protected void okPressed() {
		DefaultOfficeManagerConfiguration config = new DefaultOfficeManagerConfiguration();
		try {
			config.setOfficeHome(tOfficeHome.getText());
		} catch (IllegalArgumentException iae) {
			// office home not valid
			setErrorMessage(tOfficeHome.getText() + Messages.MissingConversionDialog_OfficeHomeNoValidPath);
			return;
		}
		officeHomePath = tOfficeHome.getText();
		super.okPressed();
	}

	public String getOfficeHomePath() {
		return officeHomePath;
	}
}
