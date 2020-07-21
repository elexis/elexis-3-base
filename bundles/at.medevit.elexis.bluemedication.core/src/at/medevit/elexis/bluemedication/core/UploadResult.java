package at.medevit.elexis.bluemedication.core;

import java.time.LocalDateTime;

public class UploadResult {
	
	private String url;
	
	private String id;
	
	private boolean uploadedMediplan;
	
	private String typ;
	
	private LocalDateTime timestamp;
	
	public UploadResult(String url, String id, String typ, boolean uploadedMediplan){
		this.url = url;
		this.id = id;
		this.uploadedMediplan = uploadedMediplan;
		this.typ = typ;
		this.timestamp = LocalDateTime.now();
	}
	
	public String getUrl(){
		return url;
	}
	
	public String getId(){
		return id;
	}
	
	public String getTyp(){
		return typ;
	}
	
	public boolean isUploadedMediplan(){
		return uploadedMediplan;
	}
}
