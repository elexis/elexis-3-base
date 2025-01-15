package at.medevit.elexis.ehc.ui.vacdoc.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportVaccinationsWizard extends Wizard {
	public static Logger logger = LoggerFactory.getLogger(ExportVaccinationsWizard.class);

	private ExportVaccinationsWizardPage1 vaccinationsMainPage;

	private final ExportType exportType;

	public ExportVaccinationsWizard(ExportType exportType) {
		this.exportType = exportType;
		setWindowTitle("Impfungen export als " + exportType.name());
	}

	public ExportVaccinationsWizard() {
		this(ExportType.FHIR);
	}

	@Override
	public boolean performFinish() {
		return vaccinationsMainPage.finish();
	}

	@Override
	public void addPages() {
		super.addPages();
		vaccinationsMainPage = new ExportVaccinationsWizardPage1("Impfungen auswählen", exportType);
		addPage(vaccinationsMainPage);
	}

	public enum ExportType {
		FHIR, XDM
	}

	protected boolean isResizable() {
		return true;
	}
}
