/*******************************************************************************
 * Copyright (c) 2007-2014, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     D. Lutz - initial API and implementation
 *     N. Giger - Moved KonsTextLock to separate file
 * 
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.data;

import org.iatrix.views.JournalView;

import ch.elexis.data.Anwender;
import ch.elexis.data.Konsultation;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.StringTool;

/**
 * Handle locking of kons text to avoid access of multiple users/instances at the same time
 * 
 * @author danlutz
 */
public class KonsTextLock {
	// unique value for this instance
	String identifier = null;
	String konsultationId = null;
	String userId = null;
	String key = null;

	// constructor
	public KonsTextLock(Konsultation konsultation, Anwender user){
		identifier = StringTool.unique(JournalView.ID);

		konsultationId = konsultation.getId();
		userId = user.getId();

		// create key name
		StringBuffer sb = new StringBuffer();
		sb.append(JournalView.ID).append("_").append("konslock").append("_").append(konsultationId);

		key = sb.toString();
	}

	public Value getLockValue(){
		String lockValue =
			PersistentObject.getConnection().queryString(
				"SELECT wert from CONFIG WHERE param = " + JdbcLink.wrap(key));
		return new Value(lockValue);
	}

	// taken from PersistentObject
	// return true if lock is ok, false else
	// may also be called to update a lock
	// a lock older than 2 hours is considered as outdated
	// therefore, a lock must regularly be updated
	public synchronized boolean lock(){
		Stm stm = PersistentObject.getConnection().getStatement();

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
				return true;
			}

			long locktime = lockValue.getTimestamp();
			long age = now - locktime;
			if (age > 2 * 1000L * 3600L) { // Älter als zwei Stunden -> Löschen
				// System.err.println("DEBUG:   lock() giving up: age = " + age);
				stm.exec("DELETE FROM CONFIG WHERE param=" + JdbcLink.wrap(key));
			} else {
				PersistentObject.getConnection().releaseStatement(stm);
				return false;
			}
		}
		// Neues Lock erstellen
		Value lockValue = new Value(userId, identifier);
		// System.err.println("DEBUG:   lock() insert: time = " + lockValue.getTimestamp());
		String lockstring = lockValue.getLockString();
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO CONFIG (param,wert) VALUES (").append(JdbcLink.wrap(key))
			.append(",").append("'").append(lockstring).append("')");
		stm.exec(sb.toString());
		// Prüfen, ob wir es wirklich haben, oder ob doch jemand anders schneller war.
		String check =
			stm.queryString("SELECT wert FROM CONFIG WHERE param=" + JdbcLink.wrap(key));
		if (check == null || !check.equals(lockstring)) {
			// lock doesn't exist or has wrong value
			PersistentObject.getConnection().releaseStatement(stm);
			return false;
		}

		PersistentObject.getConnection().releaseStatement(stm);
		return true;
	}

	/**
	 * Check wheter we own the lock
	 * 
	 * @return true if we own the lock, false otherwise
	 */
	public synchronized boolean isLocked(){
		Value lockValue = getLockValue();
		return (identifier.equals(lockValue.getIdentifier()));
	}

	// taken from PersistentObject
	public synchronized boolean unlock(){
		String lock =
			PersistentObject.getConnection().queryString(
				"SELECT wert from CONFIG WHERE param=" + JdbcLink.wrap(key));
		if (StringTool.isNothing(lock)) {
			return false;
		}

		Value lockValue = new Value(lock);
		String lockIdentifier = lockValue.getIdentifier();
		if (lockIdentifier != null && lockIdentifier.equals(identifier)) {
			PersistentObject.getConnection().exec(
				"DELETE FROM CONFIG WHERE param=" + JdbcLink.wrap(key));
			return true;
		}

		return false;
	}

	public class Value {
		private long timestamp = 0;
		private String userId = null;
		private String identifier = null;

		Value(String lockValue){
			// format: <timestamp>#<userid>#<identifier>

			if (lockValue != null) {
				String[] tokens = lockValue.split("#");
				if (tokens.length == 3) {
					try {
						timestamp = Long.parseLong(tokens[0]);
					} catch (NumberFormatException ex) {
						return;
					}

					userId = tokens[1];
					identifier = tokens[2];
				}
			}
		}

		Value(String userId, String identifier){
			timestamp = System.currentTimeMillis();

			this.userId = userId;
			this.identifier = identifier;
		}

		String getLockString(){
			StringBuffer sb = new StringBuffer();
			sb.append(new Long(timestamp).toString()).append("#").append(userId).append("#")
				.append(identifier);

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

		String getIdentifier(){
			return identifier;
		}
	}
}