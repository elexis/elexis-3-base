package at.medevit.elexis.bluemedication.ui.document;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.documents.DocumentStore;

@Component(service = {})
public class DocumentStoreServiceHolder {
	private static DocumentStore localDocumentStore;

	@Reference
	public void bind(DocumentStore service) {
		DocumentStoreServiceHolder.localDocumentStore = service;
	}

	public static void unbind(DocumentStore service) {
		DocumentStoreServiceHolder.localDocumentStore = null;
	}

	public static DocumentStore getService() {
		return localDocumentStore;
	}
}
