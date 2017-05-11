package ch.elexis.data;

import java.util.Hashtable;
import java.util.Optional;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IVerify;
import ch.elexis.core.model.IVerifyConverter;
import ch.elexis.core.model.BillingVerify;
import ch.elexis.core.types.VerifyType;
import ch.elexis.tarmedprefs.PreferenceConstants;

public class TarmedVerifyConverter implements IVerifyConverter {
	
	@Override
	public Optional<IVerify> convert(IBillable iBillable){
		IVerify verify = BillingVerify.create(iBillable, VerifyType.TARMED, 1);
		if (iBillable instanceof TarmedLeistung) {
			TarmedLeistung tLeistung = (TarmedLeistung) iBillable;
			Hashtable<?, ?> ext = tLeistung.loadExtension();
			verify.getInfo().put("code", tLeistung.getCode());
			verify.getInfo().put("isTarmed", "true");
			verify.getInfo().put("dbStateExists", String.valueOf(tLeistung.exists()));
			verify.getInfo().put("sparteAsText", tLeistung.getSparteAsText());
			verify.getInfo().put("Bezug", String.valueOf(ext.get("Bezug")));
			
			verify.getInfo().put("id", iBillable.getId());
			verify.getInfo().put("className", iBillable.getClass().getName());
			
			verify.getInfo().put("requiresSide", String.valueOf(tLeistung.requiresSide()));
			verify.getInfo().put("exclusion", tLeistung.getExclusion());
			verify.getInfo().put("parentCode", tLeistung.getParent());
			verify.getInfo().put("leistungTyp", String.valueOf(ext.get("LEISTUNG_TYP")));
			
			boolean optify = CoreHub.userCfg.get(Preferences.LEISTUNGSCODES_OPTIFY, true);
			if (optify) {
				verify.getInfo().put("optify", String.valueOf(optify));
				verify.getInfo().put("limits", String.valueOf(ext.get("limits")));
				verify.getInfo().put("GueltigVon", tLeistung.get("GueltigVon"));
				verify.getInfo().put("GueltigBis", tLeistung.get("GueltigBis"));
				
			}
			verify.getInfo().put("billElectronically", String.valueOf(CoreHub.mandantCfg != null
				&& CoreHub.mandantCfg.get(PreferenceConstants.BILL_ELECTRONICALLY, false)));
			return Optional.of(verify);
		}
		else {
			return Optional.empty();
		}
		
	}
	
}
