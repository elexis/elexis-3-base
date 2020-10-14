package ch.elexis.base.solr.task;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.ComponentException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.model.tasks.IIdentifiedRunnable;
import ch.elexis.core.model.tasks.IIdentifiedRunnableFactory;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.IEncounterService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.tasks.model.ITaskService;

@Component
public class SolrIndexerIdentifiedRunnableFactory implements IIdentifiedRunnableFactory {
	
	private IModelService omnivoreModelService;
	private IModelService coreModelService;
	@Reference
	private IEncounterService encounterService;
	
	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	private void setModelService(IModelService modelService){
		coreModelService = modelService;
	}
	
	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.omnivore.data.model)")
	public void setOmnivoreModelService(IModelService modelService){
		omnivoreModelService = modelService;
	}
	
	@Override
	public List<IIdentifiedRunnable> getProvidedRunnables(){
		List<IIdentifiedRunnable> ret = new ArrayList<>();
		ret.add(new SolrIndexerIdentifiedRunnable(coreModelService, omnivoreModelService,
			encounterService));
		return ret;
	}
	
	@Override
	public void initialize(Object taskService){
		try {
			SolrIndexerIdentifiedRunnableTaskDescriptor.getOrCreate((ITaskService) taskService);
		} catch (TaskException e) {
			throw new ComponentException(e);
		}
	}
	
}
