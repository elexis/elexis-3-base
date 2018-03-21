package ch.elexis.data.importer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.data.TarmedKumulation;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.TimeTool;

/**
 * Importer for tarmed LEISTUNG_BLOECKE information. Currently only {@link TarmedKumulation} rules
 * are imported.
 * 
 * @author thomas
 *
 */
public class BlockImporter {
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceImporter.class);
	
	private JdbcLink cacheDb;
	private String lang;
	private String law;
	
	public BlockImporter(JdbcLink cacheDb, String lang, String law){
		this.cacheDb = cacheDb;
		this.lang = lang;
		this.law = law;
	}
	
	public IStatus doImport(IProgressMonitor ipm) throws SQLException, IOException{
		Stm servicesStm = null;
		try {
			ipm.subTask("Importiere Blöcke");
			
			servicesStm = cacheDb.getStatement();
			ResultSet res =
				servicesStm.query(String.format("SELECT DISTINCT BLOCK FROM %sLEISTUNG_BLOECKE", //$NON-NLS-1$
				TarmedReferenceDataImporter.ImportPrefix));
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
	 * Import all the kumulations from the LEISTUNG_KUMULATION table for the given code. The
	 * kumulations contain inclusions, exclusions and exclusives.
	 * 
	 * @param code
	 *            of a tarmed value
	 * @param stmCached
	 * @throws SQLException
	 */
	private void importKumulations(String blockName) throws SQLException{
		Stm subStm = cacheDb.getStatement();
		try {
			try (ResultSet res = subStm.query(String.format(
				"SELECT * FROM %sLEISTUNG_KUMULATION WHERE LNR_MASTER=%s AND ART_MASTER='B'",
				TarmedReferenceDataImporter.ImportPrefix, JdbcLink.wrap(blockName)))) {
				TimeTool fromTime = new TimeTool();
				TimeTool toTime = new TimeTool();
				
				while (res != null && res.next()) {
					fromTime.set(res.getString("GUELTIG_VON"));
					toTime.set(res.getString("GUELTIG_BIS"));
					
					new TarmedKumulation(blockName, res.getString("ART_MASTER"),
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
}
