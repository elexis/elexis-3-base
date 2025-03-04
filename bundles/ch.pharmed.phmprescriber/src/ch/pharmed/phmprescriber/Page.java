/*******************************************************************************
 * Copyright (c) 2014, Pharmed Solutions GmbH
 * All rights reserved.
 *******************************************************************************/

package ch.pharmed.phmprescriber;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.data.Artikel;
import ch.elexis.data.Patient;
import ch.elexis.data.Rezept;

class Page implements Printable {

	// Fonts
	private static Font fnt = new Font("Helvetica", Font.PLAIN, 8); //$NON-NLS-1$
	private static Font fntBold = new Font("Helvetica", Font.BOLD, 8); //$NON-NLS-1$
	private static Font fntItalic = new Font("Helvetica", Font.ITALIC, 8); //$NON-NLS-1$
	private static Font fntTitle = new Font("Helvetica", Font.BOLD, 11); //$NON-NLS-1$

	// Message-Constants
	private String PHONE;
	private String FAX;

	private String EAN;
	private String ZSR;

	private String TITLE;
	private String BORN;
	private String REPETITION;

	private static final String PROMO = "https://www.pharmedsolutions.ch"; //$NON-NLS-1$

	// Layout-Constants
	private final double LMARGINRATIO = 0.3;
	private final double SPACERATIO = 1.5;

	int Code128Width = 185;
	int Code128Height = 36;
	int QRCodeBorder = 118;

	// Objects to print
	private Physician ph;
	private Rezept rp;
	private String presID;
	private String QRCode;
	private Patient pat;

	// Barcodes
	private BufferedImage imgQRCode;
	private BufferedImage imgCode128;

	// Indices for correctly rendering the page
	private int firstProd = 0;
	private int lastProd = 0;

	private ResourceBundle messages;

	public Page(Physician ph, Rezept rp, String presID, String QRCode, Integer firstProductIndex,
			Integer lastProductIndex) {

		this.ph = ph;
		this.rp = rp;
		this.pat = rp.getPatient();
		this.presID = presID;
		this.QRCode = QRCode;
		this.firstProd = firstProductIndex;
		this.lastProd = lastProductIndex;

		this.messages = ResourceBundle.getBundle("ch.pharmed.phmprescriber.MessagesBundle", new Locale("de", "CH")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		this.PHONE = messages.getString("Page_4");
		this.FAX = messages.getString("Page_5");

		this.EAN = messages.getString("Page_6");
		this.ZSR = messages.getString("Page_7");

		this.TITLE = messages.getString("Page_8");
		this.BORN = messages.getString("Page_9");
		this.REPETITION = messages.getString("Page_10");

	}

	public int print(Graphics g, PageFormat pageFormat, int page) throws PrinterException {

		// Define the origin of the printArea
		double printAreaX = pageFormat.getImageableX();
		double printAreaY = pageFormat.getImageableY();

		// Measures the size of strings
		FontMetrics metrics = g.getFontMetrics(fnt);

		// Parameters for the layout
		// Dynamic variable to measure the y-position of each line

		int intMeasureY = 0;
		// Others
		int intMarginLeft = Integer.valueOf((int) Math.round(printAreaX * LMARGINRATIO));
		int intSpace = Integer.valueOf((int) Math.round(metrics.getHeight() * SPACERATIO));
		int intDefaultHeight = metrics.getHeight();
		int intPageWidth = Integer.valueOf((int) Math.round(pageFormat.getImageableWidth()));

		int pageHeight = Integer.valueOf((int) Math.round(pageFormat.getImageableHeight()));

		metrics = g.getFontMetrics(fntTitle);
		int intSpaceBig = metrics.getHeight();

		// Graphics object to draw lines etc.
		Graphics2D g2d;
		Line2D.Double line = new Line2D.Double();
		// Set colour to black
		g.setColor(Color.black);

		// Validate the number of pages

		// Create a graphic2D object a set the default parameters
		g2d = (Graphics2D) g;
		g2d.setColor(Color.black);

		// Translate the origin to be (0,0)
		// Note: Imageable includes already margins for Headers and Footers
		g2d.translate(printAreaX, printAreaY);

		// -- (1) Print the line on the left side
		line.setLine(0, 0, 0, pageFormat.getHeight() + 500);
		g2d.draw(line);

		// -- (2) Print the physicians attributes

		g.setFont(fntBold);
		// Measure String height to start drawing at the right place
		metrics = g.getFontMetrics(fntBold);
		intMeasureY += metrics.getHeight();

		g.drawString(ph.getTitle() + StringUtils.SPACE + ph.getFirstname() + StringUtils.SPACE + ph.getLastname(),
				intMarginLeft, intMeasureY); // $NON-NLS-1$

		// Set font to default
		g.setFont(fnt);

		// Measure the x-position (Page-Width - length of string)
		metrics = g.getFontMetrics(fnt);

		// Draw the date
		g.drawString(rp.getDate(), intPageWidth - metrics.stringWidth(rp.getDate()), intMeasureY);

		intMeasureY += metrics.getHeight();

		// Draw strings of Address, Phone and insurance information
		g.drawString(ph.getSpecialty1(), intMarginLeft, intMeasureY);

		if (ph.getSpecialty2().length() > 0) {
			intMeasureY += intDefaultHeight;
			g.drawString(ph.getSpecialty2(), intMarginLeft, intMeasureY);
		}

		intMeasureY += intSpace;

		g.drawString(ph.getStreet() + StringUtils.SPACE + ph.getPostbox(), intMarginLeft, intMeasureY);

		intMeasureY += intDefaultHeight;

		g.drawString(ph.getZip() + StringUtils.SPACE + ph.getCity(), intMarginLeft, intMeasureY);

		intMeasureY += intSpace;

		// Measure the label strings to align the phone and fax numbers
		int phoneWidth = metrics.stringWidth(PHONE);

		if (metrics.stringWidth(PHONE) < metrics.stringWidth(FAX) && ph.getFax().length() > 0)
			phoneWidth = metrics.stringWidth(FAX);

		g.drawString(PHONE, intMarginLeft, intMeasureY);
		g.drawString(ph.getPhone(), intMarginLeft + phoneWidth, intMeasureY);

		if (ph.getFax().length() > 0) {
			intMeasureY += intDefaultHeight;

			g.drawString(FAX, intMarginLeft, intMeasureY);
			g.drawString(ph.getFax(), intMarginLeft + phoneWidth, intMeasureY);

		}

		intMeasureY += intSpace;

		// Measure the label strings to align the ZSR and EAN identifiers
		int EANWidth = metrics.stringWidth(ZSR);

		if (metrics.stringWidth(ZSR) < metrics.stringWidth(EAN) && ph.getGlnid().length() > 0)
			EANWidth = metrics.stringWidth(EAN);

		g.drawString(ZSR, intMarginLeft, intMeasureY);
		g.drawString(ph.getZsrid(), intMarginLeft + EANWidth, intMeasureY);

		if (ph.getGlnid().length() > 0) {

			intMeasureY += intDefaultHeight;

			g.drawString(EAN, intMarginLeft, intMeasureY);
			g.drawString(ph.getGlnid(), intMarginLeft + EANWidth, intMeasureY);

		}

		intMeasureY += intSpaceBig;

		// -- (3) Print the line
		line.setLine(intMarginLeft, intMeasureY, pageFormat.getWidth(), intMeasureY);
		g2d.draw(line);

		intMeasureY += intSpaceBig + intSpace;

		// -- (4) Title
		g.setFont(fntTitle);
		g.drawString(TITLE, intMarginLeft, intMeasureY);

		intMeasureY += intSpaceBig + intDefaultHeight;

		// -- (5) Patient
		g.setFont(fntBold);

		g.drawString(pat.getName() + StringUtils.SPACE + pat.getVorname(), intMarginLeft, intMeasureY);

		metrics = g.getFontMetrics(fntBold);
		int xPat = intMarginLeft + metrics.stringWidth(pat.getName() + StringUtils.SPACE + pat.getVorname());

		g.setFont(fnt);
		g.drawString(", " + BORN + pat.getGeburtsdatum(), xPat, intMeasureY); //$NON-NLS-1$

		intMeasureY += intSpaceBig + intSpace;

		// -- (6) Products
		LineBreakMeasurer lineBreakMeasurer;
		int intstart, intend;

		Hashtable hash = new Hashtable();

		// Print all the items
		for (int i = this.firstProd; i <= lastProd; i = i + 1) {

			ch.elexis.data.Prescription actualLine = rp.getLines().get(i);
			Artikel article = actualLine.getArtikel();

			AttributedString attributedString = new AttributedString("1x " + article.getLabel(), hash); //$NON-NLS-1$

			attributedString.addAttribute(TextAttribute.FONT, fntBold);

			g2d.setFont(fntBold);
			FontRenderContext frc = g2d.getFontRenderContext();

			AttributedCharacterIterator attributedCharacterIterator = attributedString.getIterator();

			intstart = attributedCharacterIterator.getBeginIndex();
			intend = attributedCharacterIterator.getEndIndex();

			lineBreakMeasurer = new LineBreakMeasurer(attributedCharacterIterator, frc);

			float width = (float) intPageWidth - intMarginLeft;

			int X = intMarginLeft;

			lineBreakMeasurer.setPosition(intstart);

			// Create TextLayout accordingly and draw it
			while (lineBreakMeasurer.getPosition() < intend) {

				TextLayout textLayout = lineBreakMeasurer.nextLayout(width);

				intMeasureY += textLayout.getAscent();
				X = intMarginLeft;

				textLayout.draw(g2d, X, intMeasureY);
				intMeasureY += textLayout.getDescent() + textLayout.getLeading();

			}

			// Draw the label
			String label = actualLine.getBemerkung();

			if (actualLine.getDosis().length() > 0) {

				label = actualLine.getDosis() + ", " + label; //$NON-NLS-1$

			}

			// If there is no label specified, go to the next iterations
			if (label.length() == 0) {
				intMeasureY += intSpaceBig * 2;
				continue;
			}

			attributedString = new AttributedString(label, hash);

			attributedString.addAttribute(TextAttribute.FONT, fnt);

			g2d.setFont(fnt);
			frc = g2d.getFontRenderContext();

			attributedCharacterIterator = attributedString.getIterator();

			intstart = attributedCharacterIterator.getBeginIndex();
			intend = attributedCharacterIterator.getEndIndex();

			lineBreakMeasurer = new LineBreakMeasurer(attributedCharacterIterator, frc);

			lineBreakMeasurer.setPosition(intstart);

			// Create TextLayout accordingly and draw it
			while (lineBreakMeasurer.getPosition() < intend) {

				// Extra code to determine line breaks in the string --> go on new line, if
				// there is one
				int next = lineBreakMeasurer.nextOffset(width);
				int limit = next;
				if (limit <= label.length()) {
					for (int k = lineBreakMeasurer.getPosition(); k < next; ++k) {
						char c = label.charAt(k);
						if (c == '\n') {
							limit = k + 1;
							break;
						}
					}
				}

				TextLayout textLayout = lineBreakMeasurer.nextLayout(width, limit, false);

				intMeasureY += textLayout.getAscent();
				X = intMarginLeft;

				textLayout.draw(g2d, X, intMeasureY);
				intMeasureY += textLayout.getDescent() + textLayout.getLeading();

			}

			intMeasureY += intSpaceBig * 2;

		}

		// (7) Draw now the Footer:

		// Create the barcodes
		Barcode bc = new Barcode();

		this.imgCode128 = bc.getCode128(presID);
		this.imgQRCode = bc.getQRCode(QRCode);

		// (a) Code 128 with decoded String
		g.setFont(fntBold);
		metrics = g.getFontMetrics(fntBold);
		int xPrescId = intPageWidth - Code128Width
				+ Integer.valueOf((int) Math.round((Code128Width - metrics.stringWidth(this.presID)) / 2));

		g.drawString(this.presID, xPrescId, pageHeight);

		g.drawImage(imgCode128, intPageWidth - Code128Width, pageHeight - Code128Height - intDefaultHeight,
				Code128Width, Code128Height, null);

		// (b) QR-Code
		g.drawImage(imgQRCode, intMarginLeft, pageHeight - QRCodeBorder, QRCodeBorder, QRCodeBorder, null);

		// (c) Promotion-String
		g.setColor(Color.gray);
		metrics = g.getFontMetrics(fntItalic);
		g.setFont(fntItalic);
		g.drawString(PROMO,
				Integer.valueOf((int) Math.round((intMarginLeft + (QRCodeBorder - metrics.stringWidth(PROMO)) / 2))),
				pageHeight);

		// set to default
		g.setColor(Color.black);
		g.setFont(fnt);

		// (8) Return, that this page exists and thus will be rendered and printed
		return (PAGE_EXISTS);

	}
}
