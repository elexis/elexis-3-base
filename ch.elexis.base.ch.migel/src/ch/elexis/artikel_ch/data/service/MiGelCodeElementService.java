package ch.elexis.artikel_ch.data.service;

import java.util.HashMap;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;

import ch.elexis.artikel_ch.data.MiGelArtikel;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.data.Query;

@Component
public class MiGelCodeElementService implements ICodeElementServiceContribution {
	
	@Override
	public String getSystem(){
		return MiGelArtikel.MIGEL_NAME;
	}
	
	@Override
	public Optional<ICodeElement> createFromCode(String code, HashMap<Object, Object> context){
		Query<MiGelArtikel> query = new Query<>(MiGelArtikel.class);
		String found = query.findSingle(MiGelArtikel.FLD_SUB_ID, Query.EQUALS, code);
		if (found != null) {
			return Optional.of((ICodeElement) MiGelArtikel.load(found));
		} else {
			query.clear();
			found = query.findSingle(MiGelArtikel.FLD_EAN, Query.EQUALS, code);
			if (found != null) {
				return Optional.of((ICodeElement) MiGelArtikel.load(found));
			} else {
				query.clear();
				found = query.findSingle(MiGelArtikel.FLD_ID, Query.EQUALS, code);
				if (found != null) {
					return Optional.of((ICodeElement) MiGelArtikel.load(found));
				}
			}
		}
		return Optional.empty();
	}
}
