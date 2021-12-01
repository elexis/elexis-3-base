/*******************************************************************************
 * Copyright (c) 2019 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.fop.printing.print;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.render.print.PageableRenderer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.utils.CoreUtil;
import ch.itmed.fop.printing.xml.documents.FoTransformer;

public final class PrintProvider {
	private static Logger logger = LoggerFactory.getLogger(PrintProvider.class);

	private static DocPrintJob createDocPrintJob(String printerName) {
		PrintService[] services = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);

		for (PrintService printer : services) {
			if (printer.getName().equals(printerName)) {
				return printer.createPrintJob();
			}
		}
		return null;
	}

	/**
	 * Prints the documents.
	 * 
	 * @param foStream
	 * @param printerName
	 * @throws IOException
	 * @throws FOPException
	 * @throws TransformerException
	 * @throws PrintException
	 */
	public static void print(InputStream foStream, String printerName)
			throws IOException, FOPException, TransformerException, PrintException {
		// always try to use printing via formats before using javax renderer
		if (createDocFlavorPrintJob(printerName, DocFlavor.INPUT_STREAM.PDF) != null) {
			logger.info("Using fo pdf printing with printer [" + printerName + "]");
			printWithOutputFormatAndDocFlavor(foStream, printerName, MimeConstants.MIME_PDF,
				DocFlavor.INPUT_STREAM.PDF);
			return;
		} else if (createDocFlavorPrintJob(printerName,
			DocFlavor.INPUT_STREAM.POSTSCRIPT) != null) {
			logger.info("Using fo postscript printing with printer [" + printerName + "]");
			printWithOutputFormatAndDocFlavor(foStream, printerName, MimeConstants.MIME_POSTSCRIPT,
				DocFlavor.INPUT_STREAM.POSTSCRIPT);
			return;
		} else if (createDocFlavorPrintJob(printerName, DocFlavor.INPUT_STREAM.PNG) != null) {
			logger.info("Using fo png printing with printer [" + printerName + "]");
			printWithOutputFormatAndDocFlavor(foStream, printerName, MimeConstants.MIME_PNG,
				DocFlavor.INPUT_STREAM.PNG);
			return;
		}
		logger.info("Using default fo javax printing with printer [" + printerName + "]");
		
		// Make sure that the position of the marker is not at the end of stream
		foStream.reset();

		FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
		DocPrintJob printJob = createDocPrintJob(printerName);
		logPrinterDebugInfo(printJob, DocFlavor.SERVICE_FORMATTED.PAGEABLE);
		FOUserAgent userAgent = fopFactory.newFOUserAgent();
		PageableRenderer renderer = new PageableRenderer(userAgent);

		userAgent.setRendererOverride(renderer);
		// Construct FOP with desired output format
		Fop fop = fopFactory.newFop(userAgent);

		// Setup JAXP using identity transformer
		TransformerFactory factory = TransformerFactory.newInstance();
		// identity transformer
		Transformer transformer = factory.newTransformer();

		// Setup input stream
		Source src = new StreamSource(foStream);

		// Resulting SAX events (the generated FO) must be piped through to FOP
		Result res = new SAXResult(fop.getDefaultHandler());

		// Start XSLT transformation and FOP processing
		transformer.transform(src, res);
		
		Doc doc = new SimpleDoc(renderer, DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
		printJob.print(doc, null);
		logger.info("Print job sent to printer: " + printerName);
	}
	
	private static DocPrintJob createDocFlavorPrintJob(String printerName, DocFlavor docFlavor){
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		for (PrintService printer : services) {
			if (printer.getName().equals(printerName)) {
				if (printer.isDocFlavorSupported(docFlavor)) {
					DocPrintJob ret = printer.createPrintJob();
					return ret;
				}
			}
		}
		return null;
	}
	
	private static void logPrinterDebugInfo(DocPrintJob printJob, DocFlavor printingDocFlavor){
		if (System.getProperty(FoTransformer.DEBUG_MODE) != null) {
			logger.info("Printer [" + printJob.getPrintService().getName() + "]");
			DocFlavor[] supportedFlavors = printJob.getPrintService().getSupportedDocFlavors();
			Arrays.asList(supportedFlavors).forEach(df -> logger.info("docflavor [" + df + "]"));
			if (printingDocFlavor == DocFlavor.INPUT_STREAM.PNG) {
				Object supported = printJob.getPrintService()
					.getSupportedAttributeValues(MediaSize.class, printingDocFlavor, null);
				if (supported != null) {
					if (supported.getClass().isArray()) {
						Object[] array = (Object[]) supported;
						for (Object object : array) {
							logger.info("attr mediasize [" + object + "]");
						}
					} else {
						logger.info("attr mediasize [" + supported + "]");
					}
				}
				supported = printJob.getPrintService()
					.getSupportedAttributeValues(MediaPrintableArea.class, printingDocFlavor, null);
				if (supported != null) {
					if (supported.getClass().isArray()) {
						Object[] array = (Object[]) supported;
						for (Object object : array) {
							logger.info("attr mediaprintablearea [" + object + "]");
						}
					} else {
						logger.info("attr mediaprintablearea [" + supported + "]");
					}
				}
			}
		}
	}
	
	private static void printWithOutputFormatAndDocFlavor(InputStream foStream, String printerName,
		String outputFormat, DocFlavor printingDocFlavor)
		throws IOException, FOPException, TransformerException, PrintException{
		boolean usePng = printingDocFlavor == DocFlavor.INPUT_STREAM.PNG;
		if (usePng) {
			outputFormat = MimeConstants.MIME_PDF;
		}
		// Make sure that the position of the marker is not at the end of stream
		foStream.reset();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
		FOUserAgent userAgent = fopFactory.newFOUserAgent();
		// Construct FOP with desired output format
		Fop fop = fopFactory.newFop(outputFormat, userAgent, outputStream);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		
		// Setup input stream
		Source src = new StreamSource(foStream);
		
		// Resulting SAX events (the generated FO) must be piped through to FOP
		Result res = new SAXResult(fop.getDefaultHandler());
		
		// Start XSLT transformation and FOP processing
		transformer.transform(src, res);
		
		if (System.getProperty(FoTransformer.DEBUG_MODE) != null
			&& MimeConstants.MIME_PDF.equals(outputFormat)) {
			File userDir = CoreUtil.getWritableUserDir();
			File xmlOutput = new File(userDir, "medi-print_debug.pdf");
			try (FileOutputStream fo = new FileOutputStream(xmlOutput)) {
				fo.write(outputStream.toByteArray());
			} catch (IOException e) {
				LoggerFactory.getLogger(PrintProvider.class)
					.error("Could not write medi-print debug pdf", e);
			}
		}
		if (usePng) {
			List<ByteArrayOutputStream> outputStreams = convertPdfToPng(outputStream);
			for (ByteArrayOutputStream os : outputStreams) {
				print(os, printerName, printingDocFlavor);
			}
		} else {
			print(outputStream, printerName, printingDocFlavor);
		}
	}
	
	private static void print(ByteArrayOutputStream outputStream, String printerName,
		DocFlavor printingDocFlavor) throws PrintException{
		DocPrintJob printJob = createDocFlavorPrintJob(printerName, printingDocFlavor);
		if (printJob != null) {
			logPrinterDebugInfo(printJob, printingDocFlavor);
			if (printJob.getPrintService().isDocFlavorSupported(printingDocFlavor)) {
				Doc doc = new SimpleDoc(new ByteArrayInputStream(outputStream.toByteArray()),
					printingDocFlavor, null);
				printJob.print(doc, null);
			} else {
				logger.error(
					"Could not print on [" + printerName + "] with [" + printingDocFlavor + "]");
			}
		}
	}
	
	private static List<ByteArrayOutputStream> convertPdfToPng(ByteArrayOutputStream outputStream)
		throws IOException{
		List<ByteArrayOutputStream> ret = new ArrayList<>();
		PDDocument document = PDDocument.load(new ByteArrayInputStream(outputStream.toByteArray()));
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		for (int page = 0; page < document.getNumberOfPages(); ++page) {
			BufferedImage renderedImage = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
			
			//			Image resultingImage = renderedImage.getScaledInstance(400, 400, Image.SCALE_DEFAULT);
			//			BufferedImage outputImage = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
			//			outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIOUtil.writeImage(renderedImage, "PNG", out, 300);
			if (System.getProperty(FoTransformer.DEBUG_MODE) != null) {
				File userDir = CoreUtil.getWritableUserDir();
				File pngOut = new File(userDir, "medi-print_debug.png");
				try (FileOutputStream fo = new FileOutputStream(pngOut)) {
					ImageIOUtil.writeImage(renderedImage, "PNG", fo, 300);
				}
			}
			ret.add(out);
		}
		document.close();
		return ret;
	}
}
