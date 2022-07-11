package at.medevit.elexis.emediplan.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import at.medevit.elexis.emediplan.StartupHandler;

public class OpenImportHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String emediplan = event.getParameter("at.medevit.elexis.emediplan.ui.openImport.parameter.emediplan"); //$NON-NLS-1$
		String patientid = event.getParameter("at.medevit.elexis.emediplan.ui.openImport.parameter.patientid"); //$NON-NLS-1$
		StartupHandler.openEMediplanImportDialog(emediplan, patientid);
		return null;
	}

}
