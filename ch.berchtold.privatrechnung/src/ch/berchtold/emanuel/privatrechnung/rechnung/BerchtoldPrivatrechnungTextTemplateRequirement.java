package ch.berchtold.emanuel.privatrechnung.rechnung;

import ch.berchtold.emanuel.privatrechnung.data.PreferenceConstants;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.text.ITextTemplateRequirement;
import ch.elexis.data.Mandant;

public class BerchtoldPrivatrechnungTextTemplateRequirement implements ITextTemplateRequirement {
	
	public static final String TT_BILL1_DESC = "Vorlage Berchtold Privatrechnung 1";
	public static final String TT_BILL2_DESC = "Vorlage Berchtold Privatrechnung 2";
	public static final String TT_ESR_DESC = "Vorlage Berchtold Privatrechnung ESR";
	
	@Override
	public String[] getNamesOfRequiredTextTemplate(){
		return new String[] {
			getBill1Template(), getBill2Template(), getESRTemplate()
		};
	}
	
	@Override
	public String[] getDescriptionsOfRequiredTextTemplate(){
		return new String[] {
			TT_BILL1_DESC, TT_BILL2_DESC, TT_ESR_DESC
		};
	}
	
	public static String getBill1Template(){
		Mandant sm = ElexisEventDispatcher.getSelectedMandator();
		if(sm==null) return StringConstants.EMPTY;
		return CoreHub.localCfg.get(PreferenceConstants.cfgTemplateBill + "/"
			+ sm.getId(), StringConstants.EMPTY);
	}
	
	public static String getBill2Template(){
		Mandant sm = ElexisEventDispatcher.getSelectedMandator();
		if(sm==null) return StringConstants.EMPTY;
		return CoreHub.localCfg.get(PreferenceConstants.cfgTemplateBill2 + "/"
			+ sm.getId(), StringConstants.EMPTY);
	}
	
	public static String getESRTemplate(){
		Mandant sm = ElexisEventDispatcher.getSelectedMandator();
		if(sm==null) return StringConstants.EMPTY;
		return CoreHub.localCfg.get(PreferenceConstants.cfgTemplateESR + "/"
			+ sm.getId(), StringConstants.EMPTY);
	}
	
}
