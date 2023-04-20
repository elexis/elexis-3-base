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

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.ui.ArtikelstammLabelProvider;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IStockEntry;
import ch.elexis.core.services.IStockService.Availability;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.StockServiceHolder;
import ch.elexis.core.services.holder.StoreToStringServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.e4.util.CoreUiUtil;

/**
 * {@link LabelProvider} that extends the basic
 * {@link ArtikelstammLabelProvider} to consider the stock status of articles.
 * Applicable to Elexis v2.1 only.
 */
public class LagerhaltungArtikelstammLabelProvider extends ArtikelstammLabelProvider implements IColorProvider {

	@Inject
	private IEclipseContext eclipseContext;

	private Image blackBoxedImage = ResourceManager.getPluginImage("at.medevit.ch.artikelstamm.ui", //$NON-NLS-1$
			"/rsc/icons/flag-black.png"); //$NON-NLS-1$

	public LagerhaltungArtikelstammLabelProvider() {
		// trigger injection of application context
		CoreUiUtil.injectServicesWithContext(this);
	}

	@Override
	public Image getImage(Object element) {
		IArtikelstammItem ai = (IArtikelstammItem) element;
		if (ai.isBlackBoxed())
			return blackBoxedImage;
		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
		Long availability = null;
		IArtikelstammItem ai = (IArtikelstammItem) element;
		if (eclipseContext != null) {
			MPart mPart = eclipseContext.getActive(MPart.class);
			if (mPart != null && "ch.elexis.LeistungenView".equals(mPart.getElementId()) //$NON-NLS-1$
					&& ContextServiceHolder.get().getTyped(IEncounter.class).isPresent()) {
				availability = getAvailability(ai,
						Optional.of(ContextServiceHolder.get().getTyped(IEncounter.class).get().getMandator()));
			} else {
				availability = getAvailability(ai, ContextServiceHolder.get().getActiveMandator());
			}
		} else {
			availability = StockServiceHolder.get().getCumulatedStockForArticle(ai);
		}
		if (availability != null) {
			return ai.getLabel() + " (LB: " + availability + ")"; //$NON-NLS-2$
		}
		return ai.getLabel();
	}

	private Long getAvailability(IArtikelstammItem ai, Optional<IMandator> mandator) {
		List<IStockEntry> stockEntries = StockServiceHolder.get()
				.findAllStockEntriesForArticle(StoreToStringServiceHolder.getStoreToString(ai));
		if (!stockEntries.isEmpty()) {
			if (mandator.isPresent()) {
				return new Long(stockEntries.stream().filter(
						se -> (se.getStock().getOwner() == null || se.getStock().getOwner().equals(mandator.get())))
						.mapToInt(se -> se.getCurrentStock()).sum());
			} else {
				return new Long(stockEntries.stream().mapToInt(se -> se.getCurrentStock()).sum());
			}
		}
		return null;
	}

	/**
	 * Lagerartikel are shown in blue, articles that should be ordered are shown in
	 * red
	 */
	@Override
	public Color getForeground(Object element) {
		IArtikelstammItem ai = (IArtikelstammItem) element;

		Availability availability = StockServiceHolder.get().getCumulatedAvailabilityForArticle(ai);
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
	public Color getBackground(Object element) {
		IArtikelstammItem ai = (IArtikelstammItem) element;
		if (ai.isBlackBoxed())
			return UiDesk.getColor(UiDesk.COL_GREY60);
		return super.getBackground(element);
	}

}
