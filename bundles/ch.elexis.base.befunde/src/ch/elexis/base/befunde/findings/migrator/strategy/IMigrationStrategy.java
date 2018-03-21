package ch.elexis.base.befunde.findings.migrator.strategy;

import java.util.Optional;

import ch.elexis.core.findings.IObservation;
import ch.elexis.core.findings.templates.model.FindingsTemplate;
import ch.elexis.core.findings.templates.service.IFindingsTemplateService;

public interface IMigrationStrategy {
	
	public Optional<IObservation> migrate();
	
	public void setTemplateService(IFindingsTemplateService tempalteService);
	
	public void setTemplate(FindingsTemplate template);
}
