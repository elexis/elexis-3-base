package ch.elexis.icpc.model.internal.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;

import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementService.ContextKeys;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.icpc.model.icpc.IcpcCode;
import ch.elexis.icpc.model.internal.Code;

@Component
public class CodeElementContribution implements ICodeElementServiceContribution {
	
	@Override
	public String getSystem(){
		return Code.CODESYSTEM_NAME;
	}
	
	@Override
	public CodeElementTyp getTyp(){
		return CodeElementTyp.DIAGNOSE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Optional<ICodeElement> loadFromCode(String code, Map<Object, Object> context){
		return (Optional<ICodeElement>) (Optional<?>) IcpcModelServiceHolder.get().load(code,
			IcpcCode.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ICodeElement> getElements(Map<Object, Object> context){
		if (context.get(ContextKeys.TREE_ROOTS) != null
			&& context.get(ContextKeys.TREE_ROOTS).equals(Boolean.TRUE)) {
			return (List<ICodeElement>) (List<?>) Code.getRootCodes();
		}
		IQuery<IcpcCode> query = IcpcModelServiceHolder.get().getQuery(IcpcCode.class);
		query.and("id", COMPARATOR.NOT_EQUALS, "ver");
		return (List<ICodeElement>) (List<?>) query.execute();
	}
}
