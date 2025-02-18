package ch.berchtold.emanuel.privatrechnung.model.internal;

import ch.elexis.core.jpa.model.adapter.AbstractModelQuery;
import ch.elexis.core.services.IQuery;
import jakarta.persistence.EntityManager;

public class PrivatRechnungModelQuery<T> extends AbstractModelQuery<T> implements IQuery<T> {

	public PrivatRechnungModelQuery(Class<T> clazz, boolean refreshCache, EntityManager entityManager,
			boolean includeDeleted) {
		super(clazz, refreshCache, entityManager, includeDeleted);
	}

	@Override
	protected void initialize() {
		adapterFactory = PrivatRechnungModelAdapterFactory.getInstance();

		entityClazz = adapterFactory.getEntityClass(clazz);

		criteriaQuery = criteriaBuilder.createQuery(entityClazz);
		rootQuery = criteriaQuery.from(entityClazz);
	}
}
