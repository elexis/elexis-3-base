package at.medevit.elexis.emediplan.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class CustomMessageDialog<T extends Composite> extends MessageDialog {
	private final T customArea;
	
	public CustomMessageDialog(Shell parent, String title, String message, T customArea){
		super(parent, title, null, message, CONFIRM, 0, new String[] {
			IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL
		});
		int style = SWT.SHEET;
		setShellStyle(getShellStyle() | style);
		this.customArea = customArea;
	}
	
	@Override
	protected Control createCustomArea(Composite parent){
		if (customArea != null) {
			Composite content = new Composite(parent, SWT.NONE);
			content.setLayout(new GridLayout(1, false));
			content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			customArea.setParent(content);
			customArea.setVisible(true);
			return content;
		}
		return null;
	}
	
	public T getCustomArea(){
		return customArea;
	}
	
	@Override
	protected void buttonPressed(int buttonId){
		if (buttonId == MessageDialog.OK && customArea != null) {
			customArea.update();
		}
		super.buttonPressed(buttonId);
	}
	
}