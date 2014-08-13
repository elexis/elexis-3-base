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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.Wizard;

public interface IWizardDescriptor {
	/**
	 * Return the label for this wizard.
	 * 
	 * @return the label for this wizard
	 */
	String getLabel();
	
	/**
	 * Create a wizard.
	 * 
	 * @return the wizard
	 * @throws CoreException
	 */
	Wizard createWizard() throws CoreException;
	
	/**
	 * Return the category id for this wizard.
	 * 
	 * @return the category or <code>null</code>
	 */
	String getCategoryId();
}
