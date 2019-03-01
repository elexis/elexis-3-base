package ch.elexis.base.ch.arzttarife.model.service;

import java.util.Optional;

import ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung;
import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.core.jpa.entities.ComplementaryLeistung;
import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.jpa.entities.PhysioLeistung;
import ch.elexis.core.jpa.entities.RFE;
import ch.elexis.core.jpa.entities.TarmedExtension;
import ch.elexis.core.jpa.entities.TarmedGroup;
import ch.elexis.core.jpa.entities.TarmedKumulation;
import ch.elexis.core.jpa.entities.TarmedLeistung;
import ch.elexis.core.jpa.model.adapter.AbstractModelAdapterFactory;
import ch.elexis.core.jpa.model.adapter.MappingEntry;
import ch.elexis.core.model.Identifiable;

public class ArzttarifeModelAdapterFactory extends AbstractModelAdapterFactory {
	
	private static ArzttarifeModelAdapterFactory INSTANCE;
	
	public static synchronized ArzttarifeModelAdapterFactory getInstance(){
		if (INSTANCE == null) {
			INSTANCE = new ArzttarifeModelAdapterFactory();
		}
		return INSTANCE;
	}
	
	private ArzttarifeModelAdapterFactory(){
		super();
	}
	
	@Override
	protected void initializeMappings(){
		addMapping(new MappingEntry(IPhysioLeistung.class,
			ch.elexis.base.ch.arzttarife.physio.model.PhysioLeistung.class, PhysioLeistung.class));
		addMapping(new MappingEntry(IComplementaryLeistung.class,
			ch.elexis.base.ch.arzttarife.complementary.model.ComplementaryLeistung.class,
			ComplementaryLeistung.class));
		addMapping(new MappingEntry(ITarmedLeistung.class,
			ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung.class, TarmedLeistung.class));
		addMapping(new MappingEntry(ITarmedExtension.class,
			ch.elexis.base.ch.arzttarife.tarmed.model.TarmedExtension.class,
			TarmedExtension.class));
		addMapping(new MappingEntry(ITarmedKumulation.class,
			ch.elexis.base.ch.arzttarife.tarmed.model.TarmedKumulation.class,
			TarmedKumulation.class));
		addMapping(new MappingEntry(ITarmedGroup.class,
			ch.elexis.base.ch.arzttarife.tarmed.model.TarmedGroup.class, TarmedGroup.class));
		
		addMapping(new MappingEntry(IReasonForEncounter.class,
			ch.elexis.base.ch.arzttarife.rfe.model.ReasonForEncounter.class, RFE.class));
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getAdapter(EntityWithId entity, Class<T> clazz, boolean registerEntityChangeEvent){
		if (entity != null) {
			Optional<Identifiable> adapter =
				getInstance().getModelAdapter(entity, clazz, true, registerEntityChangeEvent);
			return (T) adapter.orElse(null);
		}
		return null;
	}
	
}
