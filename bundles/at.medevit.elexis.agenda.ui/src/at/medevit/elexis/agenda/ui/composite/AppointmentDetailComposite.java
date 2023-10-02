package at.medevit.elexis.agenda.ui.composite;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.handlers.IHandlerService;

import ch.elexis.core.mail.MailTextTemplate;
import ch.elexis.core.mail.PreferenceConstants;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.ITextTemplate;
import ch.elexis.core.services.IAppointmentService;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.e4.fieldassist.AsyncContentProposalProvider;
import ch.elexis.core.ui.e4.fieldassist.IdentifiableContentProposal;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.medelexis.pea.PeaService;

public class AppointmentDetailComposite extends Composite {

	private IAppointment appointment;

	@Inject
	private IAppointmentService appointmentService;

	@Inject
	private IConfigService configService;

	@Inject
	private ECommandService commandService;

	@Inject
	private EHandlerService handlerService;

	private CDateTime txtDateFrom;
	private CDateTime txtDateFromDrop;
	private CDateTime txtDateFromNoDrop;
	private Button btnIsAllDay, chkEmail, chkEmailDialog;
	private boolean isEmailConfigured, isPatientSelected, hasEmail;
	private CDateTime txtTimeFrom;
	private Spinner txtDuration;
	private CDateTime txtTimeTo;
	private Label emailTemplatesLabel;
	private Composite compContext;
	private Text txtContact;
	private CDateTime pickerContext;
	private ComboViewer emailTemplatesViewer;
	private Combo comboArea;
	private Combo comboType;
	private Combo comboStatus;
	private Text txtReason;
	private Text txtPatSearch;
	private DayOverViewComposite dayBar;
	private TableViewer appointmentsViewer;
	private ITextTemplate previousSelection = null;
	private static final String INVALID_PEA_URL = "https://pea.myelexis.ch?sid=null";
	private Label emailHyperlink;
	private IContact selectedContact;
	private String peaUrl;
	private Date appointmentStartDate;
	private Date combinedDateTime;
	Color blue = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
	SelectionAdapter dateTimeSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			updateDateTimeFields(e.getSource());
		}
	};

	private void updateDateTimeFields(Object source) {
		Date dateFrom = txtDateFrom.getSelection();
		Date timeFrom = txtTimeFrom.getSelection();
		Date timeTo = txtTimeTo.getSelection();
		int duration = txtDuration.getSelection();
		LocalDateTime dateTimeFrom = LocalDateTime.of(dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
				timeFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
		LocalDateTime dateTimeEnd = LocalDateTime.of(dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
				timeTo.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());

		if (source != null && (source.equals(txtDuration) || source.equals(txtTimeFrom))) {
			txtTimeTo.setSelection(Date
					.from(ZonedDateTime.of(dateTimeFrom.plusMinutes(duration), ZoneId.systemDefault()).toInstant()));
		} else if (source != null && (source.equals(txtTimeTo))) {
			txtDuration.setSelection((int) dateTimeFrom.until(dateTimeEnd, ChronoUnit.MINUTES));
		}
		// apply changes to model
		dayBar.set();
	}

	public AppointmentDetailComposite(Composite parent, int style, IAppointment appointment) {
		super(parent, style);
		CoreUiUtil.injectServicesWithContext(this);
		this.appointment = appointment;
		setLayout(new GridLayout(2, false));
		setLayoutData(new GridData(GridData.FILL_BOTH));
		createContents(this);
	}

	private void createContents(Composite parent) {

		Objects.requireNonNull(appointment, "Appointment cannot be null"); //$NON-NLS-1$
		Optional<PeaService> peaService = OsgiServiceUtil.getService(PeaService.class);

		peaUrl = INVALID_PEA_URL;

		if (peaService.isPresent()) {
//			peaService.get().initializeService(new NullProgressMonitor());
			peaUrl = peaService.get().getPeaUrl();
		}
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(4, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		new Label(container, SWT.NULL).setText(Messages.AppointmentDetailComposite_search);
		txtPatSearch = new Text(container, SWT.SEARCH | SWT.ICON_SEARCH);
		txtPatSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtPatSearch.setMessage(Messages.AppointmentDetailComposite_name_birthday_patNr_or_free);
		AsyncContentProposalProvider<IPatient> aopp = new AsyncContentProposalProvider<IPatient>("description1", //$NON-NLS-1$
				"description2", "dob", "code") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			@Override
			public IQuery<IPatient> createBaseQuery() {
				return CoreModelServiceHolder.get().getQuery(IPatient.class);
			}

			@Override
			protected boolean isPatientQuery() {
				return true;
			}

			@Override
			public Text getWidget() {
				return txtPatSearch;
			}
		};
		txtPatSearch.setData(reloadAsPatient(Optional.ofNullable(appointment.getContact())).orElse(null));
		txtPatSearch.setTextLimit(80);
		ControlDecoration controlDecoration = new ControlDecoration(txtPatSearch, SWT.LEFT | SWT.TOP);
		controlDecoration.setDescriptionText(Messages.AppointmentDetailComposite_search_contact_via_fields);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION);
		controlDecoration.setImage(fieldDecoration.getImage());
		controlDecoration.show();
		controlDecoration.setShowHover(true);
		txtPatSearch.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				controlDecoration.showHoverText(Messages.AppointmentDetailComposite_search_contact_via_fields_hover);
			}
		});

		ContentProposalAdapter cppa = new ContentProposalAdapter(txtPatSearch, new TextContentAdapter(), aopp, null,
				null);
		aopp.configureContentProposalAdapter(cppa);

		cppa.addContentProposalListener(new IContentProposalListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void proposalAccepted(IContentProposal proposal) {
				IdentifiableContentProposal<IPatient> prop = (IdentifiableContentProposal<IPatient>) proposal;
				txtPatSearch.setText(prop.getLabel());
				txtPatSearch.setData(prop.getIdentifiable());
				appointment.setSubjectOrPatient(prop.getIdentifiable().getId());
				refreshPatientModel();
				updateEmailControlsStatus();
			}
		});
		txtPatSearch.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				reloadContactLabel();
				if (!txtDataIsMatchingContact() || StringUtils.isBlank(txtPatSearch.getText())) {
					txtPatSearch.setData(null);
				}
				if (!txtDataIsMatchingContact()) {
					appointmentsViewer.setInput(Collections.emptyList());
				}
				updateEmailControlsStatus();
				Object data = txtPatSearch.getData();
				if (data instanceof IPatient) {
					IPatient selectedPatient = (IPatient) data;
					if (selectedPatient != null) {
						ContextServiceHolder.get().setActivePatient(selectedPatient);
					}
				}
			}
		});

		cppa.addContentProposalListener(new IContentProposalListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void proposalAccepted(IContentProposal proposal) {
				IdentifiableContentProposal<IPatient> prop = (IdentifiableContentProposal<IPatient>) proposal;
				txtPatSearch.setText(prop.getLabel());
				txtPatSearch.setData(prop.getIdentifiable());
				appointment.setSubjectOrPatient(prop.getIdentifiable().getId());
				refreshPatientModel();
				updateEmailControlsStatus();
			}
		});

		Button btnExpand = new Button(container, SWT.TOGGLE);
		btnExpand.setImage(Images.IMG_NEW.getImage());
		btnExpand.setToolTipText(Messages.AppointmentDetailComposite_expand_hover);
		btnExpand.setText(Messages.AppointmentDetailComposite_expand);
		compContext = new Composite(container, SWT.NONE);
		compContext.setLayout(new GridLayout(1, false));
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		compContext.setLayoutData(gd);

		txtContact = new Text(compContext, SWT.BORDER | SWT.MULTI);
		txtContact.setText(StringUtils.EMPTY);
		txtContact.setBackground(compContext.getBackground());
		txtContact.setEditable(false);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		txtContact.setLayoutData(gd);

		pickerContext = new CDateTime(compContext, CDT.BORDER | CDT.SIMPLE);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		pickerContext.setLayoutData(gd);
		pickerContext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				txtDateFrom.setSelection(pickerContext.getSelection());
				setCompTimeToModel();
				loadCompTimeFromModel();
				dayBar.refresh();

				updateCombinedDateTime();
			}
		});

		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gd.verticalIndent = 15;
		Composite compDateArea = createUIDateAreaContents(gd, container);

		Group compContentMiddle = new Group(compDateArea, SWT.BORDER);
		compContentMiddle.setLayout(new GridLayout(4, false));
		compContentMiddle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		compContentMiddle.setText(Messages.AppointmentDetailComposite_planned_dates);

		appointmentsViewer = new TableViewer(compContentMiddle, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData listGd = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 3);
		appointmentsViewer.getControl().setLayoutData(listGd);
		appointmentsViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object sel = event.getStructuredSelection().getFirstElement();
				if (dayBar != null && sel instanceof IAppointment && !sel.equals(appointment)) {
					reloadAppointment((IAppointment) sel);
					ContextServiceHolder.get().getRootContext().setTyped(sel);
				}
			}
		});
		appointmentsViewer.setContentProvider(ArrayContentProvider.getInstance());
		appointmentsViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IAppointment) element).getLabel();
			}
		});
		Button btnAdd = new Button(compContentMiddle, SWT.NULL);
		btnAdd.setText(Messages.AppointmentDetailComposite_insert);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CoreModelServiceHolder.get().save(setToModel());
				cloneAndReloadAppointment();
				// cannot query this appointment with subjectOrPatient empty
				// same handling as legacy implementation
				if (appointment.getSubjectOrPatient() == null || appointment.getSubjectOrPatient().isEmpty()) {
					loadAppointmentsForPatient();
				}
			}
		});
		Button btnDelete = new Button(compContentMiddle, SWT.NULL);
		btnDelete.setText(Messages.AppointmentDetailComposite_delete);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CoreModelServiceHolder.get().delete(appointment);
				appointmentsViewer.remove(appointment);
				cloneAndReloadAppointment();
			}
		});
		Button btnPrint = new Button(compContentMiddle, SWT.NULL);
		btnPrint.setText(Messages.AppointmentDetailComposite_print);
		btnPrint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getAppointmentContact().ifPresent(contact -> {
					List<IAppointment> appointments = loadAppointments(contact).stream()
							.sorted(Comparator.comparing(IAppointment::getStartTime).reversed())
							.collect(Collectors.toList());

					ParameterizedCommand command = commandService.createCommand(
							"ch.elexis.agenda.commands.printAppointmentLabel", //$NON-NLS-1$
							Collections.singletonMap("ch.elexis.agenda.param.appointmentids", //$NON-NLS-1$
									appointments.stream().map(t -> t.getId()).collect(Collectors.joining(",")))); //$NON-NLS-1$
					handlerService.executeHandler(command);
				});
			}
		});
		Group compTimeSelektor = new Group(container, SWT.SHADOW_ETCHED_IN);
		compTimeSelektor.setLayout(new GridLayout(1, false));
		// FIXME works only in windows, in RAP not available
		compTimeSelektor.setTextDirection(SWT.CENTER);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		compTimeSelektor.setLayoutData(gd);

		dayBar = new DayOverViewComposite(compTimeSelektor, appointment, txtTimeFrom, txtTimeTo, txtDuration);
		dayBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		btnExpand.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				toggleVisiblityComposite(compContext);
				toggleVisiblityComposite(compContentMiddle);
				toggleVisiblityComposite(compTimeSelektor);
				getParent().pack();
				refreshPatientModel();
				dayBar.refresh();
			}
		});

		Composite compTypeReason = new Composite(container, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		compTypeReason.setLayoutData(gd);
		compTypeReason.setLayout(new GridLayout(2, false));

		Label lblType = new Label(compTypeReason, SWT.NULL);
		lblType.setText(Messages.AppointmentDetailComposite_date_type_or_status);

		Label lblReason = new Label(compTypeReason, SWT.NULL);
		lblReason.setText(Messages.AppointmentDetailComposite_reason);

		comboType = new Combo(compTypeReason, SWT.DROP_DOWN);
		comboType.setItems(appointmentService.getTypes().toArray(new String[appointmentService.getTypes().size()]));
		comboType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Map<String, Integer> preferredDurations = AppointmentServiceHolder.get()
						.getPreferredDurations(comboArea.getText());
				String selectedType = comboType.getText();
				if (preferredDurations.containsKey(selectedType)) {
					txtDuration.setSelection(preferredDurations.get(selectedType));
					updateDateTimeFields(txtDuration);
				}
			};
		});
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd.widthHint = 80;
		comboType.setLayoutData(gd);

		txtReason = new Text(compTypeReason, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		txtReason.setLayoutData(gd);

		comboStatus = new Combo(compTypeReason, SWT.DROP_DOWN);
		comboStatus.setItems(appointmentService.getStates().toArray(new String[appointmentService.getStates().size()]));
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd.widthHint = 80;
		comboStatus.setLayoutData(gd);
		Composite compCheckbox = new Composite(container, SWT.NONE);
		GridData gdCheckbox = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		compCheckbox.setLayoutData(gdCheckbox);
		compCheckbox.setLayout(new GridLayout(5, false));

		chkEmail = new Button(compCheckbox, SWT.CHECK);
		chkEmail.setText("Terminbestätigung per Mail senden: ");
		chkEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		chkEmail.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selected = chkEmail.getSelection();
				emailTemplatesViewer.getControl().setEnabled(selected);
				emailTemplatesLabel.setEnabled(selected);
				emailHyperlink.setEnabled(selected);
			}
		});

		// Ein leeres Label als Abstandshalter
		new Label(compCheckbox, SWT.NONE).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		// Erstellen Sie ein neues Label neben dem ComboViewer
		emailTemplatesLabel = new Label(compCheckbox, SWT.NONE);
		emailTemplatesLabel.setText("E-Mail Vorlagen: ");
		emailTemplatesLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		// Ihr bereits vorhandener Code für den ComboViewer
		emailTemplatesViewer = new ComboViewer(compCheckbox, SWT.DROP_DOWN | SWT.READ_ONLY);
		emailTemplatesViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		emailTemplatesViewer.setContentProvider(new ArrayContentProvider());
		emailTemplatesViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ITextTemplate) {
					return ((ITextTemplate) element).getName() + (((ITextTemplate) element).getMandator() != null
							? " (" + ((ITextTemplate) element).getMandator().getLabel() + ")"
							: StringUtils.EMPTY);
				}
				return super.getText(element);
			}
		});

		emailTemplatesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object selectedElement = selection.getFirstElement();

				if (selectedElement instanceof ITextTemplate) {
					ITextTemplate template = (ITextTemplate) selectedElement;

					if ("Terminbestätigung inkl. Anmeldeformular".equals(template.getName())
							&& INVALID_PEA_URL.equals(peaUrl)) {
						Shell shell = emailTemplatesViewer.getControl().getShell();

						MessageDialog dialog = new MessageDialog(shell, "Warnung", null,
								"Die Vorlage \"Terminbestätigung inkl. Anmeldeformular\" ist nur in Kombination mit der Online Patientenerfassung möglich.",
								MessageDialog.WARNING, new String[] { "OK" }, 0) {
							@Override
							protected Control createCustomArea(Composite parent) {
								Link link = new Link(parent, SWT.NONE);
								link.setText(
										"<a href=\"https://medelexis.ch/pea/\">Weiter Informationen finden Sie hier.</a>");
								link.addSelectionListener(new SelectionAdapter() {
									@Override
									public void widgetSelected(SelectionEvent e) {
										Program.launch("https://medelexis.ch/pea/");
									}
								});
								return link;
							}
						};

						dialog.open();

						// Setze die Auswahl immer zurück auf die vorherige Auswahl
						if (previousSelection != null) {
							emailTemplatesViewer.setSelection(new StructuredSelection(previousSelection), true);
						}
					} else {
						previousSelection = template;
					}
				}
			}
		});

		emailHyperlink = SWTHelper.createHyperlink(compCheckbox, "E-Mail bearbeiten", new HyperlinkAdapter() {
			@Override
			public void linkActivated(final HyperlinkEvent e) {
				sendEmail(false);
			}
		});

		emailHyperlink.setForeground(blue);

		updateTemplatesCombo();
		toggleVisiblityComposite(txtDateFromDrop);
		toggleVisiblityComposite(compContext);
		toggleVisiblityComposite(compContentMiddle);
		toggleVisiblityComposite(compTimeSelektor);

		loadFromModel();
		refreshPatientModel();
	}

	@SuppressWarnings("unchecked")
	private Optional<IContact> reloadAsPatient(Optional<IContact> contact) {
		if (contact.isPresent() && contact.get().isPatient()) {
			return (Optional<IContact>) (Optional<?>) CoreModelServiceHolder.get().load(contact.get().getId(),
					IPatient.class);
		}
		return contact;
	}

	private void reloadAppointment(IAppointment appointment) {
		this.appointment = appointment;
		loadFromModel();
		refreshPatientModel();
	}

	private void refreshPatientModel() {
		loadAppointmentsForPatient();
		if (dayBar != null) {
			dayBar.setAppointment(appointment);
			dayBar.refresh();
		}
		reloadContactLabel();
	}

	private void reloadContactLabel() {
		Optional<IContact> c = reloadAsPatient(getAppointmentContact());
		String currentSearchText = txtPatSearch.getText();
		if (c.isPresent() && c.get().getLabel().equals(currentSearchText)) {
			StringBuilder b = new StringBuilder();
			b.append(c.get().getDescription1());
			b.append(StringUtils.SPACE);
			b.append(c.get().getDescription2());
			b.append(StringUtils.SPACE);
			b.append(Optional.ofNullable(c.get().getDescription3()).orElse(StringUtils.EMPTY));
			if (c.get().isPatient()) {

				IPatient p = CoreModelServiceHolder.get().load(c.get().getId(), IPatient.class).get();
				if (p.getDateOfBirth() != null) {
					b.append(StringUtils.LF);
					b.append(p.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))); //$NON-NLS-1$
				}
			}
			b.append(StringUtils.LF);
			b.append(Optional.ofNullable(c.get().getMobile()).filter(i -> i != null && !i.isEmpty())
					.map(i -> StringUtils.LF + i).orElse(StringUtils.EMPTY));
			b.append(Optional.ofNullable(c.get().getPhone1()).filter(i -> i != null && !i.isEmpty())
					.map(i -> StringUtils.LF + i).orElse(StringUtils.EMPTY));
			b.append(Optional.ofNullable(c.get().getPhone2()).filter(i -> i != null && !i.isEmpty())
					.map(i -> StringUtils.LF + i).orElse(StringUtils.EMPTY));
			txtContact.setText(b.toString());
		} else {
			if (!c.isPresent() && StringUtils.isBlank(currentSearchText)) {
				txtContact.setText(Messages.AppointmentDetailComposite_no_patient_selected);
			} else {
				txtContact.setText(Messages.AppointmentDetailComposite_freetext + currentSearchText);
			}
		}
	}

	private Optional<IContact> getAppointmentContact() {
		return Optional.ofNullable(appointment.getContact());
	}

	private List<IAppointment> loadAppointments(IContact contact) {
		if (contact != null) {
			IQuery<IAppointment> query = CoreModelServiceHolder.get().getQuery(IAppointment.class);
			query.and("patId", COMPARATOR.EQUALS, contact.getId()); //$NON-NLS-1$
			query.and("tag", COMPARATOR.GREATER_OR_EQUAL, LocalDate.now()); //$NON-NLS-1$
			return query.execute();
		}
		return Collections.emptyList();
	}

	private void loadAppointmentsForPatient() {
		getAppointmentContact().ifPresent(contact -> {
			List<IAppointment> input = loadAppointments(contact).stream()
					.sorted(Comparator.comparing(IAppointment::getStartTime).reversed()).collect(Collectors.toList());
			appointmentsViewer.setInput(input);
			if (appointment != null
					&& !appointment.equals(appointmentsViewer.getStructuredSelection().getFirstElement())) {
				appointmentsViewer.setSelection(new StructuredSelection(appointment));
			}
			appointmentsViewer.refresh();
		});
	}

	private void toggleVisiblityComposite(Composite c) {
		GridData data = (GridData) c.getLayoutData();
		data.exclude = !data.exclude;
		c.setVisible(!data.exclude);

		if (c == compContext) {
			if (compContext.isVisible()) {
				toggleVisiblityComposite(txtDateFromNoDrop);
				toggleVisiblityComposite(txtDateFromDrop);
				txtDateFromNoDrop.setSelection(txtDateFromDrop.getSelection());
				pickerContext.setSelection(txtDateFromNoDrop.getSelection());
				txtDateFrom = txtDateFromNoDrop;

			} else {
				toggleVisiblityComposite(txtDateFromNoDrop);
				toggleVisiblityComposite(txtDateFromDrop);
				txtDateFromDrop.setSelection(txtDateFromNoDrop.getSelection());
				txtDateFrom = txtDateFromDrop;
			}
		}
	}

	private Composite createUIDateAreaContents(GridData gd, Composite container) {
		Composite compDateTime = new Composite(container, SWT.NULL);
		compDateTime.setLayoutData(gd);
		compDateTime.setLayout(new GridLayout(3, false));

		Label lblDateFrom = new Label(compDateTime, SWT.NULL);
		lblDateFrom.setText(Messages.AppointmentDetailComposite_tag);
		txtDateFromDrop = new CDateTime(compDateTime, CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_MEDIUM | CDT.TEXT_TRAIL);
		txtDateFromDrop.setPattern("EEE, dd.MM.yyyy "); //$NON-NLS-1$
		txtDateFromDrop.setLayoutData(new GridData());
		txtDateFromDrop.addSelectionListener(dateTimeSelectionAdapter);

		txtDateFromNoDrop = new CDateTime(compDateTime, CDT.BORDER | CDT.DATE_MEDIUM | CDT.TEXT_TRAIL);
		txtDateFromNoDrop.setPattern("EEE, dd.MM.yyyy "); //$NON-NLS-1$
		txtDateFromNoDrop.setLayoutData(new GridData());

		txtDateFromDrop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateCombinedDateTime();
				setCompTimeToModel();
				dayBar.refresh();
			}
		});
		txtDateFromNoDrop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateCombinedDateTime();
				setCompTimeToModel();
				dayBar.refresh();
			}
		});

		Composite compTime = new Composite(compDateTime, SWT.NONE);
		GridLayout gl = new GridLayout(8, false);
		compTime.setLayout(gl);
		compTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Label lblTimeFrom = new Label(compTime, SWT.NULL);
		lblTimeFrom.setText(Messages.AppointmentDetailComposite_starting_from);
		txtTimeFrom = new CDateTime(compTime, CDT.BORDER | CDT.TIME_SHORT | CDT.SPINNER);
		txtTimeFrom.addSelectionListener(dateTimeSelectionAdapter);

		Label lblDuration = new Label(compTime, SWT.NULL);
		lblDuration.setText(Messages.AppointmentDetailComposite_duration);
		txtDuration = new Spinner(compTime, SWT.BORDER);
		txtDuration.setValues(0, 0, 24 * 60, 0, 5, 10);
		txtDuration.addSelectionListener(dateTimeSelectionAdapter);

		Label lblTimeTo = new Label(compTime, SWT.NULL);
		lblTimeTo.setText(Messages.AppointmentDetailComposite_until);
		txtTimeTo = new CDateTime(compTime, CDT.BORDER | CDT.TIME_SHORT | CDT.SPINNER);
		txtTimeTo.addSelectionListener(dateTimeSelectionAdapter);
		txtTimeFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateCombinedDateTime();
				setCompTimeToModel();

			}
		});

		btnIsAllDay = new Button(compTime, SWT.CHECK);
		GridData btnIsAllDayGridData = new GridData();
		btnIsAllDayGridData.grabExcessHorizontalSpace = true;
		btnIsAllDayGridData.horizontalAlignment = SWT.LEFT;
		btnIsAllDay.setLayoutData(btnIsAllDayGridData);
		btnIsAllDay.setText(Messages.AppointmentDetailComposite_isAllDay);
		btnIsAllDay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtTimeFrom.setEnabled(!btnIsAllDay.getSelection());
				txtDuration.setEnabled(!btnIsAllDay.getSelection());
				txtTimeTo.setEnabled(!btnIsAllDay.getSelection());
			}
		});

		Composite compArea = new Composite(compTime, SWT.NONE);
		gl = new GridLayout(2, false);
		gl.marginLeft = 20;
		compArea.setLayout(gl);

		Label lblArea = new Label(compArea, SWT.NULL);
		lblArea.setText(Messages.AppointmentDetailComposite_range);
		comboArea = new Combo(compArea, SWT.DROP_DOWN);
		comboArea.setItems(configService.get("agenda/bereiche", "Praxis").split(",")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		comboArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!comboArea.getText().equals(appointment.getSchedule())) {
					appointment.setSchedule(comboArea.getText());
					dayBar.setAppointment(appointment);
					dayBar.refresh();
				}
			}
		});
		return compDateTime;
	}

	private void loadCompTimeFromModel() {
		appointmentStartDate = Date
				.from(ZonedDateTime.of(appointment.getStartTime(), ZoneId.systemDefault()).toInstant());

		txtDateFrom.setSelection(appointmentStartDate);
		pickerContext.setSelection(appointmentStartDate);
		txtTimeFrom.setSelection(appointmentStartDate);
		btnIsAllDay.setSelection(appointment.isAllDay());
		txtTimeFrom.setEnabled(!appointment.isAllDay());
		txtDuration.setEnabled(!appointment.isAllDay());
		txtTimeTo.setEnabled(!appointment.isAllDay());
		if (!appointment.isAllDay()) {
			Date appointmentEndDate = Date
					.from(ZonedDateTime.of(appointment.getEndTime(), ZoneId.systemDefault()).toInstant());
			txtTimeTo.setSelection(appointmentEndDate);
			txtDuration.setSelection(appointment.getDurationMinutes());
		}

	}

	private void loadFromModel() {
		comboStatus.setText(appointment.getState());
		comboType.setText(appointment.getType());
		comboArea.setText(appointment.getSchedule());

		txtReason.setText(appointment.getReason());
		txtPatSearch.setText(appointment.getSubjectOrPatient());

		loadCompTimeFromModel();
	}

	public void setCompTimeToModel() {
		Date dateFrom;
		if (txtDateFromDrop.isVisible()) {
			dateFrom = txtDateFromDrop.getSelection();
		} else {
			dateFrom = txtDateFrom.getSelection();
		}

		Date timeFrom = txtTimeFrom.getSelection();
		LocalDateTime dateTimeFrom = LocalDateTime.of(dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
				timeFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
		if (btnIsAllDay.getSelection()) {
			appointment.setStartTime(dateTimeFrom.toLocalDate().atStartOfDay());
			appointment.setEndTime(null);
		} else {
			Date timeTo = txtTimeTo.getSelection();
			LocalDateTime dateTimeTo = LocalDateTime.of(
					dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
					timeTo.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
			appointment.setStartTime(dateTimeFrom);
			appointment.setEndTime(dateTimeTo);
		}
	}

	public IAppointment setToModel() {
		setCompTimeToModel();
		appointment.setState(comboStatus.getText());
		appointment.setType(comboType.getText());
		appointment.setSchedule(comboArea.getText());

		appointment.setReason(txtReason.getText());

		if (txtDataIsMatchingContact()) {
			appointment.setSubjectOrPatient(((IContact) txtPatSearch.getData()).getId());
		} else if (StringUtils.isNotBlank(txtPatSearch.getText())) {
			appointment.setSubjectOrPatient(txtPatSearch.getText());
		}

		return appointment;
	}

	private boolean txtDataIsMatchingContact() {
		return txtPatSearch.getData() instanceof IContact
				&& ((IContact) txtPatSearch.getData()).getLabel().equals(txtPatSearch.getText());
	}

	private void cloneAndReloadAppointment() {
		IAppointment newAppointment = CoreModelServiceHolder.get().create(IAppointment.class);
		newAppointment.setSubjectOrPatient(appointment.getContact() != null ? appointment.getContact().getId()
				: appointment.getSubjectOrPatient());
		appointment = newAppointment;
		setToModel();
		reloadAppointment(appointment);
	}

	private void updateTemplatesCombo() {

		emailTemplatesViewer.setInput(MailTextTemplate.load());
		emailTemplatesViewer.refresh();

	}

	private void updateEmailControlsStatus() {

		isEmailConfigured = ConfigServiceHolder.get().get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT_APPOINTMENT,
				null) != null;

		if (!isEmailConfigured) {
			isEmailConfigured = ConfigServiceHolder.get().get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT,
					null) != null;
		}

		// Überprüfen, ob ein Patient im txtPatSearch-Steuerungselement ausgewählt ist
		IContact selectedContact = (IContact) txtPatSearch.getData();

		// Wenn kein Patient im txtPatSearch-Steuerungselement ausgewählt ist,
		// überprüfen Sie den aktuell gesetzten Patienten
		if (selectedContact == null) {
			Optional<IPatient> activePatientOptional = ContextServiceHolder.get().getActivePatient();
			if (activePatientOptional.isPresent()) {
				IPatient activePatient = activePatientOptional.get();
				if (activePatient instanceof IContact) {
					selectedContact = activePatient;
				}
			}

		}

		isPatientSelected = selectedContact != null;

		hasEmail = false;
		if (selectedContact != null) {
			String email = selectedContact.getEmail();
			hasEmail = email != null && !email.trim().isEmpty();
		}

		chkEmail.setEnabled(isEmailConfigured && isPatientSelected && hasEmail);
		emailTemplatesViewer.getControl()
				.setEnabled(chkEmail.getSelection() && isEmailConfigured && isPatientSelected && hasEmail);
		emailTemplatesLabel.setEnabled(chkEmail.getSelection() && isEmailConfigured && isPatientSelected && hasEmail);
		selectSavedEmailTemplate();
		emailHyperlink.setEnabled(chkEmail.getSelection() && isEmailConfigured && isPatientSelected && hasEmail);

	}

	private void selectSavedEmailTemplate() {
		String savedTemplate = ConfigServiceHolder.get()
				.get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT_APPOINTMENT_TEMPLATE, null);
		if (savedTemplate != null) {
			for (ITextTemplate template : (List<ITextTemplate>) emailTemplatesViewer.getInput()) {
				if (savedTemplate.equals(template.getName())) {
					emailTemplatesViewer.setSelection(new StructuredSelection(template));
					break;
				}
			}
		}
	}

	public void sendEmail(boolean autoSend) {

		isPatientSelected = txtPatSearch.getData() != null;
		if (isPatientSelected) {
			IContact selectedContact1 = (IContact) txtPatSearch.getData();
			String email = selectedContact1.getEmail();
			ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
			try {
				Command sendMailCommand = commandService.getCommand("ch.elexis.core.mail.ui.sendMail");
				HashMap<String, String> params = new HashMap<>();
				params.put("ch.elexis.core.mail.ui.sendMail.to", email);
				params.put("ch.elexis.core.mail.ui.sendMail.subject", "Terminbestätigung");
				ITextTemplate selectedTemplate = (ITextTemplate) getSelectedTemplate();
				String selectedTemplateText = (selectedTemplate != null) ? selectedTemplate.getName() : "";
				params.put("ch.elexis.core.mail.ui.sendMail.emailTemplate", selectedTemplateText);
				params.put("ch.elexis.core.mail.ui.sendMail.hideLabel", "true");
				String defaultMailAccountAppointment = ConfigServiceHolder.get()
						.get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT_APPOINTMENT, "");
				if (defaultMailAccountAppointment.isEmpty()) {
					defaultMailAccountAppointment = ConfigServiceHolder.get()
							.get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT, "");
				}
				params.put("ch.elexis.core.mail.ui.sendMail.account", defaultMailAccountAppointment);

				if (autoSend) {
					params.put("ch.elexis.core.mail.ui.sendMail.autoSend", "true");
				}
				updateCombinedDateTime();
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
				params.put("ch.elexis.core.mail.ui.sendMail.time", sdf.format(combinedDateTime));
				params.put("ch.elexis.core.mail.ui.sendMail.bereich", appointment.getSchedule());
				ParameterizedCommand parametrizedCommmand = ParameterizedCommand.generateCommand(sendMailCommand,
						params);
				PlatformUI.getWorkbench().getService(IHandlerService.class).executeCommand(parametrizedCommmand, null);
			} catch (Exception ex) {
				throw new RuntimeException("ch.elexis.core.mail.ui.sendMail not found", ex);
			}
		}
	}

	public Object getSelectedTemplate() {
		return (emailTemplatesViewer != null && emailTemplatesViewer.getStructuredSelection() != null)
				? emailTemplatesViewer.getStructuredSelection().getFirstElement()
				: null;
	}

	public boolean isEmailCheckboxSelected() {
		return chkEmail != null && chkEmail.getSelection();
	}

	private void updateCombinedDateTime() {
		Date selectedDateFromDrop = txtDateFromDrop.isVisible() ? txtDateFromDrop.getSelection()
				: txtDateFrom.getSelection();
		Date selectedTimeFrom = txtTimeFrom.getSelection();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(selectedDateFromDrop);
		Calendar timeCalendar = Calendar.getInstance();
		timeCalendar.setTime(selectedTimeFrom);
		calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		combinedDateTime = calendar.getTime();
	}
}
