package org.iatrix.bestellung.rose;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.ui.icons.Images;

public class RoseClientNumberComposite extends Composite {

	private Text clientIdent;
	private Text clientNumber;

	private AdditionalClientNumber additionalClientNumber;

	public RoseClientNumberComposite(Composite parent, int style) {
		super(parent, style);

		createContent();
	}

	public void setClientNumber(AdditionalClientNumber additionalClientNumber) {
		this.additionalClientNumber = additionalClientNumber;
		clientIdent.setText(additionalClientNumber.getClientIdent());
		clientNumber.setText(additionalClientNumber.getClientNumber());
	}

	public Optional<AdditionalClientNumber> getClientNumber() {
		if (StringUtils.isNotEmpty(clientIdent.getText()) && StringUtils.isNotEmpty(clientNumber.getText())) {
			additionalClientNumber.setClientIdent(clientIdent.getText());
			additionalClientNumber.setClientNumber(clientNumber.getText());
			return Optional.of(additionalClientNumber);
		}
		return Optional.empty();
	}

	private void createContent() {
		setLayout(new GridLayout(2, true));

		Label lbl = new Label(this, SWT.NONE);
		lbl.setText("Identifikation (frei wählbar)");

		clientIdent = new Text(this, SWT.BORDER);
		clientIdent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		lbl = new Label(this, SWT.NONE);
		lbl.setText("Kundennummer");

		clientNumber = new Text(this, SWT.BORDER);
		clientNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Button btn = new Button(this, SWT.PUSH);
		btn.setImage(Images.IMG_ADDITEM.getImage());
		btn.setToolTipText("Kundennummer entfernen");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				additionalClientNumber.setClientIdent("");
				additionalClientNumber.setClientNumber("");
				RoseClientNumberComposite.this.dispose();
			}
		});
	}
}
