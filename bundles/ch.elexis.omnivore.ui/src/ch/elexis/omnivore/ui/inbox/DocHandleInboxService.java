package ch.elexis.omnivore.ui.inbox;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import at.medevit.elexis.inbox.model.IInboxElementService;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.services.EncounterServiceHolder;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.ui.preferences.PreferencePage;

@Component(property = EventConstants.EVENT_TOPIC + "=" + ElexisEventTopics.EVENT_CREATE)
public class DocHandleInboxService implements EventHandler {
	
	@Reference
	private IInboxElementService service;
	
	@Reference
	private IConfigService configService;
	
	private void createInboxElement(IDocumentHandle docHandle){
		if (docHandle != null && !docHandle.isCategory()) {
			Optional<IEncounter> encounter =
				EncounterServiceHolder.get().getLatestEncounter(docHandle.getPatient());
			IMandator mandator = null;
			if (encounter.isPresent()) {
				mandator = encounter.get().getMandator();
			} else {
				mandator = ContextServiceHolder.get().getActiveMandator().orElse(null);
			}
			if (mandator != null) {
				service.createInboxElement(docHandle.getPatient(), mandator, docHandle);
			}
		}
	}
	
	@Override
	public void handleEvent(Event event){
		boolean showCreatedInInbox =
			configService.get(PreferencePage.GLOBAL_SHOW_CREATED_IN_INBOX, true);
		if (showCreatedInInbox) {
			if (event.getProperty(ElexisEventTopics.ECLIPSE_E4_DATA) instanceof IDocumentHandle) {
				createInboxElement(
					(IDocumentHandle) event.getProperty(ElexisEventTopics.ECLIPSE_E4_DATA));
			}
		}
		
	}
}
