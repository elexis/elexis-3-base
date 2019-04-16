package ch.elexis.data.importer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.data.TarmedExtension;
import ch.elexis.data.TarmedKumulation;
import ch.elexis.data.TarmedLeistung;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.TimeTool;

public class ServiceImporter {
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceImporter.class);
	
	private JdbcLink cacheDb;
	private String lang;
	private String law;
	
	private ChapterImporter chapterImporter;
	
	// fields relevant for the currently imported service
	private String code;
	private TarmedExtension extension;
	private Hashtable<String, String> extensionMap;
	private TimeTool validFrom;
	private TimeTool validTo;
	
	private int serviceCount;
	
	public ServiceImporter(JdbcLink cacheDb, ChapterImporter chapterImporter, String lang,
		String law){
		this.cacheDb = cacheDb;
		this.lang = lang;
		this.law = law;
		this.chapterImporter = chapterImporter;
		this.validFrom = new TimeTool();
		this.validTo = new TimeTool();
	}
	
	public void setServiceCount(int count){
		this.serviceCount = count;
	}
	
	public IStatus doImport(IProgressMonitor ipm) throws SQLException, IOException{
		Stm servicesStm = null;
		try {
			ipm.subTask(Messages.TarmedImporter_singleLst);
			
			int count = 0;
			servicesStm = cacheDb.getStatement();
			ResultSet res = servicesStm.query(String.format("SELECT * FROM %sLEISTUNG", //$NON-NLS-1$
				TarmedReferenceDataImporter.ImportPrefix));
			while (res.next()) {
				code = res.getString("LNR");
				initValidTime(res);
				String id = getId(res);
				
				TarmedLeistung tl = TarmedLeistung.load(id);
				if (tl.exists()) {
					logger.debug("Skipped " + tl.getLabel());
					ipm.worked(1);
					continue;
				} else {
					String parentId = getParentId(res.getString("KNR"));
					if (parentId == null) {
						throw new IllegalStateException(
							"Could not find parentId for chapter number [" + res.getString("KNR")
								+ "] and service id [" + id + "]");
					}
					tl = new TarmedLeistung(id, code, parentId, //$NON-NLS-1$
						"0000", ImporterUtil.getAsString(res, "QT_DIGNITAET"), //$NON-NLS-1$//$NON-NLS-2$
						ImporterUtil.getAsString(res, "Sparte"), false); //$NON-NLS-1$
					tl.set(new String[] {
						TarmedLeistung.FLD_GUELTIG_VON, TarmedLeistung.FLD_GUELTIG_BIS,
						TarmedLeistung.FLD_LAW
					}, validFrom.toString(TimeTool.DATE_COMPACT),
						validTo.toString(TimeTool.DATE_COMPACT), law);
					extension = tl.getExtension();
					extensionMap = tl.loadExtension();
					
					if (hasColumn(res, "F_AL_R")) {
						ImporterUtil.putResultSetToMap(extensionMap, res, "LEISTUNG_TYP", "SEITE", //$NON-NLS-1$//$NON-NLS-2$
							"SEX", "ANAESTHESIE", "K_PFL", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							"BEHANDLUNGSART", "TP_AL", "TP_ASSI", "TP_TL", "ANZ_ASSI", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
							"LSTGIMES_MIN", "VBNB_MIN", "BEFUND_MIN", "RAUM_MIN", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"WECHSEL_MIN", "F_AL", "F_TL", "F_AL_R"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					} else {
						ImporterUtil.putResultSetToMap(extensionMap, res, "LEISTUNG_TYP", "SEITE", //$NON-NLS-1$//$NON-NLS-2$
							"SEX", "ANAESTHESIE", "K_PFL", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							"BEHANDLUNGSART", "TP_AL", "TP_ASSI", "TP_TL", "ANZ_ASSI", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
							"LSTGIMES_MIN", "VBNB_MIN", "BEFUND_MIN", "RAUM_MIN", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"WECHSEL_MIN", "F_AL", "F_TL"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					// get QL_DIGNITAET
					String dqua = getQLDignitaet();
					
					// get BEZ_255, MED_INTERPRET, TECH_INTERPRET
					String[] texts = getTexts();
					tl.set(new String[] {
						"DigniQuali", "Text" //$NON-NLS-1$//$NON-NLS-2$
					}, dqua, texts[0]);
					if (texts[1] != null) {
						extension.set(TarmedExtension.FLD_MED_INTERPRET, texts[1]);
					}
					if (texts[2] != null) {
						extension.set(TarmedExtension.FLD_TECH_INTERPRET, texts[2]);
					}
					
					// get LEISTUNG_HIERARCHIE
					String slaves = getSlavesString();
					extensionMap.put(TarmedLeistung.EXT_FLD_HIERARCHY_SLAVES, slaves);
					
					// get LEISTUNG_GRUPPEN
					String groups = getGroups();
					extensionMap.put(TarmedLeistung.EXT_FLD_SERVICE_GROUPS, groups);
					
					// get LEISTUNG_BLOECKE
					String blocks = getBlocks();
					extensionMap.put(TarmedLeistung.EXT_FLD_SERVICE_BLOCKS, blocks);
					
					// get LEISTUNG_ALTER
					String age = getAge();
					extensionMap.put(TarmedLeistung.EXT_FLD_SERVICE_AGE, age);
					
					// get LEISTUNG_KOMBINATION
					String[] combinations = getCombinations();
					if (combinations[0] != null) {
						extensionMap.put("kombination_and", combinations[0]);
					}
					if (combinations[1] != null) {
						extensionMap.put("kombination_or", combinations[1]);
					}
					
					// get OPERATOR, MENGE, ZR_ANZAHL, PRO_NACH, ZR_EINHEIT
					String limits = getLimits();
					extensionMap.put("limits", limits);
					
					// get LNR_SLAVE, TYP (invalid combinations with other codes)
					importKumulations();
					
					tl.setExtension(extensionMap);
					
					logger.debug("Imported " + tl.getLabel());
					ipm.worked(1);
					ipm.subTask(Messages.TarmedImporter_singleLst + " (" + count++ + "/"
						+ serviceCount + ")");
				}
				
				if (ipm.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
			}
			res.close();
			logger.debug("Imported " + count + " services");
		} finally {
			if (servicesStm != null) {
				cacheDb.releaseStatement(servicesStm);
			}
		}
		return Status.OK_STATUS;
	}
	
	private HashMap<String, Integer> columnMap = new HashMap<>();
	
	private boolean hasColumn(ResultSet res, String columnLabel){
		Integer index = columnMap.get(columnLabel);
		if (index != null && index > 0) {
			return true;
		} else if (index == null) {
			try {
				int found = res.findColumn(columnLabel);
				columnMap.put(columnLabel, found);
				return true;
			} catch (SQLException ex) {
				columnMap.put(columnLabel, -1);
				return false;
			}
		}
		return false;
	}
	
	private String getParentId(String chapterCode){
		return chapterImporter.getIdForCode(chapterCode, validFrom, law);
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
	private void importKumulations() throws SQLException{
		Stm subStm = cacheDb.getStatement();
		try {
			try (ResultSet res = subStm
				.query(String.format(
					"SELECT * FROM %sLEISTUNG_KUMULATION WHERE LNR_MASTER=%s AND ART_MASTER='L'",
					TarmedReferenceDataImporter.ImportPrefix, JdbcLink.wrap(code)))) {
				TimeTool fromTime = new TimeTool();
				TimeTool toTime = new TimeTool();
				
				while (res != null && res.next()) {
					fromTime.set(res.getString("GUELTIG_VON"));
					toTime.set(res.getString("GUELTIG_BIS"));
					
					new TarmedKumulation(code, res.getString("ART_MASTER"),
						res.getString("LNR_SLAVE"), res.getString("ART_SLAVE"),
						res.getString("TYP"), res.getString("ANZEIGE"),
						res.getString("GUELTIG_SEITE"), fromTime.toString(TimeTool.DATE_COMPACT),
						toTime.toString(TimeTool.DATE_COMPACT), law);
				}
			}
		} finally {
			if (subStm != null) {
				cacheDb.releaseStatement(subStm);
			}
		}
	}
	
	private String[] getCombinations() throws SQLException, IOException{
		String[] ret = new String[2];
		StringBuilder sbAnd = new StringBuilder();
		StringBuilder sbOr = new StringBuilder();
		Stm subStm = cacheDb.getStatement();
		try {
			ResultSet rsub = subStm
				.query(String.format("SELECT * FROM %sLEISTUNG_KOMBINATION WHERE LNR_MASTER=%s",
					TarmedReferenceDataImporter.ImportPrefix, JdbcLink.wrap(code))); //$NON-NLS-1$
			List<Map<String, String>> validResults =
				ImporterUtil.getValidValueMaps(rsub, validFrom);
			if (!validResults.isEmpty()) {
				for (Map<String, String> map : validResults) {
					String typ = map.get("TYP");
					String slave = map.get("LNR_SLAVE");
					if (typ != null) {
						if (typ.equals("and")) { //$NON-NLS-1$
							if (sbAnd.length() > 0) {
								sbAnd.append(",");
							}
							sbAnd.append(slave); //$NON-NLS-1$
						} else if (typ.equals("or")) { //$NON-NLS-1$
							if (sbOr.length() > 0) {
								sbOr.append(",");
							}
							sbOr.append(slave); //$NON-NLS-1$
						}
					}
				}
			}
			rsub.close();
		} finally {
			if (subStm != null) {
				cacheDb.releaseStatement(subStm);
			}
		}
		ret[0] = sbAnd.toString();
		ret[1] = sbOr.toString();
		return ret;
	}
	
	private String getLimits() throws SQLException, IOException{
		StringBuilder sb = new StringBuilder();
		Stm subStm = cacheDb.getStatement();
		try {
			ResultSet rsub =
				subStm.query(
					String.format("SELECT * FROM %sLEISTUNG_MENGEN_ZEIT WHERE LNR=%s AND ART='L'",
					TarmedReferenceDataImporter.ImportPrefix, JdbcLink.wrap(code))); //$NON-NLS-1$
			List<Map<String, String>> validResults =
				ImporterUtil.getValidValueMaps(rsub, validFrom);
			if (!validResults.isEmpty()) {
				for (Map<String, String> map : validResults) {
					sb.append(map.get("OPERATOR")).append(","); //$NON-NLS-1$ //$NON-NLS-2$
					sb.append(map.get("MENGE")).append(","); //$NON-NLS-1$ //$NON-NLS-2$
					sb.append(map.get("ZR_ANZAHL")).append(","); //$NON-NLS-1$ //$NON-NLS-2$
					sb.append(map.get("PRO_NACH")).append(","); //$NON-NLS-1$ //$NON-NLS-2$
					sb.append(map.get("ZR_EINHEIT")).append(","); //$NON-NLS-1$ //$NON-NLS-2$
					sb.append(map.get("REGEL_EL_ABR")).append("#"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			rsub.close();
		} finally {
			if (subStm != null) {
				cacheDb.releaseStatement(subStm);
			}
		}
		return sb.toString();
	}
	
	private String getBlocks() throws SQLException, IOException{
		StringBuilder sb = new StringBuilder();
		Stm subStm = cacheDb.getStatement();
		try {
			ResultSet rsub =
				subStm.query(String.format("SELECT * FROM %sLEISTUNG_BLOECKE WHERE LNR=%s",
					TarmedReferenceDataImporter.ImportPrefix, JdbcLink.wrap(code))); //$NON-NLS-1$
			List<Map<String, String>> validResults = ImporterUtil.getAllValueMaps(rsub);
			if (!validResults.isEmpty()) {
				for (Map<String, String> map : validResults) {
					if (sb.length() == 0) {
						sb.append(map.get("BLOCK"));
					} else {
						sb.append(", " + map.get("BLOCK"));
					}
					LocalDate from = ImporterUtil.getLocalDate(map, "GUELTIG_VON");
					LocalDate to = LocalDate.parse(map.get("GUELTIG_BIS"),
						DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.S"));
					sb.append("[").append(from.toString()).append("|").append(to.toString())
						.append("]");
				}
			}
			rsub.close();
		} finally {
			if (subStm != null) {
				cacheDb.releaseStatement(subStm);
			}
		}
		return sb.toString();
	}
	
	private String getGroups() throws SQLException, IOException{
		StringBuilder sb = new StringBuilder();
		Stm subStm = cacheDb.getStatement();
		try {
			ResultSet rsub =
				subStm.query(String.format("SELECT * FROM %sLEISTUNG_GRUPPEN WHERE LNR=%s",
					TarmedReferenceDataImporter.ImportPrefix, JdbcLink.wrap(code))); //$NON-NLS-1$
			List<Map<String, String>> validResults = ImporterUtil.getAllValueMaps(rsub);
			if (!validResults.isEmpty()) {
				for (Map<String, String> map : validResults) {
					if (sb.length() == 0) {
						sb.append(map.get("GRUPPE"));
					} else {
						sb.append(", " + map.get("GRUPPE"));
					}
					LocalDate from = ImporterUtil.getLocalDate(map, "GUELTIG_VON");
					LocalDate to = LocalDate.parse(map.get("GUELTIG_BIS"),
						DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.S"));
					sb.append("[").append(from.toString()).append("|").append(to.toString())
						.append("]");
				}
			}
			rsub.close();
		} finally {
			if (subStm != null) {
				cacheDb.releaseStatement(subStm);
			}
		}
		return sb.toString();
	}
	
	private String getAge() throws SQLException, IOException{
		StringBuilder sb = new StringBuilder();
		Stm subStm = cacheDb.getStatement();
		try {
			ResultSet rsub =
				subStm.query(String.format("SELECT * FROM %sLEISTUNG_ALTER WHERE LNR=%s",
					TarmedReferenceDataImporter.ImportPrefix, JdbcLink.wrap(code))); //$NON-NLS-1$
			List<Map<String, String>> validResults = ImporterUtil.getAllValueMaps(rsub);
			if (!validResults.isEmpty()) {
				for (Map<String, String> map : validResults) {
					try {
						StringBuilder def = new StringBuilder();
						if (sb.length() == 0) {
							def.append(getAgeDefinition(map));
						} else {
							def.append(", " + getAgeDefinition(map));
						}
						LocalDate from = ImporterUtil.getLocalDate(map, "GUELTIG_VON");
						LocalDate to = LocalDate.parse(map.get("GUELTIG_BIS"),
							DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.S"));
						def.append("[").append(from.toString()).append("|").append(to.toString())
							.append("]");
						
						sb.append(def.toString());
					} catch (IllegalStateException e) {
						logger.warn("Exception on age import, continuing", e);
					}
				}
			}
			rsub.close();
		} finally {
			if (subStm != null) {
				cacheDb.releaseStatement(subStm);
			}
		}
		return sb.toString();
	}
	
	private String getAgeDefinition(Map<String, String> map){
		StringBuilder sb = new StringBuilder();
		int fromAge = getAgeInt(map.get("VON_ALTER"), -1);
		int toAge = getAgeInt(map.get("BIS_ALTER"), -1);
		if (checkValidRange(fromAge, toAge)) {
			sb.append(fromAge);
			sb.append("|");
			sb.append(getToleranceInt(map.get("VON_TOLERANZ"), 0));
			sb.append("|");
			sb.append(toAge);
			sb.append("|");
			sb.append(getToleranceInt(map.get("BIS_TOLERANZ"), 0));
			sb.append("|");
			sb.append(map.get("ZR_EINHEIT"));
		} else {
			throw new IllegalStateException("Not valid age range from " + fromAge + " to " + toAge);
		}
		return sb.toString();
	}
	
	private boolean checkValidRange(int fromAge, int toAge){
		// not both not set
		return !(fromAge == -1 && toAge == -1);
	}
	
	private Integer getToleranceInt(String string, int defaultValue){
		if (string == null || string.isEmpty()) {
			return defaultValue;
		}
		try {
			return Float.valueOf(string).intValue();
		} catch (NumberFormatException fe) {
			throw new IllegalStateException(fe);
		}
	}
	
	private Integer getAgeInt(String string, int defaultValue){
		if (string == null || string.isEmpty()) {
			return defaultValue;
		}
		try {
			return Float.valueOf(string).intValue();
		} catch (NumberFormatException fe) {
			throw new IllegalStateException(fe);
		}
	}
	
	private String getSlavesString() throws SQLException, IOException{
		StringBuilder sb = new StringBuilder();
		Stm subStm = cacheDb.getStatement();
		try {
			ResultSet rsub = subStm
				.query(String.format("SELECT * FROM %sLEISTUNG_HIERARCHIE WHERE LNR_MASTER=%s",
					TarmedReferenceDataImporter.ImportPrefix, JdbcLink.wrap(code))); //$NON-NLS-1$
			List<Map<String, String>> validResults = ImporterUtil.getAllValueMaps(rsub);
			if (!validResults.isEmpty()) {
				// do not import directly as bezug, that will lead to incorrect bills
				for (Map<String, String> map : validResults) {
					if (sb.length() == 0) {
						sb.append(map.get("LNR_SLAVE"));
					} else {
						sb.append(", " + map.get("LNR_SLAVE"));
					}
					LocalDate from = ImporterUtil.getLocalDate(map, "GUELTIG_VON");
					LocalDate to = LocalDate.parse(map.get("GUELTIG_BIS"),
						DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.S"));
					sb.append("[").append(from.toString()).append("|").append(to.toString())
						.append("]");
				}
			}
			rsub.close();
		} finally {
			if (subStm != null) {
				cacheDb.releaseStatement(subStm);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Get texts. Index 0 = BEZ_255, Index 1 = MED_INTERPRET, Index 2 = TECH_INTERPRET
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	private String[] getTexts() throws SQLException, IOException{
		String[] ret = new String[3];
		Stm subStm = cacheDb.getStatement();
		try {
			ResultSet rsub = subStm
				.query(String.format("SELECT * FROM %sLEISTUNG_TEXT WHERE SPRACHE=%s AND LNR=%s",
					TarmedReferenceDataImporter.ImportPrefix, lang, JdbcLink.wrap(code))); //$NON-NLS-1$
			List<Map<String, String>> validResults =
				ImporterUtil.getValidValueMaps(rsub, validFrom);
			if (!validResults.isEmpty()) {
				Map<String, String> row = ImporterUtil.getLatestMap(validResults);
				ret[0] = StringUtils.abbreviate(row.get("BEZ_255"), 255); //$NON-NLS-1$
				ret[1] = row.get("MED_INTERPRET"); //$NON-NLS-1$
				ret[2] = row.get("TECH_INTERPRET"); //$NON-NLS-1$
			}
			rsub.close();
		} finally {
			if (subStm != null) {
				cacheDb.releaseStatement(subStm);
			}
		}
		return ret;
	}
	
	private String getQLDignitaet() throws SQLException, IOException{
		String ret = "";
		Stm subStm = cacheDb.getStatement();
		try {
			ResultSet rsub =
				subStm.query(String.format("SELECT * FROM %sLEISTUNG_DIGNIQUALI WHERE LNR=%s",
					TarmedReferenceDataImporter.ImportPrefix, JdbcLink.wrap(code))); //$NON-NLS-1$
			List<Map<String, String>> validResults =
				ImporterUtil.getValidValueMaps(rsub, validFrom);
			if (!validResults.isEmpty()) {
				ret = ImporterUtil.getLatestMap(validResults).get("QL_DIGNITAET");
			}
			rsub.close();
		} finally {
			if (subStm != null) {
				cacheDb.releaseStatement(subStm);
			}
		}
		return ret;
	}
	
	private void initValidTime(ResultSet res) throws SQLException{
		validFrom.set(res.getString("GUELTIG_VON"));
		validTo.set(res.getString("GUELTIG_BIS"));
	}
	
	private String getId(ResultSet res) throws SQLException{
		return res.getString("LNR") + "-" + validFrom.toString(TimeTool.DATE_COMPACT)
			+ getLawIdExtension();
	}
	
	private String getLawIdExtension(){
		if (law != null && !law.isEmpty()) {
			return "-" + law;
		}
		return "";
	}
}
