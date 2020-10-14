package ch.elexis.base.solr.internal.bean;

import org.apache.solr.client.solrj.beans.Field;

import ch.elexis.core.model.IDocumentLetter;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.holder.StoreToStringServiceHolder;
import ch.elexis.omnivore.model.IDocumentHandle;

public class ElexisSolrBean {
	
	@Field
	public final String id;
	@Field
	public final String sts;
	@Field
	public final String doc_type;
	
	public ElexisSolrBean(Identifiable identifiable){
		this.id = identifiable.getId();
		this.sts = StoreToStringServiceHolder.getStoreToString(identifiable);
		if (identifiable instanceof IEncounter) {
			doc_type = "encounter";
		} else if (identifiable instanceof IDocumentHandle) {
			doc_type = "document";
		} else if (identifiable instanceof IDocumentLetter) {
			doc_type = "letter";
		} else {
			doc_type = "unknown";
		}
	}
	
}
