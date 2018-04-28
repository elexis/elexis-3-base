package ch.elexis.base.ch.diagnosecodes.service;

import java.util.HashMap;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;

import ch.elexis.core.data.interfaces.ICodeElement;
import ch.elexis.core.data.services.ICodeElementServiceContribution;
import ch.elexis.data.ICD10;
import ch.elexis.data.Query;

@Component
public class ICD10CodeElementService implements ICodeElementServiceContribution {
	
	@Override
	public String getSystem(){
		return ICD10.CODESYSTEM_NAME;
	}
	
	@Override
	public Optional<ICodeElement> createFromCode(String code, HashMap<Object, Object> context){
		Query<ICD10> query = new Query<>(ICD10.class);
		String found = query.findSingle(ICD10.FLD_CODE, Query.EQUALS, code);
		if (found != null) {
			return Optional.of((ICodeElement) ICD10.load(found));
		}
		return Optional.empty();
	}
}
