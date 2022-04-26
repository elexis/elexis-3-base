/*******************************************************************************
 * Copyright (c) 2015 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.elexis.common.ui.cv;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.icons.ImageSize;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.viewers.CommonViewerContentProvider;
import ch.elexis.core.ui.util.viewers.CommonViewerContentProvider.QueryFilter;

public class MephaPrefferedProviderSorterAction extends Action {

	private CommonViewerContentProvider commonViewerContentProvider;

	public static final String CFG_PREFER_MEPHA = "artikelstammPreferMepha";

	private MephaPrefferdQueryFilter queryFilter;

	public MephaPrefferedProviderSorterAction(CommonViewerContentProvider commonViewerContentProvider) {
		this.commonViewerContentProvider = commonViewerContentProvider;
		this.queryFilter = new MephaPrefferdQueryFilter();
	}

	@Override
	public String getText() {
		return "Mepha";
	}

	@Override
	public String getToolTipText() {
		return "Mepha Artikel bevorzugen (werden zuoberst angezeigt)";
	}

	@Override
	public int getStyle() {
		return Action.AS_CHECK_BOX;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return Images.lookupImageDescriptor("mepha.png", ImageSize._16x16_DefaultIconSize);
	}

	@Override
	public void run() {
		ConfigServiceHolder.get().set(CFG_PREFER_MEPHA, isChecked());
		if (isChecked()) {
			commonViewerContentProvider.addQueryFilter(queryFilter);
		} else {
			commonViewerContentProvider.removeQueryFilter(queryFilter);
		}
	}

	private class MephaPrefferdQueryFilter implements QueryFilter {

		@Override
		public void apply(IQuery<?> query) {
			// #3627 need to work-around
			Map<String, Object> caseContext = new HashMap<>();
			caseContext.put("when|comp_gln|equals|7601001001121", Integer.valueOf(1));
			caseContext.put("otherwise", Integer.valueOf(2));
			query.orderBy(caseContext, ORDER.ASC);
		}
	}
}
