package ch.elexis.global_inbox;

import java.io.File;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ch.elexis.core.data.activator.CoreHub;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IStartup {
	
	// The plug-in ID
	public static final String PLUGIN_ID = "ch.elexis.global_inbox"; //$NON-NLS-1$
	
	// The shared instance
	private static Activator plugin;
	private InboxContentProvider contentProvider = new InboxContentProvider();
	
	/**
	 * The constructor
	 */
	public Activator(){}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
	 */
	public void start(BundleContext context) throws Exception{
		super.start(context);
		plugin = this;
		
		String giDirSetting = CoreHub.localCfg.get(Preferences.PREF_DIR, "NOTSET");
		if("NOTSET".equals(giDirSetting)) {
			File giDir = new File(CoreHub.getWritableUserDir(), "GlobalInbox");
			boolean created = giDir.mkdir();
			if(created) {
				CoreHub.localCfg.set(Preferences.PREF_DIR, giDir.getAbsolutePath());
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
	 */
	public void stop(BundleContext context) throws Exception{
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault(){
		return plugin;
	}
	
	public InboxContentProvider getContentProvider(){
		return contentProvider;
	}
	
	@Override
	public void earlyStartup(){}
	
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
}
