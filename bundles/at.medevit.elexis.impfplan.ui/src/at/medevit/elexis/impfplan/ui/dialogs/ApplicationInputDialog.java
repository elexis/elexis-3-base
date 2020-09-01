package at.medevit.elexis.impfplan.ui.dialogs;

import java.util.Optional;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
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
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.data.Patient;

public class ApplicationInputDialog extends TitleAreaDialog {
	private static final String DEF_SIDE = "left";
	private boolean showSideOption;
	private String lotNo, side;
	
	private Text txtLotNo;
	private Button btnLeft, btnRight;
	private Optional<IArticle> art;
	
	public ApplicationInputDialog(Shell parentShell, IArticle article){
		super(parentShell);
		showSideOption = ConfigServiceHolder.getUser(PreferencePage.VAC_SHOW_SIDE, false);
		art = Optional.ofNullable(article);
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		setTitle("Impfungsdetails");
		setTitleImage(ResourceManager.getPluginImage("at.medevit.elexis.impfplan.ui",
			"rsc/icons/vaccination_logo.png"));
		Patient sp = ElexisEventDispatcher.getSelectedPatient();
		setMessage((sp!=null) ? sp.getLabel() : "missing patient name"); //$NON-NLS-1$
		
		Composite area = (Composite) super.createDialogArea(parent);
		Composite containerLotNo = new Composite(area, SWT.NONE);
		containerLotNo.setLayout(new GridLayout(2, false));
		containerLotNo.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblLotNo = new Label(containerLotNo, SWT.NONE);
		lblLotNo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		lblLotNo.setText("Lot-Nummer für Impfstoff angeben");
		
		Label lblArticleName = new Label(containerLotNo, SWT.NONE);
		lblArticleName.setAlignment(SWT.CENTER);
		lblArticleName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		FontDescriptor boldDescriptor =
			FontDescriptor.createFrom(lblArticleName.getFont()).setStyle(SWT.BOLD);
		Font boldFont = boldDescriptor.createFont(lblArticleName.getDisplay());
		lblArticleName.setFont(boldFont);
		lblArticleName.setText((art.isPresent() ? art.get().getLabel() : "Artikel nicht gefunden"));
		
		Label lblP = new Label(containerLotNo, SWT.NONE);
		lblP.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
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
			
			if (DEF_SIDE.equals(ConfigServiceHolder.getUser(PreferencePage.VAC_DEFAULT_SIDE, DEF_SIDE))) {
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
