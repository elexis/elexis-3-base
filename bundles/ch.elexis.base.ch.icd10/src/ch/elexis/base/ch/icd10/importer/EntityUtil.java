package ch.elexis.base.ch.icd10.importer;

import java.util.List;

import jakarta.persistence.EntityManager;
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

	public static void removeAll(List<Object> removeObjects) {
		EntityManager em = (EntityManager) EntityManagerHolder.get().getEntityManager();
		try {
			em.getTransaction().begin();
			for (Object object : removeObjects) {
				Object mergedObject = em.merge(object);
				em.remove(mergedObject);
			}
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
}
