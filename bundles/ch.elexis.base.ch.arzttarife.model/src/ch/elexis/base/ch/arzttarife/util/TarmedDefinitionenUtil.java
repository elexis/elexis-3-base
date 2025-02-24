package ch.elexis.base.ch.arzttarife.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.model.service.EntityManagerHolder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class TarmedDefinitionenUtil {

	/** Text zu einem Code der qualitativen Dignität holen */
	public static String getTextForDigniQuali(final String kuerzel) {
		return TarmedDefinitionenUtil.getTitle("DIGNI_QUALI", kuerzel);
	}

	/** Kurz-Code für eine qualitative Dignität holen */
	public static String getCodeForDigniQuali(final String titel) {
		return TarmedDefinitionenUtil.getKuerzel("DIGNI_QUALI", titel);
	}

	/** Text für einen Code für quantitative Dignität holen */
	public static String getTextForDigniQuanti(final String kuerzel) {
		return TarmedDefinitionenUtil.getTitle("DIGNI_QUALI", kuerzel);
	}

	/** Text für einen Sparten-Code holen */
	public static String getTextForSparte(final String kuerzel) {
		return TarmedDefinitionenUtil.getTitle("SPARTE", kuerzel);
	}

	/** Text für eine Anästhesie-Risikoklasse holen */
	public static String getTextForRisikoKlasse(final String kuerzel) {
		return TarmedDefinitionenUtil.getTitle("ANAESTHESIE", kuerzel);
	}

	/** Text für einen ZR_EINHEIT-Code holen (Sitzung, Monat usw.) */
	public static String getTextForZR_Einheit(final String kuerzel) {
		return TarmedDefinitionenUtil.getTitle("ZR_EINHEIT", kuerzel);
	}

	public static String getTitle(String spalte, String kuerzel) {
		EntityManager em = (EntityManager) EntityManagerHolder.get().getEntityManager();
		TypedQuery<ch.elexis.core.jpa.entities.TarmedDefinitionen> query = em.createNamedQuery(
				"TarmedDefinitionen.spalte.kuerzel", ch.elexis.core.jpa.entities.TarmedDefinitionen.class);
		query.setParameter("spalte", spalte);
		query.setParameter("kuerzel", kuerzel);
		List<ch.elexis.core.jpa.entities.TarmedDefinitionen> result = query.getResultList();

		if (!result.isEmpty()) {
			return result.get(0).getTitel();
		}
		return StringUtils.EMPTY;
	}

	public static String getKuerzel(String spalte, String titel) {
		EntityManager em = (EntityManager) EntityManagerHolder.get().getEntityManager();
		TypedQuery<ch.elexis.core.jpa.entities.TarmedDefinitionen> query = em.createNamedQuery(
				"TarmedDefinitionen.spalte.titel", ch.elexis.core.jpa.entities.TarmedDefinitionen.class);
		query.setParameter("spalte", spalte);
		query.setParameter("titel", titel);
		List<ch.elexis.core.jpa.entities.TarmedDefinitionen> result = query.getResultList();
		if (!result.isEmpty()) {
			return result.get(0).getKuerzel();
		}
		return StringUtils.EMPTY;
	}
}
