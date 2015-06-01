package ch.elexis.tarmed.printer;

import ch.elexis.core.ui.text.ITextTemplateRequirement;

public class TarmedTemplateRequirement implements ITextTemplateRequirement {
	
	public static final String TT_TARMED_S1 = "Tarmedrechnung_S1";
	public static final String TT_TARMED_S1_DESC =
		"Tarmed-Rechnungsvorlage 1. Seite (XML 4.0 Standard)";
	public static final String TT_TARMED_S2 = "Tarmedrechnung_S2";
	public static final String TT_TARMED_S2_DESC =
		"Tarmed-Rechnungsvorlage Folgeseite (XML 4.0 Standard)";
	public static final String TT_TARMED_44_S1 = "TR44_S1";
	public static final String TT_TARMED_44_S1_DESC =
		"Tarmed-Rechnungsvorlage 1.Seite (XML4.4 Standard)";
	public static final String TT_TARMED_44_S2 = "TR44_S2";
	public static final String TT_TARMED_44_S2_DESC =
		"Tarmed-Rechnungsvorlage Folgeseite (XML 4.4 Standard)";
	public static final String TT_TARMED_EZ = "Tarmedrechnung_EZ";
	public static final String TT_TARMED_EZ_DESC = "Tarmed-Rechnungsvorlage mit Einzahlungsschein";
	
	public static final String TT_TARMED_M1 = "Tarmedrechnung_M1";
	public static final String TT_TARMED_M1_DESC = "Tarmed Zahlungserinnerung (1. Mahnung)";
	public static final String TT_TARMED_M2 = "Tarmedrechnung_M2";
	public static final String TT_TARMED_M2_DESC = "Tarmedvorlage für 2.Mahnung";
	public static final String TT_TARMED_M3 = "Tarmedrechnung_M3";
	public static final String TT_TARMED_M3_DESC = "Tarmedvorlage für 3.Mahnung";
	
	@Override
	public String[] getNamesOfRequiredTextTemplate(){
		return new String[] {
			TT_TARMED_S1, TT_TARMED_S2, TT_TARMED_44_S1, TT_TARMED_44_S2, TT_TARMED_EZ,
			TT_TARMED_M1, TT_TARMED_M2, TT_TARMED_M3
		};
	}
	
	@Override
	public String[] getDescriptionsOfRequiredTextTemplate(){
		return new String[] {
			TT_TARMED_S1_DESC, TT_TARMED_S2_DESC, TT_TARMED_44_S1_DESC, TT_TARMED_44_S2_DESC,
			TT_TARMED_EZ_DESC, TT_TARMED_M1_DESC, TT_TARMED_M2_DESC, TT_TARMED_M3_DESC
		};
	}
	
}
