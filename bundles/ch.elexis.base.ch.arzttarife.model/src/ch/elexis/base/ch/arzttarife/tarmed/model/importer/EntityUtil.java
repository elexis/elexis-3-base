package ch.elexis.base.ch.arzttarife.tarmed.model.importer;

import java.util.List;
import java.util.Map;

import ch.elexis.base.ch.arzttarife.model.service.EntityManagerHolder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;

public class EntityUtil {

	public static void save(List<Object> saveObject) {
		EntityManager em = (EntityManager) EntityManagerHolder.get().getEntityManager();
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

	public static void remove(Object object) {
		EntityManager em = (EntityManager) EntityManagerHolder.get().getEntityManager();
		try {
			em.getTransaction().begin();
			object = em.merge(object);
			em.remove(object);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	public static <T> T load(String id, Class<T> clazz) {
		EntityManager em = (EntityManager) EntityManagerHolder.get().getEntityManager();
		try {
			return em.find(clazz, id);
		} finally {
			em.close();
		}
	}

	public static <T> List<T> loadAll(Class<T> clazz) {
		EntityManager em = (EntityManager) EntityManagerHolder.get().getEntityManager();
		try {
			CriteriaQuery<T> criteria = em.getCriteriaBuilder().createQuery(clazz);
			criteria.select(criteria.from(clazz));
			return em.createQuery(criteria).getResultList();
		} finally {
			em.close();
		}
	}

	public static <T> List<T> loadByNamedQuery(Map<String, Object> propertyMap, Class<T> clazz) {
		EntityManager em = (EntityManager) EntityManagerHolder.get().getEntityManager();
		try {
			StringBuilder queryName = new StringBuilder();
			queryName.append(clazz.getSimpleName());
			for (String property : propertyMap.keySet()) {
				queryName.append(".").append(property);
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

	@SuppressWarnings("unchecked")
	public static <T> List<T> loadByNativeQuery(String query, Class<T> clazz) {
		EntityManager em = (EntityManager) EntityManagerHolder.get().getEntityManager();
		try {
			Query nativeQuery = em.createNativeQuery(query, clazz);
			return nativeQuery.getResultList();
		} finally {
			em.close();
		}
	}

	public static int executeUpdate(String string) {
		EntityManager em = (EntityManager) EntityManagerHolder.get().getEntityManager();
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
