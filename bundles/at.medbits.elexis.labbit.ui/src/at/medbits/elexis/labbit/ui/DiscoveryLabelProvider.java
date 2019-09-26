/*******************************************************************************
 * Copyright (c) 2019 Medbits GmbH.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Thomas Huster - initial API and implementation
 *******************************************************************************/
package at.medbits.elexis.labbit.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import at.medbits.elexis.labbit.discovery.DiscoveryInfo;
import ch.elexis.core.ui.icons.Images;

public class DiscoveryLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element){
		if (element instanceof DiscoveryInfo) {
			DiscoveryInfo info = (DiscoveryInfo) element;
			return info.getProperty("configids") + " @" + info.getLocation().getHost();
		}
		return super.getText(element);
	}
	
	@Override
	public Image getImage(Object element){
		return Images.IMG_VIEW_LABORATORY.getImage();
	}
}
