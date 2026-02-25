package ch.elexis.agenda.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import ch.elexis.agenda.commands.EmailEditHandler;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.agenda.ui.Messages;
import ch.elexis.core.mail.MailAccount.TYPE;
import ch.elexis.core.mail.MailTextTemplate;
import ch.elexis.core.mail.ui.client.MailClientComponent;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.ITextTemplate;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IContext;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.ITextReplacementService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.util.SWTHelper;
import jakarta.inject.Inject;

public class EmailComposite extends Composite {

	private static final String TEMPLATE_TEXT = "[Pea.SiteUrl]"; //$NON-NLS-1$
	private static final String TEMPLATE = "Terminbestätigung inkl. Anmeldeformular"; //$NON-NLS-1$
	private static final String URL = "https://medelexis.ch/pea/"; //$NON-NLS-1$
	private static final String HYPERLINK = "<a href=\"https://medelexis.ch/pea/\">Weitere Informationen finden Sie hier.</a>"; //$NON-NLS-1$
	private static final String QUERY_SYMBOL = "?"; //$NON-NLS-1$
	private String preparedMessageText;
	@Inject
	private ITextReplacementService textReplacement;
	@Inject
	private IContextService contextService;


	private EmailEditHandler emailEdith;
	private Button chkEmail;
	private Label emailTemplatesLabel;
	private ComboViewer emailTemplatesViewer;
	private ITextTemplate previousTemplate = null;
	private boolean isEmailConfigured;
	private boolean isPatientSelected;
	private boolean hasEmail;
	private IContact pat;
	private Color blue = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
	private Label emailHyperlink;

	public EmailComposite(Composite parent, int style, IContact selectedContact, IAppointment appointment) {
		super(parent, style);
		CoreUiUtil.injectServicesWithContext(this);
		this.pat = selectedContact;
		setLayout(new GridLayout(4, false));
		chkEmail = new Button(this, SWT.CHECK);
		chkEmail.setText(Messages.Appointment_Confirmation);
		chkEmail.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false)); // Updated GridData
		chkEmail.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selected = chkEmail.getSelection();
				emailTemplatesViewer.getControl().setEnabled(selected);
				emailTemplatesLabel.setEnabled(selected);
				emailHyperlink.setEnabled(selected);
			}
		});

		emailTemplatesLabel = new Label(this, SWT.NONE);
		emailTemplatesLabel.setText(Messages.Core_E_Mail + StringUtils.SPACE + Messages.Core_Temlate);
		emailTemplatesLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false)); // Updated GridData
		emailTemplatesViewer = new ComboViewer(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		emailTemplatesViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		emailTemplatesViewer.setContentProvider(new ArrayContentProvider());
		emailTemplatesViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ITextTemplate) {
					ITextTemplate template = (ITextTemplate) element;
					return template.getName()
							+ (template.getMandator() != null ? " (" + template.getMandator().getLabel() + ")" //$NON-NLS-1$ //$NON-NLS-2$
									: StringUtils.EMPTY);
				}
				return super.getText(element);
			}
		});

		emailTemplatesViewer.addSelectionChangedListener(event -> {
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			Object selectedElement = selection.getFirstElement();
			if (selectedElement instanceof ITextTemplate) {
				ITextTemplate template = (ITextTemplate) selectedElement;
				String test = calculateTestValue();
				if (shouldNotExecute(template.getName(), test)) {
					Shell shell = emailTemplatesViewer.getControl().getShell();
					MessageDialog dialog = createMessageDialog(shell);
					if (dialog.open() == 0 && previousTemplate != null) {
						emailTemplatesViewer.setSelection(new StructuredSelection(previousTemplate), true);
					}
				} else {
					previousTemplate = template;
				}
			}
		});

		emailHyperlink = SWTHelper.createHyperlink(this, "E-Mail bearbeiten", new HyperlinkAdapter() { //$NON-NLS-1$
			@Override
			public void linkActivated(final HyperlinkEvent e) {
				String emailTemplate = getSelectedEmailTemplateViewerDetails().getTemplate();
				if (emailTemplate != null) {
					IContext context = contextService.createNamedContext("appointment_reminder_context"); //$NON-NLS-1$
					context.setTyped(appointment);
					context.setTyped(pat);
					preparedMessageText = textReplacement.performReplacement(context, emailTemplate);
				}
				if (emailEdith == null) {
					emailEdith = new EmailEditHandler();
				}
				String subject = getSelectedEmailTemplateViewerDetails().getName();
				emailEdith.openSendMailDialogWithContent(appointment, pat, preparedMessageText, subject);
			}
		});
		emailHyperlink.setForeground(blue);
		emailHyperlink.setEnabled(false);
		updateTemplatesCombo();
	}

	private void updateTemplatesCombo() {
		emailTemplatesViewer.setInput(MailTextTemplate.load());
	}

	public boolean isCheckboxChecked() {
		return chkEmail.getSelection();
	}

	private void selectSavedEmailTemplate() {
		String savedTemplate = ConfigServiceHolder.get()
				.get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT_APPOINTMENT_TEMPLATE, null);
		String test = calculateTestValue();
		List<ITextTemplate> templates = (List<ITextTemplate>) emailTemplatesViewer.getInput();
		if (templates == null || templates.isEmpty()) {
			return;
		}
		if (shouldNotExecute(savedTemplate, test)) {
			emailTemplatesViewer.setSelection(new StructuredSelection(templates.get(0)));
			return;
		}
		findAndSelectTemplate(savedTemplate, templates);
	}
	
  private void findAndSelectTemplate(String savedTemplate, List<ITextTemplate> templates) {
		if (savedTemplate != null && !savedTemplate.trim().isEmpty()) {
			for (ITextTemplate template : templates) {
				if (savedTemplate.equals(template.getName())) {
					emailTemplatesViewer.setSelection(new StructuredSelection(template));
					return;
				}
			}
		}
		emailTemplatesViewer.setSelection(new StructuredSelection(templates.get(0)));
	}
	
  public Object getSelectedTemplate() {
		return (emailTemplatesViewer != null && emailTemplatesViewer.getStructuredSelection() != null)
				? emailTemplatesViewer.getStructuredSelection().getFirstElement()
				: null;
	}

	private boolean shouldNotExecute(String savedTemplate, String test) {
		return TEMPLATE.equals(savedTemplate) && QUERY_SYMBOL.equals(test);
	}

	private String calculateTestValue() {
		textReplacement = OsgiServiceUtil.getService(ITextReplacementService.class)
				.orElseThrow(() -> new IllegalStateException());
		return textReplacement.performReplacement(ContextServiceHolder.get().getRootContext(), TEMPLATE_TEXT);
	}

  private MessageDialog createMessageDialog(Shell shell) {
		return new MessageDialog(shell, Messages.Warnung, null, Messages.Warning_Kein_Pea, MessageDialog.WARNING,
				new String[] { Messages.Core_Ok }, 0) {
			@Override
			protected Control createCustomArea(Composite parent) {
				Link link = new Link(parent, SWT.NONE);
				link.setText(HYPERLINK);
				link.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Program.launch(URL);
					}
				});
				return link;
			}
		};
	}

	public ITextTemplate getSelectedEmailTemplateViewerDetails() {
		IStructuredSelection selection = (IStructuredSelection) emailTemplatesViewer.getSelection();
		Object selectedElement = selection.getFirstElement();
		if (selectedElement instanceof ITextTemplate) {
			return (ITextTemplate) selectedElement;
		}
		return null;
	}

	public void updateStatus(boolean isEmailConfigured, boolean isPatientSelected, boolean hasEmail) {
		chkEmail.setEnabled(isEmailConfigured && isPatientSelected && hasEmail);
		boolean isEmailControlEnabled = chkEmail.getSelection() && isEmailConfigured && isPatientSelected && hasEmail;
		emailTemplatesViewer.getControl().setEnabled(isEmailControlEnabled);
		emailTemplatesLabel.setEnabled(isEmailControlEnabled);
		selectSavedEmailTemplate();
	}

	void updateEmailControlsStatus(IContact iContact) {
		List<String> validAccounts = getSendMailAccounts();
		String defaultMailAccountAppointment = ConfigServiceHolder.get()
				.get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT_APPOINTMENT, null);
		String defaultMailAccount = ConfigServiceHolder.get().get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT, null);
		isEmailConfigured = isValidEmailConfiguration(validAccounts, defaultMailAccountAppointment, defaultMailAccount);
		isPatientSelected = iContact instanceof IPatient;
		hasEmail = hasValidEmail(iContact);
		updateStatus(isEmailConfigured, isPatientSelected, hasEmail);
		this.pat = iContact;
	}

	private List<String> getSendMailAccounts() {
		List<String> ret = new ArrayList<>();
		ret.addAll(filterAccounts(MailClientComponent.getMailClient().getAccountsLocal()));
		ret.addAll(filterAccounts(MailClientComponent.getMailClient().getAccounts()));
		return ret;
	}

	private List<String> filterAccounts(List<String> accounts) {
		return accounts.stream().filter(aid -> MailClientComponent.getMailClient().getAccount(aid).isPresent())
				.filter(aid -> MailClientComponent.getMailClient().getAccount(aid).get().getType() == TYPE.SMTP)
				.collect(Collectors.toList());
	}

	private boolean isValidEmailConfiguration(List<String> validAccounts, String defaultMailAccountAppointment,
			String defaultMailAccount) {
		return validAccounts.contains(defaultMailAccountAppointment) || validAccounts.contains(defaultMailAccount);
	}

	private boolean hasValidEmail(IContact selectedContact) {
		return selectedContact != null && StringUtils.isNotBlank(selectedContact.getEmail());
	}

	public IPatient getSelectedPatient() {
		if (pat instanceof IPatient) {
			return (IPatient) pat;
		}
		return null;
	}

	public static class EmailDetails {
		private IPatient patient;
		private ITextTemplate template;

		public EmailDetails(ITextTemplate template, IPatient patient) {
			this.template = template;
			this.patient = patient;
		}

		public String getTemplateContent() {
			return template.getTemplate();
		}

		public String getTemplateName() {
			return template.getName();
		}

		public IPatient patient() {
			return patient.asIPatient();
		}
	}

  public EmailDetails extractEmailDetails() {
		ITextTemplate selectedTemplateViewerDetails = getSelectedEmailTemplateViewerDetails();
		if (selectedTemplateViewerDetails != null) {
			IPatient selectedPatient = getSelectedPatient();
			if (selectedPatient != null) {
				return new EmailDetails(selectedTemplateViewerDetails, selectedPatient);
			}
		}
		return null;
	}
}