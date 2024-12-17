package ch.elexis.dialogs;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import org.eclipse.core.databinding.conversion.IConverter;

public class Date2LocalTimeConverter implements IConverter<Date, LocalTime> {

	@Override
	public Object getFromType() {
		return Date.class;
	}

	@Override
	public Object getToType() {
		return LocalTime.class;
	}

	@Override
	public LocalTime convert(Date fromObject) {
		if (fromObject != null) {
			return LocalDateTime.ofInstant(fromObject.toInstant(), ZoneId.systemDefault()).toLocalTime();
		}
		return null;
	}

}
