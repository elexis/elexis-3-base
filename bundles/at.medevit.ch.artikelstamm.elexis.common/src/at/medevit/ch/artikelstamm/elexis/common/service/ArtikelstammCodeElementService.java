package at.medevit.ch.artikelstamm.elexis.common.service;

import java.util.HashMap;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;

import at.medevit.ch.artikelstamm.ArtikelstammConstants;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.data.interfaces.ICodeElement;
import ch.elexis.core.data.services.ICodeElementServiceContribution;

@Component
public class ArtikelstammCodeElementService implements ICodeElementServiceContribution {
	
	@Override
	public String getSystem(){
		return ArtikelstammConstants.CODESYSTEM_NAME;
	}
	
	@Override
	public Optional<ICodeElement> createFromCode(String code, HashMap<Object, Object> context){
		ArtikelstammItem found = ArtikelstammItem.findByEANorGTIN(code);
		if (found == null) {
			found = ArtikelstammItem.findByPharmaCode(code);
		}
		return Optional.ofNullable(found);
	}
}
