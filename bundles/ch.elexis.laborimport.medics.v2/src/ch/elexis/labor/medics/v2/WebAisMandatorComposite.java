package ch.elexis.labor.medics.v2;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.dialogs.base.InputDialog;
import ch.elexis.labor.medics.v2.order.WebAis;

public class WebAisMandatorComposite extends Composite {

	private IMandator mandator;

	private Button btnCredentials;

	private MedicsPreferencePage medicsPreferencePage;

	public WebAisMandatorComposite(IMandator mandator, MedicsPreferencePage medicsPreferencePage, Composite container,
			int style) {
		super(container, style);
		this.mandator = mandator;
		this.medicsPreferencePage = medicsPreferencePage;
		createContent();
	}

	private void createContent() {
		setLayout(new GridLayout());

		Label lab = new Label(this, SWT.NONE);
		lab.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lab.setText(mandator.getLabel());

		btnCredentials = new Button(this, SWT.PUSH);
		btnCredentials.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CredentialsDialog dialog = new CredentialsDialog(getShell());
				if (dialog.open() == InputDialog.OK) {
					updateCredentials();
				}
			}
		});
		updateCredentials();
	}

	public void updateCredentials() {
		if (WebAis.hasCredentials(mandator)) {
			btnCredentials.setText("Anmelde Informationen überschrieben");
		} else {
			btnCredentials.setText("Anmelde Informationen setzen");
		}
		layout();
	}

	private void setDefaultCredentialsConfig(String customer, String username, String password, String requester) {
		List<IMandator> mandators = CoreModelServiceHolder.get().getQuery(IMandator.class).execute();
		mandators.sort((l, r) -> {
			return l.getLabel().compareTo(r.getLabel());
		});
		mandators.forEach(m -> {
			if(!WebAis.hasCredentials(m)) {
				setCredentialsConfig(m, customer, username, password, requester);
			}
		});
		medicsPreferencePage.updateMandatorComposites();
	}

	private void setCredentialsConfig(IMandator mandator, String customer, String username, String password,
			String requester) {
		ConfigServiceHolder.get().set(mandator, WebAis.CFG_MEDICS_LABORDER_CUSTOMER, customer);
		ConfigServiceHolder.get().set(mandator, WebAis.CFG_MEDICS_LABORDER_USERNAME, username);
		ConfigServiceHolder.get().set(mandator, WebAis.CFG_MEDICS_LABORDER_PASSWORD, password);
		ConfigServiceHolder.get().set(mandator, WebAis.CFG_MEDICS_LABORDER_REQUESTER, requester);
	}

	private class CredentialsDialog extends Dialog {

		private Text customerTxt;
		private Text usernameTxt;
		private Text passwordTxt;
		private Text requesterTxt;

		protected CredentialsDialog(Shell parentShell) {
			super(parentShell);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite ret = (Composite) super.createDialogArea(parent);

			customerTxt = new Text(ret, SWT.BORDER);
			customerTxt.setMessage("Customer");
			customerTxt.setToolTipText("Customer");
			customerTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

			usernameTxt = new Text(ret, SWT.BORDER);
			usernameTxt.setMessage("Username");
			usernameTxt.setToolTipText("Username");
			usernameTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

			passwordTxt = new Text(ret, SWT.BORDER);
			passwordTxt.setMessage("Password");
			passwordTxt.setToolTipText("Password");
			passwordTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

			requesterTxt = new Text(ret, SWT.BORDER);
			requesterTxt.setMessage("Requester");
			requesterTxt.setToolTipText("Requester");
			requesterTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

			loadFromConfig();
			
			Button clear = new Button(ret, SWT.PUSH);
			clear.setText("Leeren");
			clear.setToolTipText("Diese Konfiguration leeren.");
			clear.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			clear.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setCredentialsConfig(mandator, null, null, null, null);
					loadFromConfig();
				}
			});

			if (ContextServiceHolder.get().getActiveUser().isPresent()
					&& ContextServiceHolder.get().getActiveUser().get().isAdministrator()) {
				Button setAsDefault = new Button(ret, SWT.PUSH);
				setAsDefault.setText("Als std. übernehmen");
				setAsDefault
						.setToolTipText("Als Standard bei allen Mandanten die noch keine Konfiguration haben setzen.");
				setAsDefault.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				setAsDefault.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						setDefaultCredentialsConfig(customerTxt.getText(), usernameTxt.getText(), passwordTxt.getText(),
								requesterTxt.getText());
					}
				});
			}

			return ret;
		}

		private void loadFromConfig() {
			customerTxt.setText(
					ConfigServiceHolder.get().get(mandator, WebAis.CFG_MEDICS_LABORDER_CUSTOMER, StringUtils.EMPTY));
			usernameTxt.setText(
					ConfigServiceHolder.get().get(mandator, WebAis.CFG_MEDICS_LABORDER_USERNAME, StringUtils.EMPTY));
			passwordTxt.setText(
					ConfigServiceHolder.get().get(mandator, WebAis.CFG_MEDICS_LABORDER_PASSWORD, StringUtils.EMPTY));
			requesterTxt.setText(
					ConfigServiceHolder.get().get(mandator, WebAis.CFG_MEDICS_LABORDER_REQUESTER, StringUtils.EMPTY));
		}

		@Override
		protected void okPressed() {
			setCredentialsConfig(mandator, customerTxt.getText(), usernameTxt.getText(), passwordTxt.getText(),
					requesterTxt.getText());
			super.okPressed();
		}
	}
}
