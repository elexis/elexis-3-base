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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import at.medevit.elexis.inbox.core.ui.filter.PathologicInboxFilter;
import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.ui.part.provider.IInboxElementUiProvider;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.LabServiceHolder;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.data.LabResult;
import ch.rgw.tools.Result;

public class LabResultUiProvider implements IInboxElementUiProvider {
	private static DecorationOverlayIcon pathologicLabImage;

	private LabResultLabelProvider labelProvider;
	private PathologicInboxFilter filter;

	public LabResultUiProvider() {
		labelProvider = new LabResultLabelProvider();
	}

	@Override
	public ImageDescriptor getFilterImage() {
		if (pathologicLabImage == null) {
			initializeImages();
		}
		return pathologicLabImage;
	}

	@Override
	public ViewerFilter getFilter() {
		if (filter == null) {
			filter = new PathologicInboxFilter();
		}
		return filter;
	}

	@Override
	public LabelProvider getLabelProvider() {
		return labelProvider;
	}

	@Override
	public IColorProvider getColorProvider() {
		return labelProvider;
	}

	@Override
	public boolean isProviderFor(IInboxElement element) {
		Object obj = element.getObject();
		if (obj instanceof LabResult) {
			return true;
		} else if (obj instanceof ILabResult) {
			return true;
		}
		return false;
	}

	private static void initializeImages() {
		ImageDescriptor[] overlays = new ImageDescriptor[1];
		overlays[0] = AbstractUIPlugin.imageDescriptorFromPlugin("at.medevit.elexis.inbox.ui", //$NON-NLS-1$
				"/rsc/img/achtung_overlay.png"); //$NON-NLS-1$

		pathologicLabImage = new DecorationOverlayIcon(Images.IMG_VIEW_LABORATORY.getImage(), overlays);
	}

	@Override
	public void doubleClicked(IInboxElement element) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isVisible(IInboxElement element) {
		Object obj = element.getObject();
		if (obj instanceof LabResult) {
			return StringUtils.isNotBlank(((LabResult) obj).getResult());
		} else if (obj instanceof ILabResult) {
			ILabResult labResult = (ILabResult) obj;
			if (StringUtils.isBlank(labResult.getResult())) {
				if (LabItemTyp.FORMULA == labResult.getItem().getTyp()) {
					Result<String> result = LabServiceHolder.get().evaluate(labResult);
					if (result.isOK() && StringUtils.isNotBlank(result.get())) {
						labResult.setResult(result.get());
						CoreModelServiceHolder.get().save(labResult);
					}
				}
				return StringUtils.isNotBlank(labResult.getResult());
			}
		}
		return true;
	}
}
