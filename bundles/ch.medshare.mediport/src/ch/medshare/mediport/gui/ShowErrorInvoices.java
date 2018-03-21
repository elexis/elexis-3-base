package ch.medshare.mediport.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.ui.util.SWTHelper;
import ch.medshare.mediport.config.Client;

public class ShowErrorInvoices extends Dialog {
	
	private final Client client;
	
	public ShowErrorInvoices(Shell parentShell, Client client){
		super(parentShell);
		this.client = client;
	}
	
	@Override
	protected void configureShell(Shell newShell){
		super.configureShell(newShell);
		newShell.setSize(300, 300);
		newShell.setText(Messages.getString("ShowErrorInvoices.title.Antworten")); //$NON-NLS-1$
	}
	
	@Override
	public Control createDialogArea(final Composite parent){
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		Text txtInformation = new Text(comp, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		txtInformation.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		txtInformation.setEditable(false);
		
		txtInformation.setText(Messages.getString("ShowErrorInvoices.msg.Rechnungsantworten") + //$NON-NLS-1$
			Messages.getString("ShowErrorInvoices.msg2.Rechnungsantworten")); //$NON-NLS-1$
		
		new ErrorInvoiceForm(comp, SWT.NONE, client);
		return parent;
	}
}
