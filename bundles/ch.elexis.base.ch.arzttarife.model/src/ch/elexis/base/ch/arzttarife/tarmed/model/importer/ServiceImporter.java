package ch.elexis.base.ch.arzttarife.tarmed.model.importer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.core.jpa.entities.TarmedExtension;
import ch.elexis.core.jpa.entities.TarmedKumulation;
import ch.elexis.core.jpa.entities.TarmedLeistung;
import ch.elexis.core.jpa.model.util.JpaModelUtil;
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
	private Map<Object, Object> extensionMap;
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
				
				TarmedLeistung tl = EntityUtil.load(id, TarmedLeistung.class);
				if (tl != null) {
					logger.debug("Skipped " + tl.getCode());
					ipm.worked(1);
					continue;
				} else {
					String parentId = getParentId(res.getString("KNR"));
					if (parentId == null) {
						throw new IllegalStateException(
							"Could not find parentId for chapter number [" + res.getString("KNR")
								+ "] and service id [" + id + "]");
					}
					
					tl = new TarmedLeistung();
					tl.setId(id);
					tl.setCode_(code);
					tl.setParent(parentId);
					tl.setDigniQuali("0000");
					tl.setDigniQuanti(ImporterUtil.getAsString(res, "QT_DIGNITAET"));
					tl.setSparte(ImporterUtil.getAsString(res, "Sparte"));
					tl.setChapter(false);
					tl.setGueltigVon(validFrom.toLocalDate());
					tl.setGueltigBis(validTo.toLocalDate());
					tl.setLaw(law);
	
					extension = new TarmedExtension();
					extension.setCode(tl.getId());
					extensionMap = JpaModelUtil.extInfoFromBytes(extension.getExtInfo());
					
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
					tl.setDigniQuali(dqua);
					tl.setTx255(texts[0]);

					if (texts[1] != null) {
						extension.setMed_interpret(texts[1]);
					}
					if (texts[2] != null) {
						extension.setTech_interpret(texts[2]);
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
					
					extension.setExtInfo(JpaModelUtil.extInfoToBytes(extensionMap));
					
					EntityUtil.save(Arrays.asList(extension, tl));
					
					logger.debug("Imported " + tl.getId());
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
		return chapterImporter.getIdForCode(chapterCode, validFrom.toLocalDate(), law);
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
					"SELECT * FROM %sLEISTUNG_KUMULATION WHERE LNR_MASTER='%s' AND ART_MASTER='L'",
					TarmedReferenceDataImporter.ImportPrefix, code))) {
				TimeTool fromTime = new TimeTool();
				TimeTool toTime = new TimeTool();
				
				List<Object> kumulations = new ArrayList<>();
				boolean skip = false;
				while (res != null && res.next()) {
					skip = false;
					fromTime.set(res.getString("GUELTIG_VON"));
					toTime.set(res.getString("GUELTIG_BIS"));
					
					TarmedKumulation kumulation = new TarmedKumulation();
					kumulation.setMasterCode(code);
					kumulation.setMasterArt(res.getString("ART_MASTER"));
					kumulation.setSlaveCode(res.getString("LNR_SLAVE"));
					kumulation.setSlaveArt(res.getString("ART_SLAVE"));
					kumulation.setTyp(res.getString("TYP"));
					kumulation.setView(res.getString("ANZEIGE"));
					kumulation.setValidSide(res.getString("GUELTIG_SEITE"));
					kumulation.setValidFrom(fromTime.toLocalDate());
					kumulation.setValidTo(toTime.toLocalDate());
					kumulation.setLaw(law);
					
					// same code (TarmedLeistung) can be imported multiple times, filter out already imported kumulation
					// .masterCode.masterArt.typ
					HashMap<String, Object> propertyMap = new HashMap<String, Object>();
					propertyMap.put("masterCode", kumulation.getMasterCode());
					propertyMap.put("masterArt", kumulation.getMasterArt());
					propertyMap.put("typ", kumulation.getTyp());
					List<TarmedKumulation> existing =
						EntityUtil.loadByNamedQuery(propertyMap, TarmedKumulation.class);
					if (existing != null && !existing.isEmpty()) {
						for (TarmedKumulation existingKumulation : existing) {
							if(existingKumulation.getSlaveCode().equals(kumulation.getSlaveCode()) && existingKumulation.getSlaveArt().equals(kumulation.getSlaveArt()) && 
								existingKumulation.getValidFrom().isEqual(kumulation.getValidFrom())
								&& existingKumulation.getValidTo()
									.isEqual(kumulation.getValidTo())) {
								skip = true;
								break;
							}
						}
					}
					if (!skip) {
						kumulations.add(kumulation);
					}
				}
				EntityUtil.save(kumulations);
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
				.query(String.format("SELECT * FROM %sLEISTUNG_KOMBINATION WHERE LNR_MASTER='%s'",
					TarmedReferenceDataImporter.ImportPrefix, code)); //$NON-NLS-1$
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
					String.format("SELECT * FROM %sLEISTUNG_MENGEN_ZEIT WHERE LNR='%s' AND ART='L'",
						TarmedReferenceDataImporter.ImportPrefix, code)); //$NON-NLS-1$
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
				subStm.query(String.format("SELECT * FROM %sLEISTUNG_BLOECKE WHERE LNR='%s'",
					TarmedReferenceDataImporter.ImportPrefix, code)); //$NON-NLS-1$
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
				subStm.query(String.format("SELECT * FROM %sLEISTUNG_GRUPPEN WHERE LNR='%s'",
					TarmedReferenceDataImporter.ImportPrefix, code)); //$NON-NLS-1$
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
				subStm.query(String.format("SELECT * FROM %sLEISTUNG_ALTER WHERE LNR='%s'",
					TarmedReferenceDataImporter.ImportPrefix, code)); //$NON-NLS-1$
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
				.query(String.format("SELECT * FROM %sLEISTUNG_HIERARCHIE WHERE LNR_MASTER='%s'",
					TarmedReferenceDataImporter.ImportPrefix, code)); //$NON-NLS-1$
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
				.query(
					String.format("SELECT * FROM %sLEISTUNG_TEXT WHERE SPRACHE='%s' AND LNR='%s'",
						TarmedReferenceDataImporter.ImportPrefix, lang, code)); //$NON-NLS-1$
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
				subStm.query(String.format("SELECT * FROM %sLEISTUNG_DIGNIQUALI WHERE LNR='%s'",
					TarmedReferenceDataImporter.ImportPrefix, code)); //$NON-NLS-1$
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
