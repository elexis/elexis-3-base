package ch.elexis.icpc.model.internal.service;

import java.util.Optional;

import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.jpa.entities.ICPCCode;
import ch.elexis.core.jpa.entities.ICPCEncounter;
import ch.elexis.core.jpa.entities.ICPCEpisode;
import ch.elexis.core.jpa.model.adapter.AbstractModelAdapterFactory;
import ch.elexis.core.jpa.model.adapter.MappingEntry;
import ch.elexis.core.model.Identifiable;
import ch.elexis.icpc.model.icpc.IcpcCode;
import ch.elexis.icpc.model.icpc.IcpcEncounter;
import ch.elexis.icpc.model.icpc.IcpcEpisode;
import ch.elexis.icpc.model.internal.Code;
import ch.elexis.icpc.model.internal.Encounter;
import ch.elexis.icpc.model.internal.Episode;

public class IcpcModelAdapterFactory extends AbstractModelAdapterFactory {

	private static IcpcModelAdapterFactory INSTANCE;

	public static synchronized IcpcModelAdapterFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new IcpcModelAdapterFactory();
		}
		return INSTANCE;
	}

	private IcpcModelAdapterFactory() {
		super();
	}

	@Override
	protected void initializeMappings() {
		addMapping(new MappingEntry(IcpcCode.class, Code.class, ICPCCode.class));
		addMapping(new MappingEntry(IcpcEpisode.class, Episode.class, ICPCEpisode.class));
		addMapping(new MappingEntry(IcpcEncounter.class, Encounter.class, ICPCEncounter.class));
	}

	@SuppressWarnings("unchecked")
	public <T> T getAdapter(EntityWithId entity, Class<T> clazz, boolean registerEntityChangeEvent) {
		if (entity != null) {
			Optional<Identifiable> adapter = getInstance().getModelAdapter(entity, clazz, true,
					registerEntityChangeEvent);
			return (T) adapter.orElse(null);
		}
		return null;
	}

}
