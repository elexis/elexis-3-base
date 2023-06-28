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

package ch.itmed.fop.printing.xml.documents;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IFormattedOutput;
import ch.elexis.core.services.IFormattedOutputFactory;
import ch.elexis.core.services.IFormattedOutputFactory.ObjectType;
import ch.elexis.core.services.IFormattedOutputFactory.OutputType;
import ch.rgw.tools.ExHandler;

public class PdfTransformer {
	private static Logger logger = LoggerFactory.getLogger(PdfTransformer.class);

	public static String DEBUG_MODE = "fop.printing.debug"; //$NON-NLS-1$

	/**
	 * Creates an FO file and returns it as an InputStream.
	 *
	 * @param inputStream
	 * @return
	 */
	public static InputStream transformXmlToPdf(InputStream xmlInputStream, File xslFile) throws Exception {

		if (xslFile.exists() == false) {
			logger.error("XSL template " + xslFile.getAbsolutePath() + " not found"); //$NON-NLS-1$ //$NON-NLS-2$
			throw new IllegalStateException(
					"Druck fehlgeschlagen. Die Vorlage " + xslFile.toString() + " konnte nicht gefunden werden.");
		}

		if (xmlInputStream == null) {
			logger.error("Failed to create XML file"); //$NON-NLS-1$
			return null;
		}

		if (System.getProperty(DEBUG_MODE) != null) {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			IOUtils.copy(xmlInputStream, bo);

			// setup pretty printing xml
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); //$NON-NLS-1$ //$NON-NLS-2$
			StreamResult result = new StreamResult(new StringWriter());
			StreamSource source = new StreamSource(new ByteArrayInputStream(bo.toByteArray()));
			transformer.transform(source, result);
			System.out.println(result.getWriter().toString());

			xmlInputStream = new ByteArrayInputStream(bo.toByteArray());
		}

		BundleContext bundleContext = FrameworkUtil.getBundle(PdfTransformer.class).getBundleContext();
		ServiceReference<IFormattedOutputFactory> fopFactoryRef = bundleContext
				.getServiceReference(IFormattedOutputFactory.class);
		if (fopFactoryRef != null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try (InputStream xslInput = new FileInputStream(xslFile)) {
				IFormattedOutputFactory fopFactory = bundleContext.getService(fopFactoryRef);

				IFormattedOutput foOutputt = fopFactory.getFormattedOutputImplementation(ObjectType.XMLSTREAM,
						OutputType.PDF);
				foOutputt.transform(xmlInputStream, xslInput, out);
			} catch (IllegalStateException e) {
				ExHandler.handle(e);
			}
			bundleContext.ungetService(fopFactoryRef);

			return new ByteArrayInputStream(out.toByteArray());
		}

		return null;
	}
}
