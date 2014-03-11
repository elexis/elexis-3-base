package com.hilotec.elexis.kgview.medikarte;

import java.util.Comparator;

import ch.elexis.data.Artikel;
import ch.elexis.data.Prescription;
import ch.rgw.tools.TimeTool;

import com.hilotec.elexis.kgview.data.FavMedikament;

/**
 * Comparator fuer Medikamentenkarteeintraege (Prescription)
 */
public class MedikarteEintragComparator
	implements Comparator<Prescription>
{
	public enum Sortierung {
		ALPHABETISCH,
		CHRONOLOGISCH,
		ORDNUNGSZAHL,
	};
	MedikarteEintragComparator.Sortierung sort;

	public MedikarteEintragComparator(MedikarteEintragComparator.Sortierung sort) {
		this.sort = sort;
	}

	// Helper zum Sortieren weil wir die 0 ganz unten wollen
	private int oz(int o) {
		return (o == 0 ? Integer.MAX_VALUE : o);
	}

	/** Sortierung nur alphabetisch */
	private int compareNurLabel(Prescription p1, Prescription p2) {
		Artikel a1 = p1.getArtikel();
		Artikel a2 = p2.getArtikel();

		// Alphabetisch nach Fav-Medi Name
		FavMedikament fm1 = FavMedikament.load(a1);
		FavMedikament fm2 = FavMedikament.load(a2);
		if (fm1 != null && fm2 != null)
			return fm1.getBezeichnung().compareTo(
					fm2.getBezeichnung());

		// Als letzte Moeglichkeit nehmen wir das Artikel-Label
		return a1.getLabel().compareTo(a2.getLabel());
	}

	/** Sortierung nur chronologisch (neuste zuoberst) */
	private int compareNurChronologisch(Prescription p1, Prescription p2) {
		TimeTool b1 = new TimeTool(p1.getBeginDate());
		TimeTool b2 = new TimeTool(p2.getBeginDate());
		TimeTool e1 = new TimeTool(p1.getEndDate());
		TimeTool e2 = new TimeTool(p2.getEndDate());

		// Sortieren nach Startdatum
		int bc = b1.compareTo(b2);
		if (bc != 0) return -bc;

		// Sortieren nach Enddatum
		return -e1.compareTo(e2);
	}



	/** Sortierung nach Ordnungszahl */
	private int compareOz(Prescription p1, Prescription p2) {
		Integer o1 = oz(MedikarteHelpers.getOrdnungszahl(p1));
		Integer o2 = oz(MedikarteHelpers.getOrdnungszahl(p2));

		if (!o1.equals(o2))
			return o1.compareTo(o2);

		// Als fallback sortieren wir chronologisch
		return compareNurLabel(p1, p2);
	}

	/** Sortierung chronologisch */
	private int compareCh(Prescription p1, Prescription p2) {
		int ord = compareNurChronologisch(p1, p2);
		if (ord != 0) return ord;

		// Als Fallback sortieren wir alphabetisch
		return compareNurLabel(p1, p2);
	}

	/** Sortierung alphabetisch */
	public int compareAl(Prescription p1, Prescription p2) {
		int ord = compareNurLabel(p1, p2);
		if (ord != 0) return ord;

		// Als Fallback sortieren wir chronologisch
		return compareCh(p1, p2);
	}


	public int compare(Prescription p1, Prescription p2) {
		switch (sort) {
			case ALPHABETISCH:  return compareAl(p1, p2);
			case CHRONOLOGISCH: return compareCh(p1, p2);
			case ORDNUNGSZAHL:  return compareOz(p1, p2);
		}
		throw new RuntimeException("Unbekannte Sortierungsreihenfolge");
	}
}
