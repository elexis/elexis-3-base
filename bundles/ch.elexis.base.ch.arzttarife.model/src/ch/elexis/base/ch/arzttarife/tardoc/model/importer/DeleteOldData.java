package ch.elexis.base.ch.arzttarife.tardoc.model.importer;

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
					.executeNativeUpdate("DELETE FROM TARDOC_EXTENSION "
							+ "WHERE exists( SELECT 1 FROM TARDOC WHERE TARDOC_EXTENSION.Code = TARDOC.ID "
							+ "AND TARDOC.Law='" + law + "') AND ID <> '" + VersionUtil.VERSION_ENTRY_ID + "'");
			affected += ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARDOC_EXTENSION "
							+ "WHERE exists( SELECT 1 FROM TARDOC_GROUP WHERE TARDOC_EXTENSION.Code = TARDOC_GROUP.ID "
							+ "AND TARDOC_GROUP.Law='" + law + "') AND ID <> '" + VersionUtil.VERSION_ENTRY_ID + "'");
		} else {
			affected = ArzttarifeModelServiceHolder.get().executeNativeUpdate("DELETE FROM TARDOC_EXTENSION "
					+ "WHERE exists( SELECT 1 FROM TARDOC WHERE TARDOC_EXTENSION.Code = TARDOC.ID "
					+ "AND (TARDOC.Law='' or TARDOC.Law IS NULL))  AND ID <> '" + VersionUtil.VERSION_ENTRY_ID + "'");
			affected += ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARDOC_EXTENSION "
							+ "WHERE exists( SELECT 1 FROM TARDOC_GROUP WHERE TARDOC_EXTENSION.Code = TARDOC_GROUP.ID "
							+ "AND (TARDOC_GROUP.Law='' or TARDOC_GROUP.Law IS NULL))  AND ID <> '"
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		}
		logger.debug("Deleted " + affected + " tarmed extensions");

		affected = 0;
		if (law != null && !law.isEmpty()) {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARDOC WHERE Law='" + law + "'  AND ID <> '" //$NON-NLS-1$//$NON-NLS-2$
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		} else {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARDOC WHERE (Law='' or Law IS NULL) AND ID <> '" //$NON-NLS-1$
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		}
		logger.debug("Deleted " + affected + " tarmed");

		affected = 0;
		if (law != null && !law.isEmpty()) {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARDOC_DEFINITIONEN WHERE Law='" + law + "'  AND ID <> '" //$NON-NLS-1$//$NON-NLS-2$
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		} else {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARDOC_DEFINITIONEN WHERE (Law='' or Law IS NULL) AND ID <> '" //$NON-NLS-1$
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		}
		logger.debug("Deleted " + affected + " tarmed definitionen");

		affected = 0;
		if (law != null && !law.isEmpty()) {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARDOC_KUMULATION WHERE Law='" + law + "' AND ID <> '" //$NON-NLS-1$//$NON-NLS-2$
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		} else {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARDOC_KUMULATION WHERE (Law='' or Law IS NULL) AND ID <> '" //$NON-NLS-1$
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		}
		logger.debug("Deleted " + affected + " tarmed kumulation");

		affected = 0;
		if (law != null && !law.isEmpty()) {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARDOC_GROUP WHERE Law='" + law + "' AND ID <> '" //$NON-NLS-1$//$NON-NLS-2$
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		} else {
			affected = ArzttarifeModelServiceHolder.get()
					.executeNativeUpdate("DELETE FROM TARDOC_GROUP WHERE (Law='' or Law IS NULL) AND ID <> '" //$NON-NLS-1$
							+ VersionUtil.VERSION_ENTRY_ID + "'");
		}
		logger.debug("Deleted " + affected + " tarmed group");

		ArzttarifeModelServiceHolder.get().clearCache();

		return Status.OK_STATUS;
	}
}
