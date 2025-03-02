/*******************************************************************************
 * Copyright (c) 2014, Pharmed Solutions GmbH
 * All rights reserved.
 *******************************************************************************/

package ch.pharmed.phmprescriber;

import java.awt.image.BufferedImage;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;

public class Barcode {

	public BufferedImage getQRCode(String strQRCode) {

		BitMatrix bitMatrix;
		Writer writer = new QRCodeWriter();

		try {

			// Write QR Code
			bitMatrix = writer.encode(strQRCode, BarcodeFormat.QR_CODE, 800, 800);

			MatrixToImageWriter.toBufferedImage(bitMatrix);
//		            System.out.println("QR Code Generated.");
			return MatrixToImageWriter.toBufferedImage(bitMatrix);

		} catch (Exception e) {
			System.out.println("Exception Found." + e.getMessage());
		}
		return null;

	}

	public BufferedImage getCode128(String strCode128) {

		BitMatrix bitMatrix;

		try {
			// Write Barcode

			bitMatrix = new Code128Writer().encode(strCode128, BarcodeFormat.CODE_128, 195, 40, null);

			MatrixToImageWriter.toBufferedImage(bitMatrix);
//		            System.out.println("Code128 Barcode Generated.");

			return MatrixToImageWriter.toBufferedImage(bitMatrix);

		} catch (Exception e) {

			System.out.println("Exception Found." + e.getMessage());

		}
		return null;

	}
}
