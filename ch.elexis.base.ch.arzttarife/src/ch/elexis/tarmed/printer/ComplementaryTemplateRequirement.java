package ch.elexis.tarmed.printer;

import ch.elexis.core.ui.text.ITextTemplateRequirement;

public class ComplementaryTemplateRequirement implements ITextTemplateRequirement {
	
	public static final String TT_COMPLEMENTARY_S1 = "TR59_S1";
	public static final String TT_COMPLEMENTARY_S1_DESC =
		"Komplementär-Rechnungsvorlage 1.Seite (XML4.4 Standard)";
	public static final String TT_COMPLEMENTARY_S2 = "TR59_S2";
	public static final String TT_COMPLEMENTARY_S2_DESC =
		"Komplementär-Rechnungsvorlage Folgeseite (XML 4.4 Standard)";
	public static final String TT_COMPLEMENTARY_EZ = "TR59_EZ";
	public static final String TT_COMPLEMENTARY_EZ_DESC =
		"Komplementär-Rechnungsvorlage mit Einzahlungsschein";
	
	@Override
	public String[] getNamesOfRequiredTextTemplate(){
		return new String[] {
			TT_COMPLEMENTARY_S1, TT_COMPLEMENTARY_S2, TT_COMPLEMENTARY_EZ
		};
	}
	
	@Override
	public String[] getDescriptionsOfRequiredTextTemplate(){
		return new String[] {
			TT_COMPLEMENTARY_S1_DESC, TT_COMPLEMENTARY_S2_DESC, TT_COMPLEMENTARY_EZ_DESC
		};
	}
}