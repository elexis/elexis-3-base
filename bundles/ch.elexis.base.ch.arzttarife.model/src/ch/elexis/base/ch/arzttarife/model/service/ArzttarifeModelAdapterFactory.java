package ch.elexis.base.ch.arzttarife.model.service;

import ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung;
import ch.elexis.base.ch.arzttarife.nutrition.INutritionLeistung;
import ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung;
import ch.elexis.base.ch.arzttarife.pandemie.IPandemieLeistung;
import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung;
import ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocExtension;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocGroup;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance;
import ch.elexis.core.jpa.entities.ComplementaryLeistung;
import ch.elexis.core.jpa.entities.NutritionLeistung;
import ch.elexis.core.jpa.entities.OccupationalLeistung;
import ch.elexis.core.jpa.entities.PandemieLeistung;
import ch.elexis.core.jpa.entities.PhysioLeistung;
import ch.elexis.core.jpa.entities.PsychoLeistung;
import ch.elexis.core.jpa.entities.RFE;
import ch.elexis.core.jpa.entities.TardocExtension;
import ch.elexis.core.jpa.entities.TardocGroup;
import ch.elexis.core.jpa.entities.TardocKumulation;
import ch.elexis.core.jpa.entities.TardocLeistung;
import ch.elexis.core.jpa.entities.TarmedExtension;
import ch.elexis.core.jpa.entities.TarmedGroup;
import ch.elexis.core.jpa.entities.TarmedKumulation;
import ch.elexis.core.jpa.entities.TarmedLeistung;
import ch.elexis.core.jpa.entities.TarmedPauschalen;
import ch.elexis.core.jpa.model.adapter.AbstractModelAdapterFactory;
import ch.elexis.core.jpa.model.adapter.MappingEntry;

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
		addMapping(new MappingEntry(IPhysioLeistung.class,
				ch.elexis.base.ch.arzttarife.physio.model.PhysioLeistung.class, PhysioLeistung.class));
		addMapping(new MappingEntry(IComplementaryLeistung.class,
				ch.elexis.base.ch.arzttarife.complementary.model.ComplementaryLeistung.class,
				ComplementaryLeistung.class));
		addMapping(new MappingEntry(IPandemieLeistung.class,
				ch.elexis.base.ch.arzttarife.pandemie.model.PandemieLeistung.class, PandemieLeistung.class));
		addMapping(new MappingEntry(ITarmedAllowance.class,
				ch.elexis.base.ch.arzttarife.tarmedallowance.model.TarmedAllowance.class, TarmedPauschalen.class));
		addMapping(new MappingEntry(INutritionLeistung.class,
				ch.elexis.base.ch.arzttarife.nutrition.model.NutritionLeistung.class, NutritionLeistung.class));
		addMapping(new MappingEntry(IPsychoLeistung.class,
				ch.elexis.base.ch.arzttarife.psycho.model.PsychoLeistung.class, PsychoLeistung.class));
		addMapping(new MappingEntry(IOccupationalLeistung.class,
				ch.elexis.base.ch.arzttarife.occupational.model.OccupationalLeistung.class,
				OccupationalLeistung.class));

		addMapping(new MappingEntry(ITarmedLeistung.class,
				ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung.class, TarmedLeistung.class));
		addMapping(new MappingEntry(ITarmedExtension.class,
				ch.elexis.base.ch.arzttarife.tarmed.model.TarmedExtension.class, TarmedExtension.class));
		addMapping(new MappingEntry(ITarmedKumulation.class,
				ch.elexis.base.ch.arzttarife.tarmed.model.TarmedKumulation.class, TarmedKumulation.class));
		addMapping(new MappingEntry(ITarmedGroup.class, ch.elexis.base.ch.arzttarife.tarmed.model.TarmedGroup.class,
				TarmedGroup.class));

		addMapping(new MappingEntry(ITardocLeistung.class,
				ch.elexis.base.ch.arzttarife.tardoc.model.TardocLeistung.class, TardocLeistung.class));
		addMapping(new MappingEntry(ITardocExtension.class,
				ch.elexis.base.ch.arzttarife.tardoc.model.TardocExtension.class, TardocExtension.class));
		addMapping(new MappingEntry(ITardocKumulation.class,
				ch.elexis.base.ch.arzttarife.tardoc.model.TardocKumulation.class, TardocKumulation.class));
		addMapping(new MappingEntry(ITardocGroup.class, ch.elexis.base.ch.arzttarife.tardoc.model.TardocGroup.class,
				TardocGroup.class));

		addMapping(new MappingEntry(IReasonForEncounter.class,
				ch.elexis.base.ch.arzttarife.rfe.model.ReasonForEncounter.class, RFE.class));
	}
}
