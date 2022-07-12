package ch.elexis.ebanking.qr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Hashtable;
import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import ch.elexis.ebanking.qr.model.QRBillData;

/**
 *
 * Die sich bei einer Fehlerkorrekturstufe M und bei binärer Codierung daraus
 * ergebende Version des QR-Codes ist die Version 25 mit 117 x 117 Modulen
 *
 * Die Abmessung des Swiss QR Code beim Drucken muss immer 46 x 46 mm (ohne
 * umgebende Ruhezone) betragen
 *
 * @author thomas
 *
 */
public class QRBillImage {

	private QRBillData data;

	public QRBillImage(QRBillData data) {
		this.data = data;
	}

	public Optional<Image> getImage() {
		Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
		hintMap.put(EncodeHintType.QR_VERSION, 18);
		hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8"); //$NON-NLS-1$

		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		try {
			BitMatrix bitMatrix = qrCodeWriter.encode(data.toString(), BarcodeFormat.QR_CODE, 200, 200, hintMap);
			int width = bitMatrix.getWidth();
			int height = bitMatrix.getHeight();

			ImageData data = new ImageData(width, height, 24, new PaletteData(0xFF, 0xFF00, 0xFF0000));
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					data.setPixel(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
				}
			}
			addCross(data);
			return Optional.of(new Image(Display.getDefault(), data));
		} catch (WriterException e) {
			LoggerFactory.getLogger(getClass()).error("Error creating QR", e); //$NON-NLS-1$
			return Optional.empty();
		}
	}

	private int[] emptyRow = { 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF,
			0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF,
			0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF,
			0xFFFFFF, 0xFFFFFF, 0xFFFFFF };

	private int[] fullRow = { 0xFFFFFF, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
			0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
			0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
			0x000000, 0x000000, 0xFFFFFF };
	private int[] vertRow = { 0xFFFFFF, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
			0x000000, 0x000000, 0x000000, 0x000000, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF,
			0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
			0x000000, 0x000000, 0xFFFFFF };

	private int[] horiRow = { 0xFFFFFF, 0x000000, 0x000000, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF,
			0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF,
			0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF,
			0x000000, 0x000000, 0xFFFFFF };

	private void addCross(ImageData data) {
		int center = data.width / 2;
		int start = center - 16;
		int lineCount = 0;
		while (lineCount < 32) {
			if (lineCount == 0 || lineCount == 31) {
				data.setPixels(start, start + lineCount, 32, emptyRow, 0);
			} else {
				if (lineCount < 3 || lineCount > (31 - 4)) {
					data.setPixels(start, start + lineCount, 32, fullRow, 0);
				} else {
					if (lineCount > 12 && lineCount < 18) {
						data.setPixels(start, start + lineCount, 32, horiRow, 0);
					} else {
						data.setPixels(start, start + lineCount, 32, vertRow, 0);
					}
				}
			}
			lineCount++;
		}
	}

	/**
	 * Get the image as base64 encoded jpg with header "data:image/jpg;base64," for
	 * use with pdf output.
	 *
	 * @return
	 */
	public Optional<String> getEncodedImage() {
		Optional<Image> image = getImage();
		if (image.isPresent()) {
			Image qr = image.get();
			try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
				ImageLoader imageLoader = new ImageLoader();
				imageLoader.data = new ImageData[] { qr.getImageData() };
				imageLoader.compression = 100;
				imageLoader.save(output, SWT.IMAGE_JPEG);

				return Optional.of("data:image/jpg;base64," + Base64.getEncoder().encodeToString(output.toByteArray())); //$NON-NLS-1$

			} catch (IOException e) {
				LoggerFactory.getLogger(getClass()).error("Error encoding QR", e); //$NON-NLS-1$
			} finally {
				qr.dispose();
			}
		}
		return Optional.empty();
	}
}
