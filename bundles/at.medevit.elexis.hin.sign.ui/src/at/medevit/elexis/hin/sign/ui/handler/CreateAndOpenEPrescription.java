package at.medevit.elexis.hin.sign.ui.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.emediplan.core.EMediplanService;
import at.medevit.elexis.emediplan.core.EMediplanUtil;
import at.medevit.elexis.hin.sign.core.IHinSignService;
import at.medevit.elexis.hin.sign.ui.outputter.EPrescriptionOutputter;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IDocumentLetter;
import ch.elexis.core.model.IRecipe;
import ch.elexis.core.services.IDocumentStore;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.status.ObjectStatus;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.data.Brief;
import ch.elexis.data.OutputLog;

public class CreateAndOpenEPrescription extends AbstractHandler implements IHandler {

	private EMediplanService eMediplanService;

	private IHinSignService hinSignService;

	private IDocumentStore letterDocumentStore;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Optional<IRecipe> selectedRecipe = ContextServiceHolder.get().getTyped(IRecipe.class);
		if (selectedRecipe.isPresent() && getEMediplanService() != null && getHinSignService() != null) {
			Optional<String> existingUrl = getHinSignService().getPrescriptionUrl(selectedRecipe.get());
			if (existingUrl.isEmpty()) {
				try {
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					getEMediplanService().exportEMediplanJson(ContextServiceHolder.getActiveMandatorOrThrow(),
							selectedRecipe.get().getPatient(), selectedRecipe.get().getPrescriptions(), output);
					String chmedJson = IOUtils.toString(new ByteArrayInputStream(output.toByteArray()), "UTF-8");
					String chmed = EMediplanUtil.getEncodedJson(chmedJson);
					if (StringUtils.isNotBlank(chmed)) {
						ObjectStatus<?> status = getHinSignService().createPrescription(chmed);
						if (status.isOK() && status.get() instanceof String) {
							String url = (String) status.get();
							existingUrl = Optional.of(url);
							getHinSignService().setPrescriptionUrl(selectedRecipe.get(), url);
						}
					}
				} catch (IOException e) {
					LoggerFactory.getLogger(getClass()).error("Error creating eprescription", e);
					return null;
				}
			}
			ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();
			ObjectStatus<?> pdfResult = getHinSignService().exportPrescriptionPdf(selectedRecipe.get(), pdfOutput);
			if (pdfResult.isOK()) {
				try {
					savePrescriptionLetter(selectedRecipe.get(), "pdf",
							new ByteArrayInputStream(pdfOutput.toByteArray()));
					new OutputLog(NoPoUtil.loadAsPersistentObject(selectedRecipe.get()), new EPrescriptionOutputter());
					// open with system viewer
					Program.launch(writeTempPdf(pdfOutput));
				} catch (ElexisException | IOException e) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
							"Das Rezept konnte nicht angezeigt werden.");
				}
			}
		}
		return null;
	}

	private void savePrescriptionLetter(IRecipe recipe, String contentType, InputStream content)
			throws ElexisException {
		IDocument letter = getLetterDocumentStore().createDocument(recipe.getPatient().getId(),
				"HIN eRezept " + DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(recipe.getDate()), Brief.RP);
		letter.setExtension(contentType);
		getLetterDocumentStore().saveDocument(letter, content);
		recipe.setDocument((IDocumentLetter) letter);
		CoreModelServiceHolder.get().save(recipe);
	}

	public static String writeTempPdf(ByteArrayOutputStream pdf) throws FileNotFoundException, IOException {
		File pdfFile = File.createTempFile("ePrescription_" + System.currentTimeMillis(), ".pdf"); //$NON-NLS-1$ //$NON-NLS-2$
		try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
			fos.write(pdf.toByteArray());
			fos.flush();
		}
		return pdfFile.getAbsolutePath();
	}

	private EMediplanService getEMediplanService() {
		if (eMediplanService == null) {
			eMediplanService = OsgiServiceUtil.getService(EMediplanService.class).orElse(null);
		}
		return eMediplanService;
	}

	private IHinSignService getHinSignService() {
		if (hinSignService == null) {
			hinSignService = OsgiServiceUtil.getService(IHinSignService.class).orElse(null);
		}
		return hinSignService;
	}

	private IDocumentStore getLetterDocumentStore() {
		if (letterDocumentStore == null) {
			letterDocumentStore = OsgiServiceUtil
					.getService(IDocumentStore.class, "(storeid=ch.elexis.data.store.brief)").orElse(null);
		}
		return letterDocumentStore;
	}
}
