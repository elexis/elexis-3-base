package at.medevit.elexis.emediplan.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import at.medevit.elexis.emediplan.Startup;

public class OpenImportHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		String emediplan =
			event.getParameter("at.medevit.elexis.emediplan.ui.openImport.parameter.emediplan");
		String patientid =
			event.getParameter("at.medevit.elexis.emediplan.ui.openImport.parameter.patientid");
		Startup.openEMediplanImportDialog(emediplan, patientid);
		return null;
	}
	
}
