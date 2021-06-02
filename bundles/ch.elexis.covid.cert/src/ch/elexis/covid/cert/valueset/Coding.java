package ch.elexis.covid.cert.valueset;

import java.util.Map;
import java.util.Map.Entry;

import ch.elexis.core.findings.ICoding;

public class Coding implements ICoding {
	
	private String code;
	private String display;
	private String system;
	
	public Coding(Map<String, String> map){
		if (map.containsKey("short")) {
			this.code = map.get("short");
		}
		if (map.containsKey("display")) {
			this.display = map.get("display");
		}
		if (map.containsKey("system")) {
			this.system = map.get("system");
		}
	}
	
	public Coding(Entry<String, Map<String, String>> entry){
		this.code = entry.getKey();
		if (entry.getValue().containsKey("display")) {
			this.display = entry.getValue().get("display");
		}
		if (entry.getValue().containsKey("system")) {
			this.system = entry.getValue().get("system");
		}
		
	}
	
	@Override
	public String getSystem(){
		return system;
	}
	
	@Override
	public String getCode(){
		return code;
	}
	
	@Override
	public String getDisplay(){
		return display;
	}
	
}
