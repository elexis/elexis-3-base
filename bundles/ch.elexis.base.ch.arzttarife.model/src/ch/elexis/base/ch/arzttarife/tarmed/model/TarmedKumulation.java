package ch.elexis.base.ch.arzttarife.tarmed.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.jpa.model.adapter.mixin.IdentifiableWithXid;
import ch.elexis.core.services.INamedQuery;
import ch.rgw.tools.TimeTool;

public class TarmedKumulation
		extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.TarmedKumulation>
		implements IdentifiableWithXid, ITarmedKumulation {
	
	public TarmedKumulation(ch.elexis.core.jpa.entities.TarmedKumulation entity){
		super(entity);
	}
	
	@Override
	public String getSlaveCode(){
		return getEntity().getSlaveCode();
	}
	
	@Override
	public String getSlaveArt(){
		return getEntity().getSlaveArt();
	}
	
	@Override
	public String getValidSide(){
		return getEntity().getValidSide();
	}
	
	@Override
	public LocalDate getValidFrom(){
		return getEntity().getValidFrom();
	}
	
	@Override
	public LocalDate getValidTo(){
		return getEntity().getValidTo();
	}
	
	@Override
	public String getLaw(){
		return getEntity().getLaw();
	}
	
	@Override
	public boolean isValidKumulation(LocalDate reference){
		TimeTool date = new TimeTool(reference);
		TimeTool from = new TimeTool(getValidFrom());
		TimeTool to = new TimeTool(getValidTo());
		return (date.isAfterOrEqual(from) && date.isBeforeOrEqual(to));
	}
	
	/**
	 * Get the exclusions as String, containing the service and chapter codes. Group exclusions are
	 * NOT part of the String.
	 * 
	 * @param code
	 * @param date
	 * @return
	 */
	public static String getExclusions(String code, LocalDate date){
		INamedQuery<ITarmedKumulation> query = ArzttarifeModelServiceHolder.get()
			.getNamedQuery(ITarmedKumulation.class, "masterCode", "typ");
		List<ITarmedKumulation> exclusions =
			query.executeWithParameters(query.getParameterMap("masterCode", code, "typ",
				ch.elexis.core.jpa.entities.TarmedKumulation.TYP_EXCLUSION));
		if (exclusions == null || exclusions.isEmpty()) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		for (ITarmedKumulation excl : exclusions) {
			if ("G".equals(excl.getSlaveArt())) {
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
	 * Get {@link TarmedExclusion} objects for all exclusions defined as {@link TarmedKumulation},
	 * with code as master code and master type.
	 * 
	 * @param mastercode
	 * @param masterType
	 * @param date
	 * @param law
	 * @return
	 */
	public static List<TarmedExclusion> getExclusions(String mastercode,
		TarmedKumulationType masterType, LocalDate date, String law){
		INamedQuery<ITarmedKumulation> query = ArzttarifeModelServiceHolder.get()
			.getNamedQuery(ITarmedKumulation.class, "masterCode", "masterArt", "typ");
		List<ITarmedKumulation> exclusions = query.executeWithParameters(
			query.getParameterMap("masterCode", mastercode, "masterArt", masterType.getArt(), "typ",
				ch.elexis.core.jpa.entities.TarmedKumulation.TYP_EXCLUSION));
		
		if (law != null && !law.isEmpty()) {
			exclusions = exclusions.stream().filter(e -> law.equals(e.getLaw()))
				.collect(Collectors.toList());
		}
		
		if (exclusions == null || exclusions.isEmpty()) {
			return Collections.emptyList();
		}
		exclusions =
			exclusions.stream().filter(k -> k.isValidKumulation(date)).collect(Collectors.toList());
		return exclusions.stream().map(k -> new TarmedExclusion(k)).collect(Collectors.toList());
	}
	
	/**
	 * Get {@link TarmedExclusion} objects for all exclusions defined as {@link TarmedKumulation},
	 * with code as master code and master type.
	 * 
	 * @param mastercode
	 * @param masterType
	 * @param date
	 * @param law
	 * @return
	 */
	public static List<TarmedExclusive> getExclusives(String mastercode,
		TarmedKumulationType masterType, LocalDate date, String law){
		
		INamedQuery<ITarmedKumulation> query = ArzttarifeModelServiceHolder.get()
			.getNamedQuery(ITarmedKumulation.class, "masterCode", "masterArt", "typ");
		List<ITarmedKumulation> exclusives = query.executeWithParameters(
			query.getParameterMap("masterCode", mastercode, "masterArt", masterType.getArt(), "typ",
				ch.elexis.core.jpa.entities.TarmedKumulation.TYP_EXCLUSIVE));
		
		if (law != null && !law.isEmpty()) {
			exclusives = exclusives.stream().filter(e -> law.equals(e.getLaw()))
				.collect(Collectors.toList());
		}
		
		if (exclusives == null || exclusives.isEmpty()) {
			return Collections.emptyList();
		}
		
		exclusives =
			exclusives.stream().filter(k -> k.isValidKumulation(date)).collect(Collectors.toList());
		return exclusives.stream().map(k -> new TarmedExclusive(k)).collect(Collectors.toList());
	}
}
