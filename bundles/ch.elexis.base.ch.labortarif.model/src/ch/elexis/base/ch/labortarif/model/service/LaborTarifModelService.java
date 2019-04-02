package ch.elexis.base.ch.labortarif.model.service;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventAdmin;

import ch.elexis.base.ch.labortarif.model.LaborLeistung;
import ch.elexis.core.common.ElexisEvent;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.jpa.entities.EntityWithId;
import ch.elexis.core.jpa.model.adapter.AbstractModelService;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.IElexisEntityManager;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IStoreToStringContribution;

@Component(property = IModelService.SERVICEMODELNAME + "=ch.elexis.base.ch.labortarif.model")
public class LaborTarifModelService extends AbstractModelService
		implements IModelService, IStoreToStringContribution {
	
	@Reference
	private IElexisEntityManager entityManager;
	
	@Reference
	private EventAdmin eventAdmin;
	
	@Activate
	public void activate(){
		adapterFactory = LaborTarifModelAdapterFactory.getInstance();
	}
	
	@Override
	public <T> IQuery<T> getQuery(Class<T> clazz, boolean refreshCache, boolean includeDeleted){
		return new LaborTarifModelQuery<>(clazz, refreshCache,
			(EntityManager) entityManager.getEntityManager(), includeDeleted);
	}
	
	@Override
	protected EntityManager getEntityManager(boolean managed){
		return (EntityManager) entityManager.getEntityManager(managed);
	}
	
	@Override
	protected void closeEntityManager(EntityManager entityManager){
		this.entityManager.closeEntityManager(entityManager);
	}
	
	@Override
	protected EventAdmin getEventAdmin(){
		return eventAdmin;
	}
	
	@Override
	protected ElexisEvent getCreateEvent(Identifiable identifiable){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Optional<String> storeToString(Identifiable identifiable){
		if (identifiable instanceof LaborLeistung) {
			return Optional
				.of(LaborLeistung.STS_CLASS + StringConstants.DOUBLECOLON + identifiable.getId());
		}
		return Optional.empty();
	}
	
	@Override
	public Optional<Identifiable> loadFromString(String storeToString){
		if (storeToString.startsWith(LaborLeistung.STS_CLASS + StringConstants.DOUBLECOLON)) {
			String[] split = splitIntoTypeAndId(storeToString);
			String id = split[1];
			EntityManager em = (EntityManager) entityManager.getEntityManager();
			EntityWithId dbObject = em.find(ch.elexis.core.jpa.entities.Labor2009Tarif.class, id);
			return Optional.ofNullable(LaborTarifModelAdapterFactory.getInstance()
				.getModelAdapter(dbObject, null, false).orElse(null));
		}
		return Optional.empty();
	}
	
	@Override
	public void clearCache(){
		entityManager.clearCache();
	}
}
