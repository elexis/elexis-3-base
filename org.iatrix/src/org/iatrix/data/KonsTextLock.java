/*******************************************************************************
 * Copyright (c) 2007-2014, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *     N. Giger - Moved KonsTextLock to separate file, fixed lock for Elexis 3.0
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.iatrix.Iatrix;
import org.iatrix.views.JournalView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.util.CreatePrescriptionHelper;
import ch.elexis.data.Anwender;
import ch.elexis.data.Konsultation;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.JdbcLinkException;
import ch.rgw.tools.StringTool;

/**
 * Handle locking of kons text to avoid access of multiple users/instances at the same time
 *
 * @author danlutz
 */
public class KonsTextLock {
	// unique value for this instance
	private final String identifier;
	String konsultationId = null;
	String userId = null;
	String key = null;
	String label = null;
	boolean lockVar = false; // used only if CFG_USE_KONSTEXT_LOCKING is true
	boolean traceEnabled = false;
	boolean useDatabaseLock = false;
	private static final long MAX_AGE_2_HOURS = 2 * 1000L * 3600L; // 2 hours in milliseconds
	private final static Logger logger = LoggerFactory.getLogger("org.iatrix.KonsTextLock");

	public static void deleteObsoleteLocks(Konsultation konsultation) {
		int nrFound = 0;
		String sql = "SELECT param, wert FROM CONFIG WHERE param like '%_konslock_%'";
		ResultSet values = null;
		PreparedStatement pstm = PersistentObject.getDefaultConnection()
				.getPreparedStatement(sql);
		try {
			values = pstm.executeQuery();
			long now = System.currentTimeMillis();
			StringBuilder delStm = new StringBuilder("delete from config where param in (");
			while (values.next()) {
				String key = values.getString(1);
				String oldlock = values.getString(2);
				long locktime = 0;
				String[] tokens = oldlock.split("#");
				if (tokens.length >= 1) {
					try {
						locktime = Long.parseLong(tokens[0]);
					} catch (NumberFormatException ex) {
						break;
					}
				}
				long age = now - locktime;
				String msg = oldlock + " " + locktime + " from " + new Date(locktime).toString();
				logger.trace(msg);
				if (age > MAX_AGE_2_HOURS) { // Älter als zwei Stunden -> Löschen
					if (nrFound == 0) {
						delStm.append(" '" + key + "' ");
					} else {
						delStm.append(", '" + key + "' ");
					}
					nrFound++;
				}
			}
			if (nrFound> 0 ) {
				logger.info("Deleting " + nrFound + " obsolete _konslock_ from config");
				delStm.append(")");
				Stm stm = PersistentObject.getDefaultConnection().getStatement();
				stm.exec(delStm.toString());
				PersistentObject.getDefaultConnection().releaseStatement(stm);
			}
		} catch (SQLException | JdbcLinkException e) {
			logger.info(" Error deleting " + e );
			LoggerFactory.getLogger(CreatePrescriptionHelper.class)
				.error("Could not find any konslock", e);
		} finally {
			PersistentObject.getDefaultConnection().releasePreparedStatement(pstm);
		}
	}

	/*
	 * Trace, but prepend some common information
	 */
	private void trace(String msg){
		if (traceEnabled)
			logger.info(msg + key + " u: " + userId );
	}
	// constructor
	public KonsTextLock(Konsultation konsultation, Anwender user){
		identifier = StringTool.unique(JournalView.ID);
		traceEnabled =
			CoreHub.userCfg.get(Iatrix.CFG_USE_KONSTEXT_TRACE,
				Iatrix.CFG_USE_KONSTEXT_TRACE_DEFAULT);
		useDatabaseLock =
			CoreHub.userCfg.get(Iatrix.CFG_USE_KONSTEXT_LOCKING,
				Iatrix.CFG_USE_KONSTEXT_LOCKING_DEFAULT);

		konsultationId = konsultation.getId();
		userId = user.getId();

		// create key name
		StringBuffer sb = new StringBuffer();
		sb.append(JournalView.ID).append("_").append("konslock").append("_").append(konsultationId);
		key = sb.toString();
		trace("new KonsTextLock: ");
		lockVar = false;
	}

	public Value getLockValue(){
		String lockValue =
			PersistentObject.getDefaultConnection().queryString(
				"SELECT wert from CONFIG WHERE param = " + JdbcLink.wrap(key));
		return new Value(lockValue);
	}

	/**
	 * getKey
	 * @return the key for the Kons (user and kons.getId())
	 */
	public String getKey() {
		return key;
	}

	/**
	 * getKey
	 * @return human readable representation
	 */
	public String getLabel() {
		return label;
	}


	// taken from PersistentObject
	// return true if lock is ok, false else
	// may also be called to update a lock
	// a lock older than 2 hours is considered as outdated
	// therefore, a lock must regularly be updated
	public synchronized boolean lock(){
		if (useDatabaseLock == false) {
			lockVar = true;
			return lockVar;
		}
		trace("lock: start ");
		Stm stm = PersistentObject.getDefaultConnection().getStatement();
		try {
			long now = System.currentTimeMillis();
			// Gibt es das angeforderte Lock schon?
			String oldlock =
				stm.queryString("SELECT wert FROM CONFIG WHERE param=" + JdbcLink.wrap(key));
			if (!StringTool.isNothing(oldlock)) {
				// Ja, wie alt ist es?
				Value lockValue = new Value(oldlock);
				String lockIdentifier = lockValue.getIdentifier();
				if (lockIdentifier != null && lockIdentifier.equals(identifier)) {
					// it's our own lock. update timestamp
					lockValue = new Value(userId, identifier);
					// System.err.println("DEBUG:   lock() update: time = " +
					// lockValue.getTimestamp());
					String lockstring = lockValue.getLockString();
					StringBuilder sb = new StringBuilder();
					sb.append("UPDATE CONFIG SET wert = ").append("'" + lockstring + "'")
						.append(" WHERE param = ").append(JdbcLink.wrap(key));
					stm.exec(sb.toString());
					trace("lock: okay ");
					return true;
				}

				long locktime = lockValue.getTimestamp();
				long age = now - locktime;
				if (age > MAX_AGE_2_HOURS) { // Älter als zwei Stunden -> Löschen
					// System.err.println("DEBUG:   lock() giving up: age = " + age);
					stm.exec("DELETE FROM CONFIG WHERE param=" + JdbcLink.wrap(key));
				} else {
					label = key + " " + oldlock + " " + locktime + " from " + new Date(locktime).toString();
					trace("lock: failed " + label);
					return false;
				}
			}
			// Neues Lock erstellen
			Value lockValue = new Value(userId, identifier);
			long timestamp = lockValue.getTimestamp();
			label = lockValue.hostName + "_" + userId + " " + timestamp + " from " + new Date(timestamp).toString();

			// System.err.println("DEBUG:   lock() insert: time = " + lockValue.getTimestamp());
			String lockstring = lockValue.getLockString();
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO CONFIG (param, wert) VALUES (").append(JdbcLink.wrap(key))
				.append(",").append("'").append(lockstring).append("')");
			stm.exec(sb.toString());
			// Prüfen, ob wir es wirklich haben, oder ob doch jemand anders schneller war.
			String check =
				stm.queryString("SELECT wert FROM CONFIG WHERE param=" + JdbcLink.wrap(key));
			if (check == null || !check.equals(lockstring)) {
				// lock doesn't exist or has wrong value
				trace("lock: failed ");
				return false;
			}
			trace("lock: okay " + label);
			return true;
		} finally {
			PersistentObject.getDefaultConnection().releaseStatement(stm);
		}
	}

	/**
	 * Check wheter we own the lock
	 *
	 * @return true if we own the lock, false otherwise
	 */
	public synchronized boolean isLocked(){
		if (useDatabaseLock == false) {
			return lockVar;
		}
		Value lockValue = getLockValue();
		return (identifier.equals(lockValue.getIdentifier()));
	}

	// taken from PersistentObject
	public synchronized boolean unlock(){
		if (useDatabaseLock == false) {
			lockVar = false;
			return lockVar;
		}
		trace("unlock: start ");
		String lock =
			PersistentObject.getDefaultConnection().queryString(
				"SELECT wert from CONFIG WHERE param=" + JdbcLink.wrap(key));
		if (StringTool.isNothing(lock)) {
			trace("unlock: failed ");
			return false;
		}

		Value lockValue = new Value(lock);
		String lockIdentifier = lockValue.getIdentifier();
		if (lockIdentifier != null && lockIdentifier.equals(identifier)) {
			PersistentObject.getDefaultConnection().exec(
				"DELETE FROM CONFIG WHERE param=" + JdbcLink.wrap(key));
			trace("unlock: okay ");
			return true;
		}

		if (traceEnabled)
			trace("unlock: failed ");
		return false;
	}

	public class Value {
		private long timestamp = 0;
		private String userId = null;
		private String identifier = null;
		private String hostName = null;
		Value(String lockValue){
			// format: <timestamp>#<userid>#<identifier>#<hostname>

			if (lockValue != null) {
				String[] tokens = lockValue.split("#");
				if (tokens.length >= 3) {
					try {
						timestamp = Long.parseLong(tokens[0]);
					} catch (NumberFormatException ex) {
						return;
					}

					userId = tokens[1];
					identifier = tokens[2];
					// Allow interaction with old 2.1.7 and older 3.0
					if (tokens.length >= 4) {
						hostName = tokens[3];
					}
				}
			}
		}

		Value(String userId, String identifier){
			timestamp = System.currentTimeMillis();

			this.userId = userId;
			this.identifier = identifier;
			try {
				this.hostName =  InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				this.hostName = null;
			}
		}

		String getLockString(){
			StringBuffer sb = new StringBuffer();
			sb.append(new Long(timestamp).toString()).append("#").append(userId).append("#")
				.append(identifier).append("#").append(hostName);

			return sb.toString();
		}

		long getTimestamp(){
			return timestamp;
		}

		/**
		 * Return the Anwender owning this lock
		 *
		 * @return Anwender owning the lock, or null if not found
		 */
		public Anwender getUser(){
			return Anwender.load(userId);
		}

		/**
		 * Return the hostname owning this lock
		 *
		 * @return hostowning the lock, or null if not found
		 */
		public String getHost(){
			return hostName;
		}

		String getIdentifier(){
			return identifier;
		}
		
	}
}