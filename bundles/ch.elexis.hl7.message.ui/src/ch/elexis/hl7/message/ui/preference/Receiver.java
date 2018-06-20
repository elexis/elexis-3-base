package ch.elexis.hl7.message.ui.preference;

import java.util.StringJoiner;

public class Receiver {
	
	private String application;
	private String facility;
	
	public static Receiver of(String string){
		Receiver ret = new Receiver();
		
		String[] parts = string.split("\\|");
		if (parts != null && parts.length == 2) {
			ret.application = parts[0];
			ret.facility = parts[1];
		}
		
		return ret;
	}
	
	public String getApplication(){
		return application == null ? "" : application;
	}
	
	public String getFacility(){
		return facility == null ? "" : facility;
	}
	
	public void setApplication(String text){
		if (text == null) {
			text = "?";
		}
		application = text;
	}
	
	public void setFacility(String text){
		if (text == null) {
			text = "?";
		}
		facility = text;
	}
	
	public String toString(){
		StringJoiner sj = new StringJoiner("|");
		sj.add(application).add(facility);
		return sj.toString();
	}
	
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + ((application == null) ? 0 : application.hashCode());
		result = prime * result + ((facility == null) ? 0 : facility.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Receiver other = (Receiver) obj;
		if (application == null) {
			if (other.application != null)
				return false;
		} else if (!application.equals(other.application))
			return false;
		if (facility == null) {
			if (other.facility != null)
				return false;
		} else if (!facility.equals(other.facility))
			return false;
		return true;
	}
}
