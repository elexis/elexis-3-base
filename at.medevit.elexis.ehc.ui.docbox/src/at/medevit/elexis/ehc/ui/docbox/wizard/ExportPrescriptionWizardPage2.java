package at.medevit.elexis.ehc.ui.docbox.wizard;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import at.medevit.elexis.ehc.ui.preference.PreferencePage;
import ch.elexis.core.data.activator.CoreHub;

public class ExportPrescriptionWizardPage2 extends WizardPage {
	private Text xmlText;
	
	protected ExportPrescriptionWizardPage2(String pageName){
		super(pageName);
		setTitle(pageName);
	}
	
	@Override
	public void createControl(Composite parent){
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		xmlText = new Text(composite, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		xmlText.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		setControl(composite);
	}
	
	@Override
	public void setVisible(boolean visible){
		super.setVisible(visible);
		if (visible) {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ExportPrescriptionWizard.getDocument().cPrintXmlToStream(output);
			xmlText.setText(output.toString());
		}
	}
	
	@Override
	public boolean isPageComplete(){
		return !xmlText.getText().isEmpty();
	}
	
	public boolean finish(){
		try {
			String outputDir =
				CoreHub.userCfg.get(PreferencePage.EHC_OUTPUTDIR,
					PreferencePage.getDefaultOutputDir());
			ExportPrescriptionWizard.getDocument().cSaveToFile(
				outputDir + File.separator + ExportPrescriptionWizard.getRezept().getLabel()
					+ ".xml");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
