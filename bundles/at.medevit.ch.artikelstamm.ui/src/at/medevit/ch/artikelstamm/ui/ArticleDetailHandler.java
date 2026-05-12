package at.medevit.ch.artikelstamm.ui;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.ui.internal.ArticleDetailDialog;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.services.holder.ContextServiceHolder;

public class ArticleDetailHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Optional<IBilled> selection = ContextServiceHolder.get().getTyped(IBilled.class);
		if (selection.isPresent()) {
			Shell shell = HandlerUtil.getActiveShell(event);
			new ArticleDetailDialog(shell, selection.get()).open();
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		Optional<IBilled> selection = ContextServiceHolder.get().getTyped(IBilled.class);
		if (selection.isPresent()) {
			IBillable billable = selection.get().getBillable();
			return billable instanceof IArtikelstammItem;
		}
		return false;
	}
}
