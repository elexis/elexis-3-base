/*******************************************************************************
 * Copyright (c) 2005-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/

package ch.elexis.befunde;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;

import ch.elexis.base.befunde.ACLContributor;
import ch.elexis.base.befunde.Messages;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.TimeTool;

/**
 * Here we define our own data type for our "measurements"-Plugin. The Type is
 * derived from ch.elexis.data.PersistentObject, and thereby the management of
 * the object persistence is completely delegated to elexis. The Method
 * getSetup() creates (if necessary) a new table for our type. Some methods are
 * required for every class derived from PersistentObject to work properly.
 * Those methods are hereafter marked with -required-
 *
 * Hier wird ein eigener Datentyp für unser "Messwerte"-Plugin definiert. Der
 * Datentyp wird von PersistentObject abgeleitet, was das Persistenzmanagement
 * an Elexis delegiert. In der Methode getSetup() wird wenn nötig eine neue
 * Tabelle zur Speicherung der Daten dieses Datentyps erstellt. Einige Methoden
 * _müssen_ vorhanden sein, damit eine von PersistentObject abgeleitete Klasse
 * korrekt funktioniert. Diese sind im Folgenden mit -Zwingend- markiert.
 *
 * @author Gerry
 *
 */
public class Messwert extends PersistentObject {
	public static final String HASH_NAMES = "names"; //$NON-NLS-1$
	public static final String _FIELDS = "_FIELDS"; //$NON-NLS-1$
	public static final String FLD_BEFUNDE = "Befunde"; //$NON-NLS-1$
	public static final String FLD_NAME = "Name"; //$NON-NLS-1$
	public static final String FLD_PATIENT_ID = "PatientID"; //$NON-NLS-1$
	public static final int VERSION = 4;
	public static final String PLUGIN_ID = "ch.elexis.befunde"; //$NON-NLS-1$
	public static final String SETUP_SEPARATOR = ";;"; //$NON-NLS-1$
	public static final String SETUP_CHECKSEPARATOR = ":/:"; //$NON-NLS-1$
	/**
	 * Name of the table. By convention, every tablename has to have its plugin ID
	 * as prefix, to avoid naming conflicts. Unfortunately, this plugin was created
	 * before that convention was declared and therefore the tablename ist not quite
	 * correct. We leave it for compatibility reasons. If this plugin was made
	 * today, the name of the table ought to be something like
	 * CH_ELEXIS_BEFUNDE_MESSWERTE
	 *
	 * Name der Tabelle. Die Konvention sieht jetzt vor, dass jede Tabelle mit der
	 * Präfix ihres Plugins benannt werden muss, um Namenskonflikte zu vermeiden.
	 * Wir belassen hier jetzt dennoch diesen eigentlich unerwünschten Namen, um
	 * existierende Datenbanken nicht zu zerstören. Würde dieses Plugin jetzt neu
	 * erstellt, müsste seine Tabelle heissen: CH_ELEXIS_BEFUNDE_MESSWERTE (oder so
	 * ähnlich)
	 */
	private static final String TABLENAME = "ELEXISBEFUNDE"; //$NON-NLS-1$

	/**
	 * -required- Here we define the mapping of table fields to class members. The
	 * first String has always to be the name of the table, the other Strings are
	 * table fields, that we need as members. This mapping makes ist possible to
	 * access every declared field simply by e.g. get("Name") and
	 * set("Name","blabla") instead of having to fiddle around with SQL-statements.
	 * Elexis convention encourrages every plugin developer strongly to use this
	 * mechanism, because this ensures also the ability of elexis to run without
	 * change on different database engines.
	 *
	 * The declaration of a mapping can be a simple String (as below in "Name"). In
	 * that case, the name of the field is identical to the name of the member. It
	 * could as well be a modification of the form "Name=TBL_USR" In that case, the
	 * member "Name" would be mapped to the table field "TBL_USR". And the
	 * declaration can also be of the form "Datum=S:D:Datum". In that case,
	 * PersistentObject modifies the field before passing it to the database. In
	 * this case, every Date will be converted to the form yyyymmdd
	 * (TimeTool.DATE_COMPACT). Other modifyers include compression of fields etc.
	 * See documentation of PersistentObject.
	 *
	 * -Zwingend- Hier wird die Zuordnung von Tabellenfeldern zu Klassenmembern
	 * definiert. Der erste String ist der Name der Tabelle, die weiteren Strings
	 * sind Tabellenfelder, die als Member benötigt werden. Diese können entweder
	 * als einfaches Wort aufgeführt werden, dann wird der Member genauso heissen,
	 * wie das Tabellenfeld, und die Daten aus der Tabelle werden als einfacher Text
	 * interpretiert. Oder es kann ein String der Form Name=Feld eingesetzt werden,
	 * dann kann der Member anders heissen, als das Feld, und der Feldinhalt kann
	 * beim Lesen udn Schreiben in bestimmter Weise umgeformt werden (Als Datum
	 * interpretiert, Komprimiert etc). Näheres dazu in der Dokumenation von
	 * PersistentObject.
	 */
	static {
		addMapping(TABLENAME, FLD_PATIENT_ID, FLD_NAME, DATE_COMPOUND, FLD_BEFUNDE);
	}

	/**
	 * This is the only public constructor. The parameterless constructor should
	 * never be used, as it does not create the object in the database. The
	 * constructor with a single string parameter should never be called from
	 * outside the class itself.
	 *
	 * Dies ist der einzige öffentliche Konstruktor. Der parameterlose Konstruktor
	 * sollte nie verwendet werden, da er das Objekt nicht persistiert, und der
	 * Konstruktor mit einem einzelnen String Element sollte nie direkt aufgerufen
	 * werden (s. dort)
	 *
	 * @param pat  Der Patient, dem dieser Messwert zugeordnet werden soll
	 * @param name Name des Messwerts
	 * @param date Datum des Messwerts
	 * @param bf   Der Messwert in beliebige komplexer Form, wird als Black Box
	 *             betrachtet
	 */
	public Messwert(Patient pat, String name, String date, Map bf) {
		create(null);
		set(new String[] { FLD_PATIENT_ID, FLD_NAME, PersistentObject.FLD_DATE }, pat.getId(), name, date); // $NON-NLS-1$
																											// //$NON-NLS-2$
																											// //$NON-NLS-3$
		setMap(FLD_BEFUNDE, bf); // $NON-NLS-1$
	}

	public String getDate() {
		return get(FLD_DATE);
	}

	public void setDate(Date date) {
		TimeTool tt = new TimeTool();
		tt.setTime(date);
		set(FLD_DATE, tt.toString(TimeTool.DATE_COMPACT));
		clearCache();
	}

	/**
	 * a concise, human readable indentification for the measurement. Subclasses
	 * should always override this, because the abse implementation of
	 * PersistentObject gives only an empty string.
	 *
	 * Eine kurze menschenlesbare Identifikation für den Messwert liefern. Sollte
	 * überschrieben werden; die Standardimplementation von PersistentObject liefert
	 * einfach einen Leerstring zurück.
	 */
	@Override
	public String getLabel() {
		return get(FLD_NAME); // $NON-NLS-1$
	}

	@SuppressWarnings("unchecked")
	public String getResult(String field) {
		Map<String, String> hash = getMap(FLD_BEFUNDE);
		return hash.get(field);
	}

	/**
	 * -required- Name of the table, where objects of this class should be
	 * persisted. See remarks above near the field TABLENAME
	 *
	 * -Zwingend- Name der Tabelle, in der Objekte dieser Klasse persistiert werden
	 * sollen
	 */
	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	/**
	 * Here are configuration details read. If reading fails, the method assumes,
	 * that the table does not exist yet an creates it.
	 *
	 *
	 * Hier werden Konfigurationseinzelheiten eingelesen. Wenn das Lesen
	 * fehlschlägt, nimmt die Methode an, dass die Tabelle noch nicht existiert und
	 * legt sie neu an.
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Messwert getSetup() {
		JdbcLink j = getConnection();
		Messwert setup = new Messwert("__SETUP__"); //$NON-NLS-1$

		if (!setup.exists()) {
			try {
				if (!PersistentObject.tableExists(TABLENAME)) {
					ByteArrayInputStream bais = new ByteArrayInputStream(create.getBytes("UTF-8")); //$NON-NLS-1$
					if (j.execScript(bais, true, false) == false) {
						MessageDialog.openError(null, Messages.Messwert_valuesError,
								Messages.Messwert_couldNotCreateTable); // $NON-NLS-1$ //$NON-NLS-2$
						return null;
					}
				} else {
					ByteArrayInputStream bais = new ByteArrayInputStream(insertSetup.getBytes("UTF-8")); //$NON-NLS-1$
					if (j.execScript(bais, true, false) == false) {
						MessageDialog.openError(null, Messages.Messwert_valuesError,
								Messages.Messwert_couldNotCreateTable); // $NON-NLS-1$ //$NON-NLS-2$
						return null;
					}
				}
				Map names = setup.getMap(FLD_BEFUNDE);
				names.put("VERSION", Integer.toString(VERSION)); //$NON-NLS-1$
				setup.setMap(FLD_BEFUNDE, names);

				new ACLContributor().initializeDefaults(CoreHub.acl);
			} catch (Exception ex) {
				ExHandler.handle(ex);
			}
		} else {
			// Update from earlier format if necessary
			Map names = setup.getMap(FLD_BEFUNDE);
			String v = (String) names.get("VERSION"); //$NON-NLS-1$
			if (v == null || Integer.parseInt(v) < VERSION) {
				if (Integer.parseInt(v) < 4) {
					createOrModifyTable("ALTER TABLE " + TABLENAME + " ADD lastupdate BIGINT;"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				if (Integer.parseInt(v) < 3) {
					if (j.DBFlavor.equalsIgnoreCase("postgresql")) { //$NON-NLS-1$
						j.exec("ALTER TABLE " + TABLENAME + " ALTER Name TYPE VARCHAR(80);"); //$NON-NLS-1$ //$NON-NLS-2$
					} else if (j.DBFlavor.equalsIgnoreCase("mysql")) { //$NON-NLS-1$
						j.exec("ALTER TABLE " + TABLENAME + " MODIFY Name VARCHAR(80);"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				if (Integer.parseInt(v) < 2) { // version 1 auf 2
					j.exec("ALTER TABLE " + TABLENAME + " ADD deleted CHAR(1) default '0';"); //$NON-NLS-1$ //$NON-NLS-2$
				} else { // version 0 auf 1
					StringBuilder titles = new StringBuilder();
					Map.Entry[] entryset = (Map.Entry[]) names.entrySet().toArray(new Map.Entry[0]);
					for (Map.Entry entry : entryset) {
						String param = (String) entry.getKey();
						if (param.equals(HASH_NAMES) || param.equals("VERSION") //$NON-NLS-1$
								|| param.matches(".+_FIELDS")) { //$NON-NLS-1$
							continue;
						}
						titles.append(param).append(SETUP_SEPARATOR);
						String vals = (String) entry.getValue();
						StringBuilder flds = new StringBuilder();
						for (String s : vals.split(",")) { //$NON-NLS-1$
							flds.append(s).append(SETUP_CHECKSEPARATOR).append("s").append( //$NON-NLS-1$
									SETUP_SEPARATOR);
						}
						if (flds.length() > SETUP_CHECKSEPARATOR.length()) {
							flds.setLength(flds.length() - SETUP_CHECKSEPARATOR.length());
							names.put(param + _FIELDS, flds.toString());
						}
					}
					if (titles.length() > SETUP_SEPARATOR.length()) {
						titles.setLength(titles.length() - SETUP_SEPARATOR.length());
						names.put(HASH_NAMES, titles.toString());
					}
				}
				names.put("VERSION", Integer.toString(VERSION)); //$NON-NLS-1$
				setup.setMap(FLD_BEFUNDE, names);
			}
		}
		return setup;
	}

	/**
	 * -required- This is the standard method to construct a PersistentObject from
	 * its representation in the database. For semantic reasons we intenionally do
	 * not use the "new" constructor for that purpose. "New" should only be used to
	 * create really "new" Objects and not to load existing objects from the
	 * database. Internally, however, load simply calls new(String). The static
	 * method load(String) must exist in every sublclass of PersistentObject, but it
	 * can always be written just exacly as here. The method needs not to guarantee
	 * that the returned object exists. It can, if desired, return a null value to
	 * indicate a inexistent object. Here we return just whatever the superclass
	 * gives us.
	 *
	 * -Zwingend- Dies ist die Standardmethode zum Konstruieren eines
	 * PersistentObjects aus der Datenbank. Aus semantischen Gründen wurde hierfür
	 * bewusst nicht der "new" Konstruktor verwendet. "New" bleibt gegen aussen für
	 * die Erstellung "neuer" Objekte reserviert. Intern allerdings ruft load
	 * einfach new(String) auf. Das kann immer exakt so formuliert werden.
	 *
	 * @param id ID des zu ladenden Objektes
	 * @return Das Objekt (kann auch null oder inexistent sein)
	 */
	public static Messwert load(String id) {
		return new Messwert(id);
	}

	/**
	 * The empty constructor is only needed by the factory and should never be
	 * public.
	 *
	 * Der parameterlose Konstruktor wird nur von der Factory gebraucht und sollte
	 * nie public sein.
	 */
	protected Messwert() {
	}

	/**
	 * The constructror with a single String is used to load objects from the
	 * database and should never be called directly. For that purpose is instead the
	 * static method load() to be used. This constructor should always be defined
	 * exactly the same way as shown below. It loads the object "lazily", what means
	 * that an access to the database will not occur until a member of the object is
	 * needed. This means, that the constructor will always succeed, even if the
	 * accessed object does not exist or is not valid. The caller could check this
	 * with exists() or isValid(), but this would mean an (in most cases
	 * unneccessary= database access.
	 *
	 * Der Konstruktor mit einem String dient dem Einlesen von Objekten aus der
	 * Datenbank und sollte nie direkt aufgerufen werden. Hierfür dient die
	 * statische Methode load(). Dieser Konstruktor sollte immer genauso definiert
	 * werden, wie hier gezeigt. Er lädt das Objekt "lazy", das heisst, ein
	 * tatsächlicher Zugriff auf die Datenbank erfolgt erst dann, wenn ein Member
	 * des Objekts benötigt wird. Dies bedeutet aber auch, dass der Konstruktor
	 * scheinbar erfolgreich war, das Objekt das er zurückliefert, aber nicht
	 * existieren oder nicht gültig sein muss. Dies kann mit isValid() bzw. exists()
	 * geprüft werden (Was allerdings einen meist unnötigen Datenbankzugriff
	 * bewirkt)
	 *
	 * @param id
	 */
	protected Messwert(String id) {
		super(id);
	}

	/**
	 * Definition of the table in for of a create-script for JdbcLink
	 *
	 * Definition de Tabelle in Form eines JdbcLink-Create-Scripts
	 */
	private static final String create = "CREATE TABLE " + TABLENAME + " (" + //$NON-NLS-1$ //$NON-NLS-2$
			"ID			VARCHAR(25) primary key," + //$NON-NLS-1$
			"lastupdate BIGINT," + "deleted	CHAR(1) default '0'," + //$NON-NLS-1$ //$NON-NLS-2$
			"PatientID	VARCHAR(25)," + //$NON-NLS-1$
			"Name		VARCHAR(80)," + //$NON-NLS-1$
			"Datum		CHAR(8)," + //$NON-NLS-1$
			"Befunde 	BLOB" + //$NON-NLS-1$
			");" + //$NON-NLS-1$
			"create index idx_elbf1 on " + TABLENAME + "(Datum);" + //$NON-NLS-1$ //$NON-NLS-2$
			"create index idx_elbf2 on " + TABLENAME + "(PatientID);" + //$NON-NLS-1$ //$NON-NLS-2$
			"insert into " + TABLENAME + " (ID) values ('__SETUP__');"; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String insertSetup = "insert into " + TABLENAME + " (ID) values ('__SETUP__');"; //$NON-NLS-1$ //$NON-NLS-2$
}
