package ch.elexis.labor.medics.v2;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Activator for percentile plugin
 * 
 * @author immi
 * 
 */
public class MedicsActivator extends AbstractUIPlugin {
	public static String TEXT_ENCODING = "ISO-8859-1"; //$NON-NLS-1$
	
	/** The shared instance */
	private static MedicsActivator plugin;
	
	/** The plug-in ID */
	public static final String PLUGIN_ID = "ch.elexis.laborimport.medics.v2"; //$NON-NLS-1$
	
	/** The constructor */
	public MedicsActivator(){
		plugin = this;
	}
	
	/**
	 * Returns an instance of this activator object.
	 * 
	 * @return The shared instance
	 */
	public static MedicsActivator getInstance(){
		return plugin;
	}
	
	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path){
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
