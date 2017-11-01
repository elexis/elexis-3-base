package ch.elexis.base.befunde.findings.migrator.preferences;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.findings.migration.IMigratorService;
import ch.elexis.core.findings.templates.service.IFindingsTemplateService;

@Component
public class FindingsServiceComponent {
	private static IMigratorService migratorService;
	
	private static IFindingsTemplateService templateService;
	
	@Reference(unbind = "-")
	public void setMigratorService(IMigratorService migratorService){
		FindingsServiceComponent.migratorService = migratorService;
	}
	
	@Reference(unbind = "-")
	public void setTemplateService(IFindingsTemplateService templateService){
		FindingsServiceComponent.templateService = templateService;
	}
	
	public static IMigratorService getMigratorService(){
		return migratorService;
	}
	
	public static IFindingsTemplateService getTemplateService(){
		return templateService;
	}
}
