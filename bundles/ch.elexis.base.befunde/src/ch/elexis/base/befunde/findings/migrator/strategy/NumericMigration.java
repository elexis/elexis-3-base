package ch.elexis.base.befunde.findings.migrator.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.base.befunde.findings.migrator.messwert.MesswertFieldMapping;
import ch.elexis.befunde.Messwert;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.findings.IObservation;
import ch.elexis.core.findings.templates.model.InputDataNumeric;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class NumericMigration extends AbstractMigrationStrategy implements IMigrationStrategy {

	private static Logger logger = LoggerFactory.getLogger(NumericMigration.class);

	private MesswertFieldMapping mapping;
	private Messwert messwert;

	public NumericMigration(MesswertFieldMapping mapping, Messwert messwert) {
		this.mapping = mapping;
		this.messwert = messwert;
	}

	@Override
	public Optional<IObservation> migrate() {
		try {
			IObservation observation = (IObservation) templateService.createFinding(
					CoreModelServiceHolder.get().load(messwert.get(Messwert.FLD_PATIENT_ID), IPatient.class).get(),
					template);

			String result = messwert.getResult(mapping.getLocalBefundField());
			observation.setNumericValue(getValue(result), ((InputDataNumeric) template.getInputData()).getUnit());

			String comment = getComment(result);
			if (comment != null && !comment.isEmpty()) {
				observation.setComment(comment);
			}
			return Optional.of(observation);
		} catch (ElexisException e) {
			logger.error("Error creating observation", e); //$NON-NLS-1$
		}
		return Optional.empty();
	}

	/**
	 * Get the first numeric value.
	 *
	 * @param result
	 * @return
	 */
	public static BigDecimal getValue(String result) {
		StringBuilder sb = new StringBuilder();
		for (char c : result.toCharArray()) {
			if (Character.isDigit(c) || c == '.' || c == ',') {
				sb.append(c);
			} else {
				break;
			}
		}
		if (sb.length() > 0) {
			String value = sb.toString().replaceAll(",", "."); //$NON-NLS-1$ //$NON-NLS-2$
			if (value.startsWith(".")) { //$NON-NLS-1$
				value = "0" + value; //$NON-NLS-1$
			}
			if (value.endsWith(".")) { //$NON-NLS-1$
				value = value + "0"; //$NON-NLS-1$
			}
			try {
				return new BigDecimal(value);
			} catch (NumberFormatException ne) {
				logger.error("Could not parse numeric result [" + result + "] value [" + value + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		return null;
	}

	/**
	 * Get non numeric text at the end of the String.
	 *
	 * @param result
	 * @return
	 */
	public static String getComment(String result) {
		StringBuilder sb = new StringBuilder();
		char[] charArray = result.toCharArray();
		for (int i = charArray.length - 1; i > -1; i--) {
			char c = charArray[i];
			if (!Character.isDigit(c)) {
				sb.append(c);
			} else {
				break;
			}
		}
		if (sb.length() > 0) {
			sb = sb.reverse();
			return sb.toString();
		}
		return null;
	}

	/**
	 * Get a list of numeric values.
	 *
	 * @param result
	 * @return
	 */
	public static List<BigDecimal> getValues(String result) {
		List<BigDecimal> ret = new ArrayList<>();

		List<String> parts = new ArrayList<>();
		String[] spacesSplits = result.split(StringUtils.SPACE);
		for (String spacesSplit : spacesSplits) {
			String[] slashSplits = spacesSplit.split("\\/"); //$NON-NLS-1$
			for (String slashSplit : slashSplits) {
				parts.add(slashSplit);
			}
		}

		for (String string : parts) {
			BigDecimal value = getValue(string);
			if (value != null) {
				ret.add(value);
			}
		}
		return ret;
	}
}
