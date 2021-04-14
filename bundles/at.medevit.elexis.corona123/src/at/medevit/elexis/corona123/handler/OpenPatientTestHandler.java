package at.medevit.elexis.corona123.handler;

import ch.elexis.data.Patient;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenPatientTestHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		
		IStructuredSelection selection = HandlerUtil.getCurrentStructuredSelection(event);
		if (selection != null && !selection.isEmpty()
			&& selection.getFirstElement() instanceof Patient) {
			Patient patient = (Patient) selection.getFirstElement();
			openPatientTest(patient);
		}
		
		return null;
	}
	
	private void openPatientTest(Patient patient){
		if (UrlBuilder.isOrgId()) {
			String baseUrl = UrlBuilder.getTestBaseUrl();
			
			String patientParameters = UrlBuilder.getPatientParameters(patient);
			
			Program.launch(baseUrl + "?" + patientParameters);
		} else {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
				"Es ist keine corona123 Organisations ID konfiguriert.");
		}
	}
	
}
