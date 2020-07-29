package at.medevit.elexis.outbox.ui.command;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

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
			event.getParameter("at.medevit.elexis.outbox.ui.command.createElementNoUi.dburi");
		if (StringUtils.isNotEmpty(dburi)) {
			OutboxServiceComponent.getService().createOutboxElement(patient, mandant,
				OutboxElementType.DB.getPrefix() + dburi);
		}
		return null;
	}
}
