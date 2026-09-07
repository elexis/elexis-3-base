package ch.elexis.omnivore.ui.jobs;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.ui.service.OmnivoreModelServiceHolder;

public class OutsourceUiJob {

	public Object execute(Shell parentShell) {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(parentShell);

		try {
			dialog.run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Dateien werden ausgelagert...", IProgressMonitor.UNKNOWN);

					IQuery<IDocumentHandle> qDoc = OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);
					qDoc.and("doc", COMPARATOR.NOT_EQUALS, null);
					qDoc.and("kontakt", COMPARATOR.NOT_EQUALS, null);
					qDoc.limit(100);

					int already = 0;
					int done = 0;
					int error = 0;

					List<IDocumentHandle> notExported = qDoc.execute();
					while (!notExported.isEmpty()) {
						for (IDocumentHandle docHandle : notExported) {
							if (monitor.isCanceled())
								return;
							monitor.subTask("Datei: " + docHandle.getTitle());
							if (docHandle.isExported()) {
								already++;
							} else if (docHandle.exportToFileSystem()) {
								done++;
							} else {
								error++;
							}
							monitor.worked(1);
						}
						notExported = qDoc.execute();
					}
					monitor.done();
					SWTHelper.showInfo("Omnivore Dateien ausgelagert",
							"Es wurden " + done + " ausgelagert." + "\nEs waren bereits " + already + " ausgelagert."
									+ "\nEs konnten " + error + " nicht ausgelagert werden.");
				}
			});
		} catch (InvocationTargetException ite) {
			ite.printStackTrace();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}

		return null;
	}

}
