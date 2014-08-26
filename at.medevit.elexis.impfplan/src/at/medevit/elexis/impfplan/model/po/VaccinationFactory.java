/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.impfplan.model.po;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.data.PersistentObject;
import ch.elexis.data.PersistentObjectFactory;

public class VaccinationFactory extends PersistentObjectFactory {
	
	private static Logger log = LoggerFactory.getLogger(VaccinationFactory.class);
	
	public PersistentObject createFromString(String code){
		try {
			String[] ci = code.split("::"); //$NON-NLS-1$
			
			// silently discard all requests we can't handle
			if (!ci[0].equals(Vaccination.class.getName())) {
				return null;
			}
			
			Class<?> clazz = Class.forName(ci[0]);
			Method load = clazz.getMethod("load", new Class[] { String.class}); //$NON-NLS-1$
			return (PersistentObject) (load.invoke(null, new Object[] {
				ci[1]
			}));
		} catch (Exception ex) {
			log.warn("", ex);
			return null;
		}
	}
	
	/**
	 * create a template of an instance of a given class. A template is an instance that is not
	 * stored in the database.
	 */
	@Override
	public PersistentObject doCreateTemplate(@SuppressWarnings("rawtypes") Class typ){
		try {
			// silently discard all requests we can't handle
			if (!typ.equals(Vaccination.class)) {
				return null;
			}
			return (PersistentObject) typ.newInstance();
		} catch (Exception ex) {
			log.warn(ex.getLocalizedMessage(), ex);
			return null;
		}
	}
	
	/**
	 * Return an instance of a class managed by the plug-in as described by {@link PersistentObject}
	 * .storeToString. This can be used to create an instance of {@link ch.elexis.data.Query}
	 * without direct access to the respective data type.
	 * 
	 * @param fullyQualifiedClassName
	 *            the first part of a {@link PersistentObject}.storeToString() representation<br>
	 *            e.g. {@code ch.elexis.data.Eigenartikel[::ID]}
	 * @return a class object of the referenced data type
	 */
	@Override
	public Class<?> getClassforName(String fullyQualifiedClassName){
		Class<?> ret = null;
		try {
			ret = Class.forName(fullyQualifiedClassName);
			return ret;
		} catch (ClassNotFoundException ex) {
			log.warn("", ex);
			return ret;
		}
	}
}
