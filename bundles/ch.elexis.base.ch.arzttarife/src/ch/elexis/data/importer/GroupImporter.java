package ch.elexis.data.importer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.data.TarmedGroup;
import ch.elexis.data.TarmedKumulation;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.TimeTool;

public class GroupImporter {
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceImporter.class);
	
	private JdbcLink cacheDb;
	private String lang;
	private String law;
	private TimeTool validFrom;
	private TimeTool validTo;
	
	private HashMap<String, TransientTarmedGroup> loadedGroups;
	
	public GroupImporter(JdbcLink cacheDb, String lang, String law){
		this.cacheDb = cacheDb;
		this.lang = lang;
		this.law = law;
		this.validFrom = new TimeTool();
		this.validTo = new TimeTool();
		
		this.loadedGroups = new HashMap<>();
	}
	
	public IStatus doImport(IProgressMonitor ipm) throws SQLException, IOException{
		Stm servicesStm = null;
		try {
			ipm.subTask("Importiere Gruppen");
			
			servicesStm = cacheDb.getStatement();
			ResultSet res = servicesStm.query(String.format("SELECT * FROM %sLEISTUNG_GRUPPEN", //$NON-NLS-1$
				TarmedReferenceDataImporter.ImportPrefix));
			while (res.next()) {
				String groupName = res.getString("GRUPPE");
				String serviceCode = res.getString("LNR");
				initValidTime(res);
				
				String id = getIdString(groupName, law);
				TransientTarmedGroup transientGroup = loadedGroups.get(id);
				if (transientGroup == null) {
					transientGroup =
						new TransientTarmedGroup(id, groupName, law, validFrom, validTo, this);
					loadedGroups.put(id, transientGroup);
				}
				transientGroup.addService(serviceCode);
			}
			
			for (String key : loadedGroups.keySet()) {
				TransientTarmedGroup transientGroup = loadedGroups.get(key);
				TarmedGroup group = transientGroup.persist();
				logger.debug("Imported " + group.getLabel());
			}
		} finally {
			if (servicesStm != null) {
				cacheDb.releaseStatement(servicesStm);
			}
		}
		return Status.OK_STATUS;
	}
	
	private void initValidTime(ResultSet res) throws SQLException{
		validFrom.set(res.getString("GUELTIG_VON"));
		validTo.set(res.getString("GUELTIG_BIS"));
	}
	
	private String getIdString(String groupName, String law){
		return "GRP" + groupName + "-" + validFrom.toString(TimeTool.DATE_COMPACT)
			+ getLawIdExtension();
	}
	
	private String getLawIdExtension(){
		if (law != null && !law.isEmpty()) {
			return "-" + law;
		}
		return "";
	}
	
	private static class TransientTarmedGroup {
		
		private String id;
		private String code;
		private String law;
		private StringBuilder services;
		
		private String validFrom;
		private String validTo;
		
		private GroupImporter importer;
		
		public TransientTarmedGroup(String id, String groupName, String law, TimeTool validFrom,
			TimeTool validTo, GroupImporter importer){
			this.id = id;
			this.code = groupName;
			this.law = law;
			this.validFrom = validFrom.toString(TimeTool.DATE_COMPACT);
			this.validTo = validTo.toString(TimeTool.DATE_COMPACT);
			
			this.services = new StringBuilder();
			
			this.importer = importer;
		}
		
		public TarmedGroup persist() throws SQLException, IOException{
			TarmedGroup persistent =
				new TarmedGroup(id, code, law, validFrom, validTo, services.toString());
			
			Hashtable<String, String> extensionMap = persistent.loadExtension();
			
			// get OPERATOR, MENGE, ZR_ANZAHL, PRO_NACH, ZR_EINHEIT
			String limits = getLimits(code);
			extensionMap.put("limits", limits);
			
			// get LNR_SLAVE, TYP (invalid combinations with other codes)
			importKumulations(code);
			
			persistent.setExtension(extensionMap);
			
			return persistent;
		}
		
		public void addService(String serviceCode){
			if (services.length() > 0) {
				services.append(TarmedGroup.SERVICES_SEPARATOR);
			}
			services.append(serviceCode);
		}
		
		private String getLimits(String groupName) throws SQLException, IOException{
			StringBuilder sb = new StringBuilder();
			Stm subStm = importer.cacheDb.getStatement();
			try {
				ResultSet rsub = subStm.query(
					String.format("SELECT * FROM %sLEISTUNG_MENGEN_ZEIT WHERE LNR=%s AND ART='G'",
						TarmedReferenceDataImporter.ImportPrefix, JdbcLink.wrap(groupName))); //$NON-NLS-1$
				List<Map<String, String>> validResults =
					ImporterUtil.getValidValueMaps(rsub, new TimeTool(validFrom));
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
					importer.cacheDb.releaseStatement(subStm);
				}
			}
			return sb.toString();
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
		private void importKumulations(String groupName) throws SQLException{
			Stm subStm = importer.cacheDb.getStatement();
			try {
				try (ResultSet res = subStm.query(String.format(
					"SELECT * FROM %sLEISTUNG_KUMULATION WHERE LNR_MASTER=%s AND ART_MASTER='G'",
					TarmedReferenceDataImporter.ImportPrefix, JdbcLink.wrap(groupName)))) {
					TimeTool fromTime = new TimeTool();
					TimeTool toTime = new TimeTool();
					
					while (res != null && res.next()) {
						fromTime.set(res.getString("GUELTIG_VON"));
						toTime.set(res.getString("GUELTIG_BIS"));
						
						new TarmedKumulation(groupName, res.getString("ART_MASTER"),
							res.getString("LNR_SLAVE"), res.getString("ART_SLAVE"),
							res.getString("TYP"), res.getString("ANZEIGE"),
							res.getString("GUELTIG_SEITE"),
							fromTime.toString(TimeTool.DATE_COMPACT),
							toTime.toString(TimeTool.DATE_COMPACT), law);
					}
				}
			} finally {
				if (subStm != null) {
					importer.cacheDb.releaseStatement(subStm);
				}
			}
		}
	}
}
