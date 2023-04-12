package ch.elexis.base.ch.ebanking.model;

import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IXid;

public class EsrRecord extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.EsrRecord>
		implements IEsrRecord {

	public EsrRecord(ch.elexis.core.jpa.entities.EsrRecord entity) {
		super(entity);

	}

	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IXid getXid(String domain) {
		// TODO Auto-generated method stub
		return null;
	}

}
