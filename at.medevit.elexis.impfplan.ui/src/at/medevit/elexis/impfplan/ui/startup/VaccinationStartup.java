package at.medevit.elexis.impfplan.ui.startup;

import org.eclipse.ui.IStartup;

import ch.elexis.core.data.events.ElexisEventDispatcher;

public class VaccinationStartup implements IStartup {
	
	private final VaccinationPrescriptionEventListener vpel =
		new VaccinationPrescriptionEventListener();
	
	@Override
	public void earlyStartup(){
		ElexisEventDispatcher.getInstance().addListeners(vpel);
	}
	
}
