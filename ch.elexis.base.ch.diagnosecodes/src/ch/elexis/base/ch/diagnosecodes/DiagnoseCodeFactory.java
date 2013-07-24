/*******************************************************************************
 * Copyright (c) 2006-2007, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.base.ch.diagnosecodes;

import java.lang.reflect.Method;

import ch.elexis.core.data.PersistentObject;
import ch.elexis.core.data.PersistentObjectFactory;
import ch.elexis.core.data.Query;

public class DiagnoseCodeFactory extends PersistentObjectFactory {
	
	public DiagnoseCodeFactory(){}
	
	public PersistentObject createFromString(String code){
		try {
			String[] ci = code.split("::"); //$NON-NLS-1$
			Class clazz = Class.forName(ci[0]);
			Method load = clazz.getMethod("load", new Class[] { String.class}); //$NON-NLS-1$
			PersistentObject ret = (PersistentObject) (load.invoke(null, new Object[] {
				ci[1]
			}));
			if (ret instanceof TICode) {
				return ret;
			}
			if (!ret.exists()) {
				if (clazz.getName().equals(ICD10.class.getName())) {
					String id = new Query<ICD10>(ICD10.class).findSingle("Code", "=", ci[1]); //$NON-NLS-1$ //$NON-NLS-2$
					if (id != null) {
						return (PersistentObject) (load.invoke(null, new Object[] {
							id
						}));
					}
				}
			}
			return ret;
		} catch (Exception ex) {
			// ExHandler.handle(ex);
			return null;
		}
	}
	
	@Override
	protected PersistentObject doCreateTemplate(Class typ){
		try {
			return (PersistentObject) typ.newInstance();
		} catch (Exception e) {
			// ExHandler.handle(e);
			return null;
		}
	}
	
}
