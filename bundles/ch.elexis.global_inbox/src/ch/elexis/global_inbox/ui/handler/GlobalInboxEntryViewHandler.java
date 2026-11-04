
package ch.elexis.global_inbox.ui.handler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.program.Program;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.utils.FileUtil;
import ch.elexis.global_inbox.model.GlobalInboxEntry;
import ch.elexis.global_inbox.ui.Messages;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import jakarta.inject.Named;

public class GlobalInboxEntryViewHandler {

	@Execute
	public void execute(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) GlobalInboxEntry globalInboxEntry) {

		try {
			File mainFile = toLocalFile(globalInboxEntry.getMainFile());
			String ext = FileTool.getExtension(mainFile.getName());
			Program proggie = Program.findProgram(ext);
			String arg = mainFile.getAbsolutePath();
			if (proggie != null) {
				proggie.execute(arg);
			} else {
				if (Program.launch(arg) == false) {
					Runtime.getRuntime().exec(arg);
				}

			}

		} catch (Exception ex) {
			ExHandler.handle(ex);
			SWTHelper.showError(Messages.InboxView_couldNotStart, ex.getMessage());
		}

	}

	private File toLocalFile(IVirtualFilesystemHandle handle) throws IOException {
		File localFile = handle.toFile().orElse(null);
		if (localFile != null) {
			return localFile;
		}
		File tempFile = File.createTempFile("globalinbox_", //$NON-NLS-1$
				"_" + FileUtil.sanitizeFilename(handle.getName(), LoggerFactory.getLogger(getClass()))); //$NON-NLS-1$
		tempFile.deleteOnExit();
		try (InputStream in = handle.openInputStream()) {
			Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		return tempFile;
	}

	@CanExecute
	public boolean canExecute(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) GlobalInboxEntry globalInboxEntry) {
		return globalInboxEntry != null;
	}
}