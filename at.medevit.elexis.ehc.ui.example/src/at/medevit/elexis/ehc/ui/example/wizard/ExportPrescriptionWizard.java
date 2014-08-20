package at.medevit.elexis.ehc.ui.example.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

public class ExportPrescriptionWizard extends Wizard {
	private ExportPrescriptionWizardPage1 prescriptionMainPage;
	
	public ExportPrescriptionWizard(){}
	
	public void init(IWorkbench workbench, IStructuredSelection selection){
		setWindowTitle("Rezept export");
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public boolean performFinish(){
		return prescriptionMainPage.finish();
	}
	
	@Override
	public void addPages(){
		super.addPages();
		prescriptionMainPage = new ExportPrescriptionWizardPage1("Export Rezept");
		addPage(prescriptionMainPage);
	}
}
