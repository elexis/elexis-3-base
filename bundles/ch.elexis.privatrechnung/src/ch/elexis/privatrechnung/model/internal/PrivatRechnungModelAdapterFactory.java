package ch.elexis.privatrechnung.model.internal;

import ch.elexis.core.jpa.entities.PrivatLeistung;
import ch.elexis.core.jpa.model.adapter.AbstractModelAdapterFactory;
import ch.elexis.core.jpa.model.adapter.MappingEntry;
import ch.elexis.privatrechnung.model.IPrivatLeistung;

public class PrivatRechnungModelAdapterFactory extends AbstractModelAdapterFactory {
	
	private static PrivatRechnungModelAdapterFactory INSTANCE;
	
	public static synchronized PrivatRechnungModelAdapterFactory getInstance(){
		if (INSTANCE == null) {
			INSTANCE = new PrivatRechnungModelAdapterFactory();
		}
		return INSTANCE;
	}
	
	private PrivatRechnungModelAdapterFactory(){
		super();
	}
	
	@Override
	protected void initializeMappings(){
		addMapping(new MappingEntry(IPrivatLeistung.class, Leistung.class, PrivatLeistung.class));
	}
	
}