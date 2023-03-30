/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.ui.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

public class CDALoader {
	private static Logger log = LoggerFactory.getLogger(CDALoader.class);

	private static DocumentBuilderFactory factory = null;
	private static String defaultXsl = null;
	private static String cdaReportsBase = "cdareports"; //$NON-NLS-1$
	private static String tmpCDAFile = cdaReportsBase + File.separator + "tmp_cdafile.xml"; //$NON-NLS-1$
	private static String tmpXslFile = cdaReportsBase + File.separator + "tmp_stylesheet.xsl"; //$NON-NLS-1$

	public CDALoader() {
		File eHealthCDA = new File(cdaReportsBase);
		if (!eHealthCDA.exists() || !eHealthCDA.isDirectory()) {
			eHealthCDA.mkdir();
		}
		defaultXsl = "type=\"text/xsl\" href=\"" + initDefaultXsl() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		factory = DocumentBuilderFactory.newInstance();
	}

	public File buildXmlDocument(InputStream inStream) {
		return buildXmlDocument(inStream, StringUtils.EMPTY);
	}

	public File buildXmlDocument(InputStream inStream, String path) {
		File cdaFile = createTempCDAFile();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(inStream);

			// modify stylesheet if necessary
			XPath xpath = XPathFactory.newInstance().newXPath();
			String expression = "/processing-instruction('xml-stylesheet')"; //$NON-NLS-1$
			ProcessingInstruction pi = (ProcessingInstruction) xpath.evaluate(expression, doc, XPathConstants.NODE);

			if (pi == null) {
				pi = doc.createProcessingInstruction("xml-stylesheet", defaultXsl); //$NON-NLS-1$
				doc.insertBefore(pi, doc.getDocumentElement());
			} else if (!isValidStylesheet(pi.getNodeValue(), path)) {
				pi.setData(defaultXsl);
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(cdaFile.getAbsolutePath()));
			transformer.transform(source, result);

			log.debug("Done parsing XML"); //$NON-NLS-1$

		} catch (SAXException sax) {
			log.error("XML file causes troubles - either no proper XML or InputStream corrupt", sax); //$NON-NLS-1$
		} catch (ParserConfigurationException | XPathExpressionException | IOException e) {
			log.error("Error while trying to parse inpustream", e); //$NON-NLS-1$
		} catch (TransformerException te) {
			log.error("Could not transform content from InputStream into file [" + cdaFile.getName() + "]", te); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return cdaFile;
	}

	private File createTempCDAFile() {
		File cdaFile = new File(tmpCDAFile);
		try {
			if (cdaFile.exists()) {
				cdaFile.delete();
			}
			cdaFile.createNewFile();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return cdaFile;
	}

	private boolean isValidStylesheet(String value, String cdaFilePath) {
		if (value.startsWith("type=\"text/xsl")) { //$NON-NLS-1$
			String hrefAttribute = "href=\""; //$NON-NLS-1$
			int hrefStart = value.indexOf(hrefAttribute) + 6;
			int hrefEnd = value.indexOf("\"", hrefStart + 1); //$NON-NLS-1$
			String xslPath = value.substring(hrefStart, hrefEnd);

			if (!validHttpLink(xslPath)) {
				if (!validLocalXsl(xslPath, cdaFilePath)) {
					return false;
				}
				log.debug("Use XSL given in the stylesheet line entry"); //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	/**
	 * check if the XSL path is a http link
	 *
	 * @param link location given on the stylesheet line of the xml
	 * @return true if resovlable, false otherwise
	 */
	private boolean validHttpLink(String link) {
		URL url = null;
		try {
			url = new URL(link);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * check if the XSL can be found on the filesystem
	 *
	 * @param xslPath  path given in the stylesheet ref
	 * @param basePath of the actual xml file
	 * @return true if resolvable, false if not found
	 */
	private boolean validLocalXsl(String xslPath, String basePath) {
		File tmp = new File(xslPath);
		if (tmp.exists()) {
			return true;
		} else {
			int baseLocationEnd = basePath.lastIndexOf(File.separator);
			String path = basePath.substring(0, baseLocationEnd) + File.separator + xslPath;
			tmp = new File(path);

			// if it exist copy file to appropriate directory
			if (tmp.exists()) {
				try {
					File xslCopy = new File(cdaReportsBase + File.separator + xslPath);
					if (xslCopy.exists()) {
						xslCopy.delete();
					}
					Files.copy(tmp.toPath(), xslCopy.toPath());
				} catch (IOException e) {
					return false;
				}
				return true;
			}
			return false;
		}
	}

	private static String initDefaultXsl() {
		String path = StringUtils.EMPTY;
		try {
			File tmpXsl = new File(tmpXslFile);

			// copy of xsl only needed if not there yet
			if (!tmpXsl.exists()) {
				URL xslUrl = FileLocator.resolve(CDALoader.class.getResource("/rsc/vhitg-cda-v3.xsl")); //$NON-NLS-1$
				Files.copy(xslUrl.openStream(), tmpXsl.toPath());
			}
			path = tmpXsl.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}
}
