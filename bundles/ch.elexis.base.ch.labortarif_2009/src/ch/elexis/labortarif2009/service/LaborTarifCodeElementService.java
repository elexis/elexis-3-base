package ch.elexis.labortarif2009.service;

import java.util.HashMap;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;

import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.services.ICodeElementService.ContextKeys;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.data.Konsultation;
import ch.elexis.labortarif2009.data.Labor2009Tarif;
import ch.rgw.tools.TimeTool;

@Component
public class LaborTarifCodeElementService implements ICodeElementServiceContribution {
	
	@Override
	public String getSystem(){
		return Labor2009Tarif.CODESYSTEM_NAME;
	}
	
	@Override
	public Optional<ICodeElement> createFromCode(String code, HashMap<Object, Object> context){
		ICodeElement ret = Labor2009Tarif.getFromCode(code, getDate(context));
		return Optional.ofNullable(ret);
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
