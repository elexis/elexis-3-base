package at.medevit.elexis.ehc.ui.docbox.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.ehealth_connector.cda.ch.AbstractCdaCh;

import ch.elexis.data.Rezept;

public class ExportPrescriptionWizard extends Wizard {
	private ExportPrescriptionWizardPage1 prescriptionMainPage;
	//	private ExportPrescriptionWizardPage2 prescriptionDocboxPage;
	
	private static AbstractCdaCh<?> document;
	private static Rezept rezept;
	
	public ExportPrescriptionWizard(){
		setWindowTitle("Rezept Docbox export");
	}
	
	@Override
	public boolean performFinish(){
		return prescriptionMainPage.finish();
	}
	
	@Override
	public void addPages(){
		super.addPages();
		prescriptionMainPage = new ExportPrescriptionWizardPage1("Rezept auswählen");
		addPage(prescriptionMainPage);
		//		prescriptionDocboxPage = new ExportPrescriptionWizardPage2("Rezept bearbeiten");
		//		addPage(prescriptionDocboxPage);
	}
	
	public static void setDocument(AbstractCdaCh<?> document){
		ExportPrescriptionWizard.document = document;
	}
	
	public static AbstractCdaCh<?> getDocument(){
		return ExportPrescriptionWizard.document;
	}
	
	public static void setRezept(Rezept rezept){
		ExportPrescriptionWizard.rezept = rezept;
	}
	
	public static Rezept getRezept(){
		return ExportPrescriptionWizard.rezept;
	}
}
