package at.medevit.elexis.agenda.ui.dialog;

import java.util.Calendar;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WeeklySeriesComposite extends Composite {
	private Text txtWeekDistance;
	private Button[] weekdays = new Button[8];
	private Label lblWeekNumber;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public WeeklySeriesComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(4, false));

		Label lblEvery = new Label(this, SWT.NONE);
		lblEvery.setText(Messages.WeeklySeriesComposite_lblEvery_text); // $NON-NLS-1$

		txtWeekDistance = new Text(this, SWT.BORDER);
		GridData gd_txtWeekDistance = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtWeekDistance.widthHint = 30;
		getTxtWeekDistance().setLayoutData(gd_txtWeekDistance);
		getTxtWeekDistance().setTextLimit(2);

		Label lblWeekOn = new Label(this, SWT.NONE);
		lblWeekOn.setText(Messages.WeeklySeriesComposite_lblWeekOn_text); // $NON-NLS-1$

		lblWeekNumber = new Label(this, SWT.NONE);
		lblWeekNumber.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite.horizontalSpan = 4;
		composite.setLayoutData(gd_composite);

		getWeekdays()[Calendar.MONDAY] = new Button(composite, SWT.CHECK);
		getWeekdays()[Calendar.MONDAY].setText(Messages.SerienTermin_monday);

		getWeekdays()[Calendar.TUESDAY] = new Button(composite, SWT.CHECK);
		getWeekdays()[Calendar.TUESDAY].setText(Messages.SerienTermin_tuesday);

		getWeekdays()[Calendar.WEDNESDAY] = new Button(composite, SWT.CHECK);
		getWeekdays()[Calendar.WEDNESDAY].setText(Messages.SerienTermin_wednesday);

		getWeekdays()[Calendar.THURSDAY] = new Button(composite, SWT.CHECK);
		getWeekdays()[Calendar.THURSDAY].setText(Messages.SerienTermin_thursday);

		getWeekdays()[Calendar.FRIDAY] = new Button(composite, SWT.CHECK);
		getWeekdays()[Calendar.FRIDAY].setText(Messages.SerienTermin_friday);

		getWeekdays()[Calendar.SATURDAY] = new Button(composite, SWT.CHECK);
		getWeekdays()[Calendar.SATURDAY].setText(Messages.SerienTermin_saturday);

		getWeekdays()[Calendar.SUNDAY] = new Button(composite, SWT.CHECK);
		new Label(composite, SWT.NONE);
		getWeekdays()[Calendar.SUNDAY].setText(Messages.SerienTermin_sunday);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public Text getTxtWeekDistance() {
		return txtWeekDistance;
	}

	public Button[] getWeekdays() {
		return weekdays;
	}

	public void setWeekNumberLabel(int weekNumber, int year) {
		lblWeekNumber.setText(NLS.bind(Messages.AgendaUI_WeeklySeries_week_year, weekNumber, year));
		layout();
	}
}
