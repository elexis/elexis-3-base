package at.medevit.elexis.emediplan.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.l10n.Messages;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class RemarkDialog extends Dialog {

	private Text remarkText;

	private Button dontAskAgain;

	private String remark;

	public RemarkDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.EMediplanRemarkDialog_Title);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(area, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.EMediplanRemarkDialog_Remark);

		remarkText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		GridData textData = new GridData(SWT.FILL, SWT.FILL, true, true);
		textData.widthHint = 400;
		textData.heightHint = 80;
		remarkText.setLayoutData(textData);

		dontAskAgain = new Button(composite, SWT.CHECK);
		dontAskAgain.setText(Messages.EMediplanRemarkDialog_DontAskAgain);
		dontAskAgain.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Label hint = new Label(composite, SWT.WRAP);
		hint.setText(Messages.EMediplanRemarkDialog_ReenableHint);
		hint.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		return area;
	}

	@Override
	protected void okPressed() {
		remark = remarkText.getText();
		if (dontAskAgain.getSelection()) {
			ConfigServiceHolder.get().setActiveUserContact(Preferences.MEDICATION_SETTINGS_EMEDIPLAN_ASK_REMARK, false);
		}
		super.okPressed();
	}

	public static boolean isAskForRemark() {
		return ConfigServiceHolder.get().getActiveUserContact(Preferences.MEDICATION_SETTINGS_EMEDIPLAN_ASK_REMARK,
				true);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.EMediplanRemarkDialog_Create, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	public String getRemark() {
		return remark;
	}
}
