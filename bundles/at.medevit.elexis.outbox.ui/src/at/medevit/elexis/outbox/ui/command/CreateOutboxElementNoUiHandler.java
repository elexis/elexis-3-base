package at.medevit.elexis.outbox.ui.command;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import at.medevit.elexis.outbox.model.IOutboxElement;
import at.medevit.elexis.outbox.model.IOutboxElementService.State;
import at.medevit.elexis.outbox.model.OutboxElementType;
import at.medevit.elexis.outbox.ui.OutboxServiceComponent;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;

public class CreateOutboxElementNoUiHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		IPatient patient = ContextServiceHolder.get().getActivePatient().orElse(null);
		IMandator mandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
		
		String dburi =
			event.getParameter("at.medevit.elexis.outbox.ui.command.getOrCreateElementNoUi.dburi");
		String sentParam =
			event.getParameter("at.medevit.elexis.outbox.ui.command.getOrCreateElementNoUi.sent");
		if (StringUtils.isNotEmpty(dburi)) {
			IOutboxElement ret = null;
			boolean sent = Boolean.parseBoolean(sentParam);
			
			String uri = OutboxElementType.DB.getPrefix() + dburi;
			List<IOutboxElement> existingElements =
				OutboxServiceComponent.get().getOutboxElements(uri, State.NEW);
			if (existingElements.size() >= 1) {
				ret = existingElements.get(0);
			} else {
				ret = OutboxServiceComponent.get().createOutboxElement(patient, mandant,
					uri);
			}
			if (ret != null && sent) {
				OutboxServiceComponent.get().changeOutboxElementState(ret, State.SENT);
			}
			return ret;
		}
		return null;
	}
}
