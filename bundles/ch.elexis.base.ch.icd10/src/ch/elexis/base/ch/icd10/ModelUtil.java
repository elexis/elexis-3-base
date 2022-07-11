package ch.elexis.base.ch.icd10;

import java.util.List;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.model.IDiagnosisTree;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.INamedQuery;

@Component
public class ModelUtil {

	private static IModelService icd10ModelService;

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.base.ch.icd10)")
	public void setModelService(IModelService modelService) {
		ModelUtil.icd10ModelService = modelService;
	}

	public static Optional<IDiagnosisTree> loadDiagnosisWithId(String id) {
		return icd10ModelService.load(id, IDiagnosisTree.class);
	}

	public static Optional<IDiagnosisTree> loadDiagnosisWithCode(String code) {
		INamedQuery<IDiagnosisTree> query = icd10ModelService.getNamedQuery(IDiagnosisTree.class, "code"); //$NON-NLS-1$
		List<IDiagnosisTree> found = query.executeWithParameters(query.getParameterMap("code", code)); //$NON-NLS-1$
		if (!found.isEmpty()) {
			return Optional.of(found.get(0));
		}
		return Optional.empty();
	}

	public static List<IDiagnosisTree> loadDiagnosisWithParent(String parentId) {
		INamedQuery<IDiagnosisTree> query = icd10ModelService.getNamedQuery(IDiagnosisTree.class, "parent"); //$NON-NLS-1$
		return query.executeWithParameters(query.getParameterMap("parent", parentId)); //$NON-NLS-1$
	}

	public static List<IDiagnosisTree> loadAllDiagnosis() {
		return icd10ModelService.getQuery(IDiagnosisTree.class).execute();
	}
}
