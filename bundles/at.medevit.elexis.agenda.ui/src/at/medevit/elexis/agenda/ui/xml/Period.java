package at.medevit.elexis.agenda.ui.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.interfaces.IPeriod;
import ch.rgw.tools.TimeTool;

@XmlRootElement(name = "period")
public class Period implements Comparable<Period> {
	
	@XmlTransient
	private TimeTool fromTool;
	@XmlTransient
	private TimeTool toTool;
	
	@XmlElement
	private String from;
	@XmlElement
	private String to;
	@XmlElement
	private String personalia;
	@XmlElement
	private String reason;

	public static Period of(IPeriod iPeriod){
		Period ret = new Period();
		
		if (iPeriod instanceof Termin) {
			Termin termin = (Termin) iPeriod;
			ret.fromTool = termin.getStartTime();
			ret.toTool = termin.getEndTime();
			ret.from = ret.fromTool.toString(TimeTool.TIME_SMALL);
			ret.to = ret.toTool.toString(TimeTool.TIME_SMALL);
			
			ret.personalia = termin.getPersonalia();
			ret.reason = termin.getGrund();
		}
		
		return ret;
	}
	
	public Period(){
		// needed for jaxb
	}
	
	public TimeTool getFromTool(){
		return fromTool;
	}
	
	@Override
	public int compareTo(Period other){
		return from.compareTo(other.from);
	}
}
