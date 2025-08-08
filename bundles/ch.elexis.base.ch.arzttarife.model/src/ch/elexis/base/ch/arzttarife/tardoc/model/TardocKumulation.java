package ch.elexis.base.ch.arzttarife.tardoc.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocKumulation;
import ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationArt;
import ch.elexis.base.ch.arzttarife.tardoc.TardocKumulationTyp;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedExclusion;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.INamedQuery;
import ch.elexis.core.services.holder.XidServiceHolder;

public class TardocKumulation extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.TardocKumulation>
		implements Identifiable, ITardocKumulation {

	public TardocKumulation(ch.elexis.core.jpa.entities.TardocKumulation entity) {
		super(entity);
	}

	@Override
	public String getSlaveCode() {
		return getEntity().getSlaveCode();
	}

	@Override
	public TardocKumulationArt getSlaveArt() {
		return TardocKumulationArt.ofArt(getEntity().getSlaveArt());
	}

	@Override
	public String getValidSide() {
		return getEntity().getValidSide();
	}

	@Override
	public LocalDate getValidFrom() {
		return getEntity().getValidFrom();
	}

	@Override
	public LocalDate getValidTo() {
		return getEntity().getValidTo();
	}

	@Override
	public String getLaw() {
		return getEntity().getLaw();
	}

	@Override
	public boolean isValidKumulation(LocalDate date) {
		return (date.isAfter(getValidFrom()) || date.isEqual(getValidFrom()))
				&& (date.isBefore(getValidTo()) || date.isEqual(getValidTo()));
	}

	/**
	 * Get the exclusions as String, containing the service and chapter codes. Group
	 * exclusions are NOT part of the String.
	 *
	 * @param code
	 * @param date
	 * @return
	 */
	public static String getExclusions(String code, LocalDate date) {
		INamedQuery<ITardocKumulation> query = ArzttarifeModelServiceHolder.get().getNamedQuery(ITardocKumulation.class,
				"masterCode", "typ");
		List<ITardocKumulation> exclusions = query.executeWithParameters(query.getParameterMap("masterCode", code,
				"typ", ch.elexis.core.jpa.entities.TardocKumulation.TYP_EXCLUSION));
		if (exclusions == null || exclusions.isEmpty()) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (ITardocKumulation excl : exclusions) {
			if (excl.getSlaveArt() == TardocKumulationArt.GROUP) {
				continue;
			}
			if (excl.isValidKumulation(date)) {
				if (!sb.toString().isEmpty()) {
					sb.append(",");
				}
				sb.append(excl.getSlaveCode());
			}
		}
		return sb.toString();
	}

	/**
	 * Get {@link TarmedExclusion} objects for all exclusions defined as
	 * {@link TardocKumulation}, with code as master code and master type.
	 *
	 * @param mastercode
	 * @param masterType
	 * @param date
	 * @param law
	 * @return
	 */
	public static List<TardocExclusion> getExclusions(String mastercode, TardocKumulationArt masterType, LocalDate date,
			String law) {
		INamedQuery<ITardocKumulation> query = ArzttarifeModelServiceHolder.get().getNamedQuery(ITardocKumulation.class,
				"masterCode", "masterArt", "typ");
		List<ITardocKumulation> exclusions = query.executeWithParameters(query.getParameterMap("masterCode", mastercode,
				"masterArt", masterType.getArt(), "typ", ch.elexis.core.jpa.entities.TardocKumulation.TYP_EXCLUSION));

		if (law != null && !law.isEmpty()) {
			exclusions = new ArrayList<>(
					exclusions.stream().filter(e -> law.equals(e.getLaw())).collect(Collectors.toList()));
		}

//		if (masterType == TardocKumulationArt.SERVICE) {
//			exclusions.addAll(CustomExclusions.of(mastercode));
//		}

		if (exclusions == null || exclusions.isEmpty()) {
			return Collections.emptyList();
		}
		exclusions = exclusions.stream().filter(k -> k.isValidKumulation(date)).collect(Collectors.toList());
		return exclusions.stream().map(k -> new TardocExclusion(k)).collect(Collectors.toList());
	}

	/**
	 * Get {@link TarmedExclusion} objects for all exclusions defined as
	 * {@link TardocKumulation}, with code as master code and master type.
	 *
	 * @param mastercode
	 * @param masterType
	 * @param date
	 * @param law
	 * @return
	 */
	public static List<TardocExclusive> getExclusives(String mastercode, TardocKumulationArt masterType, LocalDate date,
			String law) {

		INamedQuery<ITardocKumulation> query = ArzttarifeModelServiceHolder.get().getNamedQuery(ITardocKumulation.class,
				"masterCode", "masterArt", "typ");
		List<ITardocKumulation> exclusives = query.executeWithParameters(query.getParameterMap("masterCode", mastercode,
				"masterArt", masterType.getArt(), "typ", ch.elexis.core.jpa.entities.TardocKumulation.TYP_EXCLUSIVE));

		if (law != null && !law.isEmpty()) {
			exclusives = exclusives.stream().filter(e -> law.equals(e.getLaw())).collect(Collectors.toList());
		}

		if (exclusives == null || exclusives.isEmpty()) {
			return Collections.emptyList();
		}

		exclusives = exclusives.stream().filter(k -> k.isValidKumulation(date)).collect(Collectors.toList());
		return exclusives.stream().map(k -> new TardocExclusive(k)).collect(Collectors.toList());
	}

	@Override
	public String getMasterCode() {
		return getEntity().getMasterCode();
	}

	@Override
	public TardocKumulationArt getMasterArt() {
		return TardocKumulationArt.ofArt(getEntity().getMasterArt());
	}

	@Override
	public TardocKumulationTyp getTyp() {
		return TardocKumulationTyp.ofTyp(getEntity().getTyp());
	}

	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists) {
		return XidServiceHolder.get().addXid(this, domain, id, updateIfExists);
	}

	@Override
	public IXid getXid(String domain) {
		return XidServiceHolder.get().getXid(this, domain);
	}
}
