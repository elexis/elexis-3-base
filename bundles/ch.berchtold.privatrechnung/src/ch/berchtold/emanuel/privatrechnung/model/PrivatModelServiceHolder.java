package ch.berchtold.emanuel.privatrechnung.model;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IModelService;

@Component
public class PrivatModelServiceHolder {
	private static IModelService modelService;
	
	@Reference(target = "(" + IModelService.SERVICEMODELNAME
		+ "=ch.berchtold.emanuel.privatrechnung.model)")
	public void setModelService(IModelService modelService){
		PrivatModelServiceHolder.modelService = modelService;
	}
	
	public static IModelService get(){
		if (modelService == null) {
			throw new IllegalStateException("No IModelService available");
		}
		return modelService;
	}
}
