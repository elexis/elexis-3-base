package at.medevit.elexis.ehc.ui.vacdoc.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.vacdoc.service.VacdocService;

public class ExportVaccinationsWizard extends Wizard {
	public static Logger logger = LoggerFactory.getLogger(ExportVaccinationsWizard.class);

	private ExportVaccinationsWizardPage1 vaccinationsMainPage;

	public ExportVaccinationsWizard(){
		setWindowTitle("Impfungen export");
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
	
	public static VacdocService getVacdocService(){
		return new VacdocService();
	}
}
