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
package ch.elexis.base.ch.medikamente.bag.data;

import java.lang.reflect.Method;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.elexis.core.data.PersistentObject;
import ch.elexis.core.data.PersistentObjectFactory;

public class BAGMediFactory extends PersistentObjectFactory {
	
	@Override
	public PersistentObject createFromString(final String code){
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
	protected PersistentObject doCreateTemplate(final Class typ){
		try {
			return (PersistentObject) typ.newInstance();
		} catch (Exception e) {
			// ExHandler.handle(e);
			return null;
		}
		
	}
	
	public static ImageDescriptor loadImageDescriptor(final String path){
		return AbstractUIPlugin
			.imageDescriptorFromPlugin("ch.elexis.base.ch.medikamente.bag", path);
	}
}
