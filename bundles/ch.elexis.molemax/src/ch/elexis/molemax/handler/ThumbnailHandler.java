package ch.elexis.molemax.handler;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThumbnailHandler {

	public static final int THUMBNAIL_MAX_WIDTH = 300;
	public static final int THUMBNAIL_MAX_HEIGHT = 300;
	private static Logger logger = LoggerFactory.getLogger(ThumbnailHandler.class);
	public static File createThumbnail(File originalImage) {
		try {
			BufferedImage originalBufferedImage = ImageIO.read(originalImage);
			if (originalBufferedImage == null) {
				logger.error("Error: The image could not be read: " + originalImage.getAbsolutePath());
				return null;
			}
			double aspectRatio = (double) originalBufferedImage.getWidth() / originalBufferedImage.getHeight();
			int thumbnailWidth = THUMBNAIL_MAX_WIDTH;
			int thumbnailHeight = (int) (THUMBNAIL_MAX_WIDTH / aspectRatio);
			if (thumbnailHeight > THUMBNAIL_MAX_HEIGHT) {
				thumbnailHeight = THUMBNAIL_MAX_HEIGHT;
				thumbnailWidth = (int) (THUMBNAIL_MAX_HEIGHT * aspectRatio);
			}
			Image thumbnailImage = originalBufferedImage.getScaledInstance(thumbnailWidth, thumbnailHeight,
					Image.SCALE_SMOOTH);
			BufferedImage thumbnailBufferedImage = new BufferedImage(thumbnailWidth, thumbnailHeight,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = thumbnailBufferedImage.createGraphics();
			g2d.drawImage(thumbnailImage, 0, 0, null);
			g2d.dispose();

			File thumbnailDirectory = new File(originalImage.getParentFile(), "thumbnails");
			if (!thumbnailDirectory.exists()) {
				thumbnailDirectory.mkdirs();
			}

			String extension = getFileExtension(originalImage.getName());
			File thumbnailFile = new File(thumbnailDirectory, originalImage.getName());
			boolean result = ImageIO.write(thumbnailBufferedImage, extension, thumbnailFile);
			if (!result) {
				logger.error("Error: Thumbnail could not be written: " + thumbnailFile.getAbsolutePath());
				return null;
			}
			return thumbnailFile;
		} catch (IOException e) {
			logger.error("Error when creating the thumbnail: " + e.getMessage(), e);

			return null;
		}
	}

	private static String getFileExtension(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(i + 1).toLowerCase();
		} else {
			return "jpg";
		}
	}

	public static boolean isSupportedImageFormat(String fileName) {
		String lowerCaseFileName = fileName.toLowerCase();
		return lowerCaseFileName.endsWith(".png") || lowerCaseFileName.endsWith(".jpg")
				|| lowerCaseFileName.endsWith(".jpeg") || lowerCaseFileName.endsWith(".bmp")
				|| lowerCaseFileName.endsWith(".gif");
	}
}
