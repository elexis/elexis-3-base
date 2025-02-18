/*******************************************************************************
 * Copyright (c) 2014, Pharmed Solutions GmbH
 * All rights reserved.
 *******************************************************************************/

package ch.pharmed.phmprescriber;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
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

import ch.elexis.core.services.holder.ConfigServiceHolder;

public class PhMPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Physician ph;

	private Text textboxZSRid;
	private Text textboxTitle;
	private Text textboxFirstname;
	private Text textboxLastname;
	private Text textboxStreet;
	private Text textboxPostbox;
	private Text textboxZip;
	private Text textboxCity;
	private Text textboxPhone;
	private Text textboxFax;
	private Text textboxSpecialty1;
	private Text textboxSpecialty2;
	private Text textboxGLNid;

	private Composite compAddress;
	private Composite compInteraction;
	private Button btngetAddress;
	private Button btnCboInteraction;

	private ResourceBundle messages;

	/**
	 * Create the preference page.
	 */
	public PhMPreferencePage() {

		String language = "de";
		String country = "CH";

		Locale currentLocale;

		currentLocale = new Locale(language, country);

		messages = ResourceBundle.getBundle("ch.pharmed.phmprescriber.MessagesBundle", currentLocale);

	}

	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {

		// Initialize the preference page

		// Create new physician
		ph = new Physician();

	}

	/**
	 * Create contents of the preference page.
	 *
	 * @param parent
	 */
	@Override
	public Control createContents(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(1, false));

		// (1) Container for ZSR
		Group zsrgroup = new Group(container, SWT.None);
		zsrgroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		zsrgroup.setLayout(new GridLayout(2, false));
		zsrgroup.setText(messages.getString("PhMPreferencePage_0"));

		Label lblZSR = new Label(zsrgroup, SWT.NONE);
		lblZSR.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblZSR.setText(messages.getString("PhMPreferencePage_1"));

		textboxZSRid = new Text(zsrgroup, SWT.BORDER);
		textboxZSRid.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		textboxZSRid.setMessage(messages.getString("PhMPreferencePage_2"));

		// (2) Button for requesting the address
		compAddress = new Composite(container, SWT.NONE);
		compAddress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compAddress.setLayout(new GridLayout(3, false));

		SelectionListener AddressSL = new AddressSelectionButtonListener();

		btngetAddress = new Button(compAddress, SWT.PUSH);
		btngetAddress.setText(messages.getString("PhMPreferencePage_3"));
		btngetAddress.addSelectionListener(AddressSL);

		// (3) Container with all fields for the address
		Group addressgroup = new Group(container, SWT.None);
		addressgroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		addressgroup.setLayout(new GridLayout(4, false));
		addressgroup.setText(messages.getString("PhMPreferencePage_4"));

		Label lblAnrede = new Label(addressgroup, SWT.NONE);
		lblAnrede.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAnrede.setText(messages.getString("PhMPreferencePage_5"));

		textboxTitle = new Text(addressgroup, SWT.BORDER);
		textboxTitle.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		textboxTitle.setMessage(messages.getString("PhMPreferencePage_6"));

		Label lblFirstname = new Label(addressgroup, SWT.NONE);
		lblFirstname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFirstname.setText(messages.getString("PhMPreferencePage_7"));

		textboxFirstname = new Text(addressgroup, SWT.BORDER);
		textboxFirstname.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label lblLastname = new Label(addressgroup, SWT.NONE);
		lblLastname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLastname.setText(messages.getString("PhMPreferencePage_8"));

		textboxLastname = new Text(addressgroup, SWT.BORDER);
		textboxLastname.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label lblStreet = new Label(addressgroup, SWT.NONE);
		lblStreet.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStreet.setText(messages.getString("PhMPreferencePage_9"));

		textboxStreet = new Text(addressgroup, SWT.BORDER);
		textboxStreet.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label lblPobo = new Label(addressgroup, SWT.NONE);
		lblPobo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPobo.setText(messages.getString("PhMPreferencePage_10"));

		textboxPostbox = new Text(addressgroup, SWT.BORDER);
		textboxPostbox.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label lblZip = new Label(addressgroup, SWT.NONE);
		lblZip.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblZip.setText(messages.getString("PhMPreferencePage_11"));

		textboxZip = new Text(addressgroup, SWT.BORDER);
		textboxZip.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label lblCity = new Label(addressgroup, SWT.NONE);
		lblCity.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCity.setText(messages.getString("PhMPreferencePage_12"));

		textboxCity = new Text(addressgroup, SWT.BORDER);
		textboxCity.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label lblPhone = new Label(addressgroup, SWT.NONE);
		lblPhone.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPhone.setText(messages.getString("PhMPreferencePage_13"));

		textboxPhone = new Text(addressgroup, SWT.BORDER);
		textboxPhone.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label lblFax = new Label(addressgroup, SWT.NONE);
		lblFax.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFax.setText(messages.getString("PhMPreferencePage_14"));

		textboxFax = new Text(addressgroup, SWT.BORDER);
		textboxFax.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label lblGLNid = new Label(addressgroup, SWT.NONE);
		lblGLNid.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGLNid.setText(messages.getString("PhMPreferencePage_15"));

		textboxGLNid = new Text(addressgroup, SWT.BORDER);
		textboxGLNid.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label lblSpecialty1 = new Label(addressgroup, SWT.NONE);
		lblSpecialty1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSpecialty1.setText(messages.getString("PhMPreferencePage_16"));

		textboxSpecialty1 = new Text(addressgroup, SWT.BORDER);
		textboxSpecialty1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label lblSpecialty2 = new Label(addressgroup, SWT.NONE);
		lblSpecialty2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSpecialty2.setText(messages.getString("PhMPreferencePage_17"));

		textboxSpecialty2 = new Text(addressgroup, SWT.BORDER);
		textboxSpecialty2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		// (4) Button for enabling automatic DD-interaction check
		compInteraction = new Composite(container, SWT.NONE);
		compInteraction.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compInteraction.setLayout(new GridLayout(3, false));

		SelectionListener interactionSL = new InteractionSelectionButtonListener();

		btnCboInteraction = new Button(compInteraction, SWT.CHECK);
		btnCboInteraction.setData(Constants.CFG_INTERATCIONS);
		btnCboInteraction.setText(messages.getString("PhMPreferencePage_18"));
		btnCboInteraction.addSelectionListener(interactionSL);

		String interactionsEnabled = ConfigServiceHolder.getGlobal(Constants.CFG_INTERATCIONS,
				Constants.CFG_INTERATCIONS);
		if (interactionsEnabled.equals("true")) { //$NON-NLS-1$
			btnCboInteraction.setSelection(true);
		}

		bindData();

		return container;
	}

	/**
	 * Bind the objects properties to the particular text boxes
	 */
	protected void bindData() {

		textboxZSRid.setText(ph.getZsrid());

		textboxTitle.setText(ph.getTitle());

		textboxFirstname.setText(ph.getFirstname());
		textboxLastname.setText(ph.getLastname());

		textboxStreet.setText(ph.getStreet());
		textboxPostbox.setText(ph.getPostbox());
		textboxZip.setText(ph.getZip());
		textboxCity.setText(ph.getCity());

		textboxPhone.setText(ph.getPhone());
		textboxFax.setText(ph.getFax());

		textboxSpecialty1.setText(ph.getSpecialty1());
		textboxSpecialty2.setText(ph.getSpecialty2());

		textboxGLNid.setText(ph.getGlnid());

	}

	// --- HANDLERS ---

	private class AddressSelectionButtonListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {

			String zsrid = textboxZSRid.getText();

			// Get the data
			ph.getAttributesFromWeb(zsrid);

			// Assign the data
			bindData();

		}
	}

	private class InteractionSelectionButtonListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {

			String interactionEnabled = "true"; //$NON-NLS-1$

			if (!((Button) e.widget).getSelection())
				interactionEnabled = "false"; //$NON-NLS-1$

			// Store the value in the preferences

			ConfigServiceHolder.setGlobal(Constants.CFG_INTERATCIONS, (String) interactionEnabled);

		}
	}

	@Override
	public boolean performOk() {
		performApply();
		return super.performOk();
	}

	@Override
	protected void performApply() {

		String strCfg = this.createCFGString();

		ConfigServiceHolder.setGlobal(Constants.CFG_PHM_PHY, strCfg.toString());
	}

	// --- Some Utils ---
	/**
	 * Create the config-string to store
	 */
	private String createCFGString() {

		String returnValue = StringUtils.EMPTY;

		returnValue += textboxZSRid.getText().replace(";", StringUtils.EMPTY) + ";"; //$NON-NLS-1$ //$NON-NLS-2$
		returnValue += textboxGLNid.getText().replace(";", StringUtils.EMPTY) + ";"; //$NON-NLS-1$ //$NON-NLS-2$

		returnValue += textboxTitle.getText().replace(";", StringUtils.EMPTY) + ";"; //$NON-NLS-1$ //$NON-NLS-2$
		returnValue += textboxFirstname.getText().replace(";", StringUtils.EMPTY) + ";"; //$NON-NLS-1$ //$NON-NLS-2$
		returnValue += textboxLastname.getText().replace(";", StringUtils.EMPTY) + ";"; //$NON-NLS-1$ //$NON-NLS-2$

		returnValue += textboxStreet.getText().replace(";", StringUtils.EMPTY) + ";"; //$NON-NLS-1$ //$NON-NLS-2$
		returnValue += textboxPostbox.getText().replace(";", StringUtils.EMPTY) + ";"; //$NON-NLS-1$ //$NON-NLS-2$
		returnValue += textboxZip.getText().replace(";", StringUtils.EMPTY) + ";"; //$NON-NLS-1$ //$NON-NLS-2$
		returnValue += textboxCity.getText().replace(";", StringUtils.EMPTY) + ";"; //$NON-NLS-1$ //$NON-NLS-2$

		returnValue += textboxPhone.getText().replace(";", StringUtils.EMPTY) + ";"; //$NON-NLS-1$ //$NON-NLS-2$
		returnValue += textboxFax.getText().replace(";", StringUtils.EMPTY) + ";"; //$NON-NLS-1$ //$NON-NLS-2$

		returnValue += textboxSpecialty1.getText().replace(";", StringUtils.EMPTY) + ";"; //$NON-NLS-1$ //$NON-NLS-2$
		returnValue += textboxSpecialty2.getText().replace(";", StringUtils.EMPTY); //$NON-NLS-1$

		return returnValue;

	}

}
