package ch.elexis.base.solr.internal.bean;

import java.time.ZoneId;
import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import ch.elexis.core.model.IEncounter;

public class IEncounterSolrBean extends ElexisSolrBean {
	
	@Field
	public final String patient_id;
	@Field
	public final String content;
	@Field
	public final Date date;
	
	public IEncounterSolrBean(IEncounter orig){
		super(orig);
		this.patient_id = orig.getPatient().getId();
		this.content = orig.getVersionedEntry().getHead();
		this.date = Date.from(orig.getTimeStamp().atZone(ZoneId.systemDefault()).toInstant());
	}
	
}
