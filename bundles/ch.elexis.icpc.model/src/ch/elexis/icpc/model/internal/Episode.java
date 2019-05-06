package ch.elexis.icpc.model.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.jpa.entities.ICPCEpisode;
import ch.elexis.core.jpa.entities.ICPCEpisodeDiagnosisLink;
import ch.elexis.core.jpa.entities.Kontakt;
import ch.elexis.core.jpa.model.adapter.AbstractIdModelAdapter;
import ch.elexis.core.jpa.model.adapter.mixin.ExtInfoHandler;
import ch.elexis.core.model.IDiagnosis;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.StoreToStringServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.icpc.model.icpc.IcpcEpisode;

public class Episode extends AbstractIdModelAdapter<ICPCEpisode>
		implements Identifiable, IcpcEpisode {
	
	private ExtInfoHandler extInfoHandler;
	
	public Episode(ICPCEpisode entity){
		super(entity);
		extInfoHandler = new ExtInfoHandler(this);
	}
	
	@Override
	public Object getExtInfo(Object key){
		return extInfoHandler.getExtInfo(key);
	}
	
	@Override
	public void setExtInfo(Object key, Object value){
		extInfoHandler.setExtInfo(key, value);
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
	public String getTitle(){
		return getEntity().getTitle();
	}
	
	@Override
	public void setTitle(String value){
		getEntity().setTitle(value);
	}
	
	@Override
	public String getNumber(){
		return getEntity().getNumber();
	}
	
	@Override
	public void setNumber(String value){
		getEntity().setNumber(value);
	}
		
	@Override
	public int getStatus(){
		return getEntity().getStatus();
	}
	
	@Override
	public void setStatus(int value){
		getEntity().setStatus(value);
	}
	
	@Override
	public IPatient getPatient(){
		return CoreModelServiceHolder.get().adapt(getEntity().getPatientKontakt(), IPatient.class)
			.orElse(null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setPatient(IPatient value){
		if (value != null) {
			getEntity().setPatientKontakt(((AbstractIdModelAdapter<Kontakt>) value).getEntity());
		} else {
			getEntity().setPatientKontakt(null);
		}
	}
	
	@Override
	public List<IDiagnosis> getDiagnosis(){
		List<ICPCEpisodeDiagnosisLink> links = getEntity().getLinks();
		List<IDiagnosis> ret = new ArrayList<>();
		for (ICPCEpisodeDiagnosisLink iDiagnosisLink : links) {
			Optional<Identifiable> loaded =
				StoreToStringServiceHolder.get().loadFromString(iDiagnosisLink.getDiagnosis());
			if (loaded.isPresent() && loaded.get() instanceof IDiagnosis) {
				ret.add((IDiagnosis) loaded.get());
			}
		}
		return ret;
	}
	
	@Override
	public void addDiagnosis(IDiagnosis diagnosis){
		Optional<String> string = StoreToStringServiceHolder.get().storeToString(diagnosis);
		string.ifPresent(s -> getEntity().addDiagnosis(s));
	}
	
	@Override
	public void removeDiagnosis(IDiagnosis diagnosis){
		Optional<String> string = StoreToStringServiceHolder.get().storeToString(diagnosis);
		string.ifPresent(s -> {
			List<ICPCEpisodeDiagnosisLink> links = getEntity().getLinks();
			for (ICPCEpisodeDiagnosisLink link : links) {
				if (link.getDiagnosis().equals(string.get())) {
					getEntity().removeDiagnosis(link);
					break;
				}
			}
		});
	}
	
	@Override
	public String getStartDate(){
		return getEntity().getStartDate();
	}
	
	@Override
	public void setStartDate(String value){
		getEntity().setStartDate(value);
	}
	
	@Override
	public String getLabel(){
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotBlank(getNumber())) {
			sb.append(getNumber()).append(": ");
		}
		sb.append(getTitle());
		sb.append(" [" + (getStatus() == 1 ? Messages.Active : Messages.Inactive) + "]");
		
		return sb.toString();
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
