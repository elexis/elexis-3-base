package ch.elexis.base.solr.internal.bean;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

public class IDocumentBean {
	
	@Field
	public String id;
	@Field
	public String label;
	@Field("patient_id")
	public String patientId;
	@Field("content")
	public String content;
	@Field("cr_date")
	public Date creationDate;
	@Field("lastupdate")
	public Long lastUpdate;
	
	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}

	public String getLabel(){
		return label;
	}
	
	public void setLabel(String label){
		this.label = label;
	}
	
	public String getPatientId(){
		return patientId;
	}
	
	public void setPatientId(String patientId){
		this.patientId = patientId;
	}
	
	public Long getLastUpdate(){
		return lastUpdate;
	}
	
	public void setLastUpdate(Long lastUpdate){
		this.lastUpdate = lastUpdate;
	}
	
	public Date getCreationDate(){
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate){
		this.creationDate = creationDate;
	}
	
	public String getContent(){
		return content;
	}
	
	public void setContent(String content){
		this.content = content;
	}

}
