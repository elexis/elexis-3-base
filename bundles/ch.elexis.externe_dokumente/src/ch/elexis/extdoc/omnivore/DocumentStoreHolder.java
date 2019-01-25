package ch.elexis.extdoc.omnivore;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IDocumentStore;

@Component
public class DocumentStoreHolder {
	
	private static IDocumentStore documentStore;
	
	@Reference(target = "(storeid=ch.elexis.data.store.omnivore)")
	public void setDocumentStore(IDocumentStore documentStore){
		DocumentStoreHolder.documentStore = documentStore;
	}
	
	public static Optional<IDocumentStore> get(){
		return Optional.of(documentStore);
	}
}
