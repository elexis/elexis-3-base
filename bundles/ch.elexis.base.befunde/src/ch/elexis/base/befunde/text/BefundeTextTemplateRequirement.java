package ch.elexis.base.befunde.text;

import ch.elexis.core.ui.text.ITextTemplateRequirement;

public class BefundeTextTemplateRequirement implements ITextTemplateRequirement {
	public static final String TT_MEASUREMENTS = "Messwerte";
	public static final String TT_MEASUREMENTS_DESC = "Vorlage zum Drucken von Messwerten";

	@Override
	public String[] getNamesOfRequiredTextTemplate() {
		return new String[] { TT_MEASUREMENTS };
	}

	@Override
	public String[] getDescriptionsOfRequiredTextTemplate() {
		return new String[] { TT_MEASUREMENTS_DESC };
	}

}
