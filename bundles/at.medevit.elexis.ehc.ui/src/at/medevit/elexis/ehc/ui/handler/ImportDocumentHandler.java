package at.medevit.elexis.ehc.ui.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.ehc.ui.dialog.ImportSelectionWizard;
import at.medevit.elexis.ehc.ui.views.EHealthConnectorView;

public class ImportDocumentHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		EHealthConnectorView view = (EHealthConnectorView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().findView(EHealthConnectorView.ID);
		if (view != null) {
			ImportSelectionWizard importSelection = new ImportSelectionWizard();

			InputStream inView = view.getDisplayedReport();
			if (inView != null) {
				ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
				copyStream(inView, outBuffer);

				ByteArrayInputStream inDocument = new ByteArrayInputStream(outBuffer.toByteArray());
				importSelection.setDocument(inDocument);
			}
			WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), importSelection);
			dialog.open();
		}
		return null;
	}

	private static void copyStream(InputStream input, OutputStream output) {
		try {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			// ignore ...
		}
	}
}
