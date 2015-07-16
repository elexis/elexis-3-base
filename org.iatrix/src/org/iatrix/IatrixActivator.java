/*******************************************************************************
 * Copyright (c) 2007-2013, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.iatrix.data.Problem;
import org.osgi.framework.BundleContext;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.icpc.Encounter;
import ch.elexis.icpc.Episode;

public class IatrixActivator extends AbstractUIPlugin {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "org.iatrix";

	/**
	 * The shared instance
	 */
	private static IatrixActivator instance;

	// hide constructor
	public IatrixActivator(){
		instance = this;
		UiDesk.getImageRegistry().put(Iatrix.IMG_ACTIVE, getImageDescriptor("icons/active.png"));
		UiDesk.getImageRegistry().put(Iatrix.IMG_INACTIVE, getImageDescriptor("icons/inactive.png"));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception{
		super.start(context);

		// Make sure Iatrix and ICPC tables are initialized.
		// Work-around for cases where tables are initialized outside the main GUI thread.
		// PersistentObject.createOrModifyTable might throw an exception in this case.
		// Thus, we force Episode, Encounter and Problem to create their tables here.

		// force initialization of these classes
		Class.forName(Episode.class.getName());
		Class.forName(Encounter.class.getName());
		Class.forName(Problem.class.getName());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception{
		instance = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static IatrixActivator getInstance(){
		return instance;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 *
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path){
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
