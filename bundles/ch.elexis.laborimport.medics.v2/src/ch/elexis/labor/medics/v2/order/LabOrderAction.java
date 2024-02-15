package ch.elexis.labor.medics.v2.order;

import java.util.Optional;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.labor.medics.v2.MedicsActivator;
import ch.elexis.labor.medics.v2.Messages;

public class LabOrderAction extends Action {

	public LabOrderAction() {
		setId("laborder"); //$NON-NLS-1$
		setImageDescriptor(MedicsActivator.getImageDescriptor("rsc/medics16.png")); //$NON-NLS-1$
		setText(Messages.LabOrderAction_nameAction);
	}

	@Override
	public void run() {
		Optional<IPatient> patient = ContextServiceHolder.get().getActivePatient();
		if (patient.isPresent()) {
			WebAis webAis = new WebAis();
			String response = webAis.createPatientAndOrder(patient.get());
			if (isUrl(response)) {
				Program.launch(response);
			} else {
				MessageDialog.openError(new Shell(), Messages.LabOrderAction_errorTitleCannotShowURL, response);
			}
		} else {
			MessageDialog.openError(new Shell(), Messages.LabOrderAction_errorTitleNoPatientSelected,
					Messages.LabOrderAction_errorMessageNoPatientSelected);
		}
	}

	private boolean isUrl(String response) {
		return response != null && response.startsWith("http");
	}
}
