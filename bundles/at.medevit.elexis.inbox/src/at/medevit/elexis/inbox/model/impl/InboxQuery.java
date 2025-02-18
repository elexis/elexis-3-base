package at.medevit.elexis.inbox.model.impl;

import ch.elexis.core.jpa.model.adapter.AbstractModelQuery;
import ch.elexis.core.services.IQuery;
import jakarta.persistence.EntityManager;

public class InboxQuery<T> extends AbstractModelQuery<T> implements IQuery<T> {

	public InboxQuery(Class<T> clazz, boolean refreshCache, EntityManager entityManager, boolean includeDeleted) {
		super(clazz, refreshCache, entityManager, includeDeleted);
	}

	@Override
	protected void initialize() {
		adapterFactory = InboxModelAdapterFactory.getInstance();

		entityClazz = adapterFactory.getEntityClass(clazz);

		criteriaQuery = criteriaBuilder.createQuery(entityClazz);
		rootQuery = criteriaQuery.from(entityClazz);
	}
}
