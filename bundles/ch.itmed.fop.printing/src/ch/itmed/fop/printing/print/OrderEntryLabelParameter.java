package ch.itmed.fop.printing.print;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.constants.OrderLabelPrintConstants;

public final class OrderEntryLabelParameter {

	private static final Logger logger = LoggerFactory.getLogger(OrderEntryLabelParameter.class);

	private static final Pattern ENTRY_PATTERN = Pattern.compile("(?<id>[^:\\s]+):(?<amount>[1-9]\\d{0,8})"); //$NON-NLS-1$

	private OrderEntryLabelParameter() {
	}

	public static Map<String, Integer> parse(String value) {
		Map<String, Integer> ret = new LinkedHashMap<>();
		if (StringUtils.isBlank(value)) {
			return ret;
		}
		for (String part : value.split(Pattern.quote(OrderLabelPrintConstants.ENTRY_SEPARATOR))) {
			if (StringUtils.isBlank(part)) {
				continue;
			}
			Matcher matcher = ENTRY_PATTERN.matcher(part.trim());
			if (matcher.matches()) {
				ret.merge(matcher.group("id"), Integer.parseInt(matcher.group("amount")), Integer::sum); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				logger.warn("Ignoring invalid order entry label parameter [{}]", part); //$NON-NLS-1$
			}
		}
		return ret;
	}
}
