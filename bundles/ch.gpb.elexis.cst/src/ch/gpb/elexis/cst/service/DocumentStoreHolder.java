package ch.gpb.elexis.cst.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IDocumentStore;

@Component(service = {})
public class DocumentStoreHolder {
	private static IDocumentStore omnivoreDocumentStore;
	
	@Reference(target = "(storeid=ch.elexis.data.store.omnivore)")
	public void bind(IDocumentStore service){
		DocumentStoreHolder.omnivoreDocumentStore = service;
	}
	
	public static IDocumentStore get(){
		return omnivoreDocumentStore;
	}
}
