package ch.elexis.agenda.series.ui;

import static ch.elexis.agenda.series.SerienTermin.decimalFormat;

import java.util.Calendar;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

import ch.elexis.actions.Activator;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.series.EndingType;
import ch.elexis.agenda.series.SerienTermin;
import ch.elexis.agenda.series.SeriesType;
import ch.elexis.agenda.series.ui.composite.MonthlySeriesComposite;
import ch.elexis.agenda.series.ui.composite.WeeklySeriesComposite;
import ch.elexis.agenda.series.ui.composite.YearlySeriesComposite;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.data.Kontakt;
import ch.rgw.tools.TimeTool;

public class SerienTerminDialog extends TitleAreaDialog {
	private DataBindingContext m_bindingContext;
	
	private Text txtEndsAfterNOccurences;
	private Group groupData;
	private Text txtReason;
	private Text txtContact;
	
	private SerienTermin serienTermin;
	private DateTime dateTimeBegin;
	private DateTime dateTimeEnd;
	private DateTime dateTimeBeginOfSeries;
	private Button btnEndsAfter;
	private Button btnEndsOn;
	
	private CTabFolder tabFolderSeriesPattern;
	
	private WeeklySeriesComposite wsc;
	private MonthlySeriesComposite msc;
	private YearlySeriesComposite ysc;
	
	private DateTime dateEndsOn;
	
	private Combo comboArea;
	
	private Spinner durationSpinner;
	
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public SerienTerminDialog(Shell parentShell, SerienTermin serienTermin){
		super(parentShell);
		this.serienTermin = serienTermin;
		if (this.serienTermin == null)
			this.serienTermin = new SerienTermin();
	}
	
	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent){
		Composite area = (Composite) super.createDialogArea(parent);
		
		setTitleImage(ResourceManager.getPluginImage("ch.elexis.agenda", "icons/recurringDate.png"));
		setMessage(Messages.getString("SerienTerminDialog.this.message")); //$NON-NLS-1$
		
		Group grpTermin = new Group(area, SWT.NONE);
		grpTermin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpTermin.setLayout(new GridLayout(6, false));
		grpTermin.setText(Messages.getString("SerienTerminDialog.grpTermin.text")); //$NON-NLS-1$
		
		Label lblBeginn = new Label(grpTermin, SWT.NONE);
		lblBeginn.setText(Messages.getString("SerienTerminDialog.lblBeginn.text")); //$NON-NLS-1$
		
		dateTimeBegin = new DateTime(grpTermin, SWT.BORDER | SWT.TIME | SWT.SHORT);
		dateTimeBegin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				updateSpinner();
			}
		});
		
		Label lblEnde = new Label(grpTermin, SWT.NONE);
		lblEnde.setText(Messages.getString("SerienTerminDialog.lblEnde.text")); //$NON-NLS-1$
		
		dateTimeEnd = new DateTime(grpTermin, SWT.BORDER | SWT.TIME | SWT.SHORT);
		dateTimeEnd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				updateSpinner();
			}
		});
		
		Label lblDauer = new Label(grpTermin, SWT.NONE);
		lblDauer.setText(Messages.getString("SerienTerminDialog.lblDauer.text")); //$NON-NLS-1$
		
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
			grpSerienmuster.setText(Messages.getString("SerienTerminDialog.grpSerienmuster.text")); //$NON-NLS-1$
			
			tabFolderSeriesPattern = new CTabFolder(grpSerienmuster, SWT.BORDER);
			tabFolderSeriesPattern
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			tabFolderSeriesPattern.setSelectionBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
			tabFolderSeriesPattern.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					serienTermin.setSeriesType((SeriesType) e.item.getData());
				}
			});
			
			CTabItem tbtmDaily = new CTabItem(tabFolderSeriesPattern, SWT.NONE);
			tbtmDaily.setText(Messages.getString("SerienTerminDialog.tbtmDaily.text")); //$NON-NLS-1$
			tbtmDaily.setData(SeriesType.DAILY);
			
			Label lblNoConfigurationNecessary = new Label(tabFolderSeriesPattern, SWT.NONE);
			tbtmDaily.setControl(lblNoConfigurationNecessary);
			lblNoConfigurationNecessary.setText(Messages
				.getString("SerienTerminDialog.lblNoConfigurationNecessary.text")); //$NON-NLS-1$
			
			CTabItem tbtmWeekly = new CTabItem(tabFolderSeriesPattern, SWT.NONE);
			tbtmWeekly.setText(Messages.getString("SerienTerminDialog.tbtmWeekly.text")); //$NON-NLS-1$
			wsc = new WeeklySeriesComposite(tabFolderSeriesPattern, SWT.NONE);
			tbtmWeekly.setControl(wsc);
			tbtmWeekly.setData(SeriesType.WEEKLY);
			
			CTabItem tbtmMonthly = new CTabItem(tabFolderSeriesPattern, SWT.NONE);
			tbtmMonthly.setText(Messages.getString("SerienTerminDialog.tbtmMonthly.text")); //$NON-NLS-1$
			msc = new MonthlySeriesComposite(tabFolderSeriesPattern, SWT.NONE);
			tbtmMonthly.setControl(msc);
			new Label(msc, SWT.NONE);
			tbtmMonthly.setData(SeriesType.MONTHLY);
			
			CTabItem tbtmYearly = new CTabItem(tabFolderSeriesPattern, SWT.NONE);
			tbtmYearly.setText(Messages.getString("SerienTerminDialog.tbtmYearly.text")); //$NON-NLS-1$
			ysc = new YearlySeriesComposite(tabFolderSeriesPattern, SWT.NONE);
			tbtmYearly.setControl(ysc);
			tbtmYearly.setData(SeriesType.YEARLY);
		}
		Group grpSeriendauer = new Group(area, SWT.NONE);
		grpSeriendauer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpSeriendauer.setLayout(new GridLayout(3, false));
		grpSeriendauer.setText(Messages.getString("SerienTerminDialog.grpSeriendauer.text")); //$NON-NLS-1$
		
		Label beginOfSeries = new Label(grpSeriendauer, SWT.NONE);
		beginOfSeries.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		beginOfSeries.setText(Messages.getString("SerienTerminDialog.beginOfSeries.text")); //$NON-NLS-1$
		
		dateTimeBeginOfSeries = new DateTime(grpSeriendauer, SWT.BORDER | SWT.DROP_DOWN | SWT.LONG);
		dateTimeBeginOfSeries.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		dateTimeBeginOfSeries.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				TimeTool tt = new TimeTool(serienTermin.getSeriesStartDate());
				wsc.setWeekNumberLabel(tt.get(Calendar.WEEK_OF_YEAR), tt.get(Calendar.YEAR));
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
		btnEndsAfter.setText(Messages.getString("SerienTerminDialog.btnEndsAfter.text")); //$NON-NLS-1$
		btnEndsAfter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				serienTermin.setEndingType(EndingType.AFTER_N_OCCURENCES);
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
		lblNewLabel.setText(Messages.getString("SerienTerminDialog.lblNewLabel.text")); //$NON-NLS-1$
		
		btnEndsOn = new Button(composite, SWT.RADIO);
		btnEndsOn.setText(Messages.getString("SerienTerminDialog.btnEndsOn.text")); //$NON-NLS-1$
		btnEndsOn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				serienTermin.setEndingType(EndingType.ON_SPECIFIC_DATE);
			}
		});
		
		dateEndsOn = new DateTime(composite, SWT.BORDER);
		
		groupData = new Group(area, SWT.NONE);
		groupData.setText(Messages.getString("SerienTerminDialog.groupData.text")); //$NON-NLS-1$
		groupData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		groupData.setLayout(new GridLayout(4, false));
		
		txtReason = new Text(groupData, SWT.BORDER);
		txtReason.setMessage(Messages.getString("SerienTerminDialog.txtReason.message")); //$NON-NLS-1$
		txtReason.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		
		Link linkCustomText = new Link(groupData, SWT.NONE);
		linkCustomText.setText(Messages.getString("SerienTerminDialog.linkCustomText.text")); //$NON-NLS-1$
		linkCustomText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				InputDialog inp =
					new InputDialog(getShell(), "Enter Text",
						"Enter Text/Name for the appointment", "", null); //$NON-NLS-1$
				if (inp.open() == Dialog.OK) {
					txtContact.setText(inp.getValue());
					serienTermin.setContact(null);
				}
			}
		});
		
		Link linkSelectContact = new Link(groupData, SWT.NONE);
		linkSelectContact.setText(Messages.getString("SerienTerminDialog.linkSelectContact.text")); //$NON-NLS-1$
		linkSelectContact.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				KontaktSelektor ksl =
					new KontaktSelektor(getShell(), Kontakt.class, "Datum zuordnen",
						"Please select the contact the date is assigned to", Kontakt.DEFAULT_SORT);
				if (ksl.open() == Dialog.OK) {
					serienTermin.setContact((Kontakt) ksl.getSelection());
					if (serienTermin.getContact() != null) {
						txtContact.setText(serienTermin.getContact().getLabel());
					}
				}
			}
		});
		
		Label lblArea = new Label(groupData, SWT.NONE);
		lblArea.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblArea.setText(Messages.getString("SerienTerminDialog.lblArea.text")); //$NON-NLS-1$
		
		comboArea = new Combo(groupData, SWT.NONE);
		comboArea.setItems(Activator.getDefault().getResources());
		comboArea.setText(Activator.getDefault().getActResource());
		comboArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				Activator.getDefault().setActResource(comboArea.getText());
			}
		});
		
		txtContact = new Text(groupData, SWT.BORDER);
		txtContact.setMessage(Messages.getString("SerienTerminDialog.txtContact.message")); //$NON-NLS-1$
		txtContact.setEditable(false);
		txtContact.setTextLimit(80);
		txtContact.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		
		m_bindingContext = initDataBindings();
		
		initDialog();
		
		return area;
	}
	
	private void initDialog(){
		if (serienTermin.getContact() != null) {
			setTitle(serienTermin.getContact().getLabel());
			txtContact.setText(serienTermin.getContact().getLabel());
		} else {
			setTitle("Kein Kontakt ausgewählt.");
			txtContact.setText(serienTermin.getFreeText());
		}
		//
		switch (serienTermin.getSeriesType()) {
		case DAILY:
			tabFolderSeriesPattern.setSelection(0);
			break;
		case WEEKLY:
			tabFolderSeriesPattern.setSelection(1);
			String[] pattern = serienTermin.getSeriesPatternString().split(",");
			wsc.getTxtWeekDistance().setText(pattern[0]);
			if (pattern.length > 1) {
				for (int i = 0; i < pattern[1].length(); i++) {
					char c = pattern[1].charAt(i);
					wsc.getWeekdays()[Character.getNumericValue(c)].setSelection(true);
				}
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(serienTermin.getSeriesStartDate());
			wsc.setWeekNumberLabel(cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.YEAR));
			break;
		case MONTHLY:
			tabFolderSeriesPattern.setSelection(2);
			msc.setDay(Integer.parseInt(serienTermin.getSeriesPatternString()));
			break;
		case YEARLY:
			tabFolderSeriesPattern.setSelection(3);
			ysc.setDay(Integer.parseInt(serienTermin.getSeriesPatternString().substring(0, 2)));
			ysc.setMonth(Integer.parseInt(serienTermin.getSeriesPatternString().substring(2, 4)));
			break;
		default:
			break;
		}
		//
		switch (serienTermin.getEndingType()) {
		case AFTER_N_OCCURENCES:
			btnEndsAfter.setSelection(true);
			txtEndsAfterNOccurences.setText(serienTermin.getEndingPatternString());
			break;
		case ON_SPECIFIC_DATE:
			btnEndsOn.setSelection(true);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(serienTermin.getEndsOnDate());
			dateEndsOn.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DATE));
			break;
		default:
			break;
		}
		//
		updateSpinner();
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
		button.setText(Messages.getString("SerienTerminDialog.other.text")); //$NON-NLS-1$
		Button button_1 = createButton(parent, IDialogConstants.STOP_ID, "remove series", false);
		button_1.setText(Messages.getString("SerienTerminDialog.other.text_1")); //$NON-NLS-1$
		
	}
	
	@Override
	protected void buttonPressed(int buttonId){
		super.buttonPressed(buttonId);
		switch (buttonId) {
		case Dialog.OK:
			serienTermin.persist();
			close();
			break;
		case IDialogConstants.STOP_ID:
			serienTermin.delete(false);
			setReturnCode(IDialogConstants.STOP_ID);
			close();
			break;
		default:
			break;
		}
		ElexisEventDispatcher.reload(Termin.class);
	}
	
	@Override
	protected void okPressed(){
		switch (serienTermin.getSeriesType()) {
		case DAILY:
			switch (serienTermin.getEndingType()) {
			case AFTER_N_OCCURENCES:
				serienTermin.setSeriesPatternString(txtEndsAfterNOccurences.getText());
				break;
			case ON_SPECIFIC_DATE:
				serienTermin.setSeriesPatternString(SerienTermin.dateFormat.format(serienTermin
					.getEndsOnDate()));
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
			serienTermin.setSeriesPatternString(sb.toString());
			break;
		case MONTHLY:
			serienTermin.setSeriesPatternString(msc.getDay() + "");
			break;
		case YEARLY:
			serienTermin.setSeriesPatternString(decimalFormat.format(ysc.getDay())
				+ decimalFormat.format(ysc.getMonth()));
			break;
		default:
			break;
		}
		if (serienTermin.getContact() == null) {
			serienTermin.setFreeText(txtContact.getText());
		}
		
		System.out.println(serienTermin);
		super.okPressed();
	}
	
	protected DataBindingContext initDataBindings(){
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeSelectionDateTimeBeginObserveWidget =
			WidgetProperties.selection().observe(dateTimeBegin);
		IObservableValue beginTimeSerienTerminObserveValue =
			PojoProperties.value("beginTime").observe(serienTermin);
		bindingContext.bindValue(observeSelectionDateTimeBeginObserveWidget,
			beginTimeSerienTerminObserveValue, null, null);
		//
		IObservableValue observeSelectionDateTimeEndObserveWidget =
			WidgetProperties.selection().observe(dateTimeEnd);
		IObservableValue endTimeSerienTerminObserveValue =
			PojoProperties.value("endTime").observe(serienTermin);
		bindingContext.bindValue(observeSelectionDateTimeEndObserveWidget,
			endTimeSerienTerminObserveValue, null, null);
		//
		IObservableValue observeSelectionDateTimeBeginOfSeriesObserveWidget =
			WidgetProperties.selection().observe(dateTimeBeginOfSeries);
		IObservableValue seriesStartDateSerienTerminObserveValue =
			PojoProperties.value("seriesStartDate").observe(serienTermin);
		bindingContext.bindValue(observeSelectionDateTimeBeginOfSeriesObserveWidget,
			seriesStartDateSerienTerminObserveValue, null, null);
		//
		IObservableValue observeTextTxtReasonObserveWidget =
			WidgetProperties.text(SWT.Modify).observe(txtReason);
		IObservableValue reasonSerienTerminObserveValue =
			PojoProperties.value("reason").observe(serienTermin);
		bindingContext.bindValue(observeTextTxtReasonObserveWidget, reasonSerienTerminObserveValue,
			null, null);
		//
		IObservableValue observeSelectionDateEndsOnObserveWidget =
			WidgetProperties.selection().observe(dateEndsOn);
		IObservableValue endsOnDateSerienTerminObserveValue =
			PojoProperties.value("endsOnDate").observe(serienTermin);
		bindingContext.bindValue(observeSelectionDateEndsOnObserveWidget,
			endsOnDateSerienTerminObserveValue, null, null);
		//
		IObservableValue observeTextTxtEndsAfterNOccurencesObserveWidget =
			WidgetProperties.text(SWT.Modify).observe(txtEndsAfterNOccurences);
		IObservableValue endsAfterNDatesSerienTerminObserveValue =
			PojoProperties.value("endsAfterNDates").observe(serienTermin);
		bindingContext.bindValue(observeTextTxtEndsAfterNOccurencesObserveWidget,
			endsAfterNDatesSerienTerminObserveValue, null, null);
		//
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
	
	public SerienTermin getSerienTermin(){
		return serienTermin;
	}
}
