package ch.elexis.global_inbox;

import java.io.File;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.IDocumentStore;

@Component(service = {})
public class StartupComponent {
	
	// The plug-in ID
	public static final String PLUGIN_ID = "ch.elexis.global_inbox"; //$NON-NLS-1$
	
	// The shared instance
	private static StartupComponent INSTANCE;
	private InboxContentProvider contentProvider;
	
	// wait for needed services
	@Reference
	private IConfigService configServgice;
	
	@Reference
	private IContextService contextService;
	
	@Reference(target = "(storeid=ch.elexis.data.store.omnivore)")
	private IDocumentStore documentStore;
	
	public InboxContentProvider getContentProvider(){
		return contentProvider;
	}
	
	public String getCategory(File file){
		String dir = CoreHub.localCfg.get(Preferences.PREF_DIR, Preferences.PREF_DIR_DEFAULT); //$NON-NLS-1$
		File parent = file.getParentFile();
		if (parent == null) {
			return Messages.Activator_noInbox;
		} else {
			String fname = parent.getAbsolutePath();
			if (fname.startsWith(dir)) {
				if (fname.length() > dir.length()) {
					return fname.substring(dir.length() + 1);
				} else {
					return "-"; //$NON-NLS-1$
				}
				
			} else {
				return "??"; //$NON-NLS-1$
			}
		}
		
	}
	
	@Activate
	public void activate(){
		INSTANCE = this;
		contentProvider = new InboxContentProvider();
		
		String giDirSetting = configServgice.getLocal(Preferences.PREF_DIR, "NOTSET");
		if ("NOTSET".equals(giDirSetting)) {
			File giDir = new File(CoreHub.getWritableUserDir(), "GlobalInbox");
			boolean created = giDir.mkdir();
			if (created) {
				CoreHub.localCfg.set(Preferences.PREF_DIR, giDir.getAbsolutePath());
			}
		}
	}
	
	public static StartupComponent getInstance(){
		return INSTANCE;
	}
}
