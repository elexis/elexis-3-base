package at.medevit.elexis.ehc.ui.docbox.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import at.medevit.elexis.ehc.docbox.service.DocboxService;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.IOutputter;
import ch.elexis.data.OutputLog;
import ch.elexis.data.Rezept;
import ehealthconnector.cda.documents.ch.CdaCh;

public class SendPrescriptionHandler extends AbstractHandler implements IHandler, IOutputter {
	
	private static Image symbol;
	private Rezept prescription;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		prescription = (Rezept) ElexisEventDispatcher.getSelected(Rezept.class);
		if (prescription != null) {
			CdaCh cdaPrescription = DocboxService.getPrescriptionDocument(prescription);
			ByteArrayOutputStream pdfPrescription =
				DocboxService.getPrescriptionPdf(cdaPrescription);
			if (cdaPrescription != null && pdfPrescription != null) {
				// create InputStreams for sending ...
				ByteArrayOutputStream cdaOutput = new ByteArrayOutputStream();
				cdaPrescription.cPrintXmlToStream(cdaOutput);
				ByteArrayInputStream cdaInput = new ByteArrayInputStream(cdaOutput.toByteArray());

				ByteArrayInputStream pdfInput =
					new ByteArrayInputStream(pdfPrescription.toByteArray());
				String message = DocboxService.sendPrescription(cdaInput, pdfInput);
				if (!message.startsWith("FAILED")) {
					SavePrescriptionUtil.savePrescription(prescription, "pdf",
						pdfPrescription.toByteArray());
					new OutputLog(prescription, this);
					try {
						Program.launch(writeTempPdf(pdfPrescription));
					} catch (IOException e) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
							"Das Rezept konnte nicht angezeigt werden.");
					}
				} else {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
						"Das Rezept konnte nicht gesendet werden. Rückmeldung:\n\n" + message);
				}
				return null;
			}
		}
		MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
			"Das Rezept konnte nicht gesendet werden.");
		return null;
	}
	
	private String writeTempPdf(ByteArrayOutputStream pdf) throws FileNotFoundException,
		IOException{
		File pdfFile = File.createTempFile(getRezeptFileName(), ".pdf");
		try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
			fos.write(pdf.toByteArray());
			fos.flush();
		}
		return pdfFile.getAbsolutePath();
	}
	
	public String getRezeptFileName(){
		String ret = prescription.getLabel();
		return ret.replaceAll(" ", "_");
	}
	
	@Override
	public String getOutputterID(){
		return "at.medevit.elexis.ehc.ui.docbox.outputter";
	}
	
	@Override
	public String getOutputterDescription(){
		return "Rezeptübermittlung an Docbox";
	}
	
	@Override
	public Image getSymbol(){
		if (symbol == null) {
		ImageDescriptor iDesc =
			AbstractUIPlugin.imageDescriptorFromPlugin("at.medevit.elexis.ehc.ui.docbox",
			"/icons/docbox16.png");
			symbol = iDesc.createImage();
		}
		return symbol;
	}
}
