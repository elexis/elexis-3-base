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

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.ui.ArtikelstammLabelProvider;
import ch.elexis.core.services.IStockService.Availability;
import ch.elexis.core.services.holder.StockServiceHolder;
import ch.elexis.core.ui.UiDesk;

/**
 * {@link LabelProvider} that extends the basic {@link ArtikelstammLabelProvider} to consider the
 * stock status of articles. Applicable to Elexis v2.1 only.
 */
public class LagerhaltungArtikelstammLabelProvider extends ArtikelstammLabelProvider
		implements IColorProvider {
	
	private Image blackBoxedImage = ResourceManager.getPluginImage("at.medevit.ch.artikelstamm.ui",
		"/rsc/icons/flag-black.png");
	
	@Override
	public Image getImage(Object element){
		IArtikelstammItem ai = (IArtikelstammItem) element;
		if (ai.isBlackBoxed())
			return blackBoxedImage;
		return super.getImage(element);
	}
	
	@Override
	public String getText(Object element){
		IArtikelstammItem ai = (IArtikelstammItem) element;
		Long availability = StockServiceHolder.get().getCumulatedStockForArticle(ai);
		if (availability != null) {
			return ai.getLabel() + " (LB: " + availability + ")";
		}
		return ai.getLabel();
		
	}
	
	/**
	 * Lagerartikel are shown in blue, articles that should be ordered are shown in red
	 */
	@Override
	public Color getForeground(Object element){
		IArtikelstammItem ai = (IArtikelstammItem) element;
		
		Availability availability =
			StockServiceHolder.get().getCumulatedAvailabilityForArticle(ai);
		if (availability != null) {
			switch (availability) {
			case CRITICAL_STOCK:
			case OUT_OF_STOCK:
				return UiDesk.getColor(UiDesk.COL_RED);
			default:
				return UiDesk.getColor(UiDesk.COL_BLUE);
			}
		}
		return null;
	}
	
	@Override
	public Color getBackground(Object element){
		IArtikelstammItem ai = (IArtikelstammItem) element;
		if (ai.isBlackBoxed())
			return UiDesk.getColor(UiDesk.COL_GREY60);
		return super.getBackground(element);
	}
	
}
