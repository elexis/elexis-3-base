package ch.elexis.base.ch.arzttarife.tarmed.model.importer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.tarmed.model.VersionUtil;

public class DeleteOldData {

	private static final Logger logger = LoggerFactory.getLogger(DeleteOldData.class);

	private String law;

	public DeleteOldData(String law) {
		this.law = law;
	}

	public IStatus delete(IProgressMonitor ipm) {
		ipm.subTask(Messages.TarmedImporter_deleteOldData);

		// start deleting
		int affected = 0;
		if (law != null && !law.isEmpty()) {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARMED_EXTENSION "
							+ "WHERE exists( SELECT 1 FROM TARMED WHERE TARMED_EXTENSION.Code = TARMED.ID "
							+ "AND TARMED.Law='" + law + "') AND ID <> '" + VersionUtil.VERSION_ENTRY_ID + "'");
			affected += ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARMED_EXTENSION "
							+ "WHERE exists( SELECT 1 FROM TARMED_GROUP WHERE TARMED_EXTENSION.Code = TARMED_GROUP.ID "
							+ "AND TARMED_GROUP.Law='" + law + "') AND ID <> '" + VersionUtil.VERSION_ENTRY_ID + "'");
		} else {
			affected = ArzttarifeModelServiceHolder.get().executeNativeUpdate("DELETE FROM TARMED_EXTENSION "
					+ "WHERE exists( SELECT 1 FROM TARMED WHERE TARMED_EXTENSION.Code = TARMED.ID "
					+ "AND (TARMED.Law='' or TARMED.Law IS NULL))  AND ID <> '" + VersionUtil.VERSION_ENTRY_ID + "'");
			affected += ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARMED_EXTENSION "
							+ "WHERE exists( SELECT 1 FROM TARMED_GROUP WHERE TARMED_EXTENSION.Code = TARMED_GROUP.ID "
							+ "AND (TARMED_GROUP.Law='' or TARMED_GROUP.Law IS NULL))  AND ID <> '"
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		}
		logger.debug("Deleted " + affected + " tarmed extensions");

		affected = 0;
		if (law != null && !law.isEmpty()) {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARMED WHERE Law='" + law + "'  AND ID <> '" //$NON-NLS-1$//$NON-NLS-2$
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		} else {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARMED WHERE (Law='' or Law IS NULL) AND ID <> '" //$NON-NLS-1$
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		}
		logger.debug("Deleted " + affected + " tarmed");

		affected = 0;
		if (law != null && !law.isEmpty()) {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARMED_DEFINITIONEN WHERE Law='" + law + "'  AND ID <> '" //$NON-NLS-1$//$NON-NLS-2$
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		} else {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARMED_DEFINITIONEN WHERE (Law='' or Law IS NULL) AND ID <> '" //$NON-NLS-1$
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		}
		logger.debug("Deleted " + affected + " tarmed definitionen");

		affected = 0;
		if (law != null && !law.isEmpty()) {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARMED_KUMULATION WHERE Law='" + law + "' AND ID <> '" //$NON-NLS-1$//$NON-NLS-2$
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		} else {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARMED_KUMULATION WHERE (Law='' or Law IS NULL) AND ID <> '" //$NON-NLS-1$
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		}
		logger.debug("Deleted " + affected + " tarmed kumulation");

		affected = 0;
		if (law != null && !law.isEmpty()) {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARMED_GROUP WHERE Law='" + law + "' AND ID <> '" //$NON-NLS-1$//$NON-NLS-2$
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		} else {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARMED_GROUP WHERE (Law='' or Law IS NULL) AND ID <> '" //$NON-NLS-1$
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		}
		logger.debug("Deleted " + affected + " tarmed group");

		ArzttarifeModelServiceHolder.get().clearCache();

		return Status.OK_STATUS;
	}
}
