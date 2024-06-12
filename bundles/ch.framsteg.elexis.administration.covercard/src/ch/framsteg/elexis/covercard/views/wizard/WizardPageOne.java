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
package ch.framsteg.elexis.covercard.views.wizard;

import java.util.Properties;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.framsteg.elexis.covercard.dao.CardInfoData;
import ch.framsteg.elexis.covercard.dao.PatientInfoData;
import ch.framsteg.elexis.covercard.views.dialogs.CardInfoDialog;

public class WizardPageOne extends WizardPage {

	private static final String PATIENT_GROUP_TITLE = "wizard.page1.patient.group.title";
	private static final String PRENAME = "wizard.page1.prename";
	private static final String NAME = "wizard.page1.name";
	private static final String BIRTHDAY = "wizard.page1.birthday";
	private static final String SEX = "wizard.page1.sex";
	private static final String ADDRESS = "wizard.page1.address";
	private static final String ZIP = "wizard.page1.zip";
	private static final String LOCATION = "wizard.page1.location";
	private static final String AHV = "wizard.page1.ahv.nr";
	private static final String INSURANT = "wizard.page1.insurant.nr";
	private static final String CARD_NR = "wizard.page1.card.nr";
	private static final String CARD_ID_NR = "wizard.page1.card.id.nr";
	private static final String SHOW_ALL = "wizard.page1.show.all";

	private CardInfoData cardInfoData;
	private PatientInfoData patientInfoData;
	private Properties messagesProperties;

	private Composite container;

	protected WizardPageOne(String pageName, Properties applicationProperties, Properties messagesProperties,
			CardInfoData cardInfoData, PatientInfoData patientInfoData) {
		super(pageName);
		setTitle(pageName);
		this.cardInfoData = cardInfoData;
		this.patientInfoData = patientInfoData;
		this.messagesProperties = messagesProperties;
	}

	@Override
	public void createControl(Composite parent) {

		container = new Composite(parent, SWT.NONE);

		GridLayout containerLayout1 = new GridLayout();
		container.setLayout(containerLayout1);
		containerLayout1.numColumns = 1;

		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 2;

		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);

		Group patientGroup = new Group(container, SWT.NONE);
		patientGroup.setText(messagesProperties.getProperty(PATIENT_GROUP_TITLE));
		patientGroup.setFont(JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT));
		patientGroup.setLayout(groupLayout);
		patientGroup.setLayoutData(gridData1);
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;

		Label lbl_prename = new Label(patientGroup, SWT.NONE);
		lbl_prename.setText(messagesProperties.getProperty(PRENAME));
		Text txt_prename = new Text(patientGroup, SWT.BORDER);
		txt_prename.setText(patientInfoData.getPrename());
		txt_prename.setEditable(false);
		txt_prename.setLayoutData(gridData);

		Label lbl_name = new Label(patientGroup, SWT.NONE);
		lbl_name.setText(messagesProperties.getProperty(NAME));
		Text txt_name = new Text(patientGroup, SWT.BORDER);
		txt_name.setText(patientInfoData.getName());
		txt_name.setEditable(false);
		txt_name.setLayoutData(gridData);

		Label lbl_birthday = new Label(patientGroup, SWT.NONE);
		lbl_birthday.setText(messagesProperties.getProperty(BIRTHDAY));
		Text txt_birthday = new Text(patientGroup, SWT.BORDER);
		txt_birthday.setText(patientInfoData.getBirthday());
		txt_birthday.setEditable(false);
		txt_birthday.setLayoutData(gridData);

		Label lbl_sex = new Label(patientGroup, SWT.NONE);
		lbl_sex.setText(messagesProperties.getProperty(SEX));
		Text txt_sex = new Text(patientGroup, SWT.BORDER);
		txt_sex.setText(patientInfoData.getSex());
		txt_sex.setEditable(false);
		txt_sex.setLayoutData(gridData);

		Label lbl_address = new Label(patientGroup, SWT.NONE);
		lbl_address.setText(messagesProperties.getProperty(ADDRESS));
		Text txt_address = new Text(patientGroup, SWT.BORDER);
		txt_address.setText(patientInfoData.getAddress());
		txt_address.setEditable(false);
		txt_address.setLayoutData(gridData);

		Label lbl_zip = new Label(patientGroup, SWT.NONE);
		lbl_zip.setText(messagesProperties.getProperty(ZIP));
		Text txt_zip = new Text(patientGroup, SWT.BORDER);
		txt_zip.setText(patientInfoData.getZip());
		txt_zip.setEditable(false);
		txt_zip.setLayoutData(gridData);

		Label lbl_location = new Label(patientGroup, SWT.NONE);
		lbl_location.setText(messagesProperties.getProperty(LOCATION));
		Text txt_location = new Text(patientGroup, SWT.BORDER);
		txt_location.setText(patientInfoData.getLocation());
		txt_location.setEditable(false);
		txt_location.setLayoutData(gridData);

		Label lbl_cardholderIdentifier = new Label(patientGroup, SWT.NONE);
		lbl_cardholderIdentifier.setText(messagesProperties.getProperty(AHV));
		Text txt_cardholderIdentifier = new Text(patientGroup, SWT.BORDER);
		txt_cardholderIdentifier.setText(patientInfoData.getCardholderIdentifier());
		txt_cardholderIdentifier.setEditable(false);
		txt_cardholderIdentifier.setLayoutData(gridData);

		Label lbl_insuredNumber = new Label(patientGroup, SWT.NONE);
		lbl_insuredNumber.setText(messagesProperties.getProperty(INSURANT));
		Text txt_insuredNumber = new Text(patientGroup, SWT.BORDER);
		txt_insuredNumber.setText(patientInfoData.getInsuredNumber());
		txt_insuredNumber.setEditable(false);
		txt_insuredNumber.setLayoutData(gridData);

		Label lbl_cardNumber = new Label(patientGroup, SWT.NONE);
		lbl_cardNumber.setText(messagesProperties.getProperty(CARD_NR));
		Text txt_cardNumber = new Text(patientGroup, SWT.BORDER);
		txt_cardNumber.setText(patientInfoData.getCardNumber());
		txt_cardNumber.setEditable(false);
		txt_cardNumber.setLayoutData(gridData);

		Label lbl_insuredPersonNumber = new Label(patientGroup, SWT.NONE);
		lbl_insuredPersonNumber.setText(messagesProperties.getProperty(CARD_ID_NR));
		Text txt_insuredPersonNumber = new Text(patientGroup, SWT.BORDER);
		txt_insuredPersonNumber.setText(patientInfoData.getInsuredPersonNumber());
		txt_insuredPersonNumber.setEditable(false);
		txt_insuredPersonNumber.setLayoutData(gridData);

		Button btnShowAll = new Button(container, SWT.PUSH);
		btnShowAll.setText(messagesProperties.getProperty(SHOW_ALL));
		btnShowAll.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		btnShowAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CardInfoDialog cardInfoDialog = new CardInfoDialog(parent.getShell(), cardInfoData, messagesProperties);
				cardInfoDialog.open();
			}
		});

		parent.getShell().setMinimumSize(800, 600);

		patientGroup.pack();
		container.pack();
		container.getShell().pack();

		canFlipToNextPage();
		setPageComplete(true);
		setControl(container);
	}
}
