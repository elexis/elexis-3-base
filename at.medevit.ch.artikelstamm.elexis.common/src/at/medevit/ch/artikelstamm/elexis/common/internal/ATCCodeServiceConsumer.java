/*******************************************************************************
 * Copyright (c) 2013-2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.elexis.common.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.atc_codes.ATCCodeService;

public class ATCCodeServiceConsumer {
	
	private static Logger log = LoggerFactory.getLogger(ATCCodeServiceConsumer.class);
	private static ATCCodeService atcCodeService = null;
	
	public synchronized void bind(ATCCodeService consumer){
		atcCodeService = consumer;
		log.debug("Binding " + consumer);
	}
	
	public synchronized void unbind(ATCCodeService consumer){
		atcCodeService = null;
	}
	
	public static ATCCodeService getATCCodeService(){
		return atcCodeService;
	}
}
