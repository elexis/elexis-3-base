package ch.elexis.base.ch.arzttarife.xml.exporter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.rgw.tools.TimeTool;

/**
 * Provides access to the configured vat values. Vat values are implemented in
 * at.medevit.medelexis.vat_ch. No direct dependency is possible as
 * at.medevit.medelexis.vat_ch is locate in a closed source repository.
 * 
 */
public class VatUtil {

	public static final String VAT_VALUE_WITH_DATE = "at.medevit.medelexis.vat_ch/ValueWithDate";

	private static String[] defaultRatesStart = { "0", "2.5", "8.0" };
	private static String[] defaultRates2018 = { "0", "2.5", "7.7" };
	private static String[] defaultRates2024 = { "0", "2.6", "8.1" };

	public static boolean isVatAvailable() {
		return !readDateableConfigValue(ConfigServiceHolder.get()).isEmpty();
	}

	/**
	 * Get the normal vat rate at the date. Value is set by
	 * at.medevit.medelexis.vat_ch.util.VatUtil.
	 * 
	 * @param perfConstant
	 * @param date
	 * @return
	 */
	public static String getNormalRateFromConfig(LocalDate date) {
		if (date != null) {
			return getCurrentValueFromConfig(date, 1);
		}
		return null;
	}

	/**
	 * Get the reduced vat rate at the date. Value is set by
	 * at.medevit.medelexis.vat_ch.util.VatUtil.
	 * 
	 * @param perfConstant
	 * @param date
	 * @return
	 */
	public static String getReducedRateFromConfig(LocalDate date) {
		if (date != null) {
			return getCurrentValueFromConfig(date, 2);
		}
		return null;
	}

	/**
	 * Returns the first matching value with is after date x.
	 *
	 * @param configValue
	 * @param date
	 * @param intValIdx
	 * @return
	 */
	private static String getCurrentValueFromConfig(LocalDate date, int intValIdx) {
		List<String> values = readDateableConfigValue(ConfigServiceHolder.get());

		values.sort((l, r) -> {
			String[] lsplits = l.split("::");
			String[] rsplits = r.split("::");
			if (!lsplits[0].isEmpty() && !rsplits[0].isEmpty()) {
				LocalDate lDate = new TimeTool(lsplits[0]).toLocalDate();
				LocalDate rDate = new TimeTool(rsplits[0]).toLocalDate();
				return rDate.compareTo(lDate);
			} else if (!lsplits[0].isEmpty() || !rsplits[0].isEmpty()) {
				if (!lsplits[0].isEmpty()) {
					return -1;
				} else {
					return 1;
				}
			}
			return 0;
		});

		for (String s : values) {
			String[] splits = s.split("::");
			if (splits.length == 3) {
				String dateAsString = splits[0];
				if (!dateAsString.isEmpty()) {
					LocalDate configDate = new TimeTool(dateAsString).toLocalDate();
					boolean isAfter = date.isAfter(configDate) || date.equals(configDate);
					if (isAfter) {
						return splits[intValIdx];
					}
				} else {
					// no date specified - for legacy propose
					return splits[intValIdx];
				}
			}
		}

		return null;
	}

	private static List<String> readDateableConfigValue(IConfigService configService) {
		List<String> configValues = new ArrayList<>();
		String rawValue = configService.get(VAT_VALUE_WITH_DATE, null);
		if (StringUtils.isBlank(rawValue)) {
			return configValues;
		}
		String[] configValue = rawValue.split(",");

		if (configValue != null) {
			configValues.addAll(Arrays.asList(configValue));
			for (String s : configValue) {
				if (s.startsWith("::")) {
					break;
				}
			}
		}
		return configValues;
	}

	public static int guessVatCode(Double vatRate) {
		if (vatRate != null) {
			// make a guess for the correct code
			if (vatRate == 0)
				return 0;
			else if (vatRate < 7)
				return 2;
			else
				return 1;
		}
		return 0;
	}

	/**
	 * String array containing [no, reduced, normal] vat rates as string for the
	 * provided date.
	 * 
	 * @param date
	 * @return
	 */
	public static String[] getDefaultVatRateArray(LocalDate date) {
		if (date.isBefore(LocalDate.of(2018, 1, 1))) {
			return defaultRatesStart;
		} else if (date.isBefore(LocalDate.of(2024, 1, 1))) {
			return defaultRates2018;
		} else {
			return defaultRates2024;
		}
	}
}
