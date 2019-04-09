/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/

package ch.elexis.icpc;

import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;

public class IcpcImporter extends ImporterPage {
	// ImporterPage.DBBasedImporter dbi;
	JdbcLink pj;
	
	public IcpcImporter(){
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Composite createPage(Composite parent){
		/*
		 * dbi = new ImporterPage.DBBasedImporter(parent, this);
		 * dbi.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true)); return dbi;
		 */
		FileBasedImporter fbi = new FileBasedImporter(parent, this);
		fbi.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return fbi;
		
	}
	
	/*
	 * public boolean connect(){ String type = results[0]; if (type != null) { String server =
	 * results[1]; String db = results[2]; String user = results[3]; String password = results[4];
	 * 
	 * if (type.equals("MySQL")) { j = JdbcLink.createMySqlLink(server, db); return j.connect(user,
	 * password); } else if (type.equals("PostgreSQL")) { j = JdbcLink.createPostgreSQLLink(server,
	 * db); return j.connect(user, password); } else if (type.equals("ODBC")) { j =
	 * JdbcLink.createODBCLink(db); return j.connect(user, password); } }
	 * 
	 * return false; }
	 */
	
	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception{
		monitor.beginTask("Importiere ICPC-2", 727);
		return doImport(monitor, new DatabaseBuilder().setReadOnly(true).open(new File(results[0])));
	}

	public IStatus doImport(IProgressMonitor monitor, Database db){
		monitor.worked(1);
		pj = PersistentObject.getConnection();
		
		monitor.subTask("Lösche alte Daten");
		pj.exec("DELETE FROM " + IcpcCode.TABLENAME + " where ID != 'ver'");
		monitor.worked(1);
		monitor.subTask("Lese Daten ein");
		PreparedStatement ps =
			pj.prepareStatement("INSERT INTO "
				+ IcpcCode.TABLENAME
				+ " ("
				+ "ID,component,txt,synonyms,short,icd10,criteria,inclusion,exclusion,consider,note)"
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?);");
		monitor.worked(1);
		try {
			Table table = db.getTable("ICPC2eGM");
			Iterator<Row> it = table.iterator();
			while (it.hasNext()) {
				Row row = it.next();
				ps.setString(1, (String) row.get("CODE")); // id
				ps.setObject(2, row.get("COMPONENT")); // component
				ps.setString(3, (String) row.get("TEXT"));// txt
				ps.setString(4, (String) row.get("SYNONYMS"));// synonyms
				ps.setString(5, (String) row.get("SHORT"));// short
				ps.setString(6, (String) row.get("ICD-10"));// icd10
				ps.setString(7, (String) row.get("CRIT"));// criteria
				ps.setString(8, (String) row.get("INCL"));
				ps.setString(9, (String) row.get("EXCL"));
				ps.setString(10, (String) row.get("CONS"));
				ps.setString(11, (String) row.get("NOTE"));
				ps.execute();
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				monitor.worked(1);
			}
			monitor.done();
		} catch (Exception ex) {
			ExHandler.handle(ex);
			return new Status(Status.ERROR, "ICPC", 3, ex.getMessage(), null);
		}
		return Status.OK_STATUS;
	}
	
	/*
	 * @Override public IStatus doImport(IProgressMonitor monitor) throws Exception{
	 * monitor.beginTask("Importiere ICPC-2", 727); monitor.subTask("Verbinde"); if (!connect()) {
	 * monitor.done(); return new Status(Status.ERROR, "Icpc", 1,
	 * "Konnte keine Verbindung herstellen", null); } pj = PersistentObject.getConnection(); Stm
	 * stmSrc = j.getStatement(); monitor.subTask("Lösche alte Daten"); monitor.worked(1); // patch
	 * 03.02.2010 / tschaller: // nachfolgendes IcpcCode.initialize(); wirft eine Exception:
	 * org.eclipse.swt.SWTException: // Invalid thread access // da createOrModifyTable(createDB);
	 * bereits beim laden des Plugins ausgeführt wird, braucht // es das hier gar nicht. // ich
	 * beschränke mich daher auf ein delete from ch_elexis_icpc... //
	 * pj.exec("DROP INDEX "+IcpcCode.TABLENAME+"_IDX1 ON "+IcpcCode.TABLENAME); //
	 * pj.exec("DROP TABLE "+IcpcCode.TABLENAME); // IcpcCode.initialize(); pj.exec("DELETE FROM " +
	 * IcpcCode.TABLENAME + " where ID != 'ver'"); monitor.worked(1);
	 * monitor.subTask("Lese Daten ein"); PreparedStatement ps = pj .prepareStatement("INSERT INTO "
	 * + IcpcCode.TABLENAME + " (" +
	 * "ID,component,txt,synonyms,short,icd10,criteria,inclusion,exclusion,consider,note)" +
	 * "VALUES (?,?,?,?,?,?,?,?,?,?,?);"); monitor.worked(1); try { ResultSet res =
	 * stmSrc.query("SELECT * FROM \"ICPC2eGM\""); while (res.next()) { ps.setString(1,
	 * res.getString(1)); //id ps.setString(2, res.getString(2)); // component ps.setString(3,
	 * convert(res,3));// txt ps.setString(4, convert(res,4));//synonyms ps.setString(5,
	 * convert(res,5));//short ps.setString(6, res.getString(6));//icd10 ps.setString(7,
	 * convert(res,7));//criteria ps.setString(8, convert(res,8)); ps.setString(9, convert(res,9));
	 * ps.setString(10, convert(res,10)); ps.setString(11, convert(res,11)); ps.execute(); if
	 * (monitor.isCanceled()) { return Status.CANCEL_STATUS; } monitor.worked(1); } monitor.done();
	 * } catch (Exception ex) { ExHandler.handle(ex); return new Status(Status.ERROR, "ICPC", 3,
	 * ex.getMessage(), null); } finally { j.releaseStatement(stmSrc); } return Status.OK_STATUS; }
	 */
	
	@Override
	public String getDescription(){
		return "International Classification of Primary Care";
	}
	
	@Override
	public String getTitle(){
		return "ICPC-2";
	}
	
	private String convert(ResultSet res, int field) throws Exception{
		Reader reader = res.getCharacterStream(field);
		if (reader == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(reader);
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		String ret = sb.toString();
		return ret;
	}
}
