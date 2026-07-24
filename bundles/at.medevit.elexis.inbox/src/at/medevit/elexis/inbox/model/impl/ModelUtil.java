package at.medevit.elexis.inbox.model.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.services.IModelService;

@Component(service = {})
public class ModelUtil {

	private static IModelService modelService;
	private static final int MODEL_CACHE_SIZE = 2048;
	private static final Cache<String, Object> modelCache = CacheBuilder.newBuilder().maximumSize(MODEL_CACHE_SIZE)
			.weakValues().build();

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	public void setModelService(IModelService modelService) {
		ModelUtil.modelService = modelService;
	}

	public static <T> T loadCoreModel(EntityWithId entity, Class<T> clazz) {
		if (entity != null) {
			String key = clazz.getName() + ':' + entity.getId();
			Object cached = modelCache.getIfPresent(key);
			if (cached != null) {
				return clazz.cast(cached);
			}
			T loaded = modelService.load(entity.getId(), clazz).orElse(null);
			if (loaded != null) {
				modelCache.put(key, new WeakReference<>(loaded));
			}
			return loaded;
		}
		return null;
	}
}
