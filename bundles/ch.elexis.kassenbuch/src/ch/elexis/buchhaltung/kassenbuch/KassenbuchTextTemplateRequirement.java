package ch.elexis.buchhaltung.kassenbuch;

import ch.elexis.core.ui.text.ITextTemplateRequirement;

public class KassenbuchTextTemplateRequirement implements ITextTemplateRequirement {
	public static final String TT_LIST = "Liste";
	public static final String TT_LIST_DESC = "Vorlage für Liste aus Kassenbuch";

	@Override
	public String[] getNamesOfRequiredTextTemplate() {
		return new String[] { TT_LIST };
	}

	@Override
	public String[] getDescriptionsOfRequiredTextTemplate() {
		return new String[] { TT_LIST_DESC };
	}

}
