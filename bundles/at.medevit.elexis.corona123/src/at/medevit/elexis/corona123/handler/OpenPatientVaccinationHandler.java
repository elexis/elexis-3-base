package at.medevit.elexis.corona123.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import ch.elexis.core.model.IPatient;

public class OpenPatientVaccinationHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = HandlerUtil.getCurrentStructuredSelection(event);
		if (selection != null && !selection.isEmpty() && selection.getFirstElement() instanceof IPatient) {
			IPatient patient = (IPatient) selection.getFirstElement();
			openPatientVaccination(patient);
		}
		return null;
	}

	private void openPatientVaccination(IPatient patient) {
		if (UrlBuilder.isOrgId()) {
			String baseUrl = UrlBuilder.getVaccinationBaseUrl();

			String patientParameters = UrlBuilder.getPatientParameters(patient);
			patientParameters += UrlBuilder.getVaccinationDefaultParameters();

			Program.launch(baseUrl + "?" + patientParameters);
		} else {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
					"Es ist keine corona123 Organisations ID konfiguriert.");
		}
	}
}
