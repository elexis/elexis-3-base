/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation, adapted from JavaAgenda
 *
 *******************************************************************************/

package ch.elexis.actions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.agenda.Messages;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.preferences.CorePreferenceInitializer;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

/**
 * Der Synchronizer synchronisiert die lokalen Daten mit dem Agenda-Server.
 * Ausserdem kann optional eine externe Agenda synchronisiert werden.
 *
 * @author gerry
 *
 */
@Deprecated
public class Synchronizer {
	private static final Logger log = LoggerFactory.getLogger(Synchronizer.class);

	JdbcLink sync;
	int lastSync;
	static boolean pingPause = false;
	private Hashtable<String, String> map;
	Activator agenda;

	Query<Patient> qPat = new Query<Patient>(Patient.class);

	/**
	 * Einen neuen Synchronizer erstellen Wenn unter agenda/sync eine gültige
	 * Verbindung besteht, und enable auf true ist, dann erfolgt bei jedem Heartbeat
	 * eine Synchronisation (ausser, wenn pingPause auf true gesetzt wurde).
	 */
	public Synchronizer() {

		if (ConfigServiceHolder.getGlobal(PreferenceConstants.AG_SYNC_ENABLED, false) == true) {
			String base = CorePreferenceInitializer.getDefaultDBPath();
			String typ = ConfigServiceHolder.getGlobal(PreferenceConstants.AG_SYNC_TYPE, "hsqldb"); //$NON-NLS-1$
			String connect = ConfigServiceHolder.getGlobal(PreferenceConstants.AG_SYNC_CONNECTOR,
					"jdbc:hsqldb:" + base + "/db"); //$NON-NLS-1$ //$NON-NLS-2$
			String dbhost = ConfigServiceHolder.getGlobal(PreferenceConstants.AG_SYNC_HOST, "localhost"); //$NON-NLS-1$

			if (typ.equalsIgnoreCase("mysql")) { //$NON-NLS-1$
				sync = JdbcLink.createMySqlLink(dbhost, connect);
			} else if (typ.equalsIgnoreCase("postgresql")) { //$NON-NLS-1$
				sync = JdbcLink.createPostgreSQLLink(dbhost, connect);
			} else if (typ.equalsIgnoreCase("odbc")) { //$NON-NLS-1$
				sync = JdbcLink.createODBCLink(dbhost);
			} else {
				sync = null;
			}
			if (sync != null) {
				if (!sync.connect(ConfigServiceHolder.getGlobal(PreferenceConstants.AG_SYNC_DBUSER, "sa"), //$NON-NLS-1$
						ConfigServiceHolder.getGlobal(PreferenceConstants.AG_SYNC_DBPWD, StringUtils.EMPTY))) {
					log.warn(Messages.Synchronizer_connctNotSuccessful + sync.lastErrorString);
					sync = null;
				} else {
					map = getBereichMapping();
				}
			}

		}
		// Sicherstellen, dass die Datenbank existiert
		Termin.load("1"); //$NON-NLS-1$
		lastSync = TimeTool.getTimeInSeconds();
	}

	/**
	 * Sync unterbrechen
	 *
	 * @param p
	 */
	static public void pause(final boolean p) {
		pingPause = p;
	}

	/**
	 * Synchronisation durchführen. Falls eine Verbidnung zu einer externen
	 * JavaAgenda besteht, werden 1. Neue Daten der externen Agenda hierher
	 * repliziert 2. Neue Daten der hiesigen Agenda auf die externe repliziert
	 *
	 * Danach wird in jedem Fall ein updateEvent gestartet, um die Anzeige der
	 * Agenda aufzufrischen
	 */
	public void doSync() {
		if (pingPause) {
			return;
		}
		pingPause = true;
		if (sync != null) {
			Stm stmOther = sync.getStatement();
			Stm stmMine = PersistentObject.getConnection().getStatement();
			PreparedStatement psInsert;
			PreparedStatement psUpdate;

			StringBuilder sql = new StringBuilder(200);
			sql.append("SELECT * FROM agnTermine WHERE deleted='0' AND Tag=") //$NON-NLS-1$
					.append(JdbcLink.wrap(agenda.getActDate().toString(TimeTool.DATE_COMPACT))).append(" AND BeiWem=") //$NON-NLS-1$
					.append(JdbcLink.wrap(map.get(agenda.getActResource())));

			try {
				// 1. Synchronisation von remote nach lokal
				ResultSet res = stmOther.query(sql.toString());
				while (res.next()) {
					int von = res.getInt("Beginn"); //$NON-NLS-1$
					int dauer = res.getInt("Dauer"); //$NON-NLS-1$
					int bis = von + dauer;
					int d = res.getInt("deleted"); //$NON-NLS-1$
					String id = res.getString("ID"); //$NON-NLS-1$
					Termin t = Termin.load(id); // Existiert dieser Termin schon
					// lokal?
					if ((t == null) || (t.state() < PersistentObject.EXISTS)) {
						if (d != 0) { // Wenn nein, ist er sowieso gelöscht,
							// dann nicht synchronisieren
							continue;
						}
						if ((t == null) || (t.state() < PersistentObject.DELETED)) {
							t = new Termin( // Sonst lokal neu erstellen
									id, agenda.getActResource(), res.getString("Tag"), von, bis, //$NON-NLS-1$
									res.getString("TerminTyp"), //$NON-NLS-1$
									res.getString("TerminStatus") //$NON-NLS-1$
							);
						}
						setTermin(t, res);
					} else { // Termin existiert schon lokal
						if (d != 0) { // remote gelöscht, dann lokal auch
							// löschen.
							t.delete();
						} else { // Sonst nur Änderungen übertragen
							int lasteditSeconds = res.getInt("lastedit"); //$NON-NLS-1$
							int lasteditMinutes = lasteditSeconds / 60;
							int my_lasteditMinutes = t.getLastedit();
							// int my_lasteditSeconds=my_lasteditMinutes*60;
							// System.out.println(t.getPersonalia()+" - "+res.getString("Personalien"));
							if (my_lasteditMinutes < lasteditMinutes) { // Wenn
								// remot
								// neuer
								// ist
								t.set(new String[] { "Tag", "Typ", "Status", "Beginn", "Dauer", "BeiWem", "lastedit" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
										new String[] { res.getString("Tag"), res.getString("TerminTyp"), //$NON-NLS-1$ //$NON-NLS-2$
												res.getString("TerminStatus"), //$NON-NLS-1$
												Integer.toString(von), Integer.toString(dauer), agenda.getActResource(),
												Integer.toString(lasteditMinutes) });
								setTermin(t, res);
							}
						}
					}

				}
				res.close();

				// 2. Synchronisation von lokal nach remote
				sql.setLength(0);
				// Lokale Termine
				sql.append("SELECT * FROM AGNTERMINE WHERE deleted='0' AND Tag=") //$NON-NLS-1$
						.append(JdbcLink.wrap(agenda.getActDate().toString(TimeTool.DATE_COMPACT)))
						.append(" AND Bereich=").append(JdbcLink.wrap(agenda.getActResource())); //$NON-NLS-1$
				res = stmMine.query(sql.toString());
				psInsert = sync.getConnection().prepareStatement(
						"INSERT INTO agnTermine (Tag, Beginn, Dauer, BeiWem, PatID, Personalien, Grund, TerminTyp, TerminStatus , Angelegt, Lastedit, ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);"); //$NON-NLS-1$
				psUpdate = sync.getConnection().prepareStatement(
						"UPDATE agnTermine SET Tag=?,Beginn=?,Dauer=?,BeiWem=?,PatID=?,Personalien=?,Grund=?,TerminTyp=?,TerminStatus=?,Angelegt=?,Lastedit=? WHERE ID=?"); //$NON-NLS-1$
				PreparedStatement ps;
				while (res.next()) {
					int myLasteditMinutes = res.getInt("lastedit"); //$NON-NLS-1$
					int myLasteditSeconds = myLasteditMinutes * 60;
					int otherLastSeconds = stmOther
							.queryInt("SELECT lastedit FROM agnTermine WHERE ID='" + res.getString("ID") + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					int otherLastMinutes = otherLastSeconds / 60;
					if (otherLastSeconds == -1) { // Termin existiert remote
						// noch nicht -> erstellen
						ps = psInsert;
						ps.setInt(10, Integer.parseInt(Termin.createTimeStamp()));
					} else { // Termin existiert remote schon -> ggf. updaten
						if (myLasteditMinutes <= (otherLastMinutes)) {
							continue;
						}
						ps = psUpdate;
						ps.setInt(10, res.getInt("Angelegt")); //$NON-NLS-1$
					}
					String mandant = map.get(res.getString("Bereich")); //$NON-NLS-1$
					if (mandant == null) {
						continue;
					}
					ps.setString(1, res.getString("Tag")); //$NON-NLS-1$
					ps.setInt(2, res.getInt("Beginn")); //$NON-NLS-1$
					ps.setInt(3, res.getInt("Dauer")); //$NON-NLS-1$
					ps.setString(4, mandant);
					String pers = res.getString("PatID"); //$NON-NLS-1$
					Patient pat = Patient.load(pers);
					int nr = 0;
					if (pat.state() > PersistentObject.INVALID_ID) {
						nr = Integer.parseInt(pat.getPatCode());
						pers = pat.getLabel();
					}
					ps.setInt(5, nr);
					ps.setString(6, pers);
					ps.setString(7, res.getString("Grund")); //$NON-NLS-1$
					ps.setString(8, res.getString("TerminTyp")); //$NON-NLS-1$
					ps.setString(9, res.getString("TerminStatus")); //$NON-NLS-1$
					ps.setInt(11, myLasteditSeconds);
					ps.setString(12, res.getString("ID")); //$NON-NLS-1$
					ps.execute();
				}

			} catch (Exception ex) {
				ExHandler.handle(ex);
			} finally {
				sync.releaseStatement(stmOther);
				PersistentObject.getConnection().releaseStatement(stmMine);
				pingPause = false;
			}
		}
		lastSync = TimeTool.getTimeInSeconds();
		pingPause = false;
		ElexisEventDispatcher.reload(Termin.class);
	}

	private void setTermin(final Termin t, final ResultSet res) throws SQLException {
		t.set("Grund", res.getString("Grund")); //$NON-NLS-1$ //$NON-NLS-2$
		String pers = res.getString("Personalien"); //$NON-NLS-1$
		String[] px = Termin.findID(pers);
		px[1] = px[1].replaceFirst("\\([mw]\\)", StringUtils.EMPTY); //$NON-NLS-1$
		qPat.clear();
		List<Patient> list = qPat.queryFields(new String[] { "Name", "Vorname", "Geburtsdatum" }, px, true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if ((list == null) || (list.size() != 1)) {
			t.set("Wer", pers); //$NON-NLS-1$
		} else {
			t.set("Wer", ((PersistentObject) list.get(0)).getId()); //$NON-NLS-1$
		}
	}

	@SuppressWarnings("unchecked")
	public static Hashtable<String, String> getBereichMapping() {
		Hashtable<String, String> ret = StringTool
				.foldStrings(ConfigServiceHolder.getGlobal(PreferenceConstants.AG_SYNC_MAPPING, null));
		if (ret == null) {
			ret = new Hashtable<String, String>();
		}
		return ret;
	}

	public static void setBereichMapping(final Hashtable<String, String> map) {
		ConfigServiceHolder.setGlobal(PreferenceConstants.AG_SYNC_MAPPING, StringTool.flattenStrings(map));
	}

}
