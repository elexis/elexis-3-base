package at.medevit.elexis.agenda.ui.composite;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.icons.Images;

public class AppointmentDetailComposite extends Composite {
	
	private IAppointment appointment;
	
	private Spinner txtDuration;
	private CDateTime txtDateFrom;
	private CDateTime txtTimeFrom;
	private CDateTime txtTimeTo;
	private Combo comboArea;
	private Combo comboType;
	private Combo comboStatus;
	private Text txtReason;
	private Text txtPatSearch;
	private DayOverViewComposite dayBar;
	private TableViewer appointmentsViewer;
	private Label lblContact;
	
	SelectionAdapter dateTimeSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e){
			Date dateFrom = txtDateFrom.getSelection();
			Date timeFrom = txtTimeFrom.getSelection();
			Date timeTo = txtTimeTo.getSelection();
			int duration = txtDuration.getSelection();
			LocalDateTime dateTimeFrom =
				LocalDateTime.of(dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
					timeFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
			LocalDateTime dateTimeEnd =
				LocalDateTime.of(dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
					timeTo.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
			
			if (e.getSource().equals(txtDuration) || e.getSource().equals(txtTimeFrom)) {
				txtTimeTo.setSelection(Date.from(
					ZonedDateTime
					.of(dateTimeFrom.plusMinutes(duration), ZoneId.systemDefault()).toInstant()));
			} else if (e.getSource().equals(txtTimeTo)) {
				txtDuration.setSelection((int) dateTimeFrom.until(dateTimeEnd, ChronoUnit.MINUTES));
			}
			
			dayBar.set();
			dayBar.redraw();
		}
	};
	
	public AppointmentDetailComposite(Composite parent, int style, IAppointment appointment){
		super(parent, style);
		this.appointment = appointment;
		setLayout(new GridLayout(2, false));
		setLayoutData(new GridData(GridData.FILL_BOTH));
		createContents(this);
	}
	
	private void createContents(Composite parent){
		Objects.requireNonNull(appointment, "Appointment cannot be null");
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(4, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		new Label(container, SWT.NULL).setText("Suche");
		txtPatSearch = new Text(container, SWT.SEARCH | SWT.ICON_SEARCH);
		txtPatSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtPatSearch.setMessage(" Vorname, Nachname, Geburtsdatum, PatientNr ");
		AsyncContentProposalProvider<IPatient> aopp = new AsyncContentProposalProvider<IPatient>(
			"description1", "description2", "dob", "code") {
			@Override
			public IQuery<IPatient> createBaseQuery(){
				return CoreModelServiceHolder.get().getQuery(IPatient.class);
			}
			
			@Override
			public Text getWidget(){
				return txtPatSearch;
			}
		};
		
		ContentProposalAdapter cppa =
			new ContentProposalAdapter(txtPatSearch, new TextContentAdapter(), aopp, null, null);
		aopp.configureContentProposalAdapter(cppa);
		
		cppa.addContentProposalListener(new IContentProposalListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void proposalAccepted(IContentProposal proposal){
				IdentifiableContentProposal<IPatient> prop =
					(IdentifiableContentProposal<IPatient>) proposal;
				txtPatSearch.setText(prop.getLabel());
				txtPatSearch.setData(prop.getIdentifiable());
				appointment.setSubjectOrPatient(prop.getIdentifiable().getId());
				refreshPatientModel();
			}
		});
		
		Button btnErweitern = new Button(container, SWT.TOGGLE);
		btnErweitern.setImage(Images.IMG_NEW.getImage());
		btnErweitern.setToolTipText("erweitern");
		btnErweitern.setText("erweitern");
		
		Composite compSelectedPatient = new Composite(container, SWT.NONE);
		compSelectedPatient.setLayout(new GridLayout(1, false));
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		gd.verticalIndent = 20;
		compSelectedPatient.setLayoutData(gd);
		
		lblContact = new Label(compSelectedPatient, SWT.BORDER);
		lblContact.setText("");
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.widthHint = 120;
		lblContact.setLayoutData(gd);
		
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd.verticalIndent = 15;
		
		Composite compDateArea = createUIDateAreaContents(gd, container);
		
		Composite compContentMiddle = new Composite(compDateArea, SWT.BORDER);
		compContentMiddle.setLayout(new GridLayout(4, false));
		compContentMiddle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 7, 1));
		
		appointmentsViewer = new TableViewer(
			compContentMiddle,
			SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData listGd = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 3);
		listGd.heightHint = 200;
		appointmentsViewer.getControl().setLayoutData(listGd);
		appointmentsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event){
				
				Object sel = event.getStructuredSelection().getFirstElement();
				if (dayBar != null && sel instanceof IAppointment && !sel.equals(appointment)) {
					reloadAppointment((IAppointment) sel);
				}
			}
		});
		appointmentsViewer.setContentProvider(ArrayContentProvider.getInstance());
		appointmentsViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element){
				return ((IAppointment) element).getLabel();
			}
		});
		Button btnAdd = new Button(compContentMiddle, SWT.NULL);
		btnAdd.setText("Einsetzen");
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				CoreModelServiceHolder.get().save(setToModel());
				cloneAndReloadAppointment();
				// cannot query this appointment with subjectOrPatient empty 
				// same handling as legacy implementation
				if (appointment.getSubjectOrPatient() == null
					|| appointment.getSubjectOrPatient().isEmpty()) {
					appointmentsViewer.add(appointment);
				}
			}
		});
		Button btnDelete = new Button(compContentMiddle, SWT.NULL);
		btnDelete.setText("Löschen");
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				CoreModelServiceHolder.get().delete(appointment);
				appointmentsViewer.remove(appointment);
				cloneAndReloadAppointment();
			}
		});
		Button btnSearch = new Button(compContentMiddle, SWT.NULL);
		btnSearch.setText("Suchen");
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				refreshPatientModel();
			}
		});
		Button btnPrint = new Button(compContentMiddle, SWT.NULL);
		btnPrint.setText("Drucken");
		btnPrint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				//TODO 
			}
		});
		Group compTimeSelektor = new Group(container, SWT.SHADOW_ETCHED_IN);
		compTimeSelektor.setLayout(new GridLayout(1, false));
		compTimeSelektor.setTextDirection(SWT.CENTER);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		compTimeSelektor.setLayoutData(gd);
		
		dayBar = new DayOverViewComposite(compTimeSelektor, appointment, txtTimeFrom, txtTimeTo,
			txtDuration);
		dayBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		btnErweitern.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				
				toggleVisiblityComposite(compSelectedPatient);
				toggleVisiblityComposite(compContentMiddle);
				toggleVisiblityComposite(compTimeSelektor);
				getParent().pack();
				
				dayBar.set();
				dayBar.redraw();
			}
		});
		
		Composite compTypeReason = new Composite(container, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		gd.verticalIndent = 10;
		compTypeReason.setLayoutData(gd);
		compTypeReason.setLayout(new GridLayout(2, false));
		
		Label lblType = new Label(compTypeReason, SWT.NULL);
		lblType.setText("Termin Typ, -status");
		
		Label lblReason = new Label(compTypeReason, SWT.NULL);
		lblReason.setText("Grund");
		
		comboType = new Combo(compTypeReason, SWT.DROP_DOWN);
		comboType.setItems(ConfigServiceHolder.get().get("agenda/TerminTypen", "").split(",")); //TODO case find noting
		
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd.widthHint = 80;
		comboType.setLayoutData(gd);
		
		txtReason = new Text(compTypeReason, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3);
		gd.heightHint = 100;
		txtReason.setLayoutData(gd);
		
		comboStatus = new Combo(compTypeReason, SWT.DROP_DOWN);
		comboStatus.setItems(ConfigServiceHolder.get().get("agenda/TerminStatus", "").split(",")); //TODO case find noting
		
		toggleVisiblityComposite(compSelectedPatient);
		toggleVisiblityComposite(compContentMiddle);
		toggleVisiblityComposite(compTimeSelektor);
		
		loadFromModel();
		refreshPatientModel();
	}
	
	private void reloadAppointment(IAppointment appointment){
		this.appointment = appointment;
		loadFromModel();
		refreshPatientModel();
	}
	
	private void refreshPatientModel(){
		loadAppointmentsForPatient();
		if (dayBar != null) {
			dayBar.reloadAppointment(appointment);
			dayBar.set();
			dayBar.redraw();
		}
		reloadContactLabel();
	}
	
	private void reloadContactLabel(){
		Optional<IContact> c = getAppointmentContact();
		if (c.isPresent()) {
			StringBuilder b = new StringBuilder();
			b.append(c.get().getDescription1());
			b.append(" ");
			b.append(c.get().getDescription2());
			b.append(" ");
			b.append(Optional.ofNullable(c.get().getDescription3()).orElse(""));
			
			if (c.get().isPatient()) {
				
				IPatient p =
					CoreModelServiceHolder.get().load(c.get().getId(), IPatient.class).get();
				if (p.getDateOfBirth() != null) {
					b.append("\n");
					b.append(p.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
				}
			}
			b.append("\n");
			b.append(Optional.ofNullable(c.get().getMobile()).filter(i -> i != null && !i
				.isEmpty())
				.map(i -> "\n" + i).orElse(""));
			b.append(Optional.ofNullable(c.get().getPhone1()).filter(i -> i != null && !i
				.isEmpty())
				.map(i -> "\n" + i).orElse(""));
			b.append(Optional.ofNullable(c.get().getPhone2())
				.filter(i -> i != null && !i.isEmpty())
				.map(i -> "\n" + i).orElse(""));
			lblContact.setText(b.toString());
		} else {
			lblContact.setText("Kein Patient\nausgewählt!");
		}
	}
	
	private Optional<IContact> getAppointmentContact(){
		return Optional.ofNullable(appointment.getContact());
	}
	
	private void loadAppointmentsForPatient(){
		getAppointmentContact().ifPresent(i -> {
			IQuery<IAppointment> query = CoreModelServiceHolder.get().getQuery(IAppointment.class);
			query.and("patId", COMPARATOR.EQUALS, i.getId());
			appointmentsViewer.setInput(query.execute().stream()
				.sorted(Comparator.comparing(IAppointment::getStartTime)
					.reversed())
				.collect(Collectors.toList()));
			if (appointment != null && !appointment
				.equals(appointmentsViewer.getStructuredSelection().getFirstElement())) {
				appointmentsViewer.setSelection(new StructuredSelection(appointment));
			}
		});
	}
	
	private void toggleVisiblityComposite(Composite c){
		GridData data = (GridData) c.getLayoutData();
		data.exclude = !data.exclude;
		c.setVisible(!data.exclude);
	}
	
	private Composite createUIDateAreaContents(GridData gd, Composite container){
		Composite compDateTime = new Composite(container, SWT.NULL);
		compDateTime.setLayoutData(gd);
		compDateTime.setLayout(new GridLayout(2, false));
		
		Label lblDateFrom = new Label(compDateTime, SWT.NULL);
		lblDateFrom.setText("Tag");
		txtDateFrom = new CDateTime(
			compDateTime,
			CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_MEDIUM | CDT.TEXT_TRAIL);
		txtDateFrom.setPattern("EEE, dd/MM/yyyy ");
		
		Composite compTime = new Composite(compDateTime, SWT.NONE);
		GridLayout gl = new GridLayout(7, false);
		gl.marginLeft = -5;
		compTime.setLayout(gl);
		compTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lblTimeFrom = new Label(compTime, SWT.NULL);
		lblTimeFrom.setText("Von");
		txtTimeFrom = new CDateTime(compTime, CDT.BORDER | CDT.TIME_SHORT | CDT.SPINNER);
		txtTimeFrom.addSelectionListener(dateTimeSelectionAdapter);
		
		Label lblDuration = new Label(compTime, SWT.NULL);
		lblDuration.setText("Dauer");
		txtDuration = new Spinner(compTime, SWT.BORDER);
		txtDuration.setValues(0, 0, 24 * 60, 0, 5, 10);
		txtDuration.addSelectionListener(dateTimeSelectionAdapter);
		
		Label lblTimeTo = new Label(compTime, SWT.NULL);
		lblTimeTo.setText("Bis");
		txtTimeTo = new CDateTime(compTime, CDT.BORDER | CDT.TIME_SHORT | CDT.SPINNER);
		txtTimeTo.addSelectionListener(dateTimeSelectionAdapter);
		
		Composite compArea = new Composite(compTime, SWT.NONE);
		gl = new GridLayout(2, false);
		gl.marginLeft = 20;
		compArea.setLayout(gl);
		
		Label lblArea = new Label(compArea, SWT.NULL);
		lblArea.setText("Bereich");
		comboArea = new Combo(compArea, SWT.DROP_DOWN);
		comboArea.setItems(ConfigServiceHolder.get().get("agenda/bereiche", "Praxis").split(","));
		comboArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				if (!comboArea.getText().equals(appointment.getSchedule())) {
					appointment.setSchedule(comboArea.getText());
					dayBar.reloadAppointment(appointment);
					dayBar.set();
					dayBar.redraw();
				}
			}
		});
		return compTime;
	}
	
	private void loadFromModel(){
		comboStatus.setText(appointment.getState());
		comboType.setText(appointment.getType());
		comboArea.setText(appointment.getSchedule());
		
		txtReason.setText(appointment.getReason());
		txtPatSearch.setText(appointment.getSubjectOrPatient());
		
		Date appointmentStartDate = Date
			.from(ZonedDateTime.of(appointment.getStartTime(), ZoneId.systemDefault()).toInstant());
		Date appointmentEndDate = Date
			.from(ZonedDateTime.of(appointment.getEndTime(), ZoneId.systemDefault()).toInstant());
		
		txtDateFrom.setSelection(appointmentStartDate);
		txtTimeFrom.setSelection(appointmentStartDate);
		txtTimeTo.setSelection(appointmentEndDate);
		txtDuration.setSelection(appointment.getDurationMinutes());
	}
	
	public IAppointment setToModel(){
		Date dateFrom = txtDateFrom.getSelection();
		Date timeFrom = txtTimeFrom.getSelection();
		Date timeTo = txtTimeTo.getSelection();
		LocalDateTime dateTimeFrom =
			LocalDateTime.of(dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
				timeFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
		LocalDateTime dateTimeTo =
			LocalDateTime.of(dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
				timeTo.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
		appointment.setStartTime(dateTimeFrom);
		appointment.setEndTime(dateTimeTo);
		
		appointment.setState(comboStatus.getText());
		appointment.setType(comboType.getText());
		appointment.setSchedule(comboArea.getText());
		
		appointment.setReason(txtReason.getText());
		if (txtPatSearch.getData() instanceof IPatient) {
			appointment.setSubjectOrPatient(((IPatient) txtPatSearch.getData()).getId());
		}
		
		return appointment;
	}
	
	private void cloneAndReloadAppointment(){
		IAppointment newAppointment = CoreModelServiceHolder.get().create(IAppointment.class);
		newAppointment
			.setSubjectOrPatient(appointment.getContact() != null ? appointment.getContact().getId()
					: appointment.getSubjectOrPatient());
		appointment = newAppointment;
		setToModel();
		reloadAppointment(appointment);
	}
}
