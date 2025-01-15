package at.medevit.elexis.ehc.ui.vacdoc.wizard;

import java.io.InputStream;
import java.util.Optional;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.ui.extension.IImportWizard;
import at.medevit.elexis.ehc.ui.vacdoc.service.VacdocServiceComponent;

public class ImportVaccinationsWizard extends Wizard implements IImportWizard {
	public static Logger logger = LoggerFactory.getLogger(ImportVaccinationsWizard.class);

	private ImportVaccinationsWizardPage1 vaccinationsMainPage;
	private Bundle ehcDocument;

	public ImportVaccinationsWizard() {
		setWindowTitle("Impfungen import");
	}

	@Override
	public boolean performFinish() {
		return vaccinationsMainPage.finish();
	}

	@Override
	public void addPages() {
		super.addPages();
		vaccinationsMainPage = new ImportVaccinationsWizardPage1("Impfungen auswählen", ehcDocument);
		addPage(vaccinationsMainPage);
	}

	@Override
	public void setDocument(InputStream document) {
		try {
			document.reset();
			Optional<Bundle> ehcDocumentOpt = VacdocServiceComponent.getService().loadVacdocDocument(document);
			ehcDocumentOpt.ifPresent(d -> ehcDocument = d);
		} catch (Exception e) {
			logger.error("Could not open document", e); //$NON-NLS-1$
			MessageDialog.openError(getShell(), "Fehler", "Konnte das Dokument nicht öffnen.");
		}
	}

	protected boolean isResizable() {
		return true;
	}
}
