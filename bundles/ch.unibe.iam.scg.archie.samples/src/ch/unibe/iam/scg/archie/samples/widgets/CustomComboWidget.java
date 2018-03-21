/*******************************************************************************
 * Copyright (c) 2008 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *******************************************************************************/
package ch.unibe.iam.scg.archie.samples.widgets;

import org.eclipse.swt.widgets.Composite;

import ch.unibe.iam.scg.archie.model.RegexValidation;
import ch.unibe.iam.scg.archie.ui.widgets.ComboWidget;

/**
 * <p>
 * TODO: DOCUMENT ME!
 * </p>
 * 
 * $Id$
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev$
 */
public class CustomComboWidget extends ComboWidget {

	public static final String DEFAULT_SELECTED = "Twenty";

	/**
	 * @param parent
	 * @param style
	 * @param labelText
	 */
	public CustomComboWidget(Composite parent, int style, final String labelText, RegexValidation regex) {
		super(parent, style, labelText, regex);

		// Populate combo items in a custom fashion. This can come out of a
		// file, database or wherever you like most.
		String[] items = new String[] { "Twenty", "Thirty", "Fourty" };
		this.setItems(items);
	}
}