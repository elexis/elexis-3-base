/*******************************************************************************
 * Copyright 2024 Framsteg GmbH / olivier.debenath@framsteg.ch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ch.framsteg.elexis.covercard.config;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

import ch.elexis.core.services.IConfigService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.data.Xid;
import jakarta.inject.Inject;

public class CovercardPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final String PLUGIN_TITLE = "HIN Card Plugin (Framsteg)";
	private static final String PLUGIN_DESCRIPTION = "HIN Card Plugin (Public License)";

	private static final String CARD_READER_TYPE = "label.covercard.prefs.cardreader.type.group";
	private static final String MAGNET_READER_LABEL = "label.covercard.prefs.cardreader.type.magnet";
	private static final String CHIP_READER_LABEL = "label.covercard.prefs.cardreader.type.chip";
	private static final String CARD_READER_CORRECTION = "label.covercard.prefs.cardreader.input.group";
	private static final String CARD_READER_REGEX = "label.covercard.prefs.cardreader.input.regex";
	private static final String HIN_CONFIGURATION = "label.covercard.prefs.hin.group";
	private static final String HIN_OAUTH_CLIENT_NAME = "label.covercard.prefs.hin.oauth.name";
	private static final String HIN_OAUTH_CLIENT_ID = "label.covercard.prefs.hin.oauth.id";
	private static final String HIN_OAUTH_TOKEN_GROUP = "label.covercard.prefs.hin.oauth.token.group";
	private static final String OFAC_CONFIGURATION = "label.covercard.prefs.ofac.group";
	private static final String OFAC_URL = "label.covercard.prefs.ofac.url";

	private static final String INSURED_NUMBER = "domain.covercard.insured.number";
	private static final String CARD_NUMBER = "domain.covercard.card.number";
	private static final String INSURED_PERSON_NUMBER = "domain.covercard.insured.person.number";
	private static final String LBL_INSURED_NUMBER = "label.covercard.insured.number";
	private static final String LBL_CARD_NUMBER = "label.covercard.card.number";
	private static final String LBL_INSURED_PERSON_NUMBER = "label.covercard.insured.person.number";
	private static final String KEY_URL = "key.url";
	private static final String KEY_XML_PARAMETER = "key.parameter.xml";
	private static final String KEY_OAUTH_NAME = "key.proxy.oauth.client.name";
	private static final String KEY_OAUTH_ID = "key.proxy.oauth.client.id";
	private static final String KEY_TOKEN_GROUP = "key.proxy.oauth.client.token.group";
	private static final String KEY_INSURED_NUMBER = "key.covercard.insured.number";
	private static final String KEY_CARD_NUMBER = "key.covercard.card.number";
	private static final String KEY_CARD_TYPE = "card.type";
	private static final String KEY_CARD_TYPE_MAGNET = "card.type.magnet";
	private static final String KEY_CARD_TYPE_CHIP = "card.type.chip";
	private static final String KEY_INSURED_PERSON_NUMBER = "key.covercard.insured.person.number";
	private static final String HIN_URL = "hin.url";
	private static final String HIN_XML_PARAMETER = "hin.xml.parameter";
	private static final String REGEX_PATTERN = "cardreader.regex.pattern";
	private static final String KEY_REGEX_PATTERN = "key.cardreader.regex.pattern";
	private String cardType = "";

	private Properties applicationProperties;
	private Properties messagesProperties;

	@Inject
	private IConfigService configService;

	private Text txtUrl;
	private Text txtMrParameter;
	private Text txtHinOauthName;
	private Text txtHinOauthClientID;
	private Text txtHinOauthTokenGroup;
	private Text txtInsuredNumber;
	private Text txtCardNumber;
	private Text txtInsuredPersonNumber;
	private Text txtRegex;
	private Button btnMagnet;
	private Button btnChip;

	public CovercardPreferencePage() {
		super(PLUGIN_TITLE);
		setTitle(PLUGIN_DESCRIPTION);
		loadProperties();
		CoreUiUtil.injectServices(this);
	}

	@Override
	public void init(IWorkbench arg0) {

	}

	private void loadProperties() {

		try {
			setApplicationProperties(new Properties());
			setMessagesProperties(new Properties());

			getApplicationProperties().load(CovercardPreferencePage.class.getClassLoader()
					.getResourceAsStream("/resources/application.properties"));
			getMessagesProperties().load(CovercardPreferencePage.class.getClassLoader()
					.getResourceAsStream("/resources/messages_de.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Control createContents(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		GridLayout cardGroupLayout = new GridLayout();
		cardGroupLayout.numColumns = 1;

		GridData cardGroupGridData = new GridData();
		cardGroupGridData.grabExcessHorizontalSpace = true;
		cardGroupGridData.horizontalAlignment = SWT.FILL;
		cardGroupGridData.verticalAlignment = SWT.FILL;
		cardGroupGridData.grabExcessVerticalSpace = true;

		Group cardTypeGroup = new Group(composite, SWT.BORDER);
		cardTypeGroup.setText(getMessagesProperties().getProperty(CARD_READER_TYPE));
		cardTypeGroup.setLayout(cardGroupLayout);
		cardTypeGroup.setLayoutData(cardGroupGridData);

		btnMagnet = new Button(cardTypeGroup, SWT.RADIO);
		btnMagnet.setText(getMessagesProperties().getProperty(MAGNET_READER_LABEL));
		btnMagnet.setEnabled(true);

		if (configService.get(getApplicationProperties().getProperty(KEY_CARD_TYPE), "")
				.equalsIgnoreCase(getApplicationProperties().getProperty(KEY_CARD_TYPE_MAGNET))) {
			btnMagnet.setSelection(true);
			cardType = getApplicationProperties().getProperty(KEY_CARD_TYPE_MAGNET);
		}

		btnMagnet.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cardType = getApplicationProperties().getProperty(KEY_CARD_TYPE_MAGNET);
			}
		});

		btnChip = new Button(cardTypeGroup, SWT.RADIO);
		btnChip.setText(getMessagesProperties().getProperty(CHIP_READER_LABEL));
		btnChip.setEnabled(true);

		if (configService.get(getApplicationProperties().getProperty(KEY_CARD_TYPE), "")
				.equalsIgnoreCase(getApplicationProperties().getProperty(KEY_CARD_TYPE_CHIP))) {
			btnChip.setSelection(true);
			cardType = getApplicationProperties().getProperty(KEY_CARD_TYPE_CHIP);
		}

		btnChip.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cardType = getApplicationProperties().getProperty(KEY_CARD_TYPE_CHIP);
			}
		});

		GridLayout cardConfigGroupLayout = new GridLayout();
		cardConfigGroupLayout.numColumns = 2;

		GridData cardConfigGroupGridData = new GridData();
		cardConfigGroupGridData.grabExcessHorizontalSpace = true;
		cardConfigGroupGridData.horizontalAlignment = SWT.FILL;
		cardConfigGroupGridData.verticalAlignment = SWT.FILL;
		cardConfigGroupGridData.grabExcessVerticalSpace = true;

		Group cardConfigGroup = new Group(composite, SWT.BORDER);
		cardConfigGroup.setText(getMessagesProperties().getProperty(CARD_READER_CORRECTION));
		cardConfigGroup.setLayout(cardConfigGroupLayout);
		cardConfigGroup.setLayoutData(cardConfigGroupGridData);

		Label lblRegex = new Label(cardConfigGroup, SWT.NONE);
		lblRegex.setText(getMessagesProperties().getProperty(CARD_READER_REGEX));

		txtRegex = new Text(cardConfigGroup, SWT.BORDER);

		if (!configService.get(getApplicationProperties().getProperty(KEY_REGEX_PATTERN), "").isEmpty()) {
			txtRegex.setText(configService.get(getApplicationProperties().getProperty(KEY_REGEX_PATTERN), ""));
		} else {
			txtRegex.setText(getApplicationProperties().getProperty(REGEX_PATTERN));
		}

		txtRegex.setEnabled(true);
		txtRegex.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout hinGroupLayout = new GridLayout();
		hinGroupLayout.numColumns = 2;

		GridData hinGroupGridData = new GridData();
		hinGroupGridData.grabExcessHorizontalSpace = true;
		hinGroupGridData.horizontalAlignment = SWT.FILL;
		hinGroupGridData.verticalAlignment = SWT.FILL;
		hinGroupGridData.grabExcessVerticalSpace = true;

		Group hinGroup = new Group(composite, SWT.BORDER);
		hinGroup.setText(getMessagesProperties().getProperty(HIN_CONFIGURATION));
		hinGroup.setLayout(hinGroupLayout);
		hinGroup.setLayoutData(hinGroupGridData);

		Label lblHinOauthName = new Label(hinGroup, SWT.NONE);
		lblHinOauthName.setText(getMessagesProperties().getProperty(HIN_OAUTH_CLIENT_NAME));

		txtHinOauthName = new Text(hinGroup, SWT.BORDER);
		txtHinOauthName.setEnabled(false);
		if (!configService.get(getApplicationProperties().getProperty(KEY_OAUTH_NAME), "").isEmpty()) {
			txtHinOauthName.setText(configService.get(getApplicationProperties().getProperty(KEY_OAUTH_NAME), ""));
		} else {
			txtHinOauthName.setText(getApplicationProperties().getProperty(KEY_OAUTH_NAME));
		}
		txtHinOauthName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lblHinOauthId = new Label(hinGroup, SWT.NONE);
		lblHinOauthId.setText(getMessagesProperties().getProperty(HIN_OAUTH_CLIENT_ID));

		txtHinOauthClientID = new Text(hinGroup, SWT.BORDER);
		txtHinOauthClientID.setEnabled(false);
		if (!configService.get(getApplicationProperties().getProperty(KEY_OAUTH_ID), "").isEmpty()) {
			txtHinOauthClientID.setText(configService.get(getApplicationProperties().getProperty(KEY_OAUTH_ID), ""));
		} else {
			txtHinOauthClientID.setText(getApplicationProperties().getProperty(KEY_OAUTH_ID));
		}
		txtHinOauthClientID.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lblHinTokenGroud = new Label(hinGroup, SWT.NONE);
		lblHinTokenGroud.setText(getMessagesProperties().getProperty(HIN_OAUTH_TOKEN_GROUP));

		txtHinOauthTokenGroup = new Text(hinGroup, SWT.BORDER);
		txtHinOauthTokenGroup.setEnabled(false);
		if (!configService.get(getApplicationProperties().getProperty(KEY_TOKEN_GROUP), "").isEmpty()) {
			txtHinOauthTokenGroup
					.setText(configService.get(getApplicationProperties().getProperty(KEY_TOKEN_GROUP), ""));
		} else {
			txtHinOauthTokenGroup.setText(getApplicationProperties().getProperty(KEY_TOKEN_GROUP));
		}
		txtHinOauthTokenGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout ofacGroupLayout = new GridLayout();
		ofacGroupLayout.numColumns = 2;

		GridData ofacGroupGridData = new GridData();
		ofacGroupGridData.grabExcessHorizontalSpace = true;
		ofacGroupGridData.horizontalAlignment = SWT.FILL;
		ofacGroupGridData.verticalAlignment = SWT.FILL;
		ofacGroupGridData.grabExcessVerticalSpace = true;

		Group ofacGroup = new Group(composite, SWT.BORDER);
		ofacGroup.setText(getMessagesProperties().getProperty(OFAC_CONFIGURATION));
		ofacGroup.setLayout(ofacGroupLayout);
		ofacGroup.setLayoutData(ofacGroupGridData);

		Label lblUrl = new Label(ofacGroup, SWT.NONE);
		lblUrl.setText(getMessagesProperties().getProperty(OFAC_URL));

		txtUrl = new Text(ofacGroup, SWT.BORDER);
		txtUrl.setEnabled(true);
		if (!configService.get(getApplicationProperties().getProperty(KEY_URL), "").isEmpty()) {
			txtUrl.setText(configService.get(getApplicationProperties().getProperty(KEY_URL), ""));
		} else {
			txtUrl.setText(getApplicationProperties().getProperty(HIN_URL));
		}

		txtUrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label lblMrParameter = new Label(ofacGroup, SWT.NONE);
		lblMrParameter.setText("XML Parameter (XXX):");

		txtMrParameter = new Text(ofacGroup, SWT.BORDER);
		txtMrParameter.setEnabled(true);
		if (!configService.get(getApplicationProperties().getProperty(KEY_XML_PARAMETER), "").isEmpty()) {
			txtMrParameter.setText(configService.get(getApplicationProperties().getProperty(KEY_XML_PARAMETER), ""));
		} else {
			txtMrParameter.setText(getApplicationProperties().getProperty(HIN_XML_PARAMETER));
		}
		txtMrParameter.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout metadataGroupLayout = new GridLayout();
		metadataGroupLayout.numColumns = 2;

		GridData metadataGroupGridData = new GridData();
		metadataGroupGridData.grabExcessHorizontalSpace = true;
		metadataGroupGridData.horizontalAlignment = SWT.FILL;
		metadataGroupGridData.verticalAlignment = SWT.FILL;
		metadataGroupGridData.grabExcessVerticalSpace = true;

		Group metadataGroup = new Group(composite, SWT.BORDER);
		metadataGroup.setText("Metadaten Domänen");
		metadataGroup.setLayout(metadataGroupLayout);
		metadataGroup.setLayoutData(metadataGroupGridData);

		Label lblInsuredNumber = new Label(metadataGroup, SWT.NONE);
		lblInsuredNumber.setText(getMessagesProperties().getProperty(LBL_INSURED_NUMBER));

		txtInsuredNumber = new Text(metadataGroup, SWT.BORDER);
		txtInsuredNumber.setEnabled(false);
		txtInsuredNumber.setText(getApplicationProperties().getProperty(INSURED_NUMBER));
		txtInsuredNumber.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lblCardNumber = new Label(metadataGroup, SWT.NONE);
		lblCardNumber.setText(getMessagesProperties().getProperty(LBL_CARD_NUMBER));

		txtCardNumber = new Text(metadataGroup, SWT.BORDER);
		txtCardNumber.setEnabled(false);
		txtCardNumber.setText(getApplicationProperties().getProperty(CARD_NUMBER));
		txtCardNumber.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lblInsuredPersonNumber = new Label(metadataGroup, SWT.NONE);
		lblInsuredPersonNumber.setText(getMessagesProperties().getProperty(LBL_INSURED_PERSON_NUMBER));

		txtInsuredPersonNumber = new Text(metadataGroup, SWT.BORDER);
		txtInsuredPersonNumber.setEnabled(false);
		txtInsuredPersonNumber.setText(getApplicationProperties().getProperty(INSURED_PERSON_NUMBER));
		txtInsuredPersonNumber.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button metadataButton = new Button(metadataGroup, SWT.PUSH);
		metadataButton.setText("Schema anpassen");

		metadataButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Xid.localRegisterXIDDomainIfNotExists("www.xid.ch/framsteg/covercard/insured-number",
						"Versicherten Nr.", Xid.ASSIGNMENT_LOCAL);
				Xid.localRegisterXIDDomainIfNotExists("www.xid.ch/framsteg/covercard/card-number", "Covercard Nr.",
						Xid.ASSIGNMENT_LOCAL);
				Xid.localRegisterXIDDomainIfNotExists("www.xid.ch/framsteg/covercard/insured-person-number",
						"ID-Karten Nr.", Xid.ASSIGNMENT_GLOBAL);
			}
		});

		return composite;
	}

	public boolean performOk() {
		configService.set(getApplicationProperties().getProperty(KEY_CARD_TYPE), cardType);
		configService.set(getApplicationProperties().getProperty(KEY_URL), txtUrl.getText());
		configService.set(getApplicationProperties().getProperty(KEY_XML_PARAMETER), txtMrParameter.getText());
		configService.set(getApplicationProperties().getProperty(KEY_OAUTH_NAME), txtHinOauthName.getText());
		configService.set(getApplicationProperties().getProperty(KEY_OAUTH_ID), txtHinOauthClientID.getText());
		configService.set(getApplicationProperties().getProperty(KEY_TOKEN_GROUP), txtHinOauthTokenGroup.getText());
		configService.set(getApplicationProperties().getProperty(KEY_INSURED_NUMBER), txtInsuredNumber.getText());
		configService.set(getApplicationProperties().getProperty(KEY_CARD_NUMBER), txtCardNumber.getText());
		configService.set(getApplicationProperties().getProperty(KEY_INSURED_PERSON_NUMBER),
				txtInsuredPersonNumber.getText());
		configService.set(getApplicationProperties().getProperty(KEY_REGEX_PATTERN), txtRegex.getText());
		return super.performOk();
	}

	public Properties getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	public Properties getMessagesProperties() {
		return messagesProperties;
	}

	public void setMessagesProperties(Properties messagesProperties) {
		this.messagesProperties = messagesProperties;
	}
}
