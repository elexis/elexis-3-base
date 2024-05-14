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
import ch.elexis.omnivore.data.Messages;
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

					List<IDocumentHandle> notExported = qDoc.execute();
					while (!notExported.isEmpty()) {
						for (IDocumentHandle docHandle : notExported) {
							if (monitor.isCanceled())
								return;
							monitor.subTask("Datei: " + docHandle.getTitle());
							if (!docHandle.exportToFileSystem()) {
								SWTHelper.showError(Messages.DocHandle_writeErrorCaption2,
										Messages.DocHandle_writeErrorCaption2, "Fehlerdetails siehe Logdatei");
							}
							monitor.worked(1);
						}
						notExported = qDoc.execute();
					}
					monitor.done();
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
