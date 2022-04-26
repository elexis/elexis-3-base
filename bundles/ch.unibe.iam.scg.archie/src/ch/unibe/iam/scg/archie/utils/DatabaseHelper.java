/*******************************************************************************
 * Copyright (c) 2008-2011 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *******************************************************************************/
package ch.unibe.iam.scg.archie.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import ch.elexis.core.ui.util.Log;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.unibe.iam.scg.archie.ArchieActivator;

/**
 * <p>
 * Database helper class. Contains global database convenience methods for easy
 * access to general statistical data.
 * </p>
 *
 * $Id: DatabaseHelper.java 781 2011-08-30 04:50:37Z gerry.weirich@gmail.com $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 781 $
 */
public class DatabaseHelper {

	/**
	 * Returns the total number of patients in the system.
	 *
	 * @return int number of patients in the system.
	 */
	public static int getNumberOfPatients() {
		return DatabaseHelper.getTotalFromQuery(
				"SELECT COUNT(ID) AS total FROM KONTAKT WHERE istPatient = '1' AND deleted = '0'", "total");
	}

	/**
	 * Returns the total number of consultations in the system.
	 *
	 * @return int number of consultations in the system.
	 */
	public static int getNumberOfConsultations() {
		return DatabaseHelper.getTotalFromQuery("SELECT COUNT(ID) AS total FROM BEHANDLUNGEN WHERE deleted = '0'",
				"total");
	}

	/**
	 * Returns the total number of invoices in the system.
	 *
	 * @return Total number of invoices in the system
	 */
	public static int getTotalNumberOfInvoices() {
		return DatabaseHelper.getTotalFromQuery("SELECT COUNT(ID) AS total FROM RECHNUNGEN where deleted = '0'",
				"total");
	}

	/**
	 * Returns the number of invoices in the system with the given status.
	 *
	 * @param status Invoice status.
	 * @return Total number of invoices with the given status.
	 * @see ch.elexis.data.Rechnung
	 */
	public static int getNumberOfInvoices(int status) {
		return DatabaseHelper.getTotalFromQuery(
				"SELECT COUNT(id) AS total FROM RECHNUNGEN WHERE deleted = '0' AND RnStatus = '" + status + "'",
				"total");
	}

	/**
	 * Return the number of patients in the system that have the given gender.
	 *
	 * @param gender Patient's gender.
	 * @see ch.elexis.data.Person
	 * @return Number of patients with that gender, 0 if nothing found.
	 */
	public static int getNumberGenderPatients(String gender) {
		// Checking Preconditions.
		if (!(gender.equals(Patient.MALE) || gender.equals(Patient.FEMALE))) {
			throw new IllegalArgumentException(
					"Gender has to be either " + Patient.MALE + " or " + Patient.FEMALE + ".");
		}
		JdbcLink link = PersistentObject.getConnection();
		Stm statement = link.getStatement();
		ResultSet result = statement
				.query("SELECT Geschlecht, COUNT(ID) AS total FROM KONTAKT WHERE istPatient = '1' AND geschlecht = '"
						+ gender + "' AND deleted = '0' GROUP BY Geschlecht");
		try {
			while (result != null && result.next()) {
				return result.getInt("total");
			}
		} catch (SQLException e) {
			ArchieActivator.LOG.log("Error while trying to data from database.\n" + e.getLocalizedMessage(),
					Log.WARNINGS);
			e.printStackTrace();
		} finally {
			link.releaseStatement(statement);
		}
		return 0;
	}

	/**
	 * Returns the int result from the given total column based on the given query.
	 *
	 * @param query       An SQL query.
	 * @param totalColumn The column name to retrieve the value from.
	 * @return The value of the total column or 0 if something went wrong.
	 * @since 0.9.2
	 */
	private static int getTotalFromQuery(String query, String totalColumn) {
		JdbcLink link = PersistentObject.getConnection();
		Stm statement = link.getStatement();
		ResultSet result = statement.query(query);

		try {
			if (result != null && result.next()) {
				return result.getInt(totalColumn);
			}
		} catch (SQLException e) {
			ArchieActivator.LOG.log("Error while trying to data from database.\n" + e.getLocalizedMessage(),
					Log.WARNINGS);
			e.printStackTrace();
		} finally {
			link.releaseStatement(statement);
		}
		return 0;
	}
}
