package at.medevit.elexis.ehc.ui.vacdoc.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportVaccinationsWizard extends Wizard {
	public static Logger logger = LoggerFactory.getLogger(ExportVaccinationsWizard.class);

	private ExportVaccinationsWizardPage1 vaccinationsMainPage;

	public ExportVaccinationsWizard(){
		setWindowTitle("Impfungen export als XDM oder CDA");
	}

	@Override
	public boolean performFinish(){
		return vaccinationsMainPage.finish();
	}
	
	@Override
	public void addPages(){
		super.addPages();
		vaccinationsMainPage = new ExportVaccinationsWizardPage1("Impfungen auswählen");
		addPage(vaccinationsMainPage);
	}
}
