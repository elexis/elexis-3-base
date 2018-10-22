package ch.elexis.base.ch.arzttarife.tarmed.model;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.jpa.model.adapter.mixin.IdentifiableWithXid;

public class TarmedExtension
		extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.TarmedExtension>
		implements IdentifiableWithXid, ITarmedExtension {
	
	public TarmedExtension(ch.elexis.core.jpa.entities.TarmedExtension entity){
		super(entity);
	}
	
}
