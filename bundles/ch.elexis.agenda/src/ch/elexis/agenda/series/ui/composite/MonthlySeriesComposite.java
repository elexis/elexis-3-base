package ch.elexis.agenda.series.ui.composite;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.elexis.agenda.series.ui.Messages;

public class MonthlySeriesComposite extends Composite {
	private Text txtDay;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public MonthlySeriesComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));

		Label lblAtThe = new Label(this, SWT.NONE);
		lblAtThe.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAtThe.setText(Messages.MonthlySeriesComposite_lblAtThe_text);
		txtDay = new Text(this, SWT.BORDER);
		txtDay.setTextLimit(2);
		txtDay.setText("15");
		GridData gd_txtDay = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtDay.widthHint = 30;
		txtDay.setLayoutData(gd_txtDay);
		new Label(this, SWT.NONE);
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

}
