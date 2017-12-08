/*******************************************************************************
 * Copyright (c) 2005-2006, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 * $Id: MesswertFactory.java 1185 2006-10-29 15:29:30Z rgw_ch $
 *******************************************************************************/

package ch.elexis.befunde;

import java.lang.reflect.Method;

import ch.elexis.data.PersistentObject;
import ch.elexis.data.PersistentObjectFactory;

/**
 * A PersistentObjectFactory is a class that can create instances of a subclass of PersistentObject.
 * It can either retrieve an object from a string representation or create a volatile template. A
 * Factory is necessary, when elexis needs to transfer instances of a class via dra&drop or load
 * instances from the database. Elexis cannot access the object's constructor by itself due to
 * classpath limitations of eclipse. The design of such a factory is quite simple and can almost
 * always just be copied from here.
 * 
 * Eine PersistentObjectFactory ist eine Klasse, die Objekte einer von PersistentObject abgeleiteten
 * Klasse erstellen kann. Sie kann einerseits ein Objekt anhand einer Sgtring-Repräsentation aus der
 * Datenbank holen und andererseits auch ein Objekt als nicht gespeichertes Template erzeugen. Eine
 * Factory wird in einem Plugin immer dann benötigt, wenn Elexis Objekte dieses Plugins per
 * Drag&Drop transferieren oder aus der Datenbank einlesen muss. Dies deswegen, weil der Eclipse
 * Class loader einen Classpath nicht über Plugin-Grenzen hinweg auflösen kann (ch.elexis,data im
 * Kernprogramm referenziert nicht dasselbe Package wie ch.elexis.data in einem Plugin)
 * 
 * Die Klasse selbst ist sehr einfach und kann direkt per Copy&Paste aus dieser Vorlage hier
 * übernommen werden.
 * 
 * @author gerry
 * 
 */
public class MesswertFactory extends PersistentObjectFactory {
	public PersistentObject createFromString(String code){
		try {
			String[] ci = code.split("::"); //$NON-NLS-1$
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
	public PersistentObject doCreateTemplate(Class typ){
		try {
			return (PersistentObject) typ.newInstance();
		} catch (Exception ex) {
			// ExHandler.handle(ex);
			return null;
		}
	}
}
