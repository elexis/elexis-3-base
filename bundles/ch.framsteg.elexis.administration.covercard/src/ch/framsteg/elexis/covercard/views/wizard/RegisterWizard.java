package ch.framsteg.elexis.covercard.views.wizard;

import java.util.Properties;

import org.eclipse.jface.wizard.Wizard;
import ch.framsteg.elexis.covercard.dao.CardInfoData;
import ch.framsteg.elexis.covercard.dao.PatientInfoData;

public class RegisterWizard extends Wizard {

	private Properties applicationProperties;
	private Properties messagesProperties;

	private WizardPage1 first;
	private WizardPage2 second;
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
		first = new WizardPage1(messagesProperties.getProperty(WIZARD_PAGE_1_TITLE), applicationProperties,
				messagesProperties, cardInfoData, patientInfoData);
		second = new WizardPage2(messagesProperties.getProperty(WIZARD_PAGE_2_TITLE), patientInfoData,
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
