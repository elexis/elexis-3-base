package at.medevit.elexis.agenda.ui.xml;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ch.elexis.agenda.data.Termin;
import ch.elexis.core.model.IPeriod;


@XmlRootElement
public class AreaPeriodsLetter {
	
	public static AreaPeriodsLetter of(String area, List<IPeriod> areaPeriods){
		AreaPeriodsLetter ret = new AreaPeriodsLetter();
		ret.area = area;
		ret.period = areaPeriods.stream().filter(ap -> !isDayLimit(ap)).map(ap -> Period.of(ap))
			.collect(Collectors.toList());
		Collections.sort(ret.period);
		if (!ret.period.isEmpty()) {
			LocalDate fromLocalDate = ret.period.get(0).getFromTool().toLocalDate();
			LocalDate toLocalDate =
				ret.period.get(ret.period.size() - 1).getFromTool().toLocalDate();
			if (fromLocalDate.equals(toLocalDate)) {
				ret.areaPeriod = fromLocalDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
			} else {
				ret.areaPeriod = fromLocalDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
					+ " - " + toLocalDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
			}
		}
		return ret;
	}
	
	public static boolean isDayLimit(IPeriod iPeriod){
		if (iPeriod instanceof Termin) {
			String type = ((Termin) iPeriod).getType();
			return type.equals(Termin.TerminTypes[1]);
		}
		return false;
	}
	
	private List<Period> period;
	
	@XmlElement
	private String area;
	@XmlElement
	private String areaPeriod;
	
	public AreaPeriodsLetter(){
	}
	
	public void setPeriod(List<Period> period){
		this.period = period;
	}
	
	public List<Period> getPeriod(){
		return period;
	}
	
	public String getArea(){
		return area;
	}
}