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
package at.medevit.elexis.inbox.ui.part.provider;

import java.time.LocalDate;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.ui.part.model.GroupedInboxElements;
import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;

public interface IInboxElementUiProvider {
	/**
	 * Image that will be placed on the filter action.
	 *
	 * @return ImageDescriptor or null
	 */
	public ImageDescriptor getFilterImage();

	/**
	 * Filter that will be applied with the filter action.
	 *
	 * @return ViewerFilter or null
	 */
	public ViewerFilter getFilter();

	/**
	 * LabelProvider used by the inbox viewer.
	 *
	 * @return LabelProvider or null
	 */
	public LabelProvider getLabelProvider();

	/**
	 * ColorProvider used by the inbox viewer.
	 *
	 * @return IColorProvider or null
	 */
	public IColorProvider getColorProvider();

	/**
	 * Test if this provider shall be used for the element.
	 *
	 * @param element
	 * @return
	 */
	public boolean isProviderFor(IInboxElement element);

	/**
	 * Method called when element is double clicked.
	 *
	 * @param element
	 */
	public void doubleClicked(IInboxElement element);

	/**
	 * Test if the element should be visible in the ui.
	 *
	 * @param element
	 * @return
	 */
	public default boolean isVisible(IInboxElement element) {
		return true;
	}

	/**
	 * Get the {@link LocalDate} for the element.
	 * 
	 * @return
	 */
	public LocalDate getObjectDate(IInboxElement element);

	/**
	 * Test if the {@link IInboxElementUiProvider} supports grouping of
	 * {@link IInboxElement}s.
	 *
	 * @return
	 */
	public default boolean isGrouped() {
		return false;
	}

	/**
	 * Get a {@link GroupedInboxElements} instance for the provided
	 * {@link PatientInboxElements} and {@link IInboxElement}. The returned
	 * {@link GroupedInboxElements} is not added to the {@link PatientInboxElements}
	 * by this method.
	 *
	 * @param patientInboxElements
	 * @param element
	 * @return
	 */
	public default GroupedInboxElements getGrouped(PatientInboxElements patientInboxElements, IInboxElement element) {
		return null;
	}
}
