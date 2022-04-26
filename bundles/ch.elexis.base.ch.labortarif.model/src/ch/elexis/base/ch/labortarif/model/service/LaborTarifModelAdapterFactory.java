package ch.elexis.base.ch.labortarif.model.service;

import ch.elexis.base.ch.labortarif.ILaborLeistung;
import ch.elexis.base.ch.labortarif.model.LaborLeistung;
import ch.elexis.core.jpa.entities.Labor2009Tarif;
import ch.elexis.core.jpa.model.adapter.AbstractModelAdapterFactory;
import ch.elexis.core.jpa.model.adapter.MappingEntry;

public class LaborTarifModelAdapterFactory extends AbstractModelAdapterFactory {

	private static LaborTarifModelAdapterFactory INSTANCE;

	public static synchronized LaborTarifModelAdapterFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new LaborTarifModelAdapterFactory();
		}
		return INSTANCE;
	}

	private LaborTarifModelAdapterFactory() {
		super();
	}

	@Override
	protected void initializeMappings() {
		addMapping(new MappingEntry(ILaborLeistung.class, LaborLeistung.class, Labor2009Tarif.class));
	}

}
