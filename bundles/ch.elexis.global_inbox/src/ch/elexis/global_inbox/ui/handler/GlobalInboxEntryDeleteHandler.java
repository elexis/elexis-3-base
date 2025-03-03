
package ch.elexis.global_inbox.ui.handler;

import java.text.MessageFormat;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;

import ch.elexis.core.ui.e4.events.ElexisUiEventTopics;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.global_inbox.model.GlobalInboxEntry;
import ch.elexis.global_inbox.ui.GlobalInboxUtil;
import ch.elexis.global_inbox.ui.Messages;
import jakarta.inject.Named;

public class GlobalInboxEntryDeleteHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SELECTION) GlobalInboxEntry globalInboxEntry,
			IEventBroker eventBroker) {

		if (SWTHelper.askYesNo(Messages.InboxView_inbox,
				MessageFormat.format(Messages.InboxView_thisreallydelete, globalInboxEntry.getTitle()))) {

			// unload the document in preview, s.t. it can be deleted by the OS (Win)
			eventBroker.send(ElexisUiEventTopics.EVENT_PREVIEW_MIMETYPE_PDF, null);

			new GlobalInboxUtil().removeFiles(globalInboxEntry);
		}

	}

	@CanExecute
	public boolean canExecute(@Named(IServiceConstants.ACTIVE_SELECTION) GlobalInboxEntry globalInboxEntry) {
		return globalInboxEntry != null;
	}

}