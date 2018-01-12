package ch.elexis.base.ch.arzttarife.service;

import java.util.HashMap;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;

import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.data.PhysioLeistung;

@Component
public class PhysioCodeElementService implements ICodeElementServiceContribution {
	
	@Override
	public String getSystem(){
		return PhysioLeistung.CODESYSTEMNAME;
	}
	
	@Override
	public Optional<ICodeElement> createFromCode(String code, HashMap<Object, Object> context){
		return Optional.ofNullable(PhysioLeistung.getFromCode(code));
	}
}
