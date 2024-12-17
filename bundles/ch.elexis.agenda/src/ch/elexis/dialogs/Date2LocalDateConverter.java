package ch.elexis.dialogs;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.eclipse.core.databinding.conversion.IConverter;

public class Date2LocalDateConverter implements IConverter<Date, LocalDate> {

	@Override
	public Object getFromType() {
		return Date.class;
	}

	@Override
	public Object getToType() {
		return LocalDate.class;
	}

	@Override
	public LocalDate convert(Date fromObject) {
		if (fromObject != null) {
			return Instant.ofEpochMilli(fromObject.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		}
		return null;
	}

}
