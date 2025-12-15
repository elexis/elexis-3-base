package ch.elexis.base.ch.arzttarife.ui.handler;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IBilled;
import ch.elexis.views.TardocDetailDialog;

public class TardocDetailHandler extends AbstractHandler {

	public static String CMDID = "ch.elexis.base.ch.arzttarife.tardoc.detail";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		if (shell != null) {
			Optional<IBilled> billed = ContextServiceHolder.get().getTyped(IBilled.class);
			if (billed.isPresent()) {
				if (billed.get().getBillable() instanceof ITardocLeistung) {
					new TardocDetailDialog(shell, billed.get()).open();
				}
			}
		}
		return shell;
	}

	@Override
	public boolean isEnabled() {
		Optional<IBilled> billed = ContextServiceHolder.get().getTyped(IBilled.class);
		if (billed.isPresent()) {
			return billed.get().getBillable() instanceof ITardocLeistung;
		}
		return false;
	}
}
