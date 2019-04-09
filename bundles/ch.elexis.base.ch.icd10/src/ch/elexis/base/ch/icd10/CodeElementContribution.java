package ch.elexis.base.ch.icd10;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;

import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementService.ContextKeys;
import ch.elexis.core.services.ICodeElementServiceContribution;

@Component
public class CodeElementContribution implements ICodeElementServiceContribution {
	
	@Override
	public String getSystem(){
		return Icd10Diagnosis.CODESYSTEM_NAME;
	}
	
	@Override
	public CodeElementTyp getTyp(){
		return CodeElementTyp.DIAGNOSE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Optional<ICodeElement> loadFromCode(String code, Map<Object, Object> context){
		return (Optional<ICodeElement>) (Optional<?>) ModelUtil.loadDiagnosisWithCode(code);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ICodeElement> getElements(Map<Object, Object> context){
		if (context.get(ContextKeys.TREE_ROOTS) != null
			&& context.get(ContextKeys.TREE_ROOTS).equals(Boolean.TRUE)) {
			return (List<ICodeElement>) (List<?>) ModelUtil.loadDiagnosisWithParent("NIL");
		}
		return (List<ICodeElement>) (List<?>) ModelUtil.loadAllDiagnosis();
	}
	
}
