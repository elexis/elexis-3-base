package ch.elexis.icpc.model.internal;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.jpa.entities.Behandlung;
import ch.elexis.core.jpa.entities.ICPCEncounter;
import ch.elexis.core.jpa.entities.ICPCEpisode;
import ch.elexis.core.jpa.model.adapter.AbstractIdModelAdapter;
import ch.elexis.core.jpa.model.adapter.mixin.IdentifiableWithXid;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.icpc.model.icpc.IcpcCode;
import ch.elexis.icpc.model.icpc.IcpcEncounter;
import ch.elexis.icpc.model.icpc.IcpcEpisode;
import ch.elexis.icpc.model.internal.service.IcpcModelServiceHolder;

public class Encounter extends AbstractIdModelAdapter<ICPCEncounter>
		implements IdentifiableWithXid, IcpcEncounter {
	
	public Encounter(ICPCEncounter entity){
		super(entity);
	}
	
	@Override
	public boolean isDeleted(){
		return getEntity().isDeleted();
	}
	
	@Override
	public void setDeleted(boolean value){
		getEntity().setDeleted(value);
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
			getEntity().setKons(((AbstractIdModelAdapter<Behandlung>) value).getEntity());
		} else {
			getEntity().setKons(null);
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
			getEntity().setEpisode(((AbstractIdModelAdapter<ICPCEpisode>) value).getEntity());
		} else {
			getEntity().setEpisode(null);
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
			getEntity().setProc(value.getId());
		} else {
			getEntity().setProc(null);
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
			getEntity().setDiag(value.getId());
		} else {
			getEntity().setDiag(null);
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
			getEntity().setRfe(value.getId());
		} else {
			getEntity().setRfe(null);
		}
	}
}
