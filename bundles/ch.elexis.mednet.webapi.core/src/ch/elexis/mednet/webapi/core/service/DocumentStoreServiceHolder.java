package ch.elexis.mednet.webapi.core.service;

import ch.elexis.core.documents.DocumentStore;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = {})
public class DocumentStoreServiceHolder {
	private static DocumentStore localDocumentStore;

	@Reference
	public void bind(DocumentStore service) {
		DocumentStoreServiceHolder.localDocumentStore = service;
	}

	public void unbind(DocumentStore service) {
		DocumentStoreServiceHolder.localDocumentStore = null;
	}

	public static DocumentStore getService() {
		return localDocumentStore;
	}
}
