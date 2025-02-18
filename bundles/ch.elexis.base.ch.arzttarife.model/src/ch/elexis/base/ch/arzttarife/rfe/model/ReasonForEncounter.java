package ch.elexis.base.ch.arzttarife.rfe.model;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter;
import ch.elexis.base.ch.arzttarife.rfe.ReasonsForEncounter;
import ch.elexis.core.jpa.entities.RFE;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.holder.XidServiceHolder;

public class ReasonForEncounter extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.RFE>
		implements Identifiable, IReasonForEncounter {

	public ReasonForEncounter(RFE entity) {
		super(entity);
	}

	@Override
	public IEncounter getEncounter() {
		if (getEntity().getKonsID() != null) {
			Optional<IEncounter> ret = CoreModelServiceHolder.get().load(getEntity().getKonsID(), IEncounter.class);
			if (ret.isPresent()) {
				return ret.get();
			}
		}
		return null;
	}

	@Override
	public void setEncounter(IEncounter value) {
		if (value != null) {
			getEntity().setKonsID(value.getId());
		} else {
			getEntity().setKonsID(null);
		}
	}

	@Override
	public String getCode() {
		return getEntity().getType() != null ? getEntity().getType() : StringUtils.EMPTY;
	}

	@Override
	public void setCode(String value) {
		getEntity().setType(value);
	}

	@Override
	public String getText() {
		String code = getCode();
		return ReasonsForEncounter.getCodeToReasonMap().get(code);
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
