package ch.elexis.base.ch.labortarif.model.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.base.ch.labortarif.ILaborLeistung;
import ch.elexis.base.ch.labortarif.LaborTarifConstants;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementService.ContextKeys;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.INamedQuery;

@Component
public class LaborTarifCodeElementServiceContribution implements ICodeElementServiceContribution {

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.base.ch.labortarif.model)")
	private IModelService modelService;

	@Override
	public String getSystem() {
		return LaborTarifConstants.CODESYSTEM_NAME;
	}

	@Override
	public CodeElementTyp getTyp() {
		return CodeElementTyp.SERVICE;
	}

	@Override
	public Optional<ICodeElement> loadFromCode(String code, Map<Object, Object> context) {
		INamedQuery<ILaborLeistung> codeQuery = modelService.getNamedQuery(ILaborLeistung.class, "code"); //$NON-NLS-1$
		List<ILaborLeistung> codes = codeQuery.executeWithParameters(codeQuery.getParameterMap("code", code)); //$NON-NLS-1$
		if (codes != null && !codes.isEmpty()) {
			LocalDate date = getDate(context);
			for (ILaborLeistung iLaborLeistung : codes) {
				if (iLaborLeistung.isValidOn(date)) {
					return Optional.of(iLaborLeistung);
				}
			}
		}
		return Optional.empty();
	}

	private LocalDate getDate(Map<Object, Object> context) {
		Object date = context.get(ContextKeys.DATE);
		if (date instanceof LocalDate) {
			return (LocalDate) date;
		}
		IEncounter encounter = (IEncounter) context.get(ContextKeys.CONSULTATION);
		if (encounter != null) {
			return encounter.getDate();
		}
		return LocalDate.now();
	}

	@Override
	public List<ICodeElement> getElements(Map<Object, Object> context) {
		// TODO Auto-generated method stub
		return null;
	}

}
