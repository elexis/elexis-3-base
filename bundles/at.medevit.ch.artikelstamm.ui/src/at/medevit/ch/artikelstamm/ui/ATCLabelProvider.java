package at.medevit.ch.artikelstamm.ui;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.icons.Images;
import at.medevit.atc_codes.ATCCode;
import at.medevit.atc_codes.ATCCodeLanguageConstants;

public class ATCLabelProvider extends LabelProvider implements IColorProvider {

	private static String prefAtcLanguage = null;

	public ATCLabelProvider(String atcLang) {
		prefAtcLanguage = atcLang;
	}

	@Override
	public String getText(Object element) {
		switch (prefAtcLanguage) {
		case ATCCodeLanguageConstants.ATC_LANGUAGE_VAL_GERMAN:
			ATCCode a = ((ATCCode) element);
			String displayName = (a.name_german != null) ? a.name_german : a.name;
			return displayName + " (" + a.atcCode + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		default:
			return ((ATCCode) element).name;
		}
	}

	@Override
	public Image getImage(Object element) {
		return Images.IMG_CATEGORY_GROUP.getImage();
	}

	@Override
	public Color getForeground(Object element) {
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		return UiDesk.getColorFromRGB("FFFACD"); //$NON-NLS-1$
	}

	public static void setPrefAtcLanguage(String prefAtcLanguage) {
		ATCLabelProvider.prefAtcLanguage = prefAtcLanguage;
	}

}
