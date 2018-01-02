package ch.elexis.base.befunde.findings.migrator.strategy;

import ch.elexis.core.findings.templates.model.FindingsTemplate;
import ch.elexis.core.findings.templates.service.IFindingsTemplateService;

public abstract class AbstractMigrationStrategy implements IMigrationStrategy {
	
	protected IFindingsTemplateService templateService;
	protected FindingsTemplate template;
	
	@Override
	public void setTemplateService(IFindingsTemplateService tempalteService){
		this.templateService = tempalteService;
	}
	
	@Override
	public void setTemplate(FindingsTemplate template){
		this.template = template;
	}
	
}
