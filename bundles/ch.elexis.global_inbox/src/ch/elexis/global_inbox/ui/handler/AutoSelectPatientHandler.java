
package ch.elexis.global_inbox.ui.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IContextService;
import ch.elexis.global_inbox.model.GlobalInboxEntry;

public class AutoSelectPatientHandler {
	
	@Inject
	private IContextService contextService;
	
	private boolean isActive;
	
	@Inject
	public void initialize(EModelService modelService, MPart part){
		MUIElement toolItem = modelService
			.find("ch.elexis.global_inbox.directtoolitem.autoSelectPatient", part.getToolbar());
		isActive = ((MDirectToolItem) toolItem).isSelected();
	}
	
	@Execute
	public void execute(MDirectToolItem toolItem){
		isActive = toolItem.isSelected();
	}
	
	@Inject
	public void setGlobalInboxEntry(@Optional @Named(IServiceConstants.ACTIVE_SELECTION)
	GlobalInboxEntry globalInboxEntry){
		if (globalInboxEntry == null) {
			return;
		}
		
		if (isActive) {
			IPatient patient = globalInboxEntry.getPatient();
			if (patient != null) {
				contextService.setActivePatient(patient);
			}
		}
	}
	
}