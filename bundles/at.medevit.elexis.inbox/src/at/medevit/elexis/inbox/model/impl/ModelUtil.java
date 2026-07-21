package at.medevit.elexis.inbox.model.impl;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.services.IModelService;

@Component(service = {})
public class ModelUtil {

	private static IModelService modelService;
	private static final int MODEL_CACHE_SIZE = 2048;
	private static final Map<String, WeakReference<Object>> modelCache = Collections
			.synchronizedMap(new LinkedHashMap<String, WeakReference<Object>>(MODEL_CACHE_SIZE, 0.75f, true) {
				private static final long serialVersionUID = 1L;

				@Override
				protected boolean removeEldestEntry(Map.Entry<String, WeakReference<Object>> eldest) {
					return size() > MODEL_CACHE_SIZE;
				}
			});

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	public void setModelService(IModelService modelService) {
		ModelUtil.modelService = modelService;
	}

	public static <T> T loadCoreModel(EntityWithId entity, Class<T> clazz) {
		if (entity != null) {
			String key = clazz.getName() + ':' + entity.getId();
			WeakReference<Object> reference = modelCache.get(key);
			Object cached = reference != null ? reference.get() : null;
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
