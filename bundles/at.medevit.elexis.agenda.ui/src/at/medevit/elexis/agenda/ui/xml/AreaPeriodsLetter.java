package at.medevit.elexis.agenda.ui.xml;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IPeriod;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.types.AppointmentType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AreaPeriodsLetter {

	public static AreaPeriodsLetter of(String area, List<IPeriod> areaPeriods) {
		AreaPeriodsLetter ret = new AreaPeriodsLetter();
		ret.area = area;
		ret.period = areaPeriods.stream().filter(ap -> !isDayLimit(ap)).map(ap -> Period.of(ap))
				.collect(Collectors.toList());
		Collections.sort(ret.period);
		if (!ret.period.isEmpty()) {
			LocalDate fromLocalDate = ret.period.get(0).getFromDateTime().toLocalDate();
			LocalDate toLocalDate = ret.period.get(ret.period.size() - 1).getFromDateTime().toLocalDate();
			if (fromLocalDate.equals(toLocalDate)) {
				ret.areaPeriod = fromLocalDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")); //$NON-NLS-1$
			} else {
				ret.areaPeriod = fromLocalDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) //$NON-NLS-1$
						+ " - " + toLocalDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return ret;
	}

	public static boolean isDayLimit(IPeriod iPeriod) {
		if (iPeriod instanceof IAppointment) {
			String type = ((IAppointment) iPeriod).getType();
			String reservedTypeString = AppointmentServiceHolder.get().getType(AppointmentType.BOOKED);
			return type.equals(reservedTypeString);
		}
		return false;
	}

	private List<Period> period;

	@XmlElement
	private String area;
	@XmlElement
	private String areaPeriod;

	public AreaPeriodsLetter() {
	}

	public void setPeriod(List<Period> period) {
		this.period = period;
	}

	public List<Period> getPeriod() {
		return period;
	}

	public String getArea() {
		return area;
	}
}