package ch.elexis.laborimport.hl7.command;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Display;

import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.laborimport.hl7.automatic.AutomaticImportService;

public class HL7FileImportHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		String fileUrl = event.getParameter("ch.elexis.laborimport.hl7.allg.importFile.fileUrl").toString();
		if (fileUrl.isEmpty()) {
			throw new ExecutionException("File url parameter is empty");
		}

		IVirtualFilesystemService vfsService = OsgiServiceUtil.getService(IVirtualFilesystemService.class).orElse(null);
		if (vfsService == null) {
			throw new ExecutionException("VirtualFileSystemService is not available");
		}

		Display display = Display.getDefault();
		if (display == null) {
			throw new ExecutionException("Could not get default display");
		}

		try {
			IVirtualFilesystemHandle vfsFile = vfsService.of(fileUrl);
			ImportFileRunnable runnable = new ImportFileRunnable(vfsFile, AutomaticImportService.MY_LAB);
			display.syncExec(runnable);
			return runnable.getResult();

		} catch (IOException e) {
			throw new ExecutionException("Invalid fileUrl [" + fileUrl + "]", e);
		}
	}

}
