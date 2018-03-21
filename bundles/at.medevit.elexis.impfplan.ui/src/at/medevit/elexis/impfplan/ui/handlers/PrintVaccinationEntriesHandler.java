/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.impfplan.ui.handlers;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import at.medevit.elexis.impfplan.ui.VaccinationComposite;
import at.medevit.elexis.impfplan.ui.VaccinationCompositePaintListener;
import at.medevit.elexis.impfplan.ui.VaccinationView;
import at.medevit.elexis.impfplan.ui.preferences.PreferencePage;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.events.MessageEvent;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;

public class PrintVaccinationEntriesHandler extends AbstractHandler {
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MMMM yyyy");
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		Patient patient = ElexisEventDispatcher.getSelectedPatient();
		
		VaccinationView vaccView =
			(VaccinationView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(VaccinationView.PART_ID);
		VaccinationComposite vaccinationComposite = vaccView.getVaccinationComposite();
		VaccinationCompositePaintListener vcpl =
			vaccinationComposite.getVaccinationCompositePaintListener();
		
		final int dim = 800;
		
		Rectangle a4Rectangle = new Rectangle(0, 0, dim, (int) (dim * 1.41));
		Display display = vaccinationComposite.getDisplay();
		final Image image = new Image(display, a4Rectangle);
		GC gc = new GC(image);
		vcpl.paintControl(gc, display, a4Rectangle.width, a4Rectangle.height, true);
		gc.dispose();
		
		try {
			createPDF(patient, image);
		} catch (COSVisitorException | IOException e) {
			MessageEvent.fireError("Fehler beim Erstellen des PDF", e.getMessage(), e);
		}
		vcpl.restorePrePrintSettting();
		return null;
	}
	
	private void createPDF(Patient patient, Image image) throws IOException, COSVisitorException{
		PDDocumentInformation pdi = new PDDocumentInformation();
		Mandant mandant = (Mandant) ElexisEventDispatcher.getSelected(Mandant.class);
		pdi.setAuthor(mandant.getName() + " " + mandant.getVorname());
		pdi.setCreationDate(new GregorianCalendar());
		pdi.setTitle("Impfausweis " + patient.getLabel());
		
		PDDocument document = new PDDocument();
		document.setDocumentInformation(pdi);
		
		PDPage page = new PDPage();
		page.setMediaBox(PDPage.PAGE_SIZE_A4);
		document.addPage(page);
		
		PDRectangle pageSize = page.findMediaBox();
		PDFont font = PDType1Font.HELVETICA_BOLD;
		
		PDFont subFont = PDType1Font.HELVETICA;
		
		PDPageContentStream contentStream = new PDPageContentStream(document, page);
		contentStream.beginText();
		contentStream.setFont(font, 14);
		contentStream.moveTextPositionByAmount(40, pageSize.getUpperRightY() - 40);
		contentStream.drawString(patient.getLabel());
		contentStream.endText();
		
		String dateLabel = sdf.format(Calendar.getInstance().getTime());
		String title = Person.load(mandant.getId()).get(Person.TITLE);
		String mandantLabel = title + " " + mandant.getName() + " " + mandant.getVorname();
		contentStream.beginText();
		contentStream.setFont(subFont, 10);
		contentStream.moveTextPositionByAmount(40, pageSize.getUpperRightY() - 55);
		contentStream.drawString("Ausstellung " + dateLabel + ", " + mandantLabel);
		contentStream.endText();
		
		BufferedImage imageAwt = convertToAWT(image.getImageData());
		
		PDXObjectImage pdPixelMap = new PDPixelMap(document, imageAwt);
		contentStream.drawXObject(pdPixelMap, 40, 30, pageSize.getWidth() - 80,
			pageSize.getHeight() - 100);
		contentStream.close();
		
		String outputPath =
			CoreHub.userCfg.get(PreferencePage.VAC_PDF_OUTPUTDIR, CoreHub.getWritableUserDir()
				.getAbsolutePath());
		if (outputPath.equals(CoreHub.getWritableUserDir().getAbsolutePath())) {
			SWTHelper
				.showInfo(
					"Kein Ausgabeverzeichnis definiert",
					"Ausgabe erfolgt in: "
						+ outputPath
						+ "\nDas Ausgabeverzeichnis kann unter Einstellungen\\Klinische Hilfsmittel\\Impfplan definiert werden.");
		}
		File outputDir = new File(outputPath);
		File pdf = new File(outputDir, "impfplan_" + patient.getPatCode() + ".pdf");
		document.save(pdf);
		document.close();
		Desktop.getDesktop().open(pdf);
	}
	
	static BufferedImage convertToAWT(ImageData data){
		ColorModel colorModel = null;
		PaletteData palette = data.palette;
		if (palette.isDirect) {
			colorModel =
				new DirectColorModel(data.depth, palette.redMask, palette.greenMask,
					palette.blueMask);
			BufferedImage bufferedImage =
				new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width,
					data.height), false, null);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					RGB rgb = palette.getRGB(pixel);
					bufferedImage.setRGB(x, y, rgb.red << 16 | rgb.green << 8 | rgb.blue);
				}
			}
			return bufferedImage;
		} else {
			RGB[] rgbs = palette.getRGBs();
			byte[] red = new byte[rgbs.length];
			byte[] green = new byte[rgbs.length];
			byte[] blue = new byte[rgbs.length];
			for (int i = 0; i < rgbs.length; i++) {
				RGB rgb = rgbs[i];
				red[i] = (byte) rgb.red;
				green[i] = (byte) rgb.green;
				blue[i] = (byte) rgb.blue;
			}
			if (data.transparentPixel != -1) {
				colorModel =
					new IndexColorModel(data.depth, rgbs.length, red, green, blue,
						data.transparentPixel);
			} else {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue);
			}
			BufferedImage bufferedImage =
				new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width,
					data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					pixelArray[0] = pixel;
					raster.setPixel(x, y, pixelArray);
				}
			}
			return bufferedImage;
		}
	}
}
