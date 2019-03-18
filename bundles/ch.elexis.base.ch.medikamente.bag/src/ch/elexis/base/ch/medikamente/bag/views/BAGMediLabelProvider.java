/*******************************************************************************
 * Copyright (c) 2007-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.base.ch.medikamente.bag.views;

import java.util.List;
import java.util.Optional;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.data.service.StockServiceHolder;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.services.IStockService.Availability;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.data.PersistentObject;
import ch.elexis.medikamente.bag.data.BAGMedi;
import ch.elexis.medikamente.bag.data.Substance;
import ch.rgw.tools.StringTool;

public class BAGMediLabelProvider extends DefaultLabelProvider implements ITableColorProvider {
	
	@Override
	public String getColumnText(final Object element, final int columnIndex){
		if (element instanceof BAGMedi) {
			BAGMedi bm = (BAGMedi) element;
			StringBuilder sb = new StringBuilder();
			sb.append(bm.getLabel()).append(" <").append(bm.getVKPreis().getAmountAsString())
				.append(">");
			
			List<Substance> conts = bm.getSubstances();
			if (conts.size() > 0) {
				sb.append("[");
				for (Substance s : conts) {
					sb.append(s.getLabel()).append("; ");
				}
				sb.append("]");
			}
			Optional<IArticle> article =
				CoreModelServiceHolder.get().load(bm.getId(), IArticle.class);
			article.ifPresent(art -> {
				Availability availability =
					StockServiceHolder.get().getCumulatedAvailabilityForArticle(art);
				if (availability != null) {
					sb.append(" (").append(availability).append(")");
				}
			});
			
			return sb.toString();
		}
		return super.getColumnText(element, columnIndex);
	}
	
	public Color getBackground(final Object element, final int columnIndex){
		// TODO Auto-generated method stub
		return null;
	}
	
	public Color getForeground(final Object element, final int columnIndex){
		if (element instanceof PersistentObject) {
			
			Optional<IArticle> article = CoreModelServiceHolder.get()
				.load(((PersistentObject) element).getId(), IArticle.class);
			if (article.isPresent()) {
				Availability availability =
					StockServiceHolder.get().getCumulatedAvailabilityForArticle(article.get());
				
				if (availability != null) {
					switch (availability) {
					case CRITICAL_STOCK:
					case OUT_OF_STOCK:
						return UiDesk.getColor(UiDesk.COL_RED);
					default:
						return UiDesk.getColor(UiDesk.COL_BLUE);
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public Image getColumnImage(final Object element, final int columnIndex){
		if (element instanceof BAGMedi) {
			BAGMedi bm = (BAGMedi) element;
			String g = StringTool.unNull(bm.get("Generikum"));
			/*
			 * if(g.equals("")){ return Desk.getImage(Desk.IMG_ACHTUNG); }
			 */
			if (g.startsWith("G")) {
				return UiDesk.getImage(BAGMedi.IMG_GENERIKUM);
			} else if (g.startsWith("O")) {
				return UiDesk.getImage(BAGMedi.IMG_HAS_GENERIKA);
			} else {
				return UiDesk.getImage(BAGMedi.IMG_ORIGINAL);
			}
		}
		
		return super.getColumnImage(element, columnIndex);
	}
	
}
