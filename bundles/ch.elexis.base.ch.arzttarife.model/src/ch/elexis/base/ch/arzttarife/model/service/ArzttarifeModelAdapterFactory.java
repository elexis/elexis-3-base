package ch.elexis.base.ch.arzttarife.model.service;

import ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung;
import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.core.jpa.entities.ComplementaryLeistung;
import ch.elexis.core.jpa.entities.PhysioLeistung;
import ch.elexis.core.jpa.entities.TarmedLeistung;
import ch.elexis.core.jpa.model.adapter.AbstractModelAdapterFactory;
import ch.elexis.core.jpa.model.adapter.MappingEntry;

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
	}
	
}
