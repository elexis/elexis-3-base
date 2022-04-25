package at.medevit.elexis.impfplan.ui;

//Send questions, comments, bug reports, etc. to the authors:
//Rob Warner (rwarner@interspatial.com)
//Robert Harris (rbrt_harris@yahoo.com)

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

public class GraphicsUtil {
	public static void drawVerticalText(String string, Display display, int x, int y, GC gc, int style) {

		if (display == null)
			SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);

		// Determine string's dimensions
		Point pt = gc.textExtent(string);

		// Create an image the same size as the string
		Image stringImage = new Image(display, pt.x, pt.y);

		// Create a GC so we can draw the image
		GC stringGc = new GC(stringImage);

		// Set attributes from the original GC to the new GC
		stringGc.setForeground(gc.getForeground());
		stringGc.setBackground(gc.getBackground());
		stringGc.setFont(gc.getFont());

		// Draw the text onto the image
		stringGc.drawText(string, 0, 0);

		// Draw the image vertically onto the original GC
		drawVerticalImage(stringImage, display, x, y, gc, style);

		// Dispose the new GC
		stringGc.dispose();

		// Dispose the image
		stringImage.dispose();
	}

	/**
	 * Draws an image vertically (rotates plus or minus 90 degrees)
	 * <dl>
	 * <dt><b>Styles: </b></dt>
	 * <dd>UP, DOWN</dd>
	 * </dl>
	 *
	 * @param image           the image to draw
	 * @param x               the x coordinate of the top left corner of the drawing
	 *                        rectangle
	 * @param y               the y coordinate of the top left corner of the drawing
	 *                        rectangle
	 * @param gc              the GC on which to draw the image
	 * @param style           the style (SWT.UP or SWT.DOWN) and (SWT.TOP or
	 *                        SWT.BOTTOM)
	 *                        <p>
	 *                        Note: Only one of the style UP or DOWN may be
	 *                        specified. The TOP or BOTTOM denoty wether y is
	 *                        defined as the source or the target to bound to
	 *                        </p>
	 * @param containerHeight
	 */
	public static void drawVerticalImage(Image image, Display display, int x, int y, GC gc, int style) {
		// Get the current display
		if (display == null)
			SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);

		// Use the image's data to create a rotated image's data
		ImageData sd = image.getImageData();
		ImageData dd = new ImageData(sd.height, sd.width, sd.depth, sd.palette);

		// Determine which way to rotate, depending on up or down
		boolean up = (style & SWT.UP) == SWT.UP;

		// Run through the horizontal pixels
		for (int sx = 0; sx < sd.width; sx++) {
			// Run through the vertical pixels
			for (int sy = 0; sy < sd.height; sy++) {
				// Determine where to move pixel to in destination image data
				int dx = up ? sy : sd.height - sy - 1;
				int dy = up ? sd.width - sx - 1 : sx;

				// Swap the x, y source data to y, x in the destination
				dd.setPixel(dx, dy, sd.getPixel(sx, sy));
			}
		}

		// Create the vertical image
		Image vertical = new Image(display, dd);

		boolean bottom = (style & SWT.BOTTOM) == SWT.BOTTOM;
		if (bottom) {
			gc.drawImage(vertical, x, y - vertical.getImageData().height);
		} else {
			// Draw the vertical image onto the original GC
			gc.drawImage(vertical, x, y);
		}

		// Dispose the vertical image
		vertical.dispose();
	}
}
