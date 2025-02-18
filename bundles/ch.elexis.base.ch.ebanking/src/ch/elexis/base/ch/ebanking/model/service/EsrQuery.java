package ch.elexis.base.ch.ebanking.model.service;

import ch.elexis.core.jpa.model.adapter.AbstractModelQuery;
import ch.elexis.core.services.IQuery;
import jakarta.persistence.EntityManager;

public class EsrQuery<T> extends AbstractModelQuery<T> implements IQuery<T> {

	public EsrQuery(Class<T> clazz, boolean refreshCache, EntityManager entityManager, boolean includeDeleted) {
		super(clazz, refreshCache, entityManager, includeDeleted);
	}

	@Override
	protected void initialize() {
		adapterFactory = EsrModelAdapterFactory.getInstance();

		entityClazz = adapterFactory.getEntityClass(clazz);

		criteriaQuery = criteriaBuilder.createQuery(entityClazz);
		rootQuery = criteriaQuery.from(entityClazz);
	}
}
