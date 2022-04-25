package ch.berchtold.emanuel.privatrechnung.model.internal;

import ch.berchtold.emanuel.privatrechnung.model.IPrivatLeistung;
import ch.elexis.core.jpa.entities.BerchtoldPrivatLeistung;
import ch.elexis.core.jpa.model.adapter.AbstractModelAdapterFactory;
import ch.elexis.core.jpa.model.adapter.MappingEntry;

public class PrivatRechnungModelAdapterFactory extends AbstractModelAdapterFactory {

	private static PrivatRechnungModelAdapterFactory INSTANCE;

	public static synchronized PrivatRechnungModelAdapterFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PrivatRechnungModelAdapterFactory();
		}
		return INSTANCE;
	}

	private PrivatRechnungModelAdapterFactory() {
		super();
	}

	@Override
	protected void initializeMappings() {
		addMapping(new MappingEntry(IPrivatLeistung.class, Leistung.class, BerchtoldPrivatLeistung.class));
	}

}