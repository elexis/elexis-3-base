package ch.medshare.mediport;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;

import ch.medshare.mediport.gui.ShowErrorInvoices;
import ch.medshare.mediport.util.MediPortHelper;

public class MediportStartup implements IStartup {
	
	public void earlyStartup(){
		int count = MediPortHelper.getReturnFiles();
		if (count > 0) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run(){
					// Verzeichnisse Ueberpruefen
					ShowErrorInvoices dialog =
						new ShowErrorInvoices(null, MediPortHelper.getCurrentClient());
					dialog.open();
				}
			});
		}
	}
	
}
