package ch.elexis.impfplan.text;

import ch.elexis.core.ui.text.ITextTemplateRequirement;

public class ImpfplanTextTemplateRequirement implements ITextTemplateRequirement {
	public static final String TT_VACCINATIONS = "Impfplan"; //$NON-NLS-1$
	public static final String TT_VACCINATIONS_DESC = "Vorlage zum Drucken des Impfplans";

	@Override
	public String[] getNamesOfRequiredTextTemplate() {
		return new String[] { TT_VACCINATIONS };
	}

	@Override
	public String[] getDescriptionsOfRequiredTextTemplate() {
		return new String[] { TT_VACCINATIONS_DESC };
	}

}
