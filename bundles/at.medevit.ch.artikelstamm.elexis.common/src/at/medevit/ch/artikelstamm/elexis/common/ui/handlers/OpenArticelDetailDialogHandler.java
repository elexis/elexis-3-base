package at.medevit.ch.artikelstamm.elexis.common.ui.handlers;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Display;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.elexis.common.ui.ArtikelstammDetailDialog;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.services.holder.ContextServiceHolder;

public class OpenArticelDetailDialogHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		Optional<IPrescription> prescription =
			ContextServiceHolder.get().getTyped(IPrescription.class);
		if (prescription.isPresent()) {
			if (prescription.get().getArticle() instanceof IArtikelstammItem) {
				ArtikelstammDetailDialog dd =
					new ArtikelstammDetailDialog(Display.getDefault().getActiveShell(),
						(IArtikelstammItem) prescription.get().getArticle());
				dd.open();
			} else {
				throw new ExecutionException(
					"Invalid article type " + prescription.get().getArticle().getClass().getName());
			}
		}
		return null;
	}
	
}
