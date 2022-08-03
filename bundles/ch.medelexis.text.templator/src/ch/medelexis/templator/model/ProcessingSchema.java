/**
 * Copyright (c) 2010-2012, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 */

package ch.medelexis.templator.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.medelexis.templator.ui.Preferences;
import ch.rgw.tools.ExHandler;

public class ProcessingSchema extends Document {
	private static final Logger log = LoggerFactory.getLogger(ProcessingSchema.class);

	private static final long serialVersionUID = -1050660384548846589L;
	public static final Namespace ns = Namespace.getNamespace("TemplateProcessingInstruction", //$NON-NLS-1$
			"http://www.medelexis.ch/templator"); //$NON-NLS-1$
	public static final Namespace nsxsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XML Schema-instance"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Namespace nsschema = Namespace.getNamespace("schemaLocation", //$NON-NLS-1$
			"http://www.medelexis.ch/templator"); //$NON-NLS-1$

	private static IProcessor[] processors = null;

	IProcessor myProcessor = new OpenOfficeProcessor();

	Document doc;

	public ProcessingSchema() {
		doc = new Document();
		doc.setRootElement(new Element("ProcessInstruction", ns));

	}

	public ProcessingSchema(Document doc) {
		this.doc = doc;
	}

	public IProcessor getProcessor() {
		Element eProcessor = doc.getRootElement().getChild("processor", ns);
		if (eProcessor != null) {
			String name = eProcessor.getAttributeValue("name");
			for (IProcessor p : getProcessors()) {
				if (p.getName().equals(name)) {
					return p;
				}
			}
		}
		log.warn("Processor is null");
		return null;
	}

	public boolean getDirectOutput() {
		String direct = doc.getRootElement().getAttributeValue("directoutput");
		return "true".equalsIgnoreCase(direct);
	}

	public void setDirectOutput(boolean bDirect) {
		doc.getRootElement().setAttribute("directoutput", Boolean.toString(bDirect));
	}

	public void setProcessor(String processor) {
		Element eProc = doc.getRootElement().getChild("processor", ns);
		if (eProc == null) {
			eProc = new Element("processor", ns);
			doc.getRootElement().addContent(eProc);
		}
		eProc.setAttribute("name", processor);
	}

	public List<Element> getFields() {

		List<Element> fields = doc.getRootElement().getChildren("field", ns);
		return fields;
	}

	public Element getField(String name) {
		List<Element> fields = doc.getRootElement().getChildren("field", ns);
		for (Element field : fields) {
			if (field.getAttributeValue("name").equals(name)) {
				return field;
			}
		}
		return null;
	}

	public String getFieldTextEscaped(String name) {
		Element field = getField(name);
		if (field != null) {
			String text = field.getText();
			if (text != null) {
				text = text.replaceAll("&", "&amp;");
				text = text.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

			}

			return text;
		} else {
			return null;
		}
	}

	public void addField(String name) {
		Element field = new Element("field", ns);
		field.setAttribute("name", name);
		doc.getRootElement().addContent(field);
	}

	public File getTemplateFile() {
		File basedir = new File(CoreHub.localCfg.get(Preferences.PREF_TEMPLATEBASE, StringUtils.EMPTY));
		String name = doc.getRootElement().getAttributeValue("template");
		File ret = name == null ? null : new File(basedir, name);
		return ret;
	}

	public void setTemplate(String template) {
		doc.getRootElement().setAttribute("template", template);
	}

	public static ProcessingSchema load(InputStream is) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		return new ProcessingSchema(builder.build(is));

	}

	public String toXML() {
		XMLOutputter xout = new XMLOutputter(Format.getCompactFormat());
		String ret = xout.outputString(doc);
		return ret;
	}

	public static IProcessor[] getProcessors() {
		if (processors == null) {
			List<IProcessor> ret = new ArrayList<IProcessor>();
			IExtensionRegistry exr = Platform.getExtensionRegistry();
			IExtensionPoint exp = exr.getExtensionPoint("ch.medelexis.text.templator.Textprocessor");
			if (exp != null) {
				IExtension[] extensions = exp.getExtensions();
				for (IExtension ex : extensions) {
					IConfigurationElement[] elems = ex.getConfigurationElements();
					for (IConfigurationElement el : elems) {
						String n = el.getAttribute("name");
						try {
							IProcessor p = (IProcessor) el.createExecutableExtension("clazz");
							ret.add(p);
						} catch (CoreException e) {
							ExHandler.handle(e);
						}
					}
				}
				processors = ret.toArray(new IProcessor[0]);

			}
		}
		return processors;
	}
}
