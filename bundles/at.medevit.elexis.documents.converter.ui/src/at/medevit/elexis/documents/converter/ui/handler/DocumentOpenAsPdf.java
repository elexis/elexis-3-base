package at.medevit.elexis.documents.converter.ui.handler;

import java.io.File;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.handlers.HandlerUtil;

import ch.elexis.core.model.IDocument;
import ch.elexis.core.services.IDocumentConverter;
import ch.elexis.core.ui.e4.util.CoreUiUtil;

public class DocumentOpenAsPdf extends AbstractHandler implements IHandler {

	@Inject
	private IDocumentConverter converter;

	public DocumentOpenAsPdf() {
		CoreUiUtil.injectServices(this);
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof StructuredSelection && !((StructuredSelection) selection).isEmpty()) {
			IDocument iDocument = (IDocument) ((StructuredSelection) selection).getFirstElement();
			Optional<File> pdfFile = converter.convertToPdf(iDocument);
			if (pdfFile.isPresent()) {
				Program.launch(pdfFile.get().getAbsolutePath());
			} else {
				MessageDialog.openError(HandlerUtil.getActiveShell(event), "Fehler",
						"Das Dokument konnte nicht konvertiert werden.");
			}
		}
		return null;
	}
}
