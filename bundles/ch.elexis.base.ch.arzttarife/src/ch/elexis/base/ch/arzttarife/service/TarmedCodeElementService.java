package ch.elexis.base.ch.arzttarife.service;

import java.util.HashMap;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;

import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.services.ICodeElementService.ContextKeys;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.TarmedLeistung;
import ch.rgw.tools.TimeTool;

@Component
public class TarmedCodeElementService implements ICodeElementServiceContribution {
	
	@Override
	public String getSystem(){
		return TarmedLeistung.CODESYSTEM_NAME;
	}
	
	@Override
	public Optional<ICodeElement> createFromCode(String code, HashMap<Object, Object> context){
		return Optional
			.ofNullable(TarmedLeistung.getFromCode(code, getDate(context), getLaw(context)));
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
	
	private String getLaw(HashMap<Object, Object> context){
		Object law = context.get(ContextKeys.LAW);
		if (law instanceof String) {
			return (String) law;
		}
		Object coverage = context.get(ContextKeys.COVERAGE);
		if (coverage instanceof Fall) {
			return ((Fall) coverage).getConfiguredBillingSystemLaw().name();
		}
		Object consultation = context.get(ContextKeys.CONSULTATION);
		if (consultation instanceof Konsultation
			&& ((Konsultation) consultation).getFall() != null) {
			return ((Konsultation) consultation).getFall().getConfiguredBillingSystemLaw().name();
		}
		return null;
	}
}
