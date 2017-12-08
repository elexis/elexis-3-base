package ch.elexis.agenda.series.ui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.elexis.agenda.series.ui.Messages;

import com.ibm.icu.util.Calendar;

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
	public WeeklySeriesComposite(Composite parent, int style){
		super(parent, style);
		setLayout(new GridLayout(4, false));
		
		Label lblEvery = new Label(this, SWT.NONE);
		lblEvery.setText(Messages.getString("WeeklySeriesComposite.lblEvery.text")); //$NON-NLS-1$
		
		txtWeekDistance = new Text(this, SWT.BORDER);
		GridData gd_txtWeekDistance = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtWeekDistance.widthHint = 30;
		getTxtWeekDistance().setLayoutData(gd_txtWeekDistance);
		getTxtWeekDistance().setTextLimit(2);
		
		Label lblWeekOn = new Label(this, SWT.NONE);
		lblWeekOn.setText(Messages.getString("WeeklySeriesComposite.lblWeekOn.text")); //$NON-NLS-1$
		
		lblWeekNumber = new Label(this, SWT.NONE);
		lblWeekNumber.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite.horizontalSpan = 4;
		composite.setLayoutData(gd_composite);
		
		getWeekdays()[Calendar.MONDAY] = new Button(composite, SWT.CHECK);
		getWeekdays()[Calendar.MONDAY].setText(Messages.getString("monday"));
		
		getWeekdays()[Calendar.TUESDAY] = new Button(composite, SWT.CHECK);
		getWeekdays()[Calendar.TUESDAY].setText(Messages.getString("tuesday"));
		
		getWeekdays()[Calendar.WEDNESDAY] = new Button(composite, SWT.CHECK);
		getWeekdays()[Calendar.WEDNESDAY].setText(Messages.getString("wednesday"));
		
		getWeekdays()[Calendar.THURSDAY] = new Button(composite, SWT.CHECK);
		getWeekdays()[Calendar.THURSDAY].setText(Messages.getString("thursday"));
		
		getWeekdays()[Calendar.FRIDAY] = new Button(composite, SWT.CHECK);
		getWeekdays()[Calendar.FRIDAY].setText(Messages.getString("friday"));
		
		getWeekdays()[Calendar.SATURDAY] = new Button(composite, SWT.CHECK);
		getWeekdays()[Calendar.SATURDAY].setText(Messages.getString("saturday"));
		
		getWeekdays()[Calendar.SUNDAY] = new Button(composite, SWT.CHECK);
		new Label(composite, SWT.NONE);
		getWeekdays()[Calendar.SUNDAY].setText(Messages.getString("sunday"));
	}
	
	@Override
	protected void checkSubclass(){
		// Disable the check that prevents subclassing of SWT components
	}
	
	public Text getTxtWeekDistance(){
		return txtWeekDistance;
	}
	
	public Button[] getWeekdays(){
		return weekdays;
	}
	
	public void setWeekNumberLabel(int weekNumber, int year){
		lblWeekNumber.setText(weekNumber + "/" + year);
		layout();
	}
}
