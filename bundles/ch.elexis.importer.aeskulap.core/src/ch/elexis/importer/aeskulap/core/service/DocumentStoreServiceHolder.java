package ch.elexis.importer.aeskulap.core.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IDocumentStore;

@Component
public class DocumentStoreServiceHolder {
	
	private static IDocumentStore documentStore;
	
	@Reference(target = "(storeid=ch.elexis.data.store.omnivore)")
	public void setDocumentStore(IDocumentStore documentStore){
		DocumentStoreServiceHolder.documentStore = documentStore;
	}
	
	public static boolean isSet() {
		return documentStore != null;
	}
	
	public static IDocumentStore get(){
		if(documentStore == null) {
			throw new IllegalStateException("No IDocumentStore implementation available");
		}
		return documentStore;
	}
}
