package at.medevit.ch.artikelstamm.model.common.preference;

import at.medevit.ch.artikelstamm.marge.Marge;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.rgw.tools.Money;

public class MargePreference {

	private static Marge[] marges = null;

	public static Marge[] getMarges() {
		if (marges == null) {
			initMarges();
		}
		return marges;
	}

	public static void storeMargeConfiguration() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < marges.length; i++) {
			Marge m = marges[i];
			sb.append(m.getStartInterval() + "/" + m.getEndInterval() + "/" + m.getAddition()); //$NON-NLS-1$ //$NON-NLS-2$
			if (i != marges.length)
				sb.append("$"); //$NON-NLS-1$
		}
		ConfigServiceHolder.get().set(PreferenceConstants.PREV_CSV_MARGE_STORAGE, sb.toString());
	}

	private static void initMarges() {
		String margeStorageString = ConfigServiceHolder.get().get(PreferenceConstants.PREV_CSV_MARGE_STORAGE,
				"0/0/0$0/0/0$0/0/0"); //$NON-NLS-1$
		String[] margeStorageEntry = margeStorageString.split("\\$"); //$NON-NLS-1$
		marges = new Marge[margeStorageEntry.length];
		for (int i = 0; i < margeStorageEntry.length; i++) {
			String entry = margeStorageEntry[i];
			String[] values = entry.split("/"); //$NON-NLS-1$
			if (values.length == 3) {
				marges[i] = new Marge();
				marges[i].setStartInterval(Double.parseDouble(values[0] == null ? "0.0" : values[0])); //$NON-NLS-1$
				marges[i].setEndInterval(Double.parseDouble(values[1] == null ? "0.0" : values[1])); //$NON-NLS-1$
				marges[i].setAddition(Double.parseDouble(values[2] == null ? "0.0" : values[2])); //$NON-NLS-1$
			}
		}
	}

	public static Money calculateVKP(Money ekPreis) {
		double amount = ekPreis.getAmount();

		for (int i = 0; i < getMarges().length; i++) {
			Marge m = getMarges()[i];
			if (!m.isValid())
				continue;
			if (m.startInterval <= amount && m.endInterval >= amount) {
				double mult = 1 + (m.addition / 100);
				return new Money(amount * mult);
			}
		}

		return new Money();
	}

}
