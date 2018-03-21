/*******************************************************************************
 * Copyright (c) 2013 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.ui.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import at.medevit.atc_codes.ATCCodeService;

public class Activator implements BundleActivator {
	
	private ServiceTracker serviceTracker;
	
	@Override
	public void start(BundleContext context) throws Exception{
		ATCCodeServiceTracker customer = new ATCCodeServiceTracker(context);
		serviceTracker = new ServiceTracker(context, ATCCodeService.class.getName(), customer);
		serviceTracker.open();
		
	}
	
	@Override
	public void stop(BundleContext context) throws Exception{
		serviceTracker.close();
	}
	
}
