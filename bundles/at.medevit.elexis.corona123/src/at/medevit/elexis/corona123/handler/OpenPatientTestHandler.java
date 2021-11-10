package at.medevit.elexis.corona123.handler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IPatient;

public class OpenPatientTestHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		
		IStructuredSelection selection = HandlerUtil.getCurrentStructuredSelection(event);
		if (selection != null && !selection.isEmpty()
			&& selection.getFirstElement() instanceof IPatient) {
			IPatient patient = (IPatient) selection.getFirstElement();
			openPatientTest(patient);
		}
		
		return null;
	}
	
	private void openPatientTest(IPatient patient){
		if (UrlBuilder.isOrgId()) {
			String baseUrl = UrlBuilder.getTestBaseUrl();
			
			String patientParameters = UrlBuilder.getPatientParameters(patient);
			try {
				String testDateParameter = "testDate=" + URLEncoder.encode(
					DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()), "UTF-8");
				
				Program.launch(baseUrl + "?" + testDateParameter + "&" + patientParameters);
			} catch (UnsupportedEncodingException e) {
				LoggerFactory.getLogger(UrlBuilder.class).error("Error getting testDate parameter",
					e);
			}
		} else {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
				"Es ist keine corona123 Organisations ID konfiguriert.");
		}
	}
	
}
