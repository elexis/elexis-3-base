package at.medevit.elexis.emediplan.core;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class EMediplanServiceHolder {
	
	private static EMediplanService eMediplanService;
	
	public EMediplanServiceHolder(){
	}
	
	@Reference
	public void setReference(EMediplanService eMediplanService){
		EMediplanServiceHolder.eMediplanService = eMediplanService;
	}
	
	public void unsetReference(EMediplanService eMediplanService){
		EMediplanServiceHolder.eMediplanService = null;
	}
	
	public static EMediplanService getService(){
		return eMediplanService;
	}
}
