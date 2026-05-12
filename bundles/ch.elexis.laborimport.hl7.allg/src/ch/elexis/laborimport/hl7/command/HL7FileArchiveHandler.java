package ch.elexis.laborimport.hl7.command;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import ch.elexis.core.importer.div.importers.multifile.strategy.FileImportStrategyUtil;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IVirtualFilesystemService;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.CODE;
import ch.rgw.tools.Result.SEVERITY;

public class HL7FileArchiveHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String fileUrl = event.getParameter("ch.elexis.laborimport.hl7.allg.archiveFile.fileUrl").toString();
		if (fileUrl.isEmpty()) {
			throw new ExecutionException("File url parameter is empty");
		}

		IVirtualFilesystemService vfsService = OsgiServiceUtil.getService(IVirtualFilesystemService.class).orElse(null);
		if (vfsService == null) {
			throw new ExecutionException("VirtualFileSystemService is not available");
		}

		try {
			IVirtualFilesystemHandle vfsFile = vfsService.of(fileUrl);
			IVirtualFilesystemHandle fileHandleAfterMove = FileImportStrategyUtil.moveAfterImport(true, vfsFile);

			Result<String> result = Result.OK();
			result.addMessage(CODE.URL, SEVERITY.OK, "url", fileHandleAfterMove.getAbsolutePath());
			return result;

		} catch (IOException e) {
			throw new ExecutionException("Invalid fileUrl [" + fileUrl + "]", e);
		}
	}

}
