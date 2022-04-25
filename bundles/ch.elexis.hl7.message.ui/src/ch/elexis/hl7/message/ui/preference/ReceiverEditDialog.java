package ch.elexis.hl7.message.ui.preference;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ReceiverEditDialog extends Dialog {

	private Text applicationTxt;
	private Text facilityTxt;
	private Receiver receiver;

	protected ReceiverEditDialog(Shell parentShell) {
		super(parentShell);
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
		if (applicationTxt != null && !applicationTxt.isDisposed()) {
			applicationTxt.setText(receiver.getApplication());
		}
		if (facilityTxt != null && !facilityTxt.isDisposed()) {
			facilityTxt.setText(receiver.getFacility());
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite ret = (Composite) super.createDialogArea(parent);
		applicationTxt = new Text(ret, SWT.BORDER);
		applicationTxt.setMessage("Application");
		applicationTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (receiver != null) {
			applicationTxt.setText(receiver.getApplication());
		}

		facilityTxt = new Text(ret, SWT.BORDER);
		facilityTxt.setMessage("Facility");
		facilityTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (receiver != null) {
			facilityTxt.setText(receiver.getFacility());
		}
		return ret;
	}

	@Override
	protected void okPressed() {
		if (receiver != null) {
			receiver.setApplication(applicationTxt.getText());
			receiver.setFacility(facilityTxt.getText());
		}
		super.okPressed();
	}
}
