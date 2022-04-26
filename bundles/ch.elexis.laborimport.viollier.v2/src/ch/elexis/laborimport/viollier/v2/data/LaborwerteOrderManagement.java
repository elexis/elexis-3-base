/*******************************************************************************
 *
 * The authorship of this code and the accompanying materials is held by
 * medshare GmbH, Switzerland. All rights reserved.
 * http://medshare.net
 *
 * This code and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0
 *
 * Year of publication: 2013
 *
 *******************************************************************************/
package ch.elexis.laborimport.viollier.v2.data;

import java.util.List;

import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;

/**
 * JoinTabelle: Für das Viollier Portal muss für jeden Laborwert, eine
 * eindeutige OrderNr definiert sein
 *
 */
@SuppressWarnings("unchecked")
public class LaborwerteOrderManagement extends PersistentObject {

	public static final String TABLENAME = "LABORWERTE_ORDER_JOINT"; //$NON-NLS-1$
	public static final String FLD_LABRESULT_ID = "LABRESULT_ID"; //$NON-NLS-1$
	public static final String FLD_ORDER_ID = "ORDER_ID"; //$NON-NLS-1$
	private static final String FLD_VERSION = FLD_LABRESULT_ID;
	private static final String VERSION = "1"; //$NON-NLS-1$

	private static final String index1SQL = "CREATE INDEX " + TABLENAME + "_idx_labresult on " + TABLENAME + "(" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ FLD_LABRESULT_ID + ");"; //$NON-NLS-1$
	private static final String index2SQL = "CREATE INDEX " + TABLENAME + "_idx_order on " + TABLENAME + "(" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ FLD_ORDER_ID + ");"; //$NON-NLS-1$
	private static final String index3SQL = "CREATE UNIQUE INDEX " + TABLENAME + "_idx_joint on " + TABLENAME + "(" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ FLD_LABRESULT_ID + ", " + FLD_ORDER_ID + ");"; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String createDB = "CREATE TABLE " //$NON-NLS-1$
			+ TABLENAME + "(" //$NON-NLS-1$
			+ "ID VARCHAR(25) primary key," // ID, Primärschlüssel //$NON-NLS-1$
			+ FLD_LABRESULT_ID + " VARCHAR(25)," // Fremdschlüssel LABORWERTE.ID //$NON-NLS-1$
			+ FLD_ORDER_ID + " VARCHAR(25)," // Fremdschlüssel KONTAKT_ORDER_MANAGEMENT.ID //$NON-NLS-1$
			+ FLD_LASTUPDATE + " BIGINT," + FLD_DELETED + " CHAR(1) default '0'" //$NON-NLS-1$ //$NON-NLS-2$
			+ ");"// //$NON-NLS-1$
			+ index1SQL + index2SQL + index3SQL;

	private static final String insertVersion = "insert into " + TABLENAME + " (ID," + FLD_VERSION //$NON-NLS-1$ //$NON-NLS-2$
			+ ") VALUES ('VERSION', " + JdbcLink.wrap(VERSION) + ");"; //$NON-NLS-1$ //$NON-NLS-2$

	static {
		addMapping(TABLENAME, FLD_LABRESULT_ID, FLD_ORDER_ID); // $NON-NLS-1$
		checkTable();
	}

	/**
	 * Pruefen ob die Tabelle existiert und gegebenenfalls aktualisieren.
	 */
	private static void checkTable() {
		LaborwerteOrderManagement check = load("VERSION"); //$NON-NLS-1$
		if (!check.exists()) {
			createOrModifyTable(createDB + insertVersion);
		}
	}

	/**
	 *
	 * @param laborwertID
	 * @param orderNrString
	 */
	public LaborwerteOrderManagement(String laborwertID, String orderNrString) {
		create(null);
		set(new String[] { FLD_LABRESULT_ID, FLD_ORDER_ID }, laborwertID, orderNrString);
	}

	/**
	 * Find the OrderId of a LabResult
	 *
	 * @param labResultId
	 *
	 * @return the orderId of a specific result
	 */
	public static String findOrderId(String labResultId) {
		Query<LaborwerteOrderManagement> qbe = new Query<LaborwerteOrderManagement>(LaborwerteOrderManagement.class);
		qbe.add(FLD_LABRESULT_ID, Query.EQUALS, labResultId);
		List<LaborwerteOrderManagement> ret = qbe.execute();
		if (ret.size() > 0) {
			LaborwerteOrderManagement result = ret.get(0);
			return result.getOrderId();
		}
		return null;
	}

	/**
	 * Lädt den Record anhand der ID
	 *
	 * @param id Key, welcher zum Laden verwendet wird
	 */
	private LaborwerteOrderManagement(String id) {
		super(id);
	}

	/**
	 * Standard Konstruktor für die Anwendung in Queries
	 *
	 */
	public LaborwerteOrderManagement() {
	}

	/**
	 * Lädt den Record anhand der ID
	 *
	 * @param id Key, welcher zum Laden verwendet wird
	 * @return Gewünschter Record
	 */
	public static LaborwerteOrderManagement load(String id) {
		return new LaborwerteOrderManagement(id);

	}

	/**
	 * @return OrderID des Laborresultats
	 */
	public String getOrderId() {
		return get(FLD_ORDER_ID);
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
