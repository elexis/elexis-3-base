package ch.itmed.fop.printing.handlers;

import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import at.medevit.elexis.barcode.Activator;
import at.medevit.elexis.barcode.fop.DOMToPdf;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IPatient;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.Setting;
import ch.itmed.fop.printing.preferences.SettingsProvider;
import ch.itmed.fop.printing.resources.ResourceProvider;
import ch.itmed.fop.printing.xml.documents.BarCodeLabel;
import ch.itmed.fop.printing.xml.documents.FoTransformer;
import ch.itmed.fop.printing.xml.elements.BarCodeElement;

public class PatientBarCodeLabelHandler extends AbstractHandler {
	private static Logger logger = LoggerFactory.getLogger(PatientLabelHandler.class);
	private Document config = null;
	private IPatient pat = null;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		StructuredSelection selection = (StructuredSelection) HandlerUtil.getCurrentSelection(event);

		if (selection != null && selection.getFirstElement() instanceof IPatient) {
			pat = (IPatient) selection.getFirstElement();
		} else {
			pat = ContextServiceHolder.get().getActivePatient().orElse(null);
		}

		if (pat == null)
			return null;

		try {
			InputStream xmlDoc = BarCodeLabel.create();
			InputStream fo = FoTransformer.transformXmlToFo(xmlDoc,
					ResourceProvider.getXslTemplateFile(PreferenceConstants.BAR_CODE_LABEL_ID));

			String docName2 = PreferenceConstants.BAR_CODE_LABEL;
			IPreferenceStore settingsStore = SettingsProvider.getStore(docName2);

			String printerName = settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName2, 0));
			logger.info("Printing document PatientLabel on printer: " + printerName); //$NON-NLS-1$

			String docName = PreferenceConstants.BAR_CODE_LABEL;
			int barcodeFormat = Integer
					.parseInt(Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 14)));

			boolean isBarcodeFormat1 = barcodeFormat == 14;

			try {

				config = BarCodeElement.barCodegenerateConfiguration(pat, isBarcodeFormat1);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}

			File xslFile = ResourceProvider.getXslTemplateFile(PreferenceConstants.BAR_CODE_LABEL_ID);
			URL xslt = xslFile.toURI().toURL();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DOMToPdf dtp = DOMToPdf.getInstance();
			dtp.transform(config, xslt.openStream(), out);
			File temp = File.createTempFile("PE_", ".pdf");
			FileOutputStream fos = new FileOutputStream(temp);
			fos.write(out.toByteArray());
			out.close();
			fos.close();

			Program.launch(temp.getAbsolutePath());

			Job printJob = new Job("Print Barcode Label") {
				@Override
				protected IStatus run(org.eclipse.core.runtime.IProgressMonitor monitor) {
					try {
						// Druckauftrag erstellen
						PrinterJob job = PrinterJob.getPrinterJob();
						PrintService printer = getPrintServiceByName(printerName);
						if (printer != null) {
							job.setPrintService(printer);
							logger.info("Printing document PatientLabel on printer: " + printerName);
						} else {
							logger.warn("Printer not found: " + printerName);
						}
						job.setPageable(new PDFPageable(PDDocument.load(temp)));

						// Druckauftrag senden
						job.print();

						System.out.println("PDF erfolgreich gedruckt.");

						return Status.OK_STATUS;
					} catch (Exception e) {
						e.printStackTrace();
						return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
								"Fehler beim Drucken des Barcode-Etiketts", e);
					} finally {
						// Temporäre Datei löschen
						temp.delete();
					}
				}
			};

			// Job ausführen
			printJob.schedule();

		} catch (IOException e) {
			Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Transformationsfehler bei Erstellung Patientenetikette", e);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	private PrintService getPrintServiceByName(String printerName) {
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		for (PrintService service : services) {
			if (service.getName().equals(printerName)) {
				return service;
			}
		}
		return null;
	}
}
