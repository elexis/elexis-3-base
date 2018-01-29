package ch.elexis.data.importer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.TarmedDefinitionen;
import ch.elexis.data.TarmedExtension;
import ch.elexis.data.TarmedGroup;
import ch.elexis.data.TarmedKumulation;
import ch.elexis.data.TarmedLeistung;
import ch.rgw.tools.JdbcLink;

public class DeleteOldData {
	
	private static final Logger logger = LoggerFactory.getLogger(DeleteOldData.class);
	
	private String law;
	
	public DeleteOldData(String law){
		this.law = law;
	}
	
	public IStatus delete(IProgressMonitor ipm){
		ipm.subTask(Messages.TarmedImporter_deleteOldData);
		
		// make sure tables are up to date
		TarmedDefinitionen.load("");
		TarmedExtension.load("");
		TarmedLeistung.load("");
		TarmedKumulation.load("");
		// start deleting
		JdbcLink jdbcLink = PersistentObject.getDefaultConnection().getJdbcLink();
		if (PersistentObject.tableExists("TARMED_EXTENSION")) {
			int affected = 0;
			if (law != null && !law.isEmpty()) {
				affected = jdbcLink.exec("DELETE FROM TARMED_EXTENSION "
					+ "WHERE exists( SELECT 1 FROM TARMED WHERE TARMED_EXTENSION.Code = TARMED.ID "
					+ "AND TARMED.Law='" + law + "') AND ID <> '" + TarmedLeistung.ROW_VERSION
					+ "'");
				
				if (PersistentObject.tableExists("TARMED_GROUP")) {
					affected += jdbcLink.exec("DELETE FROM TARMED_EXTENSION "
						+ "WHERE exists( SELECT 1 FROM TARMED_GROUP WHERE TARMED_EXTENSION.Code = TARMED_GROUP.ID "
						+ "AND TARMED_GROUP.Law='" + law + "') AND ID <> '"
						+ TarmedLeistung.ROW_VERSION + "'");
				}
			} else {
				affected = jdbcLink.exec("DELETE FROM TARMED_EXTENSION "
					+ "WHERE exists( SELECT 1 FROM TARMED WHERE TARMED_EXTENSION.Code = TARMED.ID "
					+ "AND (TARMED.Law='' or TARMED.Law IS NULL))  AND ID <> '"
					+ TarmedLeistung.ROW_VERSION + "'");
				if (PersistentObject.tableExists("TARMED_GROUP")) {
					affected += jdbcLink.exec("DELETE FROM TARMED_EXTENSION "
						+ "WHERE exists( SELECT 1 FROM TARMED_GROUP WHERE TARMED_EXTENSION.Code = TARMED_GROUP.ID "
						+ "AND (TARMED_GROUP.Law='' or TARMED_GROUP.Law IS NULL))  AND ID <> '"
						+ TarmedLeistung.ROW_VERSION + "'");
				}
			}
			logger.debug("Deleted " + affected + " tarmed extensions");
		}
		if (PersistentObject.tableExists("TARMED")) {
			int affected = 0;
			if (law != null && !law.isEmpty()) {
				affected = jdbcLink.exec("DELETE FROM TARMED WHERE Law='" + law + "'  AND ID <> '" //$NON-NLS-1$//$NON-NLS-2$
					+ TarmedLeistung.ROW_VERSION + "'");
			} else {
				affected =
					jdbcLink.exec("DELETE FROM TARMED WHERE (Law='' or Law IS NULL) AND ID <> '" //$NON-NLS-1$
					+ TarmedLeistung.ROW_VERSION + "'");
			}
			logger.debug("Deleted " + affected + " tarmed");
		}
		if (PersistentObject.tableExists("TARMED_DEFINITIONEN")) {
			int affected = 0;
			if (law != null && !law.isEmpty()) {
				affected = jdbcLink
					.exec("DELETE FROM TARMED_DEFINITIONEN WHERE Law='" + law + "'  AND ID <> '" //$NON-NLS-1$//$NON-NLS-2$
					+ TarmedLeistung.ROW_VERSION + "'");
			} else {
				affected = jdbcLink.exec(
					"DELETE FROM TARMED_DEFINITIONEN WHERE (Law='' or Law IS NULL) AND ID <> '" //$NON-NLS-1$
						+ TarmedLeistung.ROW_VERSION + "'");
			}
			logger.debug("Deleted " + affected + " tarmed definitionen");
		}
		if (PersistentObject.tableExists("TARMED_KUMULATION")) {
			int affected = 0;
			if (law != null && !law.isEmpty()) {
				affected = jdbcLink
					.exec("DELETE FROM TARMED_KUMULATION WHERE Law='" + law + "' AND ID <> '" //$NON-NLS-1$//$NON-NLS-2$
					+ TarmedLeistung.ROW_VERSION + "'");
			} else {
				affected = jdbcLink
					.exec("DELETE FROM TARMED_KUMULATION WHERE (Law='' or Law IS NULL) AND ID <> '" //$NON-NLS-1$
						+ TarmedLeistung.ROW_VERSION + "'");
			}
			logger.debug("Deleted " + affected + " tarmed kumulation");
		}
		if (PersistentObject.tableExists("TARMED_GROUP")) {
			int affected = 0;
			if (law != null && !law.isEmpty()) {
				affected =
					jdbcLink.exec("DELETE FROM TARMED_GROUP WHERE Law='" + law + "' AND ID <> '" //$NON-NLS-1$//$NON-NLS-2$
						+ TarmedGroup.ROW_VERSION + "'");
			} else {
				affected = jdbcLink
					.exec("DELETE FROM TARMED_GROUP WHERE (Law='' or Law IS NULL) AND ID <> '" //$NON-NLS-1$
						+ TarmedGroup.ROW_VERSION + "'");
			}
			logger.debug("Deleted " + affected + " tarmed group");
		}
		return Status.OK_STATUS;
	}
}
