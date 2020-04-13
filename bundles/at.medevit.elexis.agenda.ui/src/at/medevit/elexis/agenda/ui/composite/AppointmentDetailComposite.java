package at.medevit.elexis.agenda.ui.composite;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.TextContentAdapter;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.icons.Images;

public class AppointmentDetailComposite extends Composite {
	
	private final IAppointment appointment;
	
	private Spinner txtDuration;
	private CDateTime txtDateFrom;
	private CDateTime txtTimeFrom;
	private CDateTime txtTimeTo;
	private Combo comboArea;
	private Combo comboType;
	private Combo comboStatus;
	private Text txtReason;
	private Text txtPatSearch;
	
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
			}
		});
		
		Button btnErweitern = new Button(container, SWT.PUSH);
		btnErweitern.setImage(Images.IMG_NEW.getImage());
		btnErweitern.setToolTipText("erweitern");
		btnErweitern.setText("erweitern");
		btnErweitern.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				//TODO
			}
		});
		
		Composite compTime = createUIDateContents(container);
		
		Composite compArea = new Composite(compTime, SWT.NONE);
		GridLayout gl = new GridLayout(2, false);
		gl.marginLeft = 20;
		compArea.setLayout(gl);
		
		Label lblArea = new Label(compArea, SWT.NULL);
		lblArea.setText("Bereich");
		comboArea = new Combo(compArea, SWT.DROP_DOWN);
		comboArea.setItems(ConfigServiceHolder.get().get("agenda/bereiche", "Praxis").split(","));
		
		Composite compTypeReason = new Composite(container, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		gd.verticalIndent = 10;
		compTypeReason.setLayoutData(gd);
		compTypeReason.setLayout(new GridLayout(2, false));
		
		Label lblType = new Label(compTypeReason, SWT.NULL);
		lblType.setText("Termin Typ, -status");
		
		Label lblReason = new Label(compTypeReason, SWT.NULL);
		lblReason.setText("Grund");
		
		comboType = new Combo(compTypeReason, SWT.DROP_DOWN);
		comboType.setItems(ConfigServiceHolder.get().get("agenda/TerminTypen", "").split(",")); //TODO find noting
		
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd.widthHint = 80;
		comboType.setLayoutData(gd);
		
		txtReason = new Text(compTypeReason, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3);
		gd.heightHint = 100;
		txtReason.setLayoutData(gd);
		
		comboStatus = new Combo(compTypeReason, SWT.DROP_DOWN);
		comboStatus.setItems(ConfigServiceHolder.get().get("agenda/TerminStatus", "").split(",")); //TODO find noting
		
		loadFromModel();
	}
	
	private Composite createUIDateContents(Composite container){
		
		Composite compDateTime = new Composite(container, SWT.NULL);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		gd.verticalIndent = 15;
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
}
