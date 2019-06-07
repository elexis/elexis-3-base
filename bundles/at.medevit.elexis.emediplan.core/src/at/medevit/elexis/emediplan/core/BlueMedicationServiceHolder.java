package at.medevit.elexis.emediplan.core;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class BlueMedicationServiceHolder {
	
	private static BlueMedicationService service;
	
	public BlueMedicationServiceHolder(){
	}
	
	@Reference
	public void setReference(BlueMedicationService eMediplanService){
		BlueMedicationServiceHolder.service = eMediplanService;
	}
	
	public void unsetReference(EMediplanService eMediplanService){
		BlueMedicationServiceHolder.service = null;
	}
	
	public static BlueMedicationService getService(){
		return service;
	}
}
