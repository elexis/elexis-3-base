package ch.elexis.agenda.composite;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import ch.elexis.agenda.composite.EmailComposite.EmailDetails;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.agenda.ui.Messages;
import ch.elexis.agenda.util.AppointmentExtensionHandler;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPatient;
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
import ch.elexis.core.utils.CoreUtil;
import ch.rgw.tools.TimeTool;

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
	private Button btnIsAllDay;
	private CDateTime txtTimeFrom;
	private Spinner txtDuration;
	private CDateTime txtTimeTo;
	private Composite compContext;
	private Link txtContact;
	private DateTime pickerContext;
	private Combo comboArea;
	private Combo comboType;
	private Combo comboStatus;
	private Text txtReason;
	private Text txtPatSearch;
	private Text tBem;
	private DayOverViewComposite dayBar;
	private TableViewer appointmentsViewer;
	private Composite container;
	private EmailComposite emailComposite;
	private Button chkTerminLinks;
	private SashForm sash;
	private Composite leftPane;
	private Composite rightPane;
	private Button btnExpand;
	private Group compContentMiddle;
	private Group compTimeSelektor;
	private Label lblDateFrom;
	private Composite dateArea;

	private static final int[] SASH_WEIGHTS_EXPANDED = { 25, 75 };

	SelectionAdapter dateTimeSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			updateDateTimeFields(e.getSource());
		}
	};

	private static LocalDate toLocalDate(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	private static LocalTime toLocalTime(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
	}

	private void updateDateTimeFields(Object source) {
		Date dateFrom = txtDateFrom.getSelection();
		Date timeFrom = txtTimeFrom.getSelection();
		Date timeTo = txtTimeTo.getSelection();
		int duration = txtDuration.getSelection();

		LocalDate localDateFrom = toLocalDate(dateFrom);
		LocalTime localTimeFrom = toLocalTime(timeFrom);
		LocalTime localTimeTo = toLocalTime(timeTo);

		LocalDateTime dateTimeFrom = LocalDateTime.of(localDateFrom, localTimeFrom);
		LocalDateTime dateTimeTo = LocalDateTime.of(localDateFrom, localTimeTo);

		if (source == txtDuration || source == txtTimeFrom) {
			LocalDateTime newTimeTo = dateTimeFrom.plusMinutes(duration);
			txtTimeTo.setSelection(Date.from(newTimeTo.atZone(ZoneId.systemDefault()).toInstant()));
		} else if (source == txtTimeTo) {
			long diff = ChronoUnit.MINUTES.between(dateTimeFrom, dateTimeTo);
			txtDuration.setSelection((int) diff);
		}

		dayBar.set();
	}

	public AppointmentDetailComposite(Composite parent, int style, IAppointment appointment) {
		super(parent, style);
		CoreUiUtil.injectServicesWithContext(this);
		this.appointment = appointment;
		setLayout(new FillLayout());
		createContents(this);
	}

	private void createContents(Composite parent) {
		requireAppointment();
		initContainer(parent);
		createHeader(container);
		initSashForm(container);
		createContextComposite(container);
		dateArea = createUIDateAreaContents(getDateAreaGridData(), rightPane);
		createAppointmentsViewer(dateArea);
		createTimeSelector(container);
		createTypeAndReasonComposite(container);
		createEmailComposite(container);
		txtDateFrom = txtDateFromDrop;
		loadFromModel();
		refreshPatientModel();
		finalizeLayout();

	}

	private void requireAppointment() {
		Objects.requireNonNull(appointment, "Appointment cannot be null"); //$NON-NLS-1$
	}

	private void initContainer(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(1, false);
		gl.marginWidth = 6;
		gl.marginHeight = 6;
		gl.verticalSpacing = 6;
		container.setLayout(gl);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
	}

	private void createHeader(Composite parent) {
		Composite header = new Composite(parent, SWT.NONE);
		header.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		header.setLayout(new GridLayout(4, false));

		new Label(header, SWT.NONE).setText(Messages.AppointmentDetailComposite_search);
		createPatientSearch(header);

		btnExpand = new Button(header, SWT.TOGGLE);
		btnExpand.setImage(Images.IMG_NEW.getImage());
		btnExpand.setToolTipText(Messages.AppointmentDetailComposite_expand_hover);
		btnExpand.setText(Messages.AppointmentDetailComposite_expand);
		btnExpand.addSelectionListener(expandListener());
	}

	private void createPatientSearch(Composite parent) {
		txtPatSearch = new Text(parent, SWT.SEARCH | SWT.ICON_SEARCH);
		txtPatSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtPatSearch.setMessage(Messages.AppointmentDetailComposite_name_birthday_patNr_or_free);
		txtPatSearch.setTextLimit(80);
		txtPatSearch.setData(reloadAsPatient(Optional.ofNullable(appointment.getContact())).orElse(null));

		ControlDecoration deco = new ControlDecoration(txtPatSearch, SWT.LEFT | SWT.TOP);
		deco.setDescriptionText(Messages.AppointmentDetailComposite_search_contact_via_fields);
		deco.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
				.getImage());
		deco.show();
		txtPatSearch.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				deco.showHoverText(Messages.AppointmentDetailComposite_search_contact_via_fields_hover);
			}
		});

		AsyncContentProposalProvider<IPatient> provider = new PatientContentProposalProvider();
		ContentProposalAdapter adapter = new ContentProposalAdapter(txtPatSearch, new TextContentAdapter(), provider,
				null, null);
		provider.configureContentProposalAdapter(adapter);

		adapter.addContentProposalListener(proposal -> {
			@SuppressWarnings("unchecked")
			IdentifiableContentProposal<IPatient> prop = (IdentifiableContentProposal<IPatient>) proposal;
			txtPatSearch.setText(prop.getLabel());
			txtPatSearch.setData(prop.getIdentifiable());
			appointment.setSubjectOrPatient(prop.getIdentifiable().getId());
			refreshPatientModel();
		});

		txtPatSearch.addModifyListener(e -> {
			onPatientSearchModify();
		});
	}

	private class PatientContentProposalProvider extends AsyncContentProposalProvider<IPatient> {
		PatientContentProposalProvider() {
			super("description1", "description2", "dob", "code"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}

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
	}

	private void onPatientSearchModify() {
		reloadContactLabel();
		String searchText = txtPatSearch.getText();
		if (StringUtils.isBlank(searchText)) {
			txtPatSearch.setData(null);
			tBem.setText(StringUtils.EMPTY);
			if (appointmentsViewer != null) {
				appointmentsViewer.setInput(Collections.emptyList());
			}
		} else if (!txtDataIsMatchingContact()) {
			txtPatSearch.setData(null);
			tBem.setText(StringUtils.EMPTY);
			if (appointmentsViewer != null) {
				appointmentsViewer.setInput(Collections.emptyList());
			}
		}
		emailComposite.updateEmailControlsStatus(getSelectedContact());
	}

	private SelectionAdapter expandListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setExpanded(btnExpand.getSelection());
				getShell().pack();
			}
		};
	}

	private void initSashForm(Composite parent) {
		sash = new SashForm(parent, SWT.HORIZONTAL);
		sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		sash.setSashWidth(4);
		sash.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));

		leftPane = new Composite(sash, SWT.NONE);
		leftPane.setLayout(new GridLayout(1, false));

		rightPane = new Composite(sash, SWT.NONE);
		rightPane.setLayout(new GridLayout(1, false));
	}

	private void createContextComposite(Composite parent) {
		compContext = new Composite(parent, SWT.NONE);
		compContext.setLayout(new GridLayout(1, false));
		compContext.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		txtContact = new Link(leftPane, SWT.WRAP);
		txtContact.setBackground(leftPane.getBackground());
		txtContact.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtContact.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					String target = e.text;
					if (target != null && target.startsWith("tel:")) {
						String num = target.substring(4);
						num = num.replaceAll("[^+\\d]", "");
						num = num.replaceFirst("^00", "+");
						target = "tel:" + num;
					}
					java.awt.Desktop.getDesktop().browse(new java.net.URI(target));
				} catch (Exception ex) {
					SWTHelper.alert("Fehler", "Kann Ruf-Aufruf nicht starten:\n" + ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		});
		Composite calWrapper = new Composite(leftPane, SWT.NONE);
		calWrapper.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		calWrapper.setLayout(new FillLayout());


		pickerContext = new DateTime(calWrapper, SWT.CALENDAR | SWT.CALENDAR_WEEKNUMBERS | SWT.BORDER);
		pickerContext.setLayout(new GridLayout(1, false));
		pickerContext.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		pickerContext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onContextDateSelected();
			}
		});

	}

	private Date getDateFromCalendar() {
		int y = pickerContext.getYear();
		int mZeroBased = pickerContext.getMonth();
		int d = pickerContext.getDay();
		LocalDate ld = LocalDate.of(y, mZeroBased + 1, d);
		return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	private void onContextDateSelected() {
		txtDateFrom.setSelection(getDateFromCalendar());
		setCompTimeToModel();
		loadCompTimeFromModel();
		dayBar.refresh();
	}

	private GridData getDateAreaGridData() {
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 3);
		gd.verticalIndent = 15;
		return gd;
	}

	private void createAppointmentsViewer(Composite parent) {
		compContentMiddle = new Group(parent, SWT.NONE);
		compContentMiddle.setText(Messages.AppointmentDetailComposite_planned_dates);
		compContentMiddle.setLayout(new GridLayout(2, false));
		compContentMiddle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		appointmentsViewer = new TableViewer(compContentMiddle, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gdTable = new GridData(SWT.FILL, SWT.FILL, true, true);

		appointmentsViewer.getControl().setLayoutData(gdTable);
		appointmentsViewer.setContentProvider(ArrayContentProvider.getInstance());
		appointmentsViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IAppointment) element).getLabel();
			}
		});
		appointmentsViewer.addSelectionChangedListener(event -> {
			Object sel = ((StructuredSelection) event.getSelection()).getFirstElement();
			if (sel instanceof IAppointment && !sel.equals(appointment)) {
				reloadAppointment((IAppointment) sel);
				ContextServiceHolder.get().getRootContext().setTyped(sel);
			}
		});
		Composite btnCol = new Composite(compContentMiddle, SWT.NONE);
		btnCol.setLayout(new GridLayout(1, true));
		btnCol.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		createControlButtons(btnCol);
	}

	private void createControlButtons(Composite parent) {
		GridData gdButton = new GridData(SWT.FILL, SWT.TOP, true, false);

		Button btnAdd = new Button(parent, SWT.PUSH);
		btnAdd.setText(Messages.AppointmentDetailComposite_insert);
		btnAdd.setLayoutData(gdButton);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CoreModelServiceHolder.get().save(setToModel());
				cloneAndReloadAppointment();
				if (StringUtils.isEmpty(appointment.getSubjectOrPatient())) {
					loadAppointmentsForPatient();
				}
				ContextServiceHolder.get().getRootContext().setTyped(appointment);
			}
		});

		Button btnDelete = new Button(parent, SWT.PUSH);
		btnDelete.setText(Messages.AppointmentDetailComposite_delete);
		btnDelete.setLayoutData(gdButton);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CoreModelServiceHolder.get().delete(appointment);
				appointmentsViewer.remove(appointment);
				cloneAndReloadAppointment();
			}
		});

		Button btnPrint = new Button(parent, SWT.PUSH);
		btnPrint.setText(Messages.AppointmentDetailComposite_print);
		btnPrint.setLayoutData(gdButton);
		btnPrint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getAppointmentContact().ifPresent(contact -> printLabelsFor(contact));
			}
		});
	}

	private void printLabelsFor(IContact contact) {
		List<IAppointment> appts = loadAppointments(contact).stream()
				.sorted(Comparator.comparing(IAppointment::getStartTime).reversed()).collect(Collectors.toList());
		String ids = appts.stream().map(IAppointment::getId).collect(Collectors.joining(",")); //$NON-NLS-1$
		ParameterizedCommand cmd = commandService.createCommand("ch.elexis.agenda.commands.printAppointmentLabel", //$NON-NLS-1$
				Collections.singletonMap("ch.elexis.agenda.param.appointmentids", ids)); //$NON-NLS-1$
		handlerService.executeHandler(cmd);
	}

	private void createTimeSelector(Composite parent) {
		compTimeSelektor = new Group(parent, SWT.SHADOW_ETCHED_IN);
		compTimeSelektor.setTextDirection(SWT.CENTER);
		compTimeSelektor.setLayout(new GridLayout(1, false));
		compTimeSelektor.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1));
		dayBar = new DayOverViewComposite(compTimeSelektor, appointment, txtTimeFrom, txtTimeTo, txtDuration);
		dayBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}

	private void createTypeAndReasonComposite(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		comp.setLayout(new GridLayout(2, false));
		new Label(comp, SWT.NONE).setText(Messages.TerminDialog_remarks);

		tBem = new Text(comp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
		tBem.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));

		GridData gdBem = new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1);
		gdBem.heightHint = tBem.getLineHeight();
		tBem.setLayoutData(gdBem);

		tBem.addModifyListener(e -> {
			int lineCount = tBem.getLineCount();
			int newHeight = Math.min(100, lineCount * tBem.getLineHeight() + 4); // +4 Padding
			gdBem.heightHint = newHeight;
			comp.layout(true, true);
			container.layout(true, true);
			getShell().layout(true, true);

		});

		new Label(comp, SWT.NONE).setText(Messages.AppointmentDetailComposite_date_type_or_status);
		new Label(comp, SWT.NONE).setText(Messages.AppointmentDetailComposite_reason);
		comboType = createCombo(comp, appointmentService.getTypes());
		comboType.addSelectionListener(typeSelectionListener());
		comboType.addModifyListener(e -> handleComboTypeSelection());
		txtReason = new Text(comp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		txtReason.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		comboStatus = createCombo(comp, appointmentService.getStates());
		chkTerminLinks = new Button(comp, SWT.CHECK);
		chkTerminLinks.setText(Messages.Appointment_TrminLinks);
		chkTerminLinks.setEnabled(false);
	}

	private Combo createCombo(Composite parent, List<String> items) {
		Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setItems(items.toArray(new String[0]));
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		return combo;
	}

	private SelectionAdapter typeSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleComboTypeSelection();
			}
		};
	}

	private void createEmailComposite(Composite parent) {
		if (emailComposite == null) {
			emailComposite = new EmailComposite(parent, SWT.NONE, getSelectedContact(), appointment);
			emailComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		}
	}

	private void finalizeLayout() {
		sash.setWeights(SASH_WEIGHTS_EXPANDED);
		sash.setMaximizedControl(rightPane);
		container.layout(true, true);
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

	public void setExpanded(boolean expand) {
		btnExpand.setSelection(expand);
		doExpand(expand);
	}

	private void refreshPatientModel() {
		loadAppointmentsForPatient();
		if (dayBar != null) {
			dayBar.setAppointment(appointment);
			dayBar.refresh();
		}
		reloadContactLabel();

		if (StringUtils.isBlank(txtPatSearch.getText())) {
			tBem.setText(StringUtils.EMPTY);
		} else {
			IContact c = getSelectedContact();
			if (c != null && c.isPatient()) {
				IPatient p = (IPatient) CoreModelServiceHolder.get().load(c.getId(), IPatient.class).orElse(null);
				if (p != null) {
					String comment = p.getComment() != null ? p.getComment() : StringUtils.EMPTY;
					tBem.setText(comment);
				} else {
					tBem.setText(StringUtils.EMPTY);
				}
			} else {
				tBem.setText(StringUtils.EMPTY);
			}
		}
		emailComposite.updateEmailControlsStatus(getSelectedContact());
	}

	private void reloadContactLabel() {

		Optional<IContact> c = reloadAsPatient(getAppointmentContact());
		String currentSearchText = txtPatSearch.getText();
		if (c.isPresent() && c.get().getLabel().equals(currentSearchText)) {
			StringBuilder b = new StringBuilder();
			b.append(c.get().getLabel()).append("\n").append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
			Optional.ofNullable(c.get().getMobile()).filter(s -> !s.isEmpty()).ifPresent(m -> b.append("Mobil:      ")
					.append("<a href=\"").append(toTelHref(m)).append("\">").append(m).append("</a>\n"));

			Optional.ofNullable(c.get().getPhone1()).filter(s -> !s.isEmpty()).ifPresent(p1 -> b.append("Telefon 1: ")
					.append("<a href=\"").append(toTelHref(p1)).append("\">").append(p1).append("</a>\n"));

			Optional.ofNullable(c.get().getPhone2()).filter(s -> !s.isEmpty()).ifPresent(p2 -> b.append("Telefon 2: ")
					.append("<a href=\"").append(toTelHref(p2)).append("\">").append(p2).append("</a>\n"));

			txtContact.setText(b.toString());
		} else {
			if (!c.isPresent() && StringUtils.isBlank(currentSearchText)) {
				txtContact.setText(Messages.AppointmentDetailComposite_no_patient_selected);
			} else {
				txtContact.setText(Messages.AppointmentDetailComposite_freetext + currentSearchText);
			}
		}
		container.layout(true, true);
		getShell().layout(true, true);
	}

	private static String toTelHref(String raw) {
		if (StringUtils.isBlank(raw))
			return null;
		String normalized = raw.replaceAll("[^+\\d]", "");
		normalized = normalized.replaceFirst("^00", "+");
		return "tel:" + normalized;
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

	private void applyPreferredDuration() {
		Map<String, Integer> pref = AppointmentServiceHolder.get().getPreferredDurations(comboArea.getText());
		String type = comboType.getText();
		Integer d = pref.get(type);
		if (d != null) {
			txtDuration.setSelection(d);
			updateDateTimeFields(txtDuration);
		}
	}

	private Composite createUIDateAreaContents(GridData gd, Composite container) {

		GridData topOnly = new GridData(SWT.FILL, SWT.FILL, true, true);
		topOnly.horizontalSpan = (gd != null && gd.horizontalSpan > 0) ? gd.horizontalSpan : 2;
		topOnly.verticalSpan = 1;
		topOnly.verticalIndent = 0;

		Composite compDateTime = new Composite(container, SWT.NONE);
		compDateTime.setLayoutData(topOnly);

		GridLayout dateGL = new GridLayout(3, false);
		dateGL.marginHeight = 0;
		dateGL.marginWidth = 0;
		dateGL.verticalSpacing = 3;
		dateGL.horizontalSpacing = 6;
		compDateTime.setLayout(dateGL);

		lblDateFrom = new Label(compDateTime, SWT.NONE);
		lblDateFrom.setText(Messages.AppointmentDetailComposite_tag);

		txtDateFromDrop = new CDateTime(compDateTime, CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_MEDIUM | CDT.TEXT_TRAIL);
		txtDateFromDrop.setPattern("EEE, dd.MM.yyyy ");
		txtDateFromDrop.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		txtDateFromDrop.addSelectionListener(dateTimeSelectionAdapter);
		txtDateFromDrop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setCompTimeToModel();
				dayBar.refresh();
			}
		});

		txtDateFromNoDrop = new CDateTime(compDateTime, CDT.BORDER | CDT.DATE_MEDIUM | CDT.TEXT_TRAIL);
		txtDateFromNoDrop.setPattern("EEE, dd.MM.yyyy ");
		txtDateFromNoDrop.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		txtDateFromNoDrop.setVisible(false);
		txtDateFromNoDrop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setCompTimeToModel();
				dayBar.refresh();
			}
		});

		Composite compTime = new Composite(compDateTime, SWT.NONE);
		GridLayout gl = new GridLayout(8, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 2;
		gl.horizontalSpacing = 6;
		compTime.setLayout(gl);
		compTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Label lblTimeFrom = new Label(compTime, SWT.NONE);
		lblTimeFrom.setText(Messages.AppointmentDetailComposite_starting_from);

		if (CoreUtil.isLinux()) {
			txtTimeFrom = new CDateTime(compTime, CDT.BORDER | CDT.TIME_SHORT);
		} else {
			txtTimeFrom = new CDateTime(compTime, CDT.BORDER | CDT.TIME_SHORT | CDT.SPINNER);
		}
		txtTimeFrom.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		txtTimeFrom.addSelectionListener(dateTimeSelectionAdapter);

		Label lblDuration = new Label(compTime, SWT.NONE);
		lblDuration.setText(Messages.AppointmentDetailComposite_duration);

		txtDuration = new Spinner(compTime, SWT.BORDER);
		txtDuration.setValues(0, 0, 24 * 60, 0, 5, 10);
		txtDuration.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		txtDuration.addSelectionListener(dateTimeSelectionAdapter);

		Label lblTimeTo = new Label(compTime, SWT.NONE);
		lblTimeTo.setText(Messages.AppointmentDetailComposite_until);

		if (CoreUtil.isLinux()) {
			txtTimeTo = new CDateTime(compTime, CDT.BORDER | CDT.TIME_SHORT);
		} else {
			txtTimeTo = new CDateTime(compTime, CDT.BORDER | CDT.TIME_SHORT | CDT.SPINNER);
		}
		txtTimeTo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		txtTimeTo.addSelectionListener(dateTimeSelectionAdapter);

		btnIsAllDay = new Button(compTime, SWT.CHECK);
		btnIsAllDay.setText(Messages.AppointmentDetailComposite_isAllDay);
		btnIsAllDay.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		btnIsAllDay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean enabled = !btnIsAllDay.getSelection();
				txtTimeFrom.setEnabled(enabled);
				txtDuration.setEnabled(enabled);
				txtTimeTo.setEnabled(enabled);
			}
		});

		Composite compArea = new Composite(compTime, SWT.NONE);
		GridLayout glArea = new GridLayout(2, false);
		glArea.marginHeight = 0;
		glArea.marginWidth = 0;
		glArea.horizontalSpacing = 6;
		glArea.marginLeft = 8;
		compArea.setLayout(glArea);
		GridData gdCompArea = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdCompArea.horizontalSpan = 1;
		compArea.setLayoutData(gdCompArea);

		Label lblArea = new Label(compArea, SWT.NONE);
		lblArea.setText(Messages.AppointmentDetailComposite_range);

		comboArea = new Combo(compArea, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboArea.setItems(configService.get("agenda/bereiche", "Praxis").split(","));
		comboArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		comboArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!comboArea.getText().equals(appointment.getSchedule())) {
					appointment.setSchedule(comboArea.getText());
					dayBar.setAppointment(appointment);
					dayBar.refresh();
					applyPreferredDuration();
				}
			}
		});

		return compDateTime;
	}

	private void loadCompTimeFromModel() {
		Date appointmentStartDate = Date
				.from(ZonedDateTime.of(appointment.getStartTime(), ZoneId.systemDefault()).toInstant());
		txtDateFrom.setSelection(appointmentStartDate);
		LocalDate ld = appointment.getStartTime().toLocalDate();
		pickerContext.setDate(ld.getYear(), ld.getMonthValue() - 1, ld.getDayOfMonth());
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
		if (appointment.getContact() == null) {
			String sop = appointment.getSubjectOrPatient();
			if (StringUtils.isNotBlank(sop)) {
				txtPatSearch.setData(null);
				txtPatSearch.setText(sop);
				tBem.setText(StringUtils.EMPTY);
			} else {
				ContextServiceHolder.get().getActivePatient().ifPresent(p -> {
					txtPatSearch.setData(p);
					txtPatSearch.setText(p.getLabel());
					appointment.setSubjectOrPatient(p.getId());
					tBem.setText(p.getComment());
				});
			}
		} else {
			IContact pat = reloadAsPatient(Optional.ofNullable(appointment.getContact())).get();
			txtPatSearch.setData(pat);
			txtPatSearch.setText(pat.getLabel());
			tBem.setText(pat.getComment());
		}
		loadCompTimeFromModel();
		applyPreferredDuration();

	}

	private void setCompTimeToModel() {
		Date dateFrom = txtDateFrom.getSelection();
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
		appointment.setCreatedBy(ContextServiceHolder.get().getActiveUser().get().getLabel());
		if (appointment.getCreated() == null || appointment.getCreated().isEmpty()) {
			appointment.setCreated(createTimeStamp());
		}
		appointment.setLastEdit(createTimeStamp());
		appointment.setReason(txtReason.getText());
		if (txtDataIsMatchingContact()) {
			appointment.setSubjectOrPatient(((IContact) txtPatSearch.getData()).getId());
		} else if (StringUtils.isNotBlank(txtPatSearch.getText())) {
			appointment.setSubjectOrPatient(txtPatSearch.getText());
		}
		createKombiTermineIfApplicable();
		return appointment;
	}

	private void handleComboTypeSelection() {
		String selectedType = comboType.getText();
		List<String> kombiTermineList = ConfigServiceHolder.get()
				.getAsList(PreferenceConstants.AG_KOMBITERMINE + "/" + selectedType); //$NON-NLS-1$
		if (!StringUtils.isBlank(selectedType) && !kombiTermineList.isEmpty()) {
			chkTerminLinks.setEnabled(true);
		} else {
			chkTerminLinks.setEnabled(false);
			chkTerminLinks.setSelection(false);
		}
		applyPreferredDuration();
	}

	private void createKombiTermineIfApplicable() {
		if (chkTerminLinks.getSelection()) {
			return;
		}
		String selectedType = comboType.getText();
		List<String> kombiTermineList = ConfigServiceHolder.get()
				.getAsList(PreferenceConstants.AG_KOMBITERMINE + "/" + selectedType); //$NON-NLS-1$
		if (kombiTermineList.isEmpty()) {
			return;
		}
		AppointmentExtensionHandler.setMainAppointmentId(appointment, appointment.getId());
		List<String> kombiTerminIds = new ArrayList<>();
		for (String kombiTermin : kombiTermineList) {
			kombiTermin = kombiTermin.replaceAll("[{}]", ""); //$NON-NLS-1$ //$NON-NLS-2$
			String[] elements = kombiTermin.split(";"); //$NON-NLS-1$
			IAppointment newAppointment = CoreModelServiceHolder.get().create(IAppointment.class);
			newAppointment.setState(appointment.getState());
			newAppointment.setType(elements[2]);
			newAppointment.setSchedule(elements[1]);
			newAppointment.setCreatedBy(appointment.getCreatedBy());
			newAppointment.setCreated(createTimeStamp());
			newAppointment.setLastEdit(createTimeStamp());
			newAppointment.setReason(elements[0]);
			if (txtDataIsMatchingContact()) {
				newAppointment.setSubjectOrPatient(((IContact) txtPatSearch.getData()).getId());
			} else if (StringUtils.isNotBlank(txtPatSearch.getText())) {
				newAppointment.setSubjectOrPatient(txtPatSearch.getText());
			}
			LocalDateTime startTime = appointment.getStartTime();
			int offset = Integer.parseInt(elements[4]);
			if (((String) Messages.AddCombiTerminDialogBefore).equalsIgnoreCase(elements[3])) {
				startTime = startTime.minusMinutes(offset);
			} else {
				startTime = startTime.plusMinutes(offset);
			}
			newAppointment.setStartTime(startTime);
			newAppointment.setEndTime(startTime.plusMinutes(Integer.parseInt(elements[5])));
			kombiTerminIds.add(newAppointment.getId());
			AppointmentExtensionHandler.setMainAppointmentId(newAppointment, appointment.getId());
			AppointmentExtensionHandler.addLinkedAppointmentId(newAppointment, newAppointment.getId());
			CoreModelServiceHolder.get().save(newAppointment);
		}
		AppointmentExtensionHandler.addMultipleLinkedAppointments(appointment, kombiTerminIds);
		CoreModelServiceHolder.get().save(appointment);
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

	private void doExpand(boolean expand) {
		GridData gd = (GridData) sash.getLayoutData();
		if (expand) {
			setAllDetailCompositesVisible(true);
			rightPane.layout(true, true);
			sash.setMaximizedControl(null);
			sash.setWeights(SASH_WEIGHTS_EXPANDED);
			gd.verticalAlignment = SWT.FILL;
			gd.grabExcessVerticalSpace = true;
			gd.heightHint = SWT.DEFAULT;
			gd.widthHint = SWT.DEFAULT;
		} else {
			setAllDetailCompositesVisible(false);
			rightPane.layout(true, true);
			sash.setMaximizedControl(rightPane);
			gd.verticalAlignment = SWT.TOP;
			gd.grabExcessVerticalSpace = false;
			gd.widthHint = rightPane.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
			gd.heightHint = rightPane.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y;
		}
		container.layout(true, true);
		getShell().layout(true, true);
		dayBar.refresh();
	}

	private void setAllDetailCompositesVisible(boolean visible) {
		setCompositeVisible(compContext, visible);
		setCompositeVisible(compContentMiddle, visible);
		setCompositeVisible(compTimeSelektor, visible);

	}

	private void setCompositeVisible(Composite c, boolean visible) {
		GridData data = (GridData) c.getLayoutData();
		data.exclude = !visible;
		c.setVisible(visible);
	}

	public IContact getSelectedContact() {
		Object data = txtPatSearch.getData();
		if (data instanceof IContact) {
			return (IContact) data;
		}
		return null;
	}

	public boolean getEmailCheckboxStatus() {
		return emailComposite.isCheckboxChecked();
	}

	public EmailDetails getEmailDeteils() {
		return emailComposite.extractEmailDetails();
	}

	public static String createTimeStamp() {
		return Integer.toString(TimeTool.getTimeInSeconds() / 60);
	}
}