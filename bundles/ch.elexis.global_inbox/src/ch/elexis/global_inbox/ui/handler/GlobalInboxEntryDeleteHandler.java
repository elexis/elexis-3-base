
package ch.elexis.global_inbox.ui.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.global_inbox.model.GlobalInboxEntry;
import ch.elexis.global_inbox.ui.Messages;

import java.io.File;
import java.text.MessageFormat;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;

public class GlobalInboxEntryDeleteHandler {
	
	@Execute
	public void execute(
		@Named(IServiceConstants.ACTIVE_SELECTION) GlobalInboxEntry globalInboxEntry){
		
		File mainFile = globalInboxEntry.getMainFile();
		if (SWTHelper.askYesNo(Messages.InboxView_inbox, MessageFormat
			.format(Messages.InboxView_thisreallydelete, globalInboxEntry.getTitle()))) {
			File[] extensionFiles = globalInboxEntry.getExtensionFiles();
			for (File extensionFile : extensionFiles) {
				extensionFile.delete();
			}
			mainFile.delete();
		}
		
	}
	
	@CanExecute
	public boolean canExecute(
		@Named(IServiceConstants.ACTIVE_SELECTION) GlobalInboxEntry globalInboxEntry){
		return globalInboxEntry != null;
	}
	
}