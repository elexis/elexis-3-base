package ch.elexis.base.ch.ebanking.model.service;

import ch.elexis.base.ch.ebanking.model.IEsrRecord;
import ch.elexis.core.jpa.entities.EsrRecord;
import ch.elexis.core.jpa.model.adapter.AbstractModelAdapterFactory;
import ch.elexis.core.jpa.model.adapter.MappingEntry;

public class EsrModelAdapterFactory extends AbstractModelAdapterFactory {

	private static EsrModelAdapterFactory INSTANCE;

	public static synchronized EsrModelAdapterFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new EsrModelAdapterFactory();
		}
		return INSTANCE;
	}

	private EsrModelAdapterFactory() {
		super();
	}

	@Override
	protected void initializeMappings() {
		addMapping(
				new MappingEntry(IEsrRecord.class, ch.elexis.base.ch.ebanking.model.EsrRecord.class, EsrRecord.class));
	}
}
