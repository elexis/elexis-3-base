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

import org.eclipse.jface.wizard.Wizard;
import ch.framsteg.elexis.covercard.dao.CardInfoData;
import ch.framsteg.elexis.covercard.dao.PatientInfoData;

public class RegisterWizard extends Wizard {

	private Properties applicationProperties;
	private Properties messagesProperties;

	private WizardPageOne first;
	private WizardPageTwo second;
	private CardInfoData cardInfoData;
	private PatientInfoData patientInfoData;

	private final static String WIZARD_TITLE = "wizard.title";
	private final static String WIZARD_PAGE_1_TITLE = "wizard.first.page.title";
	private final static String WIZARD_PAGE_2_TITLE = "wizard.second.page.title";

	public RegisterWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	public RegisterWizard(String id, Properties applicationProperties, Properties messagesProperties,
			CardInfoData cardInfoData, PatientInfoData patientInfoData) {
		super();
		setNeedsProgressMonitor(true);
		this.applicationProperties = applicationProperties;
		this.messagesProperties = messagesProperties;
		this.setCardInfoData(cardInfoData);
		this.setPatientBean(patientInfoData);
	}

	public CardInfoData getCardInfoData() {
		return cardInfoData;
	}

	public void setCardInfoData(CardInfoData cardInfoData) {
		this.cardInfoData = cardInfoData;
	}

	@Override
	public String getWindowTitle() {
		return messagesProperties.getProperty(WIZARD_TITLE);
	}

	@Override
	public void addPages() {
		first = new WizardPageOne(messagesProperties.getProperty(WIZARD_PAGE_1_TITLE), applicationProperties,
				messagesProperties, cardInfoData, patientInfoData);
		second = new WizardPageTwo(messagesProperties.getProperty(WIZARD_PAGE_2_TITLE), patientInfoData,
				applicationProperties, messagesProperties);
		addPage(first);
		addPage(second);
	}

	@Override
	public boolean performFinish() {
		second.finish();
		return true;
	}

	public PatientInfoData getPatientBean() {
		return patientInfoData;
	}

	public void setPatientBean(PatientInfoData patientBean) {
		this.patientInfoData = patientBean;
	}
}
