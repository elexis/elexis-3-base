package at.medevit.elexis.agenda.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.model.IAppointment;

public class HistoryDialog extends Dialog {

	private String historyContent;
	private IAppointment appointment;

	public HistoryDialog(Shell parentShell, String historyContent, IAppointment appointment) {
		super(parentShell);
		this.historyContent = historyContent;
		this.appointment = appointment;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(appointment.getLabel());
		newShell.setSize(600, 400);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout());
		Browser browser = new Browser(container, SWT.NONE);
		browser.setText("<html><body>" + historyContent + "</body></html>");
		return container;
	}
}
