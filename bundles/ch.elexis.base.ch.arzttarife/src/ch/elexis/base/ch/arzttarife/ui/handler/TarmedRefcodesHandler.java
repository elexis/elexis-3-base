package ch.elexis.base.ch.arzttarife.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.views.TarmedRefcodesDialog;

public class TarmedRefcodesHandler extends AbstractHandler {

	public static String CMDID = "ch.elexis.base.ch.arzttarife.tarmed.refcodes";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		if (shell != null) {
			IEclipseContext iEclipseContext = PlatformUI.getWorkbench().getService(IEclipseContext.class);
			StructuredSelection selection = CoreUiUtil.getCommandSelection(iEclipseContext, CMDID);
			if (!selection.isEmpty() && selection.getFirstElement() instanceof IBilled) {
				IBilled billed = (IBilled) selection.getFirstElement();
				if (billed.getBillable() instanceof ITarmedLeistung) {
					new TarmedRefcodesDialog(shell, billed).open();
				}
			}
		}
		return shell;
	}

	@Override
	public boolean isEnabled() {
		IEclipseContext iEclipseContext = PlatformUI.getWorkbench().getService(IEclipseContext.class);
		StructuredSelection selection = CoreUiUtil.getCommandSelection(iEclipseContext, CMDID, false);
		if (selection != null && !selection.isEmpty() && selection.getFirstElement() instanceof IBilled) {
			IBilled billed = (IBilled) selection.getFirstElement();
			return billed.getBillable() instanceof ITarmedLeistung;
		}
		return false;
	}
}
