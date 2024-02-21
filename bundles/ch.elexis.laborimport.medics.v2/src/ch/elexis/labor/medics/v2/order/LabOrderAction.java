package ch.elexis.labor.medics.v2.order;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.labor.medics.v2.MedicsActivator;
import ch.elexis.labor.medics.v2.MedicsPreferencePage;
import ch.elexis.labor.medics.v2.Messages;

public class LabOrderAction extends Action {

	private LabOrderActionHl7 labOrderHl7;

	public LabOrderAction() {
		setId("laborder"); //$NON-NLS-1$
		setImageDescriptor(MedicsActivator.getImageDescriptor("rsc/medics16.png")); //$NON-NLS-1$
		setText(Messages.LabOrderAction_nameAction);

		labOrderHl7 = new LabOrderActionHl7();
	}

	@Override
	public void run() {
		if (StringUtils.isEmpty(ConfigServiceHolder.get().get(MedicsPreferencePage.CFG_MEDICS_ORDER_API, ""))) {
			labOrderHl7.run();
		} else {
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
	}

	private boolean isUrl(String response) {
		return response != null && response.startsWith("http");
	}
}
