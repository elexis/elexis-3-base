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
 * An IValueFormatter describes a conversion for a value in both directions.
 * Formatters are used for display and editing, that's why both directions are
 * there. If you implement a formatter that's only for display, let the parse
 * method throw an {@link UnsupportedOperationException}.
 *
 * @author Ralf Ebert <info@ralfebert.de>
 */
public interface IValueFormatter<FROM, TO> {

	public TO format(FROM obj);

	public FROM parse(TO obj);

}
