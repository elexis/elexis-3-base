/*******************************************************************************
 * Copyright (c) 2011, Christian Gruber and MEDEVIT OG
 * All rights reserved.
 *******************************************************************************/
package at.gruber.elexis.mythic22.persistency;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gruber.elexis.mythic22.Activator;
import at.gruber.elexis.mythic22.model.HaematologicalValue;
import at.gruber.elexis.mythic22.model.Mythic22Result;
import at.gruber.elexis.mythic22.ui.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.LabItem;
import ch.elexis.data.LabResult;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class PersistencyHandler {
	private static Logger logger = LoggerFactory.getLogger(PersistencyHandler.class);

	private static PersistencyHandler m_instance = null;

	private HashMap<String, String> m_csvMap;
	private String m_csvMapPath;

	/**
	 * @return the single instance of this class
	 */
	public static PersistencyHandler getInstance() {
		if (m_instance == null) {
			m_instance = new PersistencyHandler();
		}
		return m_instance;
	}

	/**
	 * constructor prepares m_inputMap with all predefined fields
	 */
	private PersistencyHandler() {
		super();
		m_csvMapPath = CoreHub.localCfg.get(Preferences.CFG_PATHMAPPINGFILE, "fail");
		m_csvMap = new HashMap<String, String>();

	}

	/**
	 * Tries to insert the Mythic22Result into Elexis and therefore persist it. If
	 * the UserID of the Mythic22Result is not found the Result will not be
	 * persisted and false will be returned Also if the Mapping file is not found or
	 * does not contain the mappings for certain LabItems the LabResults for this
	 * LabItem will not be created. If the creation of certain LabResults will fail
	 * due to missing mapping entries the method will still return true. However if
	 * the Mapping file was empty or could not be found it will return false
	 *
	 * @param mythic22Result
	 * @return true if the Mythic22Result is successfully inserted into Elexis
	 */
	public boolean persistMythicResult(Mythic22Result mythic22Result) {

		Patient patient = null;

		LinkedList<String> temp = mythic22Result.getDefaultTypeValues().get("PID");
		if (temp != null && !temp.isEmpty()) {
			String patientID = temp.getFirst();
			patient = Patient.loadByPatientID(patientID);

			if (patient == null) {
				String message = "Patient " + patientID + " could not be found!";
				Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, message);
				StatusManager.getManager().handle(status, StatusManager.SHOW);
				logger.warn(message);
				return false;
			}
			if (!patient.isValid()) {
				String message = "Patient " + patientID + " is not valid!";
				Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, message);
				StatusManager.getManager().handle(status, StatusManager.SHOW);
				logger.warn(message);
				return false;
			}
		}

		if (!getCSVMappingMap().isEmpty()) {
			for (HaematologicalValue hvalue : mythic22Result.getHaematologicalValues()) {

				if (getCSVMappingMap().containsKey(hvalue.getIdentifier())) {
					LabItem labitem = LabItem.load(getCSVMappingMap().get(hvalue.getIdentifier()));

					if (labitem != null) {
						// TODO: Configure overwrite of existing values (for that day)
						new LabResult(patient, new TimeTool(), labitem, hvalue.getValue(), "");
					} else {
						logger.warn("Could not find laboritem for " + getCSVMappingMap().get(hvalue.getIdentifier()));
					}
				}
			}
			return true;
		}
		return false;
	}

	private HashMap<String, String> getCSVMappingMap() {

		if (m_csvMap.isEmpty() || !CoreHub.localCfg.get(Preferences.CFG_PATHMAPPINGFILE, "fail").equals(m_csvMapPath)) {

			HashMap<String, String> returnmap = new HashMap<String, String>();

			try {
				m_csvMapPath = CoreHub.localCfg.get(Preferences.CFG_PATHMAPPINGFILE, "fail");
				BufferedReader breader = new BufferedReader(new FileReader(m_csvMapPath));
				String str;

				while ((str = breader.readLine()) != null) {
					str = str.trim();
					String[] strArray = str.split(";");
					returnmap.put(strArray[0], strArray[1]);
				}
				breader.close();
			} catch (Exception e) {
				String message = "Couldn't read mapping file " + m_csvMapPath + ".";
				Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, message, e);
				StatusManager.getManager().handle(status, StatusManager.SHOW);
				logger.warn(message);
			}
			m_csvMap = returnmap;
		}

		return m_csvMap;
	}

}
