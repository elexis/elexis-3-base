package ch.elexis.base.ch.arzttarife.tarmed.model;

import java.util.Map;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.jpa.model.adapter.mixin.ExtInfoHandler;
import ch.elexis.core.jpa.model.adapter.mixin.IdentifiableWithXid;

public class TarmedExtension
		extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.TarmedExtension>
		implements IdentifiableWithXid, ITarmedExtension {
	
	private ExtInfoHandler limitsExtInfoHandler;
	
	public TarmedExtension(ch.elexis.core.jpa.entities.TarmedExtension entity){
		super(entity);
		limitsExtInfoHandler = new ExtInfoHandler(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getLimits(){
		return (Map<String, String>) ((Map<?, ?>) limitsExtInfoHandler.getMap());
	}

}
