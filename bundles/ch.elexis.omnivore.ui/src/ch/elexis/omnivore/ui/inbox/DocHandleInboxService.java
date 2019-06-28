package ch.elexis.omnivore.ui.inbox;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import at.medevit.elexis.inbox.model.IInboxElementService;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.omnivore.data.DocHandle;

@Component(service = {})
public class DocHandleInboxService {
	private static IInboxElementService service;
	private ElexisEventListener docHandleListener;
	
	@Activate
	public void activate(){
		if (docHandleListener == null) {
			docHandleListener =
				new ElexisEventListenerImpl(DocHandle.class, ElexisEvent.EVENT_CREATE) {
					@Override
					public void run(ElexisEvent ev){
						createInboxElement((DocHandle) ev.getObject());
					}
				};
			ElexisEventDispatcher.getInstance().addListeners(docHandleListener);
		}
	}
	
	@Deactivate
	public void deactivate(){
		if (docHandleListener != null) {
			ElexisEventDispatcher.getInstance().removeListeners(docHandleListener);
			docHandleListener = null;
		}
	}
		
	@Reference
	public void bind(IInboxElementService service){
		DocHandleInboxService.service = service;
	}
	
	public void unbind(IInboxElementService service){
		DocHandleInboxService.service = null;
	}
	
	public static IInboxElementService getService(){
		return service;
	}
	
	private void createInboxElement(DocHandle docHandle)
	{
		if (docHandle != null & !docHandle.isCategory()) {
			Konsultation konsultation = docHandle.getPatient().getLastKonsultation();
			Mandant mandant = null;
			if (konsultation != null) {
				mandant = konsultation.getMandant();
			} else {
				mandant = ElexisEventDispatcher.getSelectedMandator();
			}
			getService().createInboxElement(docHandle.getPatient(), mandant, docHandle);
		}
	}
}
