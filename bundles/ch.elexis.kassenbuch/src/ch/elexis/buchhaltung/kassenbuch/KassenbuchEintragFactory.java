/*******************************************************************************
 * Copyright (c) 2007, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.buchhaltung.kassenbuch;

import java.lang.reflect.Method;

import ch.elexis.data.PersistentObject;
import ch.elexis.data.PersistentObjectFactory;

/**
 * This factory is needed by ch.elexis.data.Query to create templates for the
 * query by example-operation In most cases, the implementation can just be
 * copied to enable this behjaviour in other plugins.
 *
 * @author gerry
 *
 */
public class KassenbuchEintragFactory extends PersistentObjectFactory {
	public PersistentObject createFromString(String code) {
		try {
			String[] ci = code.split("::"); //$NON-NLS-1$
			Class clazz = Class.forName(ci[0]);
			Method load = clazz.getMethod("load", new Class[] { String.class }); //$NON-NLS-1$
			return (PersistentObject) (load.invoke(null, new Object[] { ci[1] }));
		} catch (Exception ex) {
			// ExHandler.handle(ex);
			return null;
		}
	}

	@Override
	public PersistentObject doCreateTemplate(Class typ) {
		try {
			return (PersistentObject) typ.newInstance();
		} catch (Exception ex) {
			// ExHandler.handle(ex);
			return null;
		}
	}
}
