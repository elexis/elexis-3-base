package ch.itmed.fop.printing.print;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.constants.OrderLabelPrintConstants;

public final class OrderEntryLabelParameter {

	private static final Logger logger = LoggerFactory.getLogger(OrderEntryLabelParameter.class);

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
			String[] idAndAmount = part.trim().split(Pattern.quote(OrderLabelPrintConstants.AMOUNT_SEPARATOR));
			if (idAndAmount.length != 2 || StringUtils.isBlank(idAndAmount[0])) {
				logger.warn("Ignoring invalid order entry label parameter [{}]", part); //$NON-NLS-1$
				continue;
			}
			try {
				int amount = Integer.parseInt(idAndAmount[1].trim());
				if (amount > 0) {
					ret.merge(idAndAmount[0].trim(), amount, Integer::sum);
				} else {
					logger.warn("Ignoring non positive amount in order entry label parameter [{}]", part); //$NON-NLS-1$
				}
			} catch (NumberFormatException e) {
				logger.warn("Ignoring invalid amount in order entry label parameter [{}]", part); //$NON-NLS-1$
			}
		}
		return ret;
	}
}
