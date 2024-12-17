package ch.elexis.dialogs;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.eclipse.core.databinding.conversion.IConverter;

public class LocalDate2DateConverter implements IConverter<LocalDate, Date> {

	@Override
	public Object getFromType() {
		return LocalDate.class;
	}

	@Override
	public Object getToType() {
		return Date.class;
	}

	@Override
	public Date convert(LocalDate fromObject) {
		if (fromObject != null) {
			return Date.from(fromObject.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		}
		return null;
	}
}
