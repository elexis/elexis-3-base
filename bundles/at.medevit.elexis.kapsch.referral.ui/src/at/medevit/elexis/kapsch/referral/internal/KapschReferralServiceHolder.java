package at.medevit.elexis.kapsch.referral.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import at.medevit.elexis.kapsch.referral.KapschReferralService;

@Component(service = {})
public class KapschReferralServiceHolder {
	
	private static KapschReferralService service;
	
	@Reference
	public void setService(KapschReferralService service){
		KapschReferralServiceHolder.service = service;
	}
	
	public static KapschReferralService get(){
		if (service == null) {
			throw new IllegalStateException("No service available");
		}
		return service;
	}
}
