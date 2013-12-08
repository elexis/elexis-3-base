package ch.elexis.base.messages;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import ch.elexis.core.data.activator.CoreHub;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements org.eclipse.ui.IStartup {
	MsgHeartListener heartListener;
	
	// The plug-in ID
	public static final String PLUGIN_ID = "ch.elexis.messages"; //$NON-NLS-1$
	
	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator(){}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception{
		super.start(context);
		heartListener = new MsgHeartListener();
		CoreHub.heart.addListener(heartListener);
		plugin = this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception{
		plugin = null;
		CoreHub.heart.removeListener(heartListener);
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
	
	public void earlyStartup(){
		
	}
	
}
