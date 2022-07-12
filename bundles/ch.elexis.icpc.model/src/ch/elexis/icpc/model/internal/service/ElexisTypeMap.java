package ch.elexis.icpc.model.internal.service;

import java.util.HashMap;

import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.jpa.entities.ICPCCode;
import ch.elexis.core.jpa.entities.ICPCEncounter;
import ch.elexis.core.jpa.entities.ICPCEpisode;
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

	public static final String TYPE_ICPCCODE = "ch.elexis.icpc.IcpcCode"; //$NON-NLS-1$
	public static final String TYPE_ICPCENCOUNTER = "ch.elexis.icpc.Encounter"; //$NON-NLS-1$
	public static final String TYPE_ICPCEPISODE = "ch.elexis.icpc.Episode"; //$NON-NLS-1$

	static {
		stsToClassMap = new HashMap<String, Class<? extends EntityWithId>>();
		classToStsMap = new HashMap<Class<? extends EntityWithId>, String>();

		// bi-directional mappable
		stsToClassMap.put(TYPE_ICPCCODE, ICPCCode.class);
		classToStsMap.put(ICPCCode.class, TYPE_ICPCCODE);
		stsToClassMap.put(TYPE_ICPCENCOUNTER, ICPCEncounter.class);
		classToStsMap.put(ICPCEncounter.class, TYPE_ICPCENCOUNTER);
		stsToClassMap.put(TYPE_ICPCEPISODE, ICPCEpisode.class);
		classToStsMap.put(ICPCEpisode.class, TYPE_ICPCEPISODE);
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