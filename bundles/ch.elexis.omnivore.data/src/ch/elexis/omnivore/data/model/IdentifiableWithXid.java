package ch.elexis.omnivore.data.model;

import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.omnivore.data.model.util.ModelUtil;

public interface IdentifiableWithXid extends Identifiable {
	
	public default boolean addXid(String domain, String id, boolean updateIfExists){
		return ModelUtil.addXid(this, domain, id, updateIfExists);
	}
	
	public default IXid getXid(String domain){
		return ModelUtil.getXid(this, domain).orElse(null);
	}
}
