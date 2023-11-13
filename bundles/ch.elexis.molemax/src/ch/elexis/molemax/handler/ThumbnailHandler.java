package ch.elexis.molemax.handler;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ThumbnailHandler {

	public static final int THUMBNAIL_MAX_WIDTH = 300;
	public static final int THUMBNAIL_MAX_HEIGHT = 300;

	public static File createThumbnail(File originalImage) throws IOException {
		BufferedImage originalBufferedImage = ImageIO.read(originalImage);
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
		File thumbnailFile = new File(thumbnailDirectory, originalImage.getName());
		ImageIO.write(thumbnailBufferedImage, "jpg", thumbnailFile);
		return thumbnailFile;
	}
}
