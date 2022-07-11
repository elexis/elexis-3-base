package ch.elexis.base.befunde.model;

import java.util.HashMap;

import ch.elexis.core.jpa.entities.Elexisbefunde;
import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.services.IModelService;

/**
 * Map type names from new {@link AbstractDBObjectIdDeleted} subclasses to
 * PersistentObject legacy type names. Use by
 * {@link IModelService#loadFromString(String)} and
 * {@link IModelService#storeToString(ch.elexis.core.model.Identifiable)}.
 *
 * @author thomas
 *
 */
public class ElexisTypeMap {

	private static final HashMap<String, Class<? extends EntityWithId>> stsToClassMap;
	private static final HashMap<Class<? extends EntityWithId>, String> classToStsMap;

	public static final String TYPE_MESSWERT = "ch.elexis.befunde.Messwert"; //$NON-NLS-1$

	static {
		stsToClassMap = new HashMap<String, Class<? extends EntityWithId>>();
		classToStsMap = new HashMap<Class<? extends EntityWithId>, String>();

		// bi-directional mappable
		stsToClassMap.put(TYPE_MESSWERT, Elexisbefunde.class);
		classToStsMap.put(Elexisbefunde.class, TYPE_MESSWERT);
	}

	/**
	 *
	 * @param obj
	 * @return <code>null</code> if not resolvable, else the resp. Entity Type
	 */
	public static String getKeyForObject(EntityWithId obj) {
		if (obj != null) {
			return classToStsMap.get(obj.getClass());
		}

		return null;
	}

	public static Class<? extends EntityWithId> get(String value) {
		return stsToClassMap.get(value);
	}
}