/*******************************************************************************
 * Copyright (c) 2015 MEDEVIT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package ch.elexis.fop.service.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.fop.apps.FopConfParser;
import org.apache.fop.apps.FopFactoryBuilder;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.elexis.core.data.util.PlatformHelper;

public class ConfigFile {
	
	Element root;
	Document doc;
	
	public Element getRootElement(){
		return root;
	}
	
	/**
	 * Create a ConfigFile instance containing FOP specific configuration. It should be used as
	 * input to the {@link FopConfParser} when creating a new {@link FopFactoryBuilder}.
	 * 
	 */
	public ConfigFile(){
		try {
			// other features should contribute fonts to this directory via p2 touchpoint 
			final File fonts =
				new File(PlatformHelper.getBasePath("ch.elexis.fop.service"), "rsc/fonts");
			
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.newDocument();
			
			root = doc.createElement("fop");
			root.setAttribute("version", "1.0");
			doc.appendChild(root);
			
			Element renderers = doc.createElement("renderers");
			root.appendChild(renderers);
			
			Element rendererPDF = doc.createElement("renderer");
			rendererPDF.setAttribute("mime", "application/pdf");
			
			Element rendererPS = doc.createElement("renderer");
			rendererPS.setAttribute("mime", "application/postscript");
			rendererPS.appendChild(getPsVersionElement());
			
			rendererPDF.appendChild(getFontsDirectoryElement(fonts));
			//			rendererPS.appendChild(getType1Fonts(fonts));
			
			renderers.appendChild(rendererPDF);
			renderers.appendChild(rendererPS);
		} catch (ParserConfigurationException e) {
			LoggerFactory.getLogger(ConfigFile.class).error("Error during FOP configuration.", e);
			throw new IllegalStateException(e);
		}
	}
	
	private Node getPsVersionElement(){
		Element ret = doc.createElement("language-level");
		ret.setTextContent("2");
		return ret;
	}
	
	private Element getFontsDirectoryElement(File fonts){
		Element ret = doc.createElement("fonts");
		
		Element autodetect = doc.createElement("auto-detect");
		Element directory = doc.createElement("directory");
		directory.setAttribute("recursive", "true");
		directory.setTextContent(fonts.getAbsolutePath());
		
		ret.appendChild(directory.cloneNode(true));
		ret.appendChild(autodetect.cloneNode(true));
		
		return ret;
	}
	
	private Element getType1Fonts(File fonts){
		Element ret = doc.createElement("fonts");
		
		Element font = doc.createElement("font");
		font.setAttribute("kerning", "yes");
		font.setAttribute("embed-url",
			"file:/" + fonts.getAbsolutePath() + File.separator + "Ocr" + File.separator
				+ "OCRA.pfb");
		Element fontTriple = doc.createElement("font-triplet");
		fontTriple.setAttribute("name", "OCRA");
		fontTriple.setAttribute("style", "normal");
		fontTriple.setAttribute("weight", "normal");
		font.appendChild(fontTriple.cloneNode(true));
		ret.appendChild(font.cloneNode(true));
		
		font = doc.createElement("font");
		font.setAttribute("kerning", "yes");
		font.setAttribute("embed-url",
			"file:/" + fonts.getAbsolutePath() + File.separator + "Ocr" + File.separator
				+ "OCRB.pfb");
		fontTriple.setAttribute("name", "OCRB");
		fontTriple.setAttribute("style", "normal");
		fontTriple.setAttribute("weight", "normal");
		font.appendChild(fontTriple.cloneNode(true));
		ret.appendChild(font.cloneNode(true));
		
		return ret;
	}
	
	private String getAsString(){
		try {
			// set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			
			// create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			return sw.toString();
		} catch (TransformerException e) {
			LoggerFactory.getLogger(ConfigFile.class).error("Error during FOP configuration.", e);
			throw new IllegalStateException(e);
		}
	}
	
	public String outputElement(){	
		return getAsString();
	}
	
	public InputStream getAsInputStream(){
		return new ByteArrayInputStream(getAsString().getBytes());
	}
}
