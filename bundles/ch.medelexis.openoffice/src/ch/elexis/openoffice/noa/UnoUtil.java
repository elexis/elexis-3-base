package ch.elexis.openoffice.noa;

import org.eclipse.swt.SWT;

import com.sun.star.awt.FontWeight;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameAccess;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.style.ParagraphAdjust;
import com.sun.star.style.XStyle;
import com.sun.star.style.XStyleFamiliesSupplier;
import com.sun.star.text.XText;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.uno.UnoRuntime;

public class UnoUtil {

	/**
	 * Sets the printer tray of the document
	 *
	 * @param doc
	 * @param tray
	 * @throws NoSuchElementException
	 * @throws WrappedTargetException
	 * @throws UnknownPropertyException
	 * @throws PropertyVetoException
	 * @throws IllegalArgumentException
	 */
	public static void setPrinterTray(XTextDocument doc, String tray) throws NoSuchElementException,
			WrappedTargetException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException {
		XText xText = doc.getText();
		XTextCursor cr = xText.createTextCursor();

		XPropertySet xTextCursorProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, cr);

		String pageStyleName = xTextCursorProps.getPropertyValue("PageStyleName").toString();

		// Get the StyleFamiliesSupplier interface of the document
		XStyleFamiliesSupplier xSupplier = (XStyleFamiliesSupplier) UnoRuntime
				.queryInterface(XStyleFamiliesSupplier.class, doc);
		// Use the StyleFamiliesSupplier interface to get the XNameAccess
		// interface of the
		// actual style families
		XNameAccess xFamilies = (XNameAccess) UnoRuntime.queryInterface(XNameAccess.class,
				xSupplier.getStyleFamilies());
		// Access the 'PageStyles' Family
		XNameContainer xFamily = (XNameContainer) UnoRuntime.queryInterface(XNameContainer.class,
				xFamilies.getByName("PageStyles"));

		XStyle xStyle = (XStyle) UnoRuntime.queryInterface(XStyle.class, xFamily.getByName(pageStyleName));
		// Get the property set of the cell's TextCursor
		XPropertySet xStyleProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xStyle);

		try {
			xStyleProps.setPropertyValue("PrinterPaperTray", (String) tray);
		} catch (Exception ex) {
			String possible = (String) xStyleProps.getPropertyValue("PrinterPaperTray");
			throw new IllegalArgumentException("Could not set Tray to " + tray + " try " + possible);
		}
	}

	/**
	 * Sets format of text cursor
	 *
	 * @param xtc
	 * @param fontName
	 * @param fontSize
	 * @param fontStyle
	 * @param adjust
	 * @throws UnknownPropertyException
	 * @throws PropertyVetoException
	 * @throws IllegalArgumentException
	 * @throws WrappedTargetException
	 */
	public static void setFormat(final XTextCursor xtc, final String fontName, final float fontSize,
			final int fontStyle, final int adjust)
			throws UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException {
		com.sun.star.beans.XPropertySet charProps = (com.sun.star.beans.XPropertySet) UnoRuntime
				.queryInterface(com.sun.star.beans.XPropertySet.class, xtc);
		if (fontName != null) {
			charProps.setPropertyValue("CharFontName", fontName);
		}
		if (fontSize > 0) {
			charProps.setPropertyValue("CharHeight", new Float(fontSize));
		}
		if (fontStyle > -1) {
			switch (fontStyle) {
			case SWT.MIN:
				charProps.setPropertyValue("CharWeight", 15f); // FontWeight.ULTRALIGHT
				break;
			case SWT.NORMAL:
				charProps.setPropertyValue("CharWeight", FontWeight.LIGHT);
				break;
			case SWT.BOLD:
				charProps.setPropertyValue("CharWeight", FontWeight.BOLD);
				break;
			}
		}
		if (adjust > 0) {
			ParagraphAdjust paradj;
			switch (adjust) {
			case SWT.LEFT:
				paradj = ParagraphAdjust.LEFT;
				break;
			case SWT.RIGHT:
				paradj = ParagraphAdjust.RIGHT;
				break;
			default:
				paradj = ParagraphAdjust.CENTER;
			}

			charProps.setPropertyValue("ParaAdjust", paradj);
		}
	}

	/**
	 * Sets format of text cursor
	 *
	 * @param xtc
	 * @param fontName
	 * @param fontSize
	 * @param fontStyle
	 * @throws UnknownPropertyException
	 * @throws PropertyVetoException
	 * @throws IllegalArgumentException
	 * @throws WrappedTargetException
	 */
	public static void setFormat(final XTextCursor xtc, final String fontName, final float fontSize,
			final int fontStyle)
			throws UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException {
		setFormat(xtc, fontName, fontSize, fontStyle, -1);
	}
}
