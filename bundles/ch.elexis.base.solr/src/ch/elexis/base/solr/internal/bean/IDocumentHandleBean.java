package ch.elexis.base.solr.internal.bean;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import ch.elexis.omnivore.model.IDocumentHandle;

public class IDocumentHandleBean extends ElexisSolrBean {
	
	@Field
	public final String patient_id;
	@Field
	public final String content;
	@Field
	public final Date date;
	@Field
	public final String author;
	
	private SolrCellMetadata metadata;
	
	public IDocumentHandleBean(IDocumentHandle documentHandle, String content, String metadata){
		super(documentHandle);
		this.patient_id = documentHandle.getPatient().getId();
		this.date = documentHandle.getCreated();
		this.content = content;
		this.metadata = new SolrCellMetadata(metadata);
		this.author = this.metadata.get("author");
	}
	
}
