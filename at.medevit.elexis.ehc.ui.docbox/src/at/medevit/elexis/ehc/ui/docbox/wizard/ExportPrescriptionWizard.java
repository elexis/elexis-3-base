package at.medevit.elexis.ehc.ui.docbox.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

import ch.elexis.data.Rezept;
import ehealthconnector.cda.documents.ch.CdaCh;

public class ExportPrescriptionWizard extends Wizard {
	private ExportPrescriptionWizardPage1 prescriptionMainPage;
	//	private ExportPrescriptionWizardPage2 prescriptionDocboxPage;
	
	private static CdaCh document;
	private static Rezept rezept;
	
	public ExportPrescriptionWizard(){}
	
	public void init(IWorkbench workbench, IStructuredSelection selection){
		setWindowTitle("Rezept Docbox export");
		setNeedsProgressMonitor(true);
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
	
	public static void setDocument(CdaCh document){
		ExportPrescriptionWizard.document = document;
	}
	
	public static CdaCh getDocument(){
		return ExportPrescriptionWizard.document;
	}
	
	public static void setRezept(Rezept rezept){
		ExportPrescriptionWizard.rezept = rezept;
	}
	
	public static Rezept getRezept(){
		return ExportPrescriptionWizard.rezept;
	}
}
