package at.medevit.ch.artikelstamm.medcalendar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MedCalendarSection implements Serializable {
	private static final long serialVersionUID = 2387067267569365661L;
	
	private String code;
	private String name;
	private int level;
	private List<String> atcCodes;
	
	public MedCalendarSection(String code, String name, int level){
		this.code = code;
		this.name = name;
		this.level = level;
		this.atcCodes = new ArrayList<String>();
	}
	
	public String getCode(){
		return code;
	}
	
	public String getName(){
		return name;
	}
	
	public int getLevel(){
		return level;
	}
	
	public void addATCCode(String atcCode){
		atcCodes.add(atcCode);
	}
	
	public List<String> getATCCodes(){
		return atcCodes;
	}
}
