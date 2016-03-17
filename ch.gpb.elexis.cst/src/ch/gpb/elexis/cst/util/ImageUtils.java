/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
package ch.gpb.elexis.cst.util;

/**
 * @author daniel ludin ludin@swissonline.ch
 * 27.06.2015
 * 
 * Helper class to convert between SWT Image and AWT BufferedImage
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class ImageUtils {

    public static BufferedImage convertToAWT(ImageData data) {
	ColorModel colorModel = null;
	PaletteData palette = data.palette;
	if (palette.isDirect) {
	    colorModel = new DirectColorModel(data.depth, palette.redMask, palette.greenMask, palette.blueMask);
	    BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(
		    data.width, data.height), false, null);
	    for (int y = 0; y < data.height; y++) {
		for (int x = 0; x < data.width; x++) {
		    int pixel = data.getPixel(x, y);
		    RGB rgb = palette.getRGB(pixel);
		    bufferedImage.setRGB(x, y, rgb.red << 16 | rgb.green << 8 | rgb.blue);
		}
	    }
	    return bufferedImage;
	} else {
	    RGB[] rgbs = palette.getRGBs();
	    byte[] red = new byte[rgbs.length];
	    byte[] green = new byte[rgbs.length];
	    byte[] blue = new byte[rgbs.length];
	    for (int i = 0; i < rgbs.length; i++) {
		RGB rgb = rgbs[i];
		red[i] = (byte) rgb.red;
		green[i] = (byte) rgb.green;
		blue[i] = (byte) rgb.blue;
	    }
	    if (data.transparentPixel != -1) {
		colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue, data.transparentPixel);
	    } else {
		colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue);
	    }
	    BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(
		    data.width, data.height), false, null);
	    WritableRaster raster = bufferedImage.getRaster();
	    int[] pixelArray = new int[1];
	    for (int y = 0; y < data.height; y++) {
		for (int x = 0; x < data.width; x++) {
		    int pixel = data.getPixel(x, y);
		    pixelArray[0] = pixel;
		    raster.setPixel(x, y, pixelArray);
		}
	    }
	    return bufferedImage;
	}
    }

    public static ImageData convertToSWT(BufferedImage bufferedImage) {
	if (bufferedImage.getColorModel() instanceof DirectColorModel) {
	    DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
	    PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(),
		    colorModel.getBlueMask());
	    ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
		    colorModel.getPixelSize(), palette);
	    for (int y = 0; y < data.height; y++) {
		for (int x = 0; x < data.width; x++) {
		    int rgb = bufferedImage.getRGB(x, y);
		    int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF));
		    data.setPixel(x, y, pixel);
		    if (colorModel.hasAlpha()) {
			data.setAlpha(x, y, (rgb >> 24) & 0xFF);
		    }
		}
	    }
	    return data;
	} else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
	    IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
	    int size = colorModel.getMapSize();
	    byte[] reds = new byte[size];
	    byte[] greens = new byte[size];
	    byte[] blues = new byte[size];
	    colorModel.getReds(reds);
	    colorModel.getGreens(greens);
	    colorModel.getBlues(blues);
	    RGB[] rgbs = new RGB[size];
	    for (int i = 0; i < rgbs.length; i++) {
		rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
	    }
	    PaletteData palette = new PaletteData(rgbs);
	    ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
		    colorModel.getPixelSize(), palette);
	    data.transparentPixel = colorModel.getTransparentPixel();
	    WritableRaster raster = bufferedImage.getRaster();
	    int[] pixelArray = new int[1];
	    for (int y = 0; y < data.height; y++) {
		for (int x = 0; x < data.width; x++) {
		    raster.getPixel(x, y, pixelArray);
		    data.setPixel(x, y, pixelArray[0]);
		}
	    }
	    return data;
	}
	return null;
    }

    static ImageData createSampleImage(Display display) {
	Image image = new Image(display, 100, 100);
	Rectangle bounds = image.getBounds();
	GC gc = new GC(image);
	gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
	gc.fillRectangle(bounds);
	gc.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
	gc.fillOval(0, 0, bounds.width, bounds.height);
	gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
	gc.drawLine(0, 0, bounds.width, bounds.height);
	gc.drawLine(bounds.width, 0, 0, bounds.height);
	gc.dispose();
	ImageData data = image.getImageData();
	image.dispose();
	return data;
    }

    public static BufferedImage[] splitImage(BufferedImage image, int rows, int cols) throws Exception {

	int chunks = rows * cols;

	int chunkWidth = image.getWidth() / cols; // determines the chunk width and height
	int chunkHeight = image.getHeight() / rows;
	int count = 0;
	BufferedImage imgs[] = new BufferedImage[chunks]; //Image array to hold image chunks
	for (int x = 0; x < rows; x++) {
	    for (int y = 0; y < cols; y++) {
		//Initialize the image array with image chunks

		int imageType = image.getType();
		if (imageType == 0) {
		    imageType = 5;
		}

		imgs[count] = new BufferedImage(chunkWidth, chunkHeight, imageType);

		// draws the image chunk
		Graphics2D gr = imgs[count++].createGraphics();
		gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y
			+ chunkWidth, chunkHeight * x + chunkHeight, null);
		gr.dispose();
	    }
	}

	//writing mini images into image files
	for (int i = 0; i < imgs.length; i++) {
	    ImageIO.write(imgs[i], "jpg", new File("D:\\tmp", "img" + i + ".jpg"));
	}

	return imgs;

    }


    public static BufferedImage[] splitImageByHeigth(BufferedImage image, int heigth) throws Exception {

	int rows = image.getHeight() / heigth;
	int remainingHeigth = image.getHeight() - (heigth * rows);
	if (remainingHeigth > 0) {
	    rows++;
	}

	int chunkWidth = image.getWidth(); // determines the chunk width and height
	int chunkHeight = heigth;

	int chunks = rows;

	int count = 0;
	BufferedImage imgs[] = new BufferedImage[chunks]; //Image array to hold image chunks

	for (int x = 0; x < rows; x++) {

	    int imageType = image.getType();
	    if (imageType == 0) {
		imageType = 5;
	    }

	    //Initialize the image array with image chunks
	    imgs[count] = new BufferedImage(chunkWidth, chunkHeight, imageType);

	    // draws the image chunk
	    Graphics2D gr = imgs[count++].createGraphics();
	    gr.drawRect(0, 0, chunkWidth, chunkHeight);


	    if (image.getHeight() - (chunkHeight * x) < heigth) {
		int rmnHeigth = image.getHeight() - (chunkHeight * x);

		gr.setBackground(Color.WHITE);

		gr.clearRect(0, 0, chunkWidth, chunkHeight);

		gr.drawImage(image,
			0, 0, chunkWidth, rmnHeigth,
			0, (chunkHeight * x), chunkWidth, (chunkHeight * x + rmnHeigth),
			null);

	    } else {

		gr.drawImage(image,
			0, 0, chunkWidth, chunkHeight,
			0, (chunkHeight * x), chunkWidth, (chunkHeight * x + chunkHeight),
			null);
	    }

	    gr.dispose();
	}

	return imgs;

    }

    public static float PixelsToPoints(float value, int dpi) {
	return value / dpi * 72;
    }

    public static void main(String[] args) {

	try {
	    File file = new File("D:\\tmp\\debug.png"); // 12639 h (11 * 1123 = 12353)    12639 - 12353 = 286
	    FileInputStream fis = new FileInputStream(file);
	    BufferedImage image = ImageIO.read(fis); //reading the image file
	    BufferedImage[] imgs = ImageUtils.splitImageByHeigth(image, 1123);

	    for (int i = 0; i < imgs.length; i++) {
		ImageIO.write(imgs[i], "png", new File("D:\\tmp", "img" + (i + 1) + ".png"));
	    }
	    fis.close();

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }


}