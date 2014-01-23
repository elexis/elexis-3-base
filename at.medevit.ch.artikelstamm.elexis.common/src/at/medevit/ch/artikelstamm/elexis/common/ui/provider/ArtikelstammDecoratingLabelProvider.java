package at.medevit.ch.artikelstamm.elexis.common.ui.provider;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;

public class ArtikelstammDecoratingLabelProvider extends DecoratingLabelProvider {
	
	public ArtikelstammDecoratingLabelProvider(ILabelProvider provider, ILabelDecorator decorator){
		super(provider, decorator);
	}
	
}
