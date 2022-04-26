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
package ch.unibe.iam.scg.archie.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * An abstract chart page, providing basic methods and error handling functions
 * for chart wizard pages.
 * </p>
 *
 * $Id: AbstractChartPage.java 705 2009-01-03 17:48:46Z peschehimself $.
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 705 $
 */
public abstract class AbstractChartPage extends WizardPage {

	/**
	 * The Constructor.
	 *
	 * @param pageName the page name
	 */
	protected AbstractChartPage(String pageName) {
		super(pageName);
	}

	/**
	 * The Constructor.
	 *
	 * @param pageName   the page name
	 * @param title      the title
	 * @param titleImage the title image
	 */
	protected AbstractChartPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * Creates the control.
	 *
	 * @param parent the parent
	 *
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(Composite)
	 */
	abstract public void createControl(Composite parent);

	/**
	 * Applies the given status to the status line of a wizard page. This method is
	 * mostly used by subclasses upon receiving an event and setting a status
	 * accordingly.
	 *
	 * @param status the status
	 */
	protected void applyToStatusLine(IStatus status) {
		String message = status.getMessage();
		if (message.length() == 0) {
			message = null;
		}

		switch (status.getSeverity()) {
		case IStatus.OK:
			this.setErrorMessage(null);
			this.setMessage(message);
			break;
		case IStatus.WARNING:
			this.setErrorMessage(null);
			this.setMessage(message, WizardPage.WARNING);
			break;
		case IStatus.INFO:
			this.setErrorMessage(null);
			this.setMessage(message, WizardPage.INFORMATION);
			break;
		default:
			this.setErrorMessage(message);
			this.setMessage(null);
			break;
		}
	}
}