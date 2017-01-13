package at.medevit.elexis.ehc.ui.vacdoc.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import at.medevit.elexis.ehc.vacdoc.service.MeineImpfungenService;

@Component
public class MeineImpfungenServiceComponent {
	private static MeineImpfungenService miService;
	
	@Reference
	public void setMeineImpfungenService(MeineImpfungenService miService){
		MeineImpfungenServiceComponent.miService = miService;
	}
	
	public void unsetMeineImpfungenService(MeineImpfungenService miService){
		MeineImpfungenServiceComponent.miService = null;
	}
	
	public static MeineImpfungenService getService(){
		if (miService == null) {
			throw new IllegalStateException("No MeineImpfungenService available");
		}
		return miService;
	}
}
