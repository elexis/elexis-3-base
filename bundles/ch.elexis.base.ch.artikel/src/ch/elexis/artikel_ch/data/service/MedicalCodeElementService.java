package ch.elexis.artikel_ch.data.service;

import java.util.HashMap;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;

import ch.elexis.artikel_ch.data.Medical;
import ch.elexis.core.data.interfaces.ICodeElement;
import ch.elexis.core.data.services.ICodeElementServiceContribution;
import ch.elexis.data.Query;

@Component
public class MedicalCodeElementService implements ICodeElementServiceContribution {
	
	@Override
	public String getSystem(){
		return Medical.CODESYSTEM_NAME;
	}
	
	@Override
	public Optional<ICodeElement> createFromCode(String code, HashMap<Object, Object> context){
		Query<Medical> query = new Query<>(Medical.class);
		String found = query.findSingle(Medical.FLD_SUB_ID, Query.EQUALS, code);
		if (found != null) {
			return Optional.of((ICodeElement) Medical.load(found));
		} else {
			query.clear();
			found = query.findSingle(Medical.FLD_EAN, Query.EQUALS, code);
			if (found != null) {
				return Optional.of((ICodeElement) Medical.load(found));
			} else {
				query.clear();
				found = query.findSingle(Medical.FLD_ID, Query.EQUALS, code);
				if (found != null) {
					return Optional.of((ICodeElement) Medical.load(found));
				}
			}
		}
		return Optional.empty();
	}
}
