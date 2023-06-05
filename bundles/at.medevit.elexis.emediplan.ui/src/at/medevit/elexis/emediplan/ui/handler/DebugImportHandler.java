package at.medevit.elexis.emediplan.ui.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.emediplan.StartupHandler;
import ch.elexis.barcode.scanner.BarcodeScannerMessage;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.dialogs.base.InputDialog;

public class DebugImportHandler extends AbstractHandler implements IHandler {

	private static boolean useJsonInput = true;
	private static boolean useBarcodeMessage = false;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		if (useJsonInput) {
			InputDialog inputDialog = new InputDialog(Display.getDefault().getActiveShell(), "eMediplan JSON",
					"Bitte geben Sie das eMediplan JSON ein das importiert werden soll", StringUtils.EMPTY, null,
					SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			if (inputDialog.open() == MessageDialog.OK) {
				String importJson = inputDialog.getValue();

				StartupHandler.openEMediplanImportDialog(getEncodedJson(importJson), null);
			}
		}

		if (useBarcodeMessage) {
			ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, new BarcodeScannerMessage("debug",
					"debug",
					"PUT CHMED16A... HERE"));
		}

		return null;
	}

	protected String getEncodedJson(@NonNull String json) {
		StringBuilder sb = new StringBuilder();
		// header for compresses json
		sb.append("CHMED16A1"); //$NON-NLS-1$

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
			gzip.write(json.getBytes());
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error encoding json", e); //$NON-NLS-1$
			throw new IllegalStateException("Error encoding json", e); //$NON-NLS-1$
		}
		sb.append(Base64.getEncoder().encodeToString(out.toByteArray()));
		return sb.toString();
	}

}
