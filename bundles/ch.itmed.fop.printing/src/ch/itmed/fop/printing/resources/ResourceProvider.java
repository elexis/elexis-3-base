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

package ch.itmed.fop.printing.resources;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.Setting;

public class ResourceProvider {
	private static final String PLUGIN_ID = "ch.itmed.fop.printing"; //$NON-NLS-1$
	private static final String xslResourcePath = "/res/xsl/"; //$NON-NLS-1$
	private static final String xmlResourcePath = "/res/xml/PaperSizes.xml"; //$NON-NLS-1$
	public static final String IMAGE_ELLIPSIS_V_PATH = "/res/icons/vertical.png"; //$NON-NLS-1$
	public static final String IMAGE_ELLIPSIS_H_PATH = "/res/icons/horizontal.png"; //$NON-NLS-1$

	private static Logger logger = LoggerFactory.getLogger(ResourceProvider.class);

	public static File getXslTemplateFile(int docId) {
		String docName = PreferenceConstants.getDocumentName(docId);

		Setting.getBoolean(docName, PreferenceConstants.getDocPreferenceConstant(docName, 2));

		if (Setting.getBoolean(docName, PreferenceConstants.getDocPreferenceConstant(docName, 2))) {
			String xslPath = Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 1));
			// replace possible invalid path due to moving the xsl and xml files #24636
			if (xslPath.contains("ch.itmed.fop.printing.ui")) { //$NON-NLS-1$
				xslPath = xslPath.replaceAll("ch.itmed.fop.printing.ui", "ch.itmed.fop.printing"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return new File(xslPath);
		}

		return loadBundleFile(xslResourcePath + docName + ".xsl"); //$NON-NLS-1$
	}

	public static File loadBundleFile(String path) {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		URL url = bundle.getEntry(path);
		try {
			url = FileLocator.toFileURL(url);
		} catch (IOException e) {
			logger.error("Could not locate bundle file at " + path, e); //$NON-NLS-1$
		}
		String bundleLocation = url.getPath();
		return new File(bundleLocation);
	}

	/**
	 * Returns the available paper formats
	 *
	 * @return
	 */
	public static String[] getPaperFormats() {
		List<String> paperSizes = new ArrayList<String>();
		try {
			Bundle bundle = Platform.getBundle(PLUGIN_ID);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(bundle.getEntry(xmlResourcePath).toString());
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("/paperSizes/paperSize"); //$NON-NLS-1$
			NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = (Node) nodeList.item(i);
				paperSizes.add(node.getAttributes().getNamedItem("id").getNodeValue()); //$NON-NLS-1$
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return paperSizes.toArray(new String[paperSizes.size()]);
	}

	/**
	 * Returns the values of the specified paper format
	 *
	 * @param paperFormatName
	 * @return
	 */
	public static List<String> getPaperFormatValues(String paperFormatName) {
		List<String> paperFormatValues = new ArrayList<String>();
		try {
			Bundle bundle = Platform.getBundle(PLUGIN_ID);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(bundle.getEntry("/res/xml/PaperSizes.xml").toString()); //$NON-NLS-1$
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("/paperSizes/paperSize"); //$NON-NLS-1$
			NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = (Node) nodeList.item(i);
				if (node.getAttributes().getNamedItem("id").getNodeValue().equals(paperFormatName)) { //$NON-NLS-1$
					NodeList childNodeList = node.getChildNodes();

					for (int k = 0; k < childNodeList.getLength(); k++) {
						Node childNode = (Node) childNodeList.item(k);

						// We only want child nodes that are of type ELEMENT_NODE
						if (childNode.getNodeType() == Node.ELEMENT_NODE) {
							paperFormatValues.add(childNode.getTextContent());
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return paperFormatValues;
	}
}
