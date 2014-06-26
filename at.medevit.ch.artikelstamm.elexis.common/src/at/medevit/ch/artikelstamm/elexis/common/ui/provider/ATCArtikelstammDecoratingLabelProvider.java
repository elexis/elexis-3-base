package at.medevit.ch.artikelstamm.elexis.common.ui.provider;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import at.medevit.atc_codes.ATCCode;
import at.medevit.ch.artikelstamm.elexis.common.ui.cv.ATCFilterInfoListElement;
import at.medevit.ch.artikelstamm.ui.ATCLabelProvider;
import ch.artikelstamm.elexis.common.ArtikelstammItem;

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
			return super.getText(element);
		} else if (element instanceof ATCCode) {
			return atcLabelProvider.getText(element);
		} else if (element instanceof ATCFilterInfoListElement) {
			ATCFilterInfoListElement afile = (ATCFilterInfoListElement) element;
			return afile.getDescription();
		}
		return null;
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
