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
package at.medevit.elexis.outbox.ui.part.provider;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;

import at.medevit.elexis.outbox.model.IOutboxElement;

public interface IOutboxElementUiProvider {
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
	 * LabelProvider used by the outbox viewer.
	 * 
	 * @return LabelProvider or null
	 */
	public LabelProvider getLabelProvider();
	
	/**
	 * ColorProvider used by the outbox viewer.
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
	public boolean isProviderFor(IOutboxElement element);
	
	/**
	 * Method called when element is double clicked.
	 * 
	 * @param element
	 */
	public void doubleClicked(IOutboxElement element);
}
