package at.medevit.elexis.kapsch.referral.ui.handler;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.kapsch.referral.internal.KapschReferralServiceHolder;
import ch.elexis.core.model.Identifiable;
import ch.elexis.data.Patient;

public class SendHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		Patient pat = null;
		IWorkbenchWindow iWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		if (iWorkbenchWindow != null) {
			ISelection selection = iWorkbenchWindow.getActivePage().getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection strucSelection = (IStructuredSelection) selection;
				pat = getAsPatient(strucSelection);
			}
		}
		if (pat != null) {
			// start sending patient data
			Optional<String> url = KapschReferralServiceHolder.get().sendPatient(pat);
			if (url.isPresent()) {
				Program.launch(url.get());
			} else {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
					"Es ist ein Fehler beim Aufruf der Zuweisung aufgetreten.");
			}
		}
		return null;
	}
	
	private Patient getAsPatient(IStructuredSelection strucSelection){
		Patient ret = null;
		if (strucSelection.getFirstElement() instanceof Identifiable) {
			ret = Patient.load(((Identifiable) strucSelection.getFirstElement()).getId());
		} else {
			ret = (Patient) strucSelection.getFirstElement();
		}
		return ret;
	}
}
