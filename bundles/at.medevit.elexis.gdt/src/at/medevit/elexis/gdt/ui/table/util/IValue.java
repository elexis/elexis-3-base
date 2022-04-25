/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.ui.table.util;

/**
 * IValue describes a value that can be get from / set to an object.
 *
 * @author Ralf Ebert <info@ralfebert.de>
 */
public interface IValue {

	public Object getValue(Object element);

	public void setValue(Object element, Object value);

}
