package ch.elexis.base.ch.ticode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent;
import org.slf4j.LoggerFactory;

import ch.elexis.core.findings.util.ModelUtil;

public class TessinerCodeSystem {

	private CodeSystem codeSystem;

	private Map<String, TessinerCode[]> codesMap;
	private Map<String, TessinerCode> codeMap;

	public TessinerCodeSystem() {
		codesMap = new HashMap<>();
		codeMap = new HashMap<>();
		try {
			String jsonString = IOUtils
					.toString(getClass().getResourceAsStream("/rsc/tessiner_mtk_extension_code_system.json"), "UTF-8");
			IBaseResource resource = ModelUtil.getAsResource(jsonString);
			if (resource instanceof CodeSystem) {
				this.codeSystem = (CodeSystem) resource;
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Exception reading code system", e);
		}
	}

	public String getCodeSystemName() {
		return codeSystem.getName();
	}

	public synchronized TessinerCode[] getRootNodes() {
		TessinerCode[] ret = codesMap.get("roots");
		if (ret == null) {
			Object[] objects = codeSystem.getConcept().stream().filter(cs -> cs.getProperty().isEmpty())
					.map(cs -> getFromCode(cs.getCode()).get()).toArray();
			ret = new TessinerCode[objects.length];
			System.arraycopy(objects, 0, ret, 0, objects.length);
			codesMap.put("roots", ret);
		}
		return ret;
	}

	public synchronized TessinerCode[] getLeafNodes() {
		TessinerCode[] ret = codesMap.get("leafs");
		if (ret == null) {
			Object[] objects = codeSystem.getConcept().stream().filter(cs -> !cs.getProperty().isEmpty())
					.map(cs -> getFromCode(cs.getCode()).get()).toArray();
			ret = new TessinerCode[objects.length];
			System.arraycopy(objects, 0, ret, 0, objects.length);
			codesMap.put("leafs", ret);
		}
		return ret;
	}

	public synchronized TessinerCode[] getChildNodes(String parentCode) {
		TessinerCode[] ret = codesMap.get(parentCode + "_children");
		if (ret == null) {
			Object[] objects = codeSystem.getConcept().stream()
					.filter(cs -> !cs.getProperty().isEmpty() && hasParent(cs, parentCode))
					.map(cs -> getFromCode(cs.getCode()).get()).toArray();
			ret = new TessinerCode[objects.length];
			System.arraycopy(objects, 0, ret, 0, objects.length);
			codesMap.put(parentCode + "_children", ret);
		}
		return ret;
	}

	private boolean hasParent(ConceptDefinitionComponent conceptDefinition, String parentCode) {
		if (!conceptDefinition.getProperty().isEmpty()) {
			Optional<ConceptPropertyComponent> parentProp = conceptDefinition.getProperty().stream()
					.filter(p -> "parent".equals(p.getCode())).findFirst();
			return parentProp.isPresent() && parentCode.equals(parentProp.get().getValue().toString());
		}
		return false;
	}

	public Optional<TessinerCode> getFromCode(String code) {
		if (code != null) {
			TessinerCode ret = codeMap.get(code);
			if (ret == null) {
				Optional<ConceptDefinitionComponent> matchingConcept = codeSystem.getConcept().stream()
						.filter(cs -> code.equals(cs.getCode())).findFirst();
				if (matchingConcept.isPresent()) {
					ret = new TessinerCode(this, matchingConcept.get());
					codeMap.put(code, ret);
					return Optional.of(ret);
				}
			} else {
				return Optional.of(ret);
			}
		}
		return Optional.empty();
	}

}
