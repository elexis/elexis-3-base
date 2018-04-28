package ch.elexis.medikamente.bag.data.service;

import java.util.HashMap;
import java.util.Optional;

import ch.elexis.core.data.interfaces.ICodeElement;
import ch.elexis.core.data.services.ICodeElementServiceContribution;
import ch.elexis.data.Query;
import ch.elexis.medikamente.bag.data.BAGMedi;

public class BAGMediCodeElementService implements ICodeElementServiceContribution {
	
	@Override
	public String getSystem(){
		return BAGMedi.CODESYSTEMNAME;
	}
	
	@Override
	public Optional<ICodeElement> createFromCode(String code, HashMap<Object, Object> context){
		Query<BAGMedi> query = new Query<>(BAGMedi.class);
		String found = query.findSingle(BAGMedi.FLD_EAN, Query.EQUALS, code);
		if (found != null) {
			return Optional.of((ICodeElement) BAGMedi.load(found));
		} else {
			query.clear();
			found = query.findSingle(BAGMedi.FLD_PHARMACODE, Query.EQUALS, code);
			if (found != null) {
				return Optional.of((ICodeElement) BAGMedi.load(found));
			}
		}
		return Optional.empty();
	}
}
