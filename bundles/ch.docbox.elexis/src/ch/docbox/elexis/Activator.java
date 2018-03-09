/*******************************************************************************
 * Copyright (c) 2010, Oliver Egger, visionary ag
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *    
 *******************************************************************************/
package ch.docbox.elexis;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ch.elexis.core.ui.util.Log;

// public class Activator extends AbstractUIPlugin implements org.eclipse.ui.IStartup, HeartListener {
public class Activator extends AbstractUIPlugin implements org.eclipse.ui.IStartup {
	public static DocboxBackgroundJob docboxBackgroundJob;
	
	public static final String PLUGIN_ID = "ch.docbox.elexis";
	
	private static Activator plugin;
	
	public static final String IMG_DOC2DOC = Activator.PLUGIN_ID + "/doc2doc"; //$NON-NLS-1$
	public static final String IMG_DOC2DOC_PATH = "icons/doc2doc16.png"; //$NON-NLS-1$
	
	public static Log log = Log.get(PLUGIN_ID); //$NON-NLS-1$
	
	/**
	 * The constructor
	 */
	public Activator(){
		log.log("Activator", Log.DEBUGMSG);
	}
	
	public static ImageDescriptor getImageDescriptor(final String path){
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception{
		log.log("start", Log.DEBUGMSG);
		super.start(context);
		plugin = this;
		log.log("createBackGroundJob", Log.DEBUGMSG);
		docboxBackgroundJob = new DocboxBackgroundJob();
		log.log("start finished", Log.DEBUGMSG);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception{
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
	
	public void earlyStartup(){
		
	}
	
	public void heartbeat(){}
	
}
