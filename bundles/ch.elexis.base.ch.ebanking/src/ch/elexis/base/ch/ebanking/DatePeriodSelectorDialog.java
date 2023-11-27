package ch.elexis.base.ch.ebanking;

import java.util.Calendar;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;

import ch.rgw.tools.TimeTool;

public class DatePeriodSelectorDialog extends TitleAreaDialog {

	private TimeTool _startDate;
	private TimeTool _endDate;
	private DateTime startDateTime;
	private DateTime endDateTime;

	public DatePeriodSelectorDialog(Shell parentShell, TimeTool startDate, TimeTool endDate) {
		super(parentShell);
		_startDate = startDate;
		_endDate = endDate;
	}

	/**
	 * Create contents of the dialog.
	 *
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Bitte wählen Sie die anzuzeigende Zeitperiode");
		setTitle("Zeitperiode setzen");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		startDateTime = new DateTime(container, SWT.CALENDAR);
		startDateTime.setDate(_startDate.get(Calendar.YEAR), _startDate.get(Calendar.MONTH),
				_startDate.get(Calendar.DAY_OF_MONTH));
		startDateTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		endDateTime = new DateTime(container, SWT.CALENDAR);
		endDateTime.setDate(_endDate.get(Calendar.YEAR), _endDate.get(Calendar.MONTH),
				_endDate.get(Calendar.DAY_OF_MONTH));
		endDateTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		return area;
	}

	/**
	 * Create contents of the button bar.
	 *
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void okPressed() {
		_startDate.set(Calendar.YEAR, startDateTime.getYear());
		_startDate.set(Calendar.MONTH, startDateTime.getMonth());
		_startDate.set(Calendar.DAY_OF_MONTH, startDateTime.getDay());
		_endDate.set(Calendar.YEAR, endDateTime.getYear());
		_endDate.set(Calendar.MONTH, endDateTime.getMonth());
		_endDate.set(Calendar.DAY_OF_MONTH, endDateTime.getDay());
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		Point p = startDateTime.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		return new Point((p.x * 2) + 10, 300);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
}
