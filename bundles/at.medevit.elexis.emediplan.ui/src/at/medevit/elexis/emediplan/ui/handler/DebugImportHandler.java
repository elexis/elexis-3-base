package at.medevit.elexis.emediplan.ui.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.emediplan.Startup;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.ui.dialogs.base.InputDialog;

public class DebugImportHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		
		InputDialog inputDialog = new InputDialog(Display.getDefault().getActiveShell(),
			"eMediplan JSON", "Bitte geben Sie das eMediplan JSON ein das importiert werden soll",
			"", null,
			SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		if (inputDialog.open() == MessageDialog.OK) {
			String importJson = inputDialog.getValue();
			
			Startup.openEMediplanImportDialog(getEncodedJson(importJson), null);
		}
		return null;
	}
	
	protected String getEncodedJson(@NonNull String json){
		StringBuilder sb = new StringBuilder();
		// header for compresses json
		sb.append("CHMED16A1");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
			gzip.write(json.getBytes());
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error encoding json", e);
			throw new IllegalStateException("Error encoding json", e);
		}
		sb.append(Base64.getEncoder().encodeToString(out.toByteArray()));
		return sb.toString();
	}
	
}
