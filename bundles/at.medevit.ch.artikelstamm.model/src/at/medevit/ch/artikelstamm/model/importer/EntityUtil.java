package at.medevit.ch.artikelstamm.model.importer;

import java.util.List;
import java.util.Map;

import ch.elexis.core.services.IElexisEntityManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;

public class EntityUtil {

	private IElexisEntityManager entityManager;

	public EntityUtil(IElexisEntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void save(List<Object> saveObject) {
		EntityManager em = (EntityManager) entityManager.getEntityManager();
		try {
			em.getTransaction().begin();
			for (Object object : saveObject) {
				em.merge(object);
			}
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	public <T> T load(String id, Class<T> clazz) {
		EntityManager em = (EntityManager) entityManager.getEntityManager();
		try {
			return em.find(clazz, id);
		} finally {
			em.close();
		}
	}

	public <T> List<T> loadAll(Class<T> clazz) {
		EntityManager em = (EntityManager) entityManager.getEntityManager();
		try {
			CriteriaQuery<T> criteria = em.getCriteriaBuilder().createQuery(clazz);
			criteria.select(criteria.from(clazz));
			return em.createQuery(criteria).getResultList();
		} finally {
			em.close();
		}
	}

	public <T> List<T> loadByNamedQuery(Map<String, String> propertyMap, Class<T> clazz) {
		EntityManager em = (EntityManager) entityManager.getEntityManager();
		try {
			StringBuilder queryName = new StringBuilder();
			queryName.append(clazz.getSimpleName());
			for (String property : propertyMap.keySet()) {
				queryName.append(".").append(property); //$NON-NLS-1$
			}
			TypedQuery<T> namedQuery = em.createNamedQuery(queryName.toString(), clazz);
			for (String property : propertyMap.keySet()) {
				namedQuery.setParameter(property, propertyMap.get(property));
			}
			return namedQuery.getResultList();
		} finally {
			em.close();
		}
	}

	public int executeUpdate(String string) {
		EntityManager em = (EntityManager) entityManager.getEntityManager();
		try {
			em.getTransaction().begin();
			Query query = em.createQuery(string);
			int ret = query.executeUpdate();
			em.getTransaction().commit();
			return ret;
		} finally {
			em.close();
		}
	}
}
