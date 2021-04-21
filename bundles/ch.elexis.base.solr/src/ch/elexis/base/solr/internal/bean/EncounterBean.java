package ch.elexis.base.solr.internal.bean;

import java.time.ZoneId;
import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;

public class EncounterBean {
	
	@Field
	public String id;
	@Field
	public String patient_id;
	@Field
	public String mandator_id;
	@Field
	public String label;
	@Field("content")
	public String content;
	@Field("cr_date")
	public Date cr_date;
	
	public EncounterBean(){}
	
	public static EncounterBean of(IEncounter orig){
		EncounterBean bean = new EncounterBean();
		bean.setId(orig.getId());
		IPatient patient = orig.getPatient();
		if (patient != null) {
			bean.setPatient_id(patient.getId());
			bean.setLabel(orig.getDate() + " - " + patient.getLabel());
		} else {
			throw new IllegalArgumentException("patient is null");
		}
		IMandator mandator = orig.getMandator();
		if (mandator != null) {
			bean.setMandator_id(mandator.getId());
		} else {
			bean.setMandator_id("");
			// TODO on production
			//			throw new IllegalArgumentException("mandator is null");
		}
		bean.setContent(orig.getHeadVersionInPlaintext());
		Date cr_date =
			Date.from(orig.getDate().atZone(ZoneId.systemDefault()).toInstant());
		bean.setCr_date(cr_date);
		return bean;
	}
	
	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getPatient_id(){
		return patient_id;
	}
	
	public void setPatient_id(String patient_id){
		this.patient_id = patient_id;
	}
	
	public String getMandator_id(){
		return mandator_id;
	}
	
	public void setMandator_id(String mandator_id){
		this.mandator_id = mandator_id;
	}
	
	public String getContent(){
		return content;
	}
	
	public void setContent(String content){
		this.content = content;
	}
	
	public Date getCr_date(){
		return cr_date;
	}
	
	public void setCr_date(Date cr_date){
		this.cr_date = cr_date;
	}
	
	public String getLabel(){
		return label;
	}
	
	public void setLabel(String label){
		this.label = label;
	}
	
}
