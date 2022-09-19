package ch.elexis.agenda.series.ui;

import static ch.elexis.agenda.series.SerienTermin.decimalFormat;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
	private static final int DAYS_OF_WEEK = 7;
	private static final int APPLY = 0;
	private static final int CANCEL = 2;

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
	private Combo comboStatus;

	private Spinner durationSpinner;

	private int result;
	private boolean noedit;

	/**
	 * Create the dialog.
	 *
	 * @param parentShell
	 */
	public SerienTerminDialog(Shell parentShell, SerienTermin serienTermin) {
		super(parentShell);
		this.serienTermin = serienTermin;
		if (this.serienTermin == null) {
			noedit = false;
			this.serienTermin = new SerienTermin();
		} else {
			noedit = true;
		}
	}

	/**
	 * Create contents of the dialog.
	 *
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);

		setTitleImage(ResourceManager.getPluginImage("ch.elexis.agenda", "icons/recurringDate.png"));
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
			public void widgetSelected(SelectionEvent e) {
				updateSpinner();
			}
		});

		Label lblEnde = new Label(grpTermin, SWT.NONE);
		lblEnde.setText(Messages.SerienTerminDialog_lblEnde_text);

		dateTimeEnd = new DateTime(grpTermin, SWT.BORDER | SWT.TIME | SWT.SHORT);
		dateTimeEnd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
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
			public void widgetSelected(SelectionEvent e) {
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
			tabFolderSeriesPattern.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			tabFolderSeriesPattern.setSelectionBackground(
					Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
			tabFolderSeriesPattern.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					serienTermin.setSeriesType((SeriesType) e.item.getData());
				}
			});

			CTabItem tbtmDaily = new CTabItem(tabFolderSeriesPattern, SWT.NONE);
			tbtmDaily.setText(Messages.SerienTerminDialog_tbtmDaily_text);
			tbtmDaily.setData(SeriesType.DAILY);

			Label lblNoConfigurationNecessary = new Label(tabFolderSeriesPattern, SWT.NONE);
			tbtmDaily.setControl(lblNoConfigurationNecessary);
			lblNoConfigurationNecessary.setText(Messages.SerienTerminDialog_lblNoConfigurationNecessary_text);

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
			public void widgetSelected(SelectionEvent e) {
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
		btnEndsAfter.setText(Messages.SerienTerminDialog_btnEndsAfter_text);
		btnEndsAfter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
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
		txtEndsAfterNOccurences.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setText(Messages.SerienTerminDialog_lblNewLabel_text);

		btnEndsOn = new Button(composite, SWT.RADIO);
		btnEndsOn.setText(Messages.SerienTerminDialog_btnEndsOn_text);
		btnEndsOn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				serienTermin.setEndingType(EndingType.ON_SPECIFIC_DATE);
			}
		});

		dateEndsOn = new DateTime(composite, SWT.BORDER);

		groupData = new Group(area, SWT.NONE);
		groupData.setText(Messages.SerienTerminDialog_groupData_text);
		groupData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		groupData.setLayout(new GridLayout(4, false));

		txtReason = new Text(groupData, SWT.BORDER);
		txtReason.setMessage(Messages.SerienTerminDialog_txtReason_message);
		txtReason.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		Link linkCustomText = new Link(groupData, SWT.NONE);
		linkCustomText.setText(Messages.SerienTerminDialog_linkCustomText_text);
		linkCustomText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog inp = new InputDialog(getShell(), "Enter Text", "Enter Text/Name for the appointment",
						StringUtils.EMPTY, null);
				if (inp.open() == Dialog.OK) {
					txtContact.setText(inp.getValue());
					serienTermin.setContact(null);
				}
			}
		});

		Link linkSelectContact = new Link(groupData, SWT.NONE);
		linkSelectContact.setText(Messages.SerienTerminDialog_linkSelectContact_text);
		linkSelectContact.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				KontaktSelektor ksl = new KontaktSelektor(getShell(), Kontakt.class, "Datum zuordnen",
						"Please select the contact the date is assigned to", Kontakt.DEFAULT_SORT);
				if (ksl.open() == Dialog.OK) {
					serienTermin.setContact((Kontakt) ksl.getSelection());
					if (serienTermin.getContact() != null) {
						txtContact.setText(serienTermin.getContact().getLabel());
					}
				}
			}
		});

		Label lbl = new Label(groupData, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lbl.setText(Messages.SerienTerminDialog_lblArea_text);

		comboArea = new Combo(groupData, SWT.NONE);
		comboArea.setItems(Activator.getDefault().getResources());
		comboArea.setText(Activator.getDefault().getActResource());
		comboArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Activator.getDefault().setActResource(comboArea.getText());
			}
		});

		lbl = new Label(groupData, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 3, 1));
		lbl.setText(Messages.SerienTerminDialog_lblStatus_text);

		comboStatus = new Combo(groupData, SWT.NONE);
		comboStatus.setItems(Termin.TerminStatus);
		comboStatus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				serienTermin.setStatus(comboStatus.getText());
			}
		});


		txtContact = new Text(groupData, SWT.BORDER);
		txtContact.setMessage(Messages.SerienTerminDialog_txtContact_message);
		txtContact.setEditable(false);
		txtContact.setTextLimit(80);
		txtContact.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		m_bindingContext = initDataBindings();

		initDialog();

		if (noedit) {
			disableAll(area);
		}

		return area;
	}

	private void disableAll(Control widget) {
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

	private void initDialog() {
		if (serienTermin.getContact() != null) {
			setTitle(serienTermin.getContact().getLabel());
			txtContact.setText(serienTermin.getContact().getLabel());
		} else {
			setTitle("Kein Kontakt ausgewählt.");
			txtContact.setText(serienTermin.getFreeText());
		}
		if (StringUtils.isNotBlank(serienTermin.getStatus())) {
			comboStatus.setText(serienTermin.getStatus());
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
			dateEndsOn.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
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
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		Button button = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		button.setText(Messages.SerienTerminDialog_other_text);
		Button button_1 = createButton(parent, IDialogConstants.STOP_ID, "remove series", false);
		button_1.setText(Messages.SerienTerminDialog_other_text_1);

	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
		switch (buttonId) {
		case Dialog.OK:
			if (noedit) {
				close();
			} else if (result == APPLY) {
				serienTermin.persist();
				close();
			}
			break;
		case IDialogConstants.STOP_ID:
			cancelEntry();
			break;
		default:
			break;
		}
		ElexisEventDispatcher.reload(Termin.class);
	}

	private void cancelEntry() {
		serienTermin.delete(false);
		setReturnCode(IDialogConstants.STOP_ID);
		close();
	}

	@Override
	protected void okPressed() {
		if (!noedit) {
			switch (serienTermin.getSeriesType()) {
			case DAILY:
				switch (serienTermin.getEndingType()) {
				case AFTER_N_OCCURENCES:
					serienTermin.setSeriesPatternString(txtEndsAfterNOccurences.getText());
					break;
				case ON_SPECIFIC_DATE:
					serienTermin.setSeriesPatternString(SerienTermin.dateFormat.format(serienTermin.getEndsOnDate()));
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
				serienTermin.setSeriesPatternString(msc.getDay() + StringUtils.EMPTY);
				break;
			case YEARLY:
				serienTermin.setSeriesPatternString(
						decimalFormat.format(ysc.getDay()) + decimalFormat.format(ysc.getMonth()));
				break;
			default:
				break;
			}
			if (serienTermin.getContact() == null) {
				serienTermin.setFreeText(txtContact.getText());
			}

			// ask user about next step (keep, change, cancel) in case of a lock time
			// collision
			if (serienTermin.collidesWithLockTimes()) {
				MessageDialog collisionDialog = new MessageDialog(getShell(),
						Messages.SerienTerminDialog_dlgLockTimesConflict, getTitleImageLabel().getImage(),
						Messages.SerienTerminDialog_dlgLockTimesSeriesConflict, MessageDialog.WARNING,
						new String[] { Messages.SerienTerminDialog_dlgBtnApplyAnyway,
								Messages.SerienTerminDialog_dlgBtnChange, Messages.SerienTerminDialog_dlgBtnCancel },
						0);

				result = collisionDialog.open();
			} else {
				result = APPLY;
				super.okPressed();
			}
		}
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeSelectionDateTimeBeginObserveWidget = WidgetProperties.selection()
				.observe(dateTimeBegin);
		IObservableValue beginTimeSerienTerminObserveValue = PojoProperties.value("beginTime").observe(serienTermin);
		bindingContext.bindValue(observeSelectionDateTimeBeginObserveWidget, beginTimeSerienTerminObserveValue, null,
				null);
		//
		IObservableValue observeSelectionDateTimeEndObserveWidget = WidgetProperties.selection().observe(dateTimeEnd);
		IObservableValue endTimeSerienTerminObserveValue = PojoProperties.value("endTime").observe(serienTermin);
		bindingContext.bindValue(observeSelectionDateTimeEndObserveWidget, endTimeSerienTerminObserveValue, null, null);
		//
		IObservableValue observeSelectionDateTimeBeginOfSeriesObserveWidget = WidgetProperties.selection()
				.observe(dateTimeBeginOfSeries);
		IObservableValue seriesStartDateSerienTerminObserveValue = PojoProperties.value("seriesStartDate")
				.observe(serienTermin);
		bindingContext.bindValue(observeSelectionDateTimeBeginOfSeriesObserveWidget,
				seriesStartDateSerienTerminObserveValue, null, null);
		//
		IObservableValue observeTextTxtReasonObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtReason);
		IObservableValue reasonSerienTerminObserveValue = PojoProperties.value("reason").observe(serienTermin);
		bindingContext.bindValue(observeTextTxtReasonObserveWidget, reasonSerienTerminObserveValue, null, null);
		//
		IObservableValue observeSelectionDateEndsOnObserveWidget = WidgetProperties.selection().observe(dateEndsOn);
		IObservableValue endsOnDateSerienTerminObserveValue = PojoProperties.value("endsOnDate").observe(serienTermin);
		bindingContext.bindValue(observeSelectionDateEndsOnObserveWidget, endsOnDateSerienTerminObserveValue, null,
				null);
		//
		IObservableValue observeTextTxtEndsAfterNOccurencesObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(txtEndsAfterNOccurences);
		IObservableValue endsAfterNDatesSerienTerminObserveValue = PojoProperties.value("endsAfterNDates")
				.observe(serienTermin);
		bindingContext.bindValue(observeTextTxtEndsAfterNOccurencesObserveWidget,
				endsAfterNDatesSerienTerminObserveValue, null, null);
		//
		return bindingContext;
	}

	private void updateSpinner() {
		int endTime = (dateTimeEnd.getHours() * 60) + dateTimeEnd.getMinutes();
		int startTime = (dateTimeBegin.getHours() * 60) + dateTimeBegin.getMinutes();
		int result = endTime - startTime;
		if (result < 0)
			result = 0;
		durationSpinner.setSelection(result);
	}

	public SerienTermin getSerienTermin() {
		return serienTermin;
	}
}
