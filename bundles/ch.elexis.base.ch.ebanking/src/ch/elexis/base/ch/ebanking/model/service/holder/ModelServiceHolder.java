package ch.elexis.base.ch.ebanking.model.service.holder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IModelService;

@Component
public class ModelServiceHolder {

	private static IModelService modelService;

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.base.ch.ebanking.model)")
	public void setModelService(IModelService modelService) {
		ModelServiceHolder.modelService = modelService;
	}

	public static IModelService get() {
		if (modelService == null) {
			throw new IllegalStateException("No IModelService available"); //$NON-NLS-1$
		}
		return modelService;
	}
}
