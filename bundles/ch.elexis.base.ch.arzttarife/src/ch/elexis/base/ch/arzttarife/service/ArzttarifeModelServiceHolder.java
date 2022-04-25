package ch.elexis.base.ch.arzttarife.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IModelService;

@Component
public class ArzttarifeModelServiceHolder {
	private static IModelService modelService;

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.base.ch.arzttarife.model)")
	public void setModelService(IModelService modelService) {
		ArzttarifeModelServiceHolder.modelService = modelService;
	}

	public static IModelService get() {
		if (modelService == null) {
			throw new IllegalStateException("No IModelService available");
		}
		return modelService;
	}
}
