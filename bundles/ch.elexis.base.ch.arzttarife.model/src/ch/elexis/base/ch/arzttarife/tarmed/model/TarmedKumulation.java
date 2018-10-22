package ch.elexis.base.ch.arzttarife.tarmed.model;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.jpa.model.adapter.mixin.IdentifiableWithXid;

public class TarmedKumulation
		extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.TarmedKumulation>
		implements IdentifiableWithXid, ITarmedKumulation {

	public TarmedKumulation(ch.elexis.core.jpa.entities.TarmedKumulation entity){
		super(entity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getSlaveCode(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSlaveCode(String value){
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSlaveArt(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSlaveArt(String value){
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getValidSide(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValidSide(String value){
		// TODO Auto-generated method stub
		
	}
	
}
