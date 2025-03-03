/*******************************************************************************
 * Copyright (c) 2011, Niklaus Giger <niklaus.giger@member.fsf.org>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Niklaus Giger - initial implementation
 *
 *******************************************************************************/

package ch.elexis.extdoc.dialogs;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.extdoc.Messages;
import ch.elexis.extdoc.util.MatchPatientToPath;

public class MoveIntoSubDirsDialog extends Action {
	private static Logger logger = null;

	public void run() {
		if (logger == null)
			logger = LoggerFactory.getLogger(this.getClass());
		logger.info("MoveIntoSubDirsDialog started"); //$NON-NLS-1$

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(null);
		try {
			dialog.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					int nrTreated = 0;
					java.util.List<File> oldFiles = MatchPatientToPath.getAllOldConventionFiles();
					int nrFiles = oldFiles.size();
					String dialogTitle = String.format("Alle (%1d) Dateien in Unterverzeichnisse auslagern ...", //$NON-NLS-1$
							nrFiles);
					logger.info(dialogTitle);
					if (oldFiles == null) {
						SWTHelper.showInfo(dialogTitle, Messages.MoveIntoSubDirsDialog_no_old_Files_found);
						return;
					}
					monitor.beginTask(dialogTitle, oldFiles.size());
					Iterator<File> iterator = oldFiles.iterator();
					while (iterator.hasNext()) {
						if (monitor.isCanceled())
							break;
						File f = iterator.next();
						logger.info("Moving " + f.getAbsolutePath()); //$NON-NLS-1$
						MatchPatientToPath.MoveIntoSubDir(f.getAbsolutePath());
						++nrTreated;
						if (nrTreated % 10 == 1) {
							monitor.subTask(String.format(Messages.MoveIntoSubDirsDialog_sub_task, f.getName()));
							monitor.worked(10);
						}
					}
					monitor.done();
					logger.info("MoveIntoSubDirsDialog done"); //$NON-NLS-1$
					SWTHelper.showInfo(dialogTitle, Messages.MoveIntoSubDirsDialog_finished);
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			SWTHelper.showInfo("Fehler beim Auslagern!!",
					Messages.MoveIntoSubDirsDialog_finished + StringUtils.LF + e.getMessage());
			logger.info("Fehler beim Auslagern!!" + e.getLocalizedMessage()); //$NON-NLS-1$
		} catch (InterruptedException e) {
			e.printStackTrace();
			SWTHelper.showInfo("Fehler beim Auslagern!",
					Messages.MoveIntoSubDirsDialog_finished + StringUtils.LF + e.getMessage());
			logger.info("Fehler beim Auslagern!!" + e.getLocalizedMessage()); //$NON-NLS-1$
		}
		return;
	}
}
