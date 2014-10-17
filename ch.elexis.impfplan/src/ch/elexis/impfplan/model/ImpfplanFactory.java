/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/
package ch.elexis.impfplan.model;

import java.lang.reflect.Method;

import ch.elexis.core.constants.StringConstants;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.PersistentObjectFactory;

public class ImpfplanFactory extends PersistentObjectFactory {
	
	public PersistentObject createFromString(String code){
		try {
			String[] ci = code.split(StringConstants.DOUBLECOLON);
			Class<?> clazz = Class.forName(ci[0]);
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
	public PersistentObject doCreateTemplate(Class<? extends PersistentObject> typ){
		try {
			return (PersistentObject) typ.newInstance();
		} catch (Exception ex) {
			// ExHandler.handle(ex);
			return null;
		}
	}
	
}
