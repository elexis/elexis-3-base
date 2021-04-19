package ch.elexis.TarmedRechnung;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IInvoice;

public class XMLExporterDocuments {
	
	private static Logger logger = LoggerFactory.getLogger(XMLExporterDocuments.class);
	
	public static final String ELEMENT_DOCUMENTS = "documents"; //$NON-NLS-1$
	public static final String ELEMENT_DOCUMENT = "document"; //$NON-NLS-1$
	public static final String ELEMENT_BASE64 = "base64"; //$NON-NLS-1$
	
	private Element documentElement = null;
	
	public XMLExporterDocuments(Element element, List<Element> documents){
		this.documentElement = element;
		this.documentElement.setAttribute("number", Integer.toString(documents.size()));
		this.documentElement.addContent(documents);
	}
	
	public static @Nullable XMLExporterDocuments buildDocuments(IInvoice invoice,
		XMLExporter xmlExporter){
		
		List<IDocument> attachments = invoice.getAttachments();
		
		if (attachments != null && attachments.size() > 0) {
			List<Element> documents = new ArrayList<Element>();
			
			for (IDocument attachment : attachments) {
				String mimeType = attachment.getMimeType();
				if (mimeType == null || !mimeType.endsWith("pdf")) {
					logger.warn("Cannot add attachment [{}], mimeType is null or not pdf",
						attachment.getId());
					continue;
				}
				
				try {
					InputStream content = attachment.getContent();
					if (content != null) {
						byte[] byteArray = IOUtils.toByteArray(attachment.getContent());
						byte[] encoded = Base64.getEncoder().encode(byteArray);
						
						Element document = new Element(ELEMENT_DOCUMENT, XMLExporter.nsinvoice);
						document.setAttribute("filename", attachment.getTitle());
						document.setAttribute("mimeType", "application/pdf");
						
						Element base64 = new Element(ELEMENT_BASE64, XMLExporter.nsinvoice);
						base64.addContent(new String(encoded));
						
						document.addContent(base64);
						documents.add(document);
					} else {
						logger.warn("Cannot add attachment [{}], content is null",
							attachment.getId());
					}
					
				} catch (IOException e) {
					logger.warn("Cannot add attachment [{}], cannot read content",
						attachment.getId(), e);
				}
			}
			
			if (documents.size() > 0) {
				return new XMLExporterDocuments(
					new Element(ELEMENT_DOCUMENTS, XMLExporter.nsinvoice), documents);
			}
			
		}
		
		return null;
		
	}
	
	public Element getElement(){
		return documentElement;
	}
	
}
