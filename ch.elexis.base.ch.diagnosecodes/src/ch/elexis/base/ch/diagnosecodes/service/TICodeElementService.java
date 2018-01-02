package ch.elexis.base.ch.diagnosecodes.service;

import java.util.HashMap;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;

import ch.elexis.base.ch.ticode.TessinerCode;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.data.TICode;

@Component
public class TICodeElementService implements ICodeElementServiceContribution {
	
	@Override
	public String getSystem(){
		return TessinerCode.CODESYSTEM_NAME;
	}
	
	@Override
	public Optional<ICodeElement> createFromCode(String code, HashMap<Object, Object> context){
		return Optional.ofNullable((ICodeElement) TICode.getFromCode(code));
	}
}
