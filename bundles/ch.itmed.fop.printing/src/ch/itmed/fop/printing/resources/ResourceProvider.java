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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.SettingsProvider;
import ch.rgw.io.Settings;

public class ResourceProvider {
	private static final String PLUGIN_ID = "ch.itmed.fop.printing";
	private static final String xslResourcePath = "/res/xsl/";
	private static final String xmlResourcePath = "/res/xml/PaperSizes.xml";
	public static final String IMAGE_ELLIPSIS_V_PATH = "/res/icons/vertical.png";
	public static final String IMAGE_ELLIPSIS_H_PATH = "/res/icons/horizontal.png";

	private static Logger logger = LoggerFactory.getLogger(ResourceProvider.class);

	public static File getXslTemplateFile(int docId) {
		String docName = PreferenceConstants.getDocumentName(docId);
		Settings settingsStore = SettingsProvider.getStore(docName);

		if (settingsStore.get(PreferenceConstants.getDocPreferenceConstant(docName, 2), false)) {
			String xslPath = settingsStore.get(PreferenceConstants.getDocPreferenceConstant(docName, 1), "");
			return new File(xslPath);
		}

		return loadBundleFile(xslResourcePath + docName + ".xsl");
	}

	public static File loadBundleFile(String path) {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		URL url = bundle.getEntry(path);
		try {
			url = FileLocator.toFileURL(url);
		} catch (IOException e) {
			logger.error("Could not locate bundle file at " + path, e);
		}
		String bundleLocation = url.getPath();
		return new File(bundleLocation);
	}

	public static Image loadImage(String path) {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		URL url = FileLocator.find(bundle, new Path(path), null);
		ImageDescriptor imageDesc = ImageDescriptor.createFromURL(url);
		return imageDesc.createImage();
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
			XPathExpression expr = xpath.compile("/paperSizes/paperSize");
			NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = (Node) nodeList.item(i);
				paperSizes.add(node.getAttributes().getNamedItem("id").getNodeValue());
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
			Document doc = builder.parse(bundle.getEntry("/res/xml/PaperSizes.xml").toString());
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("/paperSizes/paperSize");
			NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = (Node) nodeList.item(i);
				if (node.getAttributes().getNamedItem("id").getNodeValue().equals(paperFormatName)) {
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
