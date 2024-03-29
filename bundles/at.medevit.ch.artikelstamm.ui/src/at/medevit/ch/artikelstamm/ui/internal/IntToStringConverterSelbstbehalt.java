package at.medevit.ch.artikelstamm.ui.internal;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.conversion.Converter;

public class IntToStringConverterSelbstbehalt extends Converter {

	public IntToStringConverterSelbstbehalt() {
		super(Integer.class, String.class);
	}

	@Override
	public Object convert(Object fromObject) {
		if (fromObject instanceof Integer) {
			int value = (Integer) fromObject;
			if (value >= 0)
				return value + StringUtils.EMPTY;
		}
		return null;
	}

}
