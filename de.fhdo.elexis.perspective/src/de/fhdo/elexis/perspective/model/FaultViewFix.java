package de.fhdo.elexis.perspective.model;

public class FaultViewFix {
	private String missingId;
	private String replacerId;
	private String label;
	
	public FaultViewFix(String missingId){
		this.missingId = missingId;
		this.replacerId = "";
		this.label = "";
	}
	
	public void setReplacerId(String replacerId){
		if (replacerId == null) {
			replacerId = "";
		}
		this.replacerId = replacerId;
	}
	
	public void setLabel(String label){
		this.label = label;
	}
	
	public String getMissingId(){
		return missingId;
	}
	
	public String getReplacerId(){
		return replacerId;
	}
	
	public String getLabel(){
		return label;
	}
}
