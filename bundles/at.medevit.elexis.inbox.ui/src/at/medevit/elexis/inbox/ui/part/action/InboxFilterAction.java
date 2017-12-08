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
package at.medevit.elexis.inbox.ui.part.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class InboxFilterAction extends Action {
	private ImageDescriptor image;
	private ViewerFilter filter;
	private StructuredViewer viewer;
	
	public InboxFilterAction(StructuredViewer viewer, ViewerFilter extensionFilter,
		ImageDescriptor filterImage){
		this.viewer = viewer;
		this.filter = extensionFilter;
		this.image = filterImage;
	}
	
	@Override
	public ImageDescriptor getImageDescriptor(){
		return image;
	}
	
	@Override
	public int getStyle(){
		return Action.AS_CHECK_BOX;
	}
	
	@Override
	public void run(){
		if (isChecked()) {
			viewer.addFilter(filter);
		} else {
			viewer.removeFilter(filter);
		}
	}
	
}
