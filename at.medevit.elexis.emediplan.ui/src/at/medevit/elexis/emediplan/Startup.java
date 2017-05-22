package at.medevit.elexis.emediplan;

import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.emediplan.core.EMediplanService;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medication;
import at.medevit.elexis.emediplan.ui.ImportEMediplanDialog;
import ch.elexis.barcode.scanner.BarcodeScannerMessage;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.core.ui.UiDesk;

public class Startup implements IStartup {
	private static Logger logger = LoggerFactory.getLogger(Startup.class);
	
	ElexisEventListener elexisEventListenerImpl;
	
	@Override
	public void earlyStartup(){
		elexisEventListenerImpl =
			new ElexisEventListenerImpl(BarcodeScannerMessage.class, ElexisEvent.EVENT_UPDATE) {
				public void run(ElexisEvent ev){
					BundleContext bundleContext =
						FrameworkUtil.getBundle(getClass()).getBundleContext();
					BarcodeScannerMessage b = (BarcodeScannerMessage) ev.getGenericObject();
					ServiceReference<EMediplanService> eMediplanServiceRef =
						bundleContext.getServiceReference(EMediplanService.class);
					if (eMediplanServiceRef != null) {
						EMediplanService eMediplanService =
							bundleContext.getService(eMediplanServiceRef);
						Medication medication = eMediplanService.createModelFromChunk(b.getChunk());
						eMediplanService.evalulateArtikelForMedication(medication);
						if (medication != null) {
							UiDesk.getDisplay().asyncExec(new Runnable() {
								public void run(){
									logger.debug("Opening ImportEMediplanDialog");
									ImportEMediplanDialog dlg =
										new ImportEMediplanDialog(UiDesk.getTopShell(), medication);
									dlg.open();
								}
							});
						}
					}
				}
			};
		ElexisEventDispatcher.getInstance().addListeners(elexisEventListenerImpl);
		
	}
	
}
