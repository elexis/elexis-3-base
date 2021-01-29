package ch.elexis.base.ch.arzttarife.tarmed.model.importer;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import ch.elexis.base.ch.arzttarife.model.service.EntityManagerHolder;

public class EntityUtil {
	
	public static void save(List<Object> saveObject){
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
	
	public static <T> T load(String id, Class<T> clazz){
		EntityManager em = (EntityManager) EntityManagerHolder.get().getEntityManager();
		try {
			return em.find(clazz, id);
		} finally {
			em.close();
		}
	}
	
	public static <T> List<T> loadAll(Class<T> clazz){
		EntityManager em = (EntityManager) EntityManagerHolder.get().getEntityManager();
		try {
			CriteriaQuery<T> criteria = em.getCriteriaBuilder().createQuery(clazz);
		    criteria.select(criteria.from(clazz));
			return em.createQuery(criteria).getResultList();
		} finally {
			em.close();
		}
	}
	
	public static <T> List<T> loadByNamedQuery(Map<String, Object> propertyMap,
		Class<T> clazz){
		EntityManager em = (EntityManager) EntityManagerHolder.get().getEntityManager();
		try {
			StringBuilder queryName = new StringBuilder();
			queryName.append(clazz.getSimpleName());
			for (String property : propertyMap.keySet()) {
				queryName.append(".").append(property);
			}
			TypedQuery<T> namedQuery =
				em.createNamedQuery(queryName.toString(), clazz);
			for (String property : propertyMap.keySet()) {
				namedQuery.setParameter(property, propertyMap.get(property));
			}
			return namedQuery.getResultList();
		} finally {
			em.close();
		}
	}
}
