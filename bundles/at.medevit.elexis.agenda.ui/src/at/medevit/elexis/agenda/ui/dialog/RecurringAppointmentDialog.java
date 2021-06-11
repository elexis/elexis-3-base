package at.medevit.elexis.agenda.ui.dialog;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import at.medevit.elexis.agenda.ui.composite.AsyncContentProposalProvider;
import at.medevit.elexis.agenda.ui.composite.IdentifiableContentProposal;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IAppointmentSeries;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.agenda.EndingType;
import ch.elexis.core.model.agenda.SeriesType;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class RecurringAppointmentDialog extends TitleAreaDialog {
	
	private Text txtEndsAfterNOccurences;
	private Group groupData;
	private Text txtReason;
	
	private DateTime dateTimeBegin;
	private DateTime dateTimeEnd;
	private DateTime dateTimeBeginOfSeries;
	private Button btnEndsAfter;
	private Button btnEndsOn;
	
	private Spinner durationSpinner;
	private DateTime dateEndsOn;
	
	private CTabFolder tabFolderSeriesPattern;
	private WeeklySeriesComposite wsc;
	private MonthlySeriesComposite msc;
	private YearlySeriesComposite ysc;
	
	private Text txtContactSearch;
	private Combo comboSchedule;
	
	private IAppointmentSeries appointment;
	
	private boolean noedit;
	
	private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
	private DecimalFormat decimalFormat = new DecimalFormat("00");
	
	public RecurringAppointmentDialog(IAppointmentSeries appointment){
		super(Display.getDefault().getActiveShell());
		
		this.appointment = appointment;
		this.noedit = appointment.isPersistent();
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite area = (Composite) super.createDialogArea(parent);
		if (noedit) {
			setMessage(Messages.SerienTerminDialog_this_message_show);
		} else {
			setMessage(Messages.SerienTerminDialog_this_message_create);
		}
		Group grpTermin = new Group(area, SWT.NONE);
		grpTermin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpTermin.setLayout(new GridLayout(6, false));
		grpTermin.setText(Messages.SerienTerminDialog_grpTermin_text);
		
		Label lblBeginn = new Label(grpTermin, SWT.NONE);
		lblBeginn.setText(Messages.SerienTerminDialog_lblBeginn_text);
		
		dateTimeBegin = new DateTime(grpTermin, SWT.BORDER | SWT.TIME | SWT.SHORT);
		dateTimeBegin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				updateSpinner();
			}
		});
		
		Label lblEnde = new Label(grpTermin, SWT.NONE);
		lblEnde.setText(Messages.SerienTerminDialog_lblEnde_text);
		
		dateTimeEnd = new DateTime(grpTermin, SWT.BORDER | SWT.TIME | SWT.SHORT);
		dateTimeEnd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				updateSpinner();
			}
		});
		
		Label lblDauer = new Label(grpTermin, SWT.NONE);
		lblDauer.setText(Messages.SerienTerminDialog_lblDauer_text);
		
		durationSpinner = new Spinner(grpTermin, SWT.NONE);
		durationSpinner.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		durationSpinner.setMinimum(5);
		durationSpinner.setMaximum(1440);
		durationSpinner.setIncrement(5);
		durationSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				int value = durationSpinner.getSelection();
				Calendar cal = Calendar.getInstance();
				cal.clear();
				cal.set(Calendar.HOUR_OF_DAY, dateTimeBegin.getHours());
				cal.set(Calendar.MINUTE, dateTimeBegin.getMinutes());
				cal.add(Calendar.MINUTE, value);
				dateTimeEnd.setHours(cal.get(Calendar.HOUR_OF_DAY));
				dateTimeEnd.setMinutes(cal.get(Calendar.MINUTE));
			}
		});
		
		{
			Group grpSerienmuster = new Group(area, SWT.NONE);
			grpSerienmuster.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			grpSerienmuster.setLayout(new GridLayout(1, false));
			grpSerienmuster.setText(Messages.SerienTerminDialog_grpSerienmuster_text);
			
			tabFolderSeriesPattern = new CTabFolder(grpSerienmuster, SWT.BORDER);
			tabFolderSeriesPattern
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			tabFolderSeriesPattern.setSelectionBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
			tabFolderSeriesPattern.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					appointment.setSeriesType((SeriesType) e.item.getData());
				}
			});
			
			CTabItem tbtmDaily = new CTabItem(tabFolderSeriesPattern, SWT.NONE);
			tbtmDaily.setText(Messages.SerienTerminDialog_tbtmDaily_text);
			tbtmDaily.setData(SeriesType.DAILY);
			
			Label lblNoConfigurationNecessary = new Label(tabFolderSeriesPattern, SWT.NONE);
			tbtmDaily.setControl(lblNoConfigurationNecessary);
			lblNoConfigurationNecessary
				.setText(Messages.SerienTerminDialog_lblNoConfigurationNecessary_text);
			
			CTabItem tbtmWeekly = new CTabItem(tabFolderSeriesPattern, SWT.NONE);
			tbtmWeekly.setText(Messages.SerienTerminDialog_tbtmWeekly_text);
			wsc = new WeeklySeriesComposite(tabFolderSeriesPattern, SWT.NONE);
			tbtmWeekly.setControl(wsc);
			tbtmWeekly.setData(SeriesType.WEEKLY);
			
			CTabItem tbtmMonthly = new CTabItem(tabFolderSeriesPattern, SWT.NONE);
			tbtmMonthly.setText(Messages.SerienTerminDialog_tbtmMonthly_text);
			msc = new MonthlySeriesComposite(tabFolderSeriesPattern, SWT.NONE);
			tbtmMonthly.setControl(msc);
			new Label(msc, SWT.NONE);
			tbtmMonthly.setData(SeriesType.MONTHLY);
			
			CTabItem tbtmYearly = new CTabItem(tabFolderSeriesPattern, SWT.NONE);
			tbtmYearly.setText(Messages.SerienTerminDialog_tbtmYearly_text);
			ysc = new YearlySeriesComposite(tabFolderSeriesPattern, SWT.NONE);
			tbtmYearly.setControl(ysc);
			tbtmYearly.setData(SeriesType.YEARLY);
		}
		Group grpSeriendauer = new Group(area, SWT.NONE);
		grpSeriendauer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpSeriendauer.setLayout(new GridLayout(3, false));
		grpSeriendauer.setText(Messages.SerienTerminDialog_grpSeriendauer_text);
		
		Label beginOfSeries = new Label(grpSeriendauer, SWT.NONE);
		beginOfSeries.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		beginOfSeries.setText(Messages.SerienTerminDialog_beginOfSeries_text);
		
		dateTimeBeginOfSeries = new DateTime(grpSeriendauer, SWT.BORDER | SWT.DROP_DOWN | SWT.LONG);
		dateTimeBeginOfSeries.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		dateTimeBeginOfSeries.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				LocalDate start = appointment.getSeriesStartDate();
				wsc.setWeekNumberLabel(start.get(ChronoField.ALIGNED_WEEK_OF_YEAR),
					start.getYear());
				super.widgetSelected(e);
			}
		});
		
		Composite composite = new Composite(grpSeriendauer, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		GridLayout composite_layout = new GridLayout(2, false);
		composite_layout.marginHeight = 0;
		composite_layout.marginWidth = 0;
		composite.setLayout(composite_layout);
		
		btnEndsAfter = new Button(composite, SWT.RADIO);
		btnEndsAfter.setText(Messages.SerienTerminDialog_btnEndsAfter_text);
		btnEndsAfter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				appointment.setEndingType(EndingType.AFTER_N_OCCURENCES);
			}
		});
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		GridLayout composite_1_layout = new GridLayout(2, false);
		composite_1_layout.marginHeight = 0;
		composite_1_layout.marginWidth = 0;
		composite_1.setLayout(composite_1_layout);
		
		txtEndsAfterNOccurences = new Text(composite_1, SWT.BORDER);
		txtEndsAfterNOccurences
			.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setText(Messages.SerienTerminDialog_lblNewLabel_text);
		
		btnEndsOn = new Button(composite, SWT.RADIO);
		btnEndsOn.setText(Messages.SerienTerminDialog_btnEndsOn_text);
		btnEndsOn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				appointment.setEndingType(EndingType.ON_SPECIFIC_DATE);
			}
		});
		
		dateEndsOn = new DateTime(composite, SWT.BORDER);
		
		groupData = new Group(area, SWT.NONE);
		groupData.setText(Messages.SerienTerminDialog_groupData_text);
		groupData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		groupData.setLayout(new GridLayout(2, false));
		
		txtReason = new Text(groupData, SWT.BORDER);
		txtReason.setMessage(Messages.SerienTerminDialog_txtReason_message);
		txtReason.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		txtContactSearch = new Text(groupData, SWT.SEARCH | SWT.ICON_SEARCH);
		txtContactSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtContactSearch.setMessage("Vorname, Nachname");
		AsyncContentProposalProvider<IContact> aopp =
			new AsyncContentProposalProvider<IContact>(
			"description1", "description2") {
			@Override
			public IQuery<IContact> createBaseQuery(){
				return CoreModelServiceHolder.get().getQuery(IContact.class);
			}
			
			@Override
			public Text getWidget(){
				return txtContactSearch;
			}
		};
		txtContactSearch.setData(appointment.getContact());
		ControlDecoration controlDecoration =
			new ControlDecoration(txtContactSearch, SWT.LEFT | SWT.TOP);
		controlDecoration.setDescriptionText(
			"Kontakt Suche nach Vorname, Nachname\noder Freitext Eingabe für Termine ohne Kontakt");
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
			.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION);
		controlDecoration.setImage(fieldDecoration.getImage());
		controlDecoration.show();
		controlDecoration.setShowHover(true);
		
		ContentProposalAdapter cppa = new ContentProposalAdapter(txtContactSearch,
			new TextContentAdapter(), aopp, null, null);
		aopp.configureContentProposalAdapter(cppa);
		
		cppa.addContentProposalListener(new IContentProposalListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void proposalAccepted(IContentProposal proposal){
				IdentifiableContentProposal<IContact> prop =
					(IdentifiableContentProposal<IContact>) proposal;
				txtContactSearch.setText(prop.getLabel());
				txtContactSearch.setData(prop.getIdentifiable());
				appointment.setSubjectOrPatient(prop.getIdentifiable().getId());
				reloadContactLabel();
			}
		});
		
		Label lblArea = new Label(groupData, SWT.NONE);
		lblArea.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblArea.setText(Messages.SerienTerminDialog_lblArea_text);
		
		comboSchedule = new Combo(groupData, SWT.NONE);
		comboSchedule.setItems(ConfigServiceHolder.get().get("agenda/bereiche", "Praxis").split(","));
		comboSchedule.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				if (!comboSchedule.getText().equals(appointment.getSchedule())) {
					appointment.setSchedule(comboSchedule.getText());
				}
			}
		});
		
		initDataBindings();
		
		initDialog();
		
		if (noedit) {
			disableAll(area);
		}
		
		return area;
	}
	
	private Optional<IContact> getAppointmentContact(){
		return Optional.ofNullable(appointment.getContact());
	}
	
	private void reloadContactLabel(){
		Optional<IContact> c = getAppointmentContact();
		String currentSearchText = txtContactSearch.getText();
		if (c.isPresent() && c.get().getLabel().equals(currentSearchText)) {
			txtContactSearch.setText(c.get().getLabel());
		}
	}
	
	private void disableAll(Control widget){
		if (widget instanceof Composite) {
			Composite composite = (Composite) widget;
			Control[] children = composite.getChildren();
			for (Control control : children) {
				disableAll(control);
			}
			composite.setEnabled(false);
		} else if (widget instanceof Control) {
			widget.setEnabled(false);
		}
	}
	
	private void initDialog(){
		if (appointment.getContact() != null) {
			setTitle(appointment.getContact().getLabel());
		} else {
			setTitle("Kein Kontakt ausgewählt.");
		}
		if (appointment.getContact() != null) {
			txtContactSearch.setText(appointment.getContact().getLabel());
		} else {
			txtContactSearch.setText(appointment.getSubjectOrPatient());
		}
		comboSchedule.setText(appointment.getSchedule());
		//
		switch (appointment.getSeriesType()) {
		case DAILY:
			tabFolderSeriesPattern.setSelection(0);
			break;
		case WEEKLY:
			tabFolderSeriesPattern.setSelection(1);
			String[] pattern = appointment.getSeriesPatternString().split(",");
			wsc.getTxtWeekDistance().setText(pattern[0]);
			if (pattern.length > 1) {
				for (int i = 0; i < pattern[1].length(); i++) {
					char c = pattern[1].charAt(i);
					wsc.getWeekdays()[Character.getNumericValue(c)].setSelection(true);
				}
			}
			LocalDate start = appointment.getSeriesStartDate();
			wsc.setWeekNumberLabel(start.get(ChronoField.ALIGNED_WEEK_OF_YEAR), start.getYear());
			break;
		case MONTHLY:
			tabFolderSeriesPattern.setSelection(2);
			msc.setDay(Integer.parseInt(appointment.getSeriesPatternString()));
			break;
		case YEARLY:
			tabFolderSeriesPattern.setSelection(3);
			ysc.setDay(Integer.parseInt(appointment.getSeriesPatternString().substring(0, 2)));
			ysc.setMonth(Integer.parseInt(appointment.getSeriesPatternString().substring(2, 4)));
			break;
		default:
			break;
		}
		//
		switch (appointment.getEndingType()) {
		case AFTER_N_OCCURENCES:
			btnEndsAfter.setSelection(true);
			txtEndsAfterNOccurences.setText(appointment.getEndingPatternString());
			break;
		case ON_SPECIFIC_DATE:
			btnEndsOn.setSelection(true);
			LocalDate end = appointment.getSeriesEndDate();
			dateEndsOn.setDate(end.getYear(), end.getMonthValue() - 1, end.getDayOfMonth());
			break;
		default:
			break;
		}
		//
		updateSpinner();
	}
	
	@SuppressWarnings({
		"rawtypes", "unchecked"
	})
	protected DataBindingContext initDataBindings(){
		DataBindingContext bindingContext = new DataBindingContext();
		
		UpdateValueStrategy timeTarget2model = new UpdateValueStrategy();
		timeTarget2model.setConverter(new Date2LocalTimeConverter());
		UpdateValueStrategy timeModel2target = new UpdateValueStrategy();
		timeModel2target.setConverter(new LocalTime2DateConverter());
		
		UpdateValueStrategy dateTarget2model = new UpdateValueStrategy();
		dateTarget2model.setConverter(new Date2LocalDateConverter());
		UpdateValueStrategy dateModel2target = new UpdateValueStrategy();
		dateModel2target.setConverter(new LocalDate2DateConverter());
		
		//
		IObservableValue observeSelectionDateTimeBeginObserveWidget =
			WidgetProperties.dateTimeSelection().observe(dateTimeBegin);
		IObservableValue beginTimeSerienTerminObserveValue =
			PojoProperties.value("seriesStartTime").observe(appointment);
		bindingContext.bindValue(observeSelectionDateTimeBeginObserveWidget,
			beginTimeSerienTerminObserveValue, timeTarget2model, timeModel2target);
		//
		IObservableValue observeSelectionDateTimeEndObserveWidget =
			WidgetProperties.dateTimeSelection().observe(dateTimeEnd);
		IObservableValue endTimeSerienTerminObserveValue =
			PojoProperties.value("seriesEndTime").observe(appointment);
		bindingContext.bindValue(observeSelectionDateTimeEndObserveWidget,
			endTimeSerienTerminObserveValue, timeTarget2model, timeModel2target);
		//
		IObservableValue observeSelectionDateTimeBeginOfSeriesObserveWidget =
			WidgetProperties.dateTimeSelection().observe(dateTimeBeginOfSeries);
		IObservableValue seriesStartDateSerienTerminObserveValue =
			PojoProperties.value("seriesStartDate").observe(appointment);
		bindingContext.bindValue(observeSelectionDateTimeBeginOfSeriesObserveWidget,
			seriesStartDateSerienTerminObserveValue, dateTarget2model, dateModel2target);
		//
		IObservableValue observeTextTxtReasonObserveWidget =
			WidgetProperties.text(SWT.Modify).observe(txtReason);
		IObservableValue reasonSerienTerminObserveValue =
			PojoProperties.value("reason").observe(appointment);
		bindingContext.bindValue(observeTextTxtReasonObserveWidget, reasonSerienTerminObserveValue,
			null, null);
		
		IObservableValue observeSelectionDateEndsOnObserveWidget =
			WidgetProperties.dateTimeSelection().observe(dateEndsOn);
		IObservableValue endsOnDateSerienTerminObserveValue =
			PojoProperties.value("seriesEndDate").observe(appointment);
		bindingContext.bindValue(observeSelectionDateEndsOnObserveWidget,
			endsOnDateSerienTerminObserveValue, dateTarget2model, dateModel2target);
		
		return bindingContext;
	}
	
	private void updateSpinner(){
		int endTime = (dateTimeEnd.getHours() * 60) + dateTimeEnd.getMinutes();
		int startTime = (dateTimeBegin.getHours() * 60) + dateTimeBegin.getMinutes();
		int result = endTime - startTime;
		if (result < 0)
			result = 0;
		durationSpinner.setSelection(result);
	}
	
	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent){
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		Button button =
			createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		button.setText(Messages.SerienTerminDialog_other_text);
		Button button_1 = createButton(parent, IDialogConstants.STOP_ID, "remove series", false);
		button_1.setText(Messages.SerienTerminDialog_other_text_1);
		
	}
	
	@Override
	protected void buttonPressed(int buttonId){
		updateModel();
		super.buttonPressed(buttonId);
		switch (buttonId) {
		case IDialogConstants.OK_ID:
			if (noedit) {
				close();
			} else {
				AppointmentServiceHolder.get().saveAppointmentSeries(appointment);
				close();
			}
			break;
		case IDialogConstants.STOP_ID:
			AppointmentServiceHolder.get().deleteAppointmentSeries(appointment);
			close();
			break;
		default:
			break;
		}
		ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
	}
	
	private void updateModel(){
		// set data to model
		if (txtDataIsMatchingContact()) {
			appointment.setSubjectOrPatient(((IContact) txtContactSearch.getData()).getId());
		} else if (StringUtils.isNotBlank(txtContactSearch.getText())) {
			appointment.setSubjectOrPatient(txtContactSearch.getText());
		}
		// set series pattern
		switch (appointment.getSeriesType()) {
		case DAILY:
			switch (appointment.getEndingType()) {
			case AFTER_N_OCCURENCES:
				appointment.setSeriesPatternString(txtEndsAfterNOccurences.getText());
				break;
			case ON_SPECIFIC_DATE:
				appointment
					.setSeriesPatternString(dateFormatter.format(appointment.getSeriesEndDate()));
				break;
			}
			break;
		case WEEKLY:
			StringBuilder sb = new StringBuilder();
			sb.append(wsc.getTxtWeekDistance().getText() + ",");
			for (int i = 1; i < 8; i++) {
				if (wsc.getWeekdays()[i].getSelection()) {
					sb.append(i);
				}
			}
			appointment.setSeriesPatternString(sb.toString());
			break;
		case MONTHLY:
			appointment.setSeriesPatternString(msc.getDay() + "");
			break;
		case YEARLY:
			appointment.setSeriesPatternString(
				decimalFormat.format(ysc.getDay()) + decimalFormat.format(ysc.getMonth()));
			break;
		default:
			break;
		}
		// set ending pattern
		switch (appointment.getEndingType()) {
		case AFTER_N_OCCURENCES:
			appointment.setEndingPatternString(txtEndsAfterNOccurences.getText());
			break;
		case ON_SPECIFIC_DATE:
			appointment
				.setEndingPatternString(dateFormatter.format(appointment.getSeriesEndDate()));
			break;
		}
	}
	
	private boolean txtDataIsMatchingContact(){
		return txtContactSearch.getData() instanceof IContact
			&& ((IContact) txtContactSearch.getData()).getLabel()
				.equals(txtContactSearch.getText());
	}
}
