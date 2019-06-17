package ch.elexis.base.ch.diagnosecodes.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IModelService;

@Component
public class ICD10ModelServiceHolder {
	
	private static IModelService service;
	
	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.base.ch.icd10)")
	public void setModelService(IModelService service){
		ICD10ModelServiceHolder.service = service;
	}
	
	public static IModelService get(){
		if (service == null) {
			throw new IllegalStateException("No IModelService available");
		}
		return service;
	}
}
