package ch.elexis.base.ch.arzttarife.model.service;

import javax.persistence.EntityManager;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventAdmin;

import ch.elexis.core.common.ElexisEvent;
import ch.elexis.core.jpa.model.adapter.AbstractModelService;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.IElexisEntityManager;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;

@Component(property = IModelService.SERVICEMODELNAME + "=ch.elexis.base.ch.arzttarife.model")
public class ArzttarifeModelService extends AbstractModelService implements IModelService {

	@Reference(target = "(id=default)")
	private IElexisEntityManager entityManager;

	@Reference
	private EventAdmin eventAdmin;

	@Activate
	public void activate() {
		adapterFactory = ArzttarifeModelAdapterFactory.getInstance();
	}

	@Override
	public <T> IQuery<T> getQuery(Class<T> clazz, boolean refreshCache, boolean includeDeleted) {
		return new ArzttarifeQuery<>(clazz, refreshCache, (EntityManager) entityManager.getEntityManager(),
				includeDeleted);
	}

	@Override
	protected EntityManager getEntityManager(boolean managed) {
		return (EntityManager) entityManager.getEntityManager(managed);
	}

	@Override
	protected void closeEntityManager(EntityManager entityManager) {
		this.entityManager.closeEntityManager(entityManager);
	}

	@Override
	protected EventAdmin getEventAdmin() {
		return eventAdmin;
	}

	@Override
	protected ElexisEvent getCreateEvent(Identifiable identifiable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearCache() {
		entityManager.clearCache();
	}

	@Override
	protected IModelService getCoreModelService() {
		return null;
	}
}
