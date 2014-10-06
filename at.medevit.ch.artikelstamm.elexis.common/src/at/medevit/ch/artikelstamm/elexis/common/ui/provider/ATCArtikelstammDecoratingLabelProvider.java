package at.medevit.ch.artikelstamm.elexis.common.ui.provider;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import at.medevit.atc_codes.ATCCode;
import at.medevit.ch.artikelstamm.elexis.common.preference.PreferenceConstants;
import at.medevit.ch.artikelstamm.elexis.common.ui.cv.ATCFilterInfoListElement;
import at.medevit.ch.artikelstamm.elexis.common.ui.provider.atccache.ATCCodeCache;
import at.medevit.ch.artikelstamm.ui.ATCLabelProvider;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IVerrechenbar.VatInfo;

public class ATCArtikelstammDecoratingLabelProvider extends DecoratingLabelProvider {
	
	private ATCLabelProvider atcLabelProvider;
	private String atcLang;
	
	public ATCArtikelstammDecoratingLabelProvider(ILabelProvider provider,
		ILabelDecorator decorator, String atcLang){
		super(provider, decorator);
		atcLabelProvider = new ATCLabelProvider(atcLang);
	}
	
	@Override
	public String getText(Object element){
		if (element instanceof ArtikelstammItem) {
			String ret = super.getText(element);
			
			ArtikelstammItem ai = (ArtikelstammItem) element;
			String overriden =
				(String) ai.getExtInfoStoredObjectByKey(ArtikelstammItem.EXTINFO_VAL_VAT_OVERRIDEN);
			if (overriden != null) {
				ret = ret + " (MWSt: " + resolveVatInfoLabel(VatInfo.valueOf(overriden)) + ")";
			}
			if (CoreHub.globalCfg.get(PreferenceConstants.PREF_SHOW_PRICE_IN_OVERVIEW, true)) {
				Double publicPrice = ai.getPublicPrice();
				if (publicPrice > 0.0d) {
					ret = ret + " <" + ai.getPublicPrice() + "> ";
				}
			}
			
			return ret;
		} else if (element instanceof ATCCode) {
			String atcLabel = atcLabelProvider.getText(element);
			String atcLabelWAvailability =
				atcLabel + " [" + determineNumberOfAvailableArticlesForAtcCode((ATCCode) element)+" Artikel]";
			return atcLabelWAvailability;
		} else if (element instanceof ATCFilterInfoListElement) {
			ATCFilterInfoListElement afile = (ATCFilterInfoListElement) element;
			return afile.getDescription();
		}
		return null;
	}
	
	private String determineNumberOfAvailableArticlesForAtcCode(ATCCode element){
		return String.valueOf(ATCCodeCache.getAvailableArticlesByATCCode(element));
	}
	
	private String resolveVatInfoLabel(VatInfo vatinfo){
		switch (vatinfo) {
		case VAT_CH_ISMEDICAMENT:
			return "Reduziert";
		case VAT_NONE:
			return "Keine";
		default:
			return "Normal";
		}
	}
	
	@Override
	public Image getImage(Object element){
		if (element instanceof ArtikelstammItem) {
			return super.getImage(element);
		} else if (element instanceof ATCCode) {
			return atcLabelProvider.getImage(element);
		} else if (element instanceof ATCFilterInfoListElement) {
			return atcLabelProvider.getImage(element);
		}
		return null;
	}
	
	@Override
	public Color getForeground(Object element){
		if (element instanceof ArtikelstammItem) {
			return super.getForeground(element);
		} else if (element instanceof ATCCode) {
			return atcLabelProvider.getForeground(element);
		} else if (element instanceof ATCFilterInfoListElement) {
			return null;
		}
		return null;
	}
	
	@Override
	public Color getBackground(Object element){
		if (element instanceof ArtikelstammItem) {
			return super.getBackground(element);
		} else if (element instanceof ATCCode) {
			return atcLabelProvider.getBackground(element);
		} else if (element instanceof ATCFilterInfoListElement) {
			return atcLabelProvider.getBackground(element);
		}
		return null;
	}
	
}
