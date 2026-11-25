package ch.elexis.base.ch.arzttarife.model.service;

import ch.elexis.core.jpa.model.adapter.AbstractModelAdapterFactory;

public class ArzttarifeModelAdapterFactory extends AbstractModelAdapterFactory {

	private static ArzttarifeModelAdapterFactory INSTANCE;

	public static synchronized ArzttarifeModelAdapterFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ArzttarifeModelAdapterFactory();
		}
		return INSTANCE;
	}

	private ArzttarifeModelAdapterFactory() {
		super();
	}

	@Override
	protected void initializeMappings() {
		ArzttarifeModelMappingEntries.getMappings().forEach(this::addMapping);
	}
}
