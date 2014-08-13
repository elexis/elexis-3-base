/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.ui.extension;

import java.util.List;

/**
 * A wizard category contains wizard elements.
 */
public interface IWizardCategory {

	/**
	 * Return the identifier of this category.
	 * 
	 * @return the identifier of this category
	 */
	String getId();

	/**
	 * Return the label for this category.
	 * 
	 * @return the label for this category
	 */
	String getLabel();

	/**
	 * Return the wizards in this category.
	 * 
	 * @return the wizards in this category. Never <code>null</code>
	 */
	List<IWizardDescriptor> getWizards();

	/**
	 * Add a wizard to this category.
	 * 
	 * @param wizard
	 */
	void addWizard(IWizardDescriptor wizard);
}
