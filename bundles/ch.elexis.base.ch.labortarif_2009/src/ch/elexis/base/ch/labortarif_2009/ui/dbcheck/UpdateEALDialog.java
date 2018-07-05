package ch.elexis.base.ch.labortarif_2009.ui.dbcheck;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class UpdateEALDialog extends Dialog {
	
	private String title;
	private Button okButton;
	
	private Button updateEALBlocksBtn;
	private boolean updateEALBlocksState;
	
	private Button updateEALAnalysenBlocksBtn;
	private boolean updateEALAnalysenBlocksState;
	
	private Button updateEALLabItemBtn;
	private boolean updateEALLabItemState;
	
	private Button updateEALVerrechnet2015Btn;
	private boolean updateEALVerrechnet2015State;
	
	public UpdateEALDialog(Shell parentShell, String dialogTitle){
		super(parentShell);
		this.title = dialogTitle;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell){
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent){
		// create OK and Cancel buttons by default
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Control createDialogArea(Composite parent){
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);
		
		updateEALBlocksBtn = new Button(composite, SWT.CHECK);
		updateEALBlocksBtn
			.setText("In den Blöcken enthaltene Laborleistungen updaten (Bsp 1245.00 => 1245.00)");
		updateEALBlocksBtn.setSelection(false);
		
		updateEALAnalysenBlocksBtn = new Button(composite, SWT.CHECK);
		updateEALAnalysenBlocksBtn
			.setText("In den Blöcken enthaltene Laborleistungen durch Schnellanalysen ersetzen (Bsp 1245.00 => 1245.01)");
		updateEALAnalysenBlocksBtn.setSelection(false);
		
		updateEALLabItemBtn = new Button(composite, SWT.CHECK);
		updateEALLabItemBtn
			.setText("Automatische Verrechnungspositionen bei Eingang durch Laborgeräte durch Schnellanalysen ersetzen");
		updateEALLabItemBtn.setSelection(false);
		
		updateEALVerrechnet2015Btn = new Button(composite, SWT.CHECK);
		updateEALVerrechnet2015Btn
			.setText("2015 bereits verrechnete Laborleistungen wo möglich durch Schnellanalysen ersetzen");
		updateEALVerrechnet2015Btn.setSelection(false);

		applyDialogFont(composite);
		return composite;
	}
	
	/**
	 * Returns the ok button.
	 * 
	 * @return the ok button
	 */
	protected Button getOkButton(){
		return okButton;
	}

	public boolean isUpdateEALBlocks(){
		return updateEALBlocksState;
	}

	public boolean isUpdateEALAnalysenBlocks(){
		return updateEALAnalysenBlocksState;
	}
	
	public boolean isUpdateEALLabItem(){
		return updateEALLabItemState;
	}

	public boolean isUpdateVerrechnet2015(){
		return updateEALVerrechnet2015State;
	}

	@Override
	protected void okPressed(){
		updateEALBlocksState = updateEALBlocksBtn.getSelection();
		updateEALAnalysenBlocksState = updateEALAnalysenBlocksBtn.getSelection();
		updateEALLabItemState = updateEALLabItemBtn.getSelection();
		updateEALVerrechnet2015State = updateEALVerrechnet2015Btn.getSelection();
		super.okPressed();
	}
}
