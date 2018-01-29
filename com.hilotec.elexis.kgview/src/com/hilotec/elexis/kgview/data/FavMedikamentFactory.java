package com.hilotec.elexis.kgview.data;

import java.lang.reflect.Method;

import ch.elexis.data.PersistentObject;
import ch.elexis.data.PersistentObjectFactory;

public class FavMedikamentFactory extends PersistentObjectFactory {
	@Override
	public PersistentObject createFromString(final String code){
		try {
			String[] ci = code.split("::"); //$NON-NLS-1$
			@SuppressWarnings("rawtypes")
			Class clazz = Class.forName(ci[0]);
			@SuppressWarnings("unchecked")
			Method load = clazz.getMethod("load", new Class[] {String.class}); //$NON-NLS-1$
			return (PersistentObject) (load.invoke(null, new Object[] {
				ci[1]
			}));
		} catch (Exception ex) {
			return null;
		}
	}
	
	@Override
	public PersistentObject doCreateTemplate(@SuppressWarnings("rawtypes") final Class typ){
		try {
			return (PersistentObject) typ.newInstance();
		} catch (Exception ex) {
			return null;
		}
	}
}
