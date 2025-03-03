/*******************************************************************************
 * Copyright (c) 2017 MEDEVIT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package ch.elexis.fop.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FopConfParser;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.tools.fontlist.FontListGenerator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import ch.elexis.core.services.IFormattedOutput;
import ch.elexis.core.services.IFormattedOutputFactory;
import ch.elexis.fop.service.config.ConfigFile;
import ch.elexis.fop.service.dom.DomToPdf;
import ch.elexis.fop.service.dom.DomToPng;
import ch.elexis.fop.service.dom.DomToPs;
import ch.elexis.fop.service.jaxb.JaxbToPcl;
import ch.elexis.fop.service.jaxb.JaxbToPdf;
import ch.elexis.fop.service.jaxb.JaxbToPng;
import ch.elexis.fop.service.jaxb.JaxbToPs;
import ch.elexis.fop.service.xmlstream.XmlStreamToPcl;
import ch.elexis.fop.service.xmlstream.XmlStreamToPdf;
import ch.elexis.fop.service.xmlstream.XmlStreamToPng;
import ch.elexis.fop.service.xmlstream.XmlStreamToPs;

@Component
public class FormattedOutputFactory implements IFormattedOutputFactory {

	private static FopFactory fopFactory;

	@Activate
	public void activate() {
		initialize();
	}

	@Override
	public IFormattedOutput getFormattedOutputImplementation(ObjectType objectType, OutputType outputType) {
		if (objectType == ObjectType.JAXB) {
			switch (outputType) {
			case PCL:
				return JaxbToPcl.getInstance();
			case PDF:
				return JaxbToPdf.getInstance();
			case PS:
				return JaxbToPs.getInstance();
			case PNG:
				return JaxbToPng.getInstance();
			default:
				break;
			}
		} else if (objectType == ObjectType.DOM) {
			switch (outputType) {
			case PDF:
				return DomToPdf.getInstance();
			case PS:
				return DomToPs.getInstance();
			case PNG:
				return DomToPng.getInstance();
			case PCL:
				break;
			default:
				break;
			}
		} else if (objectType == ObjectType.XMLSTREAM) {
			switch (outputType) {
			case PDF:
				return XmlStreamToPdf.getInstance();
			case PS:
				return XmlStreamToPs.getInstance();
			case PNG:
				return XmlStreamToPng.getInstance();
			case PCL:
				return XmlStreamToPcl.getInstance();
			default:
				break;
			}
		}

		throw new IllegalStateException(
				"No IFormattedOutput implementation for [" + objectType + "->" + outputType + "]");
	}

	/**
	 *
	 * @param mimeType
	 * @return The fonts available for FOP processing.
	 */
	public static String[] getRegisteredFonts(String mimeType) {
		try {
			LinkedList<String> fontFamiliesList = new LinkedList<String>();

			FontListGenerator listGenerator = new FontListGenerator();
			SortedMap fontFamilies = listGenerator.listFonts(fopFactory, mimeType, null);
			Iterator iter = fontFamilies.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				fontFamiliesList.add((String) entry.getKey());
			}
			return fontFamiliesList.toArray(new String[0]);
		} catch (FOPException e) {
			LoggerFactory.getLogger(FormattedOutputFactory.class).error("Error getting fonts", e);
		}
		return new String[] { StringUtils.EMPTY };
	}

	public static void initialize() {
		try {
			FopConfParser parser = new FopConfParser(new ConfigFile().getAsInputStream(),
					new URI("http://dummy.domain"));
			FopFactoryBuilder builder = parser.getFopFactoryBuilder();
			builder.setStrictFOValidation(false);
			fopFactory = builder.build();
		} catch (SAXException | IOException | URISyntaxException e) {
			LoggerFactory.getLogger(FormattedOutputFactory.class).error("Error initializing", e);
		}
	}

	public static FopFactory getFopFactory() {
		return fopFactory;
	}
}
