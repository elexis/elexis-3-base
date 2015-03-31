package at.medevit.elexis.impfplan.ui.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

import at.medevit.elexis.impfplan.ui.preferences.PreferencePage;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Patient;

public class ApplicationInputDialog extends TitleAreaDialog {
	private static final String DEF_SIDE = "left";
	private boolean showSideOption;
	private String lotNo, side;
	
	private Text txtLotNo;
	private Button btnLeft, btnRight;
	
	public ApplicationInputDialog(Shell parentShell){
		super(parentShell);
		showSideOption = CoreHub.userCfg.get(PreferencePage.VAC_SHOW_SIDE, false);
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		setTitle("Impfungsdetails");
		setTitleImage(ResourceManager.getPluginImage("at.medevit.elexis.impfplan.ui",
			"rsc/icons/vaccination_logo.png"));
		Patient selectedPatient = ElexisEventDispatcher.getSelectedPatient();
		setMessage(selectedPatient.getLabel());
		
		Composite area = (Composite) super.createDialogArea(parent);
		Composite containerLotNo = new Composite(area, SWT.NONE);
		containerLotNo.setLayout(new GridLayout(2, false));
		containerLotNo.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblLotNo = new Label(containerLotNo, SWT.NONE);
		lblLotNo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		lblLotNo.setText("Bitte geben Sie die Lot-Nummer des Impfstoffes an");
		
		txtLotNo = new Text(containerLotNo, SWT.BORDER);
		txtLotNo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		if (showSideOption) {
			Composite containerSide = new Composite(area, SWT.NONE);
			containerSide.setLayout(new GridLayout(2, false));
			containerSide.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			Label lblSide = new Label(containerSide, SWT.NONE);
			lblSide.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			lblSide.setText("Impfung wurde auf folgender Seite verabreicht");
			
			btnLeft = new Button(containerSide, SWT.RADIO);
			btnLeft.setText("links");
			btnRight = new Button(containerSide, SWT.RADIO);
			btnRight.setText("rechts");
			
			if (DEF_SIDE.equals(CoreHub.userCfg.get(PreferencePage.VAC_DEFAULT_SIDE, DEF_SIDE))) {
				btnLeft.setSelection(true);
			} else {
				btnRight.setSelection(true);
			}
		}
		
		return area;
	}
	
	@Override
	protected void okPressed(){
		lotNo = txtLotNo.getText();
		
		if (showSideOption) {
			side = btnLeft.getSelection() ? btnLeft.getText() : btnRight.getText();
		}
		super.okPressed();
	}
	
	public String getLotNo(){
		return lotNo;
	}
	
	public String getSide(){
		return side;
	}
}
