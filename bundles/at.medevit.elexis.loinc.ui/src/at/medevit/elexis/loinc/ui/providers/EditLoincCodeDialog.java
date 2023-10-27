package at.medevit.elexis.loinc.ui.providers;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import at.medevit.elexis.loinc.model.LoincCode;
import at.medevit.elexis.loinc.ui.LoincServiceComponent;
import ch.elexis.core.data.events.ElexisEventDispatcher;

public class EditLoincCodeDialog extends TitleAreaDialog {
	
	private Text code;
	private Text shortDesc;
	private Text unit;
	private Text text;
	private Text clazz;
	private LoincCode loincCode;
	
	public EditLoincCodeDialog(Shell parentShell, LoincCode code){
		super(parentShell);
		this.loincCode = code;
	}
	
	@Override
	protected Control createContents(Composite parent){
		Control contents = super.createContents(parent);
		
		if (loincCode == null) {
			setTitle("Neuer LOINC Code");
			setMessage("Die Daten des neuen LOINC Code erfassen.");
		} else {
			setTitle("LOINC Code editieren");
			setMessage("Die Daten des LOINC Code ändern.");
		}
		return contents;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite composite = (Composite) super.createDialogArea(parent);
		
		Composite areaComposite = new Composite(composite, SWT.NONE);
		areaComposite
			.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		areaComposite.setLayout(new FormLayout());
		
		// FLD_CODE
		Label lbl = new Label(areaComposite, SWT.NONE);
		lbl.setText("Code");
		code = new Text(areaComposite, SWT.BORDER);
		if (loincCode != null)
			code.setText(loincCode.getCode());
		
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(0, 5);
		fd.right = new FormAttachment(20, -5);
		lbl.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(20, 5);
		fd.right = new FormAttachment(100, -5);
		code.setLayoutData(fd);
		
		// FLD_SHORTNAME
		lbl = new Label(areaComposite, SWT.NONE);
		lbl.setText("Kurz Beschreibung");
		shortDesc = new Text(areaComposite, SWT.BORDER);
		if (loincCode != null)
			shortDesc.setText(loincCode.get(LoincCode.FLD_SHORTNAME));
		
		fd = new FormData();
		fd.top = new FormAttachment(code, 5);
		fd.left = new FormAttachment(0, 5);
		fd.right = new FormAttachment(20, -5);
		lbl.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(code, 5);
		fd.left = new FormAttachment(20, 5);
		fd.right = new FormAttachment(100, -5);
		shortDesc.setLayoutData(fd);
		
		// FLD_UNIT
		lbl = new Label(areaComposite, SWT.NONE);
		lbl.setText("Einheit");
		unit = new Text(areaComposite, SWT.BORDER);
		if (loincCode != null)
			unit.setText(loincCode.get(LoincCode.FLD_UNIT));
		
		fd = new FormData();
		fd.top = new FormAttachment(shortDesc, 5);
		fd.left = new FormAttachment(0, 5);
		fd.right = new FormAttachment(20, -5);
		lbl.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(shortDesc, 5);
		fd.left = new FormAttachment(20, 5);
		fd.right = new FormAttachment(100, -5);
		unit.setLayoutData(fd);
		
		// FLD_LONGNAME
		lbl = new Label(areaComposite, SWT.NONE);
		lbl.setText("Text");
		text = new Text(areaComposite, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		if (loincCode != null)
			text.setText(loincCode.get(LoincCode.FLD_LONGNAME));
		
		fd = new FormData();
		fd.top = new FormAttachment(unit, 5);
		fd.left = new FormAttachment(0, 5);
		fd.right = new FormAttachment(20, -5);
		lbl.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(unit, 5);
		fd.left = new FormAttachment(20, 5);
		fd.right = new FormAttachment(100, -5);
		fd.height = 100;
		text.setLayoutData(fd);
		
		// FLD_CLASS
		lbl = new Label(areaComposite, SWT.NONE);
		lbl.setText("Klassifikation");
		clazz = new Text(areaComposite, SWT.BORDER);
		if (loincCode != null)
			clazz.setText(loincCode.get(LoincCode.FLD_CLASS));
		
		fd = new FormData();
		fd.top = new FormAttachment(text, 5);
		fd.left = new FormAttachment(0, 5);
		fd.right = new FormAttachment(20, -5);
		lbl.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(text, 5);
		fd.left = new FormAttachment(20, 5);
		fd.right = new FormAttachment(100, -5);
		clazz.setLayoutData(fd);
		
		return composite;
	}
	
	@Override
	protected void okPressed(){
		String textTxt = text.getText();
		String codeTxt = code.getText();
		String shortTxt = shortDesc.getText();
		String unitTxt = unit.getText();
		String clazzTxt = clazz.getText();
		
		if (codeTxt.length() == 0) {
			setErrorMessage("LOINC code darf nicht leer sein.");
			return;
		}
		
		LoincCode existing = LoincServiceComponent.getService().getByCode(codeTxt);
		if (existing != null) {
			setErrorMessage("LOINC mit dem code " + codeTxt + " bereits vorhanden.");
			return;
		} else {
			loincCode = new LoincCode(codeTxt, textTxt, shortTxt, clazzTxt, unitTxt);
		}
		
		ElexisEventDispatcher.reload(LoincCode.class);
		super.okPressed();
	}
}
