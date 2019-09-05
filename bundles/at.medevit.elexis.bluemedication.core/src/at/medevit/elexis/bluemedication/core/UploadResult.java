package at.medevit.elexis.bluemedication.core;

import java.time.LocalDateTime;

public class UploadResult {
	
	private String url;
	
	private String id;
	
	private LocalDateTime timestamp;
	
	public UploadResult(String url, String id){
		this.url = url;
		this.id = id;
		this.timestamp = LocalDateTime.now();
	}
	
	public String getUrl(){
		return url;
	}
	
	public String getId(){
		return id;
	}
}
