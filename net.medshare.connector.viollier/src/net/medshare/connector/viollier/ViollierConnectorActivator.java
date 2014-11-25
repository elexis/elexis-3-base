/*******************************************************************************
 * 
 * The authorship of this code and the accompanying materials is held by 
 * medshare GmbH, Switzerland. All rights reserved. 
 * http://medshare.net
 * 
 * This code and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0
 * 
 * Year of publication: 2013
 * 
 *******************************************************************************/
package net.medshare.connector.viollier;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ViollierConnectorActivator extends AbstractUIPlugin {
	
	public static String TEXT_ENCODING = "ISO-8859-1"; //$NON-NLS-1$
	
	// The plug-in ID
	public static final String PLUGIN_ID = "net.medshare.connector.viollier"; //$NON-NLS-1$
	
	// The shared instance
	private static ViollierConnectorActivator plugin;
	
	/**
	 * The constructor
	 */
	public ViollierConnectorActivator(){
		plugin = this;
	}
	
	/**
	 * Returns an instance of this activator object.
	 * 
	 * @return The shared instance
	 */
	public static ViollierConnectorActivator getInstance(){
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception{
		super.start(context);
		plugin = this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
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
	public static ViollierConnectorActivator getDefault(){
		return plugin;
	}
	
}
