package at.medevit.elexis.agenda.ui.composite;

import java.util.List;

import javax.inject.Inject;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.mail.MailTextTemplate;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.ITextTemplate;
import ch.elexis.core.services.ITextReplacementService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.utils.OsgiServiceUtil;

public class EmailComposit extends Composite {

	@Inject
	private ITextReplacementService textReplacement;

	private Button chkEmail;
	private Label emailTemplatesLabel;
	private ComboViewer emailTemplatesViewer;
	private final String templateText = "[Pea.SiteUrl]";
	private final String template = "Terminbestätigung inkl. Anmeldeformular";
	private final String url = "https://medelexis.ch/pea/";
	private final String hyperlink = "<a href=\"https://medelexis.ch/pea/\">Weitere Informationen finden Sie hier.</a>";
	private final String QUERY_SYMBOL = "?";
	private ITextTemplate previousTemplate = null;

	public EmailComposit(Composite parent, int style, Object pat) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		chkEmail = new Button(this, SWT.CHECK);
		chkEmail.setText(Messages.Appointment_Confirmation);
		chkEmail.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false)); // Updated GridData
		chkEmail.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selected = chkEmail.getSelection();
				emailTemplatesViewer.getControl().setEnabled(selected);
				emailTemplatesLabel.setEnabled(selected);
			}
		});
		emailTemplatesLabel = new Label(this, SWT.NONE);
		emailTemplatesLabel.setText(Messages.Core_E_Mail + " " + Messages.Core_Temlate);
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
							+ (template.getMandator() != null ? " (" + template.getMandator().getLabel() + ")"
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
		updateTemplatesCombo();
	}
	private void updateTemplatesCombo() {
		emailTemplatesViewer.setInput(MailTextTemplate.load());
		emailTemplatesViewer.refresh();

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
		return template.equals(savedTemplate) && QUERY_SYMBOL.equals(test);
	}
	private String calculateTestValue() {
		textReplacement = OsgiServiceUtil.getService(ITextReplacementService.class)
				.orElseThrow(() -> new IllegalStateException());
			return textReplacement.performReplacement(ContextServiceHolder.get().getRootContext(), templateText);
	}
	private MessageDialog createMessageDialog(Shell shell) {
		return new MessageDialog(shell, Messages.Warnung, null, Messages.Warning_Kein_Pea, MessageDialog.WARNING,
				new String[] { Messages.Core_Ok }, 0) {
			@Override
			protected Control createCustomArea(Composite parent) {
				Link link = new Link(parent, SWT.NONE);
				link.setText(hyperlink);
				link.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Program.launch(url);
					}
				});
				return link;
			}
		};
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
}

