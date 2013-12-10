/*******************************************************************************
 * Copyright (c) 2007-2011, Praxis Dr. med. Peter Schönbucher
 * 
 * All rights reserved.
 * This code must not be used without permission of the copyright holder. 
 *
 * Contributors:
 *    D. Lutz - Initial implementation
 *    
 *******************************************************************************/

package org.iatrix.data;

import java.io.ByteArrayInputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;

import ch.elexis.Hub;
import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.data.Artikel;
import ch.elexis.data.IDiagnose;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Prescription;
import ch.elexis.data.Query;
import ch.elexis.icpc.Encounter;
import ch.elexis.icpc.Episode;
import ch.elexis.util.Log;
import ch.elexis.util.SWTHelper;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.VersionInfo;


/**
 * Datentyp für Problemliste.
 * 
 * Ein Problem wird den Konsulationen zugeordnet, in denen das Problem behandelt wird. Fuer jedes
 * Problem muessen KK-Diagnosen definiert werden, die dann auf der Rechnung erscheinen. Jedes
 * Problem verwaltet diese Diagnosen selber und gleicht dann die Konsulationen ab, denen dieses
 * Problem zugeordnet ist. Pro Problem koennen Dauermedikamente festgelegt werden. Diese Zuordnung
 * verwendet die bereits vorhandene Relation PATIENT_ARTIKEL_JOINT.
 * 
 * Update: Das hier beschriebene "Problem" enspricht ziemlich genau dem Konzept einer "Episode" bei
 * ICPC-2. Deshalb werden diese beiden Konzepte zusammengefuehrt. Somit kann ein Problem als eine
 * Erweiterung einer Episode betrachtet werden.
 * 
 * @author Daniel Lutz <danlutz@watz.ch>
 * 
 */

public class Problem extends Episode {
	/**
	 * Separator String for Lists returned as String
	 */
	public static final String TEXT_SEPARATOR = "::";
	
	static Log log = Log.get("Problem");
	
	// version key in globalCfg
	private static final String IATRIX_VERSION_KEY = "org.iatrix/dbversion";
	
	private static final String FIELD_PROCEDERE = "Procedere";
	
	private static final String STANDARD_PROBLEM = "Standardproblem";
	
	private static final String PROBLEM_BEHDL_TABLENAME = "IATRIX_PROBLEM_BEHDL_JOINT";
	private static final String PROBLEM_DG_TABLENAME = "IATRIX_PROBLEM_DG_JOINT";
	private static final String PROBLEM_DAUERMEDIKATION_TABLENAME =
		"IATRIX_PROBLEM_DAUERMEDIKATION_JOINT";
	
	/* Encounter DB Field namess */
	private static final String FIELD_EPISODE_ID = "EpisodeID";
	private static final String FIELD_KONS_ID = "KonsID";
	
	/* own problem/konsultation assignment */
	private static final String IATRIX_DB_VERSION_PRE_ENCOUNTERS = "0.2.0";
	/* converted to use icpc encounters */
	private static final String IATRIX_DB_VERSION_ENCOUNTERS = "0.3.0";
	/* current version */
	private static final String IATRIX_DB_VERSION = "0.3.0";
	
	private static final String CREATE =
		/*
		 * "CREATE TABLE " + PROBLEM_TABLENAME + " ("+ "ID          VARCHAR(25) primary key,"+
		 * "deleted     CHAR(1) default '0',"+ "PatientID   VARCHAR(25),"+
		 * "EpisodeID   VARCHAR(25),"+ "Bezeichnung VARCHAR(50),"+ // deprecated, see Episode.Title
		 * "Nummer      VARCHAR(10),"+ // deprecated, see Episode.Number "Datum       VARCHAR(20),"+
		 * // deprecated, see Episode.StartDate "Procedere   VARCHAR(80),"+
		 * "Status      CHAR(1) DEFAULT '1',"+ // deprecated, see Episode.Status "ExtInfo     BLOB"+
		 * ");"+ ""+
		 */
		"CREATE TABLE " + PROBLEM_BEHDL_TABLENAME + " (" + "ID            VARCHAR(25) primary key,"
			+ "ProblemID     VARCHAR(25)," + "BehandlungsID VARCHAR(25)" + ");"
			+ "CREATE INDEX problembehdl1 on " + PROBLEM_BEHDL_TABLENAME + " (ProblemID);" + ""
			+ "CREATE TABLE " + PROBLEM_DG_TABLENAME + " (" + "ID				VARCHAR(25) primary key,"
			+ "ProblemID      VARCHAR(25)," + "DiagnoseID		VARCHAR(25)" + ");" + ""
			+ "CREATE INDEX problemdg1 on " + PROBLEM_DG_TABLENAME + " (ProblemID);" + ""
			+ "CREATE TABLE " + PROBLEM_DAUERMEDIKATION_TABLENAME + " ("
			+ "ID                VARCHAR(25) primary key," + "ProblemID         VARCHAR(25),"
			+ "DauermedikationID VARCHAR(25)" + ");" + ""
			+ "CREATE INDEX problemdauermedikation1 on " + PROBLEM_DAUERMEDIKATION_TABLENAME
			+ " (ProblemID);";
	/*
	 * ""+ "INSERT INTO " + PROBLEM_TABLENAME + " (ID) VALUES ('__SETUP__');";
	 */

	/*
	 * static{ addMapping(PROBLEM_TABLENAME, "PatientID", "EpisodeID", "Bezeichnung", "Nummer",
	 * "Datum", "Procedere", "Status", "Konsultationen=JOINT:ProblemID:BehandlungsID:" +
	 * PROBLEM_BEHDL_TABLENAME, "Diagnosen=JOINT:ProblemID:DiagnoseID:" + PROBLEM_DG_TABLENAME,
	 * "Dauermedikation=JOINT:ProblemID:DauermedikationID:" + PROBLEM_DAUERMEDIKATION_TABLENAME ); }
	 * 
	 * /* DB Update
	 */
	/*
	 * static final String[] dbUpdateVersions = { "0.2.0" }; static final String[] dbUpdateCmds = {
	 * "", };
	 */

	/* JdbcLink connection singleton */
	private JdbcLink j = getConnection();
	
	static {
		init();
	}
	
	/**
	 * Oeffentlicher Konstruktor zur Erstellung eines neuen Problems
	 * 
	 * @param pat
	 *            Der Patient, dem dieses Problem zugeordnet werden soll
	 * @param bezeichnung
	 *            Bezeichnung des Problems
	 */
	public Problem(Patient pat, String bezeichnung){
		super(pat, bezeichnung);
	}
	
	/**
	 * Convert an object of type Episode to a corresponding object of type Problem. The new object
	 * is a Problem representing the Episode.
	 * 
	 * @param episode
	 *            the Episode object to convert
	 * @return a Problem representing <code>episode</code>. Returns null if <code>episode</code> is
	 *         null.
	 */
	public static Problem convertEpisodeToProblem(Episode episode){
		if (episode == null) {
			return null;
		}
		
		return Problem.load(episode.getId());
	}
	
	/**
	 * Hier werden Konfigurationseinzelheiten eingelesen. Wenn das Lesen fehlschlägt, nimmt die
	 * Methode an, dass die Tabelle noch nicht existiert und legt sie neu an. Bei Bedarf wird ein
	 * DB-Update vorgenommen.
	 * 
	 * @return
	 */
	private static void init(){
		String version = Hub.globalCfg.get(IATRIX_VERSION_KEY, null);
		if (version == null) {
			// tables don't yet exist or pre 0.2.0
			// get old-style version object
			OldProblem oldSetup = OldProblem.load("__SETUP__");
			if (!oldSetup.exists()) {
				// create tables
				try {
					ByteArrayInputStream bais = new ByteArrayInputStream(CREATE.getBytes("UTF-8"));
					if (getConnection().execScript(bais, true, false) == false) {
						MessageDialog.openError(null, "Fehler bei Problem",
							"Konnte die Tabellen für Probleme nicht erstellen");
						return;
					}
					version = IATRIX_DB_VERSION;
					Hub.globalCfg.set(IATRIX_VERSION_KEY, IATRIX_DB_VERSION);
					Hub.globalCfg.flush();
				} catch (Exception ex) {
					ExHandler.handle(ex);
				}
			} else {
				// convert old-style problems
				
				convertOldProblemsToEpisodes();
				version = IATRIX_DB_VERSION_PRE_ENCOUNTERS;
				Hub.globalCfg.set(IATRIX_VERSION_KEY, IATRIX_DB_VERSION_PRE_ENCOUNTERS);
				Hub.globalCfg.flush();
			}
		}
		
		if (version != null) {
			VersionInfo vi = new VersionInfo(version);
			
			if (vi.isOlder(IATRIX_DB_VERSION_ENCOUNTERS)) {
				/* We changed to use Encounters and Episode's diagnosis */
				SWTHelper
					.showInfo("Aktualisierung DB Iatrix",
						"DB Iatrix wird nachfolgend aktualisiert. Bitte Elexis nicht beenden, bis Sie informiert werden.");
				
				boolean success1 = convertProblemAssignmentsToEncounters();
				boolean success2 = convertIatrixDiagnosisToEpisodeDiagnosis();
				
				if (!(success1 && success2)) {
					SWTHelper
						.showError(
							"Fehler bei Aktualisierung DB Iatrix",
							"Bei der Aktualisierung der DB Iatrix sind Fehler aufgetreten. "
								+ "Bitte benachrichtigen Sie Ihren Elexis-Dienstleister, um das Problem "
								+ "zu beheben, bevor Sie weiter arbeiten. Bitte beenden Sie nun Elexis. "
								+ "Informationen zum Problem sind in der Log-Datei zu finden.");
				} else {
					// success
					Hub.globalCfg.set(IATRIX_VERSION_KEY, IATRIX_DB_VERSION_ENCOUNTERS);
					Hub.globalCfg.flush();
					SWTHelper.showInfo("Aktualisierung DB Iatrix",
						"DB Iatrix wurde fertig aktualisiert. Sie können nun arbeiten.");
				}
			}
		}
		
	}
	
	/**
	 * Convert old-style problems to problems based on episodes.
	 */
	private static void convertOldProblemsToEpisodes(){
		// get all available problems (OldProblem)
		List<OldProblem> oldProblems = OldProblem.getOldProblems();
		for (OldProblem oldProblem : oldProblems) {
			Problem problem = new Problem(oldProblem.getId());
			if (problem.isAvailable()) {
				// new-style problem already exists
				continue;
			}
			
			Patient patient = Patient.load(oldProblem.get("PatientID"));
			if (patient.isAvailable()) {
				problem.create(null);
				problem.set("PatientID", patient.getId());
				
				String title = oldProblem.get("Bezeichnung");
				String number = oldProblem.get("Nummer");
				String startDate = oldProblem.get("Datum");
				String procedere = oldProblem.get("Procedere");
				String sStatus = oldProblem.get("Status");
				
				int status;
				if (sStatus != null && sStatus.equals(ACTIVE_VALUE)) {
					status = ACTIVE;
				} else {
					status = INACTIVE;
				}
				
				problem.setTitle(title);
				problem.setNumber(number);
				problem.setStartDate(startDate);
				problem.setProcedere(procedere);
				problem.setStatus(status);
			}
		}
	}
	
	/**
	 * Convert each old-style problem/konsultation assignment to an Encounter
	 * 
	 * We create an Encounter for each old-style assignment. We don't need to delete the old
	 * assignments, since we won't reference them anymore.
	 * 
	 * @return true, if no error has occured, false otherwise
	 */
	private static boolean convertProblemAssignmentsToEncounters(){
		JdbcLink j = PersistentObject.getConnection();
		
		StringBuilder sql = new StringBuilder(200);
		sql.append("SELECT ID, ProblemID, BehandlungsID FROM " + PROBLEM_BEHDL_TABLENAME);
		
		Stm stm = j.getStatement();
		ResultSet rs = stm.query(sql.toString());
		
		boolean success = true;
		
		try {
			while (rs.next()) {
				try {
					
					String id = rs.getString(1);
					String problemId = rs.getString(2);
					String konsultationId = rs.getString(3);
					
					Problem problem = Problem.load(problemId);
					Konsultation konsultation = Konsultation.load(konsultationId);
					
					// create new Encounter (replaces old-style assignment)
					if (problem != null && konsultation != null) {
						if (problem.exists() && konsultation.exists()) {
							if (!problem.isAssignedToKonsultation(konsultation)) {
								Encounter encounter = new Encounter(konsultation, problem);
								log
									.log("Converted to Encounter " + encounter.getId(),
										Log.DEBUGMSG);
							}
						}
					}
				} catch (Exception ex) {
					// Catch exceptions to make sure we don't miss the remaining
					// assignments
					success = false;
					ExHandler.handle(ex);
					log.log(ex.getMessage(), Log.ERRORS);
				}
			}
			rs.close();
		} catch (Exception ex) {
			success = false;
			ExHandler.handle(ex);
			log.log(ex.getMessage(), Log.ERRORS);
		} finally {
			j.releaseStatement(stm);
		}
		
		return success;
	}
	
	/**
	 * Convert each old-style problem/diagnosis assignment to an Episode diagnosis assignment
	 * 
	 * @return true, if no error has occured, false otherwise
	 */
	private static boolean convertIatrixDiagnosisToEpisodeDiagnosis(){
		JdbcLink j = PersistentObject.getConnection();
		
		StringBuilder sql = new StringBuilder(200);
		sql.append("SELECT ID, ProblemID, DiagnoseID FROM " + PROBLEM_DG_TABLENAME);
		
		Stm stm = j.getStatement();
		ResultSet rs = stm.query(sql.toString());
		
		boolean success = true;
		
		try {
			while (rs.next()) {
				try {
					String id = rs.getString(1);
					String problemId = rs.getString(2);
					String diagnoseId = rs.getString(3);
					
					Problem problem = Problem.load(problemId);
					IDiagnose diagnose = null;
					
					Stm stm2 = j.getStatement();
					ResultSet rs2 =
						stm2.query("SELECT DG_CODE, KLASSE FROM DIAGNOSEN WHERE ID = "
							+ JdbcLink.wrap(diagnoseId));
					if (rs2.next()) {
						String code = rs2.getString(1);
						String klasse = rs2.getString(2);
						
						StringBuilder sb = new StringBuilder();
						sb.append(klasse);
						sb.append("::");
						sb.append(code);
						try {
							PersistentObject dg = Hub.poFactory.createFromString(sb.toString());
							if (dg instanceof IDiagnose) {
								diagnose = (IDiagnose) dg;
							}
						} catch (Exception ex) {
							ExHandler.handle(ex);
						}
					}
					rs2.close();
					j.releaseStatement(stm2);
					
					// re-add diagnosis to problem via Episode
					if (problem != null && diagnose != null) {
						if (problem.exists()) {
							problem.addDiagnose(diagnose, false);
						}
					}
				} catch (Exception ex) {
					// Catch exceptions to make sure we don't miss the remaining
					// assignments
					success = false;
					ExHandler.handle(ex);
					log.log(ex.getMessage(), Log.ERRORS);
				}
			}
			rs.close();
		} catch (Exception ex) {
			success = false;
			ExHandler.handle(ex);
			log.log(ex.getMessage(), Log.ERRORS);
		} finally {
			j.releaseStatement(stm);
		}
		
		return success;
	}
	
	public static Problem load(String id){
		Problem problem = new Problem(id);
		return problem;
	}
	
	/**
	 * Der parameterlose Konstruktor wird nur von der Factory gebraucht und sollte nie public sein.
	 */
	protected Problem(){
	// empty
	}
	
	/**
	 * Creates a new problem with the given id.
	 * 
	 * @param id
	 *            the id of this object
	 */
	protected Problem(String id){
		super(id);
	}
	
	/**
	 * Ein Problem aus der Datenbank entfernen. Dabei werden auch alle verknüpften Daten gelöscht
	 * (?)
	 * 
	 * @param force
	 *            bei true wird das Problem auf jeden Fall gelöscht, bei false nur, wenn keine
	 *            vernknüpften Daten (?) von ihm existieren.
	 * @return false wenn das Problem nicht gelöscht werden konnte.
	 */
	public boolean remove(boolean force){
		if (true || (force == true)
			&& (Hub.acl.request(AccessControlDefaults.DELETE_FORCED) == true)) {
			// TODO verknuepfte Daten loeschen falls vorhanden
			// TODO Sicherstellen, dass Problem von allen Konsulationen entfernt wird.
			ExHandler.handle(new Exception("Alle Probleme von Konsulationen entfernen"));
			
			return super.delete();
		}
		return false;
	}
	
	public String getProcedere(){
		return checkNull(getExtField(FIELD_PROCEDERE));
	}
	
	public void setProcedere(String procedere){
		setExtField(FIELD_PROCEDERE, procedere);
	}
	
	public String toString(){
		return getLabel();
	}
	
	/**
	 * Check if this problem has already been assigned to the consultation
	 * 
	 * @param konsultation
	 *            the consultation to check
	 * @return true, if this problem has alread been assigned, fals else
	 */
	// DONE do this with encounters
	public boolean isAssignedToKonsultation(Konsultation konsultation){
		/*
		 * String problemKonsultationId = j.queryString( "SELECT ID FROM " + PROBLEM_BEHDL_TABLENAME
		 * + " WHERE ProblemID = " + getWrappedId() + " AND BehandlungsID = " +
		 * konsultation.getWrappedId()); if (StringTool.isNothing(problemKonsultationId)) { return
		 * false; } else { return true; }
		 */

		Encounter encounter = getEncounter(konsultation);
		return (encounter != null);
	}
	
	/**
	 * Return the corresponding encounter of this problemm/cons assignment
	 * 
	 * @param konsultation
	 *            the consultation for which an encounter should be returnedn
	 * @return reteurns the encounter respresenting this problem/cons assignemtn, or null if no
	 *         encounter could be found
	 */
	public Encounter getEncounter(Konsultation konsultation){
		if (konsultation == null) {
			return null;
		}
		
		Query<Encounter> query = new Query<Encounter>(Encounter.class);
		query.add(FIELD_EPISODE_ID, "=", getId());
		query.add(FIELD_KONS_ID, "=", konsultation.getId());
		List<Encounter> encounters = query.execute();
		if (encounters != null && !encounters.isEmpty()) {
			// return the first found encounter (actually, there should be exactly one)
			return encounters.get(0);
		}
		
		return null;
	}
	
	/**
	 * Return the corresponding encounters of this problem/cons assignment
	 * 
	 * Note: By specification, there should exist at most one encounter per Problem/Konsultation.
	 * This method's purpose is for backwards compatibility. Whenever possible, the method
	 * getEncounter(Konsultation) should be used.
	 * 
	 * @param konsultation
	 *            the consultation for which the encounters should be returned
	 * @return reteurns the encounters respresenting this problem/cons assignment, or an emtpy list
	 *         (not null) if no encounter could be found
	 */
	public List<Encounter> getEncounters(Konsultation konsultation){
		List<Encounter> result = new ArrayList<Encounter>();
		
		if (konsultation == null) {
			return result;
		}
		
		Query<Encounter> query = new Query<Encounter>(Encounter.class);
		query.add(FIELD_EPISODE_ID, "=", getId());
		query.add(FIELD_KONS_ID, "=", konsultation.getId());
		List<Encounter> encounters = query.execute();
		if (encounters != null && !encounters.isEmpty()) {
			result.addAll(encounters);
		}
		
		return result;
	}
	
	/**
	 * Fuegt ein Problem einer Konsultation hinzu, d. h. das Problem wurde in dieser Konsultation
	 * behandelt (oder soll behandelt werden).
	 * 
	 * @param konsultation
	 *            die Konsultation, zu der das Problem hinzugefuegt werden soll.
	 */
	// DONE do this with encounters
	public void addToKonsultation(Konsultation konsultation){
		if (!isAssignedToKonsultation(konsultation)) {
			
			/*
			 * // add the Problem to the Konsultation StringBuilder sql = new StringBuilder(200);
			 * sql.append( "INSERT INTO " + PROBLEM_BEHDL_TABLENAME +
			 * " (ID, ProblemID, BehandlungsID) VALUES (")
			 * .append(JdbcLink.wrap(StringTool.unique("problembhdl")))
			 * .append(",").append(getWrappedId()).append(",").append(
			 * JdbcLink.wrap(konsultation.getId())).append(")"); j.exec(sql.toString());
			 */

			Encounter encounter = new Encounter(konsultation, this);
			
			// add this Problem's Diagnosen to the Konsultation
			
			// Konsultation doesn't check if Diagnose has already been added
			// existing Diagnosen
			ArrayList<IDiagnose> existingDiagnosen = konsultation.getDiagnosen();
			
			// Problem's Diagnosen
			List<IDiagnose> diagnosen = getDiagnosen();
			for (IDiagnose diagnose : diagnosen) {
				boolean exists = false;
				for (IDiagnose dg : existingDiagnosen) {
					// note: IDiagnose doesn't guarantee that equals() is impelmented.
					// but IDiagnose objects usually extend PersistentObject
					if (dg.getId().equals(diagnose.getId())) {
						exists = true;
					}
				}
				if (!exists) {
					konsultation.addDiagnose(diagnose);
				}
			}
		}
	}
	
	/**
	 * Entfernt ein Problem von einer Konsultation hinzu, d. h. das Problem soll in dieser
	 * Konsultation nicht behandelt werden.
	 * 
	 * WICHTIG: Caller dieser Methode sollen vorher sicherstellen, dass der Encounter keine Daten
	 * enthält. Falls doch, soll der Benutzer gefragt werden, ob der Encounter trotzdem geloescht
	 * werden soll. Dies ist die Verwantortung des Callers.
	 * 
	 * @param konsultation
	 *            die Konsultation, von der das Problem entfernt werden soll.
	 */
	// DONE do this with encounters
	public void removeFromKonsultation(Konsultation konsultation){
		// remove assignment in database
		
		/*
		 * StringBuilder sql = new StringBuilder(200); sql.append("DELETE FROM " +
		 * PROBLEM_BEHDL_TABLENAME) .append(" WHERE ProblemID = " + getWrappedId()) .append(" AND")
		 * .append(" BehandlungsID = " + konsultation.getWrappedId()); j.exec(sql.toString());
		 */

		Query<Encounter> query = new Query<Encounter>(Encounter.class);
		query.add(FIELD_EPISODE_ID, "=", getId());
		query.add(FIELD_KONS_ID, "=", konsultation.getId());
		List<Encounter> encounters = query.execute();
		if (encounters != null && !encounters.isEmpty()) {
			for (Encounter encounter : encounters) {
				encounter.delete();
			}
		}
		
		// remove Diagnosen from Konsultation
		
		List<IDiagnose> diagnosen = getDiagnosen();
		removeDiagnosenFromKonsultation(konsultation, diagnosen);
	}
	
	/*
	 * Remove a List of Diagnosen from a Konsultation this Problem is assigned to. But don't remove
	 * Diagnosen from other Problems that are assigned to this Konsultation. This method is used by
	 * removeDiagnose(IDiagnose)
	 */
	private void removeDiagnosenFromKonsultation(Konsultation konsultation,
		List<IDiagnose> diagnosen){
		// all other assigned Problem's Diagnosen
		List<IDiagnose> otherProblemsDiagnosen = new ArrayList<IDiagnose>();
		List<Problem> problems = getProblemsOfKonsultation(konsultation);
		for (Problem problem : problems) {
			if (!problem.equals(this)) {
				otherProblemsDiagnosen.addAll(problem.getDiagnosen());
			}
		}
		
		// remove all Diagnosen except if it is in otherProblemsDiagnosen
		for (IDiagnose diagnose : diagnosen) {
			if (!otherProblemsDiagnosen.contains(diagnose)) {
				if (konsultation.isEditable(false)) {
					konsultation.removeDiagnose(diagnose);
				}
			}
		}
	}
	
	/*
	 * Remove a single Diagnose from a Konsultation. Also see
	 * removeDiagnosenFromKonsultation(Konsultation, List<IDiagnose>) This method is used by
	 * removeProblemFromKonsultation(Konsultation)
	 */
	private void removeDiagnoseFromKonsultation(Konsultation konsultation, IDiagnose diagnose){
		// Create List of Diagnosen with a single element
		ArrayList<IDiagnose> diagnosen = new ArrayList<IDiagnose>();
		diagnosen.add(diagnose);
		
		removeDiagnosenFromKonsultation(konsultation, diagnosen);
	}
	
	/**
	 * Liefert die Probleme, die dem Patienten zugeordnet sind.
	 * 
	 * @param der
	 *            Patient, von dem die Probleme zugureckgegeben werden sollen
	 * @return eine Liste mit den Problemen des Patienten
	 */
	public static List<Problem> getProblemsOfPatient(Patient patient){
		Query<Problem> query = new Query<Problem>(Problem.class);
		query.add("PatientID", "=", patient.getId());
		List<Problem> problems = query.execute();
		if (problems != null) {
			return problems;
		} else {
			// error, return empty list
			return new ArrayList<Problem>();
		}
	}
	
	/**
	 * Hole alle Probleme, die einer Konsulation zugeordnet sind.
	 * 
	 * @param konsultation
	 *            die Konsulation, von der die Probleme geholt werden sollen
	 * @return eine Liste aller Probleme zu dieser Konsulation
	 */
	// DONE do this with encounters
	public static List<Problem> getProblemsOfKonsultation(Konsultation konsultation){
		ArrayList<Problem> problems = new ArrayList<Problem>();
		
		/*
		 * StringBuilder sql = new StringBuilder(200); sql.append("SELECT ProblemId FROM " +
		 * PROBLEM_BEHDL_TABLENAME) .append(" WHERE BehandlungsID = " +
		 * konsultation.getWrappedId());
		 * 
		 * Stm stm = getConnection().getStatement(); ResultSet rs = stm.query(sql.toString()); try {
		 * while(rs.next()) { String id = rs.getString(1); Problem problem = Problem.load(id); if
		 * (problem != null) { problems.add(problem); } } rs.close(); } catch (Exception ex) {
		 * ExHandler.handle(ex); log.log(ex.getMessage(), Log.ERRORS); } finally {
		 * getConnection().releaseStatement(stm); }
		 */

		Query<Encounter> query = new Query<Encounter>(Encounter.class);
		query.add(FIELD_KONS_ID, "=", konsultation.getId());
		List<Encounter> encounters = query.execute();
		if (encounters != null && !encounters.isEmpty()) {
			for (Encounter encounter : encounters) {
				Episode episode = encounter.getEpisode();
				Problem problem = convertEpisodeToProblem(episode);
				
				if (problem.exists() && !problems.contains(problem)) {
					problems.add(problem);
				}
			}
		}
		
		return problems;
	}
	
	/**
	 * Liefert eine Liste aller Konsulationen zurueck, denen dieses Problem zugeordnet ist.
	 * 
	 * @return Liste aller Konsulationen
	 */
	// DONE do this with encounters
	public List<Konsultation> getKonsultationen(){
		ArrayList<Konsultation> konsultationen = new ArrayList<Konsultation>();
		
		/*
		 * StringBuilder sql = new StringBuilder(200); sql.append("SELECT BehandlungsID FROM " +
		 * PROBLEM_BEHDL_TABLENAME) .append(" WHERE ProblemID = " + getWrappedId());
		 * 
		 * Stm stm = j.getStatement(); ResultSet rs = stm.query(sql.toString()); try {
		 * while(rs.next()) { String id = rs.getString(1); Konsultation konsultation =
		 * Konsultation.load(id); if (konsultation != null) { konsultationen.add(konsultation); } }
		 * rs.close(); } catch (Exception ex) { ExHandler.handle(ex); log.log(ex.getMessage(),
		 * Log.ERRORS); } finally { j.releaseStatement(stm); }
		 */

		Query<Encounter> query = new Query<Encounter>(Encounter.class);
		query.add(FIELD_EPISODE_ID, "=", getId());
		List<Encounter> encounters = query.execute();
		if (encounters != null && !encounters.isEmpty()) {
			for (Encounter encounter : encounters) {
				Konsultation konsultation = encounter.getKons();
				if (konsultation != null && konsultation.exists()
					&& !konsultationen.contains(konsultation)) {
					konsultationen.add(konsultation);
				}
			}
		}
		
		return konsultationen;
	}
	
	/** Eine Liste der Diagnosen zu diesem Problem holen */
	// DONE do this with encounters
	public List<IDiagnose> getDiagnosen(){
		/*
		 * ArrayList<IDiagnose> ret=new ArrayList<IDiagnose>(); Stm stm=j.getStatement(); ResultSet
		 * rs1
		 * =stm.query("SELECT DiagnoseID FROM IATRIX_PROBLEM_DG_JOINT WHERE ProblemID="+JdbcLink.wrap
		 * (getId())); StringBuilder sb=new StringBuilder(); try{ while(rs1.next()==true){ String
		 * dgID=rs1.getString(1);
		 * 
		 * Stm stm2=j.getStatement(); ResultSet
		 * rs2=stm2.query("SELECT DG_CODE,KLASSE FROM DIAGNOSEN WHERE ID="+JdbcLink.wrap(dgID));
		 * if(rs2.next()){ sb.setLength(0); sb.append(rs2.getString(2)).append("::");
		 * sb.append(rs2.getString(1)); try{ PersistentObject
		 * dg=Hub.poFactory.createFromString(sb.toString()); if(dg!=null){ ret.add((IDiagnose)dg); }
		 * }catch(Exception ex){ log.log("Fehlerhafter Diagnosecode "+sb.toString(),Log.ERRORS); } }
		 * rs2.close(); j.releaseStatement(stm2); } rs1.close(); }catch(Exception ex){
		 * ExHandler.handle(ex); log.log(ex.getMessage(),Log.ERRORS); } finally{
		 * j.releaseStatement(stm); } return ret;
		 */

		return getDiagnoses();
	}
	
	/**
	 * Liefert eine Text-Repraesentation der Diagnosenliste zurueck
	 * 
	 * @return
	 */
	public String getDiagnosenAsText(){
		StringBuilder sb = new StringBuilder();
		
		List<IDiagnose> diagnosen = getDiagnosen();
		
		boolean isFirst = true;
		for (IDiagnose diagnose : diagnosen) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(TEXT_SEPARATOR);
			}
			sb.append(diagnose.getLabel());
		}
		
		return sb.toString();
	}
	
	/**
	 * Add a further diagnosis to this problem. Add the diagnosis to all assigned konsultations,
	 * too.
	 * 
	 * @param dg
	 *            the diagnosis to be added
	 */
	public void addDiagnose(IDiagnose dg){
		addDiagnose(dg, true);
	}
	
	/**
	 * Add a diagnosis to this problem. Optionally, add the diagnosis to the konsusltation
	 * 
	 * @param dg
	 *            the diagnosis to be added
	 * @param addToKonsultation
	 *            add the diagnosis to the kons if the value is true, else not.
	 */
	// DONE do this with encounters
	private void addDiagnose(IDiagnose dg, boolean addToKonsultation){
		/*
		 * No more required, since Episode stores it in a list of ids String diagnoseId =
		 * j.queryString("SELECT ID FROM DIAGNOSEN WHERE KLASSE = " +
		 * JdbcLink.wrap(dg.getClass().getName()) + " AND DG_CODE = " +
		 * JdbcLink.wrap(dg.getCode())); StringBuilder sql=new StringBuilder(200); if
		 * (StringTool.isNothing(diagnoseId)) { diagnoseId = StringTool.unique("problemdg");
		 * sql.append("INSERT INTO DIAGNOSEN (ID, DG_CODE, DG_TXT, KLASSE) VALUES (")
		 * .append(JdbcLink.wrap(diagnoseId)).append(",")
		 * .append(JdbcLink.wrap(dg.getId())).append(",")
		 * .append(JdbcLink.wrap(dg.getText())).append(",")
		 * .append(JdbcLink.wrap(dg.getClass().getName())) .append(")"); j.exec(sql.toString());
		 * sql.setLength(0); }
		 */

		// add Diagnose if it doesn't yet exists
		boolean exists = false;
		/* get existing diagnosis from Episode */
		List<IDiagnose> existing = getDiagnoses();
		for (IDiagnose eDg : existing) {
			// note: IDiagnose doesn't guarantee that equals() is impelmented.
			// but IDiagnose objects usually extend PersistentObject
			if (eDg.getId().equals(dg.getId())) {
				exists = true;
				break;
			}
		}
		if (!exists) {
			/* add diagnosis to Episode */
			addDiagnosis(dg);
			
			if (addToKonsultation) {
				// add Diagnose to konsultation
				addDiagnoseToKonsultationen(dg);
			}
		}
	}
	
	private void addDiagnoseToKonsultationen(IDiagnose diagnose){
		// pre: Diagnose has already been added to the Problem
		
		List<Konsultation> konsultationen = getKonsultationen();
		for (Konsultation konsultation : konsultationen) {
			// Konsultation doesn't check if Diagnose has already been added
			ArrayList<IDiagnose> diagnosen = konsultation.getDiagnosen();
			boolean exists = false;
			for (IDiagnose dg : diagnosen) {
				// note: IDiagnose doesn't guarantee that equals() is impelmented.
				// but IDiagnose objects usually extend PersistentObject
				if (dg.getId().equals(diagnose.getId())) {
					exists = true;
				}
			}
			if (!exists) {
				if (konsultation.isEditable(false)) {
					konsultation.addDiagnose(diagnose);
				}
			}
		}
	}
	
	/** Eine Diagnose aus der Diagnoseliste entfernen */
	// DONE Use Episode to do this
	public void removeDiagnose(IDiagnose diagnose){
		/*
		 * // remove diagnose in db
		 * 
		 * StringBuilder sql=new StringBuilder();
		 * sql.append("SELECT ID FROM DIAGNOSEN WHERE DG_CODE=")
		 * .append(JdbcLink.wrap(diagnose.getId())).append(" AND ")
		 * .append("KLASSE=").append(JdbcLink.wrap(diagnose.getClass().getName())); String
		 * dgid=j.queryString(sql.toString());
		 * 
		 * sql.setLength(0); sql.append("DELETE FROM " + PROBLEM_DG_TABLENAME + " WHERE ProblemID=")
		 * .append(getWrappedId()).append(" AND ")
		 * .append("DiagnoseID=").append(JdbcLink.wrap(dgid)); j.exec(sql.toString());
		 * 
		 * // remove Diagnose from all Konsultationen
		 * 
		 * List<Konsultation> konsultationen = getKonsultationen(); for (Konsultation konsultation :
		 * konsultationen) { removeDiagnoseFromKonsultation(konsultation, diagnose); }
		 */

		// TODO Episode.removeDiagnosis() doesn't yet really remove the diagnosis.
		removeDiagnosis(diagnose);
	}
	
	/**
	 * Add a Prescription specific to this Problem
	 * 
	 * @param prescription
	 *            the Prescription to be added
	 * @return true if the Prescription has been added, else otherwise
	 */
	public boolean addPrescription(Prescription prescription){
		String exists =
			j.queryString("SELECT ID FROM " + PROBLEM_DAUERMEDIKATION_TABLENAME
				+ " WHERE ProblemID = " + getWrappedId() + " AND DauermedikationID = "
				+ prescription.getWrappedId());
		if (StringTool.isNothing(exists)) {
			String problemDaueredikationId = StringTool.unique("problemdauermedikation");
			StringBuilder sql = new StringBuilder(200);
			sql.append(
				"INSERT INTO " + PROBLEM_DAUERMEDIKATION_TABLENAME
					+ " (ID, ProblemID, DauermedikationID) VALUES (").append(
				JdbcLink.wrap(problemDaueredikationId)).append(",").append(getWrappedId()).append(
				",").append(prescription.getWrappedId()).append(")");
			j.exec(sql.toString());
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Remove Prescription from this Problem
	 * 
	 * @param prescription
	 *            the Prescription to be removed
	 */
	public void removePrescription(Prescription prescription){
		StringBuilder sql = new StringBuilder(200);
		sql.append("DELETE FROM " + PROBLEM_DAUERMEDIKATION_TABLENAME).append(
			" WHERE ProblemID = " + getWrappedId()).append(" AND").append(
			" DauermedikationID = " + prescription.getWrappedId());
		j.exec(sql.toString());
	}
	
	/**
	 * Return all Prescriptions specific to this problem
	 * 
	 * @return a List of all Prescriptions
	 */
	public List<Prescription> getPrescriptions(){
		ArrayList<Prescription> prescriptions = new ArrayList<Prescription>();
		
		StringBuilder sql = new StringBuilder(200);
		sql.append("SELECT DauermedikationID FROM " + PROBLEM_DAUERMEDIKATION_TABLENAME).append(
			" WHERE ProblemID = " + getWrappedId());
		
		Stm stm = j.getStatement();
		ResultSet rs = stm.query(sql.toString());
		try {
			while (rs.next()) {
				String id = rs.getString(1);
				Prescription prescription = Prescription.load(id);
				if (prescription != null && prescription.exists()) {
					prescriptions.add(prescription);
				}
			}
			rs.close();
		} catch (Exception ex) {
			ExHandler.handle(ex);
			log.log(ex.getMessage(), Log.ERRORS);
		} finally {
			j.releaseStatement(stm);
		}
		return prescriptions;
	}
	
	/**
	 * Returns a textual representation of all Prescriptions. The Prescriptions are separated with
	 * TEXT_SEPARATOR ("::").
	 * 
	 * @return a list of all Prescriptions
	 */
	public String getPrescriptionsAsText(){
		StringBuilder sb = new StringBuilder();
		
		List<Prescription> prescriptions = getPrescriptions();
		
		boolean isFirst = true;
		for (Prescription prescription : prescriptions) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(TEXT_SEPARATOR);
			}
			String label;
			
			Artikel artikel = prescription.getArtikel();
			if (artikel != null && artikel.isAvailable()) {
				label = prescription.getArtikel().getLabel() + " (" + prescription.getDosis() + ")";
			} else {
				label = "Fehler" + " (" + prescription.getDosis() + ")";
			}
			
			sb.append(label);
		}
		
		return sb.toString();
	}
	
	/**
	 * Standardproblem erstellen
	 * 
	 * @param patient
	 *            der Patient, fuer den ein Standardproblem erstellt werden soll
	 * @return das gerade erstellte Problem
	 */
	public static Problem createStandardProblem(Patient patient){
		Problem problem = new Problem(patient, STANDARD_PROBLEM);
		return problem;
	}
}
