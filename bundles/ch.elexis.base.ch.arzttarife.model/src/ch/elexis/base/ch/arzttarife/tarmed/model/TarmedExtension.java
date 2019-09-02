package ch.elexis.base.ch.arzttarife.tarmed.model;

import java.util.Map;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.jpa.model.adapter.mixin.ExtInfoHandler;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.WithExtInfo;
import ch.elexis.core.services.holder.XidServiceHolder;

public class TarmedExtension
		extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.TarmedExtension>
		implements Identifiable, ITarmedExtension, WithExtInfo {
	
	private ExtInfoHandler limitsExtInfoHandler;
	
	public TarmedExtension(ch.elexis.core.jpa.entities.TarmedExtension entity){
		super(entity);
		limitsExtInfoHandler = new ExtInfoHandler(this);
	}
	
	/**
	 * modifications to this map are not persisted
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getLimits(){
		return (Map<String, String>) ((Map<?, ?>) limitsExtInfoHandler.getMap());
	}
	
	/**
	 * Not available in {@link ITarmedExtension}, as should be used for tests only
	 * 
	 * @param limits
	 */
	public void setLimits(Map<String, String> limits){
		limits.forEach((key, value) -> {
			limitsExtInfoHandler.setExtInfo(key, value);
		});
	}
	
	@Override
	public Object getExtInfo(Object key){
		return limitsExtInfoHandler.getExtInfo(key);
	}
	
	@Override
	public void setExtInfo(Object key, Object value){
		limitsExtInfoHandler.setExtInfo(key, value);
	}
	
	@Override
	public Map<Object, Object> getMap(){
		return limitsExtInfoHandler.getMap();
	}
	
	@Override
	public String getMedInterpretation(){
		return getEntity().getMed_interpret();
	}
	
	@Override
	public void setMedInterpretation(String value){
		getEntity().setMed_interpret(value);
	}
	
	@Override
	public String getTechInterpretation(){
		return getEntity().getTech_interpret();
	}
	
	@Override
	public void setTechInterpretation(String value){
		getEntity().setTech_interpret(value);
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
