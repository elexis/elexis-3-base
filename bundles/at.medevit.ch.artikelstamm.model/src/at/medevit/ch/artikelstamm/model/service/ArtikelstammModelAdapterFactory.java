package at.medevit.ch.artikelstamm.model.service;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import ch.elexis.core.jpa.entities.ArtikelstammItem;
import ch.elexis.core.jpa.model.adapter.AbstractModelAdapterFactory;
import ch.elexis.core.jpa.model.adapter.MappingEntry;

public class ArtikelstammModelAdapterFactory extends AbstractModelAdapterFactory {
	
	private static ArtikelstammModelAdapterFactory INSTANCE;
	
	public static synchronized ArtikelstammModelAdapterFactory getInstance(){
		if (INSTANCE == null) {
			INSTANCE = new ArtikelstammModelAdapterFactory();
		}
		return INSTANCE;
	}
	
	private ArtikelstammModelAdapterFactory(){
		super();
	}
	
	@Override
	protected void initializeMappings(){
		addMapping(new MappingEntry(IArtikelstammItem.class,
			at.medevit.ch.artikelstamm.model.ArtikelstammItem.class, ArtikelstammItem.class));
	}
}
