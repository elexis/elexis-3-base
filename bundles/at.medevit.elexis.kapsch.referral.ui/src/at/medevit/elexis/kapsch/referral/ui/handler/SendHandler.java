package at.medevit.elexis.kapsch.referral.ui.handler;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.kapsch.referral.internal.KapschReferralServiceHolder;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Patient;

public class SendHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		Patient pat = ElexisEventDispatcher.getSelectedPatient();
		if (pat != null) {
			// start sending patient data
			Optional<String> url = KapschReferralServiceHolder.get().getPatientReferralUrl(pat);
			if (url.isPresent()) {
				Program.launch(url.get());
			} else {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
					"Es ist ein Fehler beim Aufruf der Zuweisung aufgetreten.");
			}
		}
		return null;
	}
}
