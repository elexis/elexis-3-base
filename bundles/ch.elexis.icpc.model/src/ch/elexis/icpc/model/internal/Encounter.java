package ch.elexis.icpc.model.internal;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.jpa.entities.Behandlung;
import ch.elexis.core.jpa.entities.ICPCEncounter;
import ch.elexis.core.jpa.entities.ICPCEpisode;
import ch.elexis.core.jpa.model.adapter.AbstractIdModelAdapter;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.icpc.model.icpc.IcpcCode;
import ch.elexis.icpc.model.icpc.IcpcEncounter;
import ch.elexis.icpc.model.icpc.IcpcEpisode;
import ch.elexis.icpc.model.internal.service.IcpcModelServiceHolder;

public class Encounter extends AbstractIdModelAdapter<ICPCEncounter>
		implements Identifiable, IcpcEncounter {
	
	public Encounter(ICPCEncounter entity){
		super(entity);
	}
	
	@Override
	public boolean isDeleted(){
		return getEntity().isDeleted();
	}
	
	@Override
	public void setDeleted(boolean value){
		getEntityMarkDirty().setDeleted(value);
	}
	
	@Override
	public IEncounter getEncounter(){
		return CoreModelServiceHolder.get().adapt(getEntity().getKons(), IEncounter.class)
			.orElse(null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setEncounter(IEncounter value){
		if (value != null) {
			getEntityMarkDirty().setKons(((AbstractIdModelAdapter<Behandlung>) value).getEntity());
		} else {
			getEntityMarkDirty().setKons(null);
		}
	}
	
	@Override
	public IcpcEpisode getEpisode(){
		return IcpcModelServiceHolder.get().adapt(getEntity().getEpisode(), IcpcEpisode.class)
			.orElse(null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setEpisode(IcpcEpisode value){
		if (value != null) {
			getEntityMarkDirty()
				.setEpisode(((AbstractIdModelAdapter<ICPCEpisode>) value).getEntity());
		} else {
			getEntityMarkDirty().setEpisode(null);
		}
	}
	
	@Override
	public IcpcCode getProc(){
		String codeId = getEntity().getProc();
		if(!StringUtils.isBlank(codeId)) {
			return IcpcModelServiceHolder.get().load(codeId, IcpcCode.class).orElse(null);
		}
		return null;
	}
	
	@Override
	public void setProc(IcpcCode value){
		if (value != null) {
			getEntityMarkDirty().setProc(value.getId());
		} else {
			getEntityMarkDirty().setProc(null);
		}
	}
	
	@Override
	public IcpcCode getDiag(){
		String codeId = getEntity().getDiag();
		if (!StringUtils.isBlank(codeId)) {
			return IcpcModelServiceHolder.get().load(codeId, IcpcCode.class).orElse(null);
		}
		return null;
	}
	
	@Override
	public void setDiag(IcpcCode value){
		if (value != null) {
			getEntityMarkDirty().setDiag(value.getId());
		} else {
			getEntityMarkDirty().setDiag(null);
		}
	}
	
	@Override
	public IcpcCode getRfe(){
		String codeId = getEntity().getRfe();
		if (!StringUtils.isBlank(codeId)) {
			return IcpcModelServiceHolder.get().load(codeId, IcpcCode.class).orElse(null);
		}
		return null;
	}
	
	@Override
	public void setRfe(IcpcCode value){
		if (value != null) {
			getEntityMarkDirty().setRfe(value.getId());
		} else {
			getEntityMarkDirty().setRfe(null);
		}
	}
	
	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists){
		return XidServiceHolder.get().addXid(this, domain, id, updateIfExists);
	}
	
	@Override
	public IXid getXid(String domain){
		return XidServiceHolder.get().getXid(this, domain);
	}
}
