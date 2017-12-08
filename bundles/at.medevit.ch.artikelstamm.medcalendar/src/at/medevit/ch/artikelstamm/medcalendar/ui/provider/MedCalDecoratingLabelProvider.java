package at.medevit.ch.artikelstamm.medcalendar.ui.provider;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import at.medevit.ch.artikelstamm.elexis.common.preference.PreferenceConstants;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IVerrechenbar.VatInfo;

public class MedCalDecoratingLabelProvider extends DecoratingLabelProvider {
	
	public MedCalDecoratingLabelProvider(ILabelProvider provider, ILabelDecorator decorator){
		super(provider, decorator);
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
		} else if (element instanceof MedCalFilterInfoElement) {
			MedCalFilterInfoElement medCalFilterInfo = (MedCalFilterInfoElement) element;
			return medCalFilterInfo.getDescription();
		}
		return null;
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
		} else if (element instanceof MedCalFilterInfoElement) {
			return MedCalFilterInfoElement.FILTER_ICON;
		}
		return null;
	}
	
	@Override
	public Color getBackground(Object element){
		if (element instanceof ArtikelstammItem) {
			return super.getBackground(element);
		} else if (element instanceof MedCalFilterInfoElement) {
			return MedCalFilterInfoElement.BG_COLOR;
		}
		return null;
	}
	
	@Override
	public Color getForeground(Object element){
		if (element instanceof ArtikelstammItem) {
			return super.getForeground(element);
		} else if (element instanceof MedCalFilterInfoElement) {
			return null;
		}
		return null;
	}
	
}
