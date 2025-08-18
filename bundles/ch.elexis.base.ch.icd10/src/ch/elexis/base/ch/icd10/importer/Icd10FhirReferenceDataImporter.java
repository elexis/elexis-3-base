package ch.elexis.base.ch.icd10.importer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

import ch.elexis.core.findings.util.ModelUtil;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.jpa.entities.ICD10;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=icd10_fhir")
public class Icd10FhirReferenceDataImporter extends AbstractReferenceDataImporter implements IReferenceDataImporter {

	@Override
	public IStatus performImport(IProgressMonitor monitor, InputStream input, Integer newVersion) {
		if(input != null) {
			try {
				monitor.beginTask("ICD-10 loading CodeSystem", monitor.UNKNOWN);
				String jsonString = IOUtils.toString(input, "UTF-8");
				IBaseResource resource = ModelUtil.getAsResource(jsonString);
				if (resource instanceof CodeSystem) {
					CodeSystem codeSystem = (CodeSystem) resource;
					if (codeSystem.hasConcept()) {
						List<ICD10> existing = EntityUtil.loadAll(ICD10.class);
						if (existing.size() > 1) {
							EntityUtil.removeAll((List<Object>) (List<?>) existing);
						}
						monitor.beginTask("ICD-10 Import", codeSystem.getCount());

						for (ConceptDefinitionComponent concept : codeSystem.getConcept()) {
							Optional<ConceptPropertyComponent> classKind = concept.getProperty().stream()
									.filter(p -> p.getCode() != null && p.getCode().equals("classKind")).findFirst();
							Optional<ConceptPropertyComponent> parent = concept.getProperty().stream()
									.filter(p -> p.getCode() != null && p.getCode().equals("parent")).findFirst();
							ICD10 parentIcd10 = null;
							if (parent.isPresent()) {
								parentIcd10 = loadIcd10(parent.get().getValueStringType().getValue())
										.orElseThrow(() -> new IllegalStateException("Could not find parent ["
												+ parent.get().getValueStringType().getValue() + "]"));
							}
							if (classKind.isPresent()) {
								if (classKind.get().getValueStringType().getValue().equals("chapter")
										|| classKind.get().getValueStringType().getValue().equals("block")) {
									ICD10 chapter = new ICD10();
									if (parentIcd10 != null) {
										chapter.setParent(parentIcd10.getId());
									}
									chapter.setCode(concept.getCode());
									chapter.setText(concept.getDisplay()); // $NON-NLS-1$
									EntityUtil.save(Collections.singletonList(chapter));
								} else {
									ICD10 code = new ICD10();
									if (parentIcd10 != null) {
										code.setParent(parentIcd10.getId());
									}
									code.setCode(concept.getCode());
									code.setText(concept.getDisplay()); // $NON-NLS-1$
									EntityUtil.save(Collections.singletonList(code));
								}
							}
							monitor.worked(1);
						}
					}
				}
				return Status.OK_STATUS;
			} catch (IOException e) {
				LoggerFactory.getLogger(getClass()).error("Error importing ICD10 FHIR resource", e);
			}			
		} else {
			LoggerFactory.getLogger(getClass()).warn("No input to import");
		}
		return Status.CANCEL_STATUS;
	}

	private Optional<ICD10> loadIcd10(String code) {
		Map<String, Object> propertyMap = new LinkedHashMap<String, Object>();
		propertyMap.put("code", code);
		List<ICD10> found = EntityUtil.loadByNamedQuery(propertyMap, ICD10.class);
		if (found.size() == 1) {
			return Optional.of(found.get(0));
		}
		return Optional.empty();
	}

	@Override
	public int getCurrentVersion() {
		return -1;
	}
}
