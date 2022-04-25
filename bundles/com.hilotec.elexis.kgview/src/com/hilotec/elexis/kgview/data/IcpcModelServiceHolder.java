package com.hilotec.elexis.kgview.data;

import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IModelService;

public class IcpcModelServiceHolder {
	private static IModelService modelService;

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.icpc.model)")
	public void setModelService(IModelService modelService) {
		IcpcModelServiceHolder.modelService = modelService;
	}

	public static IModelService get() {
		if (modelService == null) {
			throw new IllegalStateException("No IModelService available");
		}
		return modelService;
	}
}
