package ch.elexis.privatrechnung.rechnung;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.text.ITextTemplateRequirement;
import ch.elexis.privatrechnung.data.PreferenceConstants;

public class PrivaterechnungTextTemplateRequirement implements ITextTemplateRequirement {
	public static final String TT_BILL_DESC = "Vorlage Privatrechnung";
	public static final String TT_BILL_ESR_DESC = "Vorlage Privatrechnung (ESR)";
	
	@Override
	public String[] getNamesOfRequiredTextTemplate(){
		return new String[] {
			getBillTemplate(), getESRTemplate()
		};
	}
	
	@Override
	public String[] getDescriptionsOfRequiredTextTemplate(){
		return new String[] {
			TT_BILL_DESC, TT_BILL_ESR_DESC
		};
	}
	
	public static String getBillTemplate(){
		return CoreHub.globalCfg.get(PreferenceConstants.cfgTemplateBill,
			PreferenceConstants.DEFAULT_TEMPLATE_BILL);
	}
	
	public static String getESRTemplate(){
		return CoreHub.globalCfg.get(PreferenceConstants.cfgTemplateESR,
			PreferenceConstants.DEFAULT_TEMPLATE_ESR);
	}
}
