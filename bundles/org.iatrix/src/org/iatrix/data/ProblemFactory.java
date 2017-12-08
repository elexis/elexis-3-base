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
package org.iatrix.data;

import java.lang.reflect.Method;

import ch.elexis.data.PersistentObject;
import ch.elexis.data.PersistentObjectFactory;
import ch.rgw.tools.ExHandler;

public class ProblemFactory extends PersistentObjectFactory {
	
	public ProblemFactory(){}
	
	@Override
	public PersistentObject createFromString(String code){
		try {
			String[] ci = code.split("::");
			Class clazz = Class.forName(ci[0]);
			Method load = clazz.getMethod("load", new Class[] {
				String.class
			});
			return (PersistentObject) (load.invoke(null, new Object[] {
				ci[1]
			}));
		} catch (Exception ex) {
			// ExHandler.handle(ex);
			return null;
			
		}
	}
	
	@Override
	public PersistentObject doCreateTemplate(Class typ){
		try {
			return (PersistentObject) typ.newInstance();
		} catch (Exception e) {
			// ExHandler.handle(e); // Sorry I don't like those exceptions in the log
			return null;
		}
		
	}
}
