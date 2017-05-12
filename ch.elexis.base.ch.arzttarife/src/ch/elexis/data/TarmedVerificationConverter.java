package ch.elexis.data;

import java.util.Hashtable;
import java.util.Optional;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.BillingVerification;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IVerificationConverter;
import ch.elexis.core.types.VerificationType;
import ch.elexis.tarmedprefs.PreferenceConstants;

public class TarmedVerificationConverter
		implements IVerificationConverter<IBillable, BillingVerification> {
	
	@Override
	public Optional<BillingVerification> convert(IBillable iBillable){
		BillingVerification billingVerification =
			BillingVerification.create(iBillable, VerificationType.TARMED, 1);
		if (iBillable instanceof TarmedLeistung) {
			TarmedLeistung tLeistung = (TarmedLeistung) iBillable;
			Hashtable<?, ?> ext = tLeistung.loadExtension();
			billingVerification.getInfo().put("code", tLeistung.getCode());
			billingVerification.getInfo().put("isTarmed", "true");
			billingVerification.getInfo().put("dbStateExists", String.valueOf(tLeistung.exists()));
			billingVerification.getInfo().put("sparteAsText", tLeistung.getSparteAsText());
			billingVerification.getInfo().put("Bezug", String.valueOf(ext.get("Bezug")));
			
			billingVerification.getInfo().put("id", iBillable.getId());
			billingVerification.getInfo().put("className", iBillable.getClass().getName());
			
			billingVerification.getInfo().put("requiresSide",
				String.valueOf(tLeistung.requiresSide()));
			billingVerification.getInfo().put("exclusion", tLeistung.getExclusion());
			billingVerification.getInfo().put("parentCode", tLeistung.getParent());
			billingVerification.getInfo().put("leistungTyp",
				String.valueOf(ext.get("LEISTUNG_TYP")));
			
			boolean optify = CoreHub.userCfg.get(Preferences.LEISTUNGSCODES_OPTIFY, true);
			if (optify) {
				billingVerification.getInfo().put("optify", String.valueOf(optify));
				billingVerification.getInfo().put("limits", String.valueOf(ext.get("limits")));
				billingVerification.getInfo().put("GueltigVon", tLeistung.get("GueltigVon"));
				billingVerification.getInfo().put("GueltigBis", tLeistung.get("GueltigBis"));
				
			}
			billingVerification.getInfo().put("billElectronically",
				String.valueOf(CoreHub.mandantCfg != null
				&& CoreHub.mandantCfg.get(PreferenceConstants.BILL_ELECTRONICALLY, false)));
			return Optional.of(billingVerification);
		} else {
			return Optional.empty();
		}
		
	}
	
}
