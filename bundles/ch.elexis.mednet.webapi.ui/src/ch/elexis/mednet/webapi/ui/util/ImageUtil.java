package ch.elexis.mednet.webapi.ui.util;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.ResourceManager;

import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;

public class ImageUtil {

	public static Image getScaledImage(Display display, String path, int width, int height, Label label) {
		Image originalImage = ResourceManager.getPluginImage(PreferenceConstants.MEDNET_PLUGIN_STRING, path);
		if (originalImage == null || originalImage.isDisposed()) {
			return null;
		}
		ImageData originalImageData = originalImage.getImageData();
		if (width <= 1 || height <= 1) {
			width = originalImageData.width;
			height = originalImageData.height;
		}
		ImageData scaledImageData = originalImageData.scaledTo(width, height);
		Image scaledImage = new Image(display, scaledImageData);
		label.addDisposeListener(e -> {
			if (!scaledImage.isDisposed()) {
				scaledImage.dispose();
			}
		});
		return scaledImage;
	}
}
