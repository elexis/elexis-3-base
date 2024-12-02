package ch.elexis.mednet.webapi.core.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IModelService;

@Component
public class ArtikelstammModelServiceHolder {
	private static IModelService modelService;

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=at.medevit.ch.artikelstamm.model)")
	public void setModelService(IModelService modelService) {
		ArtikelstammModelServiceHolder.modelService = modelService;
	}

	public static IModelService get() {
		if (modelService == null) {
			throw new IllegalStateException("No IModelService available"); //$NON-NLS-1$
		}
		return modelService;
	}
}