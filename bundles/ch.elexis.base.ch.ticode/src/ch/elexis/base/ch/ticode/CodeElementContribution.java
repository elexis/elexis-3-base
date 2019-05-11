package ch.elexis.base.ch.ticode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;

import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementService.ContextKeys;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.IStoreToStringContribution;

@Component
public class CodeElementContribution
		implements ICodeElementServiceContribution, IStoreToStringContribution {
	
	public static final String STS_CLASS = "ch.elexis.data.TICode";
	
	@Override
	public String getSystem(){
		return TessinerCode.CODESYSTEM_NAME;
	}
	
	@Override
	public CodeElementTyp getTyp(){
		return CodeElementTyp.DIAGNOSE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Optional<ICodeElement> loadFromCode(String code, Map<Object, Object> context){
		return (Optional<ICodeElement>) (Optional<?>) TessinerCode.getFromCode(code);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ICodeElement> getElements(Map<Object, Object> context){
		if (context.get(ContextKeys.TREE_ROOTS) != null
			&& context.get(ContextKeys.TREE_ROOTS).equals(Boolean.TRUE)) {
			return (List<ICodeElement>) (List<?>) Arrays.asList(TessinerCode.getRootNodes());
		}
		return Collections.emptyList();
	}
	
	@Override
	public Optional<String> storeToString(Identifiable identifiable){
		if(identifiable instanceof TessinerCode) {
			return Optional.of(STS_CLASS + IStoreToStringContribution.DOUBLECOLON
				+ ((TessinerCode) identifiable).getId());
		}
		return Optional.empty();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Optional<Identifiable> loadFromString(String storeToString){
		if (storeToString != null && storeToString.startsWith(STS_CLASS)) {
			String[] parts = storeToString.split(IStoreToStringContribution.DOUBLECOLON);
			if (parts != null && parts.length == 2) {
				return (Optional<Identifiable>) (Optional<?>) loadFromCode(parts[1]);
			}
		}
		return Optional.empty();
	}
	
	@Override
	public Class<?> getEntityForType(String type){
		// there is no entity for the ti code ...
		return null;
	}
	
	@Override
	public String getTypeForEntity(Object entityInstance){
		// there is no entity for the ti code ...
		return null;
	}
	
}
