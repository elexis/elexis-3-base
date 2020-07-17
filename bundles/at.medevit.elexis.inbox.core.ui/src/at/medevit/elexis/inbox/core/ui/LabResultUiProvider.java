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
package at.medevit.elexis.inbox.core.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import at.medevit.elexis.inbox.core.ui.filter.PathologicInboxFilter;
import at.medevit.elexis.inbox.model.InboxElement;
import at.medevit.elexis.inbox.ui.part.provider.IInboxElementUiProvider;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.data.LabResult;

public class LabResultUiProvider implements IInboxElementUiProvider {
	private static DecorationOverlayIcon pathologicLabImage;
	
	private LabResultLabelProvider labelProvider;
	private PathologicInboxFilter filter;
	
	public LabResultUiProvider(){
		labelProvider = new LabResultLabelProvider();
	}
	
	@Override
	public ImageDescriptor getFilterImage(){
		if (pathologicLabImage == null) {
			initializeImages();
		}
		return pathologicLabImage;
	}
	
	@Override
	public ViewerFilter getFilter(){
		if (filter == null) {
			filter = new PathologicInboxFilter();
		}
		return filter;
	}
	
	@Override
	public LabelProvider getLabelProvider(){
		return labelProvider;
	}
	
	@Override
	public IColorProvider getColorProvider(){
		return labelProvider;
	}
	
	@Override
	public boolean isProviderFor(InboxElement element){
		Object obj = element.getObject();
		if (obj instanceof LabResult) {
			return true;
		} else if (obj instanceof ILabResult) {
			return true;
		}
		return false;
	}
	
	private static void initializeImages(){
		ImageDescriptor[] overlays = new ImageDescriptor[1];
		overlays[0] = AbstractUIPlugin.imageDescriptorFromPlugin("at.medevit.elexis.inbox.ui", //$NON-NLS-1$
			"/rsc/img/achtung_overlay.png"); //$NON-NLS-1$
		
		pathologicLabImage =
			new DecorationOverlayIcon(Images.IMG_VIEW_LABORATORY.getImage(), overlays);
	}
	
	@Override
	public void doubleClicked(InboxElement element){
		// TODO Auto-generated method stub
		
	}
}
