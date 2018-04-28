package ch.elexis.artikel_ch.data.service;

import java.util.HashMap;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;

import ch.elexis.artikel_ch.data.Medikament;
import ch.elexis.core.data.interfaces.ICodeElement;
import ch.elexis.core.data.services.ICodeElementServiceContribution;
import ch.elexis.data.Artikel;
import ch.elexis.data.Query;

@Component
public class MedikamentCodeElementService implements ICodeElementServiceContribution {
	
	@Override
	public String getSystem(){
		return Medikament.CODESYSTEM_NAME;
	}
	
	@Override
	public Optional<ICodeElement> createFromCode(String code, HashMap<Object, Object> context){
		Query<Medikament> query = new Query<>(Medikament.class);
		String found = query.findSingle(Artikel.FLD_SUB_ID, Query.EQUALS, code);
		if (found != null) {
			return Optional.of((ICodeElement) Medikament.load(found));
		} else {
			query.clear();
			found = query.findSingle(Artikel.FLD_EAN, Query.EQUALS, code);
			if (found != null) {
				return Optional.of((ICodeElement) Medikament.load(found));
			}
		}
		return Optional.empty();
	}
}
