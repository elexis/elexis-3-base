package at.medevit.elexis.inbox.core.elements;

import at.medevit.elexis.inbox.model.IInboxElementsProvider;
import ch.elexis.core.data.events.ElexisEventDispatcher;

public class CoreElements implements IInboxElementsProvider {
	
	private LabResultCreateListener labResultListener;
	
	@Override
	public void activate(){
		// add inbox creation of LabResult
		labResultListener = new LabResultCreateListener();
		ElexisEventDispatcher.getInstance().addListeners(labResultListener);
	}
	
	@Override
	public void deactivate(){
		ElexisEventDispatcher.getInstance().removeListeners(labResultListener);
		labResultListener = null;
	}
}
