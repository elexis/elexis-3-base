/*******************************************************************************
 *
 * The authorship of this code and the accompanying materials is held by
 * medshare GmbH, Switzerland. All rights reserved.
 * http://medshare.net
 *
 * This code and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0
 *
 * Year of publication: 2012
 *
 *******************************************************************************/
package ch.elexis.laborimport.viollier.v2.data;

import org.apache.commons.lang3.StringUtils;
import java.util.List;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Kontakt;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;

/**
 * Auftragsverwaltungstabelle: Derzeit können Auftragsnummern aus verschiedenen
 * Domänen, welche Auftragsnummern vergeben verwaltet werden (z.B. die
 * unterschiedlichen Laborauftragsnummern des Labors und des Arztes)
 *
 */
@SuppressWarnings("unchecked")
public class KontaktOrderManagement extends PersistentObject {
	public static final String TABLENAME = "KONTAKT_ORDER_MANAGEMENT"; //$NON-NLS-1$

	// ================================================================
	// Wer diese Klasse anwenden will, trägt hier die Domain ID nach.
	// Ansonsten besteht die Gefahr von Überschneidungen.
	// Die Kombination von ORDER_NR_DOMAIN und ORDER_NR muss weltweit eindeiutig
	// sein!
	// Als Domain ID werden ausschliesslich GLN oder OID akzeptiert. Andere Einträge
	// werden ohne
	// Rückfrage wieder aus diesem Sourcecode entfernt.
	// Für die Schweiz gelten folgende Register:
	// GLN: http://www.refdata.ch/content/partner_d.aspx
	// OID: http://oid.refdata.ch
	public static final String ORDER_DOMAIN_LAB_ORDER_FILLER_MEDICS = "7601001383647"; // Hauptsitz Medics //$NON-NLS-1$
																						// Labor AG, Bern
	public static final String ORDER_DOMAIN_LAB_ORDER_FILLER_VIOLLIER = "7601002132732"; // Hauptsitz //$NON-NLS-1$
																							// Viollier AG, Allschwil
	// ================================================================

	public static final String FLD_KONTAKT_ID = "KONTAKT_ID"; //$NON-NLS-1$
	public static final String FLD_ORDER_NR = "ORDER_NR"; //$NON-NLS-1$
	public static final String FLD_ORDER_NR_DOMAIN = "ORDER_NR_DOMAIN"; //$NON-NLS-1$
	private static final String FLD_VERSION = FLD_KONTAKT_ID;
	private static final String VERSION = "2"; //$NON-NLS-1$

	private static final String setVersionSQL = "UPDATE " + TABLENAME + " SET " + FLD_VERSION + "='" + VERSION //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ "' WHERE ID='VERSION'; "; //$NON-NLS-1$

	private static final String index1SQL = "CREATE INDEX " + TABLENAME + "_idx_kontakt on " + TABLENAME + "(" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ FLD_KONTAKT_ID + ");"; //$NON-NLS-1$
	private static final String index2SQL = "CREATE INDEX " + TABLENAME + "_idx1 on " + TABLENAME + "(" + FLD_KONTAKT_ID //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ ", " + FLD_ORDER_NR + ", " + FLD_ORDER_NR_DOMAIN + ");"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static final String index3SQL = "CREATE UNIQUE INDEX " + TABLENAME + "_idx_order on " + TABLENAME + "(" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ FLD_ORDER_NR + ", " + FLD_ORDER_NR_DOMAIN + ");"; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String addOrderNrDomain = "ALTER TABLE " + TABLENAME //$NON-NLS-1$
			+ " ADD " + FLD_ORDER_NR_DOMAIN + " VARCHAR(255);"; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String createDB = "CREATE TABLE " //$NON-NLS-1$
			+ TABLENAME + "(" //$NON-NLS-1$
			+ "ID VARCHAR(25) primary key," // ID, Primärschlüssel //$NON-NLS-1$
			+ FLD_KONTAKT_ID + " VARCHAR(25)," // Fremdschlüssel Kontakt (z.B. Mandant) //$NON-NLS-1$
			+ FLD_ORDER_NR + " VARCHAR(12)," // Eigentliche Auftragsnummer //$NON-NLS-1$
			+ FLD_ORDER_NR_DOMAIN + " VARCHAR(255)," // Domäne, innerhalb welcher die Auftragsnummer //$NON-NLS-1$
														// eindeutig ist (z.B. OID oder GLN des Labors oder auch GLN des
														// Mandanten)
			+ FLD_LASTUPDATE + " BIGINT," + FLD_DELETED + " CHAR(1) default '0'" //$NON-NLS-1$ //$NON-NLS-2$
			+ ");"// //$NON-NLS-1$
			+ index1SQL + index2SQL + index3SQL;

	private static final String insertVersion = "insert into " + TABLENAME + " (ID," + FLD_VERSION //$NON-NLS-1$ //$NON-NLS-2$
			+ ") VALUES ('VERSION', " + JdbcLink.wrap(VERSION) + ");"; //$NON-NLS-1$ //$NON-NLS-2$

	static {
		addMapping(TABLENAME, FLD_KONTAKT_ID, FLD_ORDER_NR, FLD_ORDER_NR_DOMAIN); // $NON-NLS-1$
		checkTable();
	}

	/**
	 * Pruefen ob die Tabelle existiert und gegebenenfalls aktualisieren.
	 */
	private static void checkTable() {
		KontaktOrderManagement check = load("VERSION"); //$NON-NLS-1$
		if (!check.exists()) {
			check = checkVersion1();
		}
		if (!check.exists()) {
			createOrModifyTable(createDB + insertVersion);
		}
		try {
			int ver = Integer.parseInt(check.get(FLD_VERSION)); // $NON-NLS-1$
			switch (ver) {
			case 1:
				updateToVersion2();
				createOrModifyTable(setVersionSQL);
				break;
			}
			checkInvalidEntries();
		} catch (Exception e) {
			SWTHelper.showError("Error in Database", //$NON-NLS-1$
					String.format("Error in Table %s: No version information found", TABLENAME)); //$NON-NLS-1$
		}
	}

	/**
	 * Prüft, ob tatsächlich nur bekannte Inhalte in der Tabelle drin sind. Wenn
	 * nicht, wird eine Warnung ausgegeben.
	 */
	private static void checkInvalidEntries() {
		try {
			Query<KontaktOrderManagement> query = new Query<KontaktOrderManagement>(KontaktOrderManagement.class);
			query.add(KontaktOrderManagement.FLD_ORDER_NR_DOMAIN, Query.NOT_EQUAL,
					ORDER_DOMAIN_LAB_ORDER_FILLER_MEDICS);
			query.and();
			query.add(KontaktOrderManagement.FLD_ORDER_NR_DOMAIN, Query.NOT_EQUAL,
					ORDER_DOMAIN_LAB_ORDER_FILLER_VIOLLIER);
			List<KontaktOrderManagement> list = query.execute();
			if (list.size() > 0) {
				SWTHelper.showError("KontaktOrderManagement", "Invalid Database Entries",
						"Table " + TABLENAME + " contains " + Integer.toString(list.size())
								+ " entries of unknown order domains. Check your Database!");
			}
		} catch (Exception ex) {
		}
	}

	/**
	 * Prüft, ob schon eine frühere Tabelle existiert. Hinweis: Es gibt eine alte
	 * Medics Labor Importer Version, welche diese Tabelle ins Leben gerufen hat
	 * aber leider keinen VERSION Eintrag schreibt... Deshalb diese etwas "unschöne"
	 * Art der Prüfung.
	 *
	 * @return Versions-Record
	 */
	private static KontaktOrderManagement checkVersion1() {
		try {
			Query<KontaktOrderManagement> query = new Query<KontaktOrderManagement>(KontaktOrderManagement.class);
			query.add(KontaktOrderManagement.FLD_ORDER_NR, Query.GREATER, StringUtils.EMPTY);
			List<KontaktOrderManagement> list = query.execute();
			if (list.size() > 0) {
				updateToVersion2();
				createOrModifyTable(insertVersion);
			}
		} catch (Exception ex) {
		}
		return load("VERSION"); //$NON-NLS-1$
	}

	/**
	 * Update-Skript zum Aktualisieren auf Version 2 Bei Version 2 kommen die Spalte
	 * ORDER_NR_DOMAIN und Indexe hinzu Erstellung Update: 3.7.2012 durch medshare
	 * GmbH wegen Update Viollier Labor Importer (ch.elexis.labor.viollier.v2)
	 *
	 */
	private static void updateToVersion2() {
		createOrModifyTable(addOrderNrDomain);
		createOrModifyTable(index1SQL);
		createOrModifyTable(index2SQL);
		createOrModifyTable(index3SQL);
	}

	/**
	 * Erstellt einen neuen Eintrag anhand der übergebenen Parameter
	 *
	 * @param kontaktID     ID des Kontakts, welchem dieser Auftrag zugeordnet
	 *                      werden soll. z.B. Mandant für Laboraufträge
	 * @param orderNrString Auftragsnummer
	 * @param orderNrDomain Domäne, welche die Auftragsnummer vergeben hat. Das kann
	 *                      z.B. das Labor (HL7: Filler) oder der Arzt (HL7: Placer)
	 *                      sein.
	 */
	public KontaktOrderManagement(String kontaktID, String orderNrString, String orderNrDomain) {
		create(null);
		set(new String[] { FLD_KONTAKT_ID, FLD_ORDER_NR, FLD_ORDER_NR_DOMAIN }, kontaktID, orderNrString,
				orderNrDomain);
	}

	/**
	 * Erstellt einen neuen Eintrag anhand der übergebenen Parameter
	 *
	 * @param kontakt       Kontakt, welchem dieser Auftrag zugeordnet werden soll.
	 *                      z.B. Mandant für Laboraufträge
	 * @param orderNrString Auftragsnummer
	 * @param orderNrDomain Domäne, welche die Auftragsnummer vergeben hat. Das kann
	 *                      z.B. das Labor (HL7: Filler) oder der Arzt (HL7: Placer)
	 *                      sein.
	 */
	public KontaktOrderManagement(Kontakt kontakt, String orderNrString, String orderNrDomain) {
		create(null);
		set(new String[] { FLD_KONTAKT_ID, FLD_ORDER_NR, FLD_ORDER_NR_DOMAIN }, kontakt.getId(), orderNrString,
				orderNrDomain);
	}

	/**
	 * Lädt den Record anhand der ID
	 *
	 * @param id Key, welcher zum Laden verwendet wird
	 */
	private KontaktOrderManagement(String id) {
		super(id);
	}

	/**
	 * Standard Konstruktor für die Anwendung in Queries
	 *
	 */
	public KontaktOrderManagement() {
	}

	/**
	 * Lädt den Record anhand der ID
	 *
	 * @param id Key, welcher zum Laden verwendet wird
	 * @return Gewünschter Record
	 */
	public static KontaktOrderManagement load(String id) {
		return new KontaktOrderManagement(id);

	}

	/**
	 * @return ID des Kontakts
	 */
	public String getKontaktId() {
		return get(FLD_KONTAKT_ID);
	}

	/**
	 * Setzt die ID des Kontakts
	 *
	 * @param value Kontakt ID
	 */
	public void setKontaktId(String value) {
		set(FLD_KONTAKT_ID, value);
	}

	/**
	 * Gibt die Auftragsnummer zurück
	 *
	 * @return Auftragsnummer
	 */
	public String getOrderNr() {
		return get(FLD_ORDER_NR);
	}

	/**
	 * Setzt die Auftragsnummer
	 *
	 * @param value Auftragsnummer
	 */
	public void setOrderNr(String value) {
		set(FLD_ORDER_NR, value);
	}

	/**
	 * Gibt die Domäne zurück, welche die Auftragsnummer vergeben hat
	 *
	 * @return Identifikation der Domäne, welche die Auftragsnummer vergeben hat
	 */
	public String getOrderNrDomain() {
		return get(FLD_ORDER_NR);
	}

	/**
	 * Setzt die Domäne, welche die Auftragsnummer vergeben hat
	 *
	 * @param value Identifikation der Domäne, welche die Auftragsnummer vergeben
	 *              hat
	 */
	public void setOrderNrDomain(String value) {
		set(FLD_ORDER_NR_DOMAIN, value);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ch.elexis.laborimport.viollier.v2.data.PersistentObject#getLabel()
	 */
	@Override
	public String getLabel() {
		return getId();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ch.elexis.laborimport.viollier.v2.data.PersistentObject#getTableName()
	 */
	@Override
	protected String getTableName() {
		return TABLENAME;
	}
}
