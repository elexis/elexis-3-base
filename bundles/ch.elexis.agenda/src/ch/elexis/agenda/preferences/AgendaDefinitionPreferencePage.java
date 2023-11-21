package ch.elexis.agenda.preferences;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.agenda.Messages;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.mail.MailAccount.TYPE;
import ch.elexis.core.mail.MailTextTemplate;
import ch.elexis.core.mail.ui.client.MailClientComponent;
import ch.elexis.core.model.ITextTemplate;
import ch.elexis.core.model.agenda.AreaType;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.AddStringEntryAction;
import ch.elexis.core.ui.actions.MoveEntryWithinListAction;
import ch.elexis.core.ui.actions.RemoveSelectedEntriesAction;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.dialogs.provider.ILocalizedEnumLabelProvider;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Anwender;
import ch.elexis.data.Kontakt;
import ch.elexis.dialogs.FarbenSelektor;

public class AgendaDefinitionPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	ConfigServicePreferenceStore prefs = new ConfigServicePreferenceStore(Scope.GLOBAL);

	private Link linkAreaTypeValue;
	private ListViewer listViewerArea;
	private ListViewer listViewerAppointmentTypes;
	private ListViewer listViewerAppointmentStatus;

	private java.util.List<String> areas;
	private java.util.List<String> appointmentTypes;
	private java.util.List<String> appointmentStatus;
	private java.util.List<String> ret;
	private java.util.List<String> accounts;
	private java.util.List<String> accountsInput;
	private java.util.List<Object> combined;
	private java.util.List<ITextTemplate> templates;
	private Button btnAvoidDoubleBooking;
	private ComboViewer comboViewerAreaType;
	private ComboViewer appointmentTemplatesViewer, accountsViewer;
	private String selectedTemplateName;

	/**
	 * Create the preference page.
	 */
	public AgendaDefinitionPreferencePage() {
		setPreferenceStore(prefs);
		setDescription(Messages.AgendaDefinitionen_defForAgenda);
		areas = new ArrayList<String>(ConfigServiceHolder.getGlobalAsList(PreferenceConstants.AG_BEREICHE));
		appointmentTypes = new ArrayList<String>(
				ConfigServiceHolder.getGlobalAsList(PreferenceConstants.AG_TERMINTYPEN));
		appointmentStatus = new ArrayList<String>(
				ConfigServiceHolder.getGlobalAsList(PreferenceConstants.AG_TERMINSTATUS));
	}

	/**
	 * Create contents of the preference page.
	 *
	 * @param parent
	 */
	@Override
	public Control createContents(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(1, false));

		Composite compAreas = new Composite(container, SWT.NONE);
		compAreas.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compAreas.setLayout(new GridLayout(2, false));

		Label lblNewLabel = new Label(compAreas, SWT.NONE);
		lblNewLabel.setText(Messages.AgendaDefinitionen_shortCutsForBer);

		new Label(compAreas, SWT.NONE);

		listViewerArea = new ListViewer(compAreas, SWT.BORDER | SWT.V_SCROLL);
		listViewerArea.setContentProvider(ArrayContentProvider.getInstance());
		listViewerArea.setLabelProvider(new LabelProvider());
		listViewerArea.addSelectionChangedListener(sc -> {
			String type = ConfigServiceHolder.getGlobal(PreferenceConstants.AG_BEREICH_PREFIX
					+ sc.getStructuredSelection().getFirstElement() + PreferenceConstants.AG_BEREICH_TYPE_POSTFIX,
					null);
			if (type != null) {
				if (type.startsWith(AreaType.CONTACT.name())) {
					Kontakt contact = Kontakt.load(type.substring(AreaType.CONTACT.name().length() + 1));
					comboViewerAreaType.setSelection(new StructuredSelection(AreaType.CONTACT));
					linkAreaTypeValue.setText("<a>" + contact.getLabel() + "</a>");
					return;
				}
			}
			comboViewerAreaType.setSelection(new StructuredSelection(AreaType.GENERIC));
			linkAreaTypeValue.setText(StringUtils.EMPTY);

		});

		List listArea = listViewerArea.getList();
		GridData gd_listArea = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_listArea.heightHint = 80;
		listArea.setLayoutData(gd_listArea);

		MenuManager listAreaMenuManager = new MenuManager();
		listAreaMenuManager.add(new MoveEntryWithinListAction(listViewerArea, areas, true));
		listAreaMenuManager.add(new MoveEntryWithinListAction(listViewerArea, areas, false));
		listAreaMenuManager.add(new Separator());
		listAreaMenuManager.add(new AddStringEntryAction(listViewerArea, areas));
		listAreaMenuManager.add(new RemoveSelectedEntriesAction(listViewerArea, areas));
		Menu menu = listAreaMenuManager.createContextMenu(listArea);
		listArea.setMenu(menu);

		Composite compositeAreaType = new Composite(compAreas, SWT.NONE);
		compositeAreaType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_compositeAreaType = new GridLayout(1, false);
		gl_compositeAreaType.horizontalSpacing = 0;
		gl_compositeAreaType.marginWidth = 0;
		gl_compositeAreaType.verticalSpacing = 0;
		gl_compositeAreaType.marginHeight = 0;
		compositeAreaType.setLayout(gl_compositeAreaType);

		Label lblAreaTypeLabel = new Label(compositeAreaType, SWT.NONE);
		lblAreaTypeLabel.setBounds(0, 0, 59, 14);
		lblAreaTypeLabel.setText(Messages.AgendaDefinitionen_areaTypeLabel);

		comboViewerAreaType = new ComboViewer(compositeAreaType, SWT.NONE);
		Combo comboAreaType = comboViewerAreaType.getCombo();
		comboAreaType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboAreaType.setSize(198, 22);
		comboViewerAreaType.setContentProvider(ArrayContentProvider.getInstance());
		comboViewerAreaType.setLabelProvider(new ILocalizedEnumLabelProvider());
		comboViewerAreaType.setInput(AreaType.values());
		comboViewerAreaType.setSelection(new StructuredSelection(AreaType.GENERIC));
		comboViewerAreaType.addSelectionChangedListener(sc -> {
			if (AreaType.CONTACT.equals(comboViewerAreaType.getStructuredSelection().getFirstElement())) {
				if (linkAreaTypeValue.getText().length() == 0) {
					linkAreaTypeValue.setText("<a>select</a>");
				}
			} else {
				linkAreaTypeValue.setText(StringUtils.EMPTY);
			}
		});

		final MouseAdapter contactSelectorMouseListener = new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (AreaType.CONTACT.equals(comboViewerAreaType.getStructuredSelection().getFirstElement())) {
					KontaktSelektor ks = new KontaktSelektor(UiDesk.getTopShell(), Anwender.class,
							"Bitte Kontakt auswählen", "Selektieren Sie den zugeordneten Kontakt", null);
					int retVal = ks.open();
					if (retVal == Dialog.OK) {
						String area = (String) listViewerArea.getStructuredSelection().getFirstElement();
						Anwender anwender = (Anwender) ks.getSelection();
						AppointmentServiceHolder.get().setAreaType(area, AreaType.CONTACT, anwender.getId());
						linkAreaTypeValue.setText("<a>" + anwender.getLabel() + "</a>");
					}
				}
			}
		};
		Link farbenSelektorLink = new Link(compAreas, SWT.NONE);
		farbenSelektorLink.setText("<a>" + Messages.AgendaFarben_Link + "</a>");
		farbenSelektorLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FarbenSelektor farbenSelektor = new FarbenSelektor(UiDesk.getTopShell());
				if (farbenSelektor.open() == Dialog.OK) {
					listViewerArea.refresh();
				}
			}
		});

		linkAreaTypeValue = new Link(compositeAreaType, SWT.NONE);
		linkAreaTypeValue.addMouseListener(contactSelectorMouseListener);
		linkAreaTypeValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		linkAreaTypeValue.setSize(59, 14);

		Composite compAppointmentTypes = new Composite(container, SWT.NONE);
		compAppointmentTypes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compAppointmentTypes.setLayout(new GridLayout(1, false));

		Label lblLblappointmenttypestitle = new Label(compAppointmentTypes, SWT.NONE);
		lblLblappointmenttypestitle.setText(Messages.AgendaDefinitionen_enterTypes);

		listViewerAppointmentTypes = new ListViewer(compAppointmentTypes, SWT.BORDER | SWT.V_SCROLL);
		List listAppointmentTypes = listViewerAppointmentTypes.getList();
		GridData gd_listAppointmentTypes = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_listAppointmentTypes.heightHint = 60;
		listAppointmentTypes.setLayoutData(gd_listAppointmentTypes);
		listViewerAppointmentTypes.setContentProvider(ArrayContentProvider.getInstance());
		listViewerAppointmentTypes.setLabelProvider(new LabelProvider());

		MenuManager listViewerAppointmentTypesMenuManager = new MenuManager();
		listViewerAppointmentTypesMenuManager
				.add(new MoveEntryWithinListAction(listViewerAppointmentTypes, appointmentTypes, true));
		listViewerAppointmentTypesMenuManager
				.add(new MoveEntryWithinListAction(listViewerAppointmentTypes, appointmentTypes, false));
		listViewerAppointmentTypesMenuManager.add(new Separator());
		listViewerAppointmentTypesMenuManager
				.add(new AddStringEntryAction(listViewerAppointmentTypes, appointmentTypes));
		listViewerAppointmentTypesMenuManager
				.add(new RemoveSelectedEntriesAction(listViewerAppointmentTypes, appointmentTypes));
		Menu listViewerAppointmentTypesmenu = listViewerAppointmentTypesMenuManager
				.createContextMenu(listAppointmentTypes);
		listAppointmentTypes.setMenu(listViewerAppointmentTypesmenu);

		Composite compAppointmentStatus = new Composite(container, SWT.NONE);
		compAppointmentStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compAppointmentStatus.setLayout(new GridLayout(1, false));

		Label lblAppointmenStatusTitle = new Label(compAppointmentStatus, SWT.NONE);
		lblAppointmenStatusTitle.setText(Messages.AgendaDefinitionen_states);

		listViewerAppointmentStatus = new ListViewer(compAppointmentStatus, SWT.BORDER | SWT.V_SCROLL);
		List listAppointmentStatus = listViewerAppointmentStatus.getList();
		GridData gd_listAppointmentStatus = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_listAppointmentStatus.heightHint = 60;
		listAppointmentStatus.setLayoutData(gd_listAppointmentStatus);
		listViewerAppointmentStatus.setContentProvider(ArrayContentProvider.getInstance());
		listViewerAppointmentStatus.setLabelProvider(new LabelProvider());

		MenuManager listViewerAppointmentStatusMenuManager = new MenuManager();
		listViewerAppointmentStatusMenuManager
				.add(new MoveEntryWithinListAction(listViewerAppointmentStatus, appointmentStatus, true));
		listViewerAppointmentStatusMenuManager
				.add(new MoveEntryWithinListAction(listViewerAppointmentStatus, appointmentStatus, false));
		listViewerAppointmentStatusMenuManager.add(new Separator());
		listViewerAppointmentStatusMenuManager
				.add(new AddStringEntryAction(listViewerAppointmentStatus, appointmentStatus));
		listViewerAppointmentStatusMenuManager
				.add(new RemoveSelectedEntriesAction(listViewerAppointmentStatus, appointmentStatus));
		Menu listViewerAppointmentStatusmenu = listViewerAppointmentStatusMenuManager
				.createContextMenu(listAppointmentStatus);
		listAppointmentStatus.setMenu(listViewerAppointmentStatusmenu);

		Label separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridData separatorGridData = new GridData();
		separatorGridData.horizontalSpan = 3;
		separatorGridData.grabExcessHorizontalSpace = true;
		separatorGridData.horizontalAlignment = GridData.FILL;
		separatorGridData.verticalIndent = 0;
		separator.setLayoutData(separatorGridData);

		btnAvoidDoubleBooking = new Button(container, SWT.CHECK);
		btnAvoidDoubleBooking.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
		btnAvoidDoubleBooking.setText(Messages.AgendaDefinitionen_AvoidPatientDoubleBooking);
		btnAvoidDoubleBooking.setSelection(CoreHub.localCfg.get(PreferenceConstants.AG_AVOID_PATIENT_DOUBLE_BOOKING,
				PreferenceConstants.AG_AVOID_PATIENT_DOUBLE_BOOKING_DEFAULT));
		Label spacing = new Label(container, SWT.NONE);
		emailConfirmationsGroup(container);
		loadCombos();
		refresh();

		return container;
	}

	private void refresh() {
		listViewerArea.setInput(areas);
		listViewerAppointmentTypes.setInput(appointmentTypes);
		listViewerAppointmentStatus.setInput(appointmentStatus);
	}

	@Override
	protected void performApply() {
		ConfigServiceHolder.setGlobalAsList(PreferenceConstants.AG_BEREICHE, areas);
		ConfigServiceHolder.setGlobalAsList(PreferenceConstants.AG_TERMINTYPEN, appointmentTypes);
		ConfigServiceHolder.setGlobalAsList(PreferenceConstants.AG_TERMINSTATUS, appointmentStatus);
		CoreHub.localCfg.set(PreferenceConstants.AG_AVOID_PATIENT_DOUBLE_BOOKING, btnAvoidDoubleBooking.getSelection());
		CoreHub.localCfg.flush();

		super.performApply();
	}

	private void emailConfirmationsGroup(Composite parentComposite) {
		Group emailConfirmationsGroup = new Group(parentComposite, SWT.NONE);
		emailConfirmationsGroup.setText(Messages.AgendaStandard_E_Mail_Title);
		emailConfirmationsGroup.setLayout(new GridLayout(2, false));
		emailConfirmationsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		new Label(emailConfirmationsGroup, SWT.NONE).setText(Messages.AgendaStandard_E_Mail);
		accountsViewer = new ComboViewer(emailConfirmationsGroup);
		accountsViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		accountsViewer.setContentProvider(ArrayContentProvider.getInstance());
		accountsViewer.setLabelProvider(new LabelProvider());
		Label emailTemplatesLabel = new Label(emailConfirmationsGroup, SWT.NONE);
		emailTemplatesLabel.setText(Messages.AgendaStandard_E_Mail_Template);
		emailTemplatesLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		LabelProvider templateLabelProvider = new LabelProvider() {
			@Override
			public String getText(Object element) {
				return getTemplateText(element);
			}
		};
		appointmentTemplatesViewer = new ComboViewer(emailConfirmationsGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		appointmentTemplatesViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		appointmentTemplatesViewer.setContentProvider(ArrayContentProvider.getInstance());
		appointmentTemplatesViewer.setLabelProvider(templateLabelProvider);
		appointmentTemplatesViewer.addSelectionChangedListener(event -> {
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			if (selection != null && !selection.isEmpty()) {
				Object element = selection.getFirstElement();
					templateLabelProvider.getText(element);
					selectedTemplateName = templateLabelProvider.getText(element);
				}
		});
		loadCombos();
	}

	private void loadCombos() {
		accountsInput = getSendMailAccounts();
		accountsInput.add(0, "");
		accountsViewer.setInput(accountsInput);
		combined = new ArrayList<>();
		combined.add("");
		combined.addAll(MailTextTemplate.load());
		appointmentTemplatesViewer.setInput(combined);
		appointmentTemplatesViewer.refresh();
		loadSavedPreferences();
	}

	private java.util.List<String> getSendMailAccounts() {
		ret = new ArrayList<String>();
		accounts = MailClientComponent.getMailClient().getAccountsLocal();
		ret.addAll(accounts.stream().filter(aid -> MailClientComponent.getMailClient().getAccount(aid).isPresent())
				.filter(aid -> MailClientComponent.getMailClient().getAccount(aid).get().getType() == TYPE.SMTP)
				.collect(Collectors.toList()));
		accounts = MailClientComponent.getMailClient().getAccounts();
		ret.addAll(accounts.stream().filter(aid -> MailClientComponent.getMailClient().getAccount(aid).isPresent())
				.filter(aid -> MailClientComponent.getMailClient().getAccount(aid).get().getType() == TYPE.SMTP)
				.collect(Collectors.toList()));
		return ret;
	}

	@Override
	public boolean performOk() {
		IStructuredSelection selectedAccount = accountsViewer.getStructuredSelection();
		String selectedTemplate = selectedTemplateName;
		if (selectedAccount != null) {
			ConfigServiceHolder.get().set(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT_APPOINTMENT,
					(String) selectedAccount.getFirstElement());
		}
		if (selectedTemplate != null) {
			String template = selectedTemplateName;
			ConfigServiceHolder.get().set(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT_APPOINTMENT_TEMPLATE, template);
		}
		return super.performOk();
	}

	private void loadSavedPreferences() {
		String savedAccount = ConfigServiceHolder.get().get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT_APPOINTMENT,
				null);
		if (savedAccount != null) {
			accountsViewer.setSelection(new StructuredSelection(savedAccount));
		}
		String savedTemplate = ConfigServiceHolder.get()
				.get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT_APPOINTMENT_TEMPLATE, null);
		if (savedTemplate != null) {
			templates = MailTextTemplate.load();
			for (ITextTemplate template : templates) {
				if (savedTemplate.equals(template.getName())) {
					appointmentTemplatesViewer.setSelection(new StructuredSelection(template));
					break;
				}
			}
		}
	}

	private String getTemplateText(Object element) {
		if (element instanceof ITextTemplate) {
			ITextTemplate template = (ITextTemplate) element;
			String mandatorLabel = (template.getMandator() != null) ? " (" + template.getMandator().getLabel() + ")"
					: StringUtils.EMPTY;
			return template.getName() + mandatorLabel;
		}
		return "";
	}

	/**
	 * Initialize the preference page.
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Initialize the preference page
	}
}
