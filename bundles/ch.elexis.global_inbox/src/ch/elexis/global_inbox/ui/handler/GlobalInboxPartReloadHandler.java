
package ch.elexis.global_inbox.ui.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import ch.elexis.global_inbox.ui.parts.GlobalInboxPart;

public class GlobalInboxPartReloadHandler {

	@Execute
	public void execute(MPart part) {
		GlobalInboxPart taskResultPart = (GlobalInboxPart) part.getObject();
		taskResultPart.reloadInbox();
	}

}