package at.medevit.elexis.inbox.model.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.services.IModelService;

@Component(service = {})
public class ModelUtil {
	
	private static IModelService modelService;
	
	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	public void setModelService(IModelService modelService){
		ModelUtil.modelService = modelService;
	}
	
	public static <T> T loadCoreModel(EntityWithId entity, Class<T> clazz){
		if (entity != null) {
			return (T) modelService.load(entity.getId(), clazz).orElse(null);
		}
		return null;
	}
}
