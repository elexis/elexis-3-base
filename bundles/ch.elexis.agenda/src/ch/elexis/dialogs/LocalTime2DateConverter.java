package ch.elexis.dialogs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import org.eclipse.core.databinding.conversion.IConverter;

public class LocalTime2DateConverter implements IConverter<LocalTime, Date> {

	@Override
	public Object getFromType() {
		return LocalTime.class;
	}

	@Override
	public Object getToType() {
		return Date.class;
	}

	@Override
	public Date convert(LocalTime fromObject) {
		if (fromObject != null) {
			return Date.from(LocalDateTime.of(LocalDate.now(), fromObject).atZone(ZoneId.systemDefault()).toInstant());
		}
		return null;
	}
}
