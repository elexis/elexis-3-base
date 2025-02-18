package ch.elexis.base.ch.arzttarife.tarmed.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationArt;
import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.INamedQuery;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.rgw.tools.TimeTool;

public class TarmedGroup extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.TarmedGroup>
		implements Identifiable, ITarmedGroup {

	private LocalDate curTimeHelper = LocalDate.now();

	private ITarmedExtension extension;

	public TarmedGroup(ch.elexis.core.jpa.entities.TarmedGroup entity) {
		super(entity);
	}

	@Override
	public String getCode() {
		return getEntity().getCode();
	}

	@Override
	public List<String> getServices() {
		return getEntity().getServices();
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

	/**
	 * Get the exclusions valid now as String, containing the service and chapter
	 * codes. Group exclusions are NOT part of the String.
	 *
	 * @param encounter
	 *
	 * @return
	 */
	@Override
	public List<TarmedExclusion> getExclusions(IEncounter encounter) {
		if (encounter == null) {
			curTimeHelper = LocalDate.now();
		} else {
			curTimeHelper = encounter.getDate();
		}
		return getExclusions(curTimeHelper);
	}

	/**
	 * Get {@link TarmedExclusion} objects with this {@link TarmedLeistung} as
	 * master.
	 *
	 * @param date
	 * @return
	 */
	public List<TarmedExclusion> getExclusions(LocalDate date) {
		return TarmedKumulation.getExclusions(getCode(), TarmedKumulationArt.GROUP, date, getLaw());
	}

	@Override
	public ITarmedExtension getExtension() {
		if (extension == null) {
			INamedQuery<ITarmedExtension> query = ArzttarifeModelServiceHolder.get()
					.getNamedQuery(ITarmedExtension.class, "code");
			List<ITarmedExtension> found = query.executeWithParameters(query.getParameterMap("code", getId()));
			if (!found.isEmpty()) {
				extension = found.get(0);
			}
		}
		return extension;
	}

	@Override
	public List<TarmedLimitation> getLimitations() {
		ITarmedExtension _extension = getExtension();
		if (extension != null) {
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
	public boolean validAt(LocalDate validTime) {
		TimeTool _validTime = new TimeTool(validTime);
		TimeTool validFrom = new TimeTool(getValidFrom());
		TimeTool validTo = new TimeTool(getValidTo());
		return _validTime.isAfterOrEqual(validFrom) && _validTime.isBeforeOrEqual(validTo);
	}

	public static Optional<ITarmedGroup> find(String groupName, String law, LocalDate validFrom) {
		IQuery<ITarmedGroup> query = ArzttarifeModelServiceHolder.get().getQuery(ITarmedGroup.class);

		query.and("groupName", COMPARATOR.EQUALS, groupName);
		if (law != null) {
			if (!ArzttarifeUtil.isAvailableLaw(law)) {
				query.startGroup();
				query.or("law", COMPARATOR.EQUALS, StringUtils.EMPTY);
				query.or("law", COMPARATOR.EQUALS, null);
				query.andJoinGroups();
			} else {
				query.and("law", COMPARATOR.EQUALS, law, true);
			}
		}
		List<ITarmedGroup> groups = query.execute();
		groups = groups.stream().filter(g -> g.validAt(validFrom)).collect(Collectors.toList());
		if (!groups.isEmpty()) {
			return Optional.of(groups.get(0));
		}
		return Optional.empty();
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
