package ch.elexis.pdfBills;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.multipdf.Overlay.Position;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.LoggerFactory;

public class PdfUtil {

	public static void addCopyWatermark(File pdfFile) {
		if (pdfFile != null) {
			try {
				PDDocument pdfDoc = PDDocument.load(pdfFile);
				PDDocument copyDoc = PDDocument.load(QrRnOutputter.class.getResourceAsStream("/rsc/kopie.pdf"));
				try (Overlay overlay = new Overlay()) {
					overlay.setInputPDF(pdfDoc);
					overlay.setAllPagesOverlayPDF(copyDoc);
					overlay.setOverlayPosition(Position.BACKGROUND);
					overlay.overlay(Collections.emptyMap());
					pdfDoc.save(pdfFile);
				}
				pdfDoc.close();
				copyDoc.close();
			} catch (IOException e) {
				LoggerFactory.getLogger(PdfUtil.class)
						.error("Error adding watermark to [" + pdfFile.getAbsolutePath() + "]", e);
			}
		}
	}
}
