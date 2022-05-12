package ch.elexis.omnivore.ui.jobs;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQueryCursor;
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
					IQuery<IDocumentHandle> qDoc = OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);

					try (IQueryCursor<IDocumentHandle> docs = qDoc.executeAsCursor()) {
						monitor.beginTask("Dateien werden ausgelagert...", docs.size());

						while (docs.hasNext()) {
							IDocumentHandle docHandle = docs.next();

							if (monitor.isCanceled())
								return;
							monitor.subTask("Datei: " + docHandle.getTitle());
							if (!docHandle.exportToFileSystem()) {
								SWTHelper.showError(Messages.DocHandle_writeErrorCaption2,
										Messages.DocHandle_writeErrorCaption2, "Fehlerdetails siehe Logdatei");
							}
							monitor.worked(1);
						}
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
