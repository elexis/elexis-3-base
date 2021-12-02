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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.OrientationRequested;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.render.print.PageableRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import ch.elexis.core.utils.CoreUtil;
import ch.itmed.fop.printing.xml.documents.FoTransformer;

public final class PrintProvider {
	private static Logger logger = LoggerFactory.getLogger(PrintProvider.class);
	
	private static Map<String, CustomMediaSizeName> customMediaSizeMap = new HashMap<>();
	private static Integer customMediaSizeNumber = 1000;
	private static Integer customMediaSizeNumberRotated = 2000;
	
	private static DocPrintJob createDocPrintJob(String printerName){
		PrintService[] services =
			PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
		
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
		throws IOException, FOPException, TransformerException, PrintException{
		// always try to use printing via formats before using javax renderer
		//		if (createDocFlavorPrintJob(printerName, DocFlavor.INPUT_STREAM.PDF) != null) {
		//			logger.info("Using fo pdf printing with printer [" + printerName + "]");
		//			printWithOutputFormatAndDocFlavor(foStream, printerName, MimeConstants.MIME_PDF,
		//				DocFlavor.INPUT_STREAM.PDF, null);
		//			return;
		//		} else if (createDocFlavorPrintJob(printerName,
		//			DocFlavor.INPUT_STREAM.POSTSCRIPT) != null) {
		//			logger.info("Using fo postscript printing with printer [" + printerName + "]");
		//			printWithOutputFormatAndDocFlavor(foStream, printerName, MimeConstants.MIME_POSTSCRIPT,
		//				DocFlavor.INPUT_STREAM.POSTSCRIPT, null);
		//			return;
		//		}
		logger.info("Using default fo javax printing with printer [" + printerName + "]");
		CustomMediaSizeName mediaSize = getPageInformation(foStream);
		PrintRequestAttributeSet printRequestAttributes = getPrintRequestAttributes(mediaSize);
		
		// Make sure that the position of the marker is not at the end of stream
		foStream.reset();
		
		FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
		DocPrintJob printJob = createDocPrintJob(printerName);
		logPrinterDebugInfo(printJob);
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
		printJob.print(doc, printRequestAttributes);
		logger.info("Print job sent to printer: " + printerName);
	}
	
	private static CustomMediaSizeName getPageInformation(InputStream foStream) throws IOException{
		ByteArrayOutputStream copy = new ByteArrayOutputStream();
		IOUtils.copy(foStream, copy);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(copy.toByteArray()));
			XPathFactory xpathfactory = XPathFactory.newInstance();
			XPath xpath = xpathfactory.newXPath();
			XPathExpression expr = xpath.compile("//root/layout-master-set/simple-page-master");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0) {
				NamedNodeMap attributes = nodes.item(0).getAttributes();
				if (attributes.getNamedItem("page-width") != null
					&& attributes.getNamedItem("page-height") != null) {
					Integer width = Integer.parseInt(attributes.getNamedItem("page-width")
						.getNodeValue().replaceAll("[^0-9]", ""));
					Integer height = Integer.parseInt(attributes.getNamedItem("page-height")
						.getNodeValue().replaceAll("[^0-9]", ""));
					if (customMediaSizeMap.get(width + "x" + height) == null) {
						if (width > height) {
							CustomMediaSizeName customMediaSize =
								new CustomMediaSizeName(customMediaSizeNumberRotated++);
							customMediaSizeMap.put(width + "x" + height, customMediaSize);
							new MediaSize(height, width, Size2DSyntax.MM, customMediaSize);
						} else {
							CustomMediaSizeName customMediaSize =
								new CustomMediaSizeName(customMediaSizeNumber++);
							customMediaSizeMap.put(width + "x" + height, customMediaSize);
							new MediaSize(width, height, Size2DSyntax.MM, customMediaSize);
						}
					}
					return customMediaSizeMap.get(width + "x" + height);
				}
			}
		} catch (Exception e) {
			logger.error("Error parsing page infomation", e);
		}
		return null;
	}
	
	private static PrintRequestAttributeSet getPrintRequestAttributes(
		CustomMediaSizeName mediaSize){
		HashPrintRequestAttributeSet ret = new HashPrintRequestAttributeSet();
		ret.add(mediaSize);
		if (System.getProperty(FoTransformer.DEBUG_MODE) != null) {
			logger.info("mediasize attribute [" + mediaSize + "] ["
				+ MediaSize.getMediaSizeForName(mediaSize) + "]");
		}
		if (mediaSize.getValue() >= 2000) {
			ret.add(OrientationRequested.LANDSCAPE);
			if (System.getProperty(FoTransformer.DEBUG_MODE) != null) {
				logger.info("orientation attribute [" + mediaSize + "] ["
					+ OrientationRequested.LANDSCAPE + "]");
			}
		}
		return ret;
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
	
	private static void logPrinterDebugInfo(DocPrintJob printJob){
		if (System.getProperty(FoTransformer.DEBUG_MODE) != null) {
			logger.info("Printer [" + printJob.getPrintService().getName() + "]");
			DocFlavor[] supportedFlavors = printJob.getPrintService().getSupportedDocFlavors();
			Arrays.asList(supportedFlavors).forEach(df -> logger.info("docflavor [" + df + "]"));
		}
	}
	
	private static void printWithOutputFormatAndDocFlavor(InputStream foStream, String printerName,
		String outputFormat, DocFlavor printingDocFlavor,
		PrintRequestAttributeSet printRequestAttributes)
		throws IOException, FOPException, TransformerException, PrintException{
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
		print(outputStream, printerName, printingDocFlavor, printRequestAttributes);
	}
	
	private static void print(ByteArrayOutputStream outputStream, String printerName,
		DocFlavor printingDocFlavor, PrintRequestAttributeSet printRequestAttributes)
		throws PrintException{
		DocPrintJob printJob = createDocFlavorPrintJob(printerName, printingDocFlavor);
		if (printJob != null) {
			logPrinterDebugInfo(printJob);
			if (printJob.getPrintService().isDocFlavorSupported(printingDocFlavor)) {
				Doc doc = new SimpleDoc(new ByteArrayInputStream(outputStream.toByteArray()),
					printingDocFlavor, null);
				printJob.print(doc, printRequestAttributes);
			} else {
				logger.error(
					"Could not print on [" + printerName + "] with [" + printingDocFlavor + "]");
			}
		}
	}
}
