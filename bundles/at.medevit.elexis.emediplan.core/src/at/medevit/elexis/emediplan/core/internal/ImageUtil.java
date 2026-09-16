package at.medevit.elexis.emediplan.core.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.slf4j.LoggerFactory;

public class ImageUtil {

	private static final int WHITE_THRESHOLD = 243;

	private ImageUtil() {
	}

	public static byte[] prepareForPrint(byte[] image) {
		if (image == null || image.length == 0) {
			return image;
		}
		try (ByteArrayInputStream input = new ByteArrayInputStream(image);
				ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			ImageLoader loader = new ImageLoader();
			ImageData[] loaded = loader.load(input);
			if (loaded.length == 0) {
				return image;
			}
			ImageData source = loaded[0];
			Rectangle content = getContentBounds(source);
			if (content == null) {
				// nothing but white
				return image;
			}
			if (content.width == source.width && content.height == source.height) {
				// no border to remove
				return image;
			}
			loader.data = new ImageData[] { crop(source, content) };
			loader.compression = 100;
			loader.save(output, SWT.IMAGE_JPEG);
			return output.toByteArray();
		} catch (IOException | SWTException | SWTError | IllegalArgumentException e) {
			LoggerFactory.getLogger(ImageUtil.class).warn("Could not prepare picture", e); //$NON-NLS-1$
			return image;
		}
	}

	private static Rectangle getContentBounds(ImageData source) {
		int minX = source.width;
		int minY = source.height;
		int maxX = -1;
		int maxY = -1;
		for (int y = 0; y < source.height; y++) {
			for (int x = 0; x < source.width; x++) {
				RGB rgb = source.palette.getRGB(source.getPixel(x, y));
				if (rgb.red < WHITE_THRESHOLD || rgb.green < WHITE_THRESHOLD || rgb.blue < WHITE_THRESHOLD) {
					minX = Math.min(minX, x);
					maxX = Math.max(maxX, x);
					minY = Math.min(minY, y);
					maxY = Math.max(maxY, y);
				}
			}
		}
		if (maxX < 0) {
			return null;
		}
		return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
	}

	private static ImageData crop(ImageData source, Rectangle bounds) {
		if (bounds.width == source.width && bounds.height == source.height) {
			return source;
		}
		ImageData ret = new ImageData(bounds.width, bounds.height, source.depth, source.palette);
		for (int y = 0; y < bounds.height; y++) {
			for (int x = 0; x < bounds.width; x++) {
				ret.setPixel(x, y, source.getPixel(bounds.x + x, bounds.y + y));
			}
		}
		return ret;
	}
}
