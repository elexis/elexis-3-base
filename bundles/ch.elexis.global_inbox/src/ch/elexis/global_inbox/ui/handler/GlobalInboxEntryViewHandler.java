
package ch.elexis.global_inbox.ui.handler;

import java.io.File;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.program.Program;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.global_inbox.model.GlobalInboxEntry;
import ch.elexis.global_inbox.ui.Messages;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import jakarta.inject.Named;

public class GlobalInboxEntryViewHandler {

	@Execute
	public void execute(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) GlobalInboxEntry globalInboxEntry) {

		try {
			File mainFile = globalInboxEntry.getMainFile();
			String ext = FileTool.getExtension(mainFile.getName());
			Program proggie = Program.findProgram(ext);
			String arg = mainFile.getAbsolutePath();
			if (proggie != null) {
				proggie.execute(arg);
			} else {
				if (Program.launch(mainFile.getAbsolutePath()) == false) {
					Runtime.getRuntime().exec(arg);
				}

			}

		} catch (Exception ex) {
			ExHandler.handle(ex);
			SWTHelper.showError(Messages.InboxView_couldNotStart, ex.getMessage());
		}

	}

	@CanExecute
	public boolean canExecute(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) GlobalInboxEntry globalInboxEntry) {
		return globalInboxEntry != null;
	}

}