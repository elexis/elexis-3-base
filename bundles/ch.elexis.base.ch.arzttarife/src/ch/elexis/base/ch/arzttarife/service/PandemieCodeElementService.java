package ch.elexis.base.ch.arzttarife.service;

import java.util.HashMap;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;

import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.services.ICodeElementService.ContextKeys;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.data.Konsultation;
import ch.elexis.data.PandemieLeistung;
import ch.rgw.tools.TimeTool;

@Component
public class PandemieCodeElementService implements ICodeElementServiceContribution {
	
	@Override
	public String getSystem(){
		return PandemieLeistung.CODESYSTEMNAME;
	}
	
	@Override
	public Optional<ICodeElement> createFromCode(String code, HashMap<Object, Object> context){
		return Optional
			.ofNullable(PandemieLeistung.getFromCode(code, getDate(context)));
	}
	
	private TimeTool getDate(HashMap<Object, Object> context){
		Object date = context.get(ContextKeys.DATE);
		if (date instanceof TimeTool) {
			return (TimeTool) date;
		}
		Konsultation kons = (Konsultation) context.get(ContextKeys.CONSULTATION);
		if (kons != null) {
			return new TimeTool(kons.getDatum());
		}
		return new TimeTool();
	}
}
