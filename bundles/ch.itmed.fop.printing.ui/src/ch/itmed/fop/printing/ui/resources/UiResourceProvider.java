/*******************************************************************************
 * Copyright (c) 2019 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.fop.printing.ui.resources;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class UiResourceProvider {
	private static final String PLUGIN_ID = "ch.itmed.fop.printing.ui"; //$NON-NLS-1$

	public static Image loadImage(String path) {
		ImageRegistry imageRegistry = JFaceResources.getImageRegistry();
		Image image = imageRegistry.get(PLUGIN_ID + path);
		if (image == null) {
			Bundle bundle = Platform.getBundle(PLUGIN_ID);
			URL url = FileLocator.find(bundle, new Path(path), null);
			ImageDescriptor imageDesc = ImageDescriptor.createFromURL(url);
			image = imageDesc.createImage();
			imageRegistry.put(PLUGIN_ID + path, image);
		}
		return image;
	}

}
