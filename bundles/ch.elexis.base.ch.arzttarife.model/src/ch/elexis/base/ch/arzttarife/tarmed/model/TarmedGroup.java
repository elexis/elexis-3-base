package ch.elexis.base.ch.arzttarife.tarmed.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelAdapterFactory;
import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationArt;
import ch.elexis.core.jpa.entities.TarmedExtension;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.jpa.model.adapter.mixin.IdentifiableWithXid;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.rgw.tools.TimeTool;

public class TarmedGroup
		extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.TarmedGroup>
		implements IdentifiableWithXid, ITarmedGroup {
	
	private LocalDate curTimeHelper = LocalDate.now();
	
	public TarmedGroup(ch.elexis.core.jpa.entities.TarmedGroup entity){
		super(entity);
	}
	
	@Override
	public String getCode(){
		return getEntity().getCode();
	}
	
	@Override
	public List<String> getServices(){
		return getEntity().getServices();
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
	
	/**
	 * Get the exclusions valid now as String, containing the service and chapter codes. Group
	 * exclusions are NOT part of the String.
	 * 
	 * @param encounter
	 * 
	 * @return
	 */
	@Override
	public List<TarmedExclusion> getExclusions(IEncounter encounter){
		if (encounter == null) {
			curTimeHelper = LocalDate.now();
		} else {
			curTimeHelper = encounter.getDate();
		}
		return getExclusions(curTimeHelper);
	}
	
	/**
	 * Get {@link TarmedExclusion} objects with this {@link TarmedLeistung} as master.
	 * 
	 * @param date
	 * @return
	 */
	public List<TarmedExclusion> getExclusions(LocalDate date){
		return TarmedKumulation.getExclusions(getCode(), TarmedKumulationArt.GROUP, date,
			getLaw());
	}
	
	@Override
	public List<TarmedLimitation> getLimitations(){
		TarmedExtension extension = getEntity().getExtension();
		if (extension != null) {
			ITarmedExtension _extension = (ITarmedExtension) ArzttarifeModelAdapterFactory
				.getInstance().getModelAdapter(extension, ITarmedExtension.class, true).get();
			String lim = _extension.getLimits().get("limits");
			if (lim != null && !lim.isEmpty()) {
				List<TarmedLimitation> ret = new ArrayList<>();
				String[] lines = lim.split("#"); //$NON-NLS-1$
				for (String line : lines) {
					ret.add(TarmedLimitation.of(line).setTarmedGroup(this));
				}
				return ret;
			}
		}
		return Collections.emptyList();
	}
	
	@Override
	public boolean validAt(LocalDate validTime){
		TimeTool _validTime = new TimeTool(validTime);
		TimeTool validFrom = new TimeTool(getValidFrom());
		TimeTool validTo = new TimeTool(getValidTo());
		return _validTime.isAfterOrEqual(validFrom) && _validTime.isBeforeOrEqual(validTo);
	}
	
	public static Optional<ITarmedGroup> find(String groupName, String law, LocalDate validFrom){
		IQuery<ITarmedGroup> query =
			ArzttarifeModelServiceHolder.get().getQuery(ITarmedGroup.class);
		
		query.and("GroupName", COMPARATOR.EQUALS, groupName);
		query.and("Law", COMPARATOR.EQUALS, law);
		List<ITarmedGroup> groups = query.execute();
		groups = groups.stream().filter(g -> g.validAt(validFrom)).collect(Collectors.toList());
		if (!groups.isEmpty()) {
			return Optional.of(groups.get(0));
		}
		return Optional.empty();
	}
}
