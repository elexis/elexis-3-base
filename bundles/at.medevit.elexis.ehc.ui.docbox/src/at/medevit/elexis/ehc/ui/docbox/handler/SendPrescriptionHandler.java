package at.medevit.elexis.ehc.ui.docbox.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
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
import org.ehealth_connector.cda.ch.AbstractCdaChV1;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.docbox.service.DocboxService;
import ch.elexis.core.data.interfaces.IOutputter;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IRecipe;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.data.OutputLog;
import ch.elexis.data.Rezept;

public class SendPrescriptionHandler extends AbstractHandler implements IHandler, IOutputter {

	private static Image symbol;
	private Rezept prescription;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		prescription = getSelectedRezept();
		if (prescription != null) {
			AbstractCdaChV1<?> cdaPrescription = null;
			ByteArrayOutputStream pdfPrescription = null;
			ByteArrayOutputStream cdaOutput = new ByteArrayOutputStream();
			try {
				cdaPrescription = DocboxService.getPrescriptionDocument(prescription);

				try {
					CDAUtil.save(cdaPrescription.getDocRoot().getClinicalDocument(), cdaOutput);
				} catch (Exception ex) {
					LoggerFactory.getLogger(getClass()).error("Error creating InputStream for sending", ex); //$NON-NLS-1$
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
							"Das Rezept konnte nicht erstellt werden. " + ex.getMessage());
					return null;
				}

				pdfPrescription = DocboxService.getPrescriptionPdf(cdaOutput);
			} catch (IllegalStateException e) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
						"Das Rezept konnte nicht erstellt werden. " + e.getMessage());
				return null;
			}
			if (cdaPrescription != null && pdfPrescription != null) {
				// create InputStreams for sending ...
				ByteArrayInputStream cdaInput = new ByteArrayInputStream(cdaOutput.toByteArray());

				ByteArrayInputStream pdfInput = new ByteArrayInputStream(pdfPrescription.toByteArray());
				String message = DocboxService.sendPrescription(cdaInput, pdfInput);
				if (!message.startsWith("FAILED")) { //$NON-NLS-1$
					SavePrescriptionUtil.savePrescription(prescription, "pdf", pdfPrescription.toByteArray()); //$NON-NLS-1$
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

	private Rezept getSelectedRezept() {
		return ContextServiceHolder.get().getTyped(IRecipe.class)
				.map(ir -> ((Rezept) NoPoUtil.loadAsPersistentObject(ir))).orElse(null);
	}

	private String writeTempPdf(ByteArrayOutputStream pdf) throws FileNotFoundException, IOException {
		File pdfFile = File.createTempFile(getRezeptFileName(), ".pdf"); //$NON-NLS-1$
		try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
			fos.write(pdf.toByteArray());
			fos.flush();
		}
		return pdfFile.getAbsolutePath();
	}

	public String getRezeptFileName() {
		String ret = prescription.getLabel();
		return ret.replaceAll(StringUtils.SPACE, "_"); //$NON-NLS-1$
	}

	@Override
	public String getOutputterID() {
		return "at.medevit.elexis.ehc.ui.docbox.outputter"; //$NON-NLS-1$
	}

	@Override
	public String getOutputterDescription() {
		return "Rezeptübermittlung an Docbox";
	}

	@Override
	public Image getSymbol() {
		if (symbol == null) {
			ImageDescriptor iDesc = AbstractUIPlugin.imageDescriptorFromPlugin("at.medevit.elexis.ehc.ui.docbox", //$NON-NLS-1$
					"/icons/docbox16.png"); //$NON-NLS-1$
			symbol = iDesc.createImage();
		}
		return symbol;
	}
}
