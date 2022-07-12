package at.medevit.elexis.agenda.ui.dialog;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class YearlySeriesComposite extends Composite {
	private Text txtDay;
	private Text txtMonth;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public YearlySeriesComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Label lblAtThe = new Label(this, SWT.NONE);
		lblAtThe.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAtThe.setText(Messages.YearlySeriesComposite_lblAtThe_text); // $NON-NLS-1$

		txtDay = new Text(this, SWT.BORDER);
		txtDay.setTextLimit(2);
		txtDay.setText("15"); //$NON-NLS-1$
		GridData gd_txtDay = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtDay.widthHint = 30;
		txtDay.setLayoutData(gd_txtDay);

		Label lblAtMonth = new Label(this, SWT.NONE);
		lblAtMonth.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAtMonth.setText(Messages.YearlySeriesComposite_lblAtMonth_text); // $NON-NLS-1$

		txtMonth = new Text(this, SWT.BORDER);
		txtMonth.setTextLimit(2);
		txtMonth.setText("6"); //$NON-NLS-1$
		GridData gd_txtMonth = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtMonth.widthHint = 30;
		txtMonth.setLayoutData(gd_txtMonth);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public int getDay() {
		return Integer.parseInt(txtDay.getText());
	}

	public void setDay(int day) {
		txtDay.setText(day + StringUtils.EMPTY);
	}

	public int getMonth() {
		return Integer.parseInt(txtMonth.getText());
	}

	public void setMonth(int month) {
		txtMonth.setText(month + StringUtils.EMPTY);
	}
}
