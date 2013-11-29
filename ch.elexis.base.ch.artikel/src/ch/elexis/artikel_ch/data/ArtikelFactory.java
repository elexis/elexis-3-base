/*******************************************************************************
 * Copyright (c) 2006-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.artikel_ch.data;

import java.lang.reflect.Method;

import ch.elexis.data.PersistentObject;
import ch.elexis.data.PersistentObjectFactory;

public class ArtikelFactory extends PersistentObjectFactory {
	
	public ArtikelFactory(){}
	
	@SuppressWarnings("unchecked")
	@Override
	public PersistentObject createFromString(String code){
		try {
			String[] ci = code.split("::"); //$NON-NLS-1$
			// Workaround for compatibility with older package structure
			if (ci[0].startsWith("ch.elexis.data")) { //$NON-NLS-1$
				int ix = ci[0].lastIndexOf('.');
				ci[0] = "ch.elexis.artikel_ch.data" + ci[0].substring(ix); //$NON-NLS-1$
			}
			Class clazz = Class.forName(ci[0]);
			Method load = clazz.getMethod("load", new Class[] { String.class}); //$NON-NLS-1$
			return (PersistentObject) (load.invoke(null, new Object[] {
				ci[1]
			}));
		} catch (Exception ex) {
			
			// ExHandler.handle(ex);
			return null;
			
		}
	}
	
	@Override
	protected PersistentObject doCreateTemplate(Class<? extends PersistentObject> typ){
		try {
			return (PersistentObject) typ.newInstance();
		} catch (Exception e) {
			// ExHandler.handle(e);
			return null;
		}
		
	}
	
	@Override
	public Class getClassforName(String fullyQualifiedClassName){
		Class ret = null;
		try {
			ret = Class.forName(fullyQualifiedClassName);
			return ret;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return ret;
		}
	}
}
