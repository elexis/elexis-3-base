package ch.elexis.base.ch.arzttarife.tardoc.model.importer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.tarmed.model.importer.EntityUtil;
import ch.elexis.base.ch.arzttarife.tarmed.model.importer.TarmedReferenceDataImporter;
import ch.elexis.core.jpa.entities.TardocKumulation;
import ch.elexis.core.jpa.entities.TarmedKumulation;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.TimeTool;

/**
 * Importer for tarmed LEISTUNG_BLOECKE information. Currently only
 * {@link TarmedKumulation} rules are imported.
 *
 * @author thomas
 *
 */
public class BlockImporter {

	private static final Logger logger = LoggerFactory.getLogger(ServiceImporter.class);

	private JdbcLink cacheDb;
	private String lang;
	private String law;

	public BlockImporter(JdbcLink cacheDb, String lang, String law) {
		this.cacheDb = cacheDb;
		this.lang = lang;
		this.law = law;
	}

	public IStatus doImport(IProgressMonitor ipm) throws SQLException, IOException {
		Stm servicesStm = null;
		try {
			ipm.subTask("Importiere Blöcke");

			servicesStm = cacheDb.getStatement();
			ResultSet res = servicesStm.query(String.format("SELECT DISTINCT BLOCK FROM %sLEISTUNG_BLOECKE", //$NON-NLS-1$
					TardocReferenceDataImporter.ImportPrefix));
			while (res.next()) {
				String blockName = res.getString("BLOCK");

				importKumulations(blockName);

				logger.debug("Imported block " + blockName);
			}
		} finally {
			if (servicesStm != null) {
				cacheDb.releaseStatement(servicesStm);
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * Import all the kumulations from the LEISTUNG_KUMULATION table for the given
	 * code. The kumulations contain inclusions, exclusions and exclusives.
	 *
	 * @param code      of a tarmed value
	 * @param stmCached
	 * @throws SQLException
	 */
	private void importKumulations(String blockName) throws SQLException {
		Stm subStm = cacheDb.getStatement();
		try {
			try (ResultSet res = subStm
					.query(String.format("SELECT * FROM %sLEISTUNG_KUMULATION WHERE LNR_MASTER='%s' AND ART_MASTER='B'",
							TarmedReferenceDataImporter.ImportPrefix, blockName))) {
				TimeTool fromTime = new TimeTool();
				TimeTool toTime = new TimeTool();

				List<Object> kumulations = new ArrayList<>();
				while (res != null && res.next()) {
					fromTime.set(res.getString("GUELTIG_VON"));
					toTime.set(res.getString("GUELTIG_BIS"));

					TardocKumulation kumulation = new TardocKumulation();
					kumulation.setMasterCode(blockName);
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
				cacheDb.releaseStatement(subStm);
			}
		}
	}
}
