package ch.elexis.base.ch.arzttarife.tarmed.model;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.jpa.model.adapter.mixin.IdentifiableWithXid;

public class TarmedGroup extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.TarmedGroup>
implements IdentifiableWithXid, ITarmedGroup {

	public TarmedGroup(ch.elexis.core.jpa.entities.TarmedGroup entity){
		super(entity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getCode(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCode(String value){
		// TODO Auto-generated method stub
		
	}
	
}
