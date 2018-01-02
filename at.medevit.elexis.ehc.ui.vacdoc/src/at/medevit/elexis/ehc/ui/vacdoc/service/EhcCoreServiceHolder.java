package at.medevit.elexis.ehc.ui.vacdoc.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import at.medevit.elexis.ehc.core.EhcCoreService;

@Component
public class EhcCoreServiceHolder {
	private static EhcCoreService ehcCoreService;
	
	@Reference
	public void setEhcCoreService(EhcCoreService ehcCoreService){
		EhcCoreServiceHolder.ehcCoreService = ehcCoreService;
	}
	
	public void unsetEhcCoreService(EhcCoreService ehcCoreService){
		EhcCoreServiceHolder.ehcCoreService = null;
	}
	
	public static EhcCoreService getService(){
		if (ehcCoreService == null) {
			throw new IllegalStateException("No EhcCoreService available");
		}
		return ehcCoreService;
	}
}
