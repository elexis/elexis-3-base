package at.medevit.elexis.agenda.ui.xml;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IPeriod;

@XmlRootElement(name = "period")
public class Period implements Comparable<Period> {
	
	@XmlTransient
	private LocalDateTime fromDateTime;
	@XmlTransient
	private LocalDateTime toDateTime;
	
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
		
		if (iPeriod instanceof IAppointment) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
			IAppointment termin = (IAppointment) iPeriod;
			ret.fromDateTime = termin.getStartTime();
			ret.toDateTime = termin.getEndTime();
			ret.from = formatter.format(ret.fromDateTime);
			ret.to = formatter.format(ret.toDateTime);
			
			ret.personalia = termin.getSubjectOrPatient();
			ret.reason = termin.getReason();
		}
		
		return ret;
	}
	
	public Period(){
		// needed for jaxb
	}
	
	public LocalDateTime getFromDateTime(){
		return fromDateTime;
	}
	
	@Override
	public int compareTo(Period other){
		return from.compareTo(other.from);
	}
}
