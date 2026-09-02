package ch.elexis.base.ch.ticode;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent;

import ch.elexis.core.model.IDiagnosisTree;
import ch.elexis.core.model.IXid;

public class TessinerCode implements IDiagnosisTree {

	private ConceptDefinitionComponent conceptDefinition;
	
	private TessinerCodeSystem codeSystem;

	public TessinerCode(TessinerCodeSystem codeSystem, ConceptDefinitionComponent conceptDefinition) {
		this.codeSystem = codeSystem;
		this.conceptDefinition = conceptDefinition;
	}

	@Override
	public String getDescription() {
		String ret = conceptDefinition.getDisplay();
		String lang = Locale.getDefault().getLanguage();
		if (!"de".equals(lang)) {
			Optional<ConceptDefinitionDesignationComponent> matchingDesignation = conceptDefinition.getDesignation()
					.stream().filter(d -> d.getLanguage().equals(lang)).findAny();
			if (matchingDesignation.isPresent()) {
				ret = matchingDesignation.get().getValue();
			}
		}
		return ret;
	}

	@Override
	public void setDescription(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCodeSystemName() {
		return codeSystem.getCodeSystemName();
	}

	@Override
	public String getCode() {
		return conceptDefinition.getCode();
	}

	@Override
	public void setCode(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getText() {
		return getDescription();
	}

	@Override
	public void setText(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getId() {
		return getCode();
	}

	@Override
	public String getLabel() {
		return getCode() + StringUtils.SPACE + getText();
	}

	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IXid getXid(String domain) {
		return null;
	}

	@Override
	public Long getLastupdate() {
		return 0L;
	}

	@Override
	public IDiagnosisTree getParent() {
		if (!conceptDefinition.getProperty().isEmpty()) {
			Optional<ConceptPropertyComponent> parentProp = conceptDefinition.getProperty().stream()
					.filter(p -> "parent".equals(p.getCode())).findFirst();
			if (parentProp.isPresent()) {
				String parentCode = parentProp.get().getValue().toString();
				return codeSystem.getFromCode(parentCode).orElse(null);
			}
		}
		return null;
	}

	@Override
	public void setParent(IDiagnosisTree value) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IDiagnosisTree> getChildren() {
		return (List<IDiagnosisTree>) (List<?>) Arrays.asList(codeSystem.getChildNodes(getCode()));
	}

}
