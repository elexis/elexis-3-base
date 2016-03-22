package ch.elexis.data.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.exceptions.PersistenceException;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.ui.importer.div.importers.AccessWrapper;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Leistungsblock;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.data.TarmedKumulation;
import ch.elexis.data.TarmedLeistung;
import ch.elexis.data.Verrechnet;
import ch.rgw.compress.CompEx;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;

import com.healthmarketscience.jackcess.Database;

public class TarmedReferenceDataImporter extends AbstractReferenceDataImporter {
	private static final Logger logger = LoggerFactory.getLogger(TarmedReferenceDataImporter.class);
	
	private static final String ImportPrefix = "TARMED_IMPORT_";
	
	private JdbcLink pj;
	private JdbcLink cacheDb = null; // As we have problems parsing dates using the postgresql-JdBC,
	// we create a temporary H2 DB
	Stm source, dest;
	private String lang;
	private Database mdbDB;
	private AccessWrapper aw;
	private String mdbFilename;
	private Set<String> cachedDbTables = null;
	private int count = 0; // Our counter for the progress monitor. Twice. Once for Access import,
	// then real import
	private boolean updateBlockWarning = false;
	boolean updateIDs = false;
	boolean showRestartDialog = true;
	
	/**
	 * Only for unit tests! Suppress dialog at end of import
	 */
	public void suppressRestartDialog(){
		showRestartDialog = false;
	}
	
	@Override
	public @NonNull Class<?> getReferenceDataTypeResponsibleFor(){
		return TarmedLeistung.class;
	}
	
	@Override
	public int getCurrentVersion(){
		return TarmedLeistung.getCurrentVersion();
	}
	
	@Override
	public IStatus performImport(@Nullable IProgressMonitor ipm, InputStream input, @Nullable Integer version){
		if (ipm == null) {
			ipm = new NullProgressMonitor();
		}
		
		// init database connection
		pj = PersistentObject.getConnection();
		cacheDb = new JdbcLink("org.h2.Driver", "jdbc:h2:mem:tarmed_import", "hsql");
		cacheDb.connect("", "");
		
		if (openAccessDatabase(ipm, input) != Status.OK_STATUS
			|| deleteCachedAccessTables(ipm) != Status.OK_STATUS
			|| importAllAccessTables(ipm) != Status.OK_STATUS) {
			mdbDB = null;
			cachedDbTables = null;
			return Status.CANCEL_STATUS;
		}
		
		lang = JdbcLink.wrap(CoreHub.localCfg.get(Preferences.ABL_LANGUAGE, "d").toUpperCase()); //$NON-NLS-1$
		ipm.subTask(Messages.TarmedImporter_connecting);
		
		// always convert ids if there are old ids in the database
		TarmedLeistung leistung = TarmedLeistung.load("00.0010");
		if (leistung.exists())
			updateIDs = true;
		
		try {
			source = cacheDb.getStatement();
			dest = pj.getStatement();
			ipm.subTask(Messages.TarmedImporter_deleteOldData);
			
			pj.exec("DELETE FROM TARMED"); //$NON-NLS-1$
			pj.exec("DELETE FROM TARMED_DEFINITIONEN"); //$NON-NLS-1$
			pj.exec("DELETE FROM TARMED_EXTENSION"); //$NON-NLS-1$
			pj.exec("DELETE FROM " + TarmedKumulation.getDBTableName()); //$NON-NLS-1$
			ipm.subTask(Messages.TarmedImporter_definitions);
			importDefinition("ANAESTHESIE", "DIGNI_QUALI", "DIGNI_QUANTI", "LEISTUNG_BLOECKE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				"LEISTUNG_GRUPPEN", "LEISTUNG_TYP", "PFLICHT", "REGEL_EL_ABR", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				"SEITE", "SEX", "SPARTE", "ZR_EINHEIT"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			ipm.worked(13);
			ipm.subTask(Messages.TarmedImporter_chapter);
			try(ResultSet res = source.query(
				String.format("SELECT * FROM %sKAPITEL_TEXT WHERE SPRACHE=%s", ImportPrefix, lang))) {
				while (res != null && res.next()) {
					String code = res.getString("KNR"); //$NON-NLS-1$
					
					if (code.trim().equals("I")) { //$NON-NLS-1$
						continue;
					}
					TarmedLeistung tl = TarmedLeistung.load(code);
					String txt = convert(res, "BEZ_255"); //$NON-NLS-1$
					int subcap = code.lastIndexOf('.');
					String parent = "NIL"; //$NON-NLS-1$
					if (subcap != -1) {
						parent = code.substring(0, subcap);
					}
					if ((!tl.exists()) || (!parent.equals(tl.get("Parent")))) { //$NON-NLS-1$
						tl = new TarmedLeistung(code, parent, "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					if (tl.exists()) {
						tl.setText(txt);
						tl.flushExtension();
					}
					ipm.worked(1);
				}
			}
			ipm.subTask(Messages.TarmedImporter_singleLst);
			ResultSet res = source.query(String.format("SELECT * FROM %sLEISTUNG", ImportPrefix)); //$NON-NLS-1$
			PreparedStatement preps_extension =
				pj.prepareStatement("UPDATE TARMED_EXTENSION SET MED_INTERPRET=?,TECH_INTERPRET=? WHERE CODE=?"); //$NON-NLS-1$
			TimeTool validFrom = new TimeTool();
			while (res.next() == true) {
				validFrom.set(res.getString("GUELTIG_VON"));
				String id = res.getString("LNR") + "-" + validFrom.toString(TimeTool.DATE_COMPACT); //$NON-NLS-1$			
				
				TarmedLeistung tl = TarmedLeistung.load(id);
				if (tl.exists()) {
					continue;
				} else {
					tl = new TarmedLeistung(id, res.getString("LNR"), res.getString("KNR"), //$NON-NLS-1$
						"0000", convert(res, "QT_DIGNITAET"), convert(res, "Sparte")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				TimeSpan tsValid =
					new TimeSpan(new TimeTool(res.getString("GUELTIG_VON")), new TimeTool(
						res.getString("GUELTIG_BIS")));
				logger.debug(tsValid.dump());
				
				tl.set(new String[] {
					"GueltigVon", "GueltigBis" //$NON-NLS-1$ //$NON-NLS-2$
				}, tsValid.from.toString(TimeTool.DATE_COMPACT),
					tsValid.until.toString(TimeTool.DATE_COMPACT)); //$NON-NLS-1$ //$NON-NLS-2$
				Stm stmCached = cacheDb.getStatement();
				
				// get QL_DIGNITAET
				String dqua = "";
				ResultSet rsub =
					stmCached.query(String.format(
						"SELECT * FROM %sLEISTUNG_DIGNIQUALI WHERE LNR=%s", ImportPrefix,
						JdbcLink.wrap(tl.getCode()))); //$NON-NLS-1$
				List<Map<String, String>> validResults = getValidValueMaps(rsub, validFrom);
				if (!validResults.isEmpty()) {
					dqua = getLatestMap(validResults).get("QL_DIGNITAET");
				}
				rsub.close();
				
				// get BEZ_255, MED_INTERPRET, TECH_INTERPRET
				String kurz = ""; //$NON-NLS-1$
				rsub =
					stmCached.query(String.format(
						"SELECT * FROM %sLEISTUNG_TEXT WHERE SPRACHE=%s AND LNR=%s", ImportPrefix,
						lang, JdbcLink.wrap(tl.getCode()))); //$NON-NLS-1$
				validResults = getAllValueMaps(rsub);
				if (!validResults.isEmpty()) {
					Map<String, String> row = getLatestMap(validResults);
					kurz = row.get("BEZ_255"); //$NON-NLS-1$
					String med = row.get("MED_INTERPRET"); //$NON-NLS-1$
					String tech = row.get("TECH_INTERPRET"); //$NON-NLS-1$
					preps_extension.setString(1, med);
					preps_extension.setString(2, tech);
					preps_extension.setString(3, tl.getId());
					preps_extension.execute();
				}
				rsub.close();
				tl.set(new String[] {
					"DigniQuali", "Text"}, dqua, kurz); //$NON-NLS-1$ //$NON-NLS-2$
				
				Hashtable<String, String> ext = tl.loadExtension();
				put(ext, res, "LEISTUNG_TYP", "SEITE", "SEX", "ANAESTHESIE", "K_PFL", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					"BEHANDLUNGSART", "TP_AL", "TP_ASSI", "TP_TL", "ANZ_ASSI", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					"LSTGIMES_MIN", "VBNB_MIN", "BEFUND_MIN", "RAUM_MIN", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					"WECHSEL_MIN", "F_AL", "F_TL"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				
				// get LNR_MASTER
				rsub =
					stmCached.query(String.format(
						"SELECT * FROM %sLEISTUNG_HIERARCHIE WHERE LNR_SLAVE=%s", ImportPrefix,
						JdbcLink.wrap(tl.getCode()))); //$NON-NLS-1$
				validResults = getValidValueMaps(rsub, validFrom);
				if (!validResults.isEmpty()) {
					// importing all bezugs ziffer will mess up tarmed bill -> just import 1st
					// StringBuilder sb = new StringBuilder();
					// for (Map<String, String> map : validResults) {
					// if (sb.length() == 0)
					// sb.append(map.get("LNR_MASTER"));
					// else
					// sb.append(", " + map.get("LNR_MASTER"));
					// }
					Map<String, String> what = validResults.get(0);
					if (what != null) {
						String content = what.get("LNR_MASTER"); //$NON-NLS-1$
						if (content != null)
							ext.put("Bezug", content); //$NON-NLS-1$
					}
				}
				rsub.close();
				
				// get LNR_SLAVE, TYP
				rsub =
					stmCached.query(String.format(
						"SELECT * FROM %sLEISTUNG_KOMBINATION WHERE LNR_MASTER=%s", ImportPrefix,
						JdbcLink.wrap(tl.getCode()))); //$NON-NLS-1$
				String kombination_and = ""; //$NON-NLS-1$
				String kombination_or = ""; //$NON-NLS-1$
				validResults = getValidValueMaps(rsub, validFrom);
				if (!validResults.isEmpty()) {
					for (Map<String, String> map : validResults) {
						String typ = map.get("TYP");
						String slave = map.get("LNR_SLAVE");
						if (typ != null) {
							if (typ.equals("and")) { //$NON-NLS-1$
								kombination_and += slave + ","; //$NON-NLS-1$
							} else if (typ.equals("or")) { //$NON-NLS-1$
								kombination_or += slave + ","; //$NON-NLS-1$
							}
						}
					}
				}
				rsub.close();
				if (!kombination_and.equals("")) { //$NON-NLS-1$
					String k = kombination_and.replaceFirst(",$", ""); //$NON-NLS-1$ //$NON-NLS-2$
					ext.put("kombination_and", k); //$NON-NLS-1$
				}
				if (!kombination_or.equals("")) { //$NON-NLS-1$
					String k = kombination_or.replaceFirst(",$", ""); //$NON-NLS-1$ //$NON-NLS-2$
					ext.put("kombination_or", k); //$NON-NLS-1$
				}
				
				// get LNR_SLAVE, TYP (invalid combinations with other codes)
				importKumulations(tl.getCode(), stmCached);
				
				// get OPERATOR, MENGE, ZR_ANZAHL, PRO_NACH, ZR_EINHEIT
				rsub =
					stmCached.query(String.format(
						"SELECT * FROM %sLEISTUNG_MENGEN_ZEIT WHERE LNR=%s", ImportPrefix,
						JdbcLink.wrap(tl.getCode()))); //$NON-NLS-1$
				String limits = ""; //$NON-NLS-1$
				validResults = getValidValueMaps(rsub, validFrom);
				if (!validResults.isEmpty()) {
					for (Map<String, String> map : validResults) {
						StringBuilder sb = new StringBuilder();
						sb.append(map.get("OPERATOR")).append(","); //$NON-NLS-1$ //$NON-NLS-2$
						sb.append(map.get("MENGE")).append(","); //$NON-NLS-1$ //$NON-NLS-2$
						sb.append(map.get("ZR_ANZAHL")).append(","); //$NON-NLS-1$ //$NON-NLS-2$
						sb.append(map.get("PRO_NACH")).append(","); //$NON-NLS-1$ //$NON-NLS-2$
						sb.append(map.get("ZR_EINHEIT")).append("#"); //$NON-NLS-1$ //$NON-NLS-2$
						limits += sb.toString();
					}
				}
				rsub.close();
				if (!limits.equals("")) { //$NON-NLS-1$
					ext.put("limits", limits); //$NON-NLS-1$
				}
				tl.flushExtension();
				cacheDb.releaseStatement(stmCached);
				
				ipm.worked(1);
				if (ipm.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
			}
			res.close();
			
			if (updateIDs) {
				updateExistingIDs(ipm);
			}
			
			if (version == null) {
				TarmedLeistung.setVersion(new TimeTool().toString(TimeTool.DATE_COMPACT));
			} else {
				TarmedLeistung.setVersion(version.toString());
			}
			
			ipm.done();
			String message = Messages.TarmedImporter_successMessage;
			if (updateBlockWarning) {
				message = message + "\n" + Messages.TarmedImporter_updateBlockWarning;
			}
			if (showRestartDialog) {
				SWTHelper.showInfo(Messages.TarmedImporter_successTitle, message);
			}
			return Status.OK_STATUS;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			ExHandler.handle(ex);
		} finally {
			if (source != null) {
				pj.releaseStatement(source);
			}
			if (dest != null) {
				pj.releaseStatement(dest);
			}
			if (deleteCachedAccessTables(ipm) != Status.OK_STATUS) {
				mdbDB = null;
				return Status.CANCEL_STATUS;
			}
			mdbDB = null;
		}
		return Status.CANCEL_STATUS;
		
	}
	
	/**
	 * Import all the kumulations from the LEISTUNG_KUMULATION table for the given code. The
	 * kumulations contain inclusions, exclusions and exclusives.
	 * 
	 * @param code
	 *            of a tarmed value
	 * @param stmCached
	 * @throws SQLException
	 */
	private void importKumulations(String code, Stm stmCached) throws SQLException{
		try(ResultSet res =
			stmCached.query(String.format("SELECT * FROM %sLEISTUNG_KUMULATION WHERE LNR_MASTER=%s",
				ImportPrefix, JdbcLink.wrap(code)))) {
			TimeTool fromTime = new TimeTool();
			TimeTool toTime = new TimeTool();
			
			while (res != null && res.next()) {
				fromTime.set(res.getString("GUELTIG_VON"));
				toTime.set(res.getString("GUELTIG_BIS"));
				
				new TarmedKumulation(code, res.getString("ART_MASTER"), res.getString("LNR_SLAVE"),
					res.getString("ART_SLAVE"), res.getString("TYP"), res.getString("ANZEIGE"),
					res.getString("GUELTIG_SEITE"), fromTime.toString(TimeTool.DATE_COMPACT),
					toTime.toString(TimeTool.DATE_COMPACT));
			}
		}
	}
	
	/**
	 * Import all Access tables (using cache cachedDbTables)
	 */
	private IStatus importAllAccessTables(final IProgressMonitor monitor){
		String tablename = "";
		double weight = 0.1; // a work unit here is much less work than in the final import
		Iterator<String> iter;
		int totRows = 0;
		try {
			int nrTables = cachedDbTables.size();
			iter = cachedDbTables.iterator();
			iter = cachedDbTables.iterator();
			while (iter.hasNext()) {
				tablename = iter.next();
				totRows += mdbDB.getTable(tablename).getRowCount();
			}
			monitor.beginTask(Messages.TarmedImporter_importLstg, (int) (totRows * weight)
				+ mdbDB.getTable("LEISTUNG").getRowCount()
				+ mdbDB.getTable("KAPITEL_TEXT").getRowCount());
			
			int j = 0;
			iter = cachedDbTables.iterator();
			while (iter.hasNext()) {
				j++;
				tablename = iter.next();
				String msg =
					String.format(Messages.TarmedImporter_convertTable, tablename, ImportPrefix
						+ tablename, j, nrTables, mdbDB.getTable(tablename).getRowCount(),
						mdbFilename);
				monitor.subTask(msg);
				try {
					int nrRows = aw.convertTable(tablename, cacheDb);
					monitor.worked((int) (nrRows * weight));
				} catch (SQLException e) {
					logger.error("Failed to import table " + tablename, e);
					return Status.CANCEL_STATUS;
				}
			}
			return Status.OK_STATUS;
		} catch (IOException e) {
			logger.error("Failed to process access file " + mdbFilename, e);
			return Status.CANCEL_STATUS;
		}
	}
	
	private IStatus openAccessDatabase(final IProgressMonitor monitor, InputStream inputStream){
		File file = convertInputStreamToFile(inputStream);
		if (mdbFilename == null)
			mdbFilename = file.getName();
		try {
			aw = new AccessWrapper(file);
			aw.setPrefixForImportedTableNames(ImportPrefix);
			mdbDB = Database.open(file, true, Database.DEFAULT_AUTO_SYNC);
			cachedDbTables = mdbDB.getTableNames();
		} catch (IOException e) {
			logger.error("Failed to open access file " + file, e);
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}
	
	private IStatus deleteCachedAccessTables(final IProgressMonitor monitor){
		String tablename = "";
		Iterator<String> iter;
		iter = cachedDbTables.iterator();
		while (iter.hasNext()) {
			tablename = iter.next();
			cacheDb.exec("DROP TABLE IF EXISTS " + tablename);//$NON-NLS-1$
		}
		return Status.OK_STATUS;
	}
	
	private void importDefinition(final String... strings) throws IOException, SQLException{
		
		Stm stm = pj.getStatement();
		Stm stmCached = cacheDb.getStatement();
		PreparedStatement ps =
			pj.prepareStatement("INSERT INTO TARMED_DEFINITIONEN (Spalte,Kuerzel,Titel) VALUES (?,?,?)"); //$NON-NLS-1$
		try {
			for (String s : strings) {
				ResultSet res =
					stmCached.query(String.format(
						"SELECT * FROM %sCT_" + s + " WHERE SPRACHE=%s", ImportPrefix, lang)); //$NON-NLS-1$
				while (res.next()) {
					ps.setString(1, s);
					ps.setString(2, res.getString(1));
					ps.setString(3, res.getString(3));
					ps.execute();
				}
				res.close();
			}
		} catch (Exception ex) {
			ExHandler.handle(ex);
		} finally {
			pj.releaseStatement(stm);
			cacheDb.releaseStatement(stmCached);
		}
	}
	
	private String convert(ResultSet res, String field) throws Exception{
		Reader reader = res.getCharacterStream(field);
		if (reader == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(reader);
		int c;
		while ((c = br.read()) != -1) {
			sb.append((char) c);
		}
		return sb.toString();
	}
	
	private void put(final Hashtable<String, String> h, final ResultSet r, final String... vv)
		throws Exception{
		for (String v : vv) {
			String val = r.getString(v);
			if (val != null) {
				h.put(v, val);
			}
		}
	}
	
	private Map<String, String> getLatestMap(List<Map<String, String>> list){
		TimeTool currFrom = new TimeTool("19000101");
		TimeTool from = new TimeTool();
		Map<String, String> ret = null;
		for (Map<String, String> map : list) {
			from.set(map.get("GUELTIG_VON"));
			if (from.isAfter(currFrom)) {
				currFrom.set(from);
				ret = map;
			}
		}
		return ret;
	}
	
	/**
	 * Get a List of Maps containing the rows of the ResultSet with a matching valid date
	 * information. This is needed as we can not make constraints on a date represented as string in
	 * the db.
	 * 
	 * @param input
	 * @param validFrom
	 * @return
	 * @throws SQLException
	 */
	private List<Map<String, String>> getValidValueMaps(ResultSet input, TimeTool validFrom)
		throws Exception{
		List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
		
		// build list of column names
		ArrayList<String> headers = new ArrayList<String>();
		ResultSetMetaData meta = input.getMetaData();
		int metaLength = meta.getColumnCount();
		for (int i = 1; i <= metaLength; i++) {
			headers.add(meta.getColumnName(i));
		}
		
		TimeTool from = new TimeTool();
		TimeTool to = new TimeTool();
		
		// find rows with matching valid date information
		while (input.next()) {
			from.set(input.getString("GUELTIG_VON"));
			to.set(input.getString("GUELTIG_BIS")); //$NON-NLS-1$
			// is this the correct result
			if (validFrom.isAfterOrEqual(from) && validFrom.isBeforeOrEqual(to)) {
				HashMap<String, String> valuesMap = new HashMap<String, String>();
				// put all the columns with values into valuesMap
				for (String columnName : headers) {
					String value = convert(input, columnName);
					valuesMap.put(columnName, value);
				}
				// add map to list of matching maps
				ret.add(valuesMap);
			}
		}
		return ret;
	}
	
	/**
	 * Get a List of Maps containing the rows of the ResultSet with a matching valid date
	 * information. This is needed as we can not make constraints on a date represented as string in
	 * the db.
	 * 
	 * @param input
	 * @param validFrom
	 * @return
	 * @throws SQLException
	 */
	private List<Map<String, String>> getAllValueMaps(ResultSet input) throws Exception{
		List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
		
		// build list of column names
		ArrayList<String> headers = new ArrayList<String>();
		ResultSetMetaData meta = input.getMetaData();
		int metaLength = meta.getColumnCount();
		for (int i = 1; i <= metaLength; i++) {
			headers.add(meta.getColumnName(i));
		}
		
		// find rows with matching valid date information
		while (input.next()) {
			HashMap<String, String> valuesMap = new HashMap<String, String>();
			// put all the columns with values into valuesMap
			for (String columnName : headers) {
				String value = convert(input, columnName);
				valuesMap.put(columnName, value);
			}
			// add map to list of matching maps
			ret.add(valuesMap);
		}
		return ret;
	}
	
	void updateExistingIDs(final IProgressMonitor monitor){
		PreparedStatement ps = null;
		// update existing ids of Verrechnet
		try {
			ps =
				pj.prepareStatement("UPDATE " + Verrechnet.TABLENAME + " SET leistg_code=? WHERE id=?"); //$NON-NLS-1$
			
			Query<Verrechnet> vQuery = new Query<Verrechnet>(Verrechnet.class);
			vQuery.add(Verrechnet.CLASS, "=", TarmedLeistung.class.getName());
			List<Verrechnet> verrechnete = vQuery.execute();
			for (Verrechnet verrechnet : verrechnete) {
				// make sure code and date of consultation are available
				String code = verrechnet.get(Verrechnet.LEISTG_CODE);
				TimeTool date = null;
				Konsultation kons = verrechnet.getKons();
				if (kons != null && kons.getDatum() != null)
					date = new TimeTool(kons.getDatum());
				if (code != null && date != null) {
					monitor.subTask(Messages.TarmedImporter_updateVerrechnet + " " + code + " "
						+ date.toString(TimeTool.DATE_COMPACT));
					TarmedLeistung leistung =
						(TarmedLeistung) TarmedLeistung.getFromCode(code, date);
					// update the id
					if (leistung != null) {
						ps.setString(1, leistung.getId());
						ps.setString(2, verrechnet.getId());
						ps.execute();
					}
				}
				Thread.yield();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			ExHandler.handle(e);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
					ExHandler.handle(e);
				}
			}
		}
		
		// update existing ids of Leistungsblock
		try {
			Query<Leistungsblock> lQuery = new Query<Leistungsblock>(Leistungsblock.class);
			List<Leistungsblock> blocks = lQuery.execute();
			for (Leistungsblock block : blocks) {
				StringBuilder newCodes = new StringBuilder();
				// get blob
				byte[] compressed =
					getBinaryRaw(Leistungsblock.FLD_LEISTUNGEN, Leistungsblock.TABLENAME, block.getId());
				if (compressed != null) {
					// get String representing all contained leistungen
					String storable = new String(CompEx.expand(compressed), "UTF-8"); //$NON-NLS-1$
					// rebuild a String containing all leistungen but update TarmedLeistungen
					for (String p : storable.split(",")) {
						if (p != null && !p.isEmpty()) {
							String[] parts = p.split("::");
							if (parts[0].equals(TarmedLeistung.class.getName())) {
								monitor.subTask(Messages.TarmedImporter_updateBlock + " "
									+ parts[1]);
								TarmedLeistung leistung =
									(TarmedLeistung) TarmedLeistung.getFromCode(parts[1]);
								if (leistung != null) {
									// add new string
									if (newCodes.length() > 0)
										newCodes.append(",");
									newCodes.append(leistung.storeToString());
								} else {
									updateBlockWarning = true;
									// set string old string
									if (newCodes.length() > 0)
										newCodes.append(",");
									newCodes.append(p);
								}
							} else {
								if (newCodes.length() > 0)
									newCodes.append(",");
								newCodes.append(p);
							}
						}
					}
					// write the updated String back
					setBinaryRaw(Leistungsblock.FLD_LEISTUNGEN, Leistungsblock.TABLENAME,
						block.getId(), CompEx.Compress(newCodes.toString(), CompEx.ZIP));
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			ExHandler.handle(e);
		}
		
		// update existing ids in statistics
		Query<Kontakt> kQuery = new Query<Kontakt>(Kontakt.class);
		List<Kontakt> kontakte = kQuery.execute();
		for (Kontakt kontakt : kontakte) {
			Map exi = kontakt.getMap(Kontakt.FLD_EXTINFO);
			String typ = TarmedLeistung.class.getName();
			// get list of type
			List l = (List) exi.get(typ);
			if (l != null) {
				// we dont have access to statL.v member so update is not possible
				// for (Kontakt.statL statL : l) {
				// String[] ci = statL.v.split("::");
				// if (ci.length == 2) {
				// TarmedLeistung leistung =
				// (TarmedLeistung) TarmedLeistung.getFromCode(ci[1]);
				// if (leistung != null)
				// statL.v = leistung.storeToString();
				// }
				// }
				// clear existing statistics
				l.clear();
				exi.put(typ, l);
				kontakt.setMap(Kontakt.FLD_EXTINFO, exi);
			}
		}
	}
	
	/**
	 * Copy of method from PersistentObject to get access to a binary field
	 * 
	 * @param field
	 * @return
	 */
	private byte[] getBinaryRaw(final String field, String tablename, String id){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ").append(field).append(" FROM ").append(tablename)
			.append(" WHERE ID='").append(id).append("'");
		
		Stm stm = null;
		try {
			stm = pj.getStatement();
			ResultSet res = stm.query(sql.toString());
			if ((res != null) && (res.next() == true)) {
				return res.getBytes(field);
			}
		} catch (Exception ex) {
			ExHandler.handle(ex);
		} finally {
			if (stm != null) {
				pj.releaseStatement(stm);
			}
		}
		return null;
	}
	
	/**
	 * Copy of method from PersistentObject to get access to a binary field
	 * 
	 * @param field
	 * @return
	 */
	private void setBinaryRaw(final String field, String tablename, String id, final byte[] value){
		StringBuilder sql = new StringBuilder(1000);
		sql.append("UPDATE ").append(tablename).append(" SET ").append((field)).append("=?")
			.append(" WHERE ID='").append(id).append("'");
		String cmd = sql.toString();
		
		PreparedStatement stm = pj.prepareStatement(cmd);
		try {
			stm.setBytes(1, value);
			stm.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
			ExHandler.handle(ex);
		} finally {
			try {
				stm.close();
			} catch (SQLException e) {
				ExHandler.handle(e);
				throw new PersistenceException("Could not close statement " + e.getMessage());
			}
		}
	}
	
	private File convertInputStreamToFile(InputStream input){
		String prefix = "tarmed_db";
		String suffix = "tmp";
		
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile(prefix, suffix);
			tmpFile.deleteOnExit();
			FileOutputStream out = new FileOutputStream(tmpFile);
			IOUtils.copy(input, out);
		} catch (IOException e) {
			logger.error("Error reading input stream ...", e);
		}
		return tmpFile;
	}
	
	private void setMdbFilename(String filename){
		mdbFilename = filename;
	}
}
