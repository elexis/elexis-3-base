package at.medevit.elexis.loinc.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.loinc.model.ILoincCodeService;

public class LoincServiceComponent {
	
	private static Logger logger = LoggerFactory.getLogger(LoincServiceComponent.class);

	private static ILoincCodeService service;
	private static boolean updated = false;

	public static ILoincCodeService getService(){
		logger.info("Get updated " + updated + " service " + service);
		if (!updated) {
			// The login job
			final IRunnableWithProgress job = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException{
					service.updateTop2000();
				}
			};
			
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				
				@Override
				public void run(){
					try {
						PlatformUI.getWorkbench().getProgressService().busyCursorWhile(job);
					} catch (InvocationTargetException e) {
						throw new IllegalStateException("Update failed." + e);
					} catch (InterruptedException e) {
						throw new IllegalStateException("Update failed." + e);
					}
				}
			});
		}
		return service;
	}
	
	// Method will be used by DS to set the quote service
	public synchronized void setService(ILoincCodeService service){
		LoincServiceComponent.service = service;
	}
	
	// Method will be used by DS to unset the quote service
	public synchronized void unsetService(ILoincCodeService service){
		if (LoincServiceComponent.service == service) {
			LoincServiceComponent.service = null;
		}
	}
}
