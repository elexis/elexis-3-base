package ch.elexis.base.ch.ticode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementService.ContextKeys;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.IStoreToStringContribution;

@Component
public class CodeElementContribution implements ICodeElementServiceContribution, IStoreToStringContribution {

	public static final String STS_CLASS = "ch.elexis.data.TICode"; //$NON-NLS-1$

	private TessinerCodeSystem codeSystemKvg;
	private TessinerCodeSystem codeSystemMtk;

	@Activate
	public void activate() {
		this.codeSystemKvg = new TessinerCodeSystem("/rsc/tessiner_code_system.json");
		this.codeSystemMtk = new TessinerCodeSystem("/rsc/tessiner_mtk_extension_code_system.json");
	}

	@Override
	public String getSystem() {
		return codeSystemKvg.getCodeSystemName();
	}

	@Override
	public CodeElementTyp getTyp() {
		return CodeElementTyp.DIAGNOSE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<ICodeElement> loadFromCode(String code, Map<Object, Object> context) {
		String law = getLaw(context);
		if ("kvg".equalsIgnoreCase(law)) {
			return (Optional<ICodeElement>) (Optional<?>) codeSystemKvg.getFromCode(code);
		}
		return (Optional<ICodeElement>) (Optional<?>) codeSystemMtk.getFromCode(code);
	}

	private String getLaw(Map<Object, Object> context) {
		Object law = context.get(ContextKeys.LAW);
		if (law instanceof String) {
			return (String) law;
		}
		Object coverage = context.get(ContextKeys.COVERAGE);
		if (coverage instanceof ICoverage) {
			return ((ICoverage) coverage).getBillingSystem().getLaw().name();
		}
		Object consultation = context.get(ContextKeys.CONSULTATION);
		if (consultation instanceof IEncounter && ((IEncounter) consultation).getCoverage() != null) {
			return ((IEncounter) consultation).getCoverage().getBillingSystem().getLaw().name();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ICodeElement> getElements(Map<Object, Object> context) {
		String law = getLaw(context);
		if ("kvg".equalsIgnoreCase(law)) {
			if (context.get(ContextKeys.TREE_ROOTS) != null
					&& context.get(ContextKeys.TREE_ROOTS).equals(Boolean.TRUE)) {
				return (List<ICodeElement>) (List<?>) Arrays.asList(codeSystemKvg.getRootNodes());
			}
			return (List<ICodeElement>) (List<?>) Arrays.asList(codeSystemKvg.getLeafNodes());
		}
		if (context.get(ContextKeys.TREE_ROOTS) != null && context.get(ContextKeys.TREE_ROOTS).equals(Boolean.TRUE)) {
			return (List<ICodeElement>) (List<?>) Arrays.asList(codeSystemMtk.getRootNodes());
		}
		return (List<ICodeElement>) (List<?>) Arrays.asList(codeSystemMtk.getLeafNodes());

	}

	@Override
	public Optional<String> storeToString(Identifiable identifiable) {
		if (identifiable instanceof TessinerCode) {
			return Optional
					.of(STS_CLASS + IStoreToStringContribution.DOUBLECOLON + ((TessinerCode) identifiable).getId());
		}
		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<Identifiable> loadFromString(String storeToString) {
		if (storeToString != null && storeToString.startsWith(STS_CLASS)) {
			String[] parts = storeToString.split(IStoreToStringContribution.DOUBLECOLON);
			if (parts != null && parts.length == 2) {
				Optional<Identifiable> ret = (Optional<Identifiable>) (Optional<?>) loadFromCode(parts[1],
						Map.of(ContextKeys.LAW, "kvg"));
				// try no law
				if (ret.isEmpty()) {
					ret = (Optional<Identifiable>) (Optional<?>) loadFromCode(parts[1]);
				}
				// allow any codes with correct class as transient diagnose code
				if (ret.isEmpty()) {
					ret = Optional.of(new TransientTessinerCode(parts[1]));
				}
				return ret;
			}
		}
		return Optional.empty();
	}

	@Override
	public Class<?> getEntityForType(String type) {
		// there is no entity for the ti code ...
		return null;
	}

	@Override
	public String getTypeForEntity(Object entityInstance) {
		// there is no entity for the ti code ...
		return null;
	}

	@Override
	public String getTypeForModel(Class<?> interfaze) {
		// there is no entity for the ti code ...
		return null;
	}
}
