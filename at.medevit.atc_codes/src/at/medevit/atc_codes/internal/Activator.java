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
package at.medevit.atc_codes.internal;

import java.io.File;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import at.medevit.atc_codes.ATCCodeService;
import at.medevit.atc_codes.ATCCodeServiceImpl;

public class Activator implements BundleActivator {
	
	private static BundleContext ctx;
	
	@Override
	public void start(BundleContext context) throws Exception{
		Activator.ctx = context;
		ctx.registerService(ATCCodeService.class, new ATCCodeServiceImpl(), null);
	}
	
	@Override
	public void stop(BundleContext context) throws Exception{
		ctx = null;
	}
	
	public static File getATCFile(){
		return ctx.getDataFile("rsc/2013ATC.XML");
	}
	
	public static File getATC_DDDFile(){
		return ctx.getDataFile("rsc/2013ATC_ddd.xml");
	}
	
}
