package ch.elexis.mednet.webapi.core.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IModelService;

@Component
public class OmnivoreModelServiceHolder {

	private static IModelService omnivoreModelService;

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.omnivore.data.model)")
	public void setModelService(IModelService modelService) {
		OmnivoreModelServiceHolder.omnivoreModelService = modelService;
	}

	public static IModelService get() {
		return omnivoreModelService;
	}
}
