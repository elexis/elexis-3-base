package ch.docbox.elexis;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.docbox.ws.client.WsClientConfig;

public class CertificateConfigDialog extends Dialog {

	private Text certFileText;
	private Button certFileBtn;

	private Text certPass;

	protected CertificateConfigDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		container.setLayout(new GridLayout(2, false));

		Label lbl = new Label(container, SWT.NONE);
		lbl.setText("Zertifikat");
		Composite fileChooser = new Composite(container, SWT.NONE);
		fileChooser.setLayout(new GridLayout(2, false));
		fileChooser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		certFileText = new Text(fileChooser, SWT.BORDER);
		certFileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		certFileBtn = new Button(fileChooser, SWT.PUSH);
		certFileBtn.setText("...");
		certFileBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(getShell());
				String path = dlg.open();
				if (path != null) {
					certFileText.setText(path);
				}
			}
		});

		lbl = new Label(container, SWT.NONE);
		lbl.setText("Passwort");
		certPass = new Text(container, SWT.PASSWORD | SWT.BORDER);
		certPass.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		fromConfig();

		return container;
	}

	@Override
	protected void okPressed() {
		toConfig();
		super.okPressed();
	}

	private void fromConfig() {
		certFileText.setText(WsClientConfig.getP12Path());
		certPass.setText(WsClientConfig.getP12Password());
	}

	private void toConfig() {
		ConfigServiceHolder.setMandator(WsClientConfig.USR_DEFDOCBOXP12PATH, certFileText.getText());
		ConfigServiceHolder.setMandator(WsClientConfig.USR_DEFDOCBOXP12PASSWORD, certPass.getText());
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Zertifikat");
	}
}
