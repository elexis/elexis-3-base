package ch.elexis.labortarif2009.data;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IModelService;

@Component
public class ModelServiceHolder {
	
	private static IModelService modelService;
	
	@Reference(target = "(" + IModelService.SERVICEMODELNAME
		+ "=ch.elexis.base.ch.labortarif.model)")
	public void setModelService(IModelService modelService){
		ModelServiceHolder.modelService = modelService;
	}
	
	public static IModelService get(){
		if (modelService == null) {
			throw new IllegalStateException("No IModelService available");
		}
		return modelService;
	}
}
