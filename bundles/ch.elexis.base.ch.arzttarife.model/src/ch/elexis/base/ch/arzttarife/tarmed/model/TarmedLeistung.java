package ch.elexis.base.ch.arzttarife.tarmed.model;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.jpa.model.adapter.mixin.IdentifiableWithXid;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;

public class TarmedLeistung
		extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.TarmedLeistung>
		implements IdentifiableWithXid, ITarmedLeistung {
	
	public static final String STS_CLASS = "ch.elexis.data.TarmedLeistung";
	
	public TarmedLeistung(ch.elexis.core.jpa.entities.TarmedLeistung entity){
		super(entity);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int getMinutes(){
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void setMinutes(int value){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getAL(){
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void setAL(int value){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getTL(){
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void setTL(int value){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getDigniQuali(){
		return getEntity().getDigniQuali();
	}
	
	@Override
	public void setDigniQuali(String value){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getDigniQuanti(){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setDigniQuanti(String value){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getExclusion(){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setExclusion(String value){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public synchronized IBillableOptifier<TarmedLeistung> getOptifier(){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IBillableVerifier getVerifier(){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getCodeSystemName(){
		return ch.elexis.core.jpa.entities.TarmedLeistung.CODESYSTEM_NAME;
	}
	
	@Override
	public String getCode(){
		return getEntity().getCode_();
	}
	
	@Override
	public void setCode(String value){
		getEntity().setCode_(value);
	}
	
	@Override
	public String getText(){
		return getEntity().getTx255();
	}
	
	@Override
	public void setText(String value){
		getEntity().setTx255(value);
	}
	
}
