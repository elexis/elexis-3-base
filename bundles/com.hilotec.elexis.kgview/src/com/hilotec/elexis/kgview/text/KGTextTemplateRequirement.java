package com.hilotec.elexis.kgview.text;

import ch.elexis.core.ui.text.ITextTemplateRequirement;

public class KGTextTemplateRequirement implements ITextTemplateRequirement {

	public static final String TT_ARCHIV_KG = "ArchivKG";
	public static final String TT_MEDICARD = "Medikarte";
	public static final String TT_ARCHIV_KG_DESC = "ArchivKG Vorlage";
	public static final String TT_MEDICARD_DESC = "Vorlage für Medikamenten Karte";

	@Override
	public String[] getNamesOfRequiredTextTemplate() {
		return new String[] { TT_ARCHIV_KG, TT_MEDICARD };
	}

	@Override
	public String[] getDescriptionsOfRequiredTextTemplate() {
		return new String[] { TT_ARCHIV_KG_DESC, TT_MEDICARD_DESC };
	}

}
