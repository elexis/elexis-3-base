/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.elexis.common.ui.provider;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;

import at.medevit.ch.artikelstamm.ui.ArtikelstammLabelProvider;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;

/**
 * {@link LabelProvider} that extends the basic {@link ArtikelstammLabelProvider} to consider the
 * stock status of articles. Applicable to Elexis v2.1 only.
 */
public class LagerhaltungArtikelstammLabelProvider extends ArtikelstammLabelProvider implements
		IColorProvider {
	
	private Image blackBoxedImage = ResourceManager.getPluginImage("at.medevit.ch.artikelstamm.ui",
		"/rsc/icons/flag-black.png");
	
	@Override
	public Image getImage(Object element){
		ArtikelstammItem ai = (ArtikelstammItem) element;
		if (ai.isBlackBoxed())
			return blackBoxedImage;
		return super.getImage(element);
	}
	
	@Override
	public String getText(Object element){
		ArtikelstammItem ai = (ArtikelstammItem) element;
		int istBestand = ai.getIstbestand();
		if (istBestand == 0) {
			return ai.getLabel();
		}
		return ai.getLabel() + " (LB: " + istBestand + ")";
	}
	
	/**
	 * Lagerartikel are shown in blue, articles that should be ordered are shown in red
	 */
	@Override
	public Color getForeground(Object element){
		ArtikelstammItem ai = (ArtikelstammItem) element;
		if (ai.isLagerartikel()) {
			int trigger =
				CoreHub.globalCfg.get(Preferences.INVENTORY_ORDER_TRIGGER,
					Preferences.INVENTORY_ORDER_TRIGGER_DEFAULT);
			
			int ist = ai.getIstbestand();
			int min = ai.getMinbestand();
			
			boolean order = false;
			switch (trigger) {
			case Preferences.INVENTORY_ORDER_TRIGGER_BELOW:
				order = (ist < min);
				break;
			case Preferences.INVENTORY_ORDER_TRIGGER_EQUAL:
				order = (ist <= min);
				break;
			default:
				order = (ist < min);
			}
			
			if (order) {
				return UiDesk.getColor(UiDesk.COL_RED);
			} else {
				return UiDesk.getColor(UiDesk.COL_BLUE);
			}
		}
		return null;
	}
	
	@Override
	public Color getBackground(Object element){
		ArtikelstammItem ai = (ArtikelstammItem) element;
		if (ai.isBlackBoxed())
			return UiDesk.getColor(UiDesk.COL_GREY60);
		return super.getBackground(element);
	}
	
}
