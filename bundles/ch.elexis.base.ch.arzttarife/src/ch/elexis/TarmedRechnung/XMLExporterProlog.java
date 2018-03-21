package ch.elexis.TarmedRechnung;

import org.jdom.Element;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Rechnung;
import ch.rgw.tools.VersionInfo;

public class XMLExporterProlog {
	
	private static final String ELEMENT_PROLOG = "prolog"; //$NON-NLS-1$
	private static final String ELEMENT_PACKAGE = "package"; //$NON-NLS-1$
	private static final String ATTR_VERSION = "version"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$
	private static final String ELEMENT_GENERATOR = "generator"; //$NON-NLS-1$
	private static final String ELEMENT_DEPENDS_ON = "depends_on"; //$NON-NLS-1$

	private Element prologElement;
	
	private XMLExporterProlog(Element prolog){
		this.prologElement = prolog;
	}
	
	public Element getElement(){
		return prologElement;
	}

	public static XMLExporterProlog buildProlog(Rechnung rechnung, XMLExporter xmlExporter){
		
		Element element = new Element(ELEMENT_PROLOG, XMLExporter.nsinvoice);
		
		VersionInfo vi = new VersionInfo(CoreHub.Version);
		Element spackage = new Element(ELEMENT_PACKAGE, XMLExporter.nsinvoice);
		spackage.setAttribute(ATTR_VERSION, vi.getMaior() + vi.getMinor() + vi.getRevision());
		spackage.setAttribute(ATTR_NAME, "Elexis"); //$NON-NLS-1$
		element.addContent(spackage);
		
		Element generator = new Element(ELEMENT_GENERATOR, XMLExporter.nsinvoice);
		generator.setAttribute(ATTR_NAME, "JDOM");
		generator.setAttribute(ATTR_VERSION, "100");
		
		Element dependson = new Element(ELEMENT_DEPENDS_ON, XMLExporter.nsinvoice);
		dependson.setAttribute(ATTR_NAME, "Elexis TarmedVerifier");
		dependson.setAttribute(ATTR_VERSION, vi.getMaior() + vi.getMinor() + vi.getRevision());
		generator.addContent(dependson);
		
		element.addContent(generator);

		return new XMLExporterProlog(element);
	}
}
