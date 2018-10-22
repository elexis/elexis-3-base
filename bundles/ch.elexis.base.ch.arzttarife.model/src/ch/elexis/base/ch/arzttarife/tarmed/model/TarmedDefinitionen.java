package ch.elexis.base.ch.arzttarife.tarmed.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ch.elexis.base.ch.arzttarife.model.service.EntityManagerHolder;

public class TarmedDefinitionen {
	
	public static String getTitle(String spalte, String kuerzel){
		EntityManager em = (EntityManager) EntityManagerHolder.get().getEntityManager();
		TypedQuery<ch.elexis.core.jpa.entities.TarmedDefinitionen> query =
			em.createNamedQuery("TarmedDefinitionen.spalte.kuerzel",
				ch.elexis.core.jpa.entities.TarmedDefinitionen.class);
		query.setParameter("spalte", spalte);
		query.setParameter("kuerzel", kuerzel);
		List<ch.elexis.core.jpa.entities.TarmedDefinitionen> result = query.getResultList();
		
		if (!result.isEmpty()) {
			return result.get(0).getTitel();
		}
		return "";
	}
	
	public static String getKuerzel(String spalte, String titel){
		EntityManager em = (EntityManager) EntityManagerHolder.get().getEntityManager();
		TypedQuery<ch.elexis.core.jpa.entities.TarmedDefinitionen> query =
			em.createNamedQuery("TarmedDefinitionen.spalte.titel",
				ch.elexis.core.jpa.entities.TarmedDefinitionen.class);
		query.setParameter("spalte", spalte);
		query.setParameter("titel", titel);
		List<ch.elexis.core.jpa.entities.TarmedDefinitionen> result = query.getResultList();
		if (!result.isEmpty()) {
			return result.get(0).getKuerzel();
		}
		return "";
	}
}
