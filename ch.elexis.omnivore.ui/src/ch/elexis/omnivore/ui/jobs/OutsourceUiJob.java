package ch.elexis.omnivore.ui.jobs;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.data.Query;
import ch.elexis.omnivore.data.DocHandle;

public class OutsourceUiJob {
	
	public Object execute(Shell parentShell){
		
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(parentShell);
		
		try {
			dialog.run(true, true, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException{
					
					Query<DocHandle> qDoc;
					qDoc = new Query<DocHandle>(DocHandle.class);
					List<DocHandle> lDocs = qDoc.execute();
					
					monitor.beginTask("Dateien werden ausgelagert...", lDocs.size());
					
					for (DocHandle docHandle : lDocs) {
						if (monitor.isCanceled())
							return;
						monitor.subTask("Datei: " + docHandle.getTitle());
						docHandle.exportToFileSystem();
						monitor.worked(1);
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
