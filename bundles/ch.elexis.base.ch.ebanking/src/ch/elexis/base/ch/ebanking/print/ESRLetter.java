package ch.elexis.base.ch.ebanking.print;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IFormattedOutput;
import ch.elexis.core.services.IFormattedOutputFactory;
import ch.elexis.core.services.IFormattedOutputFactory.ObjectType;
import ch.elexis.core.services.IFormattedOutputFactory.OutputType;

public class ESRLetter {

	public static void print(ESRFileJournalLetter letter) {
		BundleContext bundleContext = FrameworkUtil.getBundle(ESRLetter.class).getBundleContext();
		ServiceReference<IFormattedOutputFactory> serviceRef = bundleContext
				.getServiceReference(IFormattedOutputFactory.class);
		if (serviceRef != null) {
			IFormattedOutputFactory service = bundleContext.getService(serviceRef);
			IFormattedOutput outputter = service.getFormattedOutputImplementation(ObjectType.JAXB, OutputType.PDF);
			ByteArrayOutputStream pdf = new ByteArrayOutputStream();

			outputter.transform(letter, ESRLetter.class.getResourceAsStream("/rsc/xslt/esrfilejournal.xslt"), //$NON-NLS-1$
					pdf, null);
			bundleContext.ungetService(serviceRef);
			saveAndOpen("EsrFileJournal", pdf);
		}
	}

	public static void print(ESRJournalLetter letter) {
		BundleContext bundleContext = FrameworkUtil.getBundle(ESRLetter.class).getBundleContext();
		ServiceReference<IFormattedOutputFactory> serviceRef = bundleContext
				.getServiceReference(IFormattedOutputFactory.class);
		if (serviceRef != null) {
			IFormattedOutputFactory service = bundleContext.getService(serviceRef);
			IFormattedOutput outputter = service.getFormattedOutputImplementation(ObjectType.JAXB, OutputType.PDF);
			ByteArrayOutputStream pdf = new ByteArrayOutputStream();

			outputter.transform(letter, ESRLetter.class.getResourceAsStream("/rsc/xslt/esrjournal.xslt"), //$NON-NLS-1$
					pdf, null);
			bundleContext.ungetService(serviceRef);
			saveAndOpen("EsrJournal", pdf);
		}
	}

	private static void saveAndOpen(String prefix, ByteArrayOutputStream pdf) {
		// save and open the file ...
		File file = null;
		FileOutputStream fout = null;
		try {
			file = File.createTempFile(prefix + "_", ".pdf"); //$NON-NLS-1$ //$NON-NLS-2$
			fout = new FileOutputStream(file);
			fout.write(pdf.toByteArray());
		} catch (IOException e) {
			Display.getDefault().syncExec(() -> {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
						"Fehler beim PDF anlegen.\n" + e.getMessage());
			});
			LoggerFactory.getLogger(ESRLetter.class).error("Error creating PDF", e); //$NON-NLS-1$
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		if (file != null) {
			Program.launch(file.getAbsolutePath());
		}
	}
}
