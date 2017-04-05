package at.medevit.ch.artikelstamm.elexis.common.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Display;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.elexis.common.ui.ArtikelstammDetailDialog;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Prescription;

public class OpenArticelDetailDialogHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		Prescription prescription =
			(Prescription) ElexisEventDispatcher.getSelected(Prescription.class);
		if (prescription != null) {
			if (prescription.getArtikel() instanceof IArtikelstammItem) {
				ArtikelstammDetailDialog dd =
					new ArtikelstammDetailDialog(Display.getDefault().getActiveShell(),
						(IArtikelstammItem) prescription.getArtikel());
				dd.open();
			} else {
				throw new ExecutionException(
					"Invalid article type " + prescription.getArtikel().getClass().getName());
			}
		}
		return null;
	}
	
}
