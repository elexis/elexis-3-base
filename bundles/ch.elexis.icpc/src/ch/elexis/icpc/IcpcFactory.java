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

package ch.elexis.icpc;

import java.lang.reflect.Method;

import ch.elexis.core.constants.StringConstants;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.PersistentObjectFactory;

public class IcpcFactory extends PersistentObjectFactory {
	
	public IcpcFactory(){}
	
	public PersistentObject createFromString(String code){
		try {
			String[] ci = code.split(StringConstants.DOUBLECOLON);
			Class<?> clazz = Class.forName(ci[0]);
			Method load = clazz.getMethod("load", new Class[] {
				String.class
			});
			PersistentObject ic;
			if (ci.length == 2) {
				ic = (PersistentObject) (load.invoke(null, new Object[] {
					ci[1]
				}));
				if (!ic.exists()) {
					String l = "*" + ci[1].substring(1);
					ic = (PersistentObject) load.invoke(null, new Object[] {
						l
					});
					((IcpcCode) ic).setLabel(ci[1]);
				}
				
			} else {
				ic = (IcpcCode) (load.invoke(null, new Object[] {
					ci[1]
				}));
				((IcpcCode) ic).setLabel(ci[2]);
			}
			return ic;
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
