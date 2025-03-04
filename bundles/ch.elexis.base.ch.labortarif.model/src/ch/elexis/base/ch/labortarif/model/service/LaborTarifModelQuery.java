package ch.elexis.base.ch.labortarif.model.service;

import ch.elexis.core.jpa.model.adapter.AbstractModelQuery;
import ch.elexis.core.services.IQuery;
import jakarta.persistence.EntityManager;

public class LaborTarifModelQuery<T> extends AbstractModelQuery<T> implements IQuery<T> {

	public LaborTarifModelQuery(Class<T> clazz, boolean refreshCache, EntityManager entityManager,
			boolean includeDeleted) {
		super(clazz, refreshCache, entityManager, includeDeleted);
	}

	@Override
	protected void initialize() {
		adapterFactory = LaborTarifModelAdapterFactory.getInstance();

		entityClazz = adapterFactory.getEntityClass(clazz);

		criteriaQuery = criteriaBuilder.createQuery(entityClazz);
		rootQuery = criteriaQuery.from(entityClazz);
	}
}
