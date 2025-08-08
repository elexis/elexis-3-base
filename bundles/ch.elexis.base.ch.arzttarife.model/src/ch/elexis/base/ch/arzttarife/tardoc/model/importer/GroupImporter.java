package ch.elexis.base.ch.arzttarife.tardoc.model.importer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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

import ch.elexis.base.ch.arzttarife.tarmed.model.importer.EntityUtil;
import ch.elexis.base.ch.arzttarife.tarmed.model.importer.ImporterUtil;
import ch.elexis.core.jpa.entities.TardocExtension;
import ch.elexis.core.jpa.entities.TardocGroup;
import ch.elexis.core.jpa.entities.TardocKumulation;
import ch.elexis.core.jpa.model.util.JpaModelUtil;
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

	private HashMap<String, TransientTardocGroup> loadedGroups;

	public GroupImporter(JdbcLink cacheDb, String lang, String law) {
		this.cacheDb = cacheDb;
		this.lang = lang;
		this.law = law;
		this.validFrom = new TimeTool();
		this.validTo = new TimeTool();

		this.loadedGroups = new HashMap<>();
	}

	public IStatus doImport(IProgressMonitor ipm) throws SQLException, IOException {
		Stm servicesStm = null;
		try {
			ipm.subTask("Importiere Gruppen");

			servicesStm = cacheDb.getStatement();
			ResultSet res = servicesStm.query(String.format("SELECT * FROM %sLEISTUNG_GRUPPEN", //$NON-NLS-1$
					TardocReferenceDataImporter.ImportPrefix));
			while (res.next()) {
				String groupName = res.getString("GRUPPE");
				String serviceCode = res.getString("LNR");
				initValidTime(res);

				String id = getIdString(groupName, law);
				TransientTardocGroup transientGroup = loadedGroups.get(id);
				if (transientGroup == null) {
					transientGroup = new TransientTardocGroup(id, groupName, law, validFrom.toLocalDate(),
							validTo.toLocalDate(), this);
					loadedGroups.put(id, transientGroup);
				}
				transientGroup.addService(serviceCode);
			}

			for (String key : loadedGroups.keySet()) {
				TransientTardocGroup transientGroup = loadedGroups.get(key);
				TardocGroup group = transientGroup.persist();
				logger.debug("Imported " + group.getGroupName());
			}
		} finally {
			if (servicesStm != null) {
				cacheDb.releaseStatement(servicesStm);
			}
		}
		return Status.OK_STATUS;
	}

	private void initValidTime(ResultSet res) throws SQLException {
		validFrom.set(res.getString("GUELTIG_VON"));
		validTo.set(res.getString("GUELTIG_BIS"));
	}

	private String getIdString(String groupName, String law) {
		return "GRP" + groupName + "-" + validFrom.toString(TimeTool.DATE_COMPACT) + getLawIdExtension();
	}

	private String getLawIdExtension() {
		if (law != null && !law.isEmpty()) {
			return "-" + law;
		}
		return StringUtils.EMPTY;
	}

	private static class TransientTardocGroup {

		private String id;
		private String code;
		private String law;
		private StringBuilder services;

		private LocalDate validFrom;
		private LocalDate validTo;

		private GroupImporter importer;

		public TransientTardocGroup(String id, String groupName, String law, LocalDate validFrom, LocalDate validTo,
				GroupImporter importer) {
			this.id = id;
			this.code = groupName;
			this.law = law;
			this.validFrom = validFrom;
			this.validTo = validTo;

			this.services = new StringBuilder();

			this.importer = importer;
		}

		public TardocGroup persist() throws SQLException, IOException {
			TardocGroup persistent = new TardocGroup();
			persistent.setId(id);
			persistent.setGroupName(code);
			persistent.setLaw(law);
			persistent.setValidFrom(validFrom);
			persistent.setValidTo(validTo);
			persistent.setRawServices(services.toString());

			TardocExtension extension = new TardocExtension();
			extension.setCode(persistent.getId());
			Map<Object, Object> extensionMap = JpaModelUtil.extInfoFromBytes(extension.getExtInfo());

			// get OPERATOR, MENGE, ZR_ANZAHL, PRO_NACH, ZR_EINHEIT
			String limits = getLimits(code);
			extensionMap.put("limits", limits);

			// get LNR_SLAVE, TYP (invalid combinations with other codes)
			importKumulations(code);

			extension.setExtInfo(JpaModelUtil.extInfoToBytes(extensionMap));

			EntityUtil.save(Arrays.asList(persistent, extension));

			return persistent;
		}

		public void addService(String serviceCode) {
			if (services.length() > 0) {
				services.append(TardocGroup.SERVICES_SEPARATOR);
			}
			services.append(serviceCode);
		}

		private String getLimits(String groupName) throws SQLException, IOException {
			StringBuilder sb = new StringBuilder();
			Stm subStm = importer.cacheDb.getStatement();
			try {
				ResultSet rsub = subStm
						.query(String.format("SELECT * FROM %sLEISTUNG_MENGEN_ZEIT WHERE LNR='%s' AND ART='G'",
								TardocReferenceDataImporter.ImportPrefix, groupName)); // $NON-NLS-1$
				List<Map<String, String>> validResults = ImporterUtil.getValidValueMaps(rsub, new TimeTool(validFrom));
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
		 * Import all the kumulations from the LEISTUNG_KUMULATION table for the given
		 * code. The kumulations contain inclusions, exclusions and exclusives.
		 *
		 * @param code      of a tarmed value
		 * @param stmCached
		 * @throws SQLException
		 */
		private void importKumulations(String groupName) throws SQLException {
			Stm subStm = importer.cacheDb.getStatement();
			try {
				try (ResultSet res = subStm.query(
						String.format("SELECT * FROM %sLEISTUNG_KUMULATION WHERE LNR_MASTER=%s AND ART_MASTER='G'",
								TardocReferenceDataImporter.ImportPrefix, JdbcLink.wrap(groupName)))) {
					TimeTool fromTime = new TimeTool();
					TimeTool toTime = new TimeTool();

					List<Object> kumulations = new ArrayList<>();
					while (res != null && res.next()) {
						fromTime.set(res.getString("GUELTIG_VON"));
						toTime.set(res.getString("GUELTIG_BIS"));

						TardocKumulation kumulation = new TardocKumulation();
						kumulation.setMasterCode(groupName);
						kumulation.setMasterArt(res.getString("ART_MASTER"));
						kumulation.setSlaveCode(res.getString("LNR_SLAVE"));
						kumulation.setSlaveArt(res.getString("ART_SLAVE"));
						kumulation.setTyp(res.getString("TYP"));
						kumulation.setView(res.getString("ANZEIGE"));
						kumulation.setValidSide(res.getString("GUELTIG_SEITE"));
						kumulation.setValidFrom(fromTime.toLocalDate());
						kumulation.setValidTo(toTime.toLocalDate());
						kumulation.setLaw(law);
						kumulations.add(kumulation);
					}
					EntityUtil.save(kumulations);
				}
			} finally {
				if (subStm != null) {
					importer.cacheDb.releaseStatement(subStm);
				}
			}
		}
	}
}
